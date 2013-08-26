/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.system;
import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.algorithm.*;
import dynetica.reaction.*;
import dynetica.gui.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author System Administrator
 */
public class TestModule extends AbstractModule{
    private static int testModuleIndex=0;
    //Map reactions=new HashMap();
  public TestModule()
  {
    super("TestModule"+testModuleIndex++);
  }
  
    @Override
  public void initializeModule()
 {
   
    ProgressiveReaction reaction= new ProgressiveReaction(this.name+"Reaction1", this);
    reaction.setStoichiometry(this.name+"A"+"+"+this.name+"B"+"->"+this.name+"C");
      try{
           reaction.setKinetics(this.name+"k*["+this.name+"A"+"]*["+this.name+"B"+"]");
         }
      catch(Exception exp)
         {
             System.out.println("Reaction Addition Failed");
             exp.printStackTrace();
         }
    
 }
    
  
}
