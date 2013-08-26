
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
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 *
 * @author Kanishk Asthana 
 * Created April 2013
 */
public abstract class AbstractModule extends ReactiveSystem{
        
    public static int index=0;
    private dynetica.gui.systems.AbstractModuleSystemGraph graph;
    private boolean popout=false;
    private boolean showConnections=false;
    
    public AbstractModule()
    {
        this.remove(timer.getName());
        timer=null;
    }
    
    public void initializeModule(){
        
    }
    
    public AbstractModule(String name)
    {
        super(name);  
        this.remove(timer.getName());
        timer=null;
    }
        
    public boolean isPopedOut(){
     return popout;
    }
    
    public void setPopOut(boolean pop){
       this.popout=pop;
    }
    
    public boolean showConnections(){
     return showConnections;
    }
    
    public void setConnections(boolean connections){
      this.showConnections=connections;
    } 
    
    @Override
    public javax.swing.JPanel editor() {
        return new dynetica.gui.systems.AbstractModuleSystemEditor(this);
    }
    
    @Override
    public AbstractNode getNode()
    {
        if (this.node!=null) return this.node;
        return new dynetica.gui.visualization.MNode(this);
    }
    
  
    public void addToSuperSystem(Entity entity) 
    {                       
      if(!(entity instanceof NetworkLayout) && !(entity instanceof Annotation))
      {
       this.getSystem().add(entity);  
      }
    
    } 
    
    @Override
    public void remove(String name) {
        if(this.contains(name))
        {
        
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
        
        if(entity.getSystem().equals(this)){
         removeEntityFromTreeModel(entity);    
         if(getSystem()!=null)
         getSystem().remove(name);
        }
        
        fireSystemStructureChange();
        
        }
   }

    @Override
    public void add(Entity entity) {
      if (!(this.contains(entity.getName()))  ) {
              
          entities.put(entity.getName(), entity);
           //Code to add entity to supersystem and check for redundancies
           
           
           if (entity instanceof Substance) {
                substances.add(entity);
                // note that ExpressionVariable is a special class of Substance
                if (entity instanceof ExpressionVariable){
                    expressions.add(entity);                }
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
            }
            else if (entity instanceof Annotation){
                systemInformation = (Annotation) entity;
            }
           if(entity.getSystem().equals(this)){ 
            try{ 
            insertEntityIntoTreeModel(entity);
            }catch(Exception moduleTreeExp){
            moduleTreeExp.printStackTrace();}
           }
            fireSystemStructureChange();
            
          if(getSystem()!=null)//This condition is essential!
         {    
          addToSuperSystem(entity);       
         }
      }
    }
    public void setGraph(dynetica.gui.systems.AbstractModuleSystemGraph graph) {
        this.graph = graph;
    }

    public dynetica.gui.systems.AbstractModuleSystemGraph getAbstractModuleGraph(){
     return this.graph;
    }
public void addEntitiesFromSuperSystem(){

    java.util.List substances= ((ModularSystem) getSystem()).getSubstances();
    java.util.List progressiveReactions=((ModularSystem)getSystem()).getProgressiveReactions();
    java.util.List equilibratedReactions=((ModularSystem)getSystem()).getEquilibratedReactions();
    java.util.List parameters=((ModularSystem)getSystem()).getParameters();
    
    for(int i=0;i<substances.size();i++)
    {
     Entity e=(Entity)substances.get(i);
     add(e);
    }
    
    for(int i=0;i<progressiveReactions.size();i++)
    {
     Entity e=(Entity)progressiveReactions.get(i);
     add(e);
    }
    
    for(int i=0;i<equilibratedReactions.size();i++)
    {
     Entity e=(Entity) equilibratedReactions.get(i);
     add(e);
    }
    
    for(int i=0;i<parameters.size();i++)
    {
     Entity e=(Entity)parameters.get(i);
     add(e);
    }

}    

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(getFullName().toString() + " {" + NEWLINE);
	str.append(NEWLINE);
        str.append(systemInformation.getCompleteInfo());
        str.append(NEWLINE);
        
        for (int i = 0; i < substances.size(); i++) {
            Substance s = ((Substance) substances.get(i));
            if (!(s instanceof ExpressionVariable) && s.getSystem().equals(this)) {
                str.append(s.getCompleteInfo()).append( NEWLINE);
            }
        }
        
	str.append(NEWLINE);
        for (int i = 0; i < parameters.size(); i++) {
            Parameter p = ((Parameter) parameters.get(i));
            if(p.getSystem().equals(this))
	    str.append(p.getCompleteInfo()).append(NEWLINE);
        }
       
	str.append(NEWLINE);
        
        for (int i = 0; i < expressions.size(); i++) {
            ExpressionVariable ev = ((ExpressionVariable) expressions.get(i));
            if(ev.getSystem().equals(this))
            str.append(ev.getCompleteInfo()).append(NEWLINE);
        }
        
        str.append(NEWLINE);
        
