/*
 * RK4Editor.java
 *
 * Created on April 17, 2001, 12:10 AM
 */

package dynetica.gui.algorithms;

import java.io.*;
import javax.swing.*;

/**
 * A lightweight editor for DirectMethod class (which implements Gillespie's
 * algorithm).
 * 
 * @author Lingchong You
 * @version 0.01
 */
public class DirectMethodEditor extends javax.swing.JPanel {

    /** Creates new customizer RK4Editor */
    dynetica.algorithm.DirectMethod algorithm;

    public DirectMethodEditor(dynetica.algorithm.DirectMethod s) {
        this.algorithm = s;
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        samplingStepField = new javax.swing.JTextField();
        iterationField = new javax.swing.JTextField();
        simulationNumberField = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("Gillespie Direct Method Algorithm");
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jSplitPane1.setDividerSize(1);

        jPanel2.setLayout(new java.awt.GridLayout(4, 1));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel4.setText("Sampling Time Step");
        jLabel4.setMaximumSize(new java.awt.Dimension(400, 20));
        jLabel4.setMinimumSize(new java.awt.Dimension(100, 20));
        jLabel4.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel2.add(jLabel4);

        jLabel6.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel6.setText("Number of Iterations");
        jLabel6.setMaximumSize(new java.awt.Dimension(400, 20));
        jLabel6.setMinimumSize(new java.awt.Dimension(100, 20));
        jLabel6.setPreferredSize(new java.awt.Dimension(150, 20));
        jPanel2.add(jLabel6);

        jLabel2.setText("Number of rounds");
        jPanel2.add(jLabel2);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Output");
        jPanel2.add(jLabel3);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setLayout(new java.awt.GridLayout(4, 1));

        samplingStepField.setText(String.valueOf(algorithm.getSamplingStep()));
        samplingStepField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        samplingStepField.setPreferredSize(new java.awt.Dimension(120, 21));
        samplingStepField.setMaximumSize(new java.awt.Dimension(400, 20));
        samplingStepField.setMinimumSize(new java.awt.Dimension(100, 20));
        samplingStepField
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        samplingStepFieldActionPerformed(evt);
                    }
                });
        samplingStepField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                samplingStepFieldFocusLost(evt);
            }
        });
        jPanel3.add(samplingStepField);

        iterationField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        iterationField.setText(String.valueOf(algorithm.getIterations()));
        iterationField.setMaximumSize(new java.awt.Dimension(400, 20));
        iterationField.setMinimumSize(new java.awt.Dimension(100, 20));
        iterationField.setPreferredSize(new java.awt.Dimension(120, 21));
        iterationField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                iterationFieldActionPerformed(evt);
            }
        });
        iterationField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                iterationFieldFocusLost(evt);
            }
        });
        jPanel3.add(iterationField);

        simulationNumberField.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        simulationNumberField
                .setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        simulationNumberField.setText(String.valueOf(algorithm
                .getNumberOfRounds()));
        simulationNumberField
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        simulationNumberFieldActionPerformed(evt);
                    }
                });
        simulationNumberField
                .addFocusListener(new java.awt.event.FocusAdapter() {
                    public void focusLost(java.awt.event.FocusEvent evt) {
                        simulationNumberFieldFocusLost(evt);
                    }
                });
        jPanel3.add(simulationNumberField);

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton1.setText("Click to save...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel3.add(jButton1);

        jSplitPane1.setRightComponent(jPanel3);

        jScrollPane1.setViewportView(jSplitPane1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void simulationNumberFieldFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_simulationNumberFieldFocusLost
        algorithm.setNumberOfRounds(Integer.parseInt(simulationNumberField
                .getText()));
    }// GEN-LAST:event_simulationNumberFieldFocusLost

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
        /*
         * fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
         * public boolean accept(File pathname) { return
         * pathname.getName().endsWith(".dyn") || pathname.isDirectory(); }
         * public String getDescription() { return "Dynetica files (*.dyn)"; }
         * 
         * });
         */
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            algorithm.setOutput(file);
        }
        // System.out.println(input.getParentFile().getPath());
        // workDir = file.getParentFile().getAbsolutePath();
        // dynetica.util.DyneticaProperties.setProperty("workingDirectory",
        // workDir);

    }// GEN-LAST:event_jButton1ActionPerformed

    private void simulationNumberFieldActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_simulationNumberFieldActionPerformed
        algorithm.setNumberOfRounds(Integer.parseInt(simulationNumberField
                .getText()));
    }// GEN-LAST:event_simulationNumberFieldActionPerformed

    private void iterationFieldFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_iterationFieldFocusLost
        setIterations();
    }// GEN-LAST:event_iterationFieldFocusLost

    private void iterationFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_iterationFieldActionPerformed
        setIterations();
    }// GEN-LAST:event_iterationFieldActionPerformed

    private void samplingStepFieldFocusLost(java.awt.event.FocusEvent evt) {// GEN-FIRST:event_samplingStepFieldFocusLost
        setSamplingStep();
    }// GEN-LAST:event_samplingStepFieldFocusLost

    private void samplingStepFieldActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_samplingStepFieldActionPerformed
        setSamplingStep();
    }// GEN-LAST:event_samplingStepFieldActionPerformed

    private void setSamplingStep() {
        algorithm.setSamplingStep(Double.parseDouble(samplingStepField
                .getText()));
    }

    private void setIterations() {
        algorithm.setIterations(Integer.parseInt(iterationField.getText()));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField iterationField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField samplingStepField;
    private javax.swing.JTextField simulationNumberField;
    // End of variables declaration//GEN-END:variables

}
