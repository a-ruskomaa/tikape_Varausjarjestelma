/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 *
 * @author aleks
 */
@Component
public class TilastoTulostaja {

    @Autowired
    JdbcTemplate jdbcTemplate;

    public void suosituimmatLisavarusteet() {
        // haetaan tietokannasta 10 suosituinta lisävarustetta, tulostetaan listana
        jdbcTemplate.query("SELECT COUNT(varausnumero) AS varauksia, nimi FROM Lisavaruste "
                + "GROUP BY nimi "
                + "ORDER By varauksia DESC "
                + "LIMIT 10;", (rs, rowNum) -> {
                    String[] riviPalat = new String[2];
                    riviPalat[0] = rs.getString("nimi");
                    if (rs.getInt("varauksia") == 1) {
                        riviPalat[1] = rs.getInt("varauksia") + " varaus";
                    } else {
                        riviPalat[1] = rs.getInt("varauksia") + " varausta";
                    }
                    System.out.println(riviPalat[0] + ", " + riviPalat[1]);
                    return null;
                }
        );
    }

    public void parhaatAsiakkaat() {
        // haetaan tietokannasta 10 eniten rahaa käyttänyttä asiakasta
        jdbcTemplate.query("SELECT nimi, email, puhelin, SUM(Varaus.yhteishinta) AS yhteishinta FROM Asiakas\n"
                + "JOIN Varaus ON Varaus.asiakasnumero = Asiakas.asiakasnumero\n"
                + "GROUP BY nimi\n"
                + "ORDER BY yhteishinta DESC\n"
                + "LIMIT 10;", (rs, rowNum) -> {
                    System.out.println(rs.getString("nimi") + ", " + rs.getString("email") + ", " + rs.getString("puhelin") + ", " + rs.getInt("yhteishinta") + " euroa");
                    return null;
                });
    }

    public void laskeVarausprosentti(LocalDateTime tarkasteluAlku, LocalDateTime tarkasteluLoppu, String tulostettava) {
        // luodaan hajautustaulu pitämään kirjaa kaikista tietokannasta löytyvistä huoneista, avaimena huonenumero
        HashMap<Integer, Huone> huoneet = new HashMap<>();
        // luodaan hajautustaulu jonne lasketaan huonekohtaisesti varauspäivien yhteenlaskettu pituus tarkastelujaksolla
        HashMap<Integer, Long> huoneittain = new HashMap<>();
        // luodaan hajautustaulu pitämään kirjaa tietokannasta löytyvien samanyyppisten huoneiden lukumäärästä
        HashMap<String, Integer> tyypit = new HashMap<>();
        // luodaan hajautustaulu pitämään kirjaa tyyppikohtaisesti varauspäivien lukumäärästä
        HashMap<String, Long> tyypeittain = new HashMap<>();

        jdbcTemplate.query("SELECT Huone.huonenumero, Huone.tyyppi, Huone.paivahinta, Varaus.alkupvm, Varaus.loppupvm FROM Huone\n"
                + "LEFT JOIN VarausHuone ON VarausHuone.huonenumero = Huone.huonenumero\n"
                + "LEFT JOIN Varaus ON Varaus.varausnumero = VarausHuone.varausnumero", (rs, rowNum) -> {
                    int huonenumero = rs.getInt("huonenumero");
                    String huonetyyppi = rs.getString("tyyppi");
                    String varausAlkupvm = rs.getString("alkupvm");
                    String varausLoppupvm = rs.getString("loppupvm");
                    //käydään kyselyn palauttamat arvot läpi rivi kerrallaan
                    //lisätään tauluun uusi huone-olio jos huonenumerolla ei ole vielä oliota
                    if (!huoneet.containsKey(huonenumero)) {
                        huoneet.put(huonenumero, new Huone(huonenumero, huonetyyppi, rs.getInt("paivahinta")));
                        //lisätään jokaisen uuden huoneen kohdalla sen tyyppisten huoneieden lukumäärää yhdellä
                        if (!tyypit.containsKey(huonetyyppi)) {
                            tyypit.put(huonetyyppi, 1);
                        } else {
                            tyypit.put(huonetyyppi, tyypit.get(huonetyyppi) + 1);
                        }
                    }
                    // lisätään tauluun huonekohtaisten varauspäivien lukumäärä tarkastelujaksolla
                    // metodi laskePaivat palauttaa päivien lukumäärän tai 0 mikäli tietokanta palauttaa päivämääriksi null
                    if (!huoneittain.containsKey(huonenumero)) {
                        huoneittain.put(huonenumero, laskePaivat(varausAlkupvm, varausLoppupvm, tarkasteluAlku, tarkasteluLoppu));
                    } else {
                        huoneittain.put(huonenumero, huoneittain.get(huonenumero) + laskePaivat(varausAlkupvm, varausLoppupvm, tarkasteluAlku, tarkasteluLoppu));
                    }
                    // lisätään tauluun tyyppikohtaisten varauspäivien lukumäärä
                    if (!tyypeittain.containsKey(huonetyyppi)) {
                        tyypeittain.put(huonetyyppi, laskePaivat(varausAlkupvm, varausLoppupvm, tarkasteluAlku, tarkasteluLoppu));
                    } else {
                        tyypeittain.put(huonetyyppi, tyypeittain.get(huonetyyppi) + laskePaivat(varausAlkupvm, varausLoppupvm, tarkasteluAlku, tarkasteluLoppu));
                    }
                    return true;
                });

        System.out.println(huoneet);
        
        // käydään läpi molemmat taulut hyödyntäen niiden identtisiä avaimia, tulostetaan varausprosentti
        if (tulostettava.equals("huoneittain")) {
            long paivia = laskePaivat(tarkasteluAlku, tarkasteluLoppu);
            for (int huone : huoneittain.keySet()) {
                System.out.println(huoneet.get(huone) + ", " + 100 * huoneittain.get(huone) / (double) paivia + "%");
            }
            // tyyppikohtaiseen taulun varausprosenttia laskettaessa saadaan mahdollisten
            // varauspäivien lukumäärä kertomalla tarkastelujakson kesto samantyyppisten huoneiden lukumäärällä
        } else if (tulostettava.equals("tyypeittain")) {
            long paivia = laskePaivat(tarkasteluAlku, tarkasteluLoppu);
            for (String tyyppi : tyypeittain.keySet()) {
                System.out.println(tyyppi + ", " + 100 * tyypeittain.get(tyyppi) / ((double) paivia * tyypit.get(tyyppi)) + "%");
            }
        }

    }

