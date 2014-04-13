package dynetica.algorithm;

import dynetica.system.*;
import dynetica.entity.*;
import dynetica.expression.*;
import javax.swing.JOptionPane;

/**
 * 
 * @author Derek Eidum (2013)
 */
public class ParameterSearchMonteCarlo implements Runnable {
    ReactiveSystem system;
    Parameter[] searchParameters;
    Substance targetSubstance;
    double targetValue;
    double targetTime;
    Algorithm algorithm;
    Thread simulationThread = null;

    int maxIterations;
    double threshold;

    public ParameterSearchMonteCarlo(ReactiveSystem sys, Parameter[] p,
            Substance s, double t, double time) {
        system = sys;
        searchParameters = p;
        targetSubstance = s;
        targetValue = t;
        targetTime = time;
    }

    public void run() {
        algorithm = system.getAlgorithm();
        algorithm.reset();
        double prevError;
        compute();
        double[] values = targetSubstance.getValues();
        int lastIdx = values.length - 1;
        if (lastIdx < 0) {
            System.out.println("Substance values are empty. Aborting.");
            return;
        }
        double value = values[values.length - 1];
        double error = targetValue - value;
        if (Math.abs(error) < Math.abs(threshold)) {
            StringBuilder message = new StringBuilder(
                    "Parameter Fitting Complete. \n");
            for (int i = 0; i < searchParameters.length; i++) {
                Parameter p = searchParameters[i];
                message.append(p.getName() + " = " + p.getValue() + "\n");
            }
            JOptionPane.showMessageDialog(system.editor(), message.toString());
            return;
        }
        int counter = 1;
        int failCount = 0;
        while (true) {
            System.out.println("\n");
            prevError = error;
            counter++;
            double[] changes = new double[searchParameters.length];
            int numAlterations = (int) Math.ceil(0.5 * changes.length);
            int c = 0;
            while (c <= 0) {
                for (int i = 0; i < searchParameters.length; i++) {
                    if (ExpressionConstants.doubleNumber.nextDouble() < ((float) numAlterations)
                            / changes.length) {
                        changes[i] = calculateChange(searchParameters[i],
                                Math.abs(prevError) / targetValue);
                        c++;
                    } else {
                        changes[i] = 0;
                    }
                }
            }

            for (int i = 0; i < searchParameters.length; i++) {
                Parameter p = searchParameters[i];
                p.setSearchingValue(p.getValue() + changes[i]);
            }
            compute();
            values = targetSubstance.getValues();
            value = values[values.length - 1];
            for (Parameter a : searchParameters) {
                System.out.print(a.getName() + " = " + a.getValue() + ", ");
            }
            System.out.println("-> " + value);
            error = targetValue - value;
            if (Math.abs(error) < Math.abs(threshold)) {
                StringBuilder message = new StringBuilder(
                        "Parameter Fitting Complete. \n");
                for (int i = 0; i < searchParameters.length; i++) {
                    Parameter p = searchParameters[i];
                    message.append(p.getName() + " = " + p.getValue() + "\n");
                }
                JOptionPane.showMessageDialog(system.editor(),
                        message.toString());
                return;
            }
            double relativeImprovement = (Math.abs(prevError) - Math.abs(error))
                    / targetValue;
            double normalNum = ExpressionConstants.doubleNumber.nextGaussian();
            System.out.println("DEBUG: i = " + counter + "; error = " + error
                    + "; prevError = " + prevError + "; relImprovement = "
                    + relativeImprovement + "; needed = " + normalNum / 25);
            if (relativeImprovement > (normalNum / 25)) {
                failCount = 0;
                continue;
            }

            // Otherwise reset all parameters to previous value
            failCount++;
            error = prevError;
            for (int i = 0; i < searchParameters.length; i++) {
                Parameter p = searchParameters[i];
                p.setSearchingValue(p.getValue() - changes[i]);
            }
            System.out.print("Reset to ");
            for (Parameter a : searchParameters) {
                System.out.print(a.getName() + " = " + (float) a.getValue()
                        + ", ");
            }

            if (failCount >= maxIterations / 5) {
                JOptionPane.showMessageDialog(system.editor(),
                        "Failed to improve system on " + maxIterations / 5
                                + " consecutive trials.  Aborting.");
                break;
            }

            if (counter >= maxIterations) {
                // System.out.println("Failed to reach target value. Final value is "
                // + value + ". \n");
                JOptionPane
                        .showMessageDialog(
                                system.editor(),
                                "Maximum iterations reached.\n"
                                        + "Consider modifying parameter bounds, max iteration count, or threshold value.");
                resetParameters();
                break;
            }
        }
    }

    private void compute() {
        algorithm = system.getAlgorithm();
        algorithm.reset();
        algorithm.setSamplingStep(targetTime / algorithm.getIterations());
        int iterations = algorithm.getIterations();
        for (int i = 0; i < iterations; i++) {
            if (simulationThread == null) {
                break;
            }
            algorithm.update();
        }
        java.awt.Toolkit.getDefaultToolkit().beep();
    }

    public double calculateChange(Parameter p, double relError) {
        double value = p.getValue();
        if (Math.abs(value) < threshold / targetValue) {
            return (ExpressionConstants.doubleNumber.nextGaussian() / 10 * relError);
        }
        return Math.pow(10, ExpressionConstants.doubleNumber.nextGaussian()
                / (25) * relError)
                * value;
    }

    public void start() {
        if (simulationThread == null) {
            simulationThread = new Thread(this, " Parameter Search");
            simulationThread.start();
        }
    }

    public void setMaxIterations(int i) {
        maxIterations = i;
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setThreshold(double d) {
        threshold = d;
    }

    public double getThreshold(double d) {
        return threshold;
    }

    public void pause() {
        simulationThread = null;
    }

    public void resetParameters() {
        Object[] parameters = system.getParameters().toArray();
        for (int i = 0; i < parameters.length; i++) {
            Parameter p = (Parameter) parameters[i];
            p.resetSearchingValue();
        }
    }
}
