/*
 * SDEEditor.java
 *
 * Created on April 17, 2001, 12:10 AM
 */

package dynetica.gui.algorithms;
import java.io.*;
import javax.swing.*;
/**
 *
 * @author Lingchong You
 * @version 1.0
 * last modifed. August 25 2006
 */
public class SDEEditor extends javax.swing.JPanel {

    /** Creates new customizer SDEEditor */
    dynetica.algorithm.SDE algorithm;
    public SDEEditor(dynetica.algorithm.SDE sde) {
        this.algorithm = sde;
        initComponents ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        maxStepField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        samplingStepField = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        iterationField = new javax.swing.JTextField();
        jLabel9 = new javax.swing.JLabel();
        globalNoiseScaleField = new javax.swing.JTextField();
        jLabel7 = new javax.swing.JLabel();
        noiseScaleField = new javax.swing.JTextField();
        jLabel8 = new javax.swing.JLabel();
        simulationsField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();

        setLayout(new java.awt.BorderLayout());

        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setText("SDE algorithm (Euler method)");
        add(jLabel1, java.awt.BorderLayout.NORTH);

        jPanel1.setLayout(new java.awt.GridLayout(7, 2, 5, 5));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel2.setText("Maximum timestep");
        jPanel1.add(jLabel2);

        maxStepField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        maxStepField.setText(String.valueOf(algorithm.getMaxStep()));
        maxStepField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                maxStepFieldActionPerformed(evt);
            }
        });
        maxStepField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                maxStepFieldFocusLost(evt);
            }
        });
        jPanel1.add(maxStepField);

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel4.setText("Sampling timestep");
        jPanel1.add(jLabel4);

        samplingStepField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        samplingStepField.setText(String.valueOf(algorithm.getSamplingStep()));
        samplingStepField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                samplingStepFieldActionPerformed(evt);
            }
        });
        samplingStepField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                samplingStepFieldFocusLost(evt);
            }
        });
        jPanel1.add(samplingStepField);

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel6.setText("Iterations per simulation");
        jPanel1.add(jLabel6);

        iterationField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        iterationField.setText(String.valueOf(algorithm.getIterations()));
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
        jPanel1.add(iterationField);

        jLabel9.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel9.setText("Extrinsic noise scale");
        jLabel9.setToolTipText("Extrinsic noise scale to the network. This term is multiplied by a Gaussian noise and added to the rate equation of each variable.");
        jPanel1.add(jLabel9);

        globalNoiseScaleField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        globalNoiseScaleField.setText(String.valueOf(algorithm.getGlobalNoiseScale()));
        globalNoiseScaleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                globalNoiseScaleFieldActionPerformed(evt);
            }
        });
        globalNoiseScaleField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                globalNoiseScaleFieldFocusLost(evt);
            }
        });
        jPanel1.add(globalNoiseScaleField);

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel7.setText("Intrinsic noise scale");
        jLabel7.setToolTipText("Scale of intrinsic noise. A noise scale of 1 corresponds to the Chemical Langevin Formulation if the extinsic noise is also set to be 0");
        jPanel1.add(jLabel7);

        noiseScaleField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        noiseScaleField.setText(String.valueOf(algorithm.getNoiseScale()));
        noiseScaleField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                noiseScaleFieldActionPerformed(evt);
            }
        });
        noiseScaleField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                noiseScaleFieldFocusLost(evt);
            }
        });
        jPanel1.add(noiseScaleField);

        jLabel8.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        jLabel8.setText("Number of simulations");
        jPanel1.add(jLabel8);

        simulationsField.setHorizontalAlignment(javax.swing.JTextField.TRAILING);
        simulationsField.setText(String.valueOf(algorithm.getNumberOfRounds()));
        simulationsField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simulationsFieldActionPerformed(evt);
            }
        });
        simulationsField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                simulationsFieldFocusLost(evt);
            }
        });
        jPanel1.add(simulationsField);

        jLabel3.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jLabel3.setText("Output");
        jPanel1.add(jLabel3);

        jButton1.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        jButton1.setText("Click to save...");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        jPanel1.add(jButton1);

        add(jPanel1, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void globalNoiseScaleFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_globalNoiseScaleFieldFocusLost
     algorithm.setGlobalNoiseScale(Double.parseDouble(globalNoiseScaleField.getText()));
    }//GEN-LAST:event_globalNoiseScaleFieldFocusLost

    private void globalNoiseScaleFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_globalNoiseScaleFieldActionPerformed
    algorithm.setGlobalNoiseScale(Double.parseDouble(globalNoiseScaleField.getText()));
    }//GEN-LAST:event_globalNoiseScaleFieldActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setAcceptAllFileFilterUsed(true);
