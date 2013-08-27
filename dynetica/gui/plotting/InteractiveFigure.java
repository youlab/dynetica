package dynetica.gui.plotting;

import java.io.*;

import de.erichseifert.gral.data.*;
import de.erichseifert.gral.plots.XYPlot;
import de.erichseifert.gral.plots.Plot;
import de.erichseifert.gral.plots.axes.*;
import de.erichseifert.gral.plots.legends.*;
import de.erichseifert.gral.plots.lines.*;
import de.erichseifert.gral.plots.points.*;
import de.erichseifert.gral.ui.InteractivePanel;
import de.erichseifert.gral.util.*;
import de.erichseifert.gral.io.data.*;
import java.awt.Color;
import java.awt.Shape;

import matlabcontrol.*;

/**
 * This is a revised implementation of Figure.java. It provides a wrapper for
 * the XYPlot and related classes (Legend, Axis, LineRenderer, etc) from the
 * free java graphing library GRAL. It is meant to provide basic functionality
 * for plotting time courses or results from sensitivity analysis.
 * 
 * To use this class, create a new instance and set up the data by using the
 * plotData method. The method expects a label for the X-axis, a vector of
 * labels for y-values (these labels are used to create legends), a double
 * vector of xValues, and a double matrix of yValues. Then embed getPlotPanel()
 * in the content pane of a java swing class to display.
 * 
 * 
 * @author lingchong
 */

public class InteractiveFigure {
    // dataModel is maintained temporally for storing data to files.
    // DataModel dataModel = null;
    //

    //
    // 8/10/2013. These are defined to allow connection to Matlab to quickly
    // plot simulation data.
    //
    static MatlabProxyFactory matlabfactory = null;
    static MatlabProxy proxy = null;

    XYPlot plot = new XYPlot();
    InteractivePanel plotPanel = new InteractivePanel(plot);
    DataTable dataTable = null;
    // DataTable plotdata = new DataTable(Double.class, Double.class);

    double xMin = Double.MAX_VALUE;
    double xMax = -Double.MAX_VALUE;
    double yMin = Double.MAX_VALUE;
    double yMax = -Double.MAX_VALUE;

    int numXVar = 1; // # of x variables
    int numYVar = 1; // # of y variables per x variable

    boolean logX = false;
    boolean logY = false;
    String[] yLabels;
    String[] yLegends; // used to create legends.

    Color[] colors = { Color.blue, Color.green, Color.red, Color.black,
            Color.cyan, Color.magenta, Color.orange, Color.pink, Color.gray,
            Color.lightGray, Color.darkGray, Color.yellow };

    Shape[] shapes = {
            new java.awt.geom.Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0),
            new java.awt.geom.Ellipse2D.Double(-4.0, -4.0, 8.0, 8.0),
            new java.awt.geom.Rectangle2D.Double(-2.0, -2.0, 4.0, 4.0),
            new java.awt.geom.Rectangle2D.Double(-4.0, -4.0, 8.0, 8.0),

