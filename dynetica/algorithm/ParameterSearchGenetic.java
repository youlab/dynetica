
package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.system.*;
import dynetica.util.Numerics;
import java.util.*;
import javax.swing.JOptionPane;

/**
 *
 * @author Derek Eidum
 */
public class ParameterSearchGenetic implements Runnable {
    
    static boolean DEBUG = true;
    
    double cullFraction = 0.5;
    double scoreWeightExponent = 2;
    double invaderProbability = 0.02;
    double mutationProbability = 0.1;
    
    public ReactiveSystem system;
    Algorithm algorithm;
    Parameter [] searchParameters;
    ArrayList<TargetObjective> objectives;
    ArrayList<ParameterVector> population;
    
    double [] originalParameterValues;
    
    Thread simulationThread = null;
    
    double threshold;
    double simulationTime;
    int numGenerations;
    int vectorPopulationSize;
    boolean randomInitialPopulation = false;
    
    public ParameterSearchGenetic(ReactiveSystem s, Parameter[] p, ArrayList<TargetObjective> to,
                double time, double th, int g, int pop, boolean r) {
        system = s;
        algorithm = s.getAlgorithm();
        searchParameters = p;
        objectives = to;
        simulationTime = time;
        threshold = th;
        numGenerations = g;
        vectorPopulationSize = pop;
        randomInitialPopulation = r;
        
        originalParameterValues = new double[searchParameters.length];
        for (int i = 0; i < searchParameters.length; i++) {
            Parameter par = searchParameters[i];
            originalParameterValues[i] = par.getValue(); 
        }
    }
    
    public void run() {
        population = new ArrayList<ParameterVector>();
        
        // The population should always include the original parameter vector
        double[] vals = new double[searchParameters.length];
        for (int i = 0; i < searchParameters.length; i++) {
            vals[i] = searchParameters[i].getValue();
        }
        ParameterVector orig = new ParameterVector(searchParameters,vals);
        population.add(orig);
        debug(orig,"Original");
        
        // Set up the rest of the population
        if (randomInitialPopulation) {
            setUpPopulationRandomly();
        } else {
            setUpPopulationUsingPerturbations();
        }
        
        ParameterVector alphaDog;
        for (int generation = 0; generation <= numGenerations; generation++){
            scorePopulation();
            Collections.sort(population);

            ArrayList<ParameterVector> nextGen = new ArrayList<ParameterVector>(population.size());
            int numSurvivors = (int) (population.size() * (1 - cullFraction));

            alphaDog = population.get(0);
            debug(alphaDog,"AlphaDog (round " + generation + ")");
            // Check end conditions
            if (generation==numGenerations){
                String message = "Maximum generations reached.\nBest results shown below.\n" + stringResult(alphaDog);
                JOptionPane.showMessageDialog(null, message);
                setParameters(alphaDog);
                return;
            }
            if (alphaDog.getScore() > 100*(1 - threshold)) {
                String message = stringResult(alphaDog);
                setParameters(alphaDog);
                JOptionPane.showMessageDialog(null, "Parameter search complete.\n"+message);
                return;
            }
            
            // Always keep the most fit vector (the alpha dog)
            nextGen.add(alphaDog);
            population.remove(0);

            // Randomly select the rest of the population by score^n
            ArrayList<Double> weights = new ArrayList<Double>(population.size());
            debug("Looping through population to get weights");
            for (int i = 0; i < population.size(); i++){
                double score = population.get(i).getScore();
                weights.add(Math.pow(score/100, scoreWeightExponent));
            }
            double totalWeight = Numerics.sum(weights);
            for (int i = 1; i < numSurvivors; i++) {
                int index = weightedRandomSelection(weights, totalWeight);
                nextGen.add(population.remove(index));
                totalWeight -= weights.remove(index);
            }
            
            // Rebuild the population via random mating (and possibly an invader)
            ArrayList<ParameterVector> children = new ArrayList<ParameterVector>();
            for (int i = 0; i < (vectorPopulationSize - numSurvivors); i++){
                if (rand() < invaderProbability) {
                    children.add(generateRandomInvader());
                    continue;
                }
                children.add(mateVectors(nextGen));
            }
            nextGen.addAll(children);
            population = nextGen;
        }
    }
    
    public void start() {
        if (simulationThread == null) {
            simulationThread = new Thread (this, " Parameter Search");
            simulationThread.start();
        }
    }
    
    public void pause() {
        simulationThread = null;
    }
    
