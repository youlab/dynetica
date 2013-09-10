package dynetica.system;

import dynetica.gui.visualization.NetworkLayout;
import dynetica.gui.visualization.AbstractNode;
import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.algorithm.*;
import dynetica.event.SystemStructureChangeEvent;
import dynetica.event.SystemStructureChangeListener;
import dynetica.reaction.*;
import dynetica.gui.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.*;

/**
 * 
 * @author Kanishk Asthana Created April 2013
 */
public abstract class AbstractModule extends ReactiveSystem {

    public static int index = 0;
    private dynetica.gui.systems.AbstractModuleSystemGraph graph;
    private boolean popout = false;
    private boolean showConnections = false;
    private boolean showImmigrants = true;
    private DefaultMutableTreeNode supersubstanceNode;
    private DefaultMutableTreeNode superparameterNode;
    private DefaultMutableTreeNode superexpressionNode;
    private DefaultMutableTreeNode superreactionNode;
    private DefaultMutableTreeNode superprogressiveNode;
    private DefaultMutableTreeNode superequilibratedNode;

    public AbstractModule() {
        this.remove(timer.getName());
        timer = null;
    }

    public void initializeModule() {

    }

    public AbstractModule(String name) {
        super(name);
        this.remove(timer.getName());
        timer = null;
    }

    public boolean isPopedOut() {
        return popout;
    }

    public void setPopOut(boolean pop) {
        this.popout = pop;
    }

    public boolean showConnections() {
        return showConnections;
    }

    public void setConnections(boolean connections) {
        this.showConnections = connections;
    }

    public boolean showImmigrants() {
        return showImmigrants;
    }

    public void setImmigrantsVisible(boolean show) {
        this.showImmigrants = show;
    }

    @Override
    public javax.swing.JPanel editor() {
        return new dynetica.gui.systems.AbstractModuleSystemEditor(this);
    }

    @Override
    public AbstractNode getNode() {
        if (this.node != null)
            return this.node;
        return new dynetica.gui.visualization.MNode(this);
    }

    public void addToSuperSystem(Entity entity) {
        if (!(entity instanceof NetworkLayout)
                && !(entity instanceof Annotation)) {
            this.getSystem().add(entity);
        }

    }

