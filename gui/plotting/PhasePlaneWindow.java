package dynetica.gui.plotting;

/**
 *
 * @author  Lingchong You
 * @version 0.1
 */
import dynetica.entity.*;
import java.util.*;
import javax.swing.*;
import java.io.*;
import matlabcontrol.*;

/**
 * August 2013. This is updated to use the more functional InteractiveFigure
 * class (wrapping the XYplot from the GRAL library.
 * 
 * @author lingchong
 */

public class PhasePlaneWindow extends javax.swing.JFrame {
    InteractiveFigure figure = new InteractiveFigure();
    // java.awt.Color colors[] ;

    // final int nSubs;
    final DefaultListModel xNames = new DefaultListModel(); // xNames has an
                                                            // item for "Time".
    final DefaultListModel yNames = new DefaultListModel();

    final dynetica.system.ReactiveSystem system;
    List substances = new ArrayList();
    List expressions = new ArrayList();

    double[] xValues;
    double[][] yValues;
    String xLabel;
    String[] yLabels;

    FigureAxisModel figureAxisModel;
    AutoUpdateThread autoUpdateThread = new AutoUpdateThread();

    //
    // 7/28/2013. These are defined to allow connection to Matlab to quickly
    // plot simulation data.
    //
    MatlabProxyFactory matlabfactory = null;
    MatlabProxy proxy = null;

    //
    // Constructs a FigureWindow that plots data directly from
    // a ReactiveSystem
    //

    public PhasePlaneWindow(dynetica.system.ReactiveSystem system) {
        super("Figure window for system " + system.getName());
        this.system = system;
        List subsAndExprs = system.getSubstances(); // this returns all the
                                                    // substances and expresion
                                                    // variables

        for (int i = 0; i < subsAndExprs.size(); i++) {
            Substance s = (Substance) (subsAndExprs.get(i));
            if (!(s instanceof ExpressionVariable))
                substances.add(s);
        }

        expressions = system.getExpressions();

        int nSubs = substances.size(); // substances.length * 2;
        System.out.println("# of substances  = " + nSubs);
        xNames.addElement(system.getTimer().getName());
        Substance s;

        //
        // Set up the list of substances and their rates
        //
        for (int i = 0; i < nSubs; i++) {
            s = (Substance) (substances.get(i));
            xNames.addElement(s.getName());
            yNames.addElement(s.getName());
        }

        for (int i = nSubs; i < 2 * nSubs; i++) {
            s = (Substance) (substances.get(i - nSubs));
            xNames.addElement("Rate of " + s.getName());
            yNames.addElement("Rate of " + s.getName());
        }

        //
        // Set up the list of expressions but no rates.
        //
        int nExprs = expressions.size();
        System.out.println("# of expressions  = " + nExprs);
        for (int i = 0; i < nExprs; i++) {
            ExpressionVariable expr = (ExpressionVariable) (expressions.get(i));
            xNames.addElement(expr.getName());
            yNames.addElement(expr.getName());
        }

        initComponents();
        figurePanel.add(figure.getPlotPanel(), java.awt.BorderLayout.CENTER);
        updateFigure();
        pack();
    }