    private void compute() {
        algorithm = system.getAlgorithm();
        algorithm.reset();
        algorithm.setSamplingStep(simulationTime / algorithm.getIterations());
        int iterations = algorithm.getIterations();
        for (int i = 0; i < iterations; i++) {
            if (simulationThread == null) {
               break;
            }
            algorithm.update();
        }
        //java.awt.Toolkit.getDefaultToolkit().beep();
    }
    
    public void setUpPopulationRandomly() {
        for (int i = 1; i < vectorPopulationSize; i++) {
            population.add(generateRandomInvader());            
        }
    }
    
    public void setUpPopulationUsingPerturbations() {
        for (int i = 1; i < vectorPopulationSize; i++) {
            double[] values = new double[searchParameters.length];
            for (int j = 0; j < searchParameters.length; j++) {
                Parameter p = searchParameters[j];
                if (p.getValue() == 0) {
                    values[j] = randN();
                } else {
                values[j] = p.getValue() * Math.pow(10, randN());
                }
                if (values[j] > p.getMax()) {values[j] = p.getMax();}
                if (values[j] < p.getMin()) {values[j] = p.getMin();}
            }
            population.add(new ParameterVector(searchParameters, values));            
        }
    }
    
    public ParameterVector generateRandomInvader() {
        double[] values = new double[searchParameters.length];
        for (int j = 0; j < searchParameters.length; j++) {
            Parameter p = searchParameters[j];
            values[j] = p.getMin() + rand() * (p.getMax() - p.getMin());
        }
       return new ParameterVector(searchParameters, values); 
    }
    
    public void scorePopulation() {
        for (ParameterVector pv : population) {
            if (pv.getScore() != null) {continue;}
            setParameters(pv);
            compute();
            double score = 0;
            for (TargetObjective obj : objectives){
                score += obj.getScore() * obj.getWeight();
            }
            pv.setScore(score);
        }
    }
    
    public void setParameters(ParameterVector pv) {
        for (int i = 0; i < searchParameters.length; i++) {
            searchParameters[i].setSearchingValue(pv.getValue(i));
        }
    }
    
    public double rand() {
        return ExpressionConstants.doubleNumber.nextDouble();
    }
    
    public double randN() {
        return ExpressionConstants.doubleNumber.nextGaussian();
    }

    private int weightedRandomSelection(ArrayList<Double> weights, double totalWeight) {
        double r = rand() * totalWeight;
        for (int i = 0; i < weights.size(); i++){
            if (r < weights.get(i)) {return i;}
            r = r - weights.get(i);
        }
        return 0;
    }

    private ParameterVector mateVectors(ArrayList<ParameterVector> parents) {
        // Choose two (different) parents based on weight scores
        ArrayList<Double> weights = new ArrayList<Double>(parents.size());
        for (int i = 0; i < parents.size(); i++) {
            weights.add(parents.get(i).getScore());
        }
        int a = weightedRandomSelection(weights, Numerics.sum(weights));
        int b = a;
        while (b==a) {
            b = weightedRandomSelection(weights, Numerics.sum(weights));
        }
        ParameterVector mother = parents.get(a);
        ParameterVector father = parents.get(b);
        
        double[] childVals = new double[searchParameters.length];
        for (int i = 0; i < childVals.length; i++){
            double r = rand();
            childVals[i] = (r*mother.getValue(i)) + ((1-r)*father.getValue(i));
            if (rand() < mutationProbability){
                childVals[i] *= Math.pow(10,randN()/25);
                Parameter p = searchParameters[i];
                if (childVals[i] > p.getMax()) {childVals[i] = p.getMax();}
                if (childVals[i] < p.getMin()) {childVals[i] = p.getMin();}
            }
        }
        
        return new ParameterVector(searchParameters, childVals);
    }

    private String stringResult(ParameterVector pv) {
        StringBuilder message = new StringBuilder("");
        Parameter[] p = pv.getParameters();
        double[] d = pv.getValues();
        for (int i = 0; i < p.length; i++) {
            message.append(p[i].getName() + " = " + d[i] + "\n");
        }
        return message.toString();
    }
    
    
    
    
    private void debug(String s) {
        if (DEBUG) {
            System.out.println(s);
        }
    }
    
    private void debug(ParameterVector pv, String name) {
        if (!DEBUG){return;}
        StringBuilder s = new StringBuilder(name + ": ");
        Parameter[] p = pv.getParameters();
        double[] d = pv.getValues();
        for (int i = 0; i < p.length; i++){
            s.append(p[i].getName() + "=" + d[i] + ", ");
        }
        if (pv.getScore()!=null){
            s.append(pv.getScore().toString());
        }
        System.out.println(s.toString());
    }
}