    private long laskePaivat(String varausAlkupvm, String varausLoppupvm, LocalDateTime tarkasteluAlku, LocalDateTime tarkasteluLoppu) {
        // palautetaan kestoksi 0, mikäli tietokantakyselyn palauttamat merkkijonot ovat null
        if (varausAlkupvm == null || varausLoppupvm == null) {
            return 0;
        }
        // tietokanta palauttaa päivämäärät merkkijonomuodossa (yyyy-mm-dd), muunnetaan datatyypiksi localdatetime
        // lisätään tunnit varauksen alku- ja loppuajankohtien mukaan
        LocalDateTime varausAlku = LocalDateTime.parse(varausAlkupvm + " " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        LocalDateTime varausLoppu = LocalDateTime.parse(varausLoppupvm + " " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        // jos varaus on alkanut tarkastelujakson ulkopuolella, huomioidaan vain tarkastelujaksolle osuvat päivät
        if (varausAlku.isBefore(tarkasteluAlku)) {
            varausAlku = tarkasteluAlku;
        }
        //toimitaan samoin varauksen loppupään kanssa
        if (varausLoppu.isAfter(tarkasteluLoppu)) {
            varausLoppu = tarkasteluLoppu;
        }
        // lasketaan päivien erotus
        long paivia = varausAlku.until(varausLoppu, ChronoUnit.DAYS);
        // jos koko varaus osuu tarkastelujakson ulkopuolelle, on päivien lukumäärä negatiivinen ja jätetään huomioimatta
        if (paivia < 0) {
            return 0;
        } else {
            return paivia;
        }
    }

    private long laskePaivat(LocalDateTime tarkasteluAlku, LocalDateTime tarkasteluLoppu) {
        // lasketaan tarkastelujakson pituus päivinä
        return tarkasteluAlku.until(tarkasteluLoppu, ChronoUnit.DAYS);
    }
}
