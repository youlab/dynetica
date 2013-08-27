package dynetica.algorithm;

/**
 * OptimizedDirectMethod.java
 *
 * Implements an optimized version of the Gillespie direct method.
 * Updates only rates of reactions that are affected by the firing of selected reaction. 
 *
 * For simple systems, this doesn't necessarily work better than the direct method.
 * 
 * 
 *  Note: July 2013, Lingchong You
 * The code is modified to allow storage of rates at different sampling time points.
 * The rates are in fact calculated using the rate expressions for the substances, *not* by taking 
 * derivative of time courses.
 * 
 */

import dynetica.entity.*;
import dynetica.system.*;
import dynetica.util.*;
import dynetica.reaction.*;
import java.util.*;

/**
 * 
 * @author Lingchong You
 * @version 1.0 implemented 5/8/2005
 */
public class OptimizedDirectMethod extends DirectMethod {

    boolean firstTime = true;
    int indices[];
    Map reactionMap;
    Vector reactionsToUpdate;
    Set exprAffectedReactions;
    int numberOfUpdates; // number of reactions to be updated.
    double totalActivity; // total reaction propensity

    /** Creates new OptimizedDirectMethod */
    public OptimizedDirectMethod() {
        this(null, 0.1, 1000, 1, null);
    }

    public OptimizedDirectMethod(ReactiveSystem system, double samplingStep,
            int iterations, int numberOfRounds, java.io.File output) {
        super(system, samplingStep, iterations, numberOfRounds, output);
    }

    public void init() {
        super.init();
        indices = new int[numberOfReactions];
        reactionMap = new Hashtable(numberOfReactions);
        exprAffectedReactions = new HashSet();
        reactionsToUpdate = new Vector(10);

        for (int i = 0; i < numberOfReactions; i++) {
            a[i] = reactions[i].getRate();
            // totalActivity += a[i];
            reactionMap.put(reactions[i], i);
        }
        numberOfUpdates = 0;

        //
        // Expressions may be time-dependent, so they may change constantly,
        // regardless which reaction is fired.
        //
        // Thus we need to update all reactions that depend on
        //
        List exprs = system.getExpressions();
        for (int i = 0; i < exprs.size(); i++) {
            ExpressionVariable s = (ExpressionVariable) (exprs.get(i));
            List temp = s.getReactions();
            for (int j = 0; j < temp.size(); j++) {
                Object pr = temp.get(j);
                exprAffectedReactions.add(pr);
                indices[numberOfUpdates++] = ((Integer) (reactionMap.get(pr)))
                        .intValue();
            }
        }

    }

    /** set up a list of reactions to be updated the next computation step */

    private void setUpdateList(Reaction r) {
        reactionsToUpdate.clear();

        List subs = r.getReactants();
        for (int i = 0; i < subs.size(); i++) {
            Substance s = (Substance) (subs.get(i));
            List temp = s.getReactions();
            for (int j = 0; j < temp.size(); j++) {
                Object pr = temp.get(j);
                if (!reactionsToUpdate.contains(pr))
                    reactionsToUpdate.add(pr);
            }

        }

        List prods = r.getProducts();
        for (int i = 0; i < prods.size(); i++) {
            Substance s = (Substance) (prods.get(i));
            List temp = s.getReactions();
            for (int j = 0; j < temp.size(); j++) {
                Object pr = temp.get(j);
                if (!reactionsToUpdate.contains(pr))
                    reactionsToUpdate.add(pr);
            }
        }

        numberOfUpdates = exprAffectedReactions.size();
        // System.out.println(numberOfUpdates);
        for (int i = 0; i < reactionsToUpdate.size(); i++) {
            ProgressiveReaction pr = (ProgressiveReaction) (reactionsToUpdate
                    .get(i));
            indices[numberOfUpdates++] = ((Integer) (reactionMap.get(pr)))
                    .intValue();
        }

    }

    /**
     * update the ReactiveSystem by firing one reaction only.
     * 
     * @return the time step needed to accomplish this one firing.
     */

    public double fire() {
        for (int i = 0; i < numberOfUpdates; i++) {
            a[indices[i]] = reactions[indices[i]].getRate();
        }

        double totalActivity = Numerics.sum(a);
        //
        // It's meaningful to update the system only when the totalActivity is
        // greater than zero.
        //
        if (totalActivity > 0.0) {
            //
            // determine the time step for the next reaction to happen.
            //
            double t = -1.0 / totalActivity * Math.log(Math.random());
            //
            // then determine which reaction to fire.
            //
            double ra = totalActivity * Math.random();
            double tmp = 0.0;
            for (int i = 0; i < numberOfReactions; i++) {
                tmp += a[i];
                if (tmp >= ra) {
                    reactions[i].fire();
                    setUpdateList(reactions[i]);
                    break;
                }
            }
            return t;
        } else {
            return samplingStep;
        }
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.algorithms.OptimizedDirectMethodEditor(this);
        // return null;
    }

}
