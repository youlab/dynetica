
package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.system.*;

/**
 * This class is used as an intermediate layer to facilitate analysis of stochastic simulations.
 * 
 * It will allow the user to conduct multiple rounds of stochastic simulations using the SAME parameter set.
 * The user can specify the total number of simulation rounds and the time window within which time course data
 * will be stored for downstream analysis.
 * 
 * It should be used on Stochastic algorithms or deterministic algorithms on models containing random parameters.
 * 
 * @author lingchong
 */
public class RepeatedStochasticSimulations implements Runnable{
    int rounds = 10;
    double time = 100.0;  // the end time point of each simulation.
    
    Thread simulationThread = null;
    
    java.util.List substances = null; 
    
    /** Holds value of property system. */
    private ReactiveSystem system;
    
    
    
    /** 
     * Holds value of time courses 
     * Make sure timeCourses[0][0].length == system.getTimePoints().length
     * 
     * Usage:
     * timeCourses[substanceIndex][cycleIndex][dataIndex]
     */
    private double [][][] timeCourses;
    
    private double [] timePoints;
    
    /**
     * iterations for each round of simulation.
     */
    private int iterations; 

    private Algorithm algorithm;
    
    /**
     * Constructor to generate an instance with a system. 
     * The user should always construct the algorithm for a specific system.
     */
    public RepeatedStochasticSimulations(ReactiveSystem system) {
        this.system = system;
        //
        // Assuming a stochastic simulation. But one can use typically deterministic simulation algorithms,
        // as the parameter values can be random numbers.
        //
        algorithm = system.getAlgorithm();
        substances = system.getSubstances();
        
         //
        //override the iterations if endTime != currentIterations * SamplingStep
        //
        iterations = Math.max(1, (int) Math.round(time/algorithm.getSamplingStep()));
        algorithm.setIterations(iterations);
     }
 
    /** Getter for property rounds.
     * @return Value of property rounds.
     */
    public int getRounds() {
        return rounds;
    }
    
    /** Setter for property rounds.
     * @param rounds New value of property rounds.
     */
    public void setRounds(int Rounds) {
        this.rounds = Rounds;
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
        //
        //override the iterations if endTime != currentIterations * SamplingStep
        //
        iterations = Math.max(1, (int) Math.round(time/algorithm.getSamplingStep()));
        algorithm.setIterations(iterations);
        
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
    
    public void run (Thread thread){
        this.simulationThread = thread;
        run();
    }
    public void run() {
        timeCourses = new double[substances.size()][rounds][iterations];
        for (int i = 0; i < rounds; i++) {
           if (simulationThread == null) {
                    System.out.println("Sensitivity analysis stopped at round " + i);
                    break;
           }
            //
            // conduct one round of simulation
            //
            compute();
          
            //
            // Store data
            //
           for (int j = 0; j < substances.size(); j++) {
                double [] values = ((Substance)(substances.get(j))).getValues();
                    timeCourses [j][i] = values;
                }
         }
         
         timePoints = system.getTimer().getTimePoints();
         //
         // delete the thread when all simulations are complete.
         simulationThread = null;
    }
    
    
    /** 
     * conducts one round of simulation
     */
    private void compute() {
        algorithm.reset();
 //       int iterations = algorithm.getIterations();
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
            simulationThread = new Thread (this, " Repeated Stochastic Simulations");
            simulationThread.start();
        }
    }
    
     public void pause() {
         simulationThread = null;
    }
    
    
    /**
     * Returns time courses of the i-th substance (as ordered in the list of substances in the system).
     * @param index
     * @return 
     */
    public double[][] getTimeCourses(int i) {
        return timeCourses[i];
    }

    /**
     * Returns time courses for the substance with name "name".
     * 
     * @return 
     */
    public double[][] getTimeCourses(String name) {
        return timeCourses[substances.indexOf((Substance)(system.get(name)))];
    }
   
    
    /**
     * Return the all time courses for the given substance, truncated between startTime and endTime.
     * @param name
     * @param startTime
     * @param endTime
     * @return 
     */
    public double[][] getTruncatedTimeCourses(String name, double startTime, double endTime) {
        double [][] fullTimeCourses = getTimeCourses(name);
        int startIndex = (int) (Math.round(startTime * algorithm.getIterations()/time));
        int endIndex = (int) (Math.round(endTime * algorithm.getIterations()/time));
        double [][] truncatedTimeCourses = new double[rounds][endIndex-startIndex + 1];
       
        for (int i = startIndex; i <= endIndex; i++) 
            for (int j = 0; j < rounds; j++)
                truncatedTimeCourses[j][i - startIndex] = fullTimeCourses[j][i];
        
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
    
    public double[] getAggregatedDataPoints(String name, double startTime, double endTime) {
        double [][] fullTimeCourses = getTimeCourses(name);
        int startIndex = (int) (Math.round(startTime * algorithm.getIterations()/time));
        int endIndex = (int) (Math.round(endTime * algorithm.getIterations()/time));
        double [] truncatedTimeCourses = new double[rounds * (endIndex-startIndex + 1)];
       
        for (int i = startIndex; i <= endIndex; i++) 
            for (int j = 0; j < rounds; j++)
                truncatedTimeCourses[(i - startIndex) * rounds + j] = fullTimeCourses[j][i];
        
        return truncatedTimeCourses;        
    }
    
    /**
     * Return the final values for each substance from the multiple rounds of simulation.
     * 
     * @param name
     * @return 
     */
    
    public double[] getFinalValues(String name){
        double [][] fullTimeCourses = getTimeCourses(name);
        double [] finalValues = new double[rounds];
        int finalIndex = fullTimeCourses[0].length - 1 ;
        for (int j = 0; j < rounds; j++)
            finalValues[j] = fullTimeCourses[j][finalIndex];
        return finalValues;
    }
    
    /**
     * Return the time points truncated between startTime and endTime.
     * 
     * @param startTime
     * @param endTime
     * @return 
     */
    
    public double[] getTruncatedTimePoints(double startTime, double endTime) {
        int startIndex = (int) (Math.round(startTime * algorithm.getIterations()/time));
        int endIndex = (int) (Math.round(endTime * algorithm.getIterations()/time));
        double [] truncatedTimePoints = new double[endIndex-startIndex + 1];
        for (int i = startIndex; i <= endIndex; i++)
            truncatedTimePoints[i-startIndex] = timePoints[i];
        return truncatedTimePoints;
    }
    
    /**
     * Returns the full set of time points.
     * @return 
     */
    public double[] getTimePoints() {
        return timePoints;
    }
    
    /**
     * Returns the full set of time courses. 
     * @return 
     */
    public double[][][] getTimeCourses(){
        return timeCourses;
    }
    
    public int getIterations(){
        return iterations;
    }
}
