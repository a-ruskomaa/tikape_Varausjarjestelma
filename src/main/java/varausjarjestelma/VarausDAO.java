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
public class VarausDAO implements DAO<Varaus, Integer> {
    
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Override
    public void create(Varaus varaus) throws SQLException {
        
    };

    @Override
    public Varaus read(Integer key) throws SQLException {
        return null;
    };

    @Override
    public Varaus update(Varaus varaus) throws SQLException {
        return null;
    };

    @Override
    public void delete(Integer key) throws SQLException {
        
    };

    @Override
    public List<Varaus> list() throws SQLException {
        return null;
    };
    
}
