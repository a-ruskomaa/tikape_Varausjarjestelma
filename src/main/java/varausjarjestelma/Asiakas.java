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
public class Asiakas {
    private Integer asiakasnumero;
    private String nimi;
    private String email;
    private String puhelin;
    private Varaus varaus;

    public Asiakas() {
    }

    public Asiakas(Integer asiakasnumero, String nimi, String email, String puhelin, Varaus varaus) {
        this.asiakasnumero = asiakasnumero;
        this.nimi = nimi;
        this.email = email;
        this.puhelin = puhelin;
        this.varaus = varaus;
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

    public Varaus getVaraus() {
        return varaus;
    }

    public void setVaraus(Varaus varaus) {
        this.varaus = varaus;
    }
    
    
}
