/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.entity;

/**
 *
 * @author owner
 */
public class TargetSubstance {
    Substance substance;
    double target;
    double weight;
    
    public TargetSubstance(Substance s, double t, double w) {
        substance = s;
        target = t;
        weight = w;
    }
    
    public Substance getSubstance() {return substance;}
    public double getTarget() {return target;}
    public double getWeight() {return weight;}
    public String getName() {return substance.getName();} 
    
    public void setWeight(double d) {weight = d;}
    public void setTarget(double d) {target = d;}
    
}
