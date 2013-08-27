/**
 * RNASplicingSite.java
 *
 *
 * Created: Thu Aug 24 00:02:45 2000
 *
 * @author Lingchong You
 * @version
 */

package dynetica.entity;

import dynetica.system.Genome;

public class RNASplicingSite extends GeneticElement {
    private static int rssIndex = 0;

    public RNASplicingSite() {
        this("RNA_Splicing_Site" + rssIndex++, null);
    }

    public RNASplicingSite(String name, Genome genome) {
        this(name, genome, 0, 0);
    }

    public RNASplicingSite(String name, Genome genome, int start, int end) {
        super(name, genome, start, end);
    }

} // RNASplicingSite
