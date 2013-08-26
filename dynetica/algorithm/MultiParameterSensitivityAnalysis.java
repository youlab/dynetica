
package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.objective.*;
import dynetica.system.*;
import java.util.*;

/**
 * 
 * @author lingchong
 */

public class MultiParameterSensitivityAnalysis implements Runnable {
    
    ArrayList<AbstractMetric> metricList; //Objectives to be evaluated 
    
    ArrayList xVariables; // variables to be changed, one by one. Here either parameters or substances can be used.
//    dynetica.util.DoubleList minValues; // stores the minimum values of the specified xVariables
//    dynetica.util.DoubleList maxValues; // stores the maximum values of xVariables
//    dynetica.util.DoubleList numPoints; // sotres the number of points to go through for each variable
    int numPoints = 20; // number of different values (within the specified range) for *each* parameter
    
    double simulationTime = 100.0;
    
//    boolean logScale = false;
    boolean plotOnComplete = false;
    
    boolean saveData = false;  // attempting to save time courses if true.    
    java.io.File output = null; // file for storing time course data if saveData is true.
    
    
    Thread simulationThread = null;
    
    ReactiveSystem system;
    Algorithm algorithm;
    
    double[][] xValues; // stores 
    double[][][] yValues; // stores the final values of different objective functions
    
    double[][][][] timeCourses; //stores the time courses of objective functions resulting from the different variable values
    
    
    
    public MultiParameterSensitivityAnalysis(ReactiveSystem sys) {
            system = sys;
            algorithm = system.getAlgorithm();
    }
    
    public MultiParameterSensitivityAnalysis (
            ReactiveSystem sys,
            java.io.File out) {    
            system = sys;
            output = out;
            
            algorithm = system.getAlgorithm();
            
            if (output != null) algorithm.setOutput(output);

    }
    
    
    public void run() {
       if (numPoints <= 0 || xVariables == null || metricList == null) {
            System.out.println("Error in parameters. NumSim = " + numPoints + " Variables = " + xVariables + " metricList = " + metricList);
            return;
        }
        
            int numVariables = xVariables.size();
            int numMetrics = metricList.size();
            int numTimePoints = algorithm.getIterations();
        
           yValues = new double[numVariables][numMetrics][numPoints + 1];
           xValues = new double[numVariables][numPoints + 1];
           timeCourses = new double[numVariables][numMetrics][numPoints + 1][numTimePoints];
        

        for (int j = 0; j < xVariables.size(); j ++) { //first layer scanning through all variables
            
            double currentValue;
            EntityVariable variable = (EntityVariable) (xVariables.get(j));
            double oldValue = variable.getValue();
            double currentMax = variable.getMax();
            double currentMin = variable.getMin();
            boolean logScale = variable.isLogScale();
                    
            for (int i = 0; i <= numPoints; i++) { // 2nd layer scanning through individual parameter values   
                if (simulationThread == null) {
                    System.out.println("Sensitivity analysis stopped at round " + i);
                    break;
                }
                
                if (!logScale) {
                    currentValue = i * (currentMax - currentMin)/numPoints + currentMin;
                }
                else {
                    currentValue = Math.exp(i * (Math.log(currentMax) - Math.log(currentMin))/numPoints + Math.log(currentMin));
                }
                
                xValues[j][i] = currentValue;
                
                
                //
                // Change the value or initial value of the correponding system variable (parameter or substance)
                //
                EntityVariable sysVariable = (EntityVariable) (system.getEntity(variable.getName()));
                if (sysVariable instanceof Parameter)
                   sysVariable.setValue(currentValue);
                
                else
                   ( (Substance) sysVariable).setInitialValue(currentValue);

                //
                // conduct a single round of simulation
                // 
                compute();

                for (int k = 0; k < metricList.size(); k++) {
                    yValues[j][k][i] = metricList.get(k).getValue();
                }

                //
                // Reset the value or initial value of the correponding system variable (parameter or substance)
                //                
                if (sysVariable instanceof Parameter)
                    sysVariable.setValue(oldValue);
                else
                    ( (Substance) sysVariable).setInitialValue(oldValue);

            }
        }
        
        if (plotOnComplete) {
//            dynetica.gui.SensitivityFigureWindow s = new dynetica.gui.SensitivityFigureWindow(this);
//            s.show();
        }
        simulationThread = null;
    }
    
    //
    // Computes one round of simulation
    //
    private void compute() {
        algorithm.reset();
        algorithm.setSamplingStep(simulationTime / algorithm.getIterations());
        int numTimePoints = algorithm.getIterations();
        for (int i = 0; i < numTimePoints; i++) {
            if (simulationThread == null) {
               break;
            }
            algorithm.update();
        }
        java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
     public void start() {
        if (simulationThread == null) {
            simulationThread = new Thread (this, " Simulation");
            simulationThread.start();
        }
    }
    
     public void pause() {
         simulationThread = null;
    }
     
    public ReactiveSystem getSystem() { return system;}
    public ArrayList getVariables() {return xVariables;}
    public ArrayList<AbstractMetric> getMetrics() {return metricList;}
    public double [][] getXValues() { return xValues;}
    public double [][][] getYValues() {return yValues;}
    public int getNumPoints() {return numPoints;}
    public double getTime() {return simulationTime;}
    public double [][][][] getTimeCourses() {return timeCourses;}
    
    public void setVariables(ArrayList v){
        xVariables = v;
    }
    
    public void addVariable(EntityVariable v) { 
        xVariables.add(v);
    }
    
    public void addMetric(AbstractMetric m){
        metricList.add(m);
    }
//    public void setMin(double d) { min = d; }
//    public void setMax(double d) { max = d; }
    public void setTime(double d) { simulationTime = d; }
    public void setNumPoints(int i) { numPoints = i; }
 //   public void setLogScale(boolean b) { logScale = b; }
    public void setMetrics(ArrayList<AbstractMetric> metrics) {metricList = metrics;}
    public void setPlotOnComplete(boolean b) {plotOnComplete = b;}

}
