/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;

/**
 *
 * @author aleks
 */
@Component
public class VarausDAO implements DAO<Varaus, Integer> {

    @Autowired
    AsiakasDAO AsiakasDAO;

    @Autowired
    HuoneDAO HuoneDAO;

    @Autowired
    JdbcTemplate jdbcTemplate;

    @Override
    public void create(Varaus varaus) throws SQLException {
        //Luodaan keyHolder tallentamaan taulun generoima varausnumero
        KeyHolder keyHolder = new GeneratedKeyHolder();
        //Otetaan yhteys tietokantaan, haetaan lisättävän varauksen tiedot parametrina saadusta oliosta ja lisätään ne kyselyyn
        jdbcTemplate.update(connection -> {
            PreparedStatement stmt = connection.prepareStatement("INSERT INTO Varaus"
                    + " (asiakasnumero, alkupvm, loppupvm)"
                    + " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, varaus.getAsiakas().getAsiakasnumero());
            stmt.setString(2, varaus.getAlkupvm().toString());
            stmt.setString(3, varaus.getLoppupvm().toString());
            return stmt;
        }, keyHolder);
        
        //Päivitetään parametrina saatuun olioon todellinen varausnumero
        varaus.setVarausnumero(keyHolder.getKey().intValue());

        //Lisätään varausolioon tallennetut huoneet tilapäiselle listalle
        List<Huone> huoneet = varaus.getHuoneet();
        //Otetaan uusi yhteys tietokantaan, luodaan BatchPreparedStatementSetter hoitamaan useampi kysely samalla yhteydellä
        jdbcTemplate.batchUpdate("INSERT INTO VarausHuone (varausnumero, huonenumero) VALUES (?, ?)",
                new BatchPreparedStatementSetter() {

            //Valmistellaan kysely jokaisen huoneet-taulun rivin osalta
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setInt(1, varaus.getVarausnumero());
                ps.setInt(2, huoneet.get(i).getHuoneumero());
            }

            @Override
            public int getBatchSize() {
                return huoneet.size();
            }
        });
        
        // lisätään tietokantaan koko huonevarauksen yhteishinta
        // tämän olisi saanut toteutettua javassa jokseenkin suoraviivaisemmin, toteutus tehtiin SQL:ssä lähinnä harjoitusmielessä

        jdbcTemplate.update("WITH Hinnat AS (SELECT paivahinta FROM Huone "
                + "JOIN VarausHuone ON Huone.huonenumero = VarausHuone.huonenumero "
                + "Join Varaus ON VarausHuone.varausnumero = Varaus.varausnumero "
                + "WHERE Varaus.varausnumero = ?) "
                + "UPDATE Varaus SET yhteishinta = ((SELECT SUM(paivahinta) FROM Hinnat) * DATEDIFF(DAY,PARSEDATETIME(Varaus.alkupvm, 'yyyy-MM-dd'),PARSEDATETIME(Varaus.loppupvm, 'yyyy-MM-dd'))) "
                + "WHERE Varaus.varausnumero = ?;", varaus.getVarausnumero(), varaus.getVarausnumero());

        //Tarkistetaan onko varauksessa lisävarusteita
        if (!varaus.getLisavarusteet().isEmpty()) {
            //Lisätään varausolioon tallennetut lisävarusteet tilapäiselle listalle
            List<Lisavaruste> lisavarusteet = varaus.getLisavarusteet();
            //Otetaan uusi yhteys tietokantaan, luodaan BatchPreparedStatementSetter hoitamaan useampi kysely samalla yhteydellä
            jdbcTemplate.batchUpdate("INSERT INTO Lisavaruste (varausnumero, nimi) VALUES (?, ?)",
                    new BatchPreparedStatementSetter() {

                //Valmistellaan kysely jokaisen lisavaruste-taulun rivin osalta
                @Override
                public void setValues(PreparedStatement ps, int i) throws SQLException {
                    ps.setInt(1, varaus.getVarausnumero());
                    ps.setString(2, lisavarusteet.get(i).getNimi());
                }

                @Override
                public int getBatchSize() {
                    return lisavarusteet.size();
                }
            });
            System.out.println("Varaus tallennettu järjestelmään!");
        }

    }

