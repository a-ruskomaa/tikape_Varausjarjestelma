package varausjarjestelma;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Scanner;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
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
    TilastoTulostaja TilastoTulostaja;

    @Autowired
    JdbcTemplate JdbcTemplate;

    public void kaynnista(Scanner lukija) {
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
    }

    private void haeHuoneita(Scanner s) {
        System.out.println("Haetaan huoneita");
        System.out.println("");

        System.out.println("Milloin varaus alkaisi (yyyy-MM-dd)?");
        LocalDateTime alku = LocalDateTime.parse(s.nextLine() + " " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Milloin varaus loppuisi (yyyy-MM-dd)?");
        LocalDateTime loppu = LocalDateTime.parse(s.nextLine() + " " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Minkä tyyppinen huone? (tyhjä = ei rajausta)");
        String tyyppi = s.nextLine();
        System.out.println("Minkä hintainen korkeintaan? (tyhjä = ei rajausta)");
        String maksimihinta = s.nextLine();

        // luodaan lista haun palauttamia huoneita varten, ajetaan search metodi halutuilla rajauksilla
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

        } else {
            System.out.println("Ei vapaita huoneita.");
        }

    }

    private void lisaaVaraus(Scanner s) {
        System.out.println("Haetaan huoneita");
        System.out.println("");

        // luodaan "placeholder"-varaus varausnumerolla -1
        Varaus varaus = new Varaus(-1);

        System.out.println("Milloin varaus alkaisi (yyyy-MM-dd)?");
        LocalDateTime alku = LocalDateTime.parse(s.nextLine() + " " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Milloin varaus loppuisi (yyyy-MM-dd)?");
        LocalDateTime loppu = LocalDateTime.parse(s.nextLine() + " " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Minkä tyyppinen huone? (tyhjä = ei rajausta)");
        String tyyppi = s.nextLine();
        System.out.println("Minkä hintainen korkeintaan? (tyhjä = ei rajausta)");
        String maksimihinta = s.nextLine();

        // päivitetään varauksen tietoihin halutut päivämäärät
        varaus.setAlkupvm(alku.toLocalDate());
        varaus.setLoppupvm(loppu.toLocalDate());

        // luodaan lista haun palauttamia huoneita varten, ajetaan search metodi halutuilla rajauksilla
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
            System.out.println("Ei vapaita huoneita.");
            return;
        } else {
            System.out.println("Huoneita vapaana: " + haetut.size());
            System.out.println("");

        }

        // kysytään haluttujen huoneiden määrä, hyväksytään vain kelvollinen arvo
        int huoneita = -1;
        while (true) {
            System.out.println("Montako huonetta varataan?");
            huoneita = Integer.valueOf(s.nextLine());
            if (huoneita >= 1 && huoneita <= haetut.size()) {
                break;
            }

            System.out.println("Epäkelpo huoneiden lukumäärä.");
        }

        // järjestetään huoneet laskevaan hintajärjestykseen
        Collections.sort(haetut, new Comparator<Huone>() {
            @Override
            public int compare(Huone h1, Huone h2) {
                return h2.getPaivahinta().compareTo(h1.getPaivahinta());
            }
        });

        // poistetaan yli jääneet huoneet listalta
        int i = huoneita;
        while (haetut.size() > huoneita) {
            haetut.remove(i);
        }

        // lisätään n kalleinta huonetta varaukseen
        varaus.setHuoneet(haetut);

        // kysytään lisävarusteet ja lisätään varaukseen
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

        // ja lopuksi kysytään varaajan tiedot
        System.out.println("Syötä varaajan nimi:");
        String nimi = s.nextLine();
        System.out.println("Syötä varaajan puhelinnumero:");
        String puhelinnumero = s.nextLine();
        System.out.println("Syötä varaajan sähköpostiosoite:");
        String sahkoposti = s.nextLine();

        // luodaan asiakas ilman asiakasnumeroa 
        Asiakas asiakas = new Asiakas(-1, nimi, sahkoposti, puhelinnumero);

        try {
            try {
                // kokeillaan ensin löytyykö tietokannasta asiakasta annetuilla tiedoilla
                // palautetaan asiakasnumerolla päivitetyt tiedot jos löytyy
                asiakas = AsiakasDAO.read(nimi, sahkoposti, puhelinnumero);
                System.out.println("Asiakkaan tiedot löytyivät, lisätään varaus samalle asiakkaalle!");
            } catch (EmptyResultDataAccessException e) {
                // luodaan uusi asiakas jos asiakasta ei löytynyt
                System.out.println("Asiakasta ei löytynyt, lisätään uuden asiakkaan tiedot järjestelmään");
                AsiakasDAO.create(asiakas);
            }

            // liitetään asiakkaan tiedot varaukseen
            varaus.setAsiakas(asiakas);

            System.out.println("Tallennetaan varausta");
            VarausDAO.create(varaus);
        } catch (Exception e) {
            e.getStackTrace();
        }   
    }

    private void listaaVaraukset() {
        System.out.println("Listataan varaukset");
        System.out.println("");

        try {
            // haetaan tietokannasta varaukset
            List<Varaus> varaukset = VarausDAO.list();

            // järjestetään lista uusin varaus ensin
            Collections.sort(varaukset, new Comparator<Varaus>() {
                @Override
                public int compare(Varaus v1, Varaus v2) {
                    return v2.getAlkupvm().compareTo(v1.getAlkupvm());
                }
            });

            for (Varaus varaus : varaukset) {
                System.out.println(varaus);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    private void tilastoja(Scanner lukija) {
        System.out.println("Mitä tilastoja tulostetaan?");
        System.out.println("");

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

        TilastoTulostaja.suosituimmatLisavarusteet();
    }

    private void parhaatAsiakkaat() {
        System.out.println("Tulostetaan parhaat asiakkaat");
        System.out.println("");

        TilastoTulostaja.parhaatAsiakkaat();
    }

    private void varausprosenttiHuoneittain(Scanner lukija) {
        System.out.println("Tulostetaan varausprosentti huoneittain");
        System.out.println("");

        System.out.println("Mistä lähtien tarkastellaan?");
        LocalDateTime alku = LocalDateTime.parse(lukija.nextLine() + "-01 " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Mihin asti tarkastellaan?");
        LocalDateTime loppu = LocalDateTime.parse(lukija.nextLine() + "-01 " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        System.out.println("");
        
        TilastoTulostaja.laskeVarausprosentti(alku, loppu, "huoneittain");
    }

    private void varausprosenttiHuonetyypeittain(Scanner lukija) {
        System.out.println("Tulostetaan varausprosentti huonetyypeittäin");
        System.out.println("");

        System.out.println("Mistä lähtien tarkastellaan?");
        LocalDateTime alku = LocalDateTime.parse(lukija.nextLine() + "-01 " + "16:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        System.out.println("Mihin asti tarkastellaan?");
        LocalDateTime loppu = LocalDateTime.parse(lukija.nextLine() + "-01 " + "10:00", DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));

        System.out.println("");
        
        TilastoTulostaja.laskeVarausprosentti(alku, loppu, "tyypeittain");
    }

}
