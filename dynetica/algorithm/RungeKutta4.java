/**
 * RungeKutta4.java
 *
 *
 * Created: Fri Sep  1 12:14:40 2000
 *
 * @author Lingchong You
 * @version 0.1
 * 
 *  Note: July 2013, Lingchong You
 * The code is modified to allow storage of rates at different sampling time points.
 * The rates are in fact calculated using the rate expressions for the substances, *not* by taking 
 * derivative of time courses.
 * 
 * 
 */
package dynetica.algorithm;
import dynetica.system.*;
import dynetica.entity.*;
import java.util.*;

/**
 *this class implements fixed-step 4th order Runge-Kutta algorithm
 */

public class RungeKutta4 extends Algorithm {
    double step;
    double []  k1, k2, k3, k4;
    double []  y1, y2;
    Substance[] s;
    int numberOfSubstances; // number of substances.
    static double maxStep = 0.01;
    SimulationTimer timer;

    public RungeKutta4() {
	this(null, 0.01, 0.1, 1000);
    }

    public RungeKutta4(ReactiveSystem system,
		       double maxStep,
		       double samplingStep,
		       int iterations) {
	super(system);
	setMaxStep(maxStep);
	setSamplingStep(samplingStep);
	setIterations(iterations);
        if (system != null) init();
    }

    public void setMaxStep(double its) {
	maxStep = its;
    }

    protected void init () {
	numberOfSubstances = system.getSubstances().size();
	s = new Substance[numberOfSubstances];
	k1 = new double[numberOfSubstances];
	k2 = new double[numberOfSubstances];
	k3 = new double[numberOfSubstances];
	k4 = new double[numberOfSubstances];
	y1 = new double[numberOfSubstances];

        //
        // store the initial values for the substances
        //
	for (int i = 0; i < numberOfSubstances; i ++) {
	    s[i] = (Substance) (system.getSubstances().get(i));
	}

	// synchronize the timer with that of the system.
	timer = system.getTimer();
    }

    @Override
    public void reset() {
        super.reset();
        system.reset();
	if ( s != null) {
            for (int i = 0; i < numberOfSubstances; i ++) {
		s[i].storeValue();
                s[i].storeRate();

            }
	    timer.storeTimePoint();
	}
    }

    public void update() {
	double t = 0.0;
	step = maxStep;
	while (t < samplingStep) {
	    if (samplingStep - t < step && 
		samplingStep - t > 0.0 ) 
		step = samplingStep - t;
	    t += step;

	    //step 1
	    for (int i = 0; i < numberOfSubstances; i ++) {
		y1[i] = s[i].getValue();
		k1[i] = step * s[i].getRate();
		s[i].setValue(y1[i] + k1[i] * 0.5);
	    }
	    timer.step(step * 0.5);

	    // step 2
	    for (int i = 0; i < numberOfSubstances; i++) {
		k2[i] = step * s[i].getRate();
		s[i].setValue(y1[i] + k2[i] * 0.5);
	    }

	    //step 3
	    for (int i = 0; i < numberOfSubstances; i++) {
		k3[i] = step * s[i].getRate();
		s[i].setValue(y1[i] + k3[i]);
	    }
	    timer.step(step * 0.5);
	    
	    //step 4
	    for (int i = 0; i < numberOfSubstances; i++) {
		k4[i] = step * s[i].getRate();
		s[i].setValue(y1[i] + (k1[i] + 2 * k2[i] + 2 * k3[i] + k4 [i]) / 6.0);
	    }    
            system.updateSpecialReactions(step);
            
        //
        // added 5/7/2006 to handle update of expression variables
        //
        system.updateExpressions();

	}
        
        
	timer.storeTimePoint();
 	for (int i = 0; i < numberOfSubstances; i++) {
	    s[i].storeValue();
            s[i].storeRate();

	}
   }
      public double getMaxStep() {return maxStep;}
      public javax.swing.JPanel editor() { return new dynetica.gui.algorithms.RK4Editor(this);}
} // RungeKutta4
