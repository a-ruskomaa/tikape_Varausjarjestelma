/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import java.sql.PreparedStatement;
import java.sql.Statement;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
/**
 *
 * @author aleks
 */
@Component
public class AsiakasDAO implements DAO<Asiakas, Integer> {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public void create(Asiakas asiakas) throws SQLException {
        
        //Luodaan keyHolder tallentamaan taulun generoima asiakasnumero
        KeyHolder keyHolder = new GeneratedKeyHolder();
        //Otetaan yhteys tietokantaan, haetaan lisättävän asiakkaan tiedot parametrina saadusta oliosta ja lisätään ne kyselyyn
        jdbcTemplate.update(connection -> {
        PreparedStatement stmt = connection.prepareStatement("INSERT INTO Asiakas"
        + " (nimi, email, puhelin)"
        + " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, asiakas.getNimi());
        stmt.setString(2, asiakas.getEmail());
        stmt.setString(3, asiakas.getPuhelin());
        return stmt;
        }, keyHolder);
        //päivitetään parametrina saatuun olioon tietokannan generoima todellinen asiakasnumero
        asiakas.setAsiakasnumero(keyHolder.getKey().intValue());
        System.out.println("Asiakas lisätty!");
    }

    @Override
    public Asiakas read(Integer key) throws SQLException {
        // etsitään tietokannasta asiakasta annetuilla tiedoilla, mahdollisen virheen käsittely metodin ulkopuolella
        Asiakas asiakas = jdbcTemplate.queryForObject(
        "SELECT * FROM Asiakas WHERE asiakasnumero = ?",
        new BeanPropertyRowMapper<>(Asiakas.class),
        key);

        return asiakas;
    }
    
    public Asiakas read(String nimi, String email, String puhelin) throws SQLException {
        // etsitään tietokannasta asiakasta annetuilla tiedoilla, mahdollisen virheen käsittely metodin ulkopuolella
        Asiakas asiakas = jdbcTemplate.queryForObject(
        "SELECT * FROM Asiakas WHERE nimi = ? AND email = ? AND puhelin = ?",
        new BeanPropertyRowMapper<>(Asiakas.class),
        nimi, email, puhelin);

        return asiakas;
    }

    @Override
    public Asiakas update(Asiakas asiakas) throws SQLException {
        jdbcTemplate.update("UPDATE Asiakas SET nimi = ?, email = ?, puhelin = ? WHERE asiakasnumero = ?", asiakas.getNimi(), asiakas.getEmail(), asiakas.getPuhelin(), asiakas.getAsiakasnumero());
        return asiakas;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        jdbcTemplate.update("DELETE FROM Asiakas WHERE asiakasnumero = ?;", key);        
    }

    @Override
    public List<Asiakas> list() throws SQLException {
        return jdbcTemplate.query(
        "SELECT * FROM Asiakas;",
        new BeanPropertyRowMapper<>(Asiakas.class));
    }
    
}