    public final void updateFigure() {
        int nSubs = substances.size();
        int nExprs = expressions.size();

        //
        // set up data for x-axis; only a single trace can be selected.
        //
        double[] completeXvalues = system.getTimer().getTimePoints();
        int xIndex = xList.getSelectedIndex();
        xLabel = "Time";
        if (xIndex > 0 && xIndex <= nSubs) {
            Substance s = (Substance) (substances.get(xIndex - 1));
            completeXvalues = s.getValues();
            xLabel = s.getName();
        }

        else if (xIndex > nSubs && xIndex <= 2 * nSubs) {
            Substance s = (Substance) (substances.get(xIndex - nSubs - 1));
            completeXvalues = s.getRates();
            xLabel = "Rate of " + s.getName();
        }

        else if (xIndex > 2 * nSubs) {
            ExpressionVariable expr = (ExpressionVariable) (expressions
                    .get(xIndex - 2 * nSubs - 1));
            completeXvalues = expr.getValues();
            xLabel = expr.getName();
        }

        int nTotalPoints = completeXvalues.length;

        //
        // set up data for y values. Multipe traces can be selected.
        //

        int[] indices = yList.getSelectedIndices();

        //
        // if the total number of time points exceeds maxDataPoints, sample only
        // maxDataPoints of data points out the time courses.
        //
        int dataPoints = Math.min(nTotalPoints, maxDataPoints);

        double samplingStep = (double) nTotalPoints / dataPoints;

        xValues = new double[dataPoints];
        yValues = new double[indices.length][dataPoints];
        yLabels = new String[indices.length];

        for (int j = 0; j < dataPoints; j++) {
            xValues[j] = completeXvalues[(int) Math.floor(j * samplingStep)];
        }

        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < nSubs) {
                Substance s = (Substance) (substances.get(indices[i]));
                for (int j = 0; j < dataPoints; j++) {
                    double sj = s.getValues()[(int) Math
                            .floor(j * samplingStep)];
                    yValues[i][j] = sj;
                }
            }

            // otherwise, get rates instead of values
            else if (indices[i] < 2 * nSubs) {
                Substance s = (Substance) (substances.get(indices[i] - nSubs));
                for (int j = 0; j < dataPoints; j++) {
                    double sj = s.getRates()[(int) Math.floor(j * samplingStep)];
                    yValues[i][j] = sj;
                }
            }

            else {
                ExpressionVariable expr = (ExpressionVariable) (expressions
                        .get(indices[i] - 2 * nSubs));
                for (int j = 0; j < dataPoints; j++) {
                    double exprJ = expr.getValues()[(int) Math.floor(j
                            * samplingStep)];
                    yValues[i][j] = exprJ;
                }
            }

