/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.gui.algorithms;

import dynetica.algorithm.*;
import dynetica.system.*;
import dynetica.entity.*;
import java.util.*;
import javax.swing.*;
/**
 *
 * @author owner
 */
public class NoiseOptimizationEditor extends javax.swing.JPanel{

    
    NoiseOptimizationCrossTrial noct;
    ReactiveSystem system;
    DefaultListModel searchParameters = new DefaultListModel();
    DefaultListModel fixedParameters = new DefaultListModel();
    
    /**
     * Creates new form ParameterSearchMonteCarloEditor
     */
    public NoiseOptimizationEditor(ReactiveSystem sys) {
        system = sys;
        setupLists();
        initComponents();
    }
    
    public void setupLists() {
        List parameters = system.getParameters();
        for (int i=0; i<parameters.size(); i++) {
            Parameter p = (Parameter) parameters.get(i);
            fixedParameters.addElement(p.getName());
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        substanceField = new javax.swing.JFormattedTextField();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        toleranceField = new javax.swing.JFormattedTextField();
        timeField = new javax.swing.JFormattedTextField();
        repetitionsField = new javax.swing.JFormattedTextField();
        iterationsField = new javax.swing.JFormattedTextField();
        jPanel1 = new javax.swing.JPanel();
        addButton = new javax.swing.JButton();
        removeButton = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        searchParameterList = new javax.swing.JList();
        jScrollPane1 = new javax.swing.JScrollPane();
        fixedList = new javax.swing.JList();
        minMaxChoice = new javax.swing.JComboBox();
        varSnrChoice = new javax.swing.JComboBox();
        jPanel2 = new javax.swing.JPanel();
        runButton = new javax.swing.JButton();
        pauseButton = new javax.swing.JButton();
        resetButton = new javax.swing.JButton();
        meanChangeField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        meanCheckBox = new javax.swing.JCheckBox();

        substanceField.setText("substance_name");
        substanceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                substanceFieldActionPerformed(evt);
            }
        });

        jLabel1.setText("Target Substance");

        jLabel2.setText("Tolerance");

        jLabel3.setText("Start Time");

        jLabel4.setText("Trials per Iteration");

        jLabel5.setText("Max Iterations");

        toleranceField.setText("0.5");
        toleranceField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toleranceFieldActionPerformed(evt);
            }
        });

        timeField.setText("0.0");

        repetitionsField.setText("10");
        repetitionsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                repetitionsFieldActionPerformed(evt);
            }
        });

        iterationsField.setText("100");

        addButton.setText("Add");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        removeButton.setText("Remove");
        removeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeButtonActionPerformed(evt);
            }
        });

        searchParameterList.setModel(searchParameters);
        jScrollPane2.setViewportView(searchParameterList);

        fixedList.setModel(fixedParameters);
        jScrollPane1.setViewportView(fixedList);

        minMaxChoice.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        minMaxChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Minimize", "Maximize" }));
        minMaxChoice.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                minMaxChoiceActionPerformed(evt);
            }
        });

        varSnrChoice.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        varSnrChoice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Variance", "SNR" }));

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(64, 64, 64)
                .addComponent(addButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(removeButton)
                .addGap(61, 61, 61))
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 161, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(minMaxChoice, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 182, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(31, 31, 31)
                        .addComponent(varSnrChoice, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(0, 0, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(minMaxChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(varSnrChoice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(removeButton)
                    .addComponent(addButton))
                .addGap(10, 10, 10)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 87, Short.MAX_VALUE)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addContainerGap())
        );

        runButton.setText("Run");
        runButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                runButtonActionPerformed(evt);
            }
        });

        pauseButton.setText("Pause");
        pauseButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseButtonActionPerformed(evt);
            }
        });

        resetButton.setText("Reset Parameters");
        resetButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resetButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(runButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pauseButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(resetButton)
                .addContainerGap(61, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(runButton)
                    .addComponent(pauseButton)
                    .addComponent(resetButton))
                .addGap(0, 22, Short.MAX_VALUE))
        );

        meanChangeField.setText("25");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, meanCheckBox, org.jdesktop.beansbinding.ELProperty.create("${selected}"), meanChangeField, org.jdesktop.beansbinding.BeanProperty.create("editable"));
        bindingGroup.addBinding(binding);

        meanChangeField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meanChangeFieldActionPerformed(evt);
            }
        });

        jLabel6.setText("%");

        meanCheckBox.setText("Limit Change in Mean");
        meanCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                meanCheckBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(48, 48, 48)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel1)
                            .addComponent(jLabel2)
                            .addComponent(jLabel5)
                            .addComponent(jLabel4)
                            .addComponent(jLabel3)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(40, 40, 40)
                        .addComponent(meanCheckBox, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(substanceField)
                        .addComponent(toleranceField)
                        .addComponent(timeField)
                        .addComponent(iterationsField)
                        .addComponent(repetitionsField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(meanChangeField, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel6)))
                .addGap(16, 16, 16))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(substanceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(toleranceField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel3)
                    .addComponent(timeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addComponent(repetitionsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(iterationsField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(meanChangeField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6)
                    .addComponent(meanCheckBox))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 12, Short.MAX_VALUE)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
       Object [] selectedNames = fixedList.getSelectedValues();
       for (int i = 0; i < selectedNames.length; i++) {
           if (! searchParameters.contains(selectedNames[i]))
           searchParameters.addElement(selectedNames[i]);
       }
    }//GEN-LAST:event_addButtonActionPerformed

    private void pauseButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pauseButtonActionPerformed
        noct.pause();
    }//GEN-LAST:event_pauseButtonActionPerformed

    private void substanceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_substanceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_substanceFieldActionPerformed

    private void toleranceFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toleranceFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_toleranceFieldActionPerformed

    private void runButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_runButtonActionPerformed
        Parameter [] parameterList = new Parameter[searchParameters.size()];
        for (int i = 0; i < parameterList.length; i++) {
            parameterList[i] = system.getParameter((String) searchParameters.get(i));
        }
        Substance s = system.getSubstance(substanceField.getText());
        double tol = Double.parseDouble(toleranceField.getText());
        double time = Double.parseDouble(timeField.getText());       
        int reps = (int) Double.parseDouble(repetitionsField.getText());
        int iter = (int) Double.parseDouble(iterationsField.getText());
        System.out.println("Running noise optimization for substance " + s.getName() + " starting at time t = " + time);
        noct = new NoiseOptimizationCrossTrial(system, parameterList, s, tol, time, reps, iter);
        String minMax = (String) minMaxChoice.getSelectedItem();
        if (minMax.equals("Maximize")) {noct.setMinimizing(false);}
        String varSnr = (String) varSnrChoice.getSelectedItem();
        if (varSnr.equals("Variance")) {noct.setIsSNR(false);}
        if (meanCheckBox.isSelected()) {
            noct.setLimitingMean(true);
            noct.setMeanTolerance(Double.parseDouble(meanChangeField.getText()));
        }
        noct.start();
        
    }//GEN-LAST:event_runButtonActionPerformed

    private void removeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeButtonActionPerformed
      Object[] selectedNames = searchParameterList.getSelectedValues();
      for (int i = 0; i < selectedNames.length; i++) {
          searchParameters.removeElement(selectedNames[i]);
      }
    }//GEN-LAST:event_removeButtonActionPerformed

    private void resetButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resetButtonActionPerformed
        Object[] parameters = system.getParameters().toArray();
        for (int i = 0; i < parameters.length; i++){
            Parameter p = (Parameter) parameters[i];
            p.resetSearchingValue();
        }
    }//GEN-LAST:event_resetButtonActionPerformed

    private void repetitionsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_repetitionsFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_repetitionsFieldActionPerformed

    private void minMaxChoiceActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_minMaxChoiceActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_minMaxChoiceActionPerformed

    private void meanChangeFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meanChangeFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meanChangeFieldActionPerformed

    private void meanCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_meanCheckBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_meanCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addButton;
    private javax.swing.JList fixedList;
    private javax.swing.JFormattedTextField iterationsField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField meanChangeField;
    private javax.swing.JCheckBox meanCheckBox;
    private javax.swing.JComboBox minMaxChoice;
    private javax.swing.JButton pauseButton;
    private javax.swing.JButton removeButton;
    private javax.swing.JFormattedTextField repetitionsField;
    private javax.swing.JButton resetButton;
    private javax.swing.JButton runButton;
    private javax.swing.JList searchParameterList;
    private javax.swing.JFormattedTextField substanceField;
    private javax.swing.JFormattedTextField timeField;
    private javax.swing.JFormattedTextField toleranceField;
    private javax.swing.JComboBox varSnrChoice;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