            new java.awt.geom.Ellipse2D.Double(-1.0, -1.0, 2.0, 2.0),
            new java.awt.geom.Ellipse2D.Double(-3.0, -3.0, 6.0, 6.0),
            new java.awt.geom.Rectangle2D.Double(-1.0, -1.0, 2.0, 2.0),
            new java.awt.geom.Rectangle2D.Double(-3.0, -3.0, 6.0, 6.0) };

    /** Holds value of property maxDataPoints. */
    private int maxDataPoints = 200;

    /** Holds value of property xLabel. */
    private String xLabel = "Time";

    public Color[] getColors() {
        return colors;
    }

    public void setColors(Color[] colors) {
        this.colors = colors;
    }

    public InteractivePanel getPlotPanel() {
        return plotPanel;
    }

    public InteractiveFigure() {
    }

    public InteractiveFigure(String xLabel, String[] labels, double[] xv,
            double[][] yv) {
        plotData(xLabel, labels, xv, yv);
    }

    /**
     * Plotting multiple y traces against one x variable (e.g. multiple time
     * courses)
     * 
     * @param xLabel
     *            : label for the x values
     * @param labels
     *            : labels for the y variables
     * @param xv
     *            [j]: j: index of x values
     * @param yv
     *            [i][j]: i: index of y variables; j: index of y values for each
     *            y variable.
     */

    public void plotData(String xLabel, String[] labels, double[] xv,
            double[][] yv) {
        // reset minimal and maximum values
        xMin = Double.MAX_VALUE;
        xMax = -Double.MAX_VALUE;
        yMin = Double.MAX_VALUE;
        yMax = -Double.MAX_VALUE;

        numXVar = 1;
        numYVar = yv.length;

        // create a new data table with yv.length columns
        dataTable = new DataTable(yv.length + 1, Double.class);

        int xLength = xv.length;
        int yLength = yv.length;
        this.xLabel = xLabel;
        this.yLabels = labels;
        this.yLegends = labels; // with a single trace of x values, keep the
                                // yLegends the same as yLabels.
        plot.clear();
        plot.setSetting(XYPlot.BACKGROUND, Color.WHITE);

        for (int j = 0; j < xLength; j++) {
            java.util.ArrayList dataRow = new java.util.ArrayList();
            dataRow.add(xv[j]);
            xMin = Math.min(xMin, xv[j]);
            xMax = Math.max(xMax, xv[j]);

            for (int i = 0; i < yLength; i++) {
                dataRow.add(yv[i][j]);

                yMin = Math.min(yMin, yv[i][j]);
                yMax = Math.max(yMax, yv[i][j]);

            }
            dataTable.add(dataRow);
        }

        if (xMin < 0)
            setLogX(false);
        if (yMin < 0)
            setLogY(false);

        for (int i = 0; i < yLength; i++) {
            //
            // create a new data series for plotting
            //
            DataSeries dataSeries;
            if (yLabels != null)
                dataSeries = new DataSeries(yLabels[i], dataTable, 0, i + 1);
            else
                dataSeries = new DataSeries(dataTable, 0, i + 1);

            plot.add(dataSeries);

            // format lines
            LineRenderer lines = new DefaultLineRenderer2D();
            plot.setLineRenderer(dataSeries, lines);
            Color currentColor;
            if (i < colors.length)
                currentColor = colors[i];
            else
                currentColor = new java.awt.Color((float) Math.random(),
                        (float) Math.random(), (float) Math.random());
            plot.getLineRenderer(dataSeries).setSetting(LineRenderer.COLOR,
                    currentColor);

            PointRenderer pointRenderer = plot.getPointRenderer(dataSeries);
            pointRenderer.setSetting(PointRenderer.COLOR,
                    GraphicsUtils.deriveDarker(currentColor));

            java.awt.Shape circle = new java.awt.geom.Ellipse2D.Double(-2.0,
                    -2.0, 4.0, 4.0);
            pointRenderer.setSetting(PointRenderer.SHAPE, circle);

        }

        formatPlot();

    }

    /**
     * Plotting multiple y traces against multiple x traces
     * 
     * @param xLabels
     *            : labels of x variables
     * @param yLabels
     *            : labels of y variables
     * @param xv
     *            [i][j] i: index of x variables; j: index of data points for
     *            each x variable
     * @param yv
     *            [i][k][j] i: index of x variables; k: index of y variables; j:
     *            index of y data points for each y variable
     */

    public void plotData(String[] xLabels, String[] yLabels, double[][] xv,
            double[][][] yv) {
        // reset minimal and maximum values
        xMin = Double.MAX_VALUE;
        xMax = -Double.MAX_VALUE;
        yMin = Double.MAX_VALUE;
        yMax = -Double.MAX_VALUE;

        numXVar = xv.length; // number of x variables
        numYVar = yv[0].length; // number of Y variables for each X variable
        int numPoints = xv[0].length; // number of data points for each trace.
        dataTable = new DataTable(numYVar + 1, Double.class);

        if (xv.length == 1)
            this.xLabel = xLabels[0];
        else
            this.xLabel = "parameters";

        this.yLabels = yLabels;
        if (yLabels != null)
            this.yLegends = new String[yLabels.length * numXVar];

        plot.clear();
        plot.setSetting(XYPlot.BACKGROUND, Color.WHITE);

        for (int i = 0; i < numXVar; i++) {

            //
            // set up a matrix (currentData) for each X varialbe.
            // X values are stored in the first column;
            // Y values are stored from 1 to numYVar columns.
            DataTable currentData = new DataTable(numYVar + 1, Double.class);
            for (int k = 0; k < numPoints; k++) {
                java.util.ArrayList dataRow = new java.util.ArrayList();
                dataRow.add(xv[i][k]);
                xMin = Math.min(xMin, xv[i][k]);
                xMax = Math.max(xMax, xv[i][k]);

                for (int j = 0; j < numYVar; j++) {
                    dataRow.add(yv[i][j][k]);

                    yMin = Math.min(yMin, yv[i][j][k]);
                    yMax = Math.max(yMax, yv[i][j][k]);

                }
                currentData.add(dataRow);

                //
                // also store the data to the overall dataTable for later
                // retrival
                dataTable.add(dataRow);
            }

            for (int j = 0; j < numYVar; j++) {

                DataSeries dataSeries;

                // construct labeled data series if yLabels are given.
                if (yLabels != null) {
                    String yLabelJ = yLabels[j];
                    if (numXVar > 1) {
                        yLabelJ = yLabelJ + " vs " + xLabels[i];
                    }

                    yLegends[i * numYVar + j] = yLabelJ;

                    dataSeries = new DataSeries(yLabelJ, currentData, 0, j + 1);
                }

                else
                    dataSeries = new DataSeries(currentData, 0, j + 1);

                if (xMin < 0)
                    setLogX(false);
                if (yMin < 0)
                    setLogY(false);

                plot.add(dataSeries);

                // format lines
                LineRenderer lines = new DefaultLineRenderer2D();
                plot.setLineRenderer(dataSeries, lines);
                Color currentColor;

                int colorIndex = i;
                if (colorIndex < colors.length)
                    currentColor = colors[colorIndex];
                else
                    currentColor = new java.awt.Color((float) Math.random(),
                            (float) Math.random(), (float) Math.random());
                plot.getLineRenderer(dataSeries).setSetting(LineRenderer.COLOR,
                        currentColor);

                PointRenderer pointRenderer = plot.getPointRenderer(dataSeries);
                Color pointColor;
                if (j % 2 == 0)
                    pointColor = GraphicsUtils.deriveDarker(currentColor);
                else
                    pointColor = GraphicsUtils.deriveBrighter(currentColor);
                pointRenderer.setSetting(PointRenderer.COLOR, pointColor);
                int ShapeIndex = j % shapes.length;
                // java.awt.Shape circle = new
                // java.awt.geom.Ellipse2D.Double(-2.0, -2.0, 4.0, 4.0);
                pointRenderer.setSetting(PointRenderer.SHAPE,
                        shapes[ShapeIndex]);

            }
        }

        formatPlot();

    }

    private void formatPlot() {
        // if there is a single trace, label the y-axis instead of using legends
        if (numYVar == 1 && numXVar == 1 && yLabels != null && xLabel != null) {
            plot.setInsets(new Insets2D.Double(20.0, 90.0, 60.0, 15.0));
            plot.setSetting(Plot.LEGEND, false);
            addXLabel();
            addYLabel();
        }

        else if (numYVar > 1 && numXVar == 1) {
            addXLabel();
            if (totalYLegendsLength() < 32)
                addLegends("NORTH");
            else
                addLegends("EAST");
        }

        else if (numYVar == 1 && numXVar > 1) {
            addXLabel();
            if (totalYLegendsLength() < 32)
                addLegends("NORTH");
            else
                addLegends("EAST");

        }

        else {
            addXLabel();
            if (totalYLegendsLength() < 32)
                addLegends("NORTH");
            else
                addLegends("EAST");
        }

        formatAxis();
    }

    private void formatAxis() {
        // Format axes
        Axis xAxis = plot.getAxis(XYPlot.AXIS_X);
        Axis yAxis = plot.getAxis(XYPlot.AXIS_Y);
        // The following code is to avoid trying to plot data that don't have a
        // significant range (Plotting
        // can freeze the program)
        //
        if ((Math.abs(xMax - xMin) <= 10.0 * Double.MIN_VALUE)
                && (xMin <= 10.0 * Double.MIN_VALUE)) {
            xMin = 0.0;
            xMax = 1.0;
        }

        if ((Math.abs(yMax - yMin) <= 10.0 * Double.MIN_VALUE)
                && (yMin <= 10.0 * Double.MIN_VALUE)) {
            yMin = -1.0;
            yMax = 1.0;
        }

        xAxis.setMax(xMax * 1.02);
        yAxis.setMax(yMax * 1.02);

        AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
        axisRendererX.setSetting(AxisRenderer.TICKS_MINOR, false);
        axisRendererY.setSetting(AxisRenderer.TICKS_MINOR, false);

        if ((logX) && xMin >= 0.0) {
            double xLogMin = Math.max(xMin, xMax / 1.0e3);
            xAxis.setMin(xLogMin);
            double tickSpacing = Math
                    .min(4.0, Math.log10(xMax / xLogMin) + 1.0);
            axisRendererX.setSetting(AxisRenderer.TICKS_SPACING, tickSpacing);
        } else
            xAxis.setMin(xMin);

        if ((logY) && yMin >= 0.0) {

            double yLogMin = Math.max(yMin, yMax / 1.0e3);
            yAxis.setMin(yLogMin);
            double tickSpacing = Math
                    .min(4.0, Math.log10(yMax / yLogMin) + 1.0);
            axisRendererY.setSetting(AxisRenderer.TICKS_SPACING, tickSpacing);
        } else
            yAxis.setMin(yMin);
    }

    private void addXLabel() {
        AxisRenderer axisRendererX = plot.getAxisRenderer(XYPlot.AXIS_X);
        axisRendererX.setSetting(AxisRenderer.LABEL, xLabel);

    }

    private void addYLabel() {
        AxisRenderer axisRendererY = plot.getAxisRenderer(XYPlot.AXIS_Y);
        axisRendererY.setSetting(AxisRenderer.LABEL, yLabels[0]);
    }

    public void addLegends(String location) {
        if (yLegends == null) {
            plot.setSetting(Plot.LEGEND, false);
            plot.setInsets(new Insets2D.Double(20.0, 50.0, 60.0, 20.0));
            return;
        }
        plot.setSetting(Plot.LEGEND, true);
        Legend legend = plot.getLegend();

        if (location.equals("EAST") || location.equals("WEST")) {
            legend.setSetting(Legend.ORIENTATION, Orientation.VERTICAL);
            double legendSpace = 100.0 + 6.5 * maxYLegendsLength(); // needs to
                                                                    // be fine
                                                                    // tuned.
            if (location.equals("EAST")) {
                plot.setSetting(Plot.LEGEND_LOCATION, Location.EAST);
                plot.setInsets(new Insets2D.Double(20.0, 50.0, 60.0,
                        legendSpace));
            } else {
                plot.setSetting(Plot.LEGEND_LOCATION, Location.WEST);
                plot.setInsets(new Insets2D.Double(20.0, legendSpace, 60.0,
                        15.0));

            }
        }

        else {
            legend.setSetting(Legend.ORIENTATION, Orientation.HORIZONTAL);
            if (location.equals("NORTH")) {
                plot.setSetting(Plot.LEGEND_LOCATION, Location.NORTH);
                plot.setInsets(new Insets2D.Double(90.0, 50.0, 60.0, 15.0));
            } else {
                plot.setSetting(Plot.LEGEND_LOCATION, Location.SOUTH);
                plot.setInsets(new Insets2D.Double(20.0, 50.0, 150.0, 15.0));
            }
        }

    }

    public void showLegend(boolean s) {
        if (s)
            formatPlot();
        else {
            plot.setSetting(Plot.LEGEND, false);
            plot.setInsets(new Insets2D.Double(20.0, 50.0, 60.0, 15.0));
            addXLabel();
        }
    }

    public void setLogX(boolean logX) {
        this.logX = logX;
        if (logX && xMin >= 0.0) {
            AxisRenderer axisRendererX = new LogarithmicRenderer2D();
            axisRendererX.setSetting(AxisRenderer.LABEL, xLabel);
            // axisRendererX.setSetting(AxisRenderer.TICKS_SPACING, 2.0);
            plot.setAxisRenderer(XYPlot.AXIS_X, axisRendererX);
        } else {
            AxisRenderer axisRendererX = new LinearRenderer2D();
            axisRendererX.setSetting(AxisRenderer.LABEL, xLabel);
            plot.setAxisRenderer(XYPlot.AXIS_X, axisRendererX);
        }

        formatPlot();
    }

    public void setLogY(boolean logY) {
        this.logY = logY;
        if (logY && yMin >= 0.0) {
            AxisRenderer axisRendererY = new LogarithmicRenderer2D();
            axisRendererY.setSetting(AxisRenderer.TICKS_SPACING, 2.0);
            plot.setAxisRenderer(XYPlot.AXIS_Y, axisRendererY);

        } else {
            AxisRenderer axisRendererY = new LinearRenderer2D();
            plot.setAxisRenderer(XYPlot.AXIS_Y, axisRendererY);

        }
        formatPlot();
    }

    public void saveData(File f) {
        DataWriterFactory factory = DataWriterFactory.getInstance();
        DataWriter writer = factory.get("text/tab-separated-values");

        try {
            writer.write(dataTable, new FileOutputStream(f));
        } catch (IOException e) {
        }
    }

    public void saveData(String fileName) {
        DataWriterFactory factory = DataWriterFactory.getInstance();
        DataWriter writer = factory.get("text/tab-separated-values");

        try {
            writer.write(dataTable, new FileOutputStream(fileName));
        } catch (IOException e) {
        }
    }

    /**
     * Getter for property xmax.
     * 
     * @return Value of property xmax.
     */
    public double getXmax() {
        return xMax;
    }

    /**
     * Getter for property xmin.
     * 
     * @return Value of property xmin.
     */
    public double getXmin() {
        return xMin;
    }

    /**
     * Getter for property ymax.
     * 
     * @return Value of property ymax.
     */
    public double getYmax() {
        return yMax;
    }

    /**
     * Getter for property ymin.
     * 
     * @return Value of property ymin.
     */
    public double getYmin() {
        return yMin;
    }

    /**
     * Setter for property xmax.
     * 
     * @param xmax
     *            New value of property xmax.
     */
    public void setXmax(double xmax) {
        xMax = xmax;
        plot.getAxis(XYPlot.AXIS_X).setMax(xmax);
        // repaint();
    }

    /**
     * Setter for property xmin.
     * 
     * @param xmin
     *            New value of property xmin.
     */
    public void setXmin(double xmin) {
        xMin = xmin;
        plot.getAxis(XYPlot.AXIS_X).setMin(xmin);

        // repaint();
    }

    /**
     * Setter for property ymax.
     * 
     * @param ymax
     *            New value of property ymax.
     */
    public void setYmax(double ymax) {
        yMax = ymax;
        plot.getAxis(XYPlot.AXIS_Y).setMax(ymax);
        // repaint();
    }

    /**
     * Setter for property ymin.
     * 
     * @param ymin
     *            New value of property ymin.
     */
    public void setYmin(double ymin) {
        yMin = ymin;
        plot.getAxis(XYPlot.AXIS_Y).setMin(ymin);
        // repaint();
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
        this.maxDataPoints = maxDataPoints;
    }

    /**
     * Getter for property xLabel.
     * 
     * @return Value of property xLabel.
     */
    public String getXLabel() {
        return xLabel;
    }

    /**
     * Setter for property xLabel.
     * 
     * @param xLabel
     *            New value of property xLabel.
     */
    public void setXLabel(String xLabel) {
        this.xLabel = xLabel;
        plot.getAxisRenderer(XYPlot.AXIS_X).setSetting(AxisRenderer.LABEL,
                xLabel);
    }

    /**
     * 
     * @param file
     *            : target file
     * @param xLabel
     *            : label for the x values
     * @param labels
     *            : labels for the y variables
     * @param xv
     *            [j]: j: index of x values
     * @param yv
     *            [i][j]: i: index of y variables; j: index of y values for each
     *            y variable.
     */

    public static void saveData(File file, String xLabel, String[] labels,
            double[] xv, double[][] yv) {

    }

    public static void saveData(File file, String xLabels[],
            String[] yLabels[], double[][] xv, double[][][] yv) {

    }

    // get maximum length of tye ylabels for formatting purposes.
    private int maxYLegendsLength() {
        int maxLength = 0;
        if (yLegends == null)
            return maxLength;
        for (int i = 0; i < yLegends.length; i++) {
            // System.out.println(yLabels[i]);
            maxLength = Math.max(maxLength, yLegends[i].length());
            // System.out.println(maxLength);
        }
        return maxLength;
    }

    private int totalYLegendsLength() {
        int totalLength = 0;
        if (yLegends == null)
            return totalLength;
        for (int i = 0; i < yLegends.length; i++) {
            // System.out.println(yLabels[i]);
            totalLength += yLegends[i].length();
            // System.out.println(maxLength);
        }
        return totalLength;
    }

    //
    // L. You, 7/28/2013. The following code is writen to to plot data in Matlab
    //

    /**
     * Plotting one y series against one x series
     * 
     * @param xLabel
     * @param yLabel
     * @param xValues
     * @param yValues
     * @param logX
     * @param logY
     * @throws MatlabConnectionException
     * @throws MatlabInvocationException
     */
    public static void matlabPlot(String xLabel, String yLabel,
            double[] xValues, double[] yValues, boolean logX, boolean logY,
            String lineStyle) throws MatlabConnectionException,
            MatlabInvocationException {

        if (matlabfactory == null)
            matlabfactory = new MatlabProxyFactory();
        if (proxy == null)
            proxy = matlabfactory.getProxy();

        String xName = "X";
        String yName = "Y";

        if (xLabel != null)
            xName = xLabel;
        if (yLabel != null)
            yName = yLabel;

        proxy.setVariable(xName, xValues);
        proxy.setVariable(yName, yValues);
        if (logX && logY)
            proxy.eval("loglog(" + xName + "," + yName + ", '" + lineStyle
                    + "')");
        else if (logX)
            proxy.eval("semilogx(" + xName + "," + yName + ", '" + lineStyle
                    + "')");
        else if (logY)
            proxy.eval("semilogy(" + xName + "," + yName + ", '" + lineStyle
                    + "')");
        else
            proxy.eval("plot(" + xName + "," + yName + ", '" + lineStyle + "')");

        if (xLabel != null)
            proxy.feval("xlabel", xLabel);
        if (yLabel != null) {
            proxy.feval("ylabel", yLabel);
            proxy.feval("legend", yLabel);
        }

    }

    /**
     * Plotting multiple y-series against one x-series
     * 
     * @param xLabel
     * @param yLabels
     * @param xValues
     *            [j] j: index of x values
     * @param yValues
     *            [i][j] i: index of y variables; j: index of data points for
     *            each y variable
     * @param logX
     * @param logY
     * @throws MatlabConnectionException
     * @throws MatlabInvocationException
     */
    public static void matlabPlot(String xLabel, String[] yLabels,
            double[] xValues, double[][] yValues, boolean logX, boolean logY)
            throws MatlabConnectionException, MatlabInvocationException {

        // Create a proxy, which we will use to control MATLAB
        if (matlabfactory == null)
            matlabfactory = new MatlabProxyFactory();
        if (proxy == null)
            proxy = matlabfactory.getProxy();

        String[] lineStyles = { "k-", "r-", "b-", "g-", "ks-", "kd-", "rs-",
                "rd-", "gs-", "gd-", "bs-", "bd-", "ks:", "rs:", "bs:", "gs:",
                "k.", "r.", "b.", "g." };

        String xName = "X";
        String[] yNames = new String[yValues.length];

        // if (xLabel != null) xName = xLabel;
        // if (yLabels != null && yLabels.length == 1 ) yNames [0] = yLabels
        // [0];

        proxy.eval("figure");
        proxy.eval("hold on");

        proxy.setVariable(xName, xValues);

        for (int i = 0; i < yValues.length; i++) {
            yNames[i] = "Y" + i;
            int lineStyleIndex = i % lineStyles.length;
            matlabPlot(xName, yNames[i], xValues, yValues[i], logX, logY,
                    lineStyles[lineStyleIndex]);
        }

        proxy.eval("hold off");
        //
        // if there're multiple traces, turn off Y axis label
        // add legends.
        proxy.feval("xlabel", xLabel);
        if (yLabels != null && yLabels.length > 1) {
            proxy.feval("ylabel", "");
            proxy.setVariable("onelegend", yLabels[0]);
            proxy.eval("ylegends = {onelegend}");
            for (int i = 1; i < yLabels.length; i++) {
                proxy.setVariable("onelegend", yLabels[i]);
                proxy.eval("ylegends = [ylegends  onelegend]");
                proxy.eval("legend(ylegends)");
            }
        }
    }

    /**
     * Plotting multiple y traces against multiple x traces.
     * 
     * @param xLabels
     *            -- its length corresponds to number of x variables
     * @param yLabels
     *            -- its length corresponds to number of y variables for each x
     *            variable
     * @param xValues
     *            [i][j] i: index of x variables; j index of data points for
     *            each x variables
     * @param yValues
     *            [i][k][j] i: index of x variables; k index of y variables for
     *            each x variable; j: index of data points for each y variable
     * @param logX
     * @param logY
     * @throws MatlabConnectionException
     * @throws MatlabInvocationException
     */
    public static void matlabPlot(String[] xLabels, String[] yLabels,
            double[][] xValues, double[][][] yValues, boolean logX, boolean logY

    ) throws MatlabConnectionException, MatlabInvocationException {

        // Create a proxy, which we will use to control MATLAB
        if (matlabfactory == null)
            matlabfactory = new MatlabProxyFactory();
        if (proxy == null)
            proxy = matlabfactory.getProxy();

        if (xValues == null || yValues == null)
            return;
        if (xValues.length != yValues.length)
            return;
        if (xLabels != null && xLabels.length != xValues.length)
            return;
        if (yLabels != null && yLabels.length != yValues[0].length)
            return;

        String[] yNames = new String[yValues[0].length];

        for (int i = 0; i < xValues.length; i++) {
            if (xLabels != null && yLabels != null) {
                for (int j = 0; j < yLabels.length; j++)
                    yNames[j] = yLabels[j] + " vs " + xLabels[i];

                //
                // maybe better to call the single-trace plot for easier
                // tracking of data and labels
                //
                matlabPlot(xLabels[i], yNames, xValues[i], yValues[i], logX,
                        logY);
            } else
                matlabPlot(null, null, xValues[i], yValues[i], logX, logY);
        }
    }

} // Figure
