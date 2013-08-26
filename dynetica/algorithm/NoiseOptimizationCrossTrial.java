/*
 * NoiseOptimizationCrossTrial
 * Algorithm for optimizing noise in stochastic simulations.
 */
package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.system.*;
import dynetica.util.*;
import javax.swing.JOptionPane;

/**
 * @author Derek Eidum (June 2013)
 */
public class NoiseOptimizationCrossTrial implements Runnable {
    
    public static final double MUTATION_PROB = .5;
    public static final double SCALE_FACTOR = 25;
    public static final double STRICT_FACTOR = 25;
    
    ReactiveSystem system;
    Algorithm algorithm;
    Substance targetSubstance;
    boolean minimize = true;
    boolean SNR = true;
    boolean limitMean = false;
    Parameter [] searchParameters;
    int numRepetitions;  // Repetitions per iteration
    int numIterations;
    double startTime;
    int startIndex;
    double varTolerance;
    double originalMean;
    double meanTolerance;
    
    Thread simulationThread = null;
    
    public NoiseOptimizationCrossTrial(ReactiveSystem sys, Parameter[] p, Substance s, 
            double tol, double time, int reps, int iters) {
        system = sys;
        searchParameters = p;
        targetSubstance = s;
        varTolerance = tol;
        startTime = time;
        numRepetitions = reps;
        numIterations = iters;
    }
    
    public void run() {
        algorithm = system.getAlgorithm();
        if ((algorithm instanceof RungeKutta4) || (algorithm instanceof RungeKuttaFehlberg)){
            JOptionPane.showMessageDialog(system.editor(), "You must use a stochastic algorithm for noise optimization.");
            return;
        }
                
        // Initialization
        
        double[][] substanceValues = new double[numRepetitions][algorithm.getIterations()];
        double[][] timeValues = new double[numRepetitions][algorithm.getIterations()];

        for (int i = 0; i < numRepetitions; i++){
            compute();
            substanceValues[i] = targetSubstance.getValues();
            timeValues[i] = system.getTimer().getTimePoints();
        }
        startIndex = calculateStartIndex(timeValues);
        double[] average = new double[substanceValues[0].length];
        for (int i = 0; i < average.length; i++){
            average[i] = 0;
            for (int j = 0; j < numRepetitions; j++){
                average[i] += substanceValues[j][i];
            }
            average[i] = average[i]/numRepetitions;
        }

        double ssMean = Statistics.mean(average, startIndex, average.length-1);
        originalMean = ssMean;

        double [] variances = new double[average.length];
        for (int c = 0; c < variances.length; c++) {
            variances[c] = Statistics.var(getColumn(substanceValues, c));
        }
        double avgVariance = Statistics.mean(variances, startIndex, variances.length-1);
        
        double prevScore;
        if (SNR) {
            prevScore = ssMean/Math.sqrt(avgVariance);
        } else {
            prevScore = avgVariance;
        }
        
        int counter = 1;        
        while (true) {
            counter++;
            if ((!SNR) && (avgVariance < varTolerance)) {
                StringBuilder message = new StringBuilder("Noise Optimization Complete.\n");
                for (int i = 0; i < searchParameters.length; i++){
                    Parameter p = searchParameters[i];
                    message = message.append(p.getName() + " = " + p.getValue() + "\n");
                }
                JOptionPane.showMessageDialog(system.editor(), message.toString());
            }
            
            double[] changes = calculateChanges();
            for (int i = 0; i < searchParameters.length; i++){
                Parameter p = searchParameters[i];
                p.setSearchingValue(p.getValue() + changes[i]);
            }
            
            substanceValues = new double[numRepetitions][algorithm.getIterations()];
            timeValues = new double[numRepetitions][algorithm.getIterations()];
            
            for (int i = 0; i < numRepetitions; i++){
                compute();
                substanceValues[i] = targetSubstance.getValues();
                timeValues[i] = system.getTimer().getTimePoints();
            }
            startIndex = calculateStartIndex(timeValues);
            average = new double[substanceValues[0].length];
            for (int i = 0; i < average.length; i++){
                average[i] = 0;
                for (int j = 0; j < numRepetitions; j++){
                    average[i] += substanceValues[j][i];
                }
                average[i] = average[i]/numRepetitions;
            }
            
            ssMean = Statistics.mean(average, startIndex, average.length-1);
            /*
            double[][] difference = new double[numRepetitions][algorithm.getIterations()];
            for (int i = 0; i < average.length; i++){
                double avg = average[i];
                for (int j = 0; j < numRepetitions; j++){
                    difference[j][i] = substanceValues[j][i] - avg;
                }
            } */
            variances = new double[average.length];
            for (int c = 0; c < variances.length; c++) {
                variances[c] = Statistics.var(getColumn(substanceValues, c));
            }
            avgVariance = Statistics.mean(variances, startIndex, variances.length-1);    
            
            double score;
            if (SNR) {
                score = ssMean/Math.sqrt(avgVariance);
            } else {
                score = avgVariance;
            }
            
            System.out.println("Mean = " + ssMean + ", Var = " + avgVariance + ", Score = " + score);
            
            boolean acceptableMeanChange = true;
            if (limitMean) {
                if (ssMean > originalMean * (1 + meanTolerance/100)) {acceptableMeanChange = false;}
                if (ssMean < originalMean * (1 - meanTolerance/100)) {acceptableMeanChange = false;}
            }
            
            double improvement = calculateImprovement(score, prevScore);
            double improvementThreshold = ExpressionConstants.doubleNumber.nextGaussian()/STRICT_FACTOR;
            if (acceptableMeanChange && (improvement > improvementThreshold)) {               
                prevScore = score;
                continue;
            }
            if (!acceptableMeanChange) {System.out.print("Too much change in mean.  ");}
            System.out.println("Resetting to previous values.");
            
            // otherwise reset parameters and try again
            for (int i = 0; i < searchParameters.length; i++) {
                Parameter p = searchParameters[i];
                p.setSearchingValue(p.getValue() - changes[i]);
            }
            
            if (counter >= numIterations){
                //System.out.println("Failed to reach target value. Final value is " + value + ". \n");
                JOptionPane.showMessageDialog(system.editor(), "Maximum iterations reached.");
                break;
            } 
            
            
        }
    }
    
    
    private void compute() {
        algorithm.reset();
        int iterations = algorithm.getIterations();
        for (int i = 0; i < iterations; i++) {
            if (simulationThread == null) {
               break;
            }
            algorithm.update();
        }
        // java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    public void start() {
        if (simulationThread == null) {
            simulationThread = new Thread (this, " Noise Optimization");
            simulationThread.start();
        }
    }
    
