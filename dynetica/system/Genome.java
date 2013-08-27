/***************************************************************************
                          Genome.java  -  description
                             -------------------
    begin                : Mon Mar 18 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/

package dynetica.system;

import dynetica.entity.*;
import dynetica.reaction.*;
import java.io.*;
import java.util.*;

public class Genome extends SimpleSystem {
    private static int genomeIndex = 0;
    /**
     * Genome class describes how the genetic elements are organized in the
     * genome. It is often initialized from an input file which stores the
     * information about a paticular genome.
     * <p>
     */

    //
    // the Genetic Elements are stored in the Vector elements
    //

    List elements = new ArrayList();

    // List blocks = new ArrayList();
    //
    // The handles of the functional elements are additionally stored
    // separately in different lists for convenient access.
    //
    List genes = new ArrayList();
    List promoters = new ArrayList();
    List RNASplicingSites = new ArrayList();
    List terminators = new ArrayList();
    // boolean translocationSet = false;
    GenomeTranslocation translocation = null;

    /**
     * Holds value of property lengthInCell. This variable indicates the length
     * of the part of the ViralGenome that is inside the cell. It's useful in
     * situations where the viral genome entry is sufficiently slow that it
     * matters. One example is the entry of T7 genome into the host cell.
     */

    private int lengthInCell = 0;

    public Genome() {
        this("Genome" + genomeIndex++, null);
    }

    public Genome(String name, GeneticSystem system) {
        setSystem(system);
        setName(name);
    }

    public void insert(GeneticElement ge) {
        for (int i = 0; i < elements.size(); i++) {
            if (((GeneticElement) elements.get(i)).getStart() > ge.getStart()) {
                elements.add(i, ge);
                fireSystemStructureChange();
                return;
            }
        }

        elements.add(ge);
        fireSystemStructureChange();
        return;
    }

    public void add(Entity e) {
        if (!contains(e.getName())) {
            entities.put(e.getName(), e);
            GeneticElement ge = (GeneticElement) e;
            insert(ge);
            // System.out.println("Inserting " + ge.getName());
            //
            // put the element into the appropriate category
            //
            if (ge instanceof Gene)
                genes.add((Gene) ge);
            else if (ge instanceof Promoter)
                promoters.add((Promoter) ge);
            else if (ge instanceof Terminator)
                terminators.add((Terminator) ge);
            else if (ge instanceof RNASplicingSite)
                RNASplicingSites.add((RNASplicingSite) ge);
            if (getSystem() != null)
                ((GeneticSystem) getSystem()).put(ge.getLongName(), ge);
        }
    }

    /**
     * puts a genetic element in the right position in the elements list. (this
     * is called when the starting position (in bp) of the genetic element has
     * been changed.
     */
    public void adjustElementPosition(GeneticElement ge) {
        elements.remove(ge);
        insert(ge);
    }

    public final GeneticElement element(int i) {
        return (GeneticElement) elements.get(i);
    }

    public List getGenes() {
        return genes;
    }

    public List getPromoters() {
        return promoters;
    }

    public List getTerminators() {
        return terminators;
    }

    public List getRNASplicingSites() {
        return RNASplicingSites;
    }

    public String toString() {
        return getFullName();
    }

    public final String getFullGenome() {
        StringBuffer s = new StringBuffer(getFullName() + "{" + NEWLINE);
        Iterator elementItr = elements.iterator();
        // Iterator blockItr = blocks.iterator();
        while (elementItr.hasNext()) {
            GeneticElement e = (GeneticElement) elementItr.next();
            s.append(e.getCompleteInfo() + NEWLINE);
        }
        s.append("}");
        return s.toString();
    }

    protected GeneticElement lastElement() {
        return (GeneticElement) elements.get(elements.size() - 1);
    }

    public void printGenes() {
        Iterator itr = genes.iterator();
        while (itr.hasNext()) {
            System.out.println(itr.next());
        }
    }

    public List getElements() {
        return elements;
    }

    /**
     * Getter for property start.
     * 
     * @return Value of property start.
     */
    public int getStart() {
        if (elements.isEmpty())
            return 0;
        else
            return ((GeneticElement) elements.get(0)).getStart();
    }

    /**
     * Getter for property length.
     * 
     * @return Value of property length.
     */
    public int getLength() {
        return getEnd() - getStart() + 1;
    }

    public int getEnd() {
        if (elements.isEmpty())
            return -1;
        else
            return ((GeneticElement) elements.get(elements.size() - 1))
                    .getEnd();
    }

    public void remove(String name) {
        Entity e = (Entity) get(name);
        super.remove(name);
        if (getSystem() != null)
            getSystem().remove(getName() + "." + name);
        elements.remove(e);
        if (e instanceof Gene) {
            ((Gene) e).removeMe();
            genes.remove(e);
        } else if (e instanceof Promoter)
            promoters.remove(e);
        else if (e instanceof RNASplicingSite)
            RNASplicingSites.remove(e);
        else if (e instanceof Terminator)
            terminators.remove(e);
    }

    public javax.swing.JPanel editor() {
        return new dynetica.gui.systems.GenomeEditor(this);
    }

    /**
     * Getter for property lengthInCell.
     * 
     * @return Value of property lengthInCell.
     */
    public int getLengthInCell() {
        return lengthInCell;
    }

    /**
     * Setter for property lengthInCell.
     * 
     * @param lengthInCell
     *            New value of property lengthInCell.
     */
    public void setLengthInCell(int lengthInCell) {
        this.lengthInCell = lengthInCell;
    }

    public void reset() {
        super.reset();
        for (int i = 0; i < elements.size(); i++)
            ((GeneticElement) (elements.get(i))).reset();
        if (translocation != null)
            lengthInCell = 0;
    }

    /**
     * Getter for property translocation.
     * 
     * @return Value of property translocation.
     */
    public GenomeTranslocation getTranslocation() {
        return translocation;
    }

    /**
     * Setter for property translocation.
     * 
     * @param translocation
     *            New value of property translocation.
     */
    public void setTranslocation(GenomeTranslocation translocation) {
        this.translocation = translocation;
    }

    //
    // a tester of the Genome class
    //

    public static class Tester {
        /**
         * tests Genome class
         */
        public static void main(String[] args) {
            File sysFile = new File(args[0]);
            GeneticSystem gs = new GeneticSystem();
            try {
                Genome T7genome = (Genome) (SystemBuilder.build(sysFile, gs));
                System.out.println(" The length of T7 genome is "
                        + T7genome.getLength());
                System.out.println(" The number of elements is "
                        + T7genome.getElements().size());
                // System.out.println(T7genome.getFullGenome());
                System.out.println(T7genome.getStart());
                System.out.println(T7genome.getEnd());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
