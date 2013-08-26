
package dynetica.entity;

import dynetica.entity.*;
import dynetica.objective.AbstractObjective;

/**
 * Wrapper class to include a substance, its objective function, and its weight
 * 
 * @author Derek Eidum
 */
public class TargetObjective {
    Substance substance;
    AbstractObjective objective;
    double weight;
    
    public TargetObjective (Substance s, AbstractObjective o, double w) {
        substance = s;
        objective = o;
        weight = w;
    }
    
    public Substance getSubstance() {return substance;}
    public AbstractObjective getObjectiveFunction() {return objective;}
    public double getWeight() {return weight;}
    
    public double getScore() {return objective.score();}
    
    public void setWeight(double w) {weight = w;}
    public void setObjectiveFunction(AbstractObjective o) {objective = o;}
    public void setSubstance(Substance s) {substance = s;}
}
