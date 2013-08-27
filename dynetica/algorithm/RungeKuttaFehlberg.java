/*
 * RungeKuttaFehlberg.java
 *
 * Created on April 18, 2001, 6:34 PM
 */

package dynetica.algorithm;

import dynetica.entity.*;
import dynetica.system.*;
import dynetica.util.*;

/**
 * RungeKuttaFehlberg implements an adaptive step 4th order Runge-Kutta
 * algorithm invented by Fehlberg. The following implementation is from Numeric
 * Receipes (c) 1988-1992 by Cambridge University Press. (p714 - p722).
 * 
 * Note: July 2013, Lingchong You The code is modified to allow storage of rates
 * at different sampling time points. The rates are in fact calculated using the
 * rate expressions for the substances, *not* by taking derivative of time
 * courses.
 * 
 * 
 * @author Lingchong You
 * @version 1.0
 */
public class RungeKuttaFehlberg extends Algorithm {

    /**
     * The following constants are used to grow/shrink a time step
     * 
     * h1 = SAFETY * h0 * (error / tolerance) ^ PGROW to grow the time step h1 =
     * SAFETY * h0 * (error / tolerance) ^ PSHRINK to shrink the time step
     */
    static double PGROW = -0.2;

    static double PSHRINK = -0.25;
    static double SAFETY = 0.9;

    //
    // The following constants are used to accompany the Fehlberg formula.
    // They were found by Cash and Karp. I copied these constants from
    // Numerical Receipes.
    //
    static double a2 = 0.2, a3 = 0.3, a4 = 0.6, a5 = 1.0, a6 = 0.875,
            b21 = 0.2, b31 = 3.0 / 40.0, b32 = 9.0 / 40.0, b41 = 0.3,
            b42 = -0.9, b43 = 1.2, b51 = -11.0 / 54.0, b52 = 5.0 / 2.0,
            b53 = -70.0 / 27.0, b54 = -35.0 / 27.0, b61 = 1631.0 / 55296.0,
            b62 = 175.0 / 512.0, b63 = 575.0 / 13824.0,
            b64 = 44275.0 / 110592.0, b65 = 253.0 / 4096.0, c1 = 37.0 / 378.0,
            c2 = 0.0, c3 = 250.0 / 621.0, c4 = 125.0 / 594.0, c5 = 0,
            c6 = 512.0 / 1771.0, dc1 = c1 - 2825.0 / 27648.0, dc2 = 0.0,
            dc3 = c3 - 18575.0 / 48384.0, dc4 = c4 - 13525.0 / 55296.0,
            dc5 = -277.0 / 14336.0, dc6 = c6 - 0.25;

    static double tolerance; // the error torlerance
    static double step;
    static double minStep; // the minimal time step allowable
    int maxIterations; // the maximum number of iterations per samplingStep
                       // allowable.
    SimulationTimer timer;

    /** Holds value of property initialStep. */
    private static double initialStep;

    double[] k1, k2, k3, k4, k5, k6; // intermediate variables
    double[] y1, y2; // intermediate variables
    double[] yerr; // Error estimate as the difference between 4th and 5th order
                   // methods.

    Substance[] s; // handles to the substances in the system

    int numberOfSubstances; // number of substances in the system.

    /** Creates new RungeKuttaFehlberg */
    public RungeKuttaFehlberg() {
        this(null, 1000, 0.1, 0.001, 1.0e-5, 1.0e-10, 10000);
    }

    public RungeKuttaFehlberg(ReactiveSystem system, int iterations,
            double samplingStep, double initialStep, double tolerance,
            double minStep, int maxIterations) {
        setSystem(system);
        setIterations(iterations);
        setSamplingStep(samplingStep);
        setInitialStep(initialStep);
        setTolerance(tolerance);
        setMinStep(minStep);
        setMaxIterations(maxIterations);
        step = initialStep;
    }