    public void pause() {
        simulationThread = null;
    }
    
    public double[] getColumn(double[][] data, int colNum){
        double[] result = new double[data.length];
        for (int i = 0; i < data.length; i++) {
            result[i] = data[i][colNum];
        }
        return result;
    }
    
    public int calculateStartIndex(double[][] timeVals) {
        int idx = 0;
        for (int r = 0; r < timeVals.length; r++){
            for (int c = 0; c < timeVals[r].length; c++) {
                if (timeVals[r][c] > startTime){
                    if (c > idx) {idx = c;}
                    break;
                }
            }
        }
        return idx;
    }
    
    public double[] calculateChanges() {
        int c = 0;
        double [] changes = new double[searchParameters.length];
        while (c <= 0){
            for (int i = 0; i < searchParameters.length; i++){
                if (ExpressionConstants.doubleNumber.nextDouble() < MUTATION_PROB){
                    double value = searchParameters[i].getValue();
                    if (Math.abs(value) < .001) {
                        changes[i] = ExpressionConstants.doubleNumber.nextGaussian()/10;
                    } else {
                        changes[i] = value * Math.pow(10, ExpressionConstants.doubleNumber.nextGaussian()/SCALE_FACTOR);
                    }
                    c++ ;
                } else {
                    changes[i] = 0;
                }
            }
        }
        return changes;
    }
    
    public double calculateImprovement(double score, double prevScore) {
        if (minimize) {
            if (prevScore==0) {return -score;}
            return (prevScore - score)/prevScore;
        }
        if (prevScore==0) {return score;}
        return (score-prevScore)/prevScore;
    }
    
    public boolean getIsMinimizing() {return minimize;}
    public void setMinimizing(boolean m) {minimize = m;}
    
    public boolean getIsSNR() {return SNR;}
    public void setIsSNR(boolean s) {SNR = s;}
    
    public boolean getLimitingMean() {return limitMean;}
    public void setLimitingMean(boolean l) {limitMean = l;}
    
    public double getMeanTolerance() {return meanTolerance;}
    public void setMeanTolerance(double t) {meanTolerance = t;}
    
}
