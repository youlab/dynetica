/*
 * BasicSensitivityAnalysisWindow.java
 *
 * Created on August 13, 2001, 9:56 PM
 */

package dynetica.gui.plotting;

/**
 *
 * @author  Lingchong You
 * @version 0.01
 */
import dynetica.algorithm.*;
import dynetica.entity.*;
import javax.swing.*;
import java.io.File;
import matlabcontrol.MatlabConnectionException;
import matlabcontrol.MatlabInvocationException;

public class BasicSensitivityAnalysisWindow extends javax.swing.JFrame {
    InteractiveFigure figure = new InteractiveFigure();

    final DefaultListModel substanceNames = new DefaultListModel();
    final dynetica.system.ReactiveSystem system;
    BasicSensitivityAnalysis algorithm;
    FigureAxisModel figureAxisModel;
    double[] xValues;
    double[][] yValues;
    String xLabel;
    String[] yLabels;

    boolean showTimeCourses = true; // by default show multiple time courses for
                                    // each variable against the parameter.

    //
    // Constructs a FigureWindow that plots data directly from
    // a ReactiveSystem
    //
    public BasicSensitivityAnalysisWindow(BasicSensitivityAnalysis algorithm) {
        super("Sensitivity Analysis For ReactiveSystem "
                + algorithm.getSystem().getName());
        this.algorithm = algorithm;
        this.system = algorithm.getSystem();
        Substance[] substances = algorithm.getSubstances();
        int nSubs = substances.length; // substances.length;
        System.out.println("# of substances = " + nSubs);
        Substance s;
        for (int i = 0; i < nSubs; i++) {
            s = (Substance) (substances[i]);
            substanceNames.addElement(s.getName());
        }
        initComponents();
        figurePanel.add(figure.getPlotPanel(), java.awt.BorderLayout.CENTER);
        updateFigure();

        pack();
    }

    public void setUpData() {
        Substance[] substances = algorithm.getSubstances();
        int nSubs = substances.length; // substances.length;
        System.out.println("# of substances = " + nSubs);
        Substance s;
        for (int i = 0; i < nSubs; i++) {
            s = (Substance) (substances[i]);
            substanceNames.addElement(s.getName());
        }
        figurePanel.setSize(400, 300);
    }

