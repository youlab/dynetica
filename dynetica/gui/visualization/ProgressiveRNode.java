/*
 * ProgressiveRNode.java
 *
 * Created on September 18, 2001, 1:11 AM
 */

package dynetica.gui.visualization;

import dynetica.gui.visualization.ArrowedLine;
import java.awt.*;

/**
 * 
 * @author You
 * @version 0.1
 */
public class ProgressiveRNode extends RNode {

    /** Creates new ProgressiveRNode */
    public ProgressiveRNode(dynetica.reaction.ProgressiveReaction r) {
        super(r);
    }

    @Override
    public void draw(java.awt.Graphics2D g) {
        java.awt.Color c = g.getColor();
        g.setColor(g.getBackground());
        g.fill(getShape());

        // Added by Kanishk Asthana 23 July 2013 to include color change on
        // selection
        if (selected)
            g.setColor(new Color(0.21f, 0.15f, 0.60f));
        else
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
        new ArrowedLine(getX() + getWidth() * 0.15, getCenterY(), getX()
                + getWidth() * 0.85, getCenterY(), height / 2, 0.4, 1.0, true)
                .draw(g);
        g.setFont(f);
        // Added by Kanishk Asthana 23 July 2013 to reset the color
        g.setColor(c);
    }
}
