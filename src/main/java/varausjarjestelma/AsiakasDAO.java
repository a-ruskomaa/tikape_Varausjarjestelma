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
public class AsiakasDAO implements DAO<Asiakas, Integer> {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public void create(Asiakas asiakas) throws SQLException {
        
    };

    @Override
    public Asiakas read(Integer key) throws SQLException {
        return null;
    };

    @Override
    public Asiakas update(Asiakas asiakas) throws SQLException {
        return null;
    };

    @Override
    public void delete(Integer key) throws SQLException {
        
    };

    @Override
    public List<Asiakas> list() throws SQLException {
        return null;
    };
    
}
