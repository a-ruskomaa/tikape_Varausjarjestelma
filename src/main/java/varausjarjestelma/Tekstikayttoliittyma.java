package varausjarjestelma;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

@Component
public class Tekstikayttoliittyma {

    @Autowired
    HuoneDAO HuoneDAO;

    @Autowired
    AsiakasDAO AsiakasDAO;

    @Autowired
    VarausDAO VarausDAO;

    @Autowired
    JdbcTemplate JdbcTemplate;

    public void kaynnista(Scanner lukija) {
        testaaHuoneita();
        testaaAsiakasta();
        testaaVarausta();
        while (true) {
            System.out.println("Komennot: ");
            System.out.println(" x - lopeta");
            System.out.println(" 1 - lisaa huone");
            System.out.println(" 2 - listaa huoneet");
            System.out.println(" 3 - hae huoneita");
            System.out.println(" 4 - lisaa varaus");
            System.out.println(" 5 - listaa varaukset");
            System.out.println(" 6 - tilastoja");
            System.out.println("");

            String komento = lukija.nextLine();
            if (komento.equals("x")) {
                break;
            }

            if (komento.equals("1")) {
                lisaaHuone(lukija);
            } else if (komento.equals("2")) {
                listaaHuoneet();
            } else if (komento.equals("3")) {
                haeHuoneita(lukija);
            } else if (komento.equals("4")) {
                lisaaVaraus(lukija);
            } else if (komento.equals("5")) {
                listaaVaraukset();
            } else if (komento.equals("6")) {
                tilastoja(lukija);
            }
        }
    }

    private void lisaaHuone(Scanner s) {
        System.out.println("Lisätään huone");
        System.out.println("");

        System.out.println("Minkä tyyppinen huone on?");
        String tyyppi = s.nextLine();
        System.out.println("Mikä huoneen numeroksi asetetaan?");
        int numero = Integer.valueOf(s.nextLine());
        System.out.println("Kuinka monta euroa huone maksaa yöltä?");
        int hinta = Integer.valueOf(s.nextLine());

        Huone huone = new Huone(numero, tyyppi, hinta);
        System.out.println(huone);

        try {
            HuoneDAO.create(huone);
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }
    }

    private void listaaHuoneet() {
        System.out.println("Listataan huoneet");
        System.out.println("");

        try {
            List<Huone> huoneet = HuoneDAO.list();
            for (Huone huone : huoneet) {
                System.out.println(huone);
            }
        } catch (Exception e) {
            System.out.println("Error: " + e);
        }

        // esimerkkitulostus -- tässä oletetaan, että huoneita on 4
        // tulostuksessa tulostetaan huoneen tyyppi, huoneen numero sekä hinta
//        System.out.println("Excelsior, 604, 119 euroa");
//        System.out.println("Excelsior, 605, 119 euroa");
//        System.out.println("Superior, 705, 159 euroa");
//        System.out.println("Commodore, 128, 229 euroa");
    }

