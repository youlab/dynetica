package dynetica.algorithm;

import dynetica.gui.plotting.Figure;
import dynetica.entity.*;
import dynetica.gui.*;
import dynetica.system.*;
import java.io.*;
import java.util.*;
import javax.swing.*;
import org.apache.commons.math3.distribution.*;

/**
 * 
 * @author Derek Eidum (2013)
 */
public class BottleNeckSimulation implements Runnable {
    ReactiveSystem system;
    Algorithm algorithm;

    Substance cooperator;
    Substance cheater;
    double initialCheaterFraction;

    int subpopulationSize;
    double probabilityThreshold;
    double subsimulationTime;
    int numRounds;
    boolean isPoisson = false;

    boolean saveData = false;
    String folderName;

    boolean normalizeIV = false;
    double normalizedIV;
    
    // added by Billy Wan (2016)
    // indicator of whether this is part of evolutionary dynamics simulation
    boolean evo = false;

    Thread simulationThread = null;

    public BottleNeckSimulation(ReactiveSystem sys, Substance co, Substance ch,
            int pop, double initFrac, double time, int rounds, boolean evo) {
        system = sys;
        cooperator = co;
        cheater = ch;
        subpopulationSize = pop;
        initialCheaterFraction = initFrac;
        subsimulationTime = time;
        numRounds = rounds;
        this.evo = evo;
    }

    public void run() {
        if (saveData) {
            folderName = "data/" + system.getName().replaceAll(" ", "_");
            int i = 0;
            File folder = new File(folderName);
            while (folder.exists()) {
                i++;
                folder = new File(folderName + i);
            }
            if (i > 0) {
                folderName = folderName + i;
            }
            boolean success = folder.mkdirs();
            if (!success) {
                System.out
                        .println("Failed to create necessary folders. Saving disabled.");
                saveData = false;
            }
        }

        if (isPoisson) {
            runPoisson();
            return;
        }

        double cheaterIV = cheater.getInitialValue();
        double cooperatorIV = cooperator.getInitialValue();

        algorithm = system.getAlgorithm();
        double[][] cooperatorValues = new double[subpopulationSize + 1][algorithm
                .getIterations()];
        double[][] cheaterValues = new double[subpopulationSize + 1][algorithm
                .getIterations()];
        double[] simulationWeight = new double[subpopulationSize + 1];
        double[] globalCheaterValues = new double[algorithm.getIterations()];
        double[] globalCooperatorValues = new double[algorithm.getIterations()];
        ArrayList<Double> totalCheaterValues = new ArrayList<Double>();
        ArrayList<Double> totalCooperatorValues = new ArrayList<Double>();
        ArrayList<Double> runningTime = new ArrayList<Double>();
        double timeOffset = 0;

        for (int run = 0; run < numRounds; run++) {
            globalCheaterValues = new double[algorithm.getIterations()];
            globalCooperatorValues = new double[algorithm.getIterations()];
            BinomialDistribution bd = new BinomialDistribution(
                    subpopulationSize, initialCheaterFraction);
            for (int cheatCount = 0; cheatCount <= subpopulationSize; cheatCount++) {
                simulationWeight[cheatCount] = bd.probability(cheatCount);
                if (normalizeIV) {
                    cheater.setInitialValue(normalizedIV * cheatCount
                            / subpopulationSize);
                    cooperator.setInitialValue(normalizedIV
                            * (1 - cheatCount / subpopulationSize));
                } else {
                    cheater.setInitialValue(cheatCount);
                    cooperator.setInitialValue(subpopulationSize - cheatCount);
                }
                compute();
                saveData(run, cheatCount, subpopulationSize);
                cooperatorValues[cheatCount] = cooperator.getValues();
                cheaterValues[cheatCount] = cheater.getValues();
                for (int i = 0; i < algorithm.getIterations(); i++) {
                    globalCheaterValues[i] += cheaterValues[cheatCount][i]
                            * simulationWeight[cheatCount];
                    globalCooperatorValues[i] += cooperatorValues[cheatCount][i]
                            * simulationWeight[cheatCount];
                }
            }
            double[] timeVals = system.getTimer().getTimePoints();
            for (int i = 0; i < globalCheaterValues.length; i++) {
                totalCheaterValues.add(globalCheaterValues[i]);
                totalCooperatorValues.add(globalCooperatorValues[i]);
                runningTime.add(timeVals[i] + timeOffset);
            }
            timeOffset = runningTime.get(runningTime.size() - 1);
            double finalCheater = globalCheaterValues[globalCheaterValues.length - 1];
            double finalCooperator = globalCooperatorValues[globalCooperatorValues.length - 1];
            initialCheaterFraction = (finalCheater)
                    / (finalCheater + finalCooperator);
        }

        cheater.setInitialValue(cheaterIV);
        cooperator.setInitialValue(cooperatorIV);
        // cheater.setValues(globalCheaterValues);
        // cooperator.setValues(globalCooperatorValues);

        double[] xvals = unwrapList(runningTime.toArray());
        double[][] yvals = new double[2][xvals.length];
        yvals[0] = unwrapList(totalCooperatorValues.toArray());
        yvals[1] = unwrapList(totalCheaterValues.toArray());
        String[] ylabels = { "Cooperator", "Cheater" };

        JFrame jf = new JFrame("Bottleneck Simulation Plot");
        Figure figure = new Figure("Time", ylabels, xvals, yvals);
        jf.getContentPane().add(figure);
        jf.setSize(500, 400);
        figure.setSize(500, 400);
        // jf.pack();
        jf.show();
    }

