
package dynetica.entity;

import dynetica.objective.*;

/**
 *
 * @author Derek Eidum
 */
public class TargetDoseObjective {
    AbstractMetric metric;
    AbstractDoseObjective objective;
    double weight;
    
    public TargetDoseObjective(AbstractMetric m, AbstractDoseObjective o, double w) {
        metric = m;
        objective = o;
        weight = w;
    }
    
    public AbstractMetric getMetric() {return metric;}
    public AbstractDoseObjective getObjective() {return objective;}
    public double getWeight() {return weight;}
    public double getScore() {return objective.score();}
    
    public void setMetric(AbstractMetric m) {metric = m;}
    public void setObjective(AbstractDoseObjective o) {objective = o;}
    public void setWeight(double w) {weight = w;}
    
    public String toString() {return objective.toString() + ": " + metric.toString();}

}
