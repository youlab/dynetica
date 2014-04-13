/***************************************************************************
 *                        Reaction.java  -  description
 *  @author begin: Sat Mar 25 2000
 *  @author copyright: (C) 2000 by Lingchong You
 *  @author Department of Chemical Engineering
 *  @author University of Wisconsin-Madison
 *  @author email: you@cae.wisc.edu
 *  @version 1.0
 */
//
// Modification & Test log:
// 8/22/2000. the basic functionality seems ok.
//  
// 8/25/2000. Now a reaction can be specified using an intuitive string
// describing the reaction.
//

//
// updated 4/20/2005
//
package dynetica.reaction;

import dynetica.entity.*;
import dynetica.system.*;
import java.util.*;

abstract public class Reaction extends Entity {
    protected Map substances = new HashMap(); // stores substances
    protected List reactants = new ArrayList();
    protected List products = new ArrayList();
    protected List catalysts = new ArrayList();
    protected List parameters = new ArrayList();

    /** Holds value of property stoichiometry. */
    private String stoichiometry;

    public Reaction() {
        super();
        stoichiometry = "";
    }

    public Reaction(String name, ReactiveSystem system) {
        super(name, system);
    }

    public Reaction(String name, ReactiveSystem system, String stoichiometry) {
        super(name, system);
        setStoichiometry(stoichiometry);
    }

    public List getReactants() {
        return reactants;
    }

    public List getProducts() {
        return products;
    }

    public List getCatalysts() {
        return catalysts;
    }

    public Set getSubstances() {
        return substances.keySet();
    }

    public boolean contains(Substance s) {
        return substances.containsKey(s);
    }

    public boolean contains(Parameter p) {
        return parameters.contains(p);
    }

    public List getParameters() {
        return parameters;
    }

    public void addParameter(Parameter p) {
        parameters.add(p);
        p.addReaction(this);
    }

    public void clearParameters() {
        for (int i = 0; i < parameters.size(); i++)
            ((Parameter) (parameters.get(i))).removeReaction(this);
        parameters.clear();
    }

    public void clearCatalysts() {
        for (int i = 0; i < catalysts.size(); i++) {
            Substance s = (Substance) (catalysts.get(i));
            s.removeReaction(this);
            substances.remove(s);
        }
        catalysts.clear();
    }

    public void clearSubstances() {
        Object[] subs = getSubstances().toArray();
        for (int i = 0; i < subs.length; i++)
            ((Substance) (subs[i])).removeReaction(this);
        substances.clear();
        reactants.clear();
        products.clear();
        catalysts.clear();
    }

    public void remove(Parameter p) {
        p.removeReaction(this);
        parameters.remove(p);
    }

    /**
     * removes a substance
     */
    public void remove(Substance s) {
        substances.remove(s);
        products.remove(s);
        reactants.remove(s);
        catalysts.remove(s);
    }

    public void addSubstance(Substance s, double coefficient) {
        this.addSubstance(s, new Double(coefficient));
    }

    public void addSubstance(Substance s, Double coefficient) {
        substances.put(s, coefficient);
        if (coefficient.doubleValue() < 0.0)
            reactants.add(s);
        else if (coefficient.doubleValue() > 0.0)
            products.add(s);
        else
            catalysts.add(s);

        // Revised 5/7/2005 for implementing the optimized direct method.
        // only add this reaction to the substance if the substance is
        // a reactant or catalyst, such that changes in the substance
        // will affect the reaction prospensity of this reactions.
        //
        //
        // if (coefficient.doubleValue() <= 0.0) s.addReaction(this);
        s.addReaction(this);
    }

    public double getRate() {
        return 0.0;
    }

    /**
     * Update the concentration of all the substances of a reaction based on the
     * time step and the rate of the reaction
     */
    // abstract public void update(double dt) throws
    // RateExpressionNotSetupException;

    /**
     * Update the reaction by one molecular event. Consider the following
     * reaction: A (10) + B (10) -> C (20) with the numbers of molecules
     * indicated in the parentheses. After one firing of the reaction, the
     * system becomes: A (9) + B (9) -> C (21).
     * 
     * This function is probably only useful when we are using stochastic
     * algorithms, such as the one proposed by Gillespie, to simulate the
     * coupled chemical reactions.
     */
    public void fire() {
        Iterator itr = substances.keySet().iterator();
        while (itr.hasNext()) {
            Substance s = (Substance) (itr.next());
            s.setValue(s.getValue() + getCoefficient(s));
        }
    }