    public void init() {
        numberOfSubstances = system.getSubstances().size();
        s = new Substance[numberOfSubstances];
        k1 = new double[numberOfSubstances];
        k2 = new double[numberOfSubstances];
        k3 = new double[numberOfSubstances];
        k4 = new double[numberOfSubstances];
        k5 = new double[numberOfSubstances];
        k6 = new double[numberOfSubstances];
        y1 = new double[numberOfSubstances];
        y2 = new double[numberOfSubstances];
        yerr = new double[numberOfSubstances];

        for (int i = 0; i < numberOfSubstances; i++) {
            s[i] = (Substance) (system.getSubstances().get(i));
        }

        // synchronize the timer with that of the system.
        timer = system.getTimer();
    }

    public void reset() {
        super.reset();
        system.reset();
        if (s != null) {
            for (int i = 0; i < numberOfSubstances; i++) {
                s[i].storeValue();
                s[i].storeRate();

            }
            timer.storeTimePoint();
        }

    }

    //
    // rkckStep takes a Cash-Karp Runge-Kutta step
    // to estimate the error and the substance levels
    // after the attempted step.
    //
    private void rkckStep(double h) {
        double t = timer.getTime();

        // step 1
        for (int i = 0; i < numberOfSubstances; i++) {
            //
            // get the substance values before this step
            //
            y1[i] = s[i].getValue();
            //
            // k1 = h * f(t, y);
            //
            k1[i] = h * s[i].getRate();

        }

        // step 2
        //
        // k2 = h * f (t + a2 * h, y + b21 * k1)
        //
        timer.setTime(t + a2 * h);
        for (int i = 0; i < numberOfSubstances; i++) {

            s[i].setValue(y1[i] + k1[i] * b21);
            k2[i] = h * s[i].getRate();

        }

        // step 3
        //
        // k3 = h * f (t + a3 * h, y + b31 * k1 + b32 * k2)
        //
        timer.setTime(t + a3 * h);
        for (int i = 0; i < numberOfSubstances; i++) {
            s[i].setValue(y1[i] + b31 * k1[i] + b32 * k2[i]);
            k3[i] = h * s[i].getRate();
        }

        // step 4
        //
        // k4 = h * f (t + a4 * h, y + b41 * k1 + b42 * k2 + b43 * k3)
        //
        timer.setTime(t + a4 * h);
        for (int i = 0; i < numberOfSubstances; i++) {

            s[i].setValue(y1[i] + b41 * k1[i] + b42 * k2[i] + b43 * k3[i]);
            k4[i] = h * s[i].getRate();
        }

        // step 5
        // k5 = h * f (t + a5 * h, y + b51 * k1 + b52 * k2 + b53 * k3 + b54 *
        // k4)
        //
        timer.setTime(t + a5 * h);
        for (int i = 0; i < numberOfSubstances; i++) {
            s[i].setValue(y1[i] + b51 * k1[i] + b52 * k2[i] + b53 * k3[i] + b54
                    * k4[i]);
            k5[i] = h * s[i].getRate();
        }

        // step 6
        // k6 = h * f (t + a6 * h, y + b61 * k1 + b62 * k2 + b63 * k3 + b64 * k4
        // + k65 * k5)
        //
        timer.setTime(t + a5 * h);
        for (int i = 0; i < numberOfSubstances; i++) {
            s[i].setValue(y1[i] + b61 * k1[i] + b62 * k2[i] + b63 * k3[i] + b64
                    * k4[i] + b65 * k5[i]);
            k6[i] = h * s[i].getRate();
        }

        for (int i = 0; i < numberOfSubstances; i++) {
            y2[i] = y1[i] + c1 * k1[i] + c3 * k3[i] + c4 * k4[i] + c6 * k6[i]; // fifth
                                                                               // order
                                                                               // estimate

            //
            // error between 4th and 5th
            //
            // note that the errors are scaled with respect to estimated new
            // values
            // if the new values are smaller than 1e-8, scale to 1e-8. This
            // number is
            // used to avoid scaling with respect to a number that is too small.
            //
            yerr[i] = Math.abs((dc1 * k1[i] + dc3 * k3[i] + dc4 * k4[i] + dc5
                    * k5[i] + dc6 * k6[i])
                    / Math.max(Math.abs(y2[i]), 1e-8));
        }

    }

