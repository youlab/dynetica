/*
 * EntityNode.java
 *
 * Created on September 7, 2001, 4:36 PM
 */

package dynetica.gui.visualization;
import dynetica.gui.visualization.AbstractNode;
import java.awt.*;
import java.awt.geom.*;
/**
 *
 * @author  Lingchong You
 * @version  1.2
 * updated 4/20/2005
 */

 abstract public class EntityNode implements AbstractNode{
    protected double width;
    protected double height;
    protected float outlineWidth = 1.0f;
    protected float interiorWidth = 0.5f;
    protected float relativeFontSize = 1.0f;
    protected java.awt.Shape shape;
    protected dynetica.entity.Entity entity;
    protected boolean textVisible = true;
    protected boolean selected = false;
    
    /** Creates new EntityNode */
    public EntityNode(dynetica.entity.Entity entity) {
        this.entity = entity;
    }

    public String getNodeName() {
        if (entity != null) 
            return entity.getName();
        else 
            return null;
    }
    
    public void setTextVisible(boolean visible) {
        textVisible = visible;
    }

    public void erase(Graphics2D g) {
        Color c = g.getColor();
        g.setColor(g.getBackground());
        draw(g);
        g.setColor(c);
    }

    public void setChangeRatio(double r) {
        width *= r;
        height *= r;
        outlineWidth *= r;
        interiorWidth *= r;
        relativeFontSize *= r;
    }
    
    public boolean contains( double x, double y) {
        return shape.contains(x, y);
    }
    
    public javax.swing.JPanel editor() {
        return entity.editor();
    }
    
    public dynetica.entity.Entity getEntity() {
        return entity;
    }
    
    public void setSelected(boolean selected){
        this.selected = selected;
    }
    public boolean isSelected() {
        return selected;
    }
}