	for (int i = 0; i < progressiveReactions.size(); i++) {
            Reaction reaction=(Reaction) progressiveReactions.get(i);
            if(reaction.getSystem().equals(this))
             str.append(reaction).append(NEWLINE);
        }

        for (int i = 0; i < equilibratedReactions.size(); i++) {
            Reaction reaction=(Reaction) equilibratedReactions.get(i);
            if(reaction.getSystem().equals(this))
             str.append(reaction).append(NEWLINE);
        }        
        
        str.append(networkLayout.getCompleteInfo()).append(NEWLINE);
        str.append("}").append(NEWLINE);
	return str.toString();
    } 

 public void clearModule(){
  Object[] entityArray=this.entities.values().toArray();
  int size=entityArray.length;
   for(int i=0;i<size;i++){
    Entity en=(Entity)entityArray[i];
    remove(en.getName());
    
    if(en.getSystem().equals(this))
    en.setSystem(this.getSystem());
  
   }
 }

 
 @Override
  public boolean insertEntityIntoTreeModel(Entity entity) {
         if (entity instanceof Substance) {
          //
         // the following if statement is added 4/19/05 by LY
         //
         if (entity instanceof ExpressionVariable) {
             if (expressionNode == null) {
                 expressionNode = new DefaultMutableTreeNode("Expressions");
                 treeModel.insertNodeInto(expressionNode, systemNode, Math.min(systemNode.getChildCount(), 2));
             }
             treeModel.insertNodeInto(new DefaultMutableTreeNode(entity.getName()), expressionNode, expressionNode.getChildCount());
             return true;
         }
         else   if (substanceNode == null) {
                 substanceNode = new DefaultMutableTreeNode("Substances");
                 treeModel.insertNodeInto(substanceNode, systemNode, Math.min(systemNode.getChildCount(), 0));
              }
             treeModel.insertNodeInto(new DefaultMutableTreeNode(entity.getName()), substanceNode, substanceNode.getChildCount());
             return true;
         }
         if (entity instanceof Reaction) {
             if (reactionNode == null) {
                 reactionNode = new DefaultMutableTreeNode("Reactions");
                 treeModel.insertNodeInto(reactionNode, systemNode, Math.min(systemNode.getChildCount(), 3));
             }
             
             if (entity instanceof Progressive) {
                 if (progressiveNode == null) {
                     progressiveNode = new DefaultMutableTreeNode("Progressive Reactions");
                     treeModel.insertNodeInto(progressiveNode, reactionNode, 0);
                 }
                 treeModel.insertNodeInto(new DefaultMutableTreeNode(entity.getName()), progressiveNode, progressiveNode.getChildCount());
                 return true;
             }

             if (entity instanceof Equilibrated) {
                 if (equilibratedNode == null) {
                     equilibratedNode = new DefaultMutableTreeNode("Equilibrated Reactions");
                     treeModel.insertNodeInto(equilibratedNode, reactionNode, Math.min(reactionNode.getChildCount(), 1));
                 }

                 treeModel.insertNodeInto(new DefaultMutableTreeNode(entity.getName()), equilibratedNode, equilibratedNode.getChildCount());
                 return true;
                }
         }
         
         if (entity instanceof Parameter) {
             if (parameterNode == null) {
                 parameterNode = new DefaultMutableTreeNode("Parameters");
                 treeModel.insertNodeInto(parameterNode, systemNode, Math.min(systemNode.getChildCount(), 1));
             }
             treeModel.insertNodeInto(new DefaultMutableTreeNode(entity.getName()), parameterNode, parameterNode.getChildCount());
             return true;
         }
          
         else 
             return false;
   }

 
    @Override
    public boolean removeEntityFromTreeModel(Entity entity) {
        Enumeration e;
        if (entity instanceof Substance) {
            if (entity instanceof ExpressionVariable)
                e = expressionNode.children();
            else
                e = substanceNode.children();
        }
        
        else if (entity instanceof Progressive) {
            e = progressiveNode.children();
        }
        
        else if (entity instanceof Equilibrated) {
            e = equilibratedNode.children();
        }
        
        else if (entity instanceof Parameter){
            e = parameterNode.children();
        }
        else 
            return false;
        
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
                // following the path to the root, remove all nodes that doesn't contain
                // a child. Reset the major nodes to null if necessary.
                //
                for (int i = pathToRoot.length - 2; i > 0; i--) {
                    if (pathToRoot[i].getChildCount() == 0) {
                        treeModel.removeNodeFromParent((DefaultMutableTreeNode) pathToRoot[i]);
                        if (substanceNode == pathToRoot[i]) substanceNode = null;
                        else if (expressionNode == pathToRoot[i]) expressionNode = null;
                        else if (reactionNode == pathToRoot[i]) reactionNode = null;
                        else if (progressiveNode == pathToRoot[i]) progressiveNode = null;
                        else if (equilibratedNode == pathToRoot[i]) equilibratedNode = null;
                        else if (parameterNode == pathToRoot[i]) parameterNode = null;
                    }
                    else
                        return true;
                    }
                  return true;
            }
        }
        return false;
     }
 
 

}

