/*
 * DecayEditor.java
 *
 * Created on April 10, 2001, 2:07 AM
 */

package dynetica.gui.reactions;

/**
 *
 * @author Lingchong You
 * @version 0.01
 */

import dynetica.entity.*;
import dynetica.system.*;
import javax.swing.*;


public class DecayEditor extends javax.swing.JPanel {

    dynetica.reaction.Decay reaction;
    DefaultListModel substanceListModel = new DefaultListModel();
    DefaultListModel parameterListModel = new DefaultListModel();
    
    /** Creates new customizer DecayEditor */
    public DecayEditor(dynetica.reaction.Decay r) {
        reaction = r;
        initComponents ();
        updateListModels();
    }

    // update the substancelistModel and parameterListModel everytime the
    // reaction is modified.
    
    private void updateListModels() {
        if (! substanceListModel.isEmpty()) substanceListModel.removeAllElements();
        Object [] subs = reaction.getSubstances().toArray();
        for (int i = 0; i < subs.length; i++) {
            substanceListModel.addElement(((Entity) (subs[i])).getName());
        }
        if (! substanceListModel.isEmpty()) substanceList.setSelectedIndex(0);
        
        if (! parameterListModel.isEmpty()) parameterListModel.removeAllElements();
        Object [] paras = reaction.getParameters().toArray();
        for (int i = 0; i < paras.length; i++) {
            parameterListModel.addElement(((Entity) (paras[i])).getName());
        }
        
        if (! parameterListModel.isEmpty()) parameterList.setSelectedIndex(0);
        
    }
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        substrateLabel = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        substrateField = new javax.swing.JTextField();
        kField = new javax.swing.JTextField();
        kineticsField = new javax.swing.JTextField();
        jSplitPane2 = new javax.swing.JSplitPane();
        substancePanel = new javax.swing.JPanel();
        sListPanel = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        substanceList = new javax.swing.JList();
        jLabel5 = new javax.swing.JLabel();
        sEditor = new javax.swing.JPanel();
        parameterPanel = new javax.swing.JPanel();
        pListPanel = new javax.swing.JPanel();
        jScrollPane4 = new javax.swing.JScrollPane();
        parameterList = new javax.swing.JList();
        jLabel6 = new javax.swing.JLabel();
        pEditor = new javax.swing.JPanel();

        setMaximumSize(new java.awt.Dimension(350, 400));
        setLayout(new java.awt.BorderLayout(0, 1));