    @Override
    public void remove(String name) {
        if (this.contains(name)) {

            Entity entity = (Entity) get(name);
            entities.remove(name);
            if (entity instanceof Substance) {
                substances.remove(entity);
                System.out.println("removing" + name);
                if (entity instanceof ExpressionVariable)
                    expressions.remove(entity);
            }

            else if (entity instanceof Progressive) {
                progressiveReactions.remove(entity);
            }

            else if (entity instanceof Equilibrated) {
                equilibratedReactions.remove(entity);
            }

            else {
                parameters.remove(entity);
            }

            if (entity.getSystem().equals(this)) {
                try {
                    removeEntityFromTreeModel(entity);
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
                if (getSystem() != null)
                    getSystem().remove(name);
            }

            fireSystemStructureChange();

        }
    }

    @Override
    public void add(Entity entity) {
        if (!(this.contains(entity.getName()))) {

            entities.put(entity.getName(), entity);
            // Code to add entity to supersystem and check for redundancies

            if (entity instanceof Substance) {
                substances.add(entity);
                // note that ExpressionVariable is a special class of Substance
                if (entity instanceof ExpressionVariable) {
                    expressions.add(entity);
                }
            }

            else if (entity instanceof Progressive) {
                progressiveReactions.add(entity);
            }

            else if (entity instanceof Equilibrated) {
                equilibratedReactions.add(entity);
            }

            else if (entity instanceof Parameter) {
                parameters.add(entity);
            }

            //
            // the following else if statement is added 4/20/2005
            else if (entity instanceof NetworkLayout) {
                networkLayout = (NetworkLayout) entity;
            } else if (entity instanceof Annotation) {
                systemInformation = (Annotation) entity;
            }
            if (entity.getSystem().equals(this)) {
                try {
                    insertEntityIntoTreeModel(entity);
                } catch (Exception moduleTreeExp) {
                    moduleTreeExp.printStackTrace();
                }
            }
            fireSystemStructureChange();

            if (getSystem() != null)// This condition is essential!
            {
                addToSuperSystem(entity);
            }
        }
    }

    public void setGraph(dynetica.gui.systems.AbstractModuleSystemGraph graph) {
        this.graph = graph;
    }

    public dynetica.gui.systems.AbstractModuleSystemGraph getAbstractModuleGraph() {
        return this.graph;
    }

    public void addEntitiesFromSuperSystem() {

        java.util.List substances = ((ModularSystem) getSystem())
                .getSubstances();
        java.util.List progressiveReactions = ((ModularSystem) getSystem())
                .getProgressiveReactions();
        java.util.List equilibratedReactions = ((ModularSystem) getSystem())
                .getEquilibratedReactions();
        java.util.List parameters = ((ModularSystem) getSystem())
                .getParameters();

        for (int i = 0; i < substances.size(); i++) {
            Entity e = (Entity) substances.get(i);
            add(e);
        }

        for (int i = 0; i < progressiveReactions.size(); i++) {
            Entity e = (Entity) progressiveReactions.get(i);
            add(e);
        }

        for (int i = 0; i < equilibratedReactions.size(); i++) {
            Entity e = (Entity) equilibratedReactions.get(i);
            add(e);
        }

        for (int i = 0; i < parameters.size(); i++) {
            Entity e = (Entity) parameters.get(i);
            add(e);
        }

    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(getFullName().toString() + " {"
                + NEWLINE);
        str.append(NEWLINE);
        str.append(systemInformation.getCompleteInfo());
        str.append(NEWLINE);

        for (int i = 0; i < substances.size(); i++) {
            Substance s = ((Substance) substances.get(i));
            if (!(s instanceof ExpressionVariable)
                    && s.getSystem().equals(this)) {
                str.append(s.getCompleteInfo()).append(NEWLINE);
            }
        }

        str.append(NEWLINE);
        for (int i = 0; i < parameters.size(); i++) {
            Parameter p = ((Parameter) parameters.get(i));
            if (p.getSystem().equals(this))
                str.append(p.getCompleteInfo()).append(NEWLINE);
        }

        str.append(NEWLINE);

        for (int i = 0; i < expressions.size(); i++) {
            ExpressionVariable ev = ((ExpressionVariable) expressions.get(i));
            if (ev.getSystem().equals(this))
                str.append(ev.getCompleteInfo()).append(NEWLINE);
        }

        str.append(NEWLINE);

        for (int i = 0; i < progressiveReactions.size(); i++) {
            Reaction reaction = (Reaction) progressiveReactions.get(i);
            if (reaction.getSystem().equals(this))
                str.append(reaction).append(NEWLINE);
        }

        for (int i = 0; i < equilibratedReactions.size(); i++) {
            Reaction reaction = (Reaction) equilibratedReactions.get(i);
            if (reaction.getSystem().equals(this))
                str.append(reaction).append(NEWLINE);
        }

        str.append(networkLayout.getCompleteInfo()).append(NEWLINE);
        str.append("}").append(NEWLINE);
        return str.toString();
    }

    public void clearModule() {
        Object[] entityArray = this.entities.values().toArray();
        int size = entityArray.length;
        for (int i = 0; i < size; i++) {
            Entity en = (Entity) entityArray[i];
            remove(en.getName());

            if (en.getSystem().equals(this))
                en.setSystem(this.getSystem());

        }
    }

    // Kanishk Asthana 22 Aug 2013: Idea to consider: why not use the method
    // Math.max() instead of Math.min()
    // when inserting nodes into the treeModel! I suspect Math.min() might cause
    // some problems in the future.
    @Override
    public boolean insertEntityIntoTreeModel(Entity entity) {
        if (entity instanceof Substance) {
            //
            // the following if statement is added 4/19/05 by LY
            //
            if (entity instanceof ExpressionVariable) {
                if (expressionNode == null) {
                    expressionNode = new DefaultMutableTreeNode("Expressions");
                    treeModel.insertNodeInto(expressionNode, systemNode,
                            Math.min(systemNode.getChildCount(), 2));
                }

                if (superexpressionNode == null) {
                    if (getSystem() instanceof ModularSystem) {
                        superexpressionNode = new DefaultMutableTreeNode(
                                "Expressions");
                        ModularSystem superSystem = (ModularSystem) getSystem();
                        if (superSystem.getModuleNodesMap().get(this) != null) {
                            DefaultMutableTreeNode supersystemNode = (DefaultMutableTreeNode) superSystem
                                    .getModuleNodesMap().get(this);
                            ((DefaultTreeModel) superSystem.getTreeModel())
                                    .insertNodeInto(superexpressionNode,
                                            supersystemNode,
                                            Math.min(supersystemNode
                                                    .getChildCount(), 2));
                        }
                    }
                }

                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        expressionNode, expressionNode.getChildCount());
                if (superexpressionNode != null) {
                    ((DefaultTreeModel) ((ModularSystem) getSystem())
                            .getTreeModel()).insertNodeInto(
                            new DefaultMutableTreeNode(entity.getName()),
                            superexpressionNode,
                            superexpressionNode.getChildCount());
                }
                return true;
            } else {
                if (substanceNode == null) {
                    substanceNode = new DefaultMutableTreeNode("Substances");
                    treeModel.insertNodeInto(substanceNode, systemNode,
                            Math.min(systemNode.getChildCount(), 0));
                }

                if (supersubstanceNode == null) {
                    if (getSystem() instanceof ModularSystem) {
                        supersubstanceNode = new DefaultMutableTreeNode(
                                "Substances");
                        ModularSystem superSystem = (ModularSystem) getSystem();
                        if (superSystem.getModuleNodesMap().get(this) != null) {
                            DefaultMutableTreeNode supersystemNode = (DefaultMutableTreeNode) superSystem
                                    .getModuleNodesMap().get(this);
                            ((DefaultTreeModel) superSystem.getTreeModel())
                                    .insertNodeInto(supersubstanceNode,
                                            supersystemNode,
                                            Math.min(supersystemNode
                                                    .getChildCount(), 0));
                        }
                    }
                }

                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        substanceNode, substanceNode.getChildCount());
                if (supersubstanceNode != null) {
                    ((DefaultTreeModel) ((ModularSystem) getSystem())
                            .getTreeModel()).insertNodeInto(
                            new DefaultMutableTreeNode(entity.getName()),
                            supersubstanceNode,
                            supersubstanceNode.getChildCount());
                }

                return true;
            }
        }
        if (entity instanceof Reaction) {
            if (reactionNode == null) {
                reactionNode = new DefaultMutableTreeNode("Reactions");
                treeModel.insertNodeInto(reactionNode, systemNode,
                        Math.min(systemNode.getChildCount(), 3));

            }
            if (superreactionNode == null) {
                if (getSystem() instanceof ModularSystem) {
                    superreactionNode = new DefaultMutableTreeNode("Reactions");
                    ModularSystem superSystem = (ModularSystem) getSystem();
                    if (superSystem.getModuleNodesMap().get(this) != null) {
                        DefaultMutableTreeNode supersystemNode = (DefaultMutableTreeNode) superSystem
                                .getModuleNodesMap().get(this);
                        ((DefaultTreeModel) superSystem.getTreeModel())
                                .insertNodeInto(
                                        superreactionNode,
                                        supersystemNode,
                                        Math.min(
                                                supersystemNode.getChildCount(),
                                                3));
                    }
                }
            }
            if (entity instanceof Progressive) {
                if (progressiveNode == null) {
                    progressiveNode = new DefaultMutableTreeNode(
                            "Progressive Reactions");
                    treeModel.insertNodeInto(progressiveNode, reactionNode,
                            Math.min(reactionNode.getChildCount(), 0));
                }

                if (superprogressiveNode == null && superreactionNode != null) {
                    if (getSystem() instanceof ModularSystem) {
                        superprogressiveNode = new DefaultMutableTreeNode(
                                "Progressive Reactions");
                        ModularSystem superSystem = (ModularSystem) getSystem();
                        ((DefaultTreeModel) superSystem.getTreeModel())
                                .insertNodeInto(superprogressiveNode,
                                        superreactionNode, Math.min(
                                                superreactionNode
                                                        .getChildCount(), 0));
                    }
                }

                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        progressiveNode, progressiveNode.getChildCount());
                if (superprogressiveNode != null) {
                    ((DefaultTreeModel) ((ModularSystem) getSystem())
                            .getTreeModel()).insertNodeInto(
                            new DefaultMutableTreeNode(entity.getName()),
                            superprogressiveNode,
                            superprogressiveNode.getChildCount());
                }

                return true;
            }

