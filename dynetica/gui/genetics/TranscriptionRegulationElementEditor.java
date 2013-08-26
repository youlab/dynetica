/*
 * TranscriptionRegulationEditor.java
 *
 * Created on April 14, 2001, 11:36 AM
 */

package dynetica.gui.genetics;

/**
 *
 * @author  Lingchong You
 * @version 0.1
 */
public class TranscriptionRegulationElementEditor extends javax.swing.JPanel {

    dynetica.entity.TranscriptionRegulationElement element;
    
    /** Creates new customizer elementticelementEditor */
    public TranscriptionRegulationElementEditor(dynetica.entity.TranscriptionRegulationElement element) {
        this.element = element;
        initComponents ();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        startLabel = new javax.swing.JLabel();
        end = new javax.swing.JLabel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        startField = new javax.swing.JTextField();
        endField = new javax.swing.JTextField();
        RNAPField = new javax.swing.JTextField();
        efficiencyField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();

        setLayout(new java.awt.BorderLayout());

        jSplitPane1.setDividerSize(1);

        jPanel2.setPreferredSize(new java.awt.Dimension(120, 100));
        jPanel2.setLayout(new java.awt.GridLayout(4, 1));

        startLabel.setText("Start");
        startLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel2.add(startLabel);

        end.setText("End");
        end.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel2.add(end);

        jLabel1.setText("RNAP");
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel2.add(jLabel1);

        jLabel2.setText("Efficiency");
        jLabel2.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jPanel2.add(jLabel2);

        jSplitPane1.setLeftComponent(jPanel2);

        jPanel3.setLayout(new java.awt.GridLayout(4, 1));

        startField.setText(String.valueOf(element.getStart()));
        startField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        startField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startFieldActionPerformed(evt);
            }
        });
        startField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                startFieldFocusLost(evt);
            }
        });
        jPanel3.add(startField);

        endField.setText(String.valueOf(element.getEnd()));
        endField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        endField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                endFieldActionPerformed(evt);
            }
        });
        endField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                endFieldFocusLost(evt);
            }
        });
        jPanel3.add(endField);

        RNAPField.setText(getRNAP());
        RNAPField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        RNAPField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                RNAPFieldActionPerformed(evt);
            }
        });
        RNAPField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                RNAPFieldFocusLost(evt);
            }
        });
        jPanel3.add(RNAPField);

        efficiencyField.setText(String.valueOf(element.getEfficiency()));
        efficiencyField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        efficiencyField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                efficiencyFieldActionPerformed(evt);
            }
        });
        efficiencyField.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                efficiencyFieldFocusLost(evt);
            }
        });
        jPanel3.add(efficiencyField);

        jSplitPane1.setRightComponent(jPanel3);

        jScrollPane1.setViewportView(jSplitPane1);

        add(jScrollPane1, java.awt.BorderLayout.CENTER);

        jLabel3.setText("<html> <i> " + element.getShortClassName() + "</i> " + element.getName());
        jLabel3.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        add(jLabel3, java.awt.BorderLayout.NORTH);
    }// </editor-fold>//GEN-END:initComponents

  private void RNAPFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_RNAPFieldActionPerformed
    setRNAP();
  }//GEN-LAST:event_RNAPFieldActionPerformed

  private void efficiencyFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_efficiencyFieldFocusLost
    setEfficiency();
  }//GEN-LAST:event_efficiencyFieldFocusLost

  private void efficiencyFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_efficiencyFieldActionPerformed
    setEfficiency();
  }//GEN-LAST:event_efficiencyFieldActionPerformed

  private void RNAPFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_RNAPFieldFocusLost
    setRNAP();
  }//GEN-LAST:event_RNAPFieldFocusLost

  private void jTextField1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField1ActionPerformed
    setRNAP();
  }//GEN-LAST:event_jTextField1ActionPerformed

  private void endFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_endFieldFocusLost
    setEnd();
  }//GEN-LAST:event_endFieldFocusLost

  private void endFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_endFieldActionPerformed
    setEnd();
  }//GEN-LAST:event_endFieldActionPerformed

  private void startFieldFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_startFieldFocusLost
    setStart();
  }//GEN-LAST:event_startFieldFocusLost

  private void startFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_startFieldActionPerformed
    setStart();
  }//GEN-LAST:event_startFieldActionPerformed
    private void setStart() {
        int newValue = Integer.parseInt(startField.getText());
        firePropertyChange("EntityState", element.getStart(), newValue);
        element.setStart(newValue);
    }
    
    private void setEnd() {
        int newValue = Integer.parseInt(endField.getText());
        firePropertyChange("EntityState", element.getEnd(), newValue);        
        element.setEnd(newValue);
    }
    
    private String getRNAP() {
        if (element.getRNAP() != null)
            return element.getRNAP().getName();
        else
            return "";
    }
    
    private void setEfficiency() {
        if (efficiencyField.getText().trim() != null)
            try {
                element.setProperty("efficiency", efficiencyField.getText());
            }
            catch (Exception e) {
                e.printStackTrace();
            }
    }
    
    private void setRNAP() {
        String newName = RNAPField.getText().trim();
        String oldName = getRNAP();
        if (oldName.compareTo( newName ) != 0) {
            try {
               element.setProperty("RNAP", RNAPField.getText());
            }
            catch(Exception e) {
                System.out.println(e);
            }
            firePropertyChange("EntityName", oldName, newName);
        }

    }


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField RNAPField;
    private javax.swing.JTextField efficiencyField;
    private javax.swing.JLabel end;
    private javax.swing.JTextField endField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTextField startField;
    private javax.swing.JLabel startLabel;
    // End of variables declaration//GEN-END:variables

}
