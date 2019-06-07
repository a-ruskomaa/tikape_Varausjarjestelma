/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
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
                    + " VALUES (?, ?, ?)", Statement.RETURN_GENERATED_KEYS);
            stmt.setInt(1, varaus.getAsiakas().getAsiakasnumero());
            stmt.setString(2, varaus.getAlkupvm());
            stmt.setString(3, varaus.getLoppupvm());
            return stmt;
        }, keyHolder);
        //Päivitetään varausolioon todellinen varausnumero
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
            //Tanne lisattava viela varauksen yhteishinnan laskeva koodi?
        });
        //Tanne lisattava viela halutut lisavarusteet
    }

    @Override
    public Varaus read(Integer key) throws SQLException {
        List<Map<String, Object>> rivit = jdbcTemplate.queryForList("SELECT * FROM Varaus"
        +                   " JOIN VarausHuone ON Varaus.varausnumero = VarausHuone.varausnumero"
        +                   " WHERE Varaus.varausnumero = ?", key);
                
        int asiakasnumero = (Integer)rivit.get(0).get("asiakasnumero");
        Asiakas asiakas = AsiakasDAO.read(asiakasnumero);
        
        Varaus varaus = new Varaus((Integer)rivit.get(0).get("varausnumero"), asiakas, (String)rivit.get(0).get("alkupvm"),(String)rivit.get(0).get("loppupvm"));
        
        for (Map rivi : rivit) {
            Huone huone = HuoneDAO.read((Integer)rivi.get("huonenumero"));
            varaus.addHuone(huone);
        }
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
        List<Map<String, Object>> rivit = jdbcTemplate.queryForList("SELECT * FROM Varaus"
        +                   " JOIN VarausHuone ON Varaus.varausnumero = VarausHuone.varausnumero");
        
        List<Varaus> varaukset = new ArrayList<>();
        
        int asiakasno = -1;
        int varausno = -1;
        Asiakas asiakas = new Asiakas();
        Varaus varaus = new Varaus();
        
        for (int i = 0; i < rivit.size(); i++) {
            int asiakasnumero = (Integer)rivit.get(i).get("asiakasnumero");
            if (asiakasno != asiakasnumero) {
                asiakas = AsiakasDAO.read(asiakasnumero);
                asiakasno = asiakasnumero;
            }
            int varausnumero = (Integer)rivit.get(i).get("varausnumero");
            if (varausno != varausnumero) {
                varaus = new Varaus(varausnumero, asiakas, (String)rivit.get(i).get("alkupvm"),(String)rivit.get(i).get("loppupvm"));
                varaukset.add(varaus);
                Huone huone = HuoneDAO.read((Integer)rivit.get(i).get("huonenumero"));
                varaus.addHuone(huone);
                varausno = varausnumero;
            } else {
                Huone huone = HuoneDAO.read((Integer)rivit.get(i).get("huonenumero"));
                varaus.addHuone(huone);
            }
            
        }

        return varaukset;
    }
    
    public List<Huone> search(Integer varausnumero) throws SQLException {
        return null;
    }
    
    public List<Huone> search(String alkupvm, String loppupvm) throws SQLException {
        return null;
    }


}