        jLabel1.setText("<html> <i> Decay Reaction </i> " + reaction.getName());
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(220, 30));
        jLabel1.setMinimumSize(new java.awt.Dimension(220, 30));
        jLabel1.setMaximumSize(new java.awt.Dimension(220, 30));
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jSplitPane3.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane3.setMaximumSize(new java.awt.Dimension(350, 400));

        jSplitPane1.setDividerLocation(150);
        jSplitPane1.setDividerSize(1);

        jPanel2.setPreferredSize(new java.awt.Dimension(130, 100));
        jPanel2.setLayout(new java.awt.GridLayout(3, 1, 2, 2));

        substrateLabel.setVerticalAlignment(javax.swing.SwingConstants.TOP);
        substrateLabel.setText("Substrate");
        substrateLabel.setToolTipText("The substrate of the decay reaction.");
        substrateLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        substrateLabel.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        substrateLabel.setPreferredSize(new java.awt.Dimension(130, 25));
        substrateLabel.setMinimumSize(new java.awt.Dimension(100, 25));
        substrateLabel.setMaximumSize(new java.awt.Dimension(400, 25));
        jPanel2.add(substrateLabel);

        jLabel4.setText("Decay rate constant");
        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jPanel2.add(jLabel4);

        jLabel3.setText("Kinetics");
        jLabel3.setToolTipText("The rate expression that describes the kinetics of this \nreaction. Follow the convention in writing down a \nmathematical expression.");
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel3.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 25));
        jLabel3.setMinimumSize(new java.awt.Dimension(100, 25));
        jLabel3.setMaximumSize(new java.awt.Dimension(100, 25));
        jPanel2.add(jLabel3);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setLayout(new java.awt.GridLayout(3, 1, 2, 2));

        substrateField.setText(getSubstrate());
        substrateField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substrateFieldActionPerformed(evt);
            }
        });
        substrateField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                substrateFieldFocusLost(evt);
            }
        });
        jPanel3.add(substrateField);

        kField.setText(getK());
        kField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kFieldActionPerformed(evt);
            }
        });
        kField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                kFieldFocusLost(evt);
            }
        });
        jPanel3.add(kField);

        kineticsField.setEditable(false);
        kineticsField.setText(getKinetics());
        jPanel3.add(kineticsField);

        jSplitPane1.setRightComponent(jPanel3);

        jSplitPane3.setLeftComponent(jSplitPane1);

        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        substancePanel.setLayout(new java.awt.GridLayout(1, 2));

        sListPanel.setLayout(new java.awt.BorderLayout());

        substanceList.setModel(substanceListModel);
        substanceList.setVisibleRowCount(Math.min(8, substanceListModel.getSize()));
        substanceList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        substanceList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                substanceListValueChanged(evt);
            }
        });
        jScrollPane3.setViewportView(substanceList);

        sListPanel.add(jScrollPane3, java.awt.BorderLayout.CENTER);

        jLabel5.setText("Substances");
        jLabel5.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        sListPanel.add(jLabel5, java.awt.BorderLayout.NORTH);

        substancePanel.add(sListPanel);

        sEditor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        substancePanel.add(sEditor);

        jSplitPane2.setTopComponent(substancePanel);

        parameterPanel.setLayout(new java.awt.GridLayout(1, 2));

        pListPanel.setLayout(new java.awt.BorderLayout());

        parameterList.setModel(parameterListModel);
        parameterList.setVisibleRowCount(Math.min(8, parameterListModel.getSize()));
        parameterList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        parameterList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                parameterListValueChanged(evt);
            }
        });
        jScrollPane4.setViewportView(parameterList);

        pListPanel.add(jScrollPane4, java.awt.BorderLayout.CENTER);

        jLabel6.setText("Parameters");
        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        pListPanel.add(jLabel6, java.awt.BorderLayout.NORTH);

        parameterPanel.add(pListPanel);

        pEditor.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        parameterPanel.add(pEditor);

        jSplitPane2.setBottomComponent(parameterPanel);

        jSplitPane3.setRightComponent(jSplitPane2);

        add(jSplitPane3, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void substrateFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_substrateFieldFocusLost
      setSubstrate();
    }//GEN-LAST:event_substrateFieldFocusLost

    private void substrateFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substrateFieldActionPerformed
       setSubstrate();
    }//GEN-LAST:event_substrateFieldActionPerformed

    private void kFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_kFieldFocusLost
      setK();
    }//GEN-LAST:event_kFieldFocusLost

    private void kFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kFieldActionPerformed
      setK();
    }//GEN-LAST:event_kFieldActionPerformed

  private void parameterListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_parameterListValueChanged

      parameterPanel.remove(pEditor);
      //If condition added by Kanishk Asthana on 23 July 2013: To fix a runtime error. This error 
      //was caused whenever the method updatelistmodel() was executed.
      //This method executes everytime the parameterlist is to be updated; so whenever updatelistmodels()
      //was executed all elements were removed from the parameterlist. Since this removal of elements triggered  
      //a parameterListvaluechanged event; the event led to this method being executed. This method would then try to get
      //the current parameter. However since the parameter list was already empty excecution of this method
      //would lead to the Editor misbehaving(runtime error)!
      //Same logic applies for the changes to substancelistmodel as well.
      if(getCurrentParameter()!=null)
      {
        pEditor = getCurrentParameter().editor();
        parameterPanel.add(pEditor);
        parameterPanel.validate();
      }

  }//GEN-LAST:event_parameterListValueChanged

  private void substanceListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_substanceListValueChanged

      substancePanel.remove(sEditor);
      //If condition added by Kanishk Asthana on 23 July 2013: See parameterListvaluechanged for and explaination
       if(getCurrentSubstance()!=null)
      {
       sEditor = getCurrentSubstance().editor();
       substancePanel.add(sEditor);
       substancePanel.validate();
      }

  }//GEN-LAST:event_substanceListValueChanged

  private Parameter getCurrentParameter() {
      int index = parameterList.getSelectedIndex();
      //If condition added by Kanishk Asthana on 23 July 2013: See parameterListvaluechanged for and explaination
      if(index!=-1)
      {   
       String pName = (String) (parameterListModel.get(index));
       return (Parameter) (reaction.getSystem().get(pName));
      }
      else return null;
  }
  
  private Substance getCurrentSubstance() {
      int index = substanceList.getSelectedIndex();
      //If condition added by Kanishk Asthana on 23 July 2013: See parameterListvaluechanged for and explaination
      if(index!=-1)
      {    
        String substanceName = (String) (substanceListModel.get(index));
        return (Substance) (reaction.getSystem().get(substanceName));
      }
      else return null;
  }

    
  private void setK() {
      String kString = kField.getText().trim();
      String oldKString = getK();
      if (kString.length() > 0 && kString.compareTo(oldKString) != 0) {
          try { 
             reaction.setProperty("k", kString);
             updateListModels();
             kineticsField.setText(getKinetics());
         }
          catch(Exception e) {
              System.out.println(e);
          }
      }
      
  }
  
  private void setSubstrate() {
      String name = substrateField.getText().trim();
      String oldName = getSubstrate();
      if (name.length() > 0 && name.compareTo(oldName) != 0) {
            if (reaction.getSystem().contains(name))
                 reaction.setSubstrate(reaction.getSystem().getSubstance(name));
            else {
                 reaction.setSubstrate(new Substance(name, ((ReactiveSystem) (reaction.getSystem()))));
            }
            updateListModels();
            kineticsField.setText(getKinetics());
      }
  }
  
  private String getK() {
      if (reaction.getK() != null)
          return reaction.getK().toString();
      else
          return "";
  }
  
  
  private String getSubstrate() {
      if (reaction.getSubstrate() != null)
          return reaction.getSubstrate().getName();
      else
          return "";
  }
  private String getStoichiometry(){
      if (reaction.getStoichiometry() != null) 
          return reaction.getStoichiometry();
      else
          return "";
  }
  
  private String getKinetics() {
      if (reaction.getKinetics() != null)
          return reaction.getKinetics();
      else
          return "";
  }
  

  
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JTextField kField;
    private javax.swing.JTextField kineticsField;
    private javax.swing.JPanel pEditor;
    private javax.swing.JPanel pListPanel;
    private javax.swing.JList parameterList;
    private javax.swing.JPanel parameterPanel;
    private javax.swing.JPanel sEditor;
    private javax.swing.JPanel sListPanel;
    private javax.swing.JList substanceList;
    private javax.swing.JPanel substancePanel;
    private javax.swing.JTextField substrateField;
    private javax.swing.JLabel substrateLabel;
    // End of variables declaration//GEN-END:variables
  public static class Tester {
      public static void main(String [] args) { 
          dynetica.reaction.Decay ma =
            new dynetica.reaction.Decay();
          ma.setName("Test");
          ma.setSystem(new dynetica.system.ReactiveSystem("Tttt"));
          try {
              ma.setProperty("substrate", "A");
              ma.setProperty("k", " a * b * [F]");
               javax.swing.JFrame frame = new javax.swing.JFrame();
           
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent e) {
                System.exit(0);
            }
        });
        frame.getContentPane().add(new DecayEditor(ma));
              frame.pack();
              frame.show();
          }
          catch (Exception e) {
              e.printStackTrace();
          }
      }
  }
}
