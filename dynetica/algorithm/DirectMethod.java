/**
 * Stochastic.java
 *
 *
 * Created: Fri Sep  1 13:59:52 2000
 *
 * @author Lingchong You
 * @version 1.0
 * @version 1.2 updated 4/19/2005
 * a) fixed a bug
 * b) added handling of ExpressionVariable -- a subclass of Substance.
 * c) The algorithm can now do multiple rounds of simulations.
 * 
 */
package dynetica.algorithm;
import dynetica.system.*;
import dynetica.entity.*;
import dynetica.util.*;
import dynetica.reaction.*;
import java.util.*;
import java.io.*;

/** 
 * This class implements the Gillespie's algorithm to simulate
 * coupled chemical reactions
 * @ref Gillespie, Daniel (1977), Exact stochastic simulation of
 *      coupled chemical reactions. J. Phys. Chem. 81(25) 2340.
 * 
 * 
 * Note: July 2013, Lingchong You
 * The code is modified to allow storage of rates at different sampling time points.
 * The rates are in fact calculated using the rate expressions for the substances, *not* by taking 
 * derivative of time courses.
 * 
 * 
 */

public class DirectMethod extends Algorithm {

    protected Reaction [] reactions;
    protected double[] a;
    protected Substance [] substances;
    
    
    int numberOfSubstances;
    int numberOfReactions;
    
    java.io.File output;
    
    SimulationTimer timer;
   
    
    public DirectMethod() {
	this(null, 0.1, 1000, 1, null);
    }

    public DirectMethod(ReactiveSystem system,
		      double samplingStep,
		      int iterations,
                      int numberOfRounds,
                      File output) {
	super(system);
	setSamplingStep(samplingStep);
	setIterations(iterations);
        setNumberOfRounds(numberOfRounds);
        setOutput(output);
    }
 
    // Revised 4/19/2005 LY
    //
    public void init() {
        system.reset();
        numberOfReactions = system.getProgressiveReactions().size();
	numberOfSubstances = system.getSubstances().size();
	reactions = new Reaction[numberOfReactions];
	substances = new Substance[numberOfSubstances];
         
	for (int i = 0; i < numberOfReactions; i++) 
	    reactions[i] = (Reaction) (system.getProgressiveReactions().get(i));
	
	a = new double[numberOfReactions];
	
	for (int i = 0; i < numberOfSubstances; i++) {
	    substances[i] = (Substance) (system.getSubstances().get(i));
	}
	timer = system.getTimer();
        
        if ( substances != null) {
            for (int i = 0; i < numberOfSubstances; i ++) {
                Substance s = substances[i];
                s.storeValue();
                s.storeRate();

            }
      	    timer.storeTimePoint();
	}
        
    }

    //
    // Revised 4/19/2005 LY
    // 
    public void reset() {
         super.reset();
    }
    
    //
    // revised 4/22/2005 LY
    public void update() {
        for (double t = 0.0; t < samplingStep; ) {
	    double step = fire();
	    //
	    // update the timer
	    //
	    timer.step(step);
	    t += step;
        //
        // the following line is added just so that this algorithm
        // is compatible with deterministic algorithms.
        // 
        // where there are equilibrated reactions in the system
        // this algorithm probably should not be applied at all!
        //
        system.updateSpecialReactions(step);
        
        //
        // added 5/7/2006 to handle update of expression variables
        //
        system.updateExpressions();
        
	}

	timer.storeTimePoint();
	for (int i = 0; i < numberOfSubstances; i ++) {
	    substances[i].storeValue();
            substances[i].storeRate();
        }

    }
    
       
    /**
     * update the ReactiveSystem by firing one reaction only.
     * @return the time step needed to accomplish this one firing.
     */

    public double fire() {
	for (int i = 0; i < numberOfReactions; i ++) {
	    a[i] = reactions[i].getRate();
	}
	
	double totalActivity = Numerics.sum(a);
	//
	//It's meaningful to update the system only when the totalActivity is
	//greater than zero.
	//
	if (totalActivity > 0.0) {
	    //
	    // determine the time step for the next reaction to happen.
	    //
	    double t = - 1.0 / totalActivity * Math.log(Math.random());	
	    //
	    // then determine which reaction to fire.
	    //
	    double ra = totalActivity * Math.random();
	    double tmp = 0.0;
	    for (int i = 0; i < numberOfReactions; i ++) {
		tmp += a[i];
		if (tmp >= ra) {
		    reactions[i].fire();
		    break;
		}
	    }
	    return t;
	}
	else {
//if total activity is zero, directly jump to the end of the samplingStep.
	    return samplingStep;
	}
    }
    
    public javax.swing.JPanel editor() {
        return new dynetica.gui.algorithms.DirectMethodEditor(this);
       // return null;
    }

  	
} // DirectMethod
