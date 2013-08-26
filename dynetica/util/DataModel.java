/**
 * DataModel.java
 *
 *
 * Created: Mon Sep 11 09:39:52 2000
 *
 * @author Lingchong You
 * @version 0.01
 */

package dynetica.util;
import dynetica.util.*;
import java.io.*;

public class DataModel  {
    double [][] xValues; // the x-axis data
    double [][] yValues; // the y-axis data

    double xmin;
    double xmax;
    double ymin;
    double ymax;
    
    boolean logY = false;
    boolean logX = false;
    
    int numberOfXtics = 6;
    int numberOfYtics = 6;

    public DataModel() {
    }
      /**
     * By default, the deminsion of xv should match that
     * of yv. The data are stored in the rows, with
     * x[i] corresponding y[i]. That is, y[i] will be plotted
     * against x[i].
     */


    public DataModel (double [][] xv,
		      double [][] yv) {
	setXValues(xv);
	setYValues(yv);	
    }

    public DataModel ( double [] xv,
		       double [][] yv) {
	setXValues(xv, yv.length);
	setYValues(yv);

    }
    
    public int getNumberOfSeries() { return xValues.length; }
    public int getNumberOfPoints() { return xValues[0].length;}
    public double[][] getXValues() {return xValues;}
    public void setXValues(double [][] xv) { 
	xValues = xv;
        xmin = Numerics.min(xv);
        xmax = Numerics.max(xv);
    }
    public void setXValues(double [] xv, int ns) {
	xValues = new double [ns][xv.length];
	for (int i= 0; i < ns; i++) xValues[i] = xv;
        xmin = Numerics.min(xv);
        xmax = Numerics.max(xv);
    }

    public double[][] getYValues() { return yValues;}
    public void setYValues(double [][] yv) { 
        yValues = yv;
        ymin = Numerics.min(yv);
        ymax = Numerics.max(yv);
    }
    public void setYValues(double [] yv, int ns) {
	yValues = new double [ns][yv.length];
	for (int i= 0; i < ns; i++) yValues[i] = yv;
        ymin = Numerics.min(yv);
        ymax = Numerics.max(yv);
    }

    public int [][] getXPoints(int xLeft, int xRight) {
	int numberOfSeries = xValues.length;
	int numberOfPoints = xValues[0].length;
 	int [][] xPoints = new int[numberOfSeries][numberOfPoints];
	for (int i = 0; i < numberOfSeries; i ++) {
	    for (int j = 0; j < numberOfPoints; j ++) {
                if (getXmax() > getXmin()) {
                    if (xValues[i][j] >= getXmin() && xValues[i][j] <= getXmax()) {
                        if (logX)
                            xPoints[i][j] = (int) 
                                ((Math.log(xValues[i][j]) - Math.log(getXmin())) /
                                (Math.log(getXmax()) - Math.log(getXmin())) * (xRight - xLeft)) + xLeft;
                          else
                            xPoints[i][j] = (int) 
                                ( (xValues[i][j] - getXmin()) / (getXmax() - getXmin()) * (xRight - xLeft) ) + xLeft;
                    }
                    //
                    // if the data is out of the defined range, don't plot it.
                    //
                    
                    else
                        xPoints[i][j] = Integer.MIN_VALUE;
                }
                //
                // if all the x values are the same (xmax == xmin), no need to do 
                // any calculation
                //
                else {
                    xPoints[i][j] = (xRight + xLeft)/2;
                }
	    }
        }
        return xPoints;
    }

