/*
 * RNode.java
 *
 * Created on September 17, 2001, 9:59 PM
 */

package dynetica.gui.visualization;
import dynetica.gui.visualization.EntityNode;
import dynetica.reaction.*;
import java.awt.*;
import java.awt.geom.*;

/**
 *
 * @author  You
 * @version 
 */
public class RNode extends EntityNode {

    /** Creates new RNode */
    public RNode(Reaction r) {
        super(r);
        width = 18.0;
        height = 12.0;
        this.shape = new Rectangle2D.Double(0, 0, width, height);
        setLocation(r.getX(),r.getY());
    }
    
    protected Rectangle2D.Double getShape() {
        return (Rectangle2D.Double) shape;
    }
    
    public void setLocation(double x, double y) {
        getShape().x = x;
        getShape().y = y;
        getEntity().setX(x);
        getEntity().setY(y);
   }
   
    public dynetica.reaction.Reaction getReaction() {
        return (dynetica.reaction.Reaction) getEntity();
    }
    
    public void draw(Graphics2D g) {
        Color c = g.getColor();
        //draw an encloure if selected. Added 5/7/2005 L. You
        if (selected) {
            g.setColor(Color.orange);
        }
        g.fill(getShape());
        g.draw(getShape());
        if (textVisible) 
            g.drawString(getNodeName(), (float) (getX() + getWidth()), (float) getY());
        g.setColor(c);
    }
    
    public void setX(double x) {
        getShape().x = x;
    }
    
    public void setY(double y) {
        getShape().y = y;
    }
    
    public double getWidth() {
        return getShape().getWidth();
    }
    
    public double getHeight() {
        return getShape().getHeight();
    }
    
    
    public double getX() {
        return getShape().x;
    }
    
    public double getY() {
        return getShape().y;
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
