/**
 * ReactionParser.java
 *
 *
 * Created: Thu Aug 25 21:53:44 2000
 *
 * @author Lingchong You
 * @version 1.0
 */

// Rewrite: Fri July 6 23:17:00 2001
// by Apirak Hoonlor
// Last work:
// add the ability to parse scientific notation display on process function.
// 

package dynetica.reaction;

import dynetica.entity.*;
import java.util.*;

public class ReactionParser {
    Map stoichiometry = new HashMap();
    /**
     * represents the string or character that separates the reactants and
     * products.
     */
    String separator;

    /** Holds value of property reactionEquation. */
    private String reactionEquation;

    public ReactionParser(String reactionEquation, String separator) {
        this.reactionEquation = reactionEquation;
        this.separator = separator;
        parseReaction();
    }

    public ReactionParser(String reactionEquation) {
        this.reactionEquation = reactionEquation;
        this.separator = "->";
        parseReaction();
    }

    public Map getStoichiometry() {
        return stoichiometry;
    }

    public void parseReaction() {
        // clear the map before doing anything.
        if (!stoichiometry.isEmpty())
            stoichiometry.clear();
        int indSeparator = reactionEquation.indexOf(separator);
        String rString = (reactionEquation.substring(0, indSeparator)).trim();
        String pString = (reactionEquation.substring(indSeparator
                + separator.length())).trim();
        if (rString.length() > 0)
            process(rString, true);
        if (pString.length() > 0)
            process(pString, false);
    }

    private void process(String string, boolean isReactants) {
       
        // Initialization of variables
        
        StringTokenizer strTokens = new StringTokenizer(string, "+");
        String substanceName = new String();
        String tempName = new String();
        String testSt = new String();
        
        double powerNumber = 0.0;
        double coefficient = 1.0;
        int tempIndex = 0;
        int i = 0;
        boolean noSpace = false;
        boolean e_asSubst = false;
        String element = new String();
        String subSt_next = new String();
        
        
        while (strTokens.hasMoreTokens()) {
            
            i = 0;
            if (!e_asSubst) {
                element = strTokens.nextToken();
            } 
            else {
                element = subSt_next;
            }

            while (element.charAt(i) == ' ')    // Excludes unnecessary spaces in the beginning
                i++;
            element = element.substring(i);
            
            int index = 0;
            
            //
            // if the string starts with a letter, the entire string
            // is considered as the name of the substance
            // and its reaction coefficient is considered to be 1.0
            // Deletes whitespaces at front and back of element
            
            if (Character.isLetter(element.charAt(0))) {
                substanceName = element.trim();
                coefficient = 1.0;
            }

            else if (Character.isDigit(element.charAt(0))) {
                //
                // find the index of the first character that is not a number or
                // a dicimal point
                //
                index++;
                
                while (Character.isDigit(element.charAt(index)) || element.charAt(index) == '.')
                    index++;
                
                coefficient = Double.parseDouble(element.substring(0, index)); // Gets coefficient as number in front of element

                tempName = element.substring(index);
                testSt = element.substring(index).trim();
                
                if (tempName.length() == testSt.length()) {
                    noSpace = true;
                } 
                else {
                    noSpace = false;
                }
                
                // 1
                if (tempName.length() > 1 && (tempName.charAt(0) == 'e' || tempName.charAt(0) == 'E') && tempName.charAt(1) == '-') {
                    // If "e" is used 
                    
                    tempIndex = 2;
                    
                    while (Character.isDigit(tempName.charAt(tempIndex)) || element.charAt(tempIndex) == '.')
                        tempIndex++;
                   
                    powerNumber = Double.parseDouble(tempName.substring(2, tempIndex));
                    powerNumber = Math.pow(10.0, powerNumber);
                    coefficient = coefficient / powerNumber;
                    substanceName = tempName.substring(tempIndex).trim();    
                } 
                
                //2
                else if (tempName.length() > 1 && (tempName.charAt(0) == 'e' || tempName.charAt(0) == 'E') && Character.isDigit(tempName.charAt(1))) {
                    tempIndex = 1;
                    
                    while (Character.isDigit(tempName.charAt(tempIndex)) || tempName.charAt(tempIndex) == '.')
                        tempIndex++;
                    
                    powerNumber = Double.parseDouble(tempName.substring(1, tempIndex));
                    powerNumber = Math.pow(10.0, powerNumber);
                    coefficient = coefficient * powerNumber;
                    substanceName = tempName.substring(tempIndex).trim();
                } 
               
                //3
                else if (tempName.length() == 1 && (tempName.charAt(0) == 'e' || tempName.charAt(0) == 'E')) {
                    
                    if (!strTokens.hasMoreTokens())
                        substanceName = "E";
                    else {
                        subSt_next = strTokens.nextToken();
                        
                        if (Character.isDigit(subSt_next.charAt(0))) {
                            tempIndex = 0;
                            
                            while (Character.isDigit(subSt_next.charAt(tempIndex)) || subSt_next.charAt(tempIndex) == '.')
                                tempIndex++;
                            
                            powerNumber = Double.parseDouble(subSt_next.substring(0, tempIndex));
                            powerNumber = Math.pow(10.0, powerNumber);
                            coefficient = coefficient * powerNumber;
                            substanceName = subSt_next.substring(tempIndex).trim();
                        } 
                        else {
                            e_asSubst = true;
                            substanceName = "E";
                        }
                    }
                } 
                
                //4
                else {
                    substanceName = tempName.trim();
                }
            } 
            
            else {
                System.out.println("Illegal reaction format");
            }
            

            if (isReactants)
                coefficient = -coefficient;
            //
            // Empty names are not counted.
            //
            if (substanceName.length() > 0)
                stoichiometry.put(substanceName, new Double(coefficient));
        }
    }

    public String toString() {
        StringBuffer str = new StringBuffer("");
        Iterator itr = stoichiometry.keySet().iterator();
        while (itr.hasNext()) {
            String name = (String) itr.next();
            str.append(name + ":" + stoichiometry.get(name) + "\t");
        }
        return str.toString();
    }

    /**
     * Getter for property reactionEquation.
     * 
     * @return Value of property reactionEquation.
     */
    public String getReactionEquation() {
        return reactionEquation;
    }

    /**
     * Setter for property reactionEquation.
     * 
     * @param reactionEquation
     *            New value of property reactionEquation.
     */
    public void setReactionEquation(String reactionEquation) {
        this.reactionEquation = reactionEquation;
        parseReaction();
    }

    public static class Tester {
        public static void main(String args[]) {
            ReactionParser p = new ReactionParser("2H2O -> 2H2 + O2");
            System.out.println(p);
        }

    }
} // ReactionParser