    public void setCoefficient(Substance s, double c) {
        if (substances.containsKey(s))
            substances.put(s, new Double(c));
    }

    public double getCoefficient(Substance s) {
        return ((Double) (substances.get(s))).doubleValue();
    }

    /**
     * returns the iterator of the reactants
     * 
     * @return iterator of the reactants
     */
    public Iterator rIterator() {
        return reactants.iterator();
    }

    /**
     * returns the iterator of the reactants
     * 
     * @return iterator of the reactants
     */
    public Iterator pIterator() {
        return products.iterator();
    }

    /**
     * returns the iterator of the catalysts
     * 
     * @return iterator of the reactants
     */
    public Iterator cIterator() {
        return catalysts.iterator();
    }

    public Iterator iterator() {
        return substances.keySet().iterator();
    }

    /**
     * prints out Reaction in an intuitive way
     */
    public String getFormulaString() {
        Iterator ritr = reactants.iterator();
        Iterator pitr = products.iterator();
        //
        // Modified by L. You on 2/9/2001.
        // Output fully specified reaction.
        //
        //
        StringBuffer tmp = new StringBuffer();

        //
        // prints out the reactants first
        //
        // note that the first substance doesn't have a preceeding "+" sign.
        // also substances with coefficients equal to 1.0 will not be preceeded
        // by a number.
        //

        if (!substances.isEmpty()) {
            if (ritr.hasNext()) {
                Substance s = (Substance) ritr.next();
                double d = -getCoefficient(s);
                if (d != 1.0)
                    tmp.append(d);
                tmp.append(s.getName());
            }

            while (ritr.hasNext()) {
                Substance s = (Substance) ritr.next();
                tmp.append(" + ");
                double d = -getCoefficient(s);
                if (d != 1.0)
                    tmp.append(d);
                tmp.append(s.getName());
            }

            tmp.append(" -> ");

            //
            // then prints out the products.
            //
            if (pitr.hasNext()) {
                Substance s = (Substance) pitr.next();
                double d = getCoefficient(s);
                if (d != 1.0)
                    tmp.append(d);
                tmp.append(s.getName());
            }

            while (pitr.hasNext()) {
                Substance s = (Substance) pitr.next();
                tmp.append("+");
                double d = getCoefficient(s);
                if (d != 1.0)
                    tmp.append(d);
                tmp.append(s.getName());
            }

        }
        return tmp.toString();
    }

    /**
     * Getter for property stoichiometry.
     * 
     * @return Value of property stoichiometry.
     */
    public String getStoichiometry() {
        return getFormulaString();
    }

    /**
     * Setter for property stoichiometry.
     * 
     * @param stoichiometry
     *            New value of property stoichiometry.
     */
    public void setStoichiometry(String stoichiometry) {
        if (getSystem() != null)
            getSystem().fireSystemStateChange();

        // System.out.println(stoichiometry);
        if (this.stoichiometry != null) {
            if (this.stoichiometry.compareTo(stoichiometry.trim()) != 0) {
                String oldStoichiometry = this.stoichiometry;
                this.stoichiometry = stoichiometry.trim();
            }
        }
        this.stoichiometry = stoichiometry.trim();
        this.clearSubstances();
        ReactionParser reactionParser = new ReactionParser(this.stoichiometry);
        Iterator itr = reactionParser.getStoichiometry().keySet().iterator();
        // System.out.println(reactionParser);
        while (itr.hasNext()) {
            String substanceName = (String) itr.next();
            Double coefficient = (Double) reactionParser.getStoichiometry()
                    .get(substanceName);
            //
            // if the super system has a substance with the given name, use
            // that substance.
            //
            // System.out.println(substanceName);
            if (getSystem().contains(substanceName))
                addSubstance(getSystem().getSubstance(substanceName),
                        coefficient);
            //
            // if not, creat a substance with that name and insert into the
            // reaction
            // as well as into the system.
            //
            else {
                System.out.println("Warning:" + substanceName
                        + " not found in system " + getSystem().getName()
                        + NEWLINE + "New substance with this name is created.");

                Substance substance = new Substance(substanceName, getSystem());
                addSubstance(substance, coefficient);
            }
        }
    }

    public void clear() {
        clearSubstances();
        clearParameters();
    }

    public dynetica.gui.visualization.AbstractNode getNode() {
        if (this.node != null)
            return this.node;
        return new dynetica.gui.visualization.RNode(this);
    }

}
