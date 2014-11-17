/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.algorithm;

import dynetica.entity.Substance;
import dynetica.system.ReactiveSystem;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author chrismurphy
 */
public class HillEquationFit {
   
    private Substance substance;
    private Substance productRate;
    private double maxRate;
    
    protected double[] sResults;
    protected double[] pResults;
    
    private double hillCoefficient;
    private double Km;
    
    public HillEquationFit(){
    }
    
    public void setSubstance(Substance s){
        this.substance = s;
    }
    
    public Substance getSubstance(){
        return substance;
    }
    
    public void setProductRate(Substance p){
        this.productRate = p;
    }
    
    public Substance getProductRate(){
        return productRate;
    }
    
    public void setMaxRate(double max){
        maxRate = max;
    }
    
    public double getMaxRate(){
        return maxRate;
    }

    public void run(){
        
        double[] sValues = substance.getValues();
        sResults = new double[sValues.length];
        double[] pValues = productRate.getRates();
        pResults = new double[sValues.length];
        
        SimpleRegression model = new SimpleRegression(true);
        
        for(int i=0;i<sValues.length;i++){
            double v = pValues[i]/maxRate;
            sResults[i] = log(sValues[i]);
            pResults[i] = log(v/(1-v));
            model.addData(sResults[i], pResults[i]);
        }
        
        hillCoefficient = model.getSlope();
        Km = exp(-(model.getIntercept())/model.getSlope());
    }
    
    public double getKm(){
        return Km;
    }
    
    public double getHillCoefficient(){
        return hillCoefficient;
    }
    
}
