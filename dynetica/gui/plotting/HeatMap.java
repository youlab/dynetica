/*
* To change this license header, choose License Headers in Project Properties.
* To change this template file, choose Tools | Templates
* and open the template in the editor.
*/
package dynetica.gui.plotting;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

import javax.swing.JPanel;

/**
 *
 * @author Billy Wan Apr 2016
 * from Beckler et al.
 * 
 */
public class HeatMap extends JPanel {
    private double[][] data;
    private double[][] plottingData;
    private int[][] dataColorIndices;
    
    // these four variables are used to print the axis labels
    private double xMin;
    private double xMax;
    private double yMin;
    private double yMax;
    
    private String title;
    private String xAxis;
    private String yAxis;
    
    private Color[] colors;
    private Color bg = Color.white;
    private Color fg = Color.black;
    
    private BufferedImage bufferedImage;
    private Graphics2D bufferedGraphics;
    
    /**
     * @param data
     *            The data to display, must be a complete array (non-ragged)
     * @param colors
     *            A variable of the type Color[]. See also
     *            {@link #createMultiGradient} and {@link #createGradient}.
     */
    public HeatMap(double[][] data, Color[] colors) {
        super();
        
        this.data = data;
        this.colors = colors;
        this.setPreferredSize(new Dimension(60 + data.length,
                60 + data[0].length));
        this.setDoubleBuffered(true);
        
        this.bg = Color.white;
        this.fg = Color.black;
        
        // this is the expensive function that draws the data plot into a
        // BufferedImage. The data plot is then cheaply drawn to the screen when
        // needed, saving us a lot of time in the end.
        drawData(this.data);
    }
    
    // return data
    public double[][] getData() {
        return data;
    }
    
    /**
     * Specify the coordinate bounds for the map. Only used for the axis labels,
     * which must be enabled separately. Calls repaint() when finished.
     *
     * @param xMin
     *            The lower bound of x-values, used for axis labels
     * @param xMax
     *            The upper bound of x-values, used for axis labels
     */
    public void setCoordinateBounds(double xMin, double xMax, double yMin,
            double yMax) {
        this.xMin = xMin;
        this.xMax = xMax;
        this.yMin = yMin;
        this.yMax = yMax;
        
        repaint();
    }
    
    /**
     * Updates the title. Calls repaint() when finished.
     * 
     * @param title
     *            The new title
     */
    public void setTitle(String title) {
        this.title = title;
        
        repaint();
    }
    
    /**
     * Updates the X-Axis title. Calls repaint() when finished.
     *
     * @param xAxisTitle
     *            The new X-Axis title
     */
    public void setXAxisTitle(String xAxisTitle) {
        this.xAxis = xAxisTitle;
        
        repaint();
    }
    
    /**
     * Updates the Y-Axis title. Calls repaint() when finished.
     *
     * @param yAxisTitle
     *            The new Y-Axis title
     */
    public void setYAxisTitle(String yAxisTitle) {
        this.yAxis = yAxisTitle;
        
        repaint();
    }
    
    /**
     * Updates the state of the legend. Calls repaint() when finished.
     *
     * @param drawLegend
     *            Specifies if the legend should be drawn
     */
//    public void setDrawLegend(boolean drawLegend) {
//        this.drawLegend = drawLegend;
//        
//        repaint();
//    }
//    
    
    /**
     * Updates the foreground color. Calls repaint() when finished.
     *
     * @param fg
     *            Specifies the desired foreground color
     */
    public void setColorForeground(Color fg) {
        this.fg = fg;
        
        repaint();
    }   
    
    /**
     * Updates the background color. Calls repaint() when finished.
     *
     * @param bg
     *            Specifies the desired background color
     */
    public void setColorBackground(Color bg) {
        this.bg = bg;
        
        repaint();
    }
    
    /**
     * This uses the current array of colors that make up the gradient, and
     * assigns a color index to each data point, stored in the dataColorIndices
     * array, which is used by the drawData() method to plot the points.
     */
    private void assignDataColors() {
        // We need to find the range of the data values,
        // in order to assign proper colors.
        double largest = Double.MIN_VALUE;
        double smallest = Double.MAX_VALUE;
        for (int x = 0; x < plottingData.length; x++) {
            for (int y = 0; y < plottingData[0].length; y++) {
                largest = Math.max(plottingData[x][y], largest);
                smallest = Math.min(plottingData[x][y], smallest);
            }
        }
        double range = largest - smallest;
        
        // dataColorIndices is the same size as the data array
        // It stores an int index into the color array
        dataColorIndices = new int[plottingData.length][plottingData[0].length];
        
        // assign a Color to each data point
        for (int x = 0; x < plottingData.length; x++) {
            for (int y = 0; y < plottingData[0].length; y++) {
                double norm = (plottingData[x][y] - smallest) / range;
                int colorIndex = (int) Math.floor(norm * (colors.length - 1));
                dataColorIndices[x][y] = colorIndex;
            }
        }
    }
    
    /**
     * Creates a BufferedImage of the actual data plot.
     *
     * This function should be called whenever the data or the gradient changes.
     */
    
