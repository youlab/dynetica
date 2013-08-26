/*
 * GeneticElementNode.java
 *
 * Created on September 22, 2001, 11:43 PM
 */

package dynetica.gui.genetics;
import dynetica.gui.visualization.SNode;
import java.awt.geom.*;
import java.awt.*;

/**
 *
 * @author  You
 * @version 
 */
public class GeneticElementNode extends SNode {

    public static double unitWidth = 0.01;
    
    /** Creates new GeneticElementNode */
    public GeneticElementNode(dynetica.entity.GeneticElement ge) {
        super(ge);
        width = unitWidth * ge.getLength();
        height = 10.0;
        shape = new Rectangle2D.Double(0, 0, width, height);        
    }
    
    
    protected RectangularShape getShape() {
        return (Rectangle2D.Double) shape;
    }
    
    public void setX(double x) {
        ((Rectangle2D.Double) getShape()).x = x;
    }
    
    public void setY(double y) {
        ((Rectangle2D.Double) getShape()).y = y;
    }
    
    public void setLocation(double x, double y) {
        ((Rectangle2D.Double) shape).x = x;
        ((Rectangle2D.Double) shape).y = y;
    }
    
    public double getX() {
        return getShape().getX();
    }
    
    public double getY() {
        return getShape().getY();
    }
    
    public void draw(Graphics2D g) {
        Color c = g.getColor();
        g.setColor(g.getBackground());
        g.fill(getShape ());
        g.setColor(c);
        g.draw(getShape());
        Font f = g.getFont();
        g.setFont(new Font(f.getFontName(), f.getStyle(), (int) (f.getSize() * relativeFontSize)));
        if (textVisible) 
            g.drawString(getNodeName(), (float) (getX()), (float) getY() - 5);
        g.setFont(f);
    }
    
    public double getUnitWidth() {
        return unitWidth;
    }
}
