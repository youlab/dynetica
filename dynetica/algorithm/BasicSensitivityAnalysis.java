/*
 * BasicSensitivityAnalysis.java
 *
 * Created on November 12, 2001, 12:24 PM
 */
package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.system.*;

/**
 * 
 * @author Lingchong You
 * @version 0.1
 */
public class BasicSensitivityAnalysis implements Runnable {
    int cycles = 5;
    double time = 50;
    Thread simulationThread = null;

    /** Holds value of property parameter. */
    private EntityVariable variable = null;
    private Substance[] substances = null; // selected substances to be
                                           // evaluated.

    /** Holds value of property system. */
    private ReactiveSystem system;

    /** Holds value of property logScale. */
    private boolean logScale = false;

    /** Holds value of property min. */
    private double min = 0.0;

    /** Holds value of property max. */
    private double max = 1.0;

    /** Holds value of property yValues. */
    private double[][] yValues;
    private double[] xValues;
    private Algorithm algorithm;

    private double[][][] timeCourses; // timeCourse[i][j][k]. i: index of y
                                      // variables, j: index of parameter value,
                                      // k: index of timepoints

    /** Creates new Sensitivity */
    public BasicSensitivityAnalysis() {
    }

    public BasicSensitivityAnalysis(ReactiveSystem system) {
        setSystem(system);
        algorithm = system.getAlgorithm();
    }

    public BasicSensitivityAnalysis(ReactiveSystem system,
            EntityVariable variable, double min, double max, boolean log,
            int cycles, Substance[] substances, double time) {
        setSystem(system);
        setVariable(variable);
        setMin(min);
        setMax(max);
        setLogScale(log);
        setSubstances(substances);
        setTime(time);
        setCycles(cycles);
    }

    /**
     * Getter for property cycles.
     * 
     * @return Value of property cycles.
     */
    public int getCycles() {
        return cycles;
    }

    /**
     * Setter for property cycles.
     * 
     * @param cycles
     *            New value of property cycles.
     */
    public void setCycles(int cycles) {
        this.cycles = cycles;
    }

    /**
     * Getter for property parameter.
     * 
     * @return Value of property parameter.
     */
    public EntityVariable getVariable() {
        return variable;
    }

    /**
     * Setter for property parameter.
     * 
     * @param parameter
     *            New value of property parameter.
     */
    public void setVariable(EntityVariable variable) {
        this.variable = variable;
    }

    /**
     * Getter for property substances.
     * 
     * @return Value of property substances.
     */
    public Substance[] getSubstances() {
        return substances;
    }

    /**
     * Setter for property substances.
     * 
     * @param substances
     *            New value of property substances.
     */
    public void setSubstances(Substance[] substances) {
        this.substances = substances;
    }

    /**
     * Getter for property time.
     * 
     * @return Value of property time.
     */
    public double getTime() {
        return time;
    }

    /**
     * Setter for property time.
     * 
     * @param time
     *            New value of property time.
     */
    public void setTime(double time) {
        this.time = time;
    }

    /**
     * Getter for property system.
     * 
     * @return Value of property system.
     */
    public ReactiveSystem getSystem() {
        return system;
    }

    /**
     * Setter for property system.
     * 
     * @param system
     *            New value of property system.
     */
    public void setSystem(ReactiveSystem system) {
        this.system = system;
    }

    public void run() {

        if (cycles > 0 && variable != null && substances != null) {
            yValues = new double[substances.length][cycles + 1];
            xValues = new double[cycles + 1];
            timeCourses = new double[substances.length][cycles + 1][algorithm
                    .getIterations()];

            double currentValue;
            double oldValue = variable.getValue();
            for (int i = 0; i <= cycles; i++) {
                if (simulationThread == null) {
                    System.out.println("Sensitivity analysis stopped at round "
                            + i);
                    break;
                }
                if (!logScale) {
                    currentValue = i * (max - min) / cycles + min;
                } else {
                    currentValue = Math.exp(i * (Math.log(max) - Math.log(min))
                            / cycles + Math.log(min));
                }
                xValues[i] = currentValue;
                if (variable instanceof Parameter)
                    variable.setValue(currentValue);
                else
                    ((Substance) variable).setInitialValue(currentValue);

                //
                // conduct one round of simulation
                //
                compute();

                for (int j = 0; j < substances.length; j++) {
                    double[] values = substances[j].getValues();
                    timeCourses[j][i] = values;
                    yValues[j][i] = values[values.length - 1];
                }
            }

            if (variable instanceof Parameter)
                variable.setValue(oldValue);
            else
                ((Substance) variable).setInitialValue(oldValue);
            simulationThread = null;
        }
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
            simulationThread = new Thread(this, " Simulation");
            simulationThread.start();
        }
    }

    public void pause() {
        simulationThread = null;
    }

    /**
     * Getter for property logScale.
     * 
     * @return Value of property logScale.
     */
    public boolean isLogScale() {
        return logScale;
    }

    /**
     * Setter for property logScale.
     * 
     * @param logScale
     *            New value of property logScale.
     */
    public void setLogScale(boolean logScale) {
        this.logScale = logScale;
    }

    /**
     * Getter for property min.
     * 
     * @return Value of property min.
     */
    public double getMin() {
        return min;
    }

    /**
     * Setter for property min.
     * 
     * @param min
     *            New value of property min.
     */
    public void setMin(double min) {
        this.min = min;
    }

    /**
     * Getter for property max.
     * 
     * @return Value of property max.
     */
    public double getMax() {
        return max;
    }

    /**
     * Setter for property max.
     * 
     * @param max
     *            New value of property max.
     */
    public void setMax(double max) {
        this.max = max;
    }

    /**
     * Indexed getter for property yValues.
     * 
     * @param index
     *            Index of the property.
     * @return Value of the property at <CODE>index</CODE>.
     */
    public double[] getYValues(int index) {
        return yValues[index];
    }

    public double[][] getYValues() {
        return yValues;
    }

    public double[] getXValues() {
        return xValues;
    }

    public double[][][] getTimeCourses() {
        return timeCourses;
    }
}