    private void drawData(double[][] data) {
        plottingData = new double[data.length][data[0].length];
        for (int ix = 0; ix < data.length; ix++) {
            for (int iy = 0; iy < data[0].length; iy++) {
                plottingData[ix][iy] = data[data.length - ix - 1][iy];
            }
        }
        assignDataColors();
        bufferedImage = new BufferedImage(plottingData.length, 
                plottingData[0].length, BufferedImage.TYPE_INT_ARGB);
        bufferedGraphics = bufferedImage.createGraphics();
        
        for (int x = 0; x < plottingData.length; x++) {
            for (int y = 0; y < plottingData[0].length; y++) {
                bufferedGraphics.setColor(colors[dataColorIndices[x][y]]);
                bufferedGraphics.fillRect(y, x, 1, 1);
            }
        }
    }
    
    // logical heat map. Don't need to update plottingData matrix because we
    // assume drawData has already been called
    private void drawDataWithThreshold() {      
        bufferedImage = new BufferedImage(plottingData.length, 
                plottingData[0].length, BufferedImage.TYPE_INT_ARGB);
        bufferedGraphics = bufferedImage.createGraphics();
        
        for (int x = 0; x < this.plottingData.length; x++) {
            for (int y = 0; y < this.plottingData[0].length; y++) {
                if (this.plottingData[x][y] >= 1) {
                    bufferedGraphics.setColor(Color.WHITE);
                }
                else {
                    bufferedGraphics.setColor(Color.BLACK);
                }
                bufferedGraphics.fillRect(y, x, 1, 1);
            }
        }
    }
    
    
    public void updateLogical(boolean isLogical) {
        if (isLogical) {
            this.drawDataWithThreshold();
        }
        else {
            this.drawData(this.data);
        }
        repaint();
    }
    
    @Override
    /**
     * The overridden painting method, now optimized to simply draw the data
     * plot to the screen, letting the drawImage method do the resizing. This
     * saves an extreme amount of time.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        int width = this.getWidth();
        int height = this.getHeight();
        
        this.setOpaque(true);
        
        // clear the panel
        g2d.setColor(bg);
        g2d.fillRect(0, 0, width, height);
        
        // The data plot itself is drawn with 1 pixel per data point, and the
        // drawImage method scales that up to fit our current window size. This
        // is very fast, and is much faster than the previous version, which
        // redrew the data plot each time we had to repaint the screen.
        g2d.drawImage(bufferedImage, 31, 31, width - 30, height - 30, 0, 0,
                bufferedImage.getWidth(), bufferedImage.getHeight(), null);
        
        // border
        g2d.setColor(fg);
        g2d.drawRect(30, 30, width - 60, height - 60);
        
        // title
        g2d.drawString(title, (width / 2) - 4 * title.length(), 20);
        
        // axis ticks - ticks start even with the bottom left coner, end very
        // close to end of line (might not be right on)
        int numXTicks = (width - 60) / 50;
        int numYTicks = (height - 60) / 50;
        
        String label = "";
        DecimalFormat df = new DecimalFormat("##.###");
        
        // Y-Axis ticks
        int yDist = (int) ((height - 60) / (double) numYTicks); // distance
        // between
        // ticks
        for (int y = 0; y <= numYTicks; y++) {
            g2d.drawLine(26, height - 30 - y * yDist, 30, height - 30 - y
                    * yDist);
            label = df.format(((y / (double) numYTicks) * (yMax - yMin))
                    + yMin);
            int labelY = height - 30 - y * yDist - 4 * label.length();
            // to get the text to fit nicely, we need to rotate the graphics
            g2d.rotate(Math.PI / 2);
            g2d.drawString(label, labelY, -14);
            g2d.rotate(-Math.PI / 2);
        }
        
        // Y-Axis title
        // to get the text to fit nicely, we need to rotate the graphics
        g2d.rotate(Math.PI / 2);
        g2d.drawString(yAxis, (height / 2) - 4 * yAxis.length(), -3);
        g2d.rotate(-Math.PI / 2);
        
        // X-Axis ticks
        int xDist = (int) ((width - 60) / (double) numXTicks); // distance
        // between
        // ticks
        for (int x = 0; x <= numXTicks; x++) {
            g2d.drawLine(30 + x * xDist, height - 30, 30 + x * xDist,
                    height - 26);
            label = df.format(((x / (double) numXTicks) * (xMax - xMin))
                    + xMin);
            int labelX = (31 + x * xDist) - 4 * label.length();
            g2d.drawString(label, labelX, height - 14);
        }
        
        // X-Axis title
        g2d.drawString(xAxis, (width / 2) - 4 * xAxis.length(), height - 3);
        
        // Legend
//        if (drawLegend) {
//            g2d.drawRect(width - 20, 30, 10, height - 60);
//            for (int y = 0; y < height - 61; y++) {
//                int yStart = height
//                        - 31
//                        - (int) Math.ceil(y
//                                * ((height - 60) / (colors.length * 1.0)));
//                yStart = height - 31 - y;
//                g2d.setColor(colors[(int) ((y / (double) (height - 60)) * (colors.length * 1.0))]);
//                g2d.fillRect(width - 19, yStart, 9, 1);
//            }
//        }
    }
}
