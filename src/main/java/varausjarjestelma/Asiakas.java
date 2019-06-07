/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package varausjarjestelma;

import java.util.List;

/**
 *
 * @author aleks
 */
public class Asiakas {
    private Integer asiakasnumero;
    private String nimi;
    private String email;
    private String puhelin;
    private List<Varaus> varaukset;

    public Asiakas() {
    }

    public Asiakas(Integer asiakasnumero, String nimi, String email, String puhelin) {
        this.asiakasnumero = asiakasnumero;
        this.nimi = nimi;
        this.email = email;
        this.puhelin = puhelin;
    }

    public Integer getAsiakasnumero() {
        return asiakasnumero;
    }

    public void setAsiakasnumero(Integer asiakasnumero) {
        this.asiakasnumero = asiakasnumero;
    }

    public String getNimi() {
        return nimi;
    }

    public void setNimi(String nimi) {
        this.nimi = nimi;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPuhelin() {
        return puhelin;
    }

    public void setPuhelin(String puhelin) {
        this.puhelin = puhelin;
    }

    public List<Varaus> getVaraukset() {
        return varaukset;
    }

    public void setVaraukset(List<Varaus> varaukset) {
        this.varaukset = varaukset;
    }

    public void addVaraus(Varaus varaus) {
        this.varaukset.add(varaus);
    }

    @Override
    public String toString() {
        return "Asiakas{" + "asiakasnumero=" + asiakasnumero + ", nimi=" + nimi + ", email=" + email + ", puhelin=" + puhelin + ", varaukset=" + varaukset + '}';
    }
    
    
}
