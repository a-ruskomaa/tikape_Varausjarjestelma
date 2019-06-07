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
    private Integer huonenumero;
    private String tyyppi;
    private Double paivahinta;

    public Huone() {
    }

    public Huone(Integer huonenumero, String tyyppi, Double paivahinta) {
        this.huonenumero = huonenumero;
        this.tyyppi = tyyppi;
        this.paivahinta = paivahinta;
    }

    public Integer getHuoneumero() {
        return huonenumero;
    }

    public void setHuoneumero(Integer huonenumero) {
        this.huonenumero = huonenumero;
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

    @Override
    public String toString() {
        return tyyppi + ", " + huonenumero + ", " + paivahinta + " euroa";
    }
    
    
    
}
