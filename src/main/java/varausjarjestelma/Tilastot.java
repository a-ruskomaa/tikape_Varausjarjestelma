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
public class Tilastot {

    @Autowired
    JdbcTemplate jdbcTemplate;

public static void suosituimmatLisavarusteet() {
    jdbcTemplate.query("SELECT ")
    
}    
}
