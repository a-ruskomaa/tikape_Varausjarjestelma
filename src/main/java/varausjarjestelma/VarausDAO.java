/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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
                    + " VALUES (?, ?, ?)");
            stmt.setInt(1, varaus.getAsiakas().getAsiakasnumero());
            stmt.setString(2, varaus.getAlkupvm());
            stmt.setString(3, varaus.getLoppupvm());
            return stmt;
        }, keyHolder);
        //Päivitetään varausolioon todellinen varausnumero
        varaus.setVarausnumero((int) keyHolder.getKey());

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
            //Tanne lisattava viela varauksen yhteishinnan laskeva koodi?
        });
        //Tanne lisattava viela halutut lisavarusteet
    }

    @Override
    public Varaus read(Integer key) throws SQLException {
        List<Map<String, Object>> rivit = jdbcTemplate.queryForList("SELECT * FROM Varaus"
        +                   "JOIN VarausHuone ON Varaus.varausnumero = VarausHuone.varausnumero"
        +                   "WHERE varausnumero = ?");
        Varaus varaus = new Varaus();
        
        int asiakasnumero = (Integer)rivit.get(0).get("asiakasnumero");
        Asiakas asiakas = AsiakasDAO.read(asiakasnumero);
        
        varaus.setVarausnumero((Integer)rivit.get(0).get("varausnumero"));
        varaus.setAsiakas(asiakas);
        varaus.setAlkupvm((String)rivit.get(0).get("alkupvm"));
        varaus.setLoppupvm((String)rivit.get(0).get("loppupvm"));
        
        for (Map rivi : rivit) {
            varaus.addHuone(HuoneDAO.read((int)rivi.get("huonenumero")));
        }
        return varaus;
    }

    @Override
    public Varaus update(Varaus varaus) throws SQLException {
        return null;
    }

    @Override
    public void delete(Integer key) throws SQLException {

    }

    @Override
    public List<Varaus> list() throws SQLException {
        return null;
    }
    
    public List<Huone> search() throws SQLException {
        return null;
    }

}
