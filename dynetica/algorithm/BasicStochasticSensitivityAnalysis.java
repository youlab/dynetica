package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.system.*;

/**
 * This allows the user to carry out basic sensitivity analysis using a stochastic repeatedStochasticSimulations.
 * For a chosen independent variable (parameter or substance), multiple rounds of stochastic simulations will be
 * carried out and the results will be viewed in terms of time courses and histograms.
 * 
 * @author  Lingchong You
 * @version 0.1
 */
public class BasicStochasticSensitivityAnalysis implements Runnable{
    int numParaValues = 5;
    int roundsPerCycle = 10;
//    int iterations = 1000; // number of iterations for each round of stochastic simulation
    double time = 50;
    Thread simulationThread = null;
    
    /** Holds value of property parameter. */
    private EntityVariable variable = null;
    private java.util.List substances = null; // selected substances to be evaluated.
    
    /** Holds value of property system. */
    private ReactiveSystem system;
    
    /** Holds value of property logScale. */
    private boolean logScale = false;
    
    /** Holds value of property min. */
    private double min = 0.0;
    
    /** Holds value of property max. */
    private double max = 1.0;
    
    private double[] xValues;
    private RepeatedStochasticSimulations repeatedStochasticSimulations;
    
    /** 
     * Holds value of time courses 
     * Make sure timeCourses[0][0].length == system.getTimePoints().length
     * 
     * Usage:
     * timeCourses[substanceIndex][numbParaValues][roundsPerCycle][dataIndex]
     */
    private double [][][][] timeCourses;
    
    /** Creates new Sensitivity */
    public BasicStochasticSensitivityAnalysis() {
    }
    
    public BasicStochasticSensitivityAnalysis(ReactiveSystem system) {
        this.system = system;
        repeatedStochasticSimulations = new RepeatedStochasticSimulations (system);
        repeatedStochasticSimulations.setRounds(roundsPerCycle);
//        this.iterations = repeatedStochasticSimulations.getIterations();
    }
  
    /** Getter for property numParaValues.
     * @return Value of property numParaValues.
     */
    public int getNumParaValues() {
        return numParaValues;
    }
    
    /** Setter for property numParaValues.
     * @param numParaValues New value of property numParaValues.
     */
    public void setNumParaValues(int cycles) {
        this.numParaValues = cycles;
    }
    
    public int getRoundsPerCycle(){
        return roundsPerCycle;
    }
    
    public void setRoundsPerCycle(int n){
        roundsPerCycle = n;
        repeatedStochasticSimulations.setRounds(n);
    }
    
    public int getIterations(){
        return repeatedStochasticSimulations.getIterations();
    }
    /** Getter for property parameter.
     * @return Value of property parameter.
     */
    public EntityVariable getVariable() {
        return variable;
    }
    
    /** Setter for property parameter.
     * @param parameter New value of property parameter.
     */
    public void setVariable(EntityVariable variable) {
        this.variable = variable;
    }
    
    
    /** Getter for property substances.
     * @return Value of property substances.
     */
    public java.util.List getSubstances() {
        return substances;
    }
    
    
    /** Setter for property substances.
     * @param substances New value of property substances.
     */
    public void setSubstances(java.util.List substances) {
        this.substances = substances;
    }
    
    /** Getter for property time.
     * @return Value of property time.
     */
    public double getTime() {
        return time;
    }
    
    /** Setter for property time.
     * @param time New value of property time.
     */
    public void setTime(double time) {
        this.time = time;
        repeatedStochasticSimulations.setTime(time);
    }
    
    /** Getter for property system.
     * @return Value of property system.
     */
    public ReactiveSystem getSystem() {
        return system;
    }
    
    /** Setter for property system.
     * @param system New value of property system.
     */
    public void setSystem(ReactiveSystem system) {
        this.system = system;
    }
    
