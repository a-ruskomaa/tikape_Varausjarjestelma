package varausjarjestelma;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class VarausjarjestelmaSovellus implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(VarausjarjestelmaSovellus.class);
    }

    @Autowired
    Tekstikayttoliittyma tekstikayttoliittyma;

    @Override
    public void run(String... args) throws Exception {
//        alustaTietokanta();
        Scanner lukija = new Scanner(System.in);
        tekstikayttoliittyma.kaynnista(lukija);
    }
    private static void alustaTietokanta() {
        // mikäli poistat vahingossa tietokannan voit ajaa tämän metodin jolloin 
        // tietokantataulu luodaan uudestaan

        try (Connection conn = DriverManager.getConnection("jdbc:h2:./hotelliketju", "sa", "")) {
            conn.prepareStatement("DROP TABLE Asiakas, Varaus, Tyyppi, Huone, Lisavaruste, VarausHuone IF EXISTS;").executeUpdate();
            conn.prepareStatement("CREATE TABLE Asiakas (\n "
                                + "asiakasnumero INTEGER PRIMARY KEY AUTO_INCREMENT,\n "
                                + "nimi VARCHAR(50),\n "
                                + "email VARCHAR(50) NOT NULL,\n "
                                + "puhelin VARCHAR(20) NOT NULL);\n "

                                + "CREATE TABLE Varaus (\n "
                                + "varausnumero INTEGER PRIMARY KEY AUTO_INCREMENT,\n "
                                + "asiakasnumero INTEGER NOT NULL,\n "
                                + "alkupvm VARCHAR(16) NOT NULL,\n "
                                + "loppupvm VARCHAR(16) NOT NULL,\n "
                                + "yhteishinta INTEGER,\n "
                                + "FOREIGN KEY (asiakasnumero) REFERENCES Asiakas(asiakasnumero));\n "

                                + "CREATE TABLE Huone (\n "
                                + "huonenumero INTEGER PRIMARY KEY,\n "
                                + "tyyppi VARCHAR(20),\n "
                                + "paivahinta INTEGER);\n "

                                + "CREATE TABLE Lisavaruste (\n "
                                + "id INTEGER AUTO_INCREMENT PRIMARY KEY,\n "
                                + "varausnumero INTEGER,\n "
                                + "nimi VARCHAR(20),\n "
                                + "FOREIGN KEY (varausnumero) REFERENCES Varaus(varausnumero));\n "

                                + "CREATE TABLE VarausHuone (\n "
                                + "varausnumero INTEGER,\n "
                                + "huonenumero INTEGER,\n "
                                + "PRIMARY KEY(varausnumero, huonenumero),\n "
                                + "FOREIGN KEY (varausnumero) REFERENCES Varaus(varausnumero),\n "
                                + "FOREIGN KEY (huonenumero) REFERENCES Huone(huonenumero));\n "
                ).executeUpdate();
            System.out.println("");
            System.out.println("---------- Tietokanta alustettu! ----------");
            System.out.println("");

        } catch (SQLException e) {
            System.out.println("SQL ERROR: " + e);
        }
    }
}