    public void updateFigure() {
        int[] indices = substanceList.getSelectedIndices();
        xLabel = algorithm.getVariable().getName();

        if (!showTimeCourses) {
            xValues = algorithm.getXValues();
            yValues = new double[indices.length][xValues.length];
            yLabels = new String[indices.length];

            for (int i = 0; i < indices.length; i++) {
                yValues[i] = algorithm.getYValues()[indices[i]];
                yLabels[i] = (String) (substanceNames.get(indices[i]));
            }
        }

        else {
            xValues = ((dynetica.system.ReactiveSystem) system).getTimer()
                    .getTimePoints();
            double[] parameterValues = algorithm.getXValues();
            yLabels = new String[parameterValues.length * indices.length];
            yValues = new double[parameterValues.length * indices.length][xValues.length];

            for (int j = 0; j < indices.length; j++) {
                int selectedIndex = indices[j];
                for (int i = 0; i < parameterValues.length; i++) {
                    int valueIndex = i + j * parameterValues.length;
                    yValues[valueIndex] = algorithm.getTimeCourses()[selectedIndex][i];

                    if (indices.length > 1) {
                        yLabels[valueIndex] = (String) (substanceNames
                                .get(selectedIndex));
                        if (algorithm.getMin() != algorithm.getMax())
                            yLabels[valueIndex] += ":"
                                    + xLabel
                                    + "="
                                    + dynetica.util.Numerics
                                            .displayFormattedValue(parameterValues[i]);
                    }

                    else {
                        if (algorithm.getMin() != algorithm.getMax())
                            yLabels[valueIndex] = xLabel
                                    + "="
                                    + dynetica.util.Numerics
                                            .displayFormattedValue(parameterValues[i]);
                    }
                }
            }

        }

        if (showTimeCourses)
            xLabel = "Time";
        if (algorithm.getMin() == algorithm.getMax() && indices.length == 1)
            yLabels = null; // suppress labeling if the variable is not changed.

        figure.plotData(xLabel, yLabels, xValues, yValues);
        figure.setLogX(logXItem.getState());
        figure.setLogY(logYItem.getState());
        figurePanel.repaint();
        if (figureAxisModel == null) {
            figureAxisModel = new FigureAxisModel();
            rangeTable.setModel(figureAxisModel);
        } else
            figureAxisModel.setFigure();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPane = new javax.swing.JSplitPane();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        substanceList = new javax.swing.JList();
        substanceListLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        rangeTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        logXbox = new javax.swing.JCheckBox();
        logYbox = new javax.swing.JCheckBox();
        figurePanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveSelectedDataItem = new javax.swing.JMenuItem();
        closeItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        logXItem = new javax.swing.JCheckBoxMenuItem();
        logYItem = new javax.swing.JCheckBoxMenuItem();
        selectAllItem = new javax.swing.JMenuItem();
        timeCoursesCheckBox = new javax.swing.JCheckBoxMenuItem();
        matlabplotItem = new javax.swing.JMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        mainPane.setDividerLocation(110);
        mainPane.setLastDividerLocation(10);
        mainPane.setPreferredSize(new java.awt.Dimension(500, 400));

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(5);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setLastDividerLocation(80);

        jPanel2.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(60, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(60, 200));

        substanceList.setModel(substanceNames);
        substanceList.setSelectedIndex(0);
        substanceList
                .addListSelectionListener(new javax.swing.event.ListSelectionListener() {
                    public void valueChanged(
                            javax.swing.event.ListSelectionEvent evt) {
                        substanceListValueChanged(evt);
                    }
                });
        jScrollPane1.setViewportView(substanceList);

        jPanel2.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        substanceListLabel.setText("Substances");
        jPanel2.add(substanceListLabel, java.awt.BorderLayout.NORTH);

        jSplitPane1.setTopComponent(jPanel2);

        jPanel3.setLayout(new java.awt.BorderLayout(0, 3));

        jLabel6.setText("Figure Configuration");
        jLabel6.setBorder(javax.swing.BorderFactory
                .createLineBorder(new java.awt.Color(0, 0, 0)));
        jLabel6.setMaximumSize(new java.awt.Dimension(200, 18));
        jLabel6.setPreferredSize(new java.awt.Dimension(110, 18));
        jPanel3.add(jLabel6, java.awt.BorderLayout.NORTH);

        rangeTable.setBorder(javax.swing.BorderFactory.createLineBorder(null));
        rangeTable.setMaximumSize(new java.awt.Dimension(70, 100));
        rangeTable.setMinimumSize(new java.awt.Dimension(70, 64));
        rangeTable.setPreferredSize(new java.awt.Dimension(70, 64));
        rangeTable.setRowMargin(5);
        rangeTable.setRowSelectionAllowed(false);
        jPanel3.add(rangeTable, java.awt.BorderLayout.CENTER);

        jPanel1.setLayout(new javax.swing.BoxLayout(jPanel1,
                javax.swing.BoxLayout.Y_AXIS));

        logXbox.setText("Log Scale in X");
        logXbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logXboxActionPerformed(evt);
            }
        });
        jPanel1.add(logXbox);

        logYbox.setText("Log Scale in Y");
        logYbox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logYboxActionPerformed(evt);
            }
        });
        jPanel1.add(logYbox);

        jPanel3.add(jPanel1, java.awt.BorderLayout.SOUTH);

        jSplitPane1.setBottomComponent(jPanel3);

        mainPane.setLeftComponent(jSplitPane1);

        figurePanel.setLayout(new java.awt.BorderLayout());
        figurePanel.add(jPanel4, java.awt.BorderLayout.NORTH);

        mainPane.setRightComponent(figurePanel);

        getContentPane().add(mainPane, java.awt.BorderLayout.CENTER);

        fileMenu.setText("File");

        saveSelectedDataItem.setText("Save Selected Data");
        saveSelectedDataItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        saveSelectedDataItemActionPerformed(evt);
                    }
                });
        fileMenu.add(saveSelectedDataItem);

        closeItem.setText("Close");
        closeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                closeItemActionPerformed(evt);
            }
        });
        fileMenu.add(closeItem);

        exitItem.setText("Exit");
        exitItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                exitItemActionPerformed(evt);
            }
        });
        fileMenu.add(exitItem);

        jMenuBar1.add(fileMenu);

        viewMenu.setText("View");

        logXItem.setText("Log Scale in X axis");
        logXItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logXItemActionPerformed(evt);
            }
        });
        viewMenu.add(logXItem);

        logYItem.setText("Log Scale in Y axis");
        logYItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logYItemActionPerformed(evt);
            }
        });
        viewMenu.add(logYItem);

        selectAllItem.setText("Select All");
        selectAllItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectAllItemActionPerformed(evt);
            }
        });
        viewMenu.add(selectAllItem);

        timeCoursesCheckBox.setSelected(true);
        timeCoursesCheckBox.setText("Show Time Courses ");
        timeCoursesCheckBox
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        timeCoursesCheckBoxActionPerformed(evt);
                    }
                });
        viewMenu.add(timeCoursesCheckBox);

        matlabplotItem.setText("Plot in Matlab");
        matlabplotItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                matlabplotItemActionPerformed(evt);
            }
        });
        viewMenu.add(matlabplotItem);

        jMenuBar1.add(viewMenu);

        setJMenuBar(jMenuBar1);
    }// </editor-fold>//GEN-END:initComponents

    private void maxPointsSliderStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_maxPointsSliderStateChanged
    }// GEN-LAST:event_maxPointsSliderStateChanged

    private void logYboxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logYboxActionPerformed
        boolean state = logYbox.isSelected();
        logYItem.setSelected(state);
        figure.setLogY(state);
        figurePanel.repaint();
    }// GEN-LAST:event_logYboxActionPerformed

    private void logXboxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logXboxActionPerformed
        boolean state = logXbox.isSelected();
        logXItem.setSelected(state);
        figure.setLogX(state);
        figurePanel.repaint();
    }// GEN-LAST:event_logXboxActionPerformed

    private void selectAllItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_selectAllItemActionPerformed
        int[] allIndices = new int[substanceNames.size()];
        for (int i = 0; i < allIndices.length; i++)
            allIndices[i] = i;
        substanceList.setSelectedIndices(allIndices);
    }// GEN-LAST:event_selectAllItemActionPerformed

    private void saveSelectedDataItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveSelectedDataItemActionPerformed
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File output = fileChooser.getSelectedFile();
            figure.saveData(output);
        }

    }// GEN-LAST:event_saveSelectedDataItemActionPerformed

    private void exitItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_exitItemActionPerformed
        System.exit(0);
    }// GEN-LAST:event_exitItemActionPerformed

    private void closeItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_closeItemActionPerformed
        this.dispose();
    }// GEN-LAST:event_closeItemActionPerformed

    private void logXItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logXItemActionPerformed
        figure.setLogX(logXItem.getState());
        logXbox.setSelected(logXItem.getState());
    }// GEN-LAST:event_logXItemActionPerformed

    private void logYItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logYItemActionPerformed
        figure.setLogY(logYItem.getState());
    }// GEN-LAST:event_logYItemActionPerformed

    private void substanceListValueChanged(
            javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_substanceListValueChanged
        updateFigure();
    }// GEN-LAST:event_substanceListValueChanged

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitForm
        this.dispose();
    }// GEN-LAST:event_exitForm

    private void timeCoursesCheckBoxActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_timeCoursesCheckBoxActionPerformed
        showTimeCourses = timeCoursesCheckBox.isSelected();
        updateFigure();
    }// GEN-LAST:event_timeCoursesCheckBoxActionPerformed

    private void matlabplotItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_matlabplotItemActionPerformed
        try {
            InteractiveFigure.matlabPlot(xLabel, yLabels, xValues, yValues,
                    logXItem.isSelected(), logYItem.isSelected());
        }

        catch (MatlabConnectionException MCE) {
            System.out.println(MCE);
        }

        catch (MatlabInvocationException MIE) {
            System.out.println(MIE);
        }
        // TODO add your handling code here:
    }// GEN-LAST:event_matlabplotItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenuItem closeItem;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JPanel figurePanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JCheckBoxMenuItem logXItem;
    private javax.swing.JCheckBox logXbox;
    private javax.swing.JCheckBoxMenuItem logYItem;
    private javax.swing.JCheckBox logYbox;
    private javax.swing.JSplitPane mainPane;
    private javax.swing.JMenuItem matlabplotItem;
    private javax.swing.JTable rangeTable;
    private javax.swing.JMenuItem saveSelectedDataItem;
    private javax.swing.JMenuItem selectAllItem;
    private javax.swing.JList substanceList;
    private javax.swing.JLabel substanceListLabel;
    private javax.swing.JCheckBoxMenuItem timeCoursesCheckBox;
    private javax.swing.JMenu viewMenu;

    // End of variables declaration//GEN-END:variables

    class FigureAxisModel extends javax.swing.table.AbstractTableModel {
        final Object[][] data = { { "xmin", new Double(figure.getXmin()) },
                { "xmax", new Double(figure.getXmax()) },
                { "ymin", new Double(figure.getYmin()) },
                { "ymax", new Double(figure.getYmax()) } };

        public FigureAxisModel() {
        }

        public void setFigure() {
            data[0][1] = new Double(figure.getXmin());
            data[1][1] = new Double(figure.getXmax());
            data[2][1] = new Double(figure.getYmin());
            data[3][1] = new Double(figure.getYmax());

            for (int i = 0; i < 4; i++)
                fireTableCellUpdated(i, 1);
        }

        public int getColumnCount() {
            return data[0].length;
        }

        public String getColumnName(int c) {
            return null;
        }

        public int getRowCount() {
            return data.length;
        }

        public Object getValueAt(int row, int col) {
            return data[row][col];
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            return (col == 1);
        }

        public void setValueAt(Object value, int row, int col) {
            data[row][col] = value;
            fireTableCellUpdated(row, col);

            double newValue = ((Double) value).doubleValue();
            if (row == 0) {
                figure.setXmin(newValue);
            }

            else if (row == 1) {
                figure.setXmax(newValue);
            }

            else if (row == 2) {
                figure.setYmin(newValue);
            }

            else if (row == 3) {
                figure.setYmax(newValue);
            }

        }
    }

}
