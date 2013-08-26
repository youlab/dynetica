
/**
 * Spacer.java
 *
 *
 * Created: Wed Aug 23 23:53:07 2000
 *
 * @author Lingchong You
 * @version
 */

package dynetica.entity;
import dynetica.system.*;

/** 
 * Spacer is an GeneticElement that has no known function
 */

public class Spacer extends GeneticElement {
    private static int spacerIndex = 0;
    public Spacer() {
        this("Spacer" + spacerIndex++, null);	
    }

    public Spacer(String name, Genome genome) {
	this(name, genome, 0, 0);
    }

    public Spacer(String name, Genome genome, int start, int end) {
	super(name, genome, start, end);
    }
    
} // Spacer
