/*
 * SNode.java
 *
 * Created on September 17, 2001, 9:24 PM
 */

package dynetica.gui.visualization;
import dynetica.gui.visualization.EntityNode;
import dynetica.entity.*;
import java.awt.*;
import java.awt.geom.*;

/**
 *
 * @author  Lingchong You
 * @version 1.2
 */
public class SNode extends EntityNode {
    
    /** Creates new SNode */
    public SNode(Substance s) {
        super(s);
        width = 15;
        height = 15;
        shape = new Ellipse2D.Double(0, 0, width, height);
        setLocation(s.getX(),s.getY());
    }

    protected RectangularShape getShape() {
        return (Ellipse2D.Double) shape;
    }
       
    public void setLocation(double x, double y) {
        ((Ellipse2D.Double) getShape()).x = x;
        ((Ellipse2D.Double) getShape()).y = y;
        getEntity().setX(x);
        getEntity().setY(y);
   }
    
    public void draw(Graphics2D g) {
         //draw with a different color if selected. Added 5/7/2005 L. You
        Color c = g.getColor();
        if (selected) {
            g.setColor(Color.green);
        }
        g.fill(getShape());
        g.draw(getShape());
                
        Font f = g.getFont();
        g.setFont(new Font(f.getFontName(), f.getStyle(), (int) (f.getSize() * relativeFontSize)));
        if (textVisible) 
            g.drawString(getNodeName(), (float) (getX() + getWidth()), (float) getY());
        g.setFont(f);
        g.setColor(c); // reset the color

    }
    
    public double getWidth() {
        return width;
    }
    
    public double getHeight() {
        return height;
    }
    
    public void setX(double x) {
        ((Ellipse2D.Double) getShape()).x = x;
        getEntity().setX(x);
    }
    
    public void setY(double y) {
        ((Ellipse2D.Double) getShape()).y = y;
        getEntity().setY(y);
    }
    
    public double getX() {
        return getShape().getX();
    }
    
    public double getY() {
        return getShape().getY();
    }
    
    
    public void setChangeRatio(double r) {
        super.setChangeRatio(r);
        getShape().setFrame(getX(), getY(), width, height);
    }
    
    public double getCenterX() {
        return getShape().getCenterX();
    }
    
    public double getCenterY() {
        return getShape().getCenterY();
    }    
}
