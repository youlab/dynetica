/*
 * EquilibratedReaction.java
 *
 * Created on February 11, 2001, 3:41 PM
 */

package dynetica.reaction;
import dynetica.system.*;
/**
 *
 * @author  Lingchong You
 * @version 0.01
 */
/**
 * a trivial implementation of the interface Equilibrated
 */

abstract public class EquilibratedReaction extends Reaction implements Equilibrated {

    /** Creates new EquilibratedReaction */
    public EquilibratedReaction() {
        super();
    }
    
    public EquilibratedReaction(String name, ReactiveSystem system) {
        super(name, system);
    }   

    public void update() {
    }
    
    public dynetica.gui.visualization.AbstractNode getNode() {
        if (this.node!=null) return this.node;
        return new dynetica.gui.visualization.EquilibratedRNode(this);
    }
    
}
