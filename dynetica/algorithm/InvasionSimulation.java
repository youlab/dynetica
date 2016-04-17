package dynetica.algorithm;

/**
 *
 * @author Xizheng (Billy) Wan (2016)
 */

import dynetica.entity.*;
import dynetica.system.*;
import dynetica.gui.plotting.*;
import java.awt.Color;
import javax.swing.*;

public class InvasionSimulation implements Runnable {
    private ReactiveSystem system;
    private Algorithm algorithm;
    private Substance cooperator;
    private Substance cheater;
    private Parameter coopParam;
    private Parameter cheaterParam;
    private double initCellDensity;
    private double initCheaterFraction;
    private int numPoints;
    private double simTime;
    private HeatMap hm;
    
    private Thread simulationThread = null;
    
    public InvasionSimulation(ReactiveSystem sys, Substance coop, Substance cheater,
            Parameter coopParam, Parameter cheaterParam, double initCD,
            double initCheaterFrac, int numPts, double time) {
        this.system = sys;
        this.cooperator = coop;
        this.cheater = cheater;
        this.coopParam = coopParam;
        this.cheaterParam = cheaterParam;
        this.initCellDensity = initCD;
        this.initCheaterFraction = initCheaterFrac;
        this.numPoints = numPts;
        this.simTime = time;
        
        this.algorithm = sys.getAlgorithm();
    }
    
    public void run() {
        double[][] cheaterFitness = new double[numPoints+1][numPoints+1];
        cheater.setInitialValue(initCellDensity * initCheaterFraction);
        cooperator.setInitialValue(initCellDensity * (1 - initCheaterFraction));
        double coopMin = coopParam.getMin();
        double coopMax = coopParam.getMax();
        double cheaterMin = cheaterParam.getMin();
        double cheaterMax = cheaterParam.getMax();
        for (int i = 0; i <= numPoints; i++) {
            cheaterParam.setValue(cheaterMin + (cheaterMax - cheaterMin) / 
                    numPoints * i);
            for (int j = 0; j <= numPoints; j++) {
                coopParam.setValue(coopMin + (coopMax - coopMin) / numPoints * j);
                compute();
                cheaterFitness[i][j] = cheater.getValue() 
                        / cheater.getInitialValue();
            }
        }
        // divide by diagonal value to get ratio (normalize by the value when 
        // cheater is alone
        for (int j = 0; j < cheaterFitness[0].length; j++) {
            double diagonalValue = cheaterFitness[j][j];
            for (int i = 0; i < cheaterFitness.length; i++) {
                cheaterFitness[i][j] /= diagonalValue;
            }
        }
        Color bl = Color.BLUE;
        Color yl = Color.YELLOW;
        Color[] colors = new Color[256];
        for (int i = 0; i < 256; i++) {
            double ratio = i / 256.0;
            int red = (int) (yl.getRed() * ratio + bl.getRed() * (1 - ratio));
            int green = (int) (yl.getGreen() * ratio + bl.getGreen() * (1 - ratio));
            int blue = (int) (yl.getBlue() * ratio + bl.getBlue() * (1 - ratio));
            Color thisColor = new Color(red, green, blue);
            colors[i] = thisColor;
        }
        
        hm = new HeatMap(cheaterFitness, colors);
        hm.setTitle("Invasion Simulation Plot for " + system.getName());
        hm.setXAxisTitle(coopParam.getName());
        hm.setYAxisTitle(cheaterParam.getName());
        hm.setCoordinateBounds(coopMin, coopMax, cheaterMin, cheaterMax);
    }
    
    public void plot() {
        new HeatMapWindow(hm).setVisible(true);
    }
    
    private void compute() {
        algorithm = system.getAlgorithm();
        algorithm.reset();
        algorithm
                .setSamplingStep(simTime / algorithm.getIterations());
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
            simulationThread = new Thread(this, " Simulation");
            simulationThread.start();
        }
    }
    
    public void pause() {
        simulationThread = null;
    }
}
