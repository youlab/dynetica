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
public class HillEquationFit extends DataFit{
    
    private double maxRate;
    
    protected double[] sResults;
    protected double[] pResults;
    private double[] bestFitPValues;
    
    private double hillCoefficient;
    private double Km;
    
    public HillEquationFit(){
        super();
    }
    
    public HillEquationFit(Substance independent, Substance dependent){
        super(independent, dependent);
    }
    
    public void setMaxRate(double max){
        maxRate = max;
    }
    
    public double getMaxRate(){
        return maxRate;
    }

    public void run(){
        
        double[] xValues = independent.getValues();
        xResults = new double[xValues.length];
        double[] yValues = dependent.getRates();
        yResults = new double[xValues.length];
        
        SimpleRegression model = new SimpleRegression(true);
        
        for(int i=0;i<xValues.length;i++){
            double v = yValues[i]/maxRate;
            if(v==0){
                sResults[i] = log(xValues[i]);
                pResults[i] = 0;
                continue;
            }
            sResults[i] = log(xValues[i]);
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
        double[] sValues = dependent.getValues();
        bestFitPValues = new double[sValues.length];
        for(int i=0;i<sValues.length;i++){
            bestFitPValues[i] = pow(sResults[i],hillCoefficient)/(pow(sResults[i],hillCoefficient) + pow(Km,hillCoefficient));
        }
    }
    
    public double[] getBestFitLine(){
        return bestFitPValues;
    }
    
}
