package dynetica.gui.visualization;

import dynetica.gui.visualization.EntityNode;
import dynetica.system.*;
import dynetica.entity.*;
import java.awt.*;
import java.awt.geom.*;

/**
 * 
 * @author Kanishk Asthana created June 2013
 */
public class MNode extends EntityNode {

    double relativeNodeSize = 1.0;

    public MNode(AbstractModule m) {
        super(m);
        width = 45;
        height = 45;
        shape = new Ellipse2D.Double(0, 0, width, height);
        setLocation(m.getX(), m.getY());

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

    public void draw(Graphics2D g) {

        Color c = g.getColor();

        java.awt.BasicStroke stroke = (BasicStroke) g.getStroke();

        g.setStroke(new java.awt.BasicStroke((float) relativeNodeSize * 3.0f));

        g.setColor(g.getBackground());

        g.fill(getShape());

        if (selected)
            g.setColor(new Color(0.1f, 0.8f, 0.6f));
        else
            g.setColor(c);

        g.draw(getShape());

        Font f = g.getFont();

        g.setFont(new Font(f.getFontName(), f.getStyle(),
                (int) (f.getSize() * relativeFontSize)));

        if (textVisible)
            g.drawString(getNodeName(), (float) (getX() + getWidth()),
                    (float) getY());// this is pretty cool dude,
        // a very large proportion of the code can be reused! Awesome, this is
        // very interesting!

        // Coordinates for Internal Circles
        double len = 15.0 * relativeNodeSize;

        double x1 = getCenterX();
        double y1 = getCenterY() - len;
        double rootThreeByTwo = Math.sqrt(3.0) / 2;

        double x2 = getCenterX() + len * rootThreeByTwo;
        double y2 = getCenterY() + len / 2;

        double x3 = getCenterX() - len * rootThreeByTwo;
        double y3 = getCenterY() + len / 2;

        g.setStroke(new java.awt.BasicStroke((float) relativeNodeSize * 1.5f));
        g.setColor(new Color(0.12f, 0.68f, 0.26f));
        g.draw(new Line2D.Double(x1, y1, x2, y2));
        g.draw(new Line2D.Double(x2, y2, x3, y3));
        g.draw(new Line2D.Double(x3, y3, x1, y1));

        if (selected)
            g.setColor(Color.RED);
        else
            g.setColor(new Color(0.26f, 0.10f, 0.92f));

        g.fill(new Ellipse2D.Double(x1 - 5, y1 - 5, 10 * relativeNodeSize,
                10 * relativeNodeSize));
        g.fill(new Ellipse2D.Double(x2 - 5, y2 - 5, 10 * relativeNodeSize,
                10 * relativeNodeSize));
        g.fill(new Ellipse2D.Double(x3 - 5, y3 - 5, 10 * relativeNodeSize,
                10 * relativeNodeSize));

        g.setFont(f);
        g.setColor(c); // reset the color
        g.setStroke(stroke);
    }

    public double getX() {
        return getShape().getX();
    }

    public double getY() {
        return getShape().getY();
    }

    public void setChangeRatio(double r) {
        width *= r;
        height *= r;
        outlineWidth *= r;
        interiorWidth *= r;
        relativeFontSize *= r;
        relativeNodeSize *= r;
        getShape().setFrame(getX(), getY(), width, height);
    }

    public double getCenterX() {
        return getShape().getCenterX();
    }

    public double getCenterY() {
        return getShape().getCenterY();
    }

}
