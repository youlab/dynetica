/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.objective.AbstractMetric;
import dynetica.system.*;
import static java.lang.Math.exp;
import static java.lang.Math.log;
import java.util.ArrayList;
import java.util.HashMap;
import org.apache.commons.math3.stat.regression.SimpleRegression;
/**
 *
 * @author chrismurphy
 */
public class DataFit {
    
     /** Holds value of property system. */
    private ReactiveSystem system;

    private String dependent;
    private String independent;
    
    protected double[] yValues;
    protected double[] xValues;
    
    HashMap<String, Double> fitParameters = new HashMap<String, Double>();
    
    public DataFit(ReactiveSystem system) {
        this.system = system;
    }
    
    public String getdependent(){
        return dependent;
    }
    public String getindependent(){
        return independent;
    }
    
    public double[] getXResults(){
        return this.xValues;
    }
    public double[] getYResults(){
        return this.yValues;
    }
    public HashMap<String, Double> getFitParameters(){
        return this.fitParameters;
    }
    
        /**
     * Getter for property system.
     * 
     * @return Value of property system.
     */
    public ReactiveSystem getSystem() {
        return system;
    }
    
    public void setDependent(String d){
        dependent = d;
    }
    
    public void setIndependent(String i){
        independent = i;
    }
    
    public void runPowerFit(){
        double[] xvals;
        double[] yvals;
        if(independent=="Time")
            xvals = system.getTimer().getTimePoints();
        else{
            if(independent.indexOf("Rate of") < 0){
            xvals = system.getSubstance(independent).getValues();
            }else{
                xvals = system.getSubstance(independent.split("\\s+")[independent.split("\\s+").length-1]).getRates();
            }
        }
        
        if(dependent.indexOf("Rate of") < 0){
            yvals = system.getSubstance(dependent).getValues();
        }else{
            yvals = system.getSubstance(dependent.split("\\s+")[dependent.split("\\s+").length-1]).getRates();
        }
        
        
    }
    
    public void runExponentialFit(){
        //want to fit equation y = alpha*e^(Beta*x)
        
        double[] xvals;
        double[] yvals;
        if(independent=="Time")
            xvals = system.getTimer().getTimePoints();
        else{
            if(independent.indexOf("Rate of") < 0){
                xvals = system.getSubstance(independent).getValues();
            }else{
                xvals = system.getSubstance(independent.split("\\s+")[independent.split("\\s+").length-1]).getRates();
            }
        }
        
        if(dependent.indexOf("Rate of") < 0){
            yvals = system.getSubstance(dependent).getValues();
        }else{
            yvals = system.getSubstance(dependent.split("\\s+")[dependent.split("\\s+").length-1]).getRates();
        }
        
        SimpleRegression model = new SimpleRegression(true);
        double x,y;
        //need to use arraylist since we may not be using all indexed values in xvals
        ArrayList<Double> xValuesList = new ArrayList<Double>();
        for(int i=0;i<xvals.length;i++){
            if(yvals[i]==0){
                continue;
            }else{
                y = log(yvals[i]);
                model.addData(xvals[i], y);
                xValuesList.add(xvals[i]);
            }
        }
        
        
        double alpha = exp(model.getIntercept());
        double Beta = exp(model.getSlope());
        
        fitParameters.put("alpha", alpha);
        fitParameters.put("Beta", Beta);
        fitParameters.put("r-squared",model.getRSquare());
        
        // now create best fit line
        xValues = new double[xValuesList.size()];
        yValues = new double[xValuesList.size()];
        for(int i=0;i<xValuesList.size();i++){
            xValues[i] = xValuesList.get(i);
            yValues[i] = alpha*exp(Beta*xValuesList.get(i));
        }

    }
    
    public void runHillFit(){
        double[] xvals;
        double[] yvals;
        if(independent=="Time")
            xvals = system.getTimer().getTimePoints();
        else{
            if(independent.indexOf("Rate of") < 0){
                xvals = system.getSubstance(independent).getValues();
            }else{
                xvals = system.getSubstance(independent.split("\\s+")[independent.split("\\s+").length-1]).getRates();
            }
        }
        
        if(dependent.indexOf("Rate of") < 0){
            yvals = system.getSubstance(dependent).getValues();
        }else{
            yvals = system.getSubstance(dependent.split("\\s+")[dependent.split("\\s+").length-1]).getRates();
        }
        
        double maxRate=0;
        for(int i=0;i<yvals.length;i++){
            if(yvals[i]>maxRate)
                maxRate = yvals[i];
        }
        
        SimpleRegression model = new SimpleRegression(true);
        double x,y;
        //need to use arraylist since we may not be using all indexed values in xvals
        ArrayList<Double> xValuesList = new ArrayList<Double>();
        for(int i=0;i<xvals.length;i++){
            double v = yvals[i]/maxRate;
            if(v==0){
                //dont include this x and y
                continue;
            }else{
                x = log(xvals[i]);
                y = log(v/(1-v));
                model.addData(x, y);
                //add x to xResults
                xValuesList.add(xvals[i]);
            }
        }

        double hillCoefficient = model.getSlope();
        double Km = exp(-(model.getIntercept())/model.getSlope());
        System.out.println(Km);
        System.out.println(hillCoefficient);
        
        //add parameters of best fit to hashmap, including r-squared value
        fitParameters.put("n", hillCoefficient);
        fitParameters.put("Km", Km);
        fitParameters.put("r-squared",model.getRSquare());
        
        // now create best fit line
        xValues = new double[xValuesList.size()];
        yValues = new double[xValuesList.size()];
        for(int i=0;i<xValuesList.size();i++){
            xValues[i] = xValuesList.get(i);
            yValues[i] = Math.pow(xValues[i],hillCoefficient)/(Math.pow(xValues[i],hillCoefficient) + Math.pow(Km,hillCoefficient));
        }
        
        
    }
    
    public void runQuadraticFit(){
        double[] xvals;
        double[] yvals;
        if(independent=="Time")
            xvals = system.getTimer().getTimePoints();
        else{
            if(independent.indexOf("Rate of") < 0){
                xvals = system.getSubstance(independent).getValues();
            }else{
                xvals = system.getSubstance(independent.split("\\s+")[independent.split("\\s+").length-1]).getRates();
            }
        }
        
        if(dependent.indexOf("Rate of") < 0){
            yvals = system.getSubstance(dependent).getValues();
        }else{
            yvals = system.getSubstance(dependent.split("\\s+")[dependent.split("\\s+").length-1]).getRates();
        }
        
    }
}
