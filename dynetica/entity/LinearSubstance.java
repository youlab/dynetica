/***************************************************************************
                          LinearSubstance.java  -  description
                             -------------------
    begin                : Mon Mar 22 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/

package dynetica.entity;

import dynetica.system.*;
import java.util.*;

public class LinearSubstance extends Substance implements Range {

    private int start;
    private int end;
    private int effectiveLength;
    private boolean insideCell;

    /** Holds value of property realStart. */
    private int realStart;
    /** Holds value of property realEnd. */
    private int realEnd;
    /** Holds value of property numTotalUnits. */
    private double numTotalUnits;

    LinearSubstance() {
        // this("LinearSubstance" + entityIndex++);
    }

    LinearSubstance(String name) {
        this(name, null);
    }

    LinearSubstance(String name, AbstractSystem system) {
        this(name, system, 0, 0);
    }

    LinearSubstance(String name, AbstractSystem system, int start, int end) {
        super(name, system);
        this.start = start;
        this.end = end;
        this.insideCell = false;
        this.effectiveLength = 0;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        if (this.start != start) {
            this.start = start;
            if (getSystem() != null)
                getSystem().fireSystemStateChange();
        }
    }

    public void align(int start) {
        this.start = start;
        this.end += start - this.start;
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public void setRange(int start, int end) {
        this.start = start;
        this.end = end;
        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public int getLength() {
        return end - start + 1;
    }

    public int getEffectiveLength() {
        return effectiveLength;
    }

    public void setEffectiveLength(int efflen) {
        this.effectiveLength = efflen;
    }

    public boolean overlaps(Range range) {
        int len1 = getLength();
        int len2 = range.getLength();
        return ((len1 + len2) > (range.getEnd() - getStart() + 1) && (len1 + len2) > (getEnd()
                - range.getStart() + 1));
    }

    public boolean isWithin(Range range) {
        return ((getStart() >= range.getStart()) && (getLength() <= range
                .getLength()));
    }

    public final boolean contains(Range range) {
        return ((getStart() <= range.getStart()) && (getLength() >= range
                .getLength()));
    }

    public boolean isInsideCell() {
        return insideCell;
    }

    public void setInsideCell(boolean inside) {
        this.insideCell = inside;
    }

    /**
     * Getter for property realStart.
     * 
     * @return Value of property realStart.
     */
    public int getRealStart() {
        return realStart;
    }

    /**
     * Setter for property realStart.
     * 
     * @param realStart
     *            New value of property realStart.
     */
    public void setRealStart(int realStart) {
        this.realStart = realStart;
    }

    /**
     * Getter for property realEnd.
     * 
     * @return Value of property realEnd.
     */
    public int getRealEnd() {
        return realEnd;
    }

    /**
     * Setter for property realEnd.
     * 
     * @param realEnd
     *            New value of property realEnd.
     */
    public void setRealEnd(int realEnd) {
        this.realEnd = realEnd;
    }

    /**
     * Getter for property numTotalUnits.
     * 
     * @return Value of property numTotalUnits.
     */
    public double getNumTotalUnits() {
        return numTotalUnits;
    }

    /**
     * Setter for property numTotalUnits.
     * 
     * @param numTotalUnits
     *            New value of property numTotalUnits.
     */
    public void setNumTotalUnits(double numTotalUnits) {
        this.numTotalUnits = numTotalUnits;
    }
}
