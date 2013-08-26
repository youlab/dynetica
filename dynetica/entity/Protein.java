/***************************************************************************
                          Protein.java  -  description
                             -------------------
    begin                : Mon Mar 22 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/

/***************************************************************************
 *                                                                         *
 *   This program is free software; you can redistribute it and/or modify  *
 *   it under the terms of the GNU General Public License as published by  *
 *   the Free Software Foundation; either version 2 of the License, or     *
 *   (at your option) any later version.                                   *
 *                                                                         *
 ***************************************************************************/
package dynetica.entity;
import dynetica.system.GeneticSystem;
import java.util.*;

public class Protein extends LinearSubstance {
    private static int proteinIndex = 0;
    /**
     * Protein class
     */

    public Protein() {
        this("Protein" + proteinIndex++);
    }
    public Protein(String name) { 
        this(name, null); 
    }
    public Protein(String name, GeneticSystem system) {
        this(name, system, 0, 0);
    }
    public Protein(String name, GeneticSystem system, int start, int end) {
	super(name, system, start, end);
        setVisible(false);
    }

    public Protein(Gene g) {
	this(g.getProteinName(), g.getGeneticSystem(), g.getStart(), g.getEnd());
    }
    
}
