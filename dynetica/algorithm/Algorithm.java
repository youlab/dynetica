/**
 * Algorithm.java
 *
 *
 * Created: Fri Sep  1 12:01:25 2000
 *
 * @author Lingchong You
 * @version 0.5 
 */

package dynetica.algorithm;
import dynetica.system.*;
import dynetica.event.*;
import dynetica.entity.*;
import java.io.*;
import java.util.*;

public abstract class Algorithm  implements dynetica.entity.Editable, Runnable {
    ReactiveSystem system;
    java.util.List listenerList = new java.util.ArrayList();
    
    protected Thread simulationThread = null;
    int pausedPosition = 0;
    int pausedRound = 0;
    static int iterations;
    
    /**
       * Get the value of iterations.
       * @return Value of iterations.
       */
    public int getIterations() {return iterations;}
    
    /**
       * Set the value of iterations.
       * @param v  Value to assign to iterations.
       */
    public void setIterations(int  v) {this.iterations = v;}
    

    static double samplingStep;
    
    /** Holds value of property finished. */
    private boolean finished = false;
    
    /** Holds value of property interrupted. */
    private boolean interrupted = false;

    /**
     * Holds value of property numberOfRounds.
     */
    private static int numberOfRounds;

    /**
     * Holds value of property output.
     */
    private java.io.File output;
    
    /**
       * Get the value of samplingStep.
       * @return Value of samplingStep.
       */
    public double getSamplingStep() {return samplingStep;}
    
    /**
       * Set the value of samplingStep.
       * @param v  Value to assign to samplingStep.
       */
    public void setSamplingStep(double v) {this.samplingStep = v;}
    
    /**
     * Get the value of system.
     * @return Value of system.
     */
    public Algorithm() {
	this(null);
    }

    public Algorithm(ReactiveSystem system) { 
	this.system = system;
        this.output = null;
        this.numberOfRounds = 1;
        this.iterations = 1;
        this.samplingStep =0;
    }

    public ReactiveSystem getSystem() {return system;}
    
    /**
     * Set the value of system.
     * @param system  Value to assign to system.
     */
    public void setSystem(ReactiveSystem  system) {
	this.system = system;
        if (system != null) {
            init();
            system.setAlgorithm(this);
        }
    }
    
    public void addSimulationDoneEventListener(dynetica.event.SimulationDoneEventListener l) {
        listenerList.add(l);
    }
    
    public void removeSimulationDoneEventListener(dynetica.event.SimulationDoneEventListener l) {
        listenerList.remove(l);
    }
    
    public void fireSimulationDone() {
        System.out.println("Simulation finished successfully.");
        for (int i =0; i < listenerList.size(); i++)
            ( (SimulationDoneEventListener) (listenerList.get(i))).simulationDone(new SimulationDoneEvent(this));
    }
    
    public void run() {
        long time = System.currentTimeMillis();
        List substances = system.getSubstances();
        SimulationTimer timer = system.getTimer();
        int j = pausedRound;
        
        PrintWriter out = null;
        if (output!=null) {
            try {
                out = new PrintWriter ( new FileOutputStream(output) );
            }
            catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
        
        if (out!=null) {
        int numberOfSubstances = system.getSubstances().size();
        //
        // print substance names
        //
        out.print(timer.getName());
        out.print("\t");

        for (int i=0; i < numberOfSubstances; i++) {
            Substance s= (Substance) (substances.get(i));
            out.print(s.getName());
            out.print("\t");
        }
            out.print("\n");
        }
        for (; j < numberOfRounds; j++) {
            //
            // reset the substances
            //
           int numberOfSubstances = substances.size();
           timer.reset();
           timer.storeTimePoint();
           
           for (int k=0; k < numberOfSubstances; k++){
            Substance s = (Substance) (substances.get(k));
            s.reset();
            s.storeValue();
            s.storeRate();
           }
            
           //carry out one round of simulation
            run (simulationThread);
            
            if (out!=null)  saveCurrentSimulationResult(out);
            
            if (simulationThread == null){
                System.out.println("Simulation paused at round " + (j+1));
                pausedRound = j;
                break;
            }
        }
        
        if (out!=null) out.close();
        
        //
        // set simulation Thread to be null after done
        simulationThread = null;
        fireSimulationDone();
        System.out.print("Total computation time: ");
        System.out.print(System.currentTimeMillis()-time);
        System.out.println(" milliseconds");

    }
    
    //
    // the following method is controlled by an outside thread.
    //
    public void run(Thread thread) {
        int i = pausedPosition;
        for (;  thread !=null && i < iterations; i++) {
            update();
            if (thread == null) {
                System.out.println("Simulation interrupted at iteration" + i);
                pausedPosition = i;
                break;
            }
        }
        java.awt.Toolkit toolkit = java.awt.Toolkit.getDefaultToolkit();
        if (i == iterations) {
            finished = true;
            toolkit.beep();
            toolkit.beep();
       }
        else  
            toolkit.beep();    
    }
    
     public void resume() {
         interrupted = false;
         if (simulationThread == null) {
           System.out.println("Paused simulation resumed at iteration " + pausedPosition);
           simulationThread = new Thread (this, "Resumed simulation");
           simulationThread.start();
         }
     }
    
     public void pause() {
         simulationThread = null;
         interrupted = true;
     }
     
    public void start() {
        if (simulationThread == null) {
            reset();
            simulationThread = new Thread (this, " Simulation");
            simulationThread.start();
        }
    }
    
    public abstract void update();
    protected abstract void init();
    public void reset(){
        simulationThread = null;
        pausedPosition = 0;
        pausedRound = 0;
        interrupted = false;
        finished = false;
        init();
    }
    
    /** Getter for property finished.
     * @return Value of property finished.
 */
    public boolean isFinished() {
        return finished;
    }
    
    /** Setter for property finished.
     * @param finished New value of property finished.
 */
    public void setFinished(boolean finished) {
        this.finished = finished;
    }
    
    /** Getter for property interrupted.
     * @return Value of property interrupted.
 */
    public boolean isInterrupted() {
        return interrupted;
    }
    
    /** Setter for property interrupted.
     * @param interrupted New value of property interrupted.
 */
    public void setInterrupted(boolean interrupted) {
        this.interrupted = interrupted;
    }

    /**
     * Getter for property numberOfRounds.
     * @return Value of property numberOfRounds.
     */
    public int getNumberOfRounds() {
        return this.numberOfRounds;
    }

    /**
     * Setter for property numberOfRounds.
     * @param numberOfRounds New value of property numberOfRounds.
     */
    public void setNumberOfRounds(int numberOfRounds) {
        this.numberOfRounds = numberOfRounds;
    }

    /**
     * Getter for property output.
     * @return Value of property output.
     */
    public java.io.File getOutput() {
        return this.output;
    }

    /**
     * Setter for property output.
     * @param output New value of property output.
     */
    public void setOutput(java.io.File output) {
        this.output = output;
    }
     // append simulation results to the output file after each round of simulation
    private void saveCurrentSimulationResult(PrintWriter out){
        List substances = system.getSubstances();
        SimulationTimer timer = system.getTimer();
        System.out.println("Saving current round of data");
	int numberOfSubstances = substances.size();
            
            
           //
           // print data
            //
            
          for (int j=0; j<iterations; j++){
                out.print(timer.getTimePoint(j));
                out.print("\t");
                for (int i=0; i<numberOfSubstances; i++) {
                    Substance s = (Substance) (substances.get(i));
                    out.print(s.getValue(j));
                    out.print("\t");
          
                }
                out.print("\n");
          }
	}

} // Algorithm
