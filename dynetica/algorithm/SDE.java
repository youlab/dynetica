/**
 * SDE.java
 *
 *
 * Created: August 25 2006
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.algorithm;
import dynetica.system.*;
import dynetica.entity.*;
import dynetica.reaction.*;
import java.io.*;

/**
 *this class implements Euler algorithm for stochastic differential equations
 */

public final class SDE extends Algorithm {
    double step;
    Substance[] s;

    protected ProgressiveReaction [] reactions;
    int numberOfSubstances; // number of substances.
    int numberOfReactions; // number of progressivereactions
    static double maxStep = 0.01;
    SimulationTimer timer;
    
    //noiseScale of 1 (default) corresponds to Lagevin formulation. 
    double noiseScale;
    
    double globalNoiseScale;

    public SDE() {
	this(null, 0.02, 0.1, 1000, 1.0, 0.0,  1, null);
    }

    public SDE(ReactiveSystem system,
		       double maxStep,
		       double samplingStep,
		       int iterations,
                       double noiseScale,
                       double globalNoiseScale,
                       int numberOfRounds,
                       File output
            ) {
	super(system);
	setMaxStep(maxStep);
	setSamplingStep(samplingStep);
	setIterations(iterations);
        setNoiseScale(noiseScale);
        setGlobalNoiseScale(globalNoiseScale);
        setNumberOfRounds(numberOfRounds);
        setOutput(output);

        if (system != null) init();
    }
    
    public void setMaxStep(double its) {
	maxStep = its;
    }

    public void setNoiseScale(double noiseScale){
        this.noiseScale = noiseScale;
    }
    public double getNoiseScale(){
        return noiseScale;
    }
    
    public void setGlobalNoiseScale(double gns){
        this.globalNoiseScale = gns;
    }
    
    public double getGlobalNoiseScale() {
        return globalNoiseScale;
    }
    
    protected void init () {
	numberOfSubstances = system.getSubstances().size();
	s = new Substance[numberOfSubstances];

        //
        // store the initial values for the substances
        //
	for (int i = 0; i < numberOfSubstances; i ++) {
	    s[i] = (Substance) (system.getSubstances().get(i));
	}

	// synchronize the timer with that of the system.
	timer = system.getTimer();
        
        numberOfReactions = system.getProgressiveReactions().size();
        reactions = new ProgressiveReaction[numberOfReactions];
        for (int i = 0; i < numberOfReactions; i++) 
	   reactions[i] = (ProgressiveReaction) (system.getProgressiveReactions().get(i));
        
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
    
    private void resetReactionNoiseTerms(){
           for (int i = 0; i < numberOfReactions; i++) reactions[i].resetNoiseTerm();
    }

    
    /**
     *use Euler method to solve the SDE
     */
    
    public void update() {
	double t = 0.0;
	step = maxStep;
	while (t < samplingStep) {
	    if (samplingStep - t < step && 
		samplingStep - t > 0.0 ) 
		step = samplingStep - t;
	    t += step;
            
            //make sure intrinsic noise terms are temporally and statistically independent
            resetReactionNoiseTerms();

            double globalNoise = new java.util.Random().nextGaussian();
             for (int i = 0; i < numberOfSubstances; i ++) {
		double y = s[i].getValue();
		double v1 = step * s[i].getRate(); //deterministic rate
                double v2 = Math.sqrt(step) * s[i].getNoise() * noiseScale; //intrinsic noise
                double v3 = Math.sqrt(step) * globalNoiseScale * globalNoise;   //extrinsic noise
             	s[i].setValue(y + v1 + v2 + v3);
	    }
             
            timer.step(step);       
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
      public javax.swing.JPanel editor() { 
            return new dynetica.gui.algorithms.SDEEditor(this);
      }
} // SDE
