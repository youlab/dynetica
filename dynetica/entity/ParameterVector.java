/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.entity;

/**
 *
 * @author Derek Eidum
 */
public class ParameterVector implements Comparable {
    Parameter[] parameters;
    double[] values;
    Double score = null;
    
    public ParameterVector(Parameter[] p, double[] vals) {
        parameters = p;
        values = vals;
    }
    
    public Parameter[] getParameters() {return parameters;}
    public double[] getValues() {return values;}
    public double getValue(int index) {return values[index];}
    public Double getScore() {return score;}
    
    public void setScore(double d) {score = d;}
    
    public int compareTo(Object o) {
        if (!(o instanceof ParameterVector)) {return 1;}
        ParameterVector other = (ParameterVector) o;
        Double oScore = other.getScore();
        if (oScore == null) {
            if (score == null) {return 0;}
            return -1;
        }
        if (score == null) {return 1;}
        if (oScore.equals(score)) {return 0;}
        if (oScore.doubleValue() > score.doubleValue()) {return 1;}
        return -1;
    }
       

}
