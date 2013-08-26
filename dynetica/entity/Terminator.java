/*
 * Terminator.java
 *
 * Created on April 7, 2000, 12:12 AM
 */
 
package dynetica.entity;
import dynetica.util.*;
import dynetica.system.*;

/** 
 *
 * @author  Lingchong You
 * @version 0.01
 */
public class Terminator extends TranscriptionRegulationElement {
    private static int terminatorIndex = 0;
    /** Creates new Terminator */
    public Terminator() {
        this("Terminator" + terminatorIndex++, null, 0, 0, 1.0);
    }

    public Terminator(String name, Genome genome, 
		      int start, int end, double efficiency) {
	super(name, genome, start, end, efficiency);
    }    
}