/*
        fileChooser.setFileFilter(new javax.swing.filechooser.FileFilter() {
            public boolean accept(File pathname) {
                return pathname.getName().endsWith(".dyn") || pathname.isDirectory();
            }
            public String getDescription() {
                return "Dynetica files (*.dyn)";
            }
 
        }); */
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            algorithm.setOutput(file);
        }
        //   System.out.println(input.getParentFile().getPath());
        // workDir = file.getParentFile().getAbsolutePath();
        // dynetica.util.DyneticaProperties.setProperty("workingDirectory", workDir);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void simulationsFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_simulationsFieldFocusLost
    algorithm.setNumberOfRounds(Integer.parseInt(simulationsField.getText()));
    }//GEN-LAST:event_simulationsFieldFocusLost

    private void simulationsFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simulationsFieldActionPerformed
 algorithm.setNumberOfRounds(Integer.parseInt(simulationsField.getText()));
    }//GEN-LAST:event_simulationsFieldActionPerformed

    private void noiseScaleFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_noiseScaleFieldFocusLost
     algorithm.setNoiseScale(Double.parseDouble(noiseScaleField.getText()));
    }//GEN-LAST:event_noiseScaleFieldFocusLost

    private void noiseScaleFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_noiseScaleFieldActionPerformed
  algorithm.setNoiseScale(Double.parseDouble(noiseScaleField.getText()));
    }//GEN-LAST:event_noiseScaleFieldActionPerformed

  private void iterationFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_iterationFieldFocusLost
      setIterations();
  }//GEN-LAST:event_iterationFieldFocusLost

  private void iterationFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_iterationFieldActionPerformed
       setIterations();
  }//GEN-LAST:event_iterationFieldActionPerformed

  private void samplingStepFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_samplingStepFieldFocusLost
        setSamplingStep();
  }//GEN-LAST:event_samplingStepFieldFocusLost

  private void samplingStepFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_samplingStepFieldActionPerformed
        setSamplingStep();
  }//GEN-LAST:event_samplingStepFieldActionPerformed

  private void maxStepFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_maxStepFieldFocusLost
       setMaxStep();
  }//GEN-LAST:event_maxStepFieldFocusLost

  private void maxStepFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_maxStepFieldActionPerformed
        setMaxStep();
  }//GEN-LAST:event_maxStepFieldActionPerformed
    private void setMaxStep(){
        algorithm.setMaxStep(Double.parseDouble(maxStepField.getText()));
    }

    private void setSamplingStep() {
        algorithm.setSamplingStep(Double.parseDouble(samplingStepField.getText()));
    }
    private void setIterations() {
        algorithm.setIterations(Integer.parseInt(iterationField.getText()));
    }
    
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField globalNoiseScaleField;
    private javax.swing.JTextField iterationField;
    private javax.swing.JButton jButton1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextField maxStepField;
    private javax.swing.JTextField noiseScaleField;
    private javax.swing.JTextField samplingStepField;
    private javax.swing.JTextField simulationsField;
    // End of variables declaration//GEN-END:variables

}