    public int [][] getYPoints (int yTop, int yBottom) {
	int numberOfSeries = xValues.length;
	int numberOfPoints = xValues[0].length;
       	
 	int [][] yPoints = new int[numberOfSeries][numberOfPoints];
	
	for (int i = 0; i < numberOfSeries; i ++) {
	    for (int j = 0; j < numberOfPoints; j ++) {
                if (getYmax() > getYmin()) {
                    if (yValues[i][j] >= getYmin() && yValues[i][j] <= getYmax()) {
                        if (logY) 
                           yPoints[i][j] = yBottom - (int)
                               ((Math.log(yValues[i][j]) - Math.log(getYmin())) /
                                 (Math.log(getYmax()) - Math.log(getYmin())) * (yBottom - yTop));
                        else
                        yPoints[i][j] = yBottom - (int)
                                (( yValues[i][j] - getYmin()) / (getYmax() - getYmin()) * ( yBottom - yTop));
	           }
                    //
                    // mark a negative value (don't draw it!)
                    // 
                   else yPoints[i][j] = Integer.MIN_VALUE;
                }
                    
                else {
                    yPoints[i][j] = (yBottom + yTop)/2;
                }
            }
	}
	return yPoints;
    }

    
    public double[] getXTicValues() {
        if (logX)
	    return getLogTics(getXmin(), getXmax(), numberOfXtics);
	else 
	    return getTics(getXmin(), getXmax(), numberOfXtics);
    }

    public int[] getXTicPositions(int xLeft, int xRight) {
	double [] xTicValues = getXTicValues();
	int [] xTicPositions = new int [xTicValues.length];
        if (xmax > getXmin()) {
            for (int i = 0; i < xTicValues.length; i ++) {
	     if (logX) {
		    xTicPositions[i] = (int) 
		        (xLeft + (((Math.log(xTicValues[i]) - Math.log(getXmin())) / 
			           (Math.log(xmax) - Math.log(getXmin()))) * 
			          (xRight - xLeft)));
	     }
	     else {
		    xTicPositions[i] = (int) 
		     (xLeft + (((xTicValues[i] - getXmin()) / (xmax - getXmin())) * 
			          (xRight - xLeft)));
	     }
	    }
        }
        //
        // Added by Lingchong You. 2/14/2001
        // if all the xvalues are the same, the calculation above is invalid.
        //
        else {
            for (int i = 0; i < xTicValues.length; i ++) 
                xTicPositions[i] = (xLeft + xRight)/2;
        }
	return xTicPositions;
    }

    public int[] getYTicPositions(int yTop, int yBottom) {
	double [] yTicValues = getYTicValues();
	int [] yTicPositions = new int [yTicValues.length];
        if (getYmax() > getYmin()) {
	    for (int i = 0; i < yTicValues.length; i ++) {
	     if (logY) {
	    	yTicPositions[i] = (int) 
		        (yBottom - (((Math.log(yTicValues[i]) - Math.log(getYmin())) / 
				 (Math.log(getYmax()) - Math.log(getYmin()))) * 
				(yBottom - yTop)));
	     }
	     else {
		    yTicPositions[i] = (int) 
		       (yBottom - (((yTicValues[i] - getYmin()) / (getYmax() - getYmin())) * 
			    	(yBottom - yTop)));
	        }
	     }
           }
        //
        // Added by Lingchong You. 2/14/2001
        // if all the yvalues are the same, the calculation above is invalid.
        //
        else {
            for (int i = 0; i < yTicValues.length; i ++ ) 
                yTicPositions[i] = (yBottom + yTop)/2;
        }
	return yTicPositions;
    }

    public double[] getYTicValues() {
	if (logY) {
	    return getLogTics(getYmin(), getYmax(), numberOfYtics);
	}
	else {
	    return getTics(getYmin(), getYmax(), numberOfYtics);
	}	
    }
    
    public static double[] getLogTics (double min, double max, int numberOfTics) {
        if (min == max) {
            double[] bad = new double [1];
            bad[0] = min;
            return bad;
        }
        
	double [] tmp = getTics(Numerics.log10(min), Numerics.log10(max), numberOfTics); 
	for (int i = 0; i < numberOfTics; i ++)
	tmp[i] = Math.pow(10.0, tmp[i]);
	return tmp;
    }

