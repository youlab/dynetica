/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.algorithm;

import dynetica.entity.Substance;

/**
 *
 * @author chrismurphy
 */
public abstract class DataFit {
    
    protected Substance independent;
    protected Substance dependent;
    protected double[] xResults;
    protected double[] yResults;

    DataFit(){
        
    }
    
    DataFit(Substance independent, Substance dependent){
        this.independent = independent;
        this.dependent = dependent;
    }
    
    public abstract void run();
 
    public abstract void createBestFitLine();
    
    public Substance getIndependent() {
        return independent;
    }

    public void setIndependent(Substance independent) {
        this.independent = independent;
    }

    public Substance getDependent() {
        return dependent;
    }

    public void setDependent(Substance dependent) {
        this.dependent = dependent;
    }

    public double[] getXresults() {
        return xResults;
    }

    public void setXresults(double[] xresults) {
        this.xResults = xresults;
    }

    public double[] getYresults() {
        return yResults;
    }

    public void setYresults(double[] yresults) {
        this.yResults = yresults;
    }
    
}