    @Override
    public Varaus read(Integer key) throws SQLException {
        //Haetaan ensin varaukseen kuuluvat huoneet ja lisätään kyselyn tuottamat rivit listalle
        List<Map<String, Object>> rivit = jdbcTemplate.queryForList("SELECT * FROM Varaus"
                + " JOIN VarausHuone ON Varaus.varausnumero = VarausHuone.varausnumero"
                + " WHERE Varaus.varausnumero = ?", key);

        //Etsitään asiakas ensimmäisen rivin arvoista
        int asiakasnumero = (Integer) rivit.get(0).get("asiakasnumero");
        Asiakas asiakas = AsiakasDAO.read(asiakasnumero);

        //Etsitään varauksen muut tiedot ensimmäisen rivin arvoista
        Varaus varaus = new Varaus((Integer) rivit.get(0).get("varausnumero"), asiakas, LocalDate.parse((String) rivit.get(0).get("alkupvm")), LocalDate.parse((String) rivit.get(0).get("loppupvm")));

        //Käydään jokainen rivi läpi, lisätään varaukseen löydetyt huoneet
        for (Map rivi : rivit) {
            Huone huone = HuoneDAO.read((Integer) rivi.get("huonenumero"));
            varaus.addHuone(huone);
        }
        
        //Haetaan erikseen vielä lisävarusteet ja lisätää ne varaukseen
        ArrayList<Lisavaruste> lisavarusteet = new ArrayList<>();
        jdbcTemplate.query("SELECT nimi FROM Lisavaruste"
                + " JOIN Varaus ON Varaus.varausnumero = Lisavaruste.varausnumero"
                + " WHERE Varaus.varausnumero = ?", (rs, rowNum) -> lisavarusteet.add(new Lisavaruste(rs.getString("nimi"))), key);

        varaus.setLisavarusteet(lisavarusteet);

        // palautetaan varaus
        return varaus;
    }

    @Override
    public Varaus update(Varaus varaus) throws SQLException {
        jdbcTemplate.update("UPDATE Varaus SET alkupvm = ?, loppupvm = ? WHERE varausnumero = ?", varaus.getAlkupvm(), varaus.getLoppupvm(), varaus.getVarausnumero());
        return varaus;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        jdbcTemplate.update("DELETE FROM Varaus WHERE varausnumero = ?", key);
    }

    @Override
    public List<Varaus> list() throws SQLException {
        //Luodaan uusi hajautustaulu varauksien tallettamista varten
        HashMap<Integer, Varaus> varauksetMap = new HashMap<>();

        //Haetaan tietokannasta tiedot varauksista, talletetaan äsken luotuun tauluun muodossa varausnumero=Varaus
        jdbcTemplate.query("SELECT * FROM Varaus;", (rs, rowNum)
                -> varauksetMap.putIfAbsent(rs.getInt("varausnumero"),
                        new Varaus(rs.getInt("varausnumero"), AsiakasDAO.read(rs.getInt("asiakasnumero")), LocalDate.parse(rs.getString("alkupvm")), LocalDate.parse(rs.getString("loppupvm")))));

        //Haetaan tietokannasta kaikkien varattujen huoneiden tiedot yhdistettynä varausnumeroon, tallennetaan listalle riveittäin
        List<Map<String, Object>> huoneet = jdbcTemplate.queryForList("SELECT Varaus.varausnumero, Huone.huonenumero, Huone.tyyppi, Huone.paivahinta FROM Varaus\n"
                + "JOIN VarausHuone ON Varaus.varausnumero = VarausHuone.varausnumero\n"
                + "JOIN Huone ON Huone.huonenumero = VarausHuone.huonenumero;");

        //käydään läpi varausten ja huoneiden tiedot sisältävä lista riveittäin
        //etsitään varaukset sisältävästä hajautustaulusta käsiteltävän rivin varausnumeroa vastaava varaus
        //lisätään rivin tiedoista luotu huone varaukseen
        for (Map rivi : huoneet) {
            varauksetMap.get((Integer) rivi.get("varausnumero")).addHuone(new Huone((Integer) rivi.get("huonenumero"), (String) rivi.get("tyyppi"), (Integer) rivi.get("paivahinta")));
        }

        //Haetaan äskeiseen tapaan varausten lisävarusteet, toistetaan edellinen käsittely
        List<Map<String, Object>> lisavarusteet = jdbcTemplate.queryForList("SELECT Varaus.varausnumero, Lisavaruste.nimi FROM Varaus\n"
                + "JOIN Lisavaruste ON Varaus.varausnumero = Lisavaruste.varausnumero;");

        for (Map rivi : lisavarusteet) {
            varauksetMap.get((Integer) rivi.get("varausnumero")).addLisavaruste(new Lisavaruste((String) rivi.get("nimi")));
        }

        //Luodaan lista varausten palauttamista varten, lisätään kaikki hajautustaulun arvojoukon Varaus-oliot listalle
        List<Varaus> varauksetList = new ArrayList<>();
        varauksetList.addAll(varauksetMap.values());

        return varauksetList;
    }

