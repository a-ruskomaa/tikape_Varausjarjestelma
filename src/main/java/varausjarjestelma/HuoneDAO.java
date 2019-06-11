/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

import java.sql.SQLException;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
/**
 *
 * @author aleks
 */
@Component
public class HuoneDAO implements DAO<Huone, Integer> {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public void create(Huone huone) {
        jdbcTemplate.update("INSERT INTO Huone (huonenumero, tyyppi, paivahinta) VALUES (?,?,?)", huone.getHuoneumero(), huone.getTyyppi(), huone.getPaivahinta());
    }

    @Override
    public Huone read(Integer key) throws SQLException {
        Huone huone = jdbcTemplate.queryForObject(
            "SELECT huonenumero, tyyppi, paivahinta FROM Huone WHERE huonenumero = ?",(rs, rowNum) -> new Huone (rs.getInt("huonenumero"), rs.getString("tyyppi"), rs.getInt("paivahinta")),
            key);
        return huone;
    }

    @Override
    public Huone update(Huone huone) throws SQLException {
        jdbcTemplate.update("UPDATE Huone SET tyyppi = ?, paivahinta = ? WHERE huonenumero = ?", huone.getTyyppi(), huone.getPaivahinta(), huone.getHuoneumero());
        return huone;
    }

    @Override
    public void delete(Integer key) throws SQLException {
        jdbcTemplate.update("DELETE FROM Huone WHERE huonenumero = ?;", key);
    }

    @Override
    public List<Huone> list() throws SQLException {
        return jdbcTemplate.query(
        "SELECT * FROM Huone;",(rs, rowNum) -> new Huone (rs.getInt("huonenumero"), rs.getString("tyyppi"), rs.getInt("paivahinta")));
    }
    
}