    public static double [] getTics(double min,
				    double max,
				    int numberOfTics) {
        // if min == max, ignore the number of Tics -- doesn't make sense anymore
        // and return only a single number!
        if (min == max) {
            double[] bad = new double [1];
            bad[0] = min;
            return bad;
        }
        
      	double [] ticValues = new double[numberOfTics];
	double diff = max - min;
	int i = 0;
	for (; diff < 10; i ++) diff *= 10;
	double tic = (int) diff / (numberOfTics - 1);
	for (; i > 0; i --) tic /= 10;
	    
	double point;
	if (min < 0) {
	    point = min - (min % tic);
	}

	else {
	    point = min - (min % tic) + tic;
	}
	
	for (int j = 0; j < numberOfTics && point < max; j ++) {
	    ticValues[j] = point;
	    point += tic;
	}
	return ticValues;
    }

    public void save(String fileName) {
	File file = new File(fileName);
	save(file);
    }

    public void save(File file) {
	try {
	    PrintWriter out = new PrintWriter ( new 
		FileOutputStream(file) );
            // Bug fix (March 12, 2003)
            // Lingchong You. 
            //
            // Need to check the size of xValues first to avoid 
            // OutOfBoundary exception when only one line of xValues are present.
            //
	    if (xValues.length == 1 || xValues[0] == xValues[1]) {
		for (int i = 0; i < xValues[0].length; i ++ ) {
		    out.print(xValues[0][i]);
		    out.print("\t");
		    for (int j = 0; j < xValues.length; j ++ ) {
			out.print(yValues[j][i]);
			out.print("\t");
		    }
		    out.print("\n");
		}
	    }
	    
	    else {
		for (int i = 0; i < xValues[0].length; i ++ ) {		
		    for (int j = 0; j < xValues.length; j ++ ) {
			out.print(xValues[0][j]);
			out.print("\t");
			out.print(yValues[i][j]);
			out.print("\t");
		    }
		    out.print("\n");
		}
	    }
	    out.close();
	}
	catch (IOException ioe) {
           ioe.printStackTrace();
        }
    }
    
    /** Getter for property xmin.
     * @return Value of property xmin.
 */
    public double getXmin() {
        return xmin;
    }
    
    /** Setter for property xmin.
     * @param xmin New value of property xmin.
 */
    public void setXmin(double xmin) {
        if (logX) 
            this.xmin = Math.max(Numerics.minPositive(xValues), xmin);
        else
            this.xmin = xmin;
    }
    
    /** Getter for property xmax.
     * @return Value of property xmax.
 */
    public double getXmax() {
        return xmax;
    }
    
    /** Setter for property xmax.
     * @param xmax New value of property xmax.
 */
    public void setXmax(double xmax) {
        if (logX) this.xmax = Math.max(Numerics.minPositive(xValues), xmax);
        else
            this.xmax = xmax;
    }
    
    /** Getter for property ymin.
     * @return Value of property ymin.
 */
    public double getYmin() {
        return ymin;
    }
    
    /** Setter for property ymin.
     * @param ymin New value of property ymin.
 */
    public void setYmin(double ymin) {
        if (logY)
            this.ymin = Math.max(Numerics.minPositive(yValues), ymin);
        else
            this.ymin = ymin;
    }
    
    /** Getter for property ymax.
     * @return Value of property ymax.
 */
    public double getYmax() {
        return ymax;
    }
    
    /** Setter for property ymax.
     * @param ymax New value of property ymax.
 */
    public void setYmax(double ymax) {
        if (logY)
            this.ymax = Math.max(Numerics.minPositive(yValues), ymax);
        else
            this.ymax = ymax;
    }
    
    public void setLogY(boolean logY) {
        this.logY = logY;
        setYmin(ymin);
        setYmax(ymax);
    }
    
    public void setLogX(boolean logX) {
        this.logX = logX;
        setXmin(xmin);
        setXmax(xmax);
    }
    
} // DataModel