    private void haeHuoneita(Scanner s) {
        System.out.println("Haetaan huoneita");
        System.out.println("");

        System.out.println("Milloin varaus alkaisi (yyyy-MM-dd)?");;
        LocalDateTime alku = LocalDateTime.parse(s.nextLine() + " " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Milloin varaus loppuisi (yyyy-MM-dd)?");
        LocalDateTime loppu = LocalDateTime.parse(s.nextLine() + " " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Minkä tyyppinen huone? (tyhjä = ei rajausta)");
        String tyyppi = s.nextLine();
        System.out.println("Minkä hintainen korkeintaan? (tyhjä = ei rajausta)");
        String maksimihinta = s.nextLine();

        List<Huone> haetut = new ArrayList<>();
        try {
            if (tyyppi.isEmpty() && maksimihinta.isEmpty()) {
                haetut = VarausDAO.search(alku, loppu);
            } else if (tyyppi.isEmpty()) {
                haetut = VarausDAO.search(alku, loppu, Integer.valueOf(maksimihinta));
            } else if (maksimihinta.isEmpty()) {
                haetut = VarausDAO.search(alku, loppu, tyyppi);
            } else {
                haetut = VarausDAO.search(alku, loppu, tyyppi, Integer.valueOf(maksimihinta));
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        if (!haetut.isEmpty()) {
            System.out.println("Vapaat huoneet: ");
            for (Huone huone : haetut) {
                System.out.println(huone);
            }
            // esimerkkitulostus -- tässä oletetaan, että vapaita huoneita löytyy 2
//            System.out.println("Excelsior, 604, 119 euroa");
//            System.out.println("Excelsior, 605, 119 euroa");
        } else {
            // vaihtoehtoisesti, mikäli yhtäkään huonetta ei ole vapaana, ohjelma
            // tulostaa
            System.out.println("Ei vapaita huoneita.");
        }

    }

    private void lisaaVaraus(Scanner s) {
        System.out.println("Haetaan huoneita");
        System.out.println("");

        Varaus varaus = new Varaus(-1);

        System.out.println("Milloin varaus alkaisi (yyyy-MM-dd)?");
        LocalDateTime alku = LocalDateTime.parse(s.nextLine() + " " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Milloin varaus loppuisi (yyyy-MM-dd)?");
        LocalDateTime loppu = LocalDateTime.parse(s.nextLine() + " " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Minkä tyyppinen huone? (tyhjä = ei rajausta)");
        String tyyppi = s.nextLine();
        System.out.println("Minkä hintainen korkeintaan? (tyhjä = ei rajausta)");
        String maksimihinta = s.nextLine();

        varaus.setAlkupvm(alku.toLocalDate());
        varaus.setLoppupvm(loppu.toLocalDate());

        List<Huone> haetut = new ArrayList<>();
        try {
            if (tyyppi.isEmpty() && maksimihinta.isEmpty()) {
                haetut = VarausDAO.search(alku, loppu);
            } else if (tyyppi.isEmpty()) {
                haetut = VarausDAO.search(alku, loppu, Integer.valueOf(maksimihinta));
            } else if (maksimihinta.isEmpty()) {
                haetut = VarausDAO.search(alku, loppu, tyyppi);
            } else {
                haetut = VarausDAO.search(alku, loppu, tyyppi, Integer.valueOf(maksimihinta));
            }
        } catch (Exception e) {
            e.getStackTrace();
        }

        if (haetut.isEmpty()) {
            // mikäli huoneita ei ole vapaana, ohjelma tulostaa seuraavan viestin
            // ja varauksen lisääminen loppuu
            System.out.println("Ei vapaita huoneita.");
            return;
        } else {
            // muulloin, ohjelma kertoo vapaiden huoneiden lukumäärän. Tässä 
            // oletetaan että vapaita huoneita on 2.
            System.out.println("Huoneita vapaana: " + haetut.size());
            System.out.println("");

        }

        // tämän jälkeen kysytään varattavien huoneiden lukumäärää
        // luvuksi tulee hyväksyä vain sopiva luku, esimerkissä 3 ei esim
        // kävisi, sillä vapaita huoneita vain 2
        int huoneita = -1;
        while (true) {
            System.out.println("Montako huonetta varataan?");
            huoneita = Integer.valueOf(s.nextLine());
            if (huoneita >= 1 && huoneita <= haetut.size()) {
                break;
            }

            System.out.println("Epäkelpo huoneiden lukumäärä.");
        }

        //järjestetään huoneet hintajärjestykseen
        Collections.sort(haetut, new Comparator<Huone>() {
            @Override
            public int compare(Huone h1, Huone h2) {
                return h2.getPaivahinta().compareTo(h1.getPaivahinta());
            }
        });
        
        //poistetaan ylijääneet huoneet listalta
        int i = huoneita;
        while (haetut.size() > huoneita) {
            haetut.remove(i);
        }
        
        //lisätään n kalleinta huonetta varaukseen
        varaus.setHuoneet(haetut);

        // tämän jälkeen kysytään lisävarusteet
        List<Lisavaruste> lisavarusteet = new ArrayList<>();
        while (true) {
            System.out.println("Syötä lisävaruste, tyhjä lopettaa");
            String nimi = s.nextLine();
            if (nimi.isEmpty()) {
                break;
            }

            lisavarusteet.add(new Lisavaruste(nimi));
        }

        varaus.setLisavarusteet(lisavarusteet);

        // ja lopuksi varaajan tiedot
        System.out.println("Syötä varaajan nimi:");
        String nimi = s.nextLine();
        System.out.println("Syötä varaajan puhelinnumero:");
        String puhelinnumero = s.nextLine();
        System.out.println("Syötä varaajan sähköpostiosoite:");
        String sahkoposti = s.nextLine();

        Asiakas asiakas = new Asiakas(-1, nimi, sahkoposti, puhelinnumero);

        try {
            System.out.println("Lisätään asiakasta...");
            AsiakasDAO.create(asiakas);
        } catch (Exception e) {
            e.getStackTrace();
        }
        varaus.setAsiakas(asiakas);
        
        try {
            System.out.println("Lisätään varausta...");
            VarausDAO.create(varaus);
        } catch (Exception e) {
            e.getStackTrace();
        }
        // kun kaikki tiedot on kerätty, ohjelma lisää varauksen tietokantaan
        // -- varaukseen tulee lisätä kalleimmat vapaat huoneet!        
    }

    private void listaaVaraukset() {
        System.out.println("Listataan varaukset");
        System.out.println("");

        try {
            List<Varaus> varaukset = VarausDAO.list();
            for (Varaus varaus : varaukset) {
                System.out.println(varaus);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        
        // alla olevassa esimerkissä oletetaan, että tietokannassa on 
        // kolme varausta
//        System.out.println("Essi Esimerkki, essi@esimerkki.net, 2019-02-14, 2019-02-15, 1 päivä, 2 lisävarustetta, 1 huone. Huoneet:");
//        System.out.println("\tCommodore, 128, 229 euroa");
//        System.out.println("\tYhteensä: 229 euroa");
//        System.out.println("");
//        System.out.println("Anssi Asiakas, anssi@asiakas.net, 2019-02-14, 2019-02-15, 1 päivä, 0 lisävarustetta, 1 huone. Huoneet:");
//        System.out.println("\tSuperior, 705, 159 euroa");
//        System.out.println("\tYhteensä: 159 euroa");
//        System.out.println("");
//        System.out.println("Anssi Asiakas, anssi@asiakas.net, 2020-03-18, 2020-03-21, 3 päivää, 6 lisävarustetta, 2 huonetta. Huoneet:");
//        System.out.println("\tSuperior, 705, 159 euroa");
//        System.out.println("\tCommodore, 128, 229 euroa");
//        System.out.println("\tYhteensä: 1164 euroa");

    }

    private void tilastoja(Scanner lukija) {
        System.out.println("Mitä tilastoja tulostetaan?");
        System.out.println("");

        // tilastoja pyydettäessä käyttäjältä kysytään tilasto
        System.out.println(" 1 - Suosituimmat lisävarusteet");
        System.out.println(" 2 - Parhaat asiakkaat");
        System.out.println(" 3 - Varausprosentti huoneittain");
        System.out.println(" 4 - Varausprosentti huonetyypeittäin");

        System.out.println("Syötä komento: ");
        int komento = Integer.valueOf(lukija.nextLine());

        if (komento == 1) {
            suosituimmatLisavarusteet();
        } else if (komento == 2) {
            parhaatAsiakkaat();
        } else if (komento == 3) {
            varausprosenttiHuoneittain(lukija);
        } else if (komento == 4) {
            varausprosenttiHuonetyypeittain(lukija);
        }
    }

    private void suosituimmatLisavarusteet() {
        System.out.println("Tulostetaan suosituimmat lisävarusteet");
        System.out.println("");
        
        Tilastot.suosituimmatLisavarusteet();

        // alla oletetaan, että lisävarusteita on vain muutama
        // mikäli tietokannassa niitä on enemmän, tulostetaan 10 suosituinta
        System.out.println("Teekannu, 2 varausta");
        System.out.println("Kahvinkeitin, 2 varausta");
        System.out.println("Silitysrauta, 1 varaus");
    }

    private void parhaatAsiakkaat() {
        System.out.println("Tulostetaan parhaat asiakkaat");
        System.out.println("");

        // alla oletetaan, että asiakkaita on vain 2
        // mikäli tietokannassa niitä on enemmän, tulostetaan asiakkaita korkeintaan 10
        System.out.println("Anssi Asiakas, anssi@asiakas.net, +358441231234, 1323 euroa");
        System.out.println("Essi Esimerkki, essi@esimerkki.net, +358443214321, 229 euroa");
    }

    private void varausprosenttiHuoneittain(Scanner lukija) {
        System.out.println("Tulostetaan varausprosentti huoneittain");
        System.out.println("");

        System.out.println("Mistä lähtien tarkastellaan?");
        LocalDateTime alku = LocalDateTime.parse(lukija.nextLine() + "-01 " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Mihin asti tarkastellaan?");
        LocalDateTime loppu = LocalDateTime.parse(lukija.nextLine() + "-01 " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // alla esimerkkitulostus
        System.out.println("Tulostetaan varausprosentti huoneittain");
        System.out.println("Excelsior, 604, 119 euroa, 0.0%");
        System.out.println("Excelsior, 605, 119 euroa, 0.0%");
        System.out.println("Superior, 705, 159 euroa, 22.8%");
        System.out.println("Commodore, 128, 229 euroa, 62.8%");
    }

    private void varausprosenttiHuonetyypeittain(Scanner lukija) {
        System.out.println("Tulostetaan varausprosentti huonetyypeittäin");
        System.out.println("");

        System.out.println("Mistä lähtien tarkastellaan?");
        LocalDateTime alku = LocalDateTime.parse(lukija.nextLine() + "-01 " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Mihin asti tarkastellaan?");
        LocalDateTime loppu = LocalDateTime.parse(lukija.nextLine() + "-01 " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        // alla esimerkkitulostus
        System.out.println("Tulostetaan varausprosentti huonetyypeittän");
        System.out.println("Excelsior, 0.0%");
        System.out.println("Superior, 22.8%");
        System.out.println("Commodore, 62.8%");
    }

    private void testaaHuoneita() {

        try {
            System.out.println("Testataan huoneita...");
//            List<Huone> huoneet = new ArrayList();
//            huoneet.add(new Huone(101, "Standard", 100));
//            huoneet.add(new Huone(102, "Standard", 120));
//            huoneet.add(new Huone(201, "Suite", 200));
//            huoneet.add(new Huone(202, "Standard", 105));
//            huoneet.add(new Huone(301, "Suite", 195));
//
//            for (Huone huone : huoneet) {
//                HuoneDAO.create(huone);
//            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void testaaAsiakasta() {

        try {
            System.out.println("Testataan asiakkaita...");
//            Asiakas asiakas = new Asiakas(-1, "Janne Makkonen", "janne.makkonen@net.fi", "0401231231");
//            AsiakasDAO.create(asiakas);
//            System.out.println(AsiakasDAO.read(asiakas.getAsiakasnumero()));
//            asiakas.setPuhelin("0501212122");
//            AsiakasDAO.update(asiakas);
//            System.out.println(AsiakasDAO.read(asiakas.getAsiakasnumero()));
//
//            AsiakasDAO.create(new Asiakas(-1, "Anssi Apina", "anssin@maili.com", "0700123123"));
//            System.out.println(AsiakasDAO.list());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void testaaVarausta() {
        try {
            System.out.println("Testataan varauksia...");
            System.out.println(VarausDAO.read(4));
//            Varaus varaus = new Varaus(-1, AsiakasDAO.read(1), LocalDate.parse("2018-07-05"), LocalDate.parse("2018-07-12"));
//            varaus.addHuone(HuoneDAO.read(101));
//            varaus.addHuone(HuoneDAO.read(102));
//            VarausDAO.create(varaus);
//
//            System.out.println("read:" + VarausDAO.read(varaus.getVarausnumero()));
//            varaus.setLoppupvm(LocalDate.parse("2018-07-13"));
//            System.out.println("update:" + VarausDAO.update(varaus));
//            System.out.println("read:" + VarausDAO.read(varaus.getVarausnumero()));
//
//            List<Huone> huoneet = new ArrayList();
//            huoneet.add(new Huone(201, "Suite", 200));
//            VarausDAO.create(new Varaus(-1, AsiakasDAO.read(2), LocalDate.parse("2018-07-01"), LocalDate.parse("2018-07-08"), huoneet));
//
//            varaus = new Varaus(-1, AsiakasDAO.read(1), LocalDate.parse("2018-06-28"), LocalDate.parse("2018-07-03"));
//            varaus.addHuone(HuoneDAO.read(101));
//            VarausDAO.create(varaus);
//
//            System.out.println("list:" + VarausDAO.list());
//
//            System.out.println(VarausDAO.search(LocalDateTime.parse("2018-06-27 16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), LocalDateTime.parse("2018-07-01 10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
