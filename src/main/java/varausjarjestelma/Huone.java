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
public class Huone {
    private Integer numero;
    private String tyyppi;
    private Double paivahinta;

    public Huone() {
    }

    public Huone(Integer numero, String tyyppi, Double paivahinta) {
        this.numero = numero;
        this.tyyppi = tyyppi;
        this.paivahinta = paivahinta;
    }

    public Integer getNumero() {
        return numero;
    }

    public void setNumero(Integer numero) {
        this.numero = numero;
    }

    public String getTyyppi() {
        return tyyppi;
    }

    public void setTyyppi(String tyyppi) {
        this.tyyppi = tyyppi;
    }

    public Double getPaivahinta() {
        return paivahinta;
    }

    public void setPaivahinta(Double paivahinta) {
        this.paivahinta = paivahinta;
    }
    
    
    
}
