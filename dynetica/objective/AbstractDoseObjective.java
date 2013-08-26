
package dynetica.objective;

/**
 *
 * @author Derek Eidum
 */
public abstract class AbstractDoseObjective {
    double[] dose;
    double[] response;
    
    public abstract double score();
    
    public void setDose(double[] d) {dose = d;}
    public void setResponse(double[] d) {response = d;}

}
