/**
 * Figure.java
 *
 *
 * Created: Sun Sep  3 17:31:06 2000
 *
 * @author Lingchong You
 * @version 0.2
 */
package dynetica.gui.plotting;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.text.*;
import dynetica.entity.*;
import dynetica.util.DataModel;
import dynetica.util.*;

public class Figure extends JPanel {
    Color bgColor = Color.white;
    DataModel data = null;
    // the following values define the dimension of the plot.
    int xLeft;
    int xRight;
    int yTop;
    int yBottom;
    
    boolean logX = false;
    boolean logY = false;
    String [] yLabels;
    NumberFormat xFormat = NumberFormat.getNumberInstance();//new DecimalFormat("0.00");
    NumberFormat yFormat = NumberFormat.getNumberInstance(); //new DecimalFormat("0.00");


    Color [] colors = {
	Color.black, 
        Color.blue, 
        Color.green, 
        Color.red, 
        Color.cyan, 
        Color.magenta, 
        Color.orange,
	Color.pink, 
        Color.gray, 
        Color.lightGray, 
        Color.darkGray, 
        Color.yellow};

        /** Holds value of property maxDataPoints. */
        private int maxDataPoints = 200;
        
        /** Holds value of property xLabel. */
        private String xLabel = "Time";
        
    public Color[] getColors() { return colors; }
    public void setColors(Color[] colors) { this.colors = colors; }

    public Figure () {}
    
    
    /**
     * By default, the deminsion of xv should match that
     * of yv. The data are stored in the rows, with
     * x[i] corresponding y[i]. That is, y[i] will be plotted
     * against x[i].
     */
    public Figure (String xLabel,
                   String [] labels,
		   double [][] xv,
		   double [][] yv) {
	super();
	data = new DataModel(xv, yv);
        this.xLabel = xLabel;
        yLabels = labels;
    }


    public Figure ( String xLabel,
                    String [] labels,
		    double [] xv,
		    double [][] yv) {
	data = new DataModel(xv, yv);
        this.xLabel = xLabel;
        yLabels = labels;
   }
    
   public void setData(String xLabel,
                    String [] labels,
		    double [] xv,
                    double [][] yv) {
         data = new DataModel( xv, yv);
         this.xLabel = xLabel;
         yLabels = labels;
   }     
                    
    public void setLogX(boolean logX) {
	this.logX = logX;
        data.setLogX(logX);
    }

    public void setLogY(boolean logY) {
	this.logY = logY;
        data.setLogY(logY);
    }

    public void saveData(File f) {
	data.save(f);
    }

    public void saveData(String fileName) {
	data.save(fileName);
    }

    public void paint(Graphics g) {
	setBackground(bgColor);
	//
	// the following line is necessary for the 
	// figure to be properly created and properly
	// updated whenever a change is made. (Well, I didn't
	// realize this until having tolerated the poor output
	// many times).
	super.paint(g);
	
        if (data != null) {
            //
            // setup the box of the figure
            //
            int width = getSize().width;
            int height = getSize().height;
            xLeft = width/8;
            if (yLabels != null && yLabels.length > 0)
                xRight = width -  width/6;
            else 
                xRight = width - xLeft;
            yTop = height / 16;
            yBottom = height - 2 * yTop;

            //
            // draw the box
            //
            g.setColor(Color.black);
            g.drawRect(xLeft, yTop - 10, xRight-xLeft, yBottom - yTop  + 10);

            //
            // setup and draw the tics of the figure
            //
            double [] xTicValues = data.getXTicValues();
            double [] yTicValues = data.getYTicValues();
            int [] xTicPositions = data.getXTicPositions(xLeft, xRight);
            int [] yTicPositions = data.getYTicPositions(yTop, yBottom);
            //
            // draw the x tics.
            //
            for (int i = 0; i < xTicValues.length; i ++) {
                g.drawLine(xTicPositions[i], yBottom, xTicPositions[i], yBottom - 5);
                g.drawString(xFormat.format(xTicValues[i]), xTicPositions[i] - 5, yBottom + 20);
            }

            //
            // draw the y tics
            //
            for (int i = 0; i < yTicValues.length; i ++) {
                g.drawLine(xLeft, yTicPositions[i], xLeft + 5, yTicPositions[i]);
                g.drawString(yFormat.format(yTicValues[i]), xLeft - 40, yTicPositions[i]);
            }


            //
            // draw the curves
            //
            int [][] xPoints = data.getXPoints(xLeft, xRight);
            int [][] yPoints = data.getYPoints(yTop, yBottom);

    //	String [] labels = data.getLabels();
            if (yLabels == null) {
                yLabels = new String [xPoints.length];
                for (int i = 0; i < xPoints.length; i ++) 
                    yLabels[i] = ("Series[" + i + "]");
            }

            int colorIndex = 0;

            for (int i = 0; i < xPoints.length; i ++) {
                g.setColor(colors[colorIndex++]);
                if (colorIndex > colors.length) colorIndex = 0;
                for (int j = 0; j < xPoints[0].length -1; j++) {
                    if (xPoints[i][j] != Integer.MIN_VALUE &&
                        xPoints[i][j+1] != Integer.MIN_VALUE &&
                        yPoints[i][j] != Integer.MIN_VALUE &&
                        yPoints[i][j+1] != Integer.MIN_VALUE) 
                        g.drawLine (xPoints[i][j], yPoints[i][j],
                                    xPoints[i][j+1], yPoints[i][j+1]);
                }
                g.drawString (yLabels[i], xRight + 5, yPoints[i][yPoints[0].length -1] );
            }
            g.setColor(Color.black);
            g.drawString (xLabel, (xLeft + xRight) / 2, yBottom + 30);
        }
    }

    /** Getter for property xmax.
     * @return Value of property xmax.
 */
    public double getXmax() {
        return data.getXmax();
}
    
/** Getter for property xmin.
 * @return Value of property xmin.
 */
public double getXmin() {
    return data.getXmin();
}

/** Getter for property ymax.
 * @return Value of property ymax.
 */
public double getYmax() {
    return data.getYmax();
}

/** Getter for property ymin.
 * @return Value of property ymin.
 */
public double getYmin() {
    return data.getYmin();
}

/** Setter for property xmax.
 * @param xmax New value of property xmax.
 */
public void setXmax(double xmax) {
    data.setXmax(xmax);
    repaint();
}

/** Setter for property xmin.
 * @param xmin New value of property xmin.
 */
public void setXmin(double xmin) {
    data.setXmin(xmin);
    repaint();
}

/** Setter for property ymax.
 * @param ymax New value of property ymax.
 */
public void setYmax(double ymax) {
    data.setYmax(ymax);
    repaint();
}

/** Setter for property ymin.
 * @param ymin New value of property ymin.
 */
public void setYmin(double ymin) {
    data.setYmin(ymin);
    repaint();
}

/** Getter for property maxDataPoints.
 * @return Value of property maxDataPoints.
 */
public int getMaxDataPoints() {
    return maxDataPoints;
}

/** Setter for property maxDataPoints.
 * @param maxDataPoints New value of property maxDataPoints.
 */
public void setMaxDataPoints(int maxDataPoints) {
    this.maxDataPoints = maxDataPoints;
}

/** Getter for property xLabel.
 * @return Value of property xLabel.
 */
public String getXLabel() {
    return xLabel;
}

/** Setter for property xLabel.
 * @param xLabel New value of property xLabel.
 */
public void setXLabel(String xLabel) {
    this.xLabel = xLabel;
}
	  
} // Figure
