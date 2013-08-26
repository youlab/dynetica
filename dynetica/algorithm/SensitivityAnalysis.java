
package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.objective.*;
import dynetica.system.*;
import java.util.*;

/**
 *
 * @author Derek Eidum (2013)
 */
public class SensitivityAnalysis implements Runnable {
    
    ArrayList<AbstractMetric> metricList;
    Variable variable;
    double min = 0;
    double max = 1;
    int numSimulations = 10;
    double time = 100;
    boolean logScale = false;
    
    boolean plotOnComplete = false;
    
    Thread simulationThread = null;
    ReactiveSystem system;
    Algorithm algorithm;
    
    double[] xValues;
    double[][] yValues;
    
    public SensitivityAnalysis(ReactiveSystem sys) {
        system = sys;
    }
    
    public SensitivityAnalysis (Variable v, 
                                ArrayList<AbstractMetric> list,
                                ReactiveSystem sys,
                                double minimum,
                                double maximum,
                                int num,
                                double simTime,
                                boolean log) {
        variable = v;
        metricList = list;
        system = sys;
        min = minimum;
        max = maximum;
        numSimulations = num;
        time = simTime;
        logScale = log;
    }
    
                                
    
    public void run() {
        if (numSimulations <= 0 || variable == null || metricList == null) {
            System.out.println("Error in parameters. NumSim = " + numSimulations + " Variable = " + variable + " metricList = " + metricList);
            return;
        }
        yValues = new double[metricList.size()][numSimulations+1];
        xValues = new double[numSimulations+1];

        double currentValue;
        double oldValue = variable.getValue();
        for (int i = 0; i <= numSimulations; i++) {
            if (simulationThread == null) {
                System.out.println("Sensitivity analysis stopped at round " + i);
                break;
            }
            if (!logScale) {
                currentValue = i * (max - min)/numSimulations + min;
            }
            else {
                currentValue = Math.exp(i * (Math.log(max) - Math.log(min))/numSimulations + Math.log(min));
            }
            xValues[i] = currentValue;
            if (variable instanceof Parameter)
               variable.setValue(currentValue);
            else
               ( (Substance) variable).setInitialValue(currentValue);

            compute();
            
            for (int j = 0; j < metricList.size(); j++) {
                yValues[j][i] = metricList.get(j).getValue();
            }
            
            if (variable instanceof Parameter)
                  variable.setValue(oldValue);
             else
                  ( (Substance) variable).setInitialValue(oldValue);
            
        }
        if (plotOnComplete) {
            dynetica.gui.plotting.SensitivityPlaneWindow s = new dynetica.gui.plotting.SensitivityPlaneWindow(this);
            s.show();
        }
        simulationThread = null;
    }
    
    private void compute() {
        algorithm = system.getAlgorithm();
        algorithm.reset();
        algorithm.setSamplingStep(time / algorithm.getIterations());
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
            simulationThread = new Thread (this, " Simulation");
            simulationThread.start();
        }
    }
    
     public void pause() {
         simulationThread = null;
    }
     
    public ReactiveSystem getSystem() { return system;}
    public Variable getVariable() {return variable;}
    public ArrayList<AbstractMetric> getMetrics() {return metricList;}
    public double[] getXValues() { return xValues;}
    public double[][] getYValues() {return yValues;}
    public double getMin() {return min;}
    public double getMax() {return max;}
    public int getNumSimulations() {return numSimulations;}
    public double getTime() {return time;}
    public boolean isLogScale() {return logScale;}
    
    public void setVariable(Variable v) { variable = v;}
    public void setMin(double d) { min = d; }
    public void setMax(double d) { max = d; }
    public void setTime(double d) { time = d; }
    public void setNumSimulations(int i) { numSimulations = i; }
    public void setLogScale(boolean b) { logScale = b; }
    public void setMetrics(ArrayList<AbstractMetric> metrics) {metricList = metrics;}
    public void setPlotOnComplete(boolean b) {plotOnComplete = b;}

}
