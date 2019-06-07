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
import java.util.List;
import java.util.ArrayList;

public class Varaus {
    private Integer varausnumero;
    private Asiakas asiakas;
    private String alkupvm;
    private String loppupvm;
    private List<Huone> huoneet;
    private List<Lisavaruste> lisavarusteet;
    private Double yhteishinta;

    public Varaus() {
    }
    
    public Varaus(Integer varausnumero, Asiakas asiakas, String alkupvm, String loppupvm, List<Huone> huoneet, List<Lisavaruste> lisavarusteet) {
        this.varausnumero = varausnumero;
        this.asiakas = asiakas;
        this.alkupvm = alkupvm;
        this.loppupvm = loppupvm;
        this.huoneet = huoneet;
        this.lisavarusteet = lisavarusteet;
    }    
    
    public Varaus(Integer varausnumero, Asiakas asiakas, String alkupvm, String loppupvm, List<Huone> huoneet) {
        this.varausnumero = varausnumero;
        this.asiakas = asiakas;
        this.alkupvm = alkupvm;
        this.loppupvm = loppupvm;
        this.huoneet = huoneet;
        this.lisavarusteet = new ArrayList<>();
    }

    
    public Varaus(Integer varausnumero, Asiakas asiakas, String alkupvm, String loppupvm) {
        this.varausnumero = varausnumero;
        this.asiakas = asiakas;
        this.alkupvm = alkupvm;
        this.loppupvm = loppupvm;
        this.huoneet = new ArrayList<>();
        this.lisavarusteet = new ArrayList<>();
    }


    public Integer getVarausnumero() {
        return varausnumero;
    }

    public void setVarausnumero(Integer varausnumero) {
        this.varausnumero = varausnumero;
    }

    public Asiakas getAsiakas() {
        return asiakas;
    }

    public void setAsiakas(Asiakas asiakas) {
        this.asiakas = asiakas;
    }

    public String getAlkupvm() {
        return alkupvm;
    }

    public void setAlkupvm(String alkupvm) {
        this.alkupvm = alkupvm;
    }

    public String getLoppupvm() {
        return loppupvm;
    }

    public void setLoppupvm(String loppupvm) {
        this.loppupvm = loppupvm;
    }

    public List<Huone> getHuoneet() {
        return huoneet;
    }

    public void setHuoneet(ArrayList<Huone> huoneet) {
        this.huoneet = huoneet;
    }
    
    public void addHuone (Huone huone) {
        this.huoneet.add(huone);
    }

    public List<Lisavaruste> getLisavarusteet() {
        return lisavarusteet;
    }

    public void setLisavarusteet(List<Lisavaruste> lisavarusteet) {
        this.lisavarusteet = lisavarusteet;
    }

    public Double getYhteishinta() {
        return yhteishinta;
    }

    public void setYhteishinta(Double yhteishinta) {
        this.yhteishinta = yhteishinta;
    }

    @Override
    public String toString() {
        return asiakas.getNimi() + ", " + asiakas.getEmail() + ", " + alkupvm + ", " + loppupvm + ", paivia_yhteensa" + ", lisavarusteita_yhteensa" + huoneet.size() + "huonetta. Huoneet: \n" + "\n";
    }
}
