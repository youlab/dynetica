/***************************************************************************
                          GeneticBlock.java  -  description
                             -------------------
    begin                : Mon Mar 18 2000
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
import java.util.*;
import dynetica.system.*;

public class GeneticBlock extends GeneticElement {
    private int wtPosition;
    private List elements = new ArrayList();

    public GeneticBlock() {
	super();
	wtPosition = 0;
    }
    
    public GeneticBlock(GeneticElement ge, int wp) {
	super(ge.getName(), ge.getGenome(), ge.getStart(),ge.getEnd());
	elements.add(ge);
	wtPosition = wp;
    }

    public final int numberOfElements() {
	return elements.size();
    }

    public final GeneticElement elementAt(int i) {
	return (GeneticElement)elements.get(i);
    }

    public final GeneticElement element(int i) {
	return (GeneticElement)elements.get(i);
    }

    public final int getWtPosition() {
	return wtPosition;
    }

    public final void append(GeneticElement ge) {
	//append a genetic GeneticElement to the block
	elements.add(ge);
	if (elements.isEmpty()) {
	    setRange(ge.getStart(),ge.getEnd());
	}
	else if (getEnd() < ge.getEnd()) {
	    setRange(getStart(),ge.getEnd());
	}
    }
    
    public final void align(int start) {
	int distance = start - getStart();
	//first update the position of each member.
	Iterator elementsItr = elements.iterator();
	while(elementsItr.hasNext()) 	    
	    ((GeneticElement)(elementsItr.next())).shift(distance);
	super.align(start);
    }

    //
    // define the output format of genetic block
    //
    
    public String toString() {
	return (getName() + " " + 
		getWtPosition() + " " + 
		getStart() + " " + 
		getEnd() + " " + 
		numberOfElements());
    }


    public static class Tester {
	public static void main(String[] args) {
	    Genome sys = new Genome();
	    GeneticElement ge1 = new GeneticElement("gp1", sys, 10, 300);
	    GeneticElement ge2 = new GeneticElement("gp2", sys, 200, 400);
	    GeneticElement ge3 = new GeneticElement( "gp3", sys, 300, 500);
	    GeneticElement ge4 = new GeneticElement( "gp4", sys, 700, 1500);
	    
	    System.out.println(ge1);
	    System.out.println(ge2);
	    System.out.println(ge3);
	    
	    GeneticBlock gb1 = new GeneticBlock(ge1, 1);
	    System.out.println("Block: "  + gb1 + 
			       " is generated from " + NEWLINE +
			       "GeneticElement: " + ge1);
	    
	    if (gb1.overlaps(ge2)) {
		gb1.append(ge2);
		System.out.println(gb1.numberOfElements());
		System.out.println("Block: "  + gb1 + 
				   " is combined by \n" +
				   "GeneticElements: " + gb1.element(0)
				   + " and "+ gb1.element(1) + "\n");
	    }
	    
	    if (gb1.overlaps(ge3)) {
		System.out.println("Block: " + gb1.getName() 
				   + "overlaps with \n"
				   + ge3);
		gb1.append(ge3);
	    }

	    System.out.println(gb1);
	    if (gb1.overlaps(ge4))
		gb1.append(ge4);
	    else
		System.out.println("Block:\n " + gb1 +"\n"
				   + " doesn't overlap with \n"
				   + ge4);
	}
    }
}