            if (entity instanceof Equilibrated) {
                if (equilibratedNode == null) {
                    equilibratedNode = new DefaultMutableTreeNode(
                            "Equilibrated Reactions");
                    treeModel.insertNodeInto(equilibratedNode, reactionNode,
                            Math.min(reactionNode.getChildCount(), 1));
                }
                if (superequilibratedNode == null && superreactionNode != null) {
                    if (getSystem() instanceof ModularSystem) {
                        superequilibratedNode = new DefaultMutableTreeNode(
                                "Equilibrated Reactions");
                        ModularSystem superSystem = (ModularSystem) getSystem();
                        ((DefaultTreeModel) superSystem.getTreeModel())
                                .insertNodeInto(superequilibratedNode,
                                        superreactionNode, Math.min(
                                                superreactionNode
                                                        .getChildCount(), 1));
                    }
                }

                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        equilibratedNode, equilibratedNode.getChildCount());

                if (superequilibratedNode != null) {
                    ((DefaultTreeModel) ((ModularSystem) getSystem())
                            .getTreeModel()).insertNodeInto(
                            new DefaultMutableTreeNode(entity.getName()),
                            superequilibratedNode,
                            superequilibratedNode.getChildCount());
                }
                return true;
            }
        }

        if (entity instanceof Parameter) {
            if (parameterNode == null) {
                parameterNode = new DefaultMutableTreeNode("Parameters");
                treeModel.insertNodeInto(parameterNode, systemNode,
                        Math.min(systemNode.getChildCount(), 1));
            }

            if (superparameterNode == null) {
                if (getSystem() instanceof ModularSystem) {
                    superparameterNode = new DefaultMutableTreeNode(
                            "Parameters");
                    ModularSystem superSystem = (ModularSystem) getSystem();
                    if (superSystem.getModuleNodesMap().get(this) != null) {
                        DefaultMutableTreeNode supersystemNode = (DefaultMutableTreeNode) superSystem
                                .getModuleNodesMap().get(this);
                        ((DefaultTreeModel) superSystem.getTreeModel())
                                .insertNodeInto(
                                        superparameterNode,
                                        supersystemNode,
                                        Math.min(
                                                supersystemNode.getChildCount(),
                                                1));
                    }
                }
            }
            treeModel.insertNodeInto(
                    new DefaultMutableTreeNode(entity.getName()),
                    parameterNode, parameterNode.getChildCount());

            if (superparameterNode != null) {
                ((DefaultTreeModel) ((ModularSystem) getSystem())
                        .getTreeModel()).insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        superparameterNode, superparameterNode.getChildCount());
            }

            return true;
        }

        else
            return false;
    }

    @Override
    public boolean removeEntityFromTreeModel(Entity entity) {
        Enumeration e;
        Enumeration supere = null;
        if (entity instanceof Substance) {
            if (entity instanceof ExpressionVariable) {
                e = expressionNode.children();
                if (superexpressionNode != null)
                    supere = superexpressionNode.children();
            } else {
                e = substanceNode.children();
                if (supersubstanceNode != null)
                    supere = supersubstanceNode.children();
            }

        }

        else if (entity instanceof Progressive) {
            {
                e = progressiveNode.children();
                if (superprogressiveNode != null)
                    supere = superprogressiveNode.children();
            }
        }

        else if (entity instanceof Equilibrated) {
            {
                e = equilibratedNode.children();
                if (superequilibratedNode != null)
                    supere = superequilibratedNode.children();
            }
        }

        else if (entity instanceof Parameter) {
            {
                e = parameterNode.children();
                if (superparameterNode != null)
                    supere = superparameterNode.children();
            }
        } else
            return false;

        return removeEntityFromModularTree(e, entity)
                && removeEntityFromSuperTree(supere, entity);

    }

    public boolean removeEntityFromModularTree(Enumeration e, Entity entity) {

        DefaultMutableTreeNode node;
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            String nodeName = node.getUserObject().toString();
            if (nodeName.compareTo(entity.getName()) == 0) {
                TreeNode[] pathToRoot = treeModel.getPathToRoot(node);
                //
                // remove the node first
                //
                treeModel.removeNodeFromParent(node);

                //
                // following the path to the root, remove all nodes that doesn't
                // contain
                // a child. Reset the major nodes to null if necessary.
                //
                for (int i = pathToRoot.length - 2; i > 0; i--) {
                    if (pathToRoot[i].getChildCount() == 0) {
                        treeModel
                                .removeNodeFromParent((DefaultMutableTreeNode) pathToRoot[i]);
                        if (substanceNode == pathToRoot[i])
                            substanceNode = null;
                        else if (expressionNode == pathToRoot[i])
                            expressionNode = null;
                        else if (reactionNode == pathToRoot[i])
                            reactionNode = null;
                        else if (progressiveNode == pathToRoot[i])
                            progressiveNode = null;
                        else if (equilibratedNode == pathToRoot[i])
                            equilibratedNode = null;
                        else if (parameterNode == pathToRoot[i])
                            parameterNode = null;
                    } else
                        return true;
                }
                return true;
            }
        }
        return false;
    }

    public boolean removeEntityFromSuperTree(Enumeration supere, Entity entity) {

        DefaultMutableTreeNode node;
        if (supere != null) {
            while (supere.hasMoreElements() && getSystem() != null) {
                ModularSystem superSystem = (ModularSystem) getSystem();
                node = (DefaultMutableTreeNode) supere.nextElement();
                String nodeName = node.getUserObject().toString();
                if (nodeName.compareTo(entity.getName()) == 0) {
                    TreeNode[] pathToRoot = ((DefaultTreeModel) superSystem
                            .getTreeModel()).getPathToRoot(node);
                    //
                    // remove the node first
                    //
                    ((DefaultTreeModel) superSystem.getTreeModel())
                            .removeNodeFromParent(node);
                    //
                    // following the path to the root, remove all nodes that
                    // doesn't contain
                    // a child. Reset the major nodes to null if necessary.
                    //
                    for (int i = pathToRoot.length - 2; i > 0; i--) {
                        if (pathToRoot[i].getChildCount() == 0) {
                            ((DefaultTreeModel) superSystem.getTreeModel())
                                    .removeNodeFromParent((DefaultMutableTreeNode) pathToRoot[i]);
                            if (supersubstanceNode == pathToRoot[i])
                                supersubstanceNode = null;
                            else if (superexpressionNode == pathToRoot[i])
                                superexpressionNode = null;
                            else if (superreactionNode == pathToRoot[i])
                                superreactionNode = null;
                            else if (superprogressiveNode == pathToRoot[i])
                                superprogressiveNode = null;
                            else if (superequilibratedNode == pathToRoot[i])
                                superequilibratedNode = null;
                            else if (superparameterNode == pathToRoot[i])
                                superparameterNode = null;
                        } else
                            return true;
                    }
                    return true;
                }
            }
        }
        return false;
    }
    /*
     * public boolean removeEntityFromTreeModel(Entity entity) { Enumeration e;
     * Enumeration supere;
     * 
     * if (entity instanceof Substance) { if (entity instanceof
     * ExpressionVariable) {e = expressionNode.children();
     * supere=superexpressionNode.children();} else {e =
     * substanceNode.children(); supere=supersubstanceNode.children();} }
     * 
     * else if (entity instanceof Progressive) { {e =
     * progressiveNode.children(); supere=superprogressiveNode.children();} }
     * 
     * else if (entity instanceof Equilibrated) { {e =
     * equilibratedNode.children(); supere= superequilibratedNode.children();} }
     * 
     * else if (entity instanceof Parameter){ {e = parameterNode.children();
     * supere=superparameterNode.children();} } else return false;
     * 
     * DefaultMutableTreeNode node; while (supere.hasMoreElements() &&
     * getSystem()!=null) { ModularSystem superSystem= (ModularSystem)
     * getSystem(); node = (DefaultMutableTreeNode) supere.nextElement(); String
     * nodeName = node.getUserObject().toString(); if
     * (nodeName.compareTo(entity.getName()) == 0) { TreeNode[] pathToRoot =
     * ((DefaultTreeModel)superSystem.getTreeModel()).getPathToRoot(node); // //
     * remove the node first //
     * ((DefaultTreeModel)superSystem.getTreeModel()).removeNodeFromParent
     * (node); // // following the path to the root, remove all nodes that
     * doesn't contain // a child. Reset the major nodes to null if necessary.
     * // for (int i = pathToRoot.length - 2; i > 0; i--) { if
     * (pathToRoot[i].getChildCount() == 0) {
     * ((DefaultTreeModel)superSystem.getTreeModel
     * ()).removeNodeFromParent((DefaultMutableTreeNode) pathToRoot[i]); if
     * (supersubstanceNode == pathToRoot[i]) supersubstanceNode = null; else if
     * (superexpressionNode == pathToRoot[i]) superexpressionNode = null; else
     * if (superreactionNode == pathToRoot[i]) superreactionNode = null; else if
     * (superprogressiveNode == pathToRoot[i]) superprogressiveNode = null; else
     * if (superequilibratedNode == pathToRoot[i]) superequilibratedNode = null;
     * else if (superparameterNode == pathToRoot[i]) superparameterNode = null;
     * } else return true; } return true; } }
     * 
     * while (e.hasMoreElements()) { node = (DefaultMutableTreeNode)
     * e.nextElement(); String nodeName = node.getUserObject().toString(); if
     * (nodeName.compareTo(entity.getName()) == 0) { TreeNode[] pathToRoot =
     * treeModel.getPathToRoot(node); // // remove the node first //
     * treeModel.removeNodeFromParent(node);
     * 
     * // // following the path to the root, remove all nodes that doesn't
     * contain // a child. Reset the major nodes to null if necessary. // for
     * (int i = pathToRoot.length - 2; i > 0; i--) { if
     * (pathToRoot[i].getChildCount() == 0) {
     * treeModel.removeNodeFromParent((DefaultMutableTreeNode) pathToRoot[i]);
     * if (substanceNode == pathToRoot[i]) substanceNode = null; else if
     * (expressionNode == pathToRoot[i]) expressionNode = null; else if
     * (reactionNode == pathToRoot[i]) reactionNode = null; else if
     * (progressiveNode == pathToRoot[i]) progressiveNode = null; else if
     * (equilibratedNode == pathToRoot[i]) equilibratedNode = null; else if
     * (parameterNode == pathToRoot[i]) parameterNode = null; } else return
     * true; } return true; } }
     * 
     * return false; }
     */

}