    public void run() {
         if (numParaValues > 0 && variable != null && substances != null) {
            System.out.println("Starting basic stochastic sensitivity analysis...");
            System.out.println("# selected substances:" + substances.size());
            System.out.println(variable.getName() + ":" + "min = " + min + ";" + "max = " + max); 
            System.out.println("# variable values:" + numParaValues);
            System.out.println("# rounds for each variable value:" + roundsPerCycle);

            xValues = new double[numParaValues+1];
            timeCourses = new double[substances.size()][numParaValues + 1][roundsPerCycle][repeatedStochasticSimulations.getIterations()];
           
            double currentValue;
            double oldValue = variable.getValue();
            for (int i = 0; i <= numParaValues; i++) {
                if (simulationThread == null) {
                    System.out.println("Sensitivity analysis stopped at round " + i);
                    break;
                }
                if (!logScale) {
                    currentValue = i * (max - min)/numParaValues + min;
                }
                else {
                    currentValue = Math.exp(i * (Math.log(max) - Math.log(min))/numParaValues + Math.log(min));
                }
                xValues[i] = currentValue;
                if (variable instanceof Parameter)
                   variable.setValue(currentValue);
                else
                   ( (Substance) variable).setInitialValue(currentValue);

                //
                // conduct repeated simulations for the given parameter value
                //
                System.out.println("Conducting repeated stochastic simulations for " 
                        + variable.getName() + " = " + currentValue);
                repeatedStochasticSimulations();
                
                for (int j = 0; j < substances.size(); j++) {
                    String name = ((Substance) (substances.get(j))).getName();
                    timeCourses [j][i] = repeatedStochasticSimulations.getTimeCourses(name);
                 }
            }
            
            if (variable instanceof Parameter)
                  variable.setValue(oldValue);
             else
                  ( (Substance) variable).setInitialValue(oldValue);
            simulationThread = null;
        }
    }
    
    private void repeatedStochasticSimulations() {
        if (simulationThread != null)
            repeatedStochasticSimulations.run(simulationThread);
    }
    
     public void start() {
        if (simulationThread == null) {
            simulationThread = new Thread (this, "Basic Stochastic Simulations");
            simulationThread.start();
        }
    }
    
     public void pause() {
         simulationThread = null;
    }
    
    /** Getter for property logScale.
     * @return Value of property logScale.
     */
    public boolean isLogScale() {
        return logScale;
    }
    
    /** Setter for property logScale.
     * @param logScale New value of property logScale.
     */
    public void setLogScale(boolean logScale) {
        this.logScale = logScale;
    }
    
    /** Getter for property min.
     * @return Value of property min.
     */
    public double getMin() {
        return min;
    }
    
    /** Setter for property min.
     * @param min New value of property min.
     */
    public void setMin(double min) {
        this.min = min;
    }
    
    /** Getter for property max.
     * @return Value of property max.
     */
    public double getMax() {
        return max;
    }
    
    /** Setter for property max.
     * @param max New value of property max.
     */
    public void setMax(double max) {
        this.max = max;
    }
    
    public double[] getXValues() {
        return xValues;
    }
    
    public double [][][][] getTimeCourses(){
        return timeCourses;
    }
    
    
    /**
     * Returns time courses for the i-th substance 
     * 
     * @return 
     */
    public double[][][] getTimeCourses(int i) {
        return timeCourses[i];
    }

    /**
     * Return the all time courses for the given substance, truncated between startTime and endTime.
     * @param name
     * @param startTime
     * @param endTime
     * @return 
     */
    public double [][][] getTruncatedTimeCourses(int index, double startTime, double endTime) {
        double [][][] fullTimeCourses = getTimeCourses(index);
        int startIndex = (int) (Math.round(startTime * getIterations()/time));
        int endIndex = (int) (Math.round(endTime * getIterations()/time));
        double [][][] truncatedTimeCourses = new double [numParaValues + 1][roundsPerCycle][endIndex-startIndex + 1];
       
        for (int k = 0; k < numParaValues + 1; k++)
           for (int j = 0; j < roundsPerCycle; j++)
                    System.arraycopy(fullTimeCourses [k][j], startIndex, 
                            truncatedTimeCourses [k][j], 0, 
                            endIndex - startIndex + 1);
        return truncatedTimeCourses;        
    }
    
    
    /**
     * Return all the data points for the given substance in a single vector, truncated between startTime and endTime
     * 
     * This is useful for later statistic analysis.
     * @param name
     * @param startTime
     * @param endTime
     * @return 
     */
    
    public double [][] getAggregatedDataPoints(int index, double startTime, double endTime) {
        double [][][] trucatedTimeCourses = getTruncatedTimeCourses(index, startTime, endTime);
        double [][] aggregatedData = new double[numParaValues + 1][roundsPerCycle * trucatedTimeCourses[0][0].length];
       
        for (int i = 0; i < numParaValues + 1; i++) 
           for (int j = 0; j < roundsPerCycle; j++)
              System.arraycopy(trucatedTimeCourses[i][j], 0, 
                      aggregatedData[i], j * trucatedTimeCourses[0][0].length, 
                      trucatedTimeCourses[0][0].length);
        
        return aggregatedData;        
    }
    
      /**
     * Returns the full set of time points.
     * @return 
     */
    public double[] getTimePoints() {
        return repeatedStochasticSimulations.getTimePoints();
    }
    
     public double[] getTruncatedTimePoints(double startTime, double endTime) {
        return repeatedStochasticSimulations.getTruncatedTimePoints(startTime, endTime);
    }

}
