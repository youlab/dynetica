/*
 * DrawingConstants.java
 *
 * Created on September 18, 2001, 2:10 AM
 */

package dynetica.gui;
import java.awt.*;
import java.awt.geom.*;
/**
 *
 * @author  You
 * @version 
 */
public interface DrawingConstants {
    final static BasicStroke defaultStroke = new BasicStroke(1.0f);
    final static BasicStroke thickStroke = new BasicStroke(2.0f);
    final static BasicStroke thinStroke = new BasicStroke(0.5f);
    final static float dash1[] = {5.0f};
    final static BasicStroke dashed = new BasicStroke(0.5f, 
                                                      BasicStroke.CAP_SQUARE, 
                                                      BasicStroke.JOIN_MITER, 
                                                      5.0f, dash1, 0.0f);

}