    public void runPoisson() {
        double cheaterIV = cheater.getInitialValue();
        double cooperatorIV = cooperator.getInitialValue();

        algorithm = system.getAlgorithm();
        double[] globalCheaterValues = new double[algorithm.getIterations()];
        double[] globalCooperatorValues = new double[algorithm.getIterations()];
        ArrayList<Double> totalCheaterValues = new ArrayList<Double>();
        ArrayList<Double> totalCooperatorValues = new ArrayList<Double>();
        ArrayList<Double> runningTime = new ArrayList<Double>();
        double timeOffset = 0;

        for (int run = 0; run < numRounds; run++) {
            globalCheaterValues = new double[algorithm.getIterations()];
            globalCooperatorValues = new double[algorithm.getIterations()];

            int maxSize = 2 * subpopulationSize;
            if (maxSize < 10) {
                maxSize = 10;
            }

            PoissonDistribution pd = new PoissonDistribution(subpopulationSize);

            for (int pop = 1; pop <= maxSize; pop++) {
                double[] simulationWeight = new double[pop + 1];
                double[][] cooperatorValues = new double[pop + 1][algorithm
                        .getIterations()];
                double[][] cheaterValues = new double[pop + 1][algorithm
                        .getIterations()];
                double poissonWeight = pd.probability(pop);
                BinomialDistribution bd = new BinomialDistribution(pop,
                        initialCheaterFraction);
                for (int cheatCount = 0; cheatCount <= pop; cheatCount++) {
                    simulationWeight[cheatCount] = bd.probability(cheatCount);
                    if (normalizeIV) {
                        cheater.setInitialValue(normalizedIV * cheatCount / pop);
                        cooperator.setInitialValue(normalizedIV
                                * (1 - cheatCount / pop));
                    } else {
                        cheater.setInitialValue(cheatCount);
                        cooperator.setInitialValue(pop - cheatCount);
                    }
                    compute();
                    saveData(run, cheatCount, pop);
                    cooperatorValues[cheatCount] = cooperator.getValues();
                    cheaterValues[cheatCount] = cheater.getValues();
                    for (int i = 0; i < algorithm.getIterations(); i++) {
                        globalCheaterValues[i] += cheaterValues[cheatCount][i]
                                * simulationWeight[cheatCount] * poissonWeight;
                        globalCooperatorValues[i] += cooperatorValues[cheatCount][i]
                                * simulationWeight[cheatCount] * poissonWeight;
                    }
                }
            }
            double[] timeVals = system.getTimer().getTimePoints();
            for (int i = 0; i < globalCheaterValues.length; i++) {
                totalCheaterValues.add(globalCheaterValues[i]);
                totalCooperatorValues.add(globalCooperatorValues[i]);
                runningTime.add(timeVals[i] + timeOffset);
            }
            timeOffset = runningTime.get(runningTime.size() - 1);
            double finalCheater = globalCheaterValues[globalCheaterValues.length - 1];
            double finalCooperator = globalCooperatorValues[globalCooperatorValues.length - 1];
            initialCheaterFraction = (finalCheater)
                    / (finalCheater + finalCooperator);
        }

        cheater.setInitialValue(cheaterIV);
        cooperator.setInitialValue(cooperatorIV);
        // cheater.setValues(globalCheaterValues);
        // cooperator.setValues(globalCooperatorValues);

        double[] xvals = unwrapList(runningTime.toArray());
        double[][] yvals = new double[2][xvals.length];
        yvals[0] = unwrapList(totalCooperatorValues.toArray());
        yvals[1] = unwrapList(totalCheaterValues.toArray());
        String[] ylabels = { "Cooperator", "Cheater" };

        JFrame jf = new JFrame("Bottleneck Simulation Plot");
        Figure figure = new Figure("Time", ylabels, xvals, yvals);
        jf.getContentPane().add(figure);
        jf.setSize(500, 400);
        figure.setSize(500, 400);
        // jf.pack();
        jf.show();
    }

