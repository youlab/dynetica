

package dynetica.algorithm;
import dynetica.system.*;
import dynetica.entity.*;
import dynetica.reaction.*;
import java.io.*;

/**
 * 
 * Created on May 7, 2005, 12:06 PM
 *
 *This class implements Gillespie's First Reaction Method in stochastic simulations
 * The results of this method is equivalent to the Direct Method.
 * @author Lingchong You
 * 
 * Note: July 2013, Lingchong You
 * The code is modified to allow storage of rates at different sampling time points.
 * The rates are in fact calculated using the rate expressions for the substances, *not* by taking 
 * derivative of time courses.
 * 
 */

public class FirstReactionMethod extends Algorithm {
    
     Reaction [] reactions;
    double[] a;
    double[] tau;
    Substance [] substances;
    
    
    int numberOfSubstances;
    int numberOfReactions;
    
    java.io.File output;
    
    SimulationTimer timer;
   
    
    public  FirstReactionMethod() {
	this(null, 0.1, 1000, 1, null);
    }

    public  FirstReactionMethod(ReactiveSystem system,
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
     @Override
    public void reset() {
         super.reset();
    }
    
 
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
        double t = Double.MAX_VALUE;
        int j=-1;
	for (int i = 0; i < numberOfReactions; i ++) {
	    a[i] = reactions[i].getRate();
            if (a[i] > 0.0 ) {
                double temp = -1.0/a[i] * Math.log(Math.random());
                if (t > temp) {
                    t = temp;
                    j=i;
                }
            }
	}
        if (t==Double.MAX_VALUE) 
            t = samplingStep;
        if (j>=0) { 
            reactions[j].fire();
    //        System.out.println(reactions[j]);
        }

        return t;
    }
    
    public javax.swing.JPanel editor() {
        return new dynetica.gui.algorithms.FirstReactionMethodEditor(this);
    }

}
