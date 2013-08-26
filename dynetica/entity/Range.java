/***************************************************************************
 *                        Range.java  -  description
 *  @author begin: Mon Mar 18 2000
 *  @author copyright: (C) 2000 by Lingchong You
 *  @author Department of Chemical Engineering
 *  @author University of Wisconsin-Madison
 *  @author email: you@cae.wisc.edu
 *  @version 0.01
 */
 /**   This program is free software; you can redistribute it and/or modify 
  *   it under the terms of the GNU General Public License as published by 
  *   the Free Software Foundation; either version 2 of the License, or   
  *   (at your option) any later version.                                
  */
package dynetica.entity;
public interface Range {
    /**
     * provides a common interface for all objects which can be specified by
     * a start point and an end point.      
     */

    /**
     * gets the start point of the range.
     */
    int getStart();

    /**
     * sets the start point of the range, with the end point fixed.
     */
    void setStart(int start);

    /**
     * aligns the range with the start point reset to a new position
     * (optional)
     */
    void align(int start); 

    /**
     * gets the end point of the range.
     */
    int getEnd();

    /**
     * sets the end point of the range, with the start point fixed.
     */
    void setEnd(int end);

    /**
     * gets the length of the range.
     */
    int getLength();

    /**
     * returns true if this range is covered by r.
     */
    boolean isWithin(Range r);

    /**
     * returns true is <it> this <\it> contains r.
     */    
    boolean contains(Range r);

    /**
     * returns true if this range overlaps with r.
     */
    boolean overlaps(Range r);
}