            yLabels[i] = (String) (yNames.get(indices[i]));
        }

        figure.plotData(xLabel, yLabels, xValues, yValues);
        figure.setLogX(logXItem.getState());
        figure.setLogY(logYItem.getState());

        figurePanel.repaint();

        if (figureAxisModel == null) {
            figureAxisModel = new FigureAxisModel();
            rangeTable.setModel(figureAxisModel);
        } else
            figureAxisModel.setFigure();

        processBar.setValue(nTotalPoints);
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
        jPanel7 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        xList = new javax.swing.JList();
        xListLabel = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        yList = new javax.swing.JList();
        yListLabel = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        rangeTable = new javax.swing.JTable();
        jPanel1 = new javax.swing.JPanel();
        logXbox = new javax.swing.JCheckBox();
        logYbox = new javax.swing.JCheckBox();
        figurePanel = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        processBar = new javax.swing.JProgressBar();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        maxPointsSlider = new javax.swing.JSlider();
        jMenuBar1 = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        saveSelectedDataItem = new javax.swing.JMenuItem();
        saveDataItem = new javax.swing.JMenuItem();
        matlabPlotMenuItem = new javax.swing.JMenuItem();
        closeItem = new javax.swing.JMenuItem();
        exitItem = new javax.swing.JMenuItem();
        simMenu = new javax.swing.JMenu();
        startItem = new javax.swing.JMenuItem();
        pauseItem = new javax.swing.JMenuItem();
        resumeItem = new javax.swing.JMenuItem();
        viewMenu = new javax.swing.JMenu();
        logXItem = new javax.swing.JCheckBoxMenuItem();
        logYItem = new javax.swing.JCheckBoxMenuItem();
        selectAllItem = new javax.swing.JMenuItem();
        autoUpdateBox = new javax.swing.JCheckBoxMenuItem();

        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                exitForm(evt);
            }
        });

        mainPane.setDividerLocation(110);
        mainPane.setLastDividerLocation(10);
        mainPane.setPreferredSize(new java.awt.Dimension(500, 550));

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(5);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
        jSplitPane1.setLastDividerLocation(80);

        jPanel2.setLayout(new java.awt.GridLayout(2, 1));

        jPanel7.setLayout(new java.awt.BorderLayout());

        jScrollPane1.setMinimumSize(new java.awt.Dimension(60, 100));
        jScrollPane1.setPreferredSize(new java.awt.Dimension(60, 200));

        xList.setModel(xNames);
        xList.setSelectedIndex(0);
        xList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        xList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                xListValueChanged(evt);
            }
        });
        jScrollPane1.setViewportView(xList);

        jPanel7.add(jScrollPane1, java.awt.BorderLayout.CENTER);

        xListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        xListLabel.setText("X value");
        xListLabel
                .setToolTipText("The variable chosen as the x value. Time is set as the X-axis by default.");
        jPanel7.add(xListLabel, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel7);

        jPanel8.setLayout(new java.awt.BorderLayout());

        jScrollPane2.setMinimumSize(new java.awt.Dimension(60, 100));
        jScrollPane2.setPreferredSize(new java.awt.Dimension(60, 200));

        yList.setModel(yNames);
        yList.setSelectedIndex(0);
        yList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                yListValueChanged(evt);
            }
        });
        jScrollPane2.setViewportView(yList);

        jPanel8.add(jScrollPane2, java.awt.BorderLayout.CENTER);

        yListLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        yListLabel.setText("Y values");
        yListLabel
                .setToolTipText("The Y-values. By default the first substance on the list is selected.");
        jPanel8.add(yListLabel, java.awt.BorderLayout.NORTH);

        jPanel2.add(jPanel8);

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

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel2.setText("Progress");
        jPanel4.add(jLabel2);

        processBar.setMaximum(system.getAlgorithm().getIterations());
        processBar.setValue(system.getTimer().getTimePoints().length);
        processBar.setMaximumSize(new java.awt.Dimension(300, 21));
        processBar.setPreferredSize(new java.awt.Dimension(160, 20));
        processBar.setStringPainted(true);
        jPanel4.add(processBar);

        figurePanel.add(jPanel4, java.awt.BorderLayout.NORTH);

        jPanel5.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 11)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel1.setText("Resolution");
        jLabel1.setToolTipText("Maximum number of time points to be drawn");
        jLabel1.setMaximumSize(new java.awt.Dimension(200, 16));
        jLabel1.setMinimumSize(new java.awt.Dimension(40, 16));
        jLabel1.setPreferredSize(new java.awt.Dimension(60, 16));
        jPanel5.add(jLabel1);

        maxPointsSlider.setFont(new java.awt.Font("Arial", 0, 8)); // NOI18N
        maxPointsSlider.setMajorTickSpacing(system.getAlgorithm()
                .getIterations() / 4);
        maxPointsSlider.setMaximum(system.getAlgorithm().getIterations());
        maxPointsSlider.setPaintLabels(true);
        maxPointsSlider.setPaintTicks(true);
        maxPointsSlider.setToolTipText("Maximum number of time points");
        maxPointsSlider.setValue(getMaxDataPoints());
        maxPointsSlider.setMaximumSize(new java.awt.Dimension(200, 32767));
        maxPointsSlider.setMinimumSize(new java.awt.Dimension(40, 40));
        maxPointsSlider.setName(""); // NOI18N
        maxPointsSlider.setPreferredSize(new java.awt.Dimension(200, 50));
        maxPointsSlider
                .addChangeListener(new javax.swing.event.ChangeListener() {
                    public void stateChanged(javax.swing.event.ChangeEvent evt) {
                        maxPointsSliderStateChanged(evt);
                    }
                });
        jPanel5.add(maxPointsSlider);

        figurePanel.add(jPanel5, java.awt.BorderLayout.SOUTH);

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

        saveDataItem.setText("Save all data");
        saveDataItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveDataItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveDataItem);

        matlabPlotMenuItem.setText("Plot in Matlab");
        matlabPlotMenuItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        matlabPlotMenuItemActionPerformed(evt);
                    }
                });
        fileMenu.add(matlabPlotMenuItem);

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

        simMenu.setText("Simulation");

        startItem.setText("Start");
        startItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                startItemActionPerformed(evt);
            }
        });
        simMenu.add(startItem);

        pauseItem.setText("Pause");
        pauseItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pauseItemActionPerformed(evt);
            }
        });
        simMenu.add(pauseItem);

        resumeItem.setText("Resume");
        resumeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resumeItemActionPerformed(evt);
            }
        });
        simMenu.add(resumeItem);

        jMenuBar1.add(simMenu);

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

        autoUpdateBox.setSelected(true);
        autoUpdateBox.setText("Automated Updating");
        autoUpdateBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoUpdateBoxActionPerformed(evt);
            }
        });
        viewMenu.add(autoUpdateBox);

        jMenuBar1.add(viewMenu);

        setJMenuBar(jMenuBar1);
    }// </editor-fold>//GEN-END:initComponents

    private void saveDataItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_saveDataItemActionPerformed
        saveData();
    }// GEN-LAST:event_saveDataItemActionPerformed

    private void yListValueChanged(javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_yListValueChanged
        updateFigure();
    }// GEN-LAST:event_yListValueChanged

    private void resumeItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_resumeItemActionPerformed
        system.getAlgorithm().resume();
        autoUpdate();
    }// GEN-LAST:event_resumeItemActionPerformed

    private void pauseItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_pauseItemActionPerformed
        system.getAlgorithm().pause();
        autoUpdateThread.pause();
    }// GEN-LAST:event_pauseItemActionPerformed

    private void startItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_startItemActionPerformed
        system.getAlgorithm().start();
        autoUpdate();
    }// GEN-LAST:event_startItemActionPerformed

    private void maxPointsSliderStateChanged(javax.swing.event.ChangeEvent evt) {// GEN-FIRST:event_maxPointsSliderStateChanged
        setMaxDataPoints(maxPointsSlider.getValue());
    }// GEN-LAST:event_maxPointsSliderStateChanged

    private void autoUpdateBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_autoUpdateBoxActionPerformed
        autoUpdate();
    }// GEN-LAST:event_autoUpdateBoxActionPerformed

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
        int[] allIndices = new int[yNames.size()];
        for (int i = 0; i < allIndices.length; i++)
            allIndices[i] = i;
        yList.setSelectedIndices(allIndices);
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
        System.out.println("At phase plane window");
        System.exit(0);
    }// GEN-LAST:event_exitItemActionPerformed

    private void closeItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_closeItemActionPerformed
           System.out.println("At phase plane window");
        this.dispose();
    }// GEN-LAST:event_closeItemActionPerformed

    private void logXItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logXItemActionPerformed
        figure.setLogX(logXItem.getState());
        logXbox.setSelected(logXItem.getState());
        figurePanel.repaint();
    }// GEN-LAST:event_logXItemActionPerformed

    private void logYItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_logYItemActionPerformed
        figure.setLogY(logYItem.getState());
        logYbox.setSelected(logYItem.getState());
        figurePanel.repaint();

    }// GEN-LAST:event_logYItemActionPerformed

    private void xListValueChanged(javax.swing.event.ListSelectionEvent evt) {// GEN-FIRST:event_xListValueChanged
        updateFigure();
    }// GEN-LAST:event_xListValueChanged

    /** Exit the Application */
    private void exitForm(java.awt.event.WindowEvent evt) {// GEN-FIRST:event_exitForm
        autoUpdateThread = null;
        this.dispose();
    }// GEN-LAST:event_exitForm

    private void matlabPlotMenuItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_matlabPlotMenuItemActionPerformed
        try {
            dynetica.gui.plotting.InteractiveFigure.matlabPlot(xLabel, yLabels,
                    xValues, yValues, logXItem.isSelected(),
                    logYItem.isSelected());
        }

        catch (MatlabConnectionException MCE) {
            System.out.println(MCE);
        }

        catch (MatlabInvocationException MIE) {
            System.out.println(MIE);
        }
    }// GEN-LAST:event_matlabPlotMenuItemActionPerformed

    private void saveData() {
        JFileChooser fileChooser = new JFileChooser();
        int returnVal = fileChooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            File output = fileChooser.getSelectedFile();
            try {
                PrintWriter out = new PrintWriter(new FileOutputStream(output));
                double[] x = system.getTimer().getTimePoints();
                int length = x.length;
                double[][] y = new double[substances.size()][length];
                out.print("Time " + "\t");
                for (int i = 0; i < substances.size(); i++) {
                    Substance s = (Substance) substances.get(i);
                    out.print(s.getName());
                    out.print("\t");

                    y[i] = s.getValues();
                }
                out.println();

                for (int i = 0; i < length; i++) {
                    out.print(x[i]);
                    out.print("\t");
                    for (int j = 0; j < y.length; j++) {
                        out.print(y[j][i]);
                        out.print("\t");
                    }
                    out.println();
                }
                out.close();
            } catch (IOException ioe) {
                // System.out.printStack(ioe);
            }
        }
    }

    public void autoUpdate() {
        if (autoUpdateBox.getState()) {
            autoUpdateThread.start();
        } else {
            autoUpdateThread.pause();
        }

    }

    /**
     * Getter for property maxDataPoints.
     * 
     * @return Value of property maxDataPoints.
     */
    public int getMaxDataPoints() {
        return maxDataPoints;
    }

    /**
     * Setter for property maxDataPoints.
     * 
     * @param maxDataPoints
     *            New value of property maxDataPoints.
     */
    public void setMaxDataPoints(int maxDataPoints) {
        this.maxDataPoints = Math.max(maxDataPoints, 1);
        updateFigure();
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBoxMenuItem autoUpdateBox;
    private javax.swing.JMenuItem closeItem;
    private javax.swing.JMenuItem exitItem;
    private javax.swing.JPanel figurePanel;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JCheckBoxMenuItem logXItem;
    private javax.swing.JCheckBox logXbox;
    private javax.swing.JCheckBoxMenuItem logYItem;
    private javax.swing.JCheckBox logYbox;
    private javax.swing.JSplitPane mainPane;
    private javax.swing.JMenuItem matlabPlotMenuItem;
    private javax.swing.JSlider maxPointsSlider;
    private javax.swing.JMenuItem pauseItem;
    private javax.swing.JProgressBar processBar;
    private javax.swing.JTable rangeTable;
    private javax.swing.JMenuItem resumeItem;
    private javax.swing.JMenuItem saveDataItem;
    private javax.swing.JMenuItem saveSelectedDataItem;
    private javax.swing.JMenuItem selectAllItem;
    private javax.swing.JMenu simMenu;
    private javax.swing.JMenuItem startItem;
    private javax.swing.JMenu viewMenu;
    private javax.swing.JList xList;
    private javax.swing.JLabel xListLabel;
    private javax.swing.JList yList;
    private javax.swing.JLabel yListLabel;
    // End of variables declaration//GEN-END:variables

    /** Holds value of property maxDataPoints. */
    private int maxDataPoints = 100;

    class AutoUpdateThread extends Thread {
        Thread controlThread = null;

        @Override
        public void run() {
            dynetica.algorithm.Algorithm algo = system.getAlgorithm();
            while (controlThread != null) {
                if (!algo.isFinished() && !algo.isInterrupted()) {
                    try {
                        updateFigure();
                        // sleep(100);
                    } catch (Exception e) {
                        break;
                    }
                } else if (algo.isFinished()) {
                    updateFigure();
                    break;
                }
            }
            //
            // stop the thread at the end of run
            //
            controlThread = null;
        }

        public void pause() {
            controlThread = null;
        }

        @Override
        public void start() {
            if (controlThread == null) {
                controlThread = new Thread(this);
                controlThread.start();
            }
        }
    }

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
                figurePanel.repaint();
            }

            else if (row == 1) {
                figure.setXmax(newValue);
                figurePanel.repaint();
            }

            else if (row == 2) {
                figure.setYmin(newValue);
                figurePanel.repaint();
            }

            else if (row == 3) {
                figure.setYmax(newValue);
                figurePanel.repaint();
            }

        }
    }

}
