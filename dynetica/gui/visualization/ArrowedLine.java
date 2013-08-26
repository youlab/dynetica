/*
 * ArrowedLine.java
 *
 * Created on September 3, 2001, 5:24 PM
 */

package dynetica.gui.visualization;

import dynetica.gui.DrawingConstants;
import java.awt.geom.*;
import java.awt.*;
/**
 *
 * @author  Lingchong You
 * @version 0.1
 */
public class ArrowedLine extends Line2D.Double{
    /**
     * the length of each tail of the arrow
     */
    public double arrowSize = 14.0;
    final static double pi = 4.0 * Math.atan(1.0);
    /**
     *the angle of the tail of the arrow w.r.t to the line
     */
    public double arrowAngle = Math.atan(1.0) * 2.0 / 5.0; // pi/4
    double arrowPosition = 0.5; // 0 = tail; 1 = head
    public boolean arrowVisible = true;
    boolean dashed = false;
    BasicStroke dashedStroke = DrawingConstants.dashed;
    
    /** Creates new ArrowedLine */
    public ArrowedLine(double x1, double y1, double x2, double y2) {
       super(x1, y1, x2, y2);
    }

    public ArrowedLine(double x1, double y1, double x2, double y2, 
                       double size, double angle, boolean arrowVisible,
                       boolean dashed) {
        super(x1, y1, x2, y2);
        this.arrowSize = size;
        this.arrowAngle = angle;
        this.arrowVisible = arrowVisible;
        this.dashed = dashed;
    }
    
    public ArrowedLine(double x1, double y1, double x2, double y2, double size, double angle, double arrowPosition, boolean arrowVisible) {
        super(x1, y1, x2, y2);
        this.arrowSize = size;
        this.arrowAngle = angle;
        this.arrowPosition = arrowPosition;
        this.arrowVisible = arrowVisible;
    }
    
    private void drawArrow(Graphics2D g) {
        if (arrowVisible) {
            double arrowHeadX = x1 + (x2 - x1) * arrowPosition;
            double arrowHeadY = y1 + (y2 - y1) * arrowPosition;
            double alpha = Math.asin((y2 - y1)/ Math.sqrt((x2 - x1)* (x2-x1) + (y2 - y1) * (y2 - y1)));
            double absoluteAngle1; 

            if (x2 > x1) absoluteAngle1 = pi + (alpha - arrowAngle);
            else   absoluteAngle1 = arrowAngle - alpha;

            double firstArrowTailX = arrowHeadX + arrowSize * Math.cos(absoluteAngle1);
            double firstArrowTailY = arrowHeadY + arrowSize * Math.sin(absoluteAngle1);
            double absoluteAngle2;
            if (x2 > x1) absoluteAngle2 = pi + (alpha + arrowAngle);
            else absoluteAngle2 = -arrowAngle - alpha;

            double secondArrowTailX = arrowHeadX + arrowSize * Math.cos(absoluteAngle2);
            double secondArrowTailY = arrowHeadY + arrowSize * Math.sin(absoluteAngle2);

            g.draw(new Line2D.Double(firstArrowTailX, firstArrowTailY, arrowHeadX, arrowHeadY));
            g.draw(new Line2D.Double(secondArrowTailX, secondArrowTailY, arrowHeadX, arrowHeadY));
        }
    }
    
    public void draw(Graphics2D g) {
        if (dashed) {
          BasicStroke oldStroke = (BasicStroke) (g.getStroke());
          BasicStroke stroke = new BasicStroke (  oldStroke.getLineWidth(),
                                      dashedStroke.getEndCap(), 
                                      dashedStroke.getLineJoin(),
                                      dashedStroke.getMiterLimit(),
                                      dashedStroke.getDashArray(),
                                      dashedStroke.getDashPhase());
          g.setStroke(stroke);
          g.draw(this);
          g.setStroke(oldStroke);
        }
        else
         g.draw(this);
        drawArrow(g);
    }
}
