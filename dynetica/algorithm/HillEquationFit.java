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
import static java.lang.Math.pow;
import org.apache.commons.math3.stat.regression.SimpleRegression;

/**
 *
 * @author chrismurphy
 */
public class HillEquationFit {
   
    private Substance substance;
    private Substance product;
    
    private double maxRate;
    
    protected double[] sResults;
    protected double[] pResults;
    private double[] bestFitPValues;
    
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
    
    public void setProduct(Substance p){
        this.product = p;
    }
    
    public Substance getProduct(){
        return product;
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
        double[] pValues = product.getRates();
        pResults = new double[sValues.length];
        
        SimpleRegression model = new SimpleRegression(true);
        
        for(int i=0;i<sValues.length;i++){
            double v = pValues[i]/maxRate;
            if(v==0)
                continue;
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
    
    public void createBestFitLine(){
        double[] sValues = substance.getValues();
        bestFitPValues = new double[sValues.length];
        for(int i=0;i<sValues.length;i++){
            bestFitPValues[i] = pow(sResults[i],hillCoefficient)/(pow(sResults[i],hillCoefficient) + pow(Km,hillCoefficient));
        }
    }
    
    public double[] getBestFitLine(){
        return bestFitPValues;
    }
    
}
