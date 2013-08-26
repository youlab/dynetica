package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.system.*;
import java.util.*;
import javax.swing.JOptionPane;


/**
 *
 * @author Derek Eidum (2013)
 */
public class ParameterSearchMultiSubstance implements Runnable {
    
    public static final double MUTATION_PROB = .5;
    public static final double SCALE_FACTOR  = 10;
    public static final double STRICT_FACTOR = 10;
    public static final double N = 2;
    
    ReactiveSystem system;
    Algorithm algorithm;
    
    Parameter [] searchParameters;
    ArrayList<TargetSubstance> targetSubstances;
    double targetTime;
    double threshold;
    int maxIterations;
    
    double targetTotal;
    
    Thread simulationThread = null;
    
    public ParameterSearchMultiSubstance(ReactiveSystem sys, Parameter[] p, 
            ArrayList<TargetSubstance> ts, double th, double time, int iter) {
        system = sys;
        searchParameters = p;
        targetSubstances = ts;
        threshold = th;
        targetTime = time;
        maxIterations = iter;
    }
    
    public void run() {
        normalizeWeights();
        compute();
        double error = 0;
        double prevError;
        targetTotal = 0;
        
        double[] finalVals = new double[targetSubstances.size()];
        for (int i = 0; i < finalVals.length; i++ ) {
            TargetSubstance ts = targetSubstances.get(i);
            double[] values = ts.getSubstance().getValues();
            finalVals[i] = values[values.length - 1];
            error += Math.abs((ts.getTarget() - finalVals[i])*ts.getWeight());
            targetTotal += ts.getTarget();
        }
        
        if (Math.abs(error) <= threshold){
            StringBuilder message = new StringBuilder("Parameter Fitting Complete. \n");
            for (int i = 0; i < searchParameters.length; i++ ) {
                Parameter p = searchParameters[i];
                message.append(p.getName() + " = " + p.getValue() + "\n");
            }
            JOptionPane.showMessageDialog(system.editor(), message.toString());
            return;
        }
        
        int counter = 1;
        int failCount = 0;
        while (true) {
            System.out.println("\n");
            prevError = error;
            counter++;
            double[] changes = calculateChanges(error);
                        
            for (int i = 0; i < searchParameters.length; i++) {
                Parameter p = searchParameters[i];
                p.setSearchingValue(p.getValue() + changes[i]);
            }
            compute();
            error = 0;
            for (int i = 0; i < finalVals.length; i++ ) {
                TargetSubstance ts = targetSubstances.get(i);
                double[] values = ts.getSubstance().getValues();
                finalVals[i] = values[values.length - 1];
                error += Math.abs((ts.getTarget() - finalVals[i])*ts.getWeight());
            }

            if (Math.abs(error) <= threshold){
                StringBuilder message = new StringBuilder("Parameter Fitting Complete. \n");
                for (int i = 0; i < searchParameters.length; i++ ) {
                    Parameter p = searchParameters[i];
                    message.append(p.getName() + " = " + p.getValue() + "\n");
                }
                JOptionPane.showMessageDialog(system.editor(), message.toString());
                return;
            }

            double improvement = (Math.abs(prevError) - Math.abs(error));
            double normalNum = ExpressionConstants.doubleNumber.nextGaussian();
            System.out.println("DEBUG: i = " + counter + "; error = " + error + 
                    "; prevError = " + prevError + "; improvement = " + 
                    improvement + "; needed = " + normalNum/STRICT_FACTOR);
            if (improvement > (normalNum / STRICT_FACTOR)) {
                failCount = 0;
                continue;
            }
            
            // Otherwise reset all parameters to previous value
            failCount++;
            error = prevError;
            for (int i = 0; i < searchParameters.length; i++) {
                Parameter p = searchParameters[i];
                p.setSearchingValue(p.getValue() - changes[i]);
            }
            System.out.print("Reset to ");
            for (Parameter a : searchParameters) {
                System.out.print(a.getName() + " = " + (float) a.getValue() + ", ");
            }
            
            if (failCount >= maxIterations/5){
                JOptionPane.showMessageDialog(system.editor(), "Failed to improve system on "
                        + maxIterations/5 + " consecutive trials.  Aborting.");
                break;
            }
                            
            if (counter >= maxIterations){
                //System.out.println("Failed to reach target value. Final value is " + value + ". \n");
                JOptionPane.showMessageDialog(system.editor(), "Maximum iterations reached.\n"
                    + "Consider modifying parameter bounds, max iteration count, or threshold value.");
                resetParameters();
                break;
            }            
        }
        
        
    }
    
    public double[] calculateChanges(double error) {
        int c = 0;
        double [] changes = new double[searchParameters.length];
        while (c <= 0){
            for (int i = 0; i < searchParameters.length; i++){
                if (ExpressionConstants.doubleNumber.nextDouble() < MUTATION_PROB){
                    double value = searchParameters[i].getValue();
                    if (Math.abs(value) < .0001) {
                        changes[i] = ExpressionConstants.doubleNumber.nextGaussian()/10;
                    } else {
                        double exp = ExpressionConstants.doubleNumber.nextGaussian()/SCALE_FACTOR * Math.pow(error, N);
                        changes[i] = value * Math.pow(10, exp);
                    }
                    c++ ;
                } else {
                    changes[i] = 0;
                }
            }
        }
        return changes;
    }    
    
    private void compute() {
        algorithm = system.getAlgorithm();
        algorithm.reset();
        algorithm.setSamplingStep(targetTime / algorithm.getIterations());
        int iterations = algorithm.getIterations();
        for (int i = 0; i < iterations; i++) {
            if (simulationThread == null) {
               break;
            }
            algorithm.update();
        }
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    public void start() {
        if (simulationThread == null) {
            simulationThread = new Thread (this, " Multi Substance Parameter Search");
            simulationThread.start();
        }
    }
    
    public void pause() {
        simulationThread = null;
    }
    
    public void resetParameters() {
        Object[] parameters = system.getParameters().toArray();
        for (int i = 0; i < parameters.length; i++){
            Parameter p = (Parameter) parameters[i];
            p.resetSearchingValue();
        }
    }
    
    public void normalizeWeights() {
        double totalWeight = 0;
        for (int i = 0; i < targetSubstances.size(); i++) {
            totalWeight += targetSubstances.get(i).getWeight();
        }
        for (int i = 0; i < targetSubstances.size(); i++) {
            TargetSubstance ts = targetSubstances.get(i);
            ts.setWeight(ts.getWeight()/totalWeight);
        }
    }
}
