/*
 * EquilibratedRNode.java
 *
 * Created on September 18, 2001, 1:40 AM
 */

package dynetica.gui.visualization;

import java.awt.*;

/**
 * 
 * @author You
 * @version
 */
public class EquilibratedRNode extends RNode {

    /** Creates new EquilibratedRNode */
    public EquilibratedRNode(dynetica.reaction.EquilibratedReaction r) {
        super(r);
    }

    public void draw(java.awt.Graphics2D g) {
        java.awt.Color c = g.getColor();
        g.setColor(g.getBackground());
        g.fill(getShape());
        g.setColor(c);
        g.setStroke(new java.awt.BasicStroke(outlineWidth));
        g.draw(getShape());
        g.setStroke(new java.awt.BasicStroke(interiorWidth));
        Font f = g.getFont();
        g.setFont(new Font(f.getFontName(), f.getStyle(),
                (int) (f.getSize() * relativeFontSize)));
        if (textVisible)
            g.drawString(getNodeName(), (float) (getX() + getWidth()),
                    (float) getY());
        if (drawInformationBox == true) {
            dynetica.reaction.EquilibratedReaction rx = (dynetica.reaction.EquilibratedReaction) getEntity();
            g.setFont(new Font(f.getFontName(), f.getStyle(), (int) (f
                    .getSize() * 1.25 * relativeFontSize)));
            g.setColor(Color.BLACK);
            g.drawString("Stoichiometry: " + rx.getStoichiometry(),
                    (float) (getX() + getWidth()),
                    (float) (getY() - getHeight()));
        }
        g.draw(new java.awt.geom.Line2D.Double(getX() + getWidth() * 0.2,
                getY() + 0.35 * getHeight(), getX() + getWidth() * 0.8, getY()
                        + 0.35 * getHeight()));
        g.draw(new java.awt.geom.Line2D.Double(getX() + getWidth() * 0.2,
                getY() + 0.65 * getHeight(), getX() + getWidth() * 0.8, getY()
                        + 0.65 * getHeight()));
        g.setFont(f);
    }
}