    private void compute() {
        algorithm = system.getAlgorithm();
        algorithm.reset();
        algorithm
                .setSamplingStep(subsimulationTime / algorithm.getIterations());
        int iterations = algorithm.getIterations();
        for (int i = 0; i < iterations; i++) {
            if (!evo && simulationThread == null) {
                break;
            }
            algorithm.update();
        }
        // java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public void start() {
        if (simulationThread == null) {
            simulationThread = new Thread(this, " Parameter Search");
            simulationThread.start();
        }
    }

    public void pause() {
        simulationThread = null;
    }

    public void setPoisson(boolean b) {
        isPoisson = b;
    }

    public double[] unwrapList(Object[] doubleList) {
        double[] result = new double[doubleList.length];
        for (int i = 0; i < result.length; i++) {
            result[i] = (double) ((Double) doubleList[i]);
        }
        return result;
    }

    private void saveData(int run, int cheatCount, int pop) {
        if (!saveData) {
            return;
        }
        double[] time = system.timer.getTimePoints();
        double[] coop = cooperator.getValues();
        double[] cheat = cheater.getValues();
        try {
            File file = new File(folderName + "/run" + run + "cheaters"
                    + cheatCount + "of" + pop + ".dat");
            file.createNewFile();
            FileWriter outFile = new FileWriter(file);
            StringBuilder writeOut = new StringBuilder("");
            for (int i = 0; i < coop.length; i++) {
                writeOut.append(time[i] + "\t" + coop[i] + "\t" + cheat[i]
                        + "\n");
            }
            outFile.write(writeOut.toString());
            outFile.close();
        } catch (IOException ex) {
            System.out
                    .println("Writing failed with IOException.  Disabling saving for this simulation...");
            ex.printStackTrace();
            saveData = false;
        }
    }

    public void setSave(boolean b) {
        saveData = b;
    }

    public boolean isSaving() {
        return saveData;
    }

    public void setNormalize(boolean b) {
        normalizeIV = b;
    }

    public boolean isNormalizing() {
        return normalizeIV;
    }

    public void setNormalizedIV(double d) {
        normalizedIV = d;
    }

    public double getNormalizedIV() {
        return normalizedIV;
    }

}
