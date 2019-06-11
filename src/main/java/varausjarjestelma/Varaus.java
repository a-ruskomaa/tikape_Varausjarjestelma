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
import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.ArrayList;

public class Varaus {
    private Integer varausnumero;
    private Asiakas asiakas;
    private LocalDate alkupvm;
    private LocalDate loppupvm;
    private List<Huone> huoneet;
    private List<Lisavaruste> lisavarusteet;
    private Double yhteishinta;

    public Varaus() {
    }
    
    public Varaus(Integer varausnumero, Asiakas asiakas, LocalDate alkupvm, LocalDate loppupvm, List<Huone> huoneet, List<Lisavaruste> lisavarusteet) {
        this.varausnumero = varausnumero;
        this.asiakas = asiakas;
        this.alkupvm = alkupvm;
        this.loppupvm = loppupvm;
        this.huoneet = huoneet;
        this.lisavarusteet = lisavarusteet;
    }    
    
    public Varaus(Integer varausnumero, Asiakas asiakas, LocalDate alkupvm, LocalDate loppupvm, List<Huone> huoneet) {
        this(varausnumero, asiakas, alkupvm, loppupvm, huoneet, new ArrayList<>());
    }

    
    public Varaus(Integer varausnumero, Asiakas asiakas, LocalDate alkupvm, LocalDate loppupvm) {
        this(varausnumero, asiakas, alkupvm, loppupvm, new ArrayList<>(), new ArrayList<>());
    }
    
    public Varaus(Integer varausnumero) {
        this.varausnumero = varausnumero;
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

    public LocalDate getAlkupvm() {
        return alkupvm;
    }

    public void setAlkupvm(LocalDate alkupvm) {
        this.alkupvm = alkupvm;
    }

    public LocalDate getLoppupvm() {
        return loppupvm;
    }

    public void setLoppupvm(LocalDate loppupvm) {
        this.loppupvm = loppupvm;
    }

    public List<Huone> getHuoneet() {
        return huoneet;
    }

    public void setHuoneet(List<Huone> huoneet) {
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
     
    public void addLisavaruste (Lisavaruste lisavaruste) {
        this.lisavarusteet.add(lisavaruste);
    }

    public Double getYhteishinta() {
        return yhteishinta;
    }

    public void setYhteishinta(Double yhteishinta) {
        this.yhteishinta = yhteishinta;
    }
    
    private Integer calculateDays(LocalDate alkupvm, LocalDate loppupvm) {
        return Period.between(alkupvm, loppupvm).getDays();
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder();
        str.append(asiakas.getNimi() + ", " + asiakas.getEmail() + ", " + alkupvm + ", " + loppupvm + ", ");
        int paivia = calculateDays(alkupvm, loppupvm);
        if (paivia == 1) {
            str.append("1 päivä, ");
        } else {
            str.append(paivia + " päivää, ");
        }
        if (lisavarusteet.size() == 1) {
            str.append("1 lisävaruste, ");
        } else {
            str.append(lisavarusteet.size() + " lisävarustetta, ");
        }
        if (huoneet.size() == 1) {
            str.append("1 huone.");
        } else {
            str.append(huoneet.size() + " huonetta.");
        }
        str.append(" Huoneet: \n");
        int total = 0;
        for (Huone huone : huoneet) {
            str.append("\t" + huone + "\n");
            total += huone.getPaivahinta() * paivia;
        }
        str.append("\tYhteensä: " + total + " euroa \n");
        return str.toString();
    }
}
