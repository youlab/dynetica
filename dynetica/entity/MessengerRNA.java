/***************************************************************************
                          mRNA.java  -  description
                             -------------------
    begin                : Mon Mar 22 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/

package dynetica.entity;
import java.util.*;
import dynetica.system.*;

public class MessengerRNA extends RNA {
    private List proteins = new LinkedList(); // the proteins coded by this mRNA
    
    public MessengerRNA() {}

    public MessengerRNA(Gene g) {
	super(g, g.getRnaName());
    }
    
    public MessengerRNA(String name, GeneticSystem system, int start,int end) {
        super(name, system, start, end, RNA.MESSENGER_RNA);
    }

    public final List getProteins() {
       return proteins;
    }
   
    public final void addProtein(Protein aProtein) {
	proteins.add(aProtein);
    }
}
