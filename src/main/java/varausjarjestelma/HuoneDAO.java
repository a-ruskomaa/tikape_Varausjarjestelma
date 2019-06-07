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
/**
 *
 * @author aleks
 */
@Component
public class HuoneDAO implements DAO<Huone, Integer> {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public void create(Huone huone) throws SQLException {
        jdbcTemplate.update("INSERT INTO Huone (huone_no, tyyppi, paivahinta) VALUES (?,?,?)", huone.getNumero(), huone.getTyyppi(), huone.getPaivahinta());
    };

    @Override
    public Huone read(Integer key) throws SQLException {
        return null;
    };

    @Override
    public Huone update(Huone huone) throws SQLException {
        return null;
    };

    @Override
    public void delete(Integer key) throws SQLException {
        
    };

    @Override
    public List<Huone> list() throws SQLException {
        return null;
    };
    
}
