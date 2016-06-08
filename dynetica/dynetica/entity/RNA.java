/***************************************************************************
                           RNA.java  -  description
                             -------------------
    begin                : Mon Mar 25 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/

package dynetica.entity;

import java.util.*;
import dynetica.system.GeneticSystem;

public class RNA extends LinearSubstance {
    private static int rnaIndex = 0;
    public static final byte MESSENGER_RNA = 0;
    public static final byte TRANSFER_RNA = 1;
    public static final byte RIBOSOMAL_RNA = 2;

    // the proteins coded by this RNA if it's mRNA
    private List proteins = new ArrayList();

    /** Holds value of property type. By default, this is Messenger RNA */
    private byte rnaType = MESSENGER_RNA;

    public RNA() {
        this("RNA" + rnaIndex++, null, 0, 0, MESSENGER_RNA);
    }

    public RNA(Gene g) {
        this(g, g.getRnaName());
    }

    public RNA(Gene g, String name) {
        this(name, g.getGeneticSystem(), g.getStart(), g.getEnd(), g
                .getGeneType());
    }

    public RNA(String name, GeneticSystem system, int start, int end, byte type) {
        super(name, system, start, end);
        setRnaType(type);
    }

    /**
     * Getter for property type.
     * 
     * @return Value of property type.
     */
    public byte getRnaType() {
        return rnaType;
    }

    /**
     * Setter for property type.
     * 
     * @param type
     *            New value of property type.
     */
    public void setRnaType(byte type) {
        this.rnaType = type;
    }

    public List getProteins() {
        return proteins;
    }

    public void addProtein(Protein aProtein) {
        proteins.add(aProtein);
    }

}