    public void update() {
        double t = 0.0;
        double errRatio;
        if (step > samplingStep)
            step = samplingStep;
        int nSteps = 0;
        for (;;) {
            if ((samplingStep - t < step) && (samplingStep - t > 0.0))
                step = samplingStep - t;

            rkckStep(step);
            errRatio = Numerics.max(yerr) / tolerance;

            //
            // Update the substance levels and go to the next step if
            // 1. The target tolerance is reached.
            // 2. The maximum # of iteractions is exceeded.
            // 3. The minimum time step is reached.
            //
            if (errRatio <= 1.0) {
                t += step;
                updateSystem(step);
                //
                // grow the step a little bit. but not more than a factor of
                // five
                //
                step *= Math.min(SAFETY * Math.pow(errRatio, PGROW), 5.0);
            }

            else if (nSteps > maxIterations) {
                System.out.println(" WARNING: # of iterations exceeds "
                        + maxIterations);
                t += step;
                updateSystem(step);
            }

            else if (step < minStep) {
                System.out.println(" WARNING: Minimum integrateion step size ("
                        + minStep + ") reached");
                t += step;
                updateSystem(step);
            }

            else {
                // step size too large.
                // Need to reset the values of all the substances
                //
                for (int i = 0; i < numberOfSubstances; i++)
                    s[i].setValue(y1[i]);
                //
                // reset the Timer back to where it started.
                //
                timer.step(0 - step);

                //
                // shrink the step size
                // and recompute. But shrink by a factor smaller than 10.
                //
                step *= Math.max(SAFETY * Math.pow(errRatio, PSHRINK), 0.1);
            }
            if (t >= samplingStep)
                break;
        }

        //
        // Store the data if the sampling step is reached.
        //
        timer.storeTimePoint();
        for (int i = 0; i < numberOfSubstances; i++) {
            s[i].storeValue();
            s[i].storeRate();

        }
    }

    private void updateSystem(double step) {
        // timer.step(step);
        for (int i = 0; i < numberOfSubstances; i++) {
            s[i].setValue(y2[i]);
        }

        system.updateSpecialReactions(step);

        //
        // added 7/28/2013 to handle update of expression variables
        //
        system.updateExpressions();
    }

    /**
     * Get the value of tolerance.
     * 
     * @return Value of tolerance.
     */
    public double getTolerance() {
        return tolerance;
    }

    /**
     * Set the value of tolerance.
     * 
     * @param v
     *            Value to assign to tolerance.
     */
    public void setTolerance(double v) {
        this.tolerance = v;
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.algorithms.RungeKuttaFehlbergEditor(this);
    }

    /**
     * Getter for property initialStep.
     * 
     * @return Value of property initialStep.
     */
    public double getInitialStep() {
        return initialStep;
    }

    /**
     * Setter for property initialStep.
     * 
     * @param initialStep
     *            New value of property initialStep.
     */
    public void setInitialStep(double initialStep) {
        this.initialStep = initialStep;
    }

    /**
     * Getter for property minStep.
     * 
     * @return Value of property minStep.
     */
    public double getMinStep() {
        return minStep;
    }

    /**
     * Setter for property minStep.
     * 
     * @param minStep
     *            New value of property minStep.
     */
    public void setMinStep(double minStep) {
        this.minStep = minStep;
    }

    /**
     * Getter for property maxIterations.
     * 
     * @return Value of property maxIterations.
     */
    public int getMaxIterations() {
        return maxIterations;
    }

    /**
     * Setter for property maxIterations.
     * 
     * @param maxIterations
     *            New value of property maxIterations.
     */
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

}