    public List<Huone> search(LocalDateTime alkupvm, LocalDateTime loppupvm) throws SQLException {
        //muutetaan parametreina saadut LocalDateTime-tyyppiset päivämäärät merkkijonomuotoon
        String alku = alkupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String loppu = loppupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        String sql = "SELECT huonenumero, tyyppi, paivahinta FROM Huone\n"
                + "                WHERE huonenumero NOT IN (SELECT Huone.huonenumero FROM Huone\n"
                + "                LEFT JOIN VarausHuone ON Huone.huonenumero = VarausHuone.huonenumero\n"
                + "                LEFT JOIN Varaus ON VarausHuone.varausnumero = Varaus.varausnumero\n"
                + "                WHERE (CONCAT(alkupvm, ' 16:00') BETWEEN ? AND ? OR\n"
                + "                CONCAT(loppupvm, ' 10:00') BETWEEN ? AND ? OR\n"
                + "                ? BETWEEN CONCAT(alkupvm, ' 16:00') AND CONCAT(loppupvm, ' 10:00')));";

        return jdbcTemplate.query(sql, (rs, rowNum) -> new Huone(rs.getInt("huonenumero"), rs.getString("tyyppi"), rs.getInt("paivahinta")), alku, loppu, alku, loppu, alku);
    }

    public List<Huone> search(LocalDateTime alkupvm, LocalDateTime loppupvm, String tyyppi) throws SQLException {
        String alku = alkupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String loppu = loppupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return jdbcTemplate.query(
                "SELECT huonenumero, tyyppi, paivahinta FROM Huone\n"
                + "                WHERE huonenumero NOT IN (SELECT Huone.huonenumero FROM Huone\n"
                + "                LEFT JOIN VarausHuone ON Huone.huonenumero = VarausHuone.huonenumero\n"
                + "                LEFT JOIN Varaus ON VarausHuone.varausnumero = Varaus.varausnumero\n"
                + "                WHERE (CONCAT(alkupvm, ' 16:00') BETWEEN ? AND ? OR\n"
                + "                CONCAT(loppupvm, ' 10:00') BETWEEN ? AND ? OR\n"
                + "                ? BETWEEN CONCAT(alkupvm, ' 16:00') AND CONCAT(loppupvm, ' 10:00')))"
                + "AND tyyppi = ?;", (rs, rowNum) -> new Huone(rs.getInt("huonenumero"), rs.getString("tyyppi"), rs.getInt("paivahinta")), alku, loppu, alku, loppu, alku, tyyppi);
    }

    public List<Huone> search(LocalDateTime alkupvm, LocalDateTime loppupvm, int maksimihinta) throws SQLException {
        String alku = alkupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String loppu = loppupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return jdbcTemplate.query(
                "SELECT huonenumero, tyyppi, paivahinta FROM Huone\n"
                + "                WHERE huonenumero NOT IN (SELECT Huone.huonenumero FROM Huone\n"
                + "                LEFT JOIN VarausHuone ON Huone.huonenumero = VarausHuone.huonenumero\n"
                + "                LEFT JOIN Varaus ON VarausHuone.varausnumero = Varaus.varausnumero\n"
                + "                WHERE (CONCAT(alkupvm, ' 16:00') BETWEEN ? AND ? OR\n"
                + "                CONCAT(loppupvm, ' 10:00') BETWEEN ? AND ? OR\n"
                + "                ? BETWEEN CONCAT(alkupvm, ' 16:00') AND CONCAT(loppupvm, ' 10:00')))"
                + "AND paivahinta <= ? ;", (rs, rowNum) -> new Huone(rs.getInt("huonenumero"), rs.getString("tyyppi"), rs.getInt("paivahinta")), alku, loppu, alku, loppu, alku, maksimihinta);
    }

    public List<Huone> search(LocalDateTime alkupvm, LocalDateTime loppupvm, String tyyppi, int maksimihinta) throws SQLException {
        String alku = alkupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        String loppu = loppupvm.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        return jdbcTemplate.query(
                "SELECT huonenumero, tyyppi, paivahinta FROM Huone\n"
                + "                WHERE huonenumero NOT IN (SELECT Huone.huonenumero FROM Huone\n"
                + "                LEFT JOIN VarausHuone ON Huone.huonenumero = VarausHuone.huonenumero\n"
                + "                LEFT JOIN Varaus ON VarausHuone.varausnumero = Varaus.varausnumero\n"
                + "                WHERE (CONCAT(alkupvm, ' 16:00') BETWEEN ? AND ? OR\n"
                + "                CONCAT(loppupvm, ' 10:00') BETWEEN ? AND ? OR\n"
                + "                ? BETWEEN CONCAT(alkupvm, ' 16:00') AND CONCAT(loppupvm, ' 10:00')))"
                + "AND tyyppi = ? AND paivahinta <= ?;", (rs, rowNum) -> new Huone(rs.getInt("huonenumero"), rs.getString("tyyppi"), rs.getInt("paivahinta")), alku, loppu, alku, loppu, alku, tyyppi, maksimihinta);
    }
}