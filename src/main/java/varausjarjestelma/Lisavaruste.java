/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

/**
 *
 * @author aleks
 */
public class Lisavaruste {
    private String nimi;
    private Integer varuste_id;

    public Lisavaruste() {
    }

    public Lisavaruste(String nimi) {
        this.nimi = nimi;
        this.varuste_id = -1;
    }

    public Lisavaruste(String nimi, Integer varuste_id) {
        this.nimi = nimi;
        this.varuste_id = varuste_id;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }
    
    public Integer getVaruste_id() {
        return varuste_id;
    }

    public void setVaruste_id(Integer varuste_id) {
        this.varuste_id = varuste_id;
    }

    @Override
    public String toString() {
        return this.getNimi(); //To change body of generated methods, choose Tools | Templates.
    }

}
