/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.objective;

import dynetica.entity.Substance;
import dynetica.util.Statistics;

/**
 *
 * @author Derek Eidum
 */
public class MatchSubstance extends AbstractObjective {
    
    public MatchSubstance(AbstractMetric m) {
        metric = m;
    }
    
    public double score() {
        if (metric instanceof CorrelationCoefficient) {
            double rsquared = metric.getValue();
            if (rsquared <= 0) {return 0;}
            if (rsquared >= 1) {return 100;}
            return 100*rsquared;
        } else {
            System.out.println("Warning: use of MatchSubstance objective for non-CorrelationCoefficient metric!");
            return 0;
        }
    }
       
    @Override
    public String toString() {
        if (metric instanceof CorrelationCoefficient) {
            CorrelationCoefficient cc = (CorrelationCoefficient) metric;
            return "Matching " + cc.getSubstance().getName() + " to " + cc.getTarget().getName();
        }
        return "Matching of " + metric.getSubstance();
    }

}
