
/**
 * GenomeEnd.java
 *
 *
 * Created: Thu Aug 24 00:09:26 2000
 *
 * @author Lingchong You
 * @version
 */

package dynetica.entity;
import dynetica.system.Genome;

public class GenomeEnd extends GeneticElement {
    private static int genomeEndIndex = 0;
    public GenomeEnd() {
	this("GenomeEnd" + genomeEndIndex++, null, 0, 0);
    }
    
    public GenomeEnd(String name, Genome system) {
	super(name, system);
    }
    
    public GenomeEnd(String name, Genome system, int start, int end) {
	super(name, system, start, end);
    }
    
} // GenomeEnd
