/***************************************************************************
                          Promoter.java  -  description
                             -------------------
    begin                : Mon Mar 22 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/

package dynetica.entity;
import java.util.*;
import java.text.*;
import dynetica.system.*;

	
public class Promoter extends TranscriptionRegulationElement {
    private static int promoterIndex = 0;
    boolean rightward;
    public Promoter() { this("Promoter" + promoterIndex++, null, 0, 0, 0.0, true); }
    public Promoter(String name, Genome genome, int start, int end,
		    double efficiency, boolean isRightward) {
	super(name, genome, start, end, efficiency);
	this.rightward = isRightward;
    }
   
    public final boolean isRightward() {
	/**
	 * finds out whether the promoter is rightward
	 */
	return rightward;
    }

    public final void setRightward( boolean dir) {
	/**
	 * sets the direction of the promoter.
	 */
	this.rightward = dir;
    }
        
     public static class Tester {
	public static void main(String[] args) {
//	    Promoter p = new Promoter("PromoterX", null, 10, 20, 0.2);
//	    if (p.isRightward())
//		System.out.println(p + " is rightward");
	}

    }

}
