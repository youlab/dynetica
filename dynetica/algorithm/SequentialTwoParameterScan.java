
package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.objective.*;
import dynetica.system.*;
import java.util.*;

/**
 * 
 * @author lingchong
 */

public class SequentialTwoParameterScan implements Runnable {
    
    ArrayList<AbstractMetric> metricList; //Objectives to be evaluated 
    
    EntityVariable variable1;
    EntityVariable variable2;
    
    int numPoints1 = 10;
    int numPoints2 = 10;
    
    double simulationTime = 100.0;
    
    boolean plotOnComplete = false;
    
    boolean saveData = false;  // attempting to save time courses if true.    
    java.io.File output = null; // file for storing time course data if saveData is true.
    
    
    Thread simulationThread = null;
    
    ReactiveSystem system;
    Algorithm algorithm;
    
    double [] x1Values; // stores
    double [] x2Values;
    
    double[][][] yValues; // stores the final values of different objective functions 
//    double[][][][] timeCourses; //stores the time courses of objective functions resulting from the different variable values
//    boolean storeTimeCourses = false;
   
    public SequentialTwoParameterScan(ReactiveSystem sys) {
            system = sys;
            algorithm = system.getAlgorithm();
    }
    
    public SequentialTwoParameterScan (
            ReactiveSystem sys,
            java.io.File out) {    
            system = sys;
            output = out;
            
            algorithm = system.getAlgorithm();
            
            if (output != null) algorithm.setOutput(output);

    }
    
    
    public void run() {
       if (numPoints1 <= 0 || numPoints2 <=0 || variable1 == null || variable2 == null || metricList == null) {
            System.out.println("Error in algorithm setup (sequential parameter scan). Aborting. ");
            return;
        }
        
            int numMetrics = metricList.size();
            
           yValues = null;
           x1Values = null;
           x2Values = null;
           EntityVariable sysVariable1 = (EntityVariable) (system.getEntity(variable1.getName()));
           EntityVariable sysVariable2 = (EntityVariable) (system.getEntity(variable2.getName()));
           yValues = new double [numMetrics][numPoints1][numPoints2];
          
           if (variable1.isLogScale())
               x1Values = dynetica.util.Numerics.logspace(variable1.getMin(), variable1.getMax(), numPoints1);
           else 
               x1Values = dynetica.util.Numerics.linspace(variable1.getMin(), variable1.getMax(), numPoints1);
           
           if (variable2.isLogScale())
               x2Values = dynetica.util.Numerics.logspace(variable2.getMin(), variable2.getMax(), numPoints2);
           else 
               x2Values = dynetica.util.Numerics.linspace(variable2.getMin(), variable2.getMax(), numPoints2);
        
           
//           timeCourses = new double[numVariables][numMetrics][numPoints + 1][numTimePoints];
        
        double oldValue1 = sysVariable1.getValue();
        double oldValue2 = sysVariable2.getValue();
        
        
           
        for (int j = 0; j < x1Values.length ; j ++) { //scanning through 1st variable
            if (simulationThread == null) {
                    System.out.println("Sensitivity analysis stopped at round ");
                    break;
            }
            
            double currentValue1 = x1Values[j];
            
            if (sysVariable1 instanceof Parameter) 
                sysVariable1.setValue(currentValue1);
            else 
                ((Substance) sysVariable1).setInitialValue(currentValue1);
                    
            for (int i = 0; i < x2Values.length; i++) { // 2nd layer scanning through individual parameter values   
                
                double currentValue2 = x2Values[i];
                //
                // Change the value or initial value of the correponding system variable (parameter or substance)
                //
              if (sysVariable2 instanceof Parameter) 
                  sysVariable2.setValue(currentValue2);
              else 
                  ((Substance) sysVariable2).setInitialValue(currentValue2);
 
                //
                // conduct a single round of simulation
                // 
                compute();

                for (int k = 0; k < metricList.size(); k++) {
                    yValues[k][j][i] = metricList.get(k).getValue();
                }

               }
   //
   // Reset the value or initial values of the correponding system variables (parameter or substance)
   //                
           if (sysVariable1 instanceof Parameter) 
               sysVariable1.setValue(oldValue1);
           else 
               ((Substance) sysVariable1).setInitialValue(oldValue1);
              
           if (sysVariable2 instanceof Parameter) 
                  sysVariable2.setValue(oldValue2);
           else 
                  ((Substance) sysVariable2).setInitialValue(oldValue2);
        }
        
        if (plotOnComplete) {
 //           dynetica.gui.plotting.MultiParameterSensitivityPlaneWindow s = 
 //                   new dynetica.gui.plotting.MultiParameterSensitivityPlaneWindow(this);
 //           s.show();
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
            simulationThread = new Thread (this, "Sequential Two Parameter Scan");
            simulationThread.start();
        }
    }
    
     public void pause() {
         simulationThread = null;
    }
     
    public ReactiveSystem getSystem() { return system;}
    public ArrayList<AbstractMetric> getMetrics() {return metricList;}
    public double [] getX1Values() { return x1Values;}
    public double [] getX2Values() { return x2Values;}
    public double [][][] getYValues() {return yValues;}
    public int getNumPoints1() {return numPoints1;}
    public int getNumPoints2() {return numPoints2;}
    public double getTime() {return simulationTime;}
 //   public double [][][][] getTimeCourses() {return timeCourses;}
    
    public void setVariable1(EntityVariable v1){
        variable1 = v1;
    }
    
    public EntityVariable getVariable1 (){
        return variable1;
    }
    
    public void setVariable2(EntityVariable v2){
        variable2 = v2;
    }
    
    public EntityVariable getVariable2 (){
        return variable2;
    }
    
    public void addMetric(AbstractMetric m){
        metricList.add(m);
    }
    public void setTime(double d) { simulationTime = d; }
    public void setNumPoints1(int i) { numPoints1 = i; }
    public void setNumPoints2(int i) { numPoints2 = i; }
    public void setMetrics(ArrayList<AbstractMetric> metrics) {metricList = metrics;}
    public void setPlotOnComplete(boolean b) {plotOnComplete = b;}

}
