/*
 * ModularSystemGraph.java
 *
 * Cloned from SystemGraph.java on 17 June 2013 11:28PM
 */

package dynetica.gui.systems;

import java.util.*;
import java.awt.geom.*;
import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.beans.*;
import dynetica.entity.*;
import dynetica.reaction.Reaction;
import dynetica.system.*;//added by Kanishk Asthana 25 April 2013
import dynetica.event.*;
import dynetica.gui.visualization.AbstractNode;
import dynetica.gui.visualization.ArrowedLine;
import dynetica.gui.DrawingConstants;
import dynetica.gui.visualization.ENode;
import dynetica.gui.entities.EntityEditorFrame;
import dynetica.gui.visualization.MNode;
import dynetica.gui.visualization.NetworkLayout;
import dynetica.gui.visualization.RNode;
import dynetica.gui.visualization.SNode;
import java.io.*;
import java.awt.print.*;

public class ModularSystemGraph extends javax.swing.JPanel implements
        DrawingConstants, Printable {
    dynetica.system.ReactiveSystem system;
    static double pi = 4 * Math.atan(1.0);

    Map substanceNodeMap = new HashMap();
    Map reactionNodeMap = new HashMap();

    // added 4/19/2005

    Map expressionNodeMap = new HashMap();

    // added 25 April 2013 by Kanishk Asthana
    Map reactionNodeMapForModules = new HashMap();
    Map expressionNodeMapForModules = new HashMap();
    Map moduleNodeMap = new HashMap();

    Map coordinateMap = new HashMap();

    Object[] substanceNodes;
    Object[] reactionNodes;
    Object[] expressionNodes;

    // added 25 April 2013 by Kanishk Asthana

    Object[] moduleNodes;
    Object[] reactionNodesForModules;
    Object[] expressionNodesForModules;

    // this field is to track the nodes selected by the user. 5/15/2005

    java.util.List selectedNodes = new ArrayList();

    Color defaultColor = Color.black;
    Color background = Color.white;
    Color substanceNodeColor = Color.blue;
    Color reactionNodeColor = Color.black;
    Color reactantLineColor = Color.red;
    Color productLineColor = Color.green;
    Color catalystLineColor = Color.LIGHT_GRAY;

    // added 25 April 2013 by Kanishk Asthana
    Color moduleNodeColor = Color.BLACK;

    // added 4/19/2005
    Color expressionNodeColor = Color.magenta;

    Color expressionLineColor = Color.ORANGE;

    protected boolean firstTime;
    AbstractNode currentNode = null;
    // Added by Kanishk Asthana 28 August 2013 10:06pm
    AbstractNode currentHoverNode = null;
    Graphics2D graph;

    //
    // graph layout information
    //
    NetworkLayout layout;
    float relativeLineWidth = 1.0f;// f means float
    double relativeArrowSize = 1.0;
    double relativeNodeSize = 1.0;

    BasicStroke stroke = new BasicStroke(0.5f);
    boolean drawNodeName = true;
    boolean drawArrows = true;

    // Holds the coordinates of the user's last mousePressed event.
    double lastX = 0.0, lastY = 0.0;

    javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();// empty
                                                                                                 // list
    dynetica.event.SizeChangeEvent sizeChangeEvent;

    public void addSizeChangeListener(SizeChangeListener l) {
        listenerList.add(SizeChangeListener.class, l);
    }

    public void removeSizeChangeListener(SizeChangeListener l) {
        listenerList.remove(SizeChangeListener.class, l);
    }

    public void fireSizeChange() {
        EventListener[] listeners = listenerList
                .getListeners(SizeChangeListener.class);
        for (int i = 0; i < listeners.length; i++) {
            if (sizeChangeEvent == null)
                sizeChangeEvent = new SizeChangeEvent(this);
            ((SizeChangeListener) listeners[i]).sizeChanged(sizeChangeEvent);
        }
        system.fireSystemStateChange();
    }

    public ModularSystemGraph(dynetica.system.ModularSystem sys) {
        system = sys;
        layout = sys.networkLayout;
        setPreferredSize(new Dimension(layout.width, layout.height));
        initComponents();
        setBackground(background);
        reset();
        repaint();
        popOutItem.setVisible(false);
        this.addMouseListener(new GraphMouseListener());
        this.addMouseMotionListener(new GraphMouseMotionListener());
        this.addSystemStructureChangeListener(new SystemStructureChangeListener() {
            public void systemStructureChanged(SystemStructureChangeEvent e) {
                reset();
                repaint();
            }
        });
    }

    protected void addSystemStructureChangeListener(
            SystemStructureChangeListener l) {
        system.addSystemStructureChangeListener(l);
    }

    /**
     * Reads coordinates from File in. the name of in should be the name of the
     * input file for the system plus ".graph" This method is called when a
     * system is opened.
     * 
     * This method is discontinued from version 1.2 and up.
     */
    public void readGraph(File in) {

    }

    /**
     * Saves the graph into the output file. The name of out should be the
     * output file for the system plus ".graph". This method is called everytime
     * the system is saved.
     */
    public void save(File out) {
    }

    //
    // re-added 5/14/2005 by LY to fix a bug in print
    private void storeCurrentCoordinates() {
        coordinateMap.clear();
        AbstractNode node;
        for (int i = 0; i < substanceNodes.length; i++) {
            node = (AbstractNode) substanceNodes[i];
            coordinateMap.put(node,
                    new Point((int) node.getX(), (int) node.getY()));
        }

        for (int i = 0; i < reactionNodes.length; i++) {
            node = (AbstractNode) reactionNodes[i];
            coordinateMap.put(node,
                    new Point((int) node.getX(), (int) node.getY()));
        }
        for (int i = 0; i < expressionNodes.length; i++) {
            node = (AbstractNode) expressionNodes[i];
            coordinateMap.put(node,
                    new Point((int) node.getX(), (int) node.getY()));
        }

        for (int i = 0; i < moduleNodes.length; i++) {
            node = (AbstractNode) moduleNodes[i];
            coordinateMap.put(node,
                    new Point((int) node.getX(), (int) node.getY()));
        }

    }

    //
    // re-added 5/14/2005 by LY to fix a bug in print

    private void restoreCoordinates() {
        AbstractNode node;
        for (int i = 0; i < substanceNodes.length; i++) {
            node = (AbstractNode) substanceNodes[i];
            Point p = (Point) coordinateMap.get(node);
            node.setLocation((double) p.x, (double) p.y);
        }

        for (int i = 0; i < reactionNodes.length; i++) {
            node = (AbstractNode) reactionNodes[i];
            Point p = (Point) coordinateMap.get(node);
            node.setLocation((double) p.x, (double) p.y);
        }
        for (int i = 0; i < expressionNodes.length; i++) {
            node = (AbstractNode) expressionNodes[i];
            Point p = (Point) coordinateMap.get(node);
            node.setLocation((double) p.x, (double) p.y);
        }

        for (int i = 0; i < moduleNodes.length; i++) {
            node = (AbstractNode) moduleNodes[i];
            Point p = (Point) coordinateMap.get(node);
            node.setLocation((double) p.x, (double) p.y);
        }

    }

    //
    // Note on 5/14/2005: trim() is not yet working
    //

    public void trim() {
        AbstractNode node;
        double maxX = -100;
        double maxY = -100;
        for (int i = 0; i < substanceNodes.length; i++) {
            node = (AbstractNode) substanceNodes[i];
            if (node.getX() > maxX)
                maxX = node.getX() + layout.margin;
            if (node.getY() > maxY)
                maxY = node.getY() + layout.margin;
        }

        for (int i = 0; i < reactionNodes.length; i++) {
            node = (AbstractNode) reactionNodes[i];
            if (node.getX() > maxX)
                maxX = node.getX() + layout.margin;
            if (node.getY() > maxY)
                maxY = node.getY() + layout.margin;
        }

        setPreferredSize(new Dimension((int) (maxX), (int) (maxY)));
        // repaint();
    }

    private AbstractNode getCurrentNode(MouseEvent e) {
        AbstractNode node = null;
        for (int i = 0; i < substanceNodes.length; i++) {
            if (((AbstractNode) (substanceNodes[i])).contains(
                    (double) e.getX(), (double) e.getY())) {
                node = (AbstractNode) (substanceNodes[i]);
                node.setSelected(true);
                return node;
            }
        }

        for (int i = 0; i < reactionNodes.length; i++) {
            if (((AbstractNode) (reactionNodes[i])).contains((double) e.getX(),
                    (double) e.getY())) {
                node = (AbstractNode) (reactionNodes[i]);
                node.setSelected(true);
                return node;
            }
        }

        for (int i = 0; i < expressionNodes.length; i++) {
            if (((AbstractNode) (expressionNodes[i])).contains(
                    (double) e.getX(), (double) e.getY())) {
                node = (AbstractNode) expressionNodes[i];
                node.setSelected(true);
                return node;
            }
        }

        for (int i = 0; i < moduleNodes.length; i++) {
            if (((AbstractNode) (moduleNodes[i])).contains((double) e.getX(),
                    (double) e.getY())) {
                node = (AbstractNode) moduleNodes[i];
                node.setSelected(true);
                return node;
            }
        }

        return null;
    }

    // Note to self(Kanishk Asthana): This function has complicated logic: write
    // a description
    protected void reset() {
        if (substanceNodes != null)
            substanceNodeMap.clear();
        reactionNodeMap.clear();
        expressionNodeMap.clear();
        moduleNodeMap.clear();
        reactionNodeMapForModules.clear();
        expressionNodeMapForModules.clear();

        ((ModularSystem) system).setGraph(this);
        int substanceNumber = system.getSubstances().size();
        int reactionNumber = system.getProgressiveReactions().size()
                + system.getEquilibratedReactions().size();
        int expressionNumber = system.getExpressions().size();
        // Added by Kanishk Asthana 7 June 2013
        int moduleNumber = 0;
        moduleNumber = ((ModularSystem) system).getModules().size();

        dynetica.entity.Substance s;

        for (int i = 0; i < substanceNumber; i++) {
            // Kanishk Asthana

            s = (dynetica.entity.Substance) (system.getSubstances().get(i));

            if (!(s.getSystem() instanceof AbstractModule)) {

                if (s instanceof ExpressionVariable) {
                    expressionNodeMap.put(s, s.getNode());
                }

                else
                    substanceNodeMap.put(s, s.getNode());

            } else if (((AbstractModule) s.getSystem()).isPopedOut()) {
                if (s instanceof ExpressionVariable) {
                    expressionNodeMap.put(s, s.getNode());
                }

                else
                    substanceNodeMap.put(s, s.getNode());

            } else {
                if (s instanceof ExpressionVariable) {
                    expressionNodeMapForModules.put(s, s.getNode());
                }
            }

        }

        substanceNodes = substanceNodeMap.values().toArray();
        expressionNodes = expressionNodeMap.values().toArray();

        // Kanishk Asthana

        expressionNodesForModules = expressionNodeMapForModules.values()
                .toArray();

        dynetica.reaction.Reaction r;

        for (int i = 0; i < system.getProgressiveReactions().size(); i++) {
            r = (dynetica.reaction.Reaction) (system.getProgressiveReactions()
                    .get(i));
            if (!(r.getSystem() instanceof AbstractModule))
                reactionNodeMap.put(r, r.getNode());
            else if (((AbstractModule) r.getSystem()).isPopedOut())
                reactionNodeMap.put(r, r.getNode());
            else
                reactionNodeMapForModules.put(r, r.getNode());
        }

        for (int i = 0; i < system.getEquilibratedReactions().size(); i++) {
            r = (dynetica.reaction.Reaction) (system.getEquilibratedReactions()
                    .get(i));
            if (!(r.getSystem() instanceof AbstractModule))
                reactionNodeMap.put(r, r.getNode());
            else if (((AbstractModule) r.getSystem()).isPopedOut())
                reactionNodeMap.put(r, r.getNode());
            else
                reactionNodeMapForModules.put(r, r.getNode());
        }

        reactionNodes = reactionNodeMap.values().toArray();
        // By Kanishk Asthana 19 June 2013
        reactionNodesForModules = reactionNodeMapForModules.values().toArray();
        // Added by Kanishk Asthana 7 June 2013

        dynetica.system.AbstractModule m;

        for (int i = 0; i < ((ModularSystem) system).getModules().size(); i++) {
            m = (dynetica.system.AbstractModule) ((ModularSystem) system)
                    .getModules().get(i);
            if (!(m.isPopedOut()))
                moduleNodeMap.put(m, m.getNode());
        }

        moduleNodes = moduleNodeMap.values().toArray();
    }

    protected void setLocation(AbstractNode node, double x, double y) {
        if (x > getWidth())
            x = getWidth() - layout.margin;
        else if (x < layout.margin)
            x = layout.margin;
        if (y > getHeight())
            y = getHeight() - layout.margin;
        else if (y < layout.margin)
            y = layout.margin;
        node.setLocation(x, y);
    }

    // Kanishk Asthana
    public void drawGraphForModularSystem(Graphics2D g2, int w, int h,
            int offsetX, int offsetY) {

        g2.setBackground(getBackground());
        g2.setColor(getBackground());
        g2.fillRect(0, 0, w + 2 * offsetX, h + 2 * offsetY);
        g2.setStroke(thinStroke);
        g2.setColor(defaultColor);
        double centerX = w / 2 + offsetX;
        double centerY = h / 2 + offsetY;
        double radiusBig = w / 2 * 0.66;
        double radiusSmall = radiusBig * 0.66;
        double radiusModule = radiusSmall * 0.66;
        double radiusExp = radiusModule * 0.66;

        int substanceNumber = substanceNodeMap.size();//

        double radiusS;
        double radiusR;
        double radiusM = radiusModule;
        double radiusE = radiusExp;

        radiusS = radiusBig;
        radiusR = radiusSmall;

        AbstractNode sn;
        for (int i = 0; i < substanceNodes.length; i++) {
            sn = (AbstractNode) substanceNodes[i];
            if (sn.getX() < layout.margin) {
                double theta = ((double) i / substanceNumber) * 2 * pi;
                double y = radiusS * Math.sin(theta) * h / w + centerY;
                double x = radiusS * Math.cos(theta) + centerX;
                setLocation(sn, x, y);
            } else {
                double x = sn.getX();
                double y = sn.getY();
                if (layout.width != w) {
                    x = x * w / layout.width + offsetX;
                }

                if (layout.height != h) {
                    y = y * h / layout.height + offsetY;
                }
                setLocation(sn, x, y);
            }

            sn.setTextVisible(drawNodeName);
        }

        AbstractNode exp;

        for (int i = 0; i < expressionNodes.length; i++) {
            exp = (AbstractNode) expressionNodes[i];
            if (exp.getX() < layout.margin) {
                double theta = ((double) i / expressionNodes.length) * 2.0 * pi
                        + pi / 2.718;
                double y = radiusE * Math.sin(theta) * h / w + centerY;
                double x = radiusE * Math.cos(theta) + centerX;
                setLocation(exp, x, y);
            } else {
                double x = exp.getX();
                double y = exp.getY();
                if (layout.width != w) {
                    x = x * w / layout.width + offsetX;
                }

                if (layout.height != h) {
                    y = y * h / layout.height + offsetY;
                }
                setLocation(exp, x, y);
            }

            exp.setTextVisible(drawNodeName);
        }

        for (int i = 0; i < reactionNodes.length; i++) {
            AbstractNode rn = (AbstractNode) reactionNodes[i];
            if (rn.getX() < layout.margin) {
                double theta = ((double) i / reactionNodes.length) * 2.0 * pi
                        + pi / 2.718;
                double y = radiusR * Math.sin(theta) * h / w + centerY;
                double x = radiusR * Math.cos(theta) + centerX;
                setLocation(rn, x, y);
            } else {
                double x = rn.getX();
                double y = rn.getY();
                if (layout.width != w) {
                    x = x * w / layout.width + offsetX;
                }

                if (layout.height != h) {
                    y = y * h / layout.height + offsetY;
                }
                setLocation(rn, x, y);
            }

            rn.setTextVisible(drawNodeName);
        }

        AbstractNode md;

        for (int i = 0; i < moduleNodes.length; i++) {
            md = (AbstractNode) moduleNodes[i];
            if (md.getX() < layout.margin) {
                double theta = ((double) i / moduleNodes.length) * 2.0 * pi
                        + pi / 2.718;
                double y = radiusM * Math.sin(theta) * h / w + centerY;
                double x = radiusM * Math.cos(theta) + centerX;
                setLocation(md, x, y);
            } else {
                double x = md.getX();
                double y = md.getY();
                if (layout.width != w) {
                    x = x * w / layout.width + offsetX;
                }

                if (layout.height != h) {
                    y = y * h / layout.height + offsetY;
                }
                setLocation(md, x, y);
            }

            md.setTextVisible(drawNodeName);
        }

        drawLinesToModularSystem(g2);

        drawLinesFromModularSystem(g2);

        g2.setColor(moduleNodeColor);
        for (int i = 0; i < moduleNodes.length; i++)
            ((AbstractNode) (moduleNodes[i])).draw(g2);

        g2.setColor(substanceNodeColor);
        for (int i = 0; i < substanceNodes.length; i++) {
            AbstractNode n = (AbstractNode) substanceNodes[i];
            n.draw(g2);
        }

        g2.setColor(expressionNodeColor);
        for (int i = 0; i < expressionNodes.length; i++) {
            ((AbstractNode) (expressionNodes[i])).draw(g2);
        }

        g2.setColor(reactionNodeColor);
        for (int i = 0; i < reactionNodes.length; i++)
            ((AbstractNode) (reactionNodes[i])).draw(g2);

    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        graph = g2;
        drawGraphForModularSystem(g2, getWidth(), getHeight(), 0, 0);
        layout.width = getWidth();
        layout.height = getHeight();
    }

    protected SNode getSubstanceNode(Substance s) {
        return (SNode) (substanceNodeMap.get(s));
    }

    protected RNode getReactionNode(Reaction r) {
        if (reactionNodeMap.containsKey(r))
            return (RNode) (reactionNodeMap.get(r));
        else {
            RNode rn = new RNode(r);
            rn.setLocation(Math.random() * getWidth() * 0.8, Math.random()
                    * getHeight() * 0.8);
            reactionNodeMap.put(r, rn);
            rn.setTextVisible(drawNodeName);
            rn.draw(graph);
            return rn;
        }
    }

    // Kanishk Asthana
    protected void drawLinesToModularSystem(Graphics2D g) {
        g.setStroke(stroke);
        for (int i = 0; i < reactionNodes.length; i++) {
            RNode rn = (RNode) reactionNodes[i];
            double x1 = rn.getCenterX();
            double y1 = rn.getCenterY();
            java.util.List reactants = rn.getReaction().getReactants();
            java.util.List products = rn.getReaction().getProducts();
            java.util.List catalysts = rn.getReaction().getCatalysts();

            g.setColor(reactantLineColor);
            // Something is wrong here, I'm pretty sure! this may cause a bug
            // later! FIGURE IT OUT!
            for (int j = 0; j < reactants.size(); j++) {
                Substance s = (Substance) reactants.get(j);
                if (!(s.getSystem() instanceof AbstractModule)) {
                    AbstractNode sn = ((Substance) reactants.get(j)).getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                } else if (((AbstractModule) s.getSystem()).isPopedOut()) {
                    AbstractNode sn = ((Substance) reactants.get(j)).getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }

                } else // This may be problematic when extending the code: be
                       // carefull!
                {
                    if (((AbstractModule) s.getSystem()).getNode() != null) {
                        double x2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterX();
                        double y2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterY();
                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                }
            }
            g.setColor(productLineColor);

            for (int j = 0; j < products.size(); j++) {

                Substance s = (Substance) products.get(j);
                if (!(s.getSystem() instanceof AbstractModule)) {
                    AbstractNode sn = ((Substance) products.get(j)).getNode();
                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();
                        new ArrowedLine(x1, y1, x2, y2, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                } else if (((AbstractModule) s.getSystem()).isPopedOut()) {
                    AbstractNode sn = ((Substance) products.get(j)).getNode();
                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();
                        new ArrowedLine(x1, y1, x2, y2, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                } else {
                    if (((AbstractModule) s.getSystem()).getNode() != null) {
                        double x2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterX();
                        double y2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterY();
                        new ArrowedLine(x1, y1, x2, y2, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }

                }
            }

            g.setColor(catalystLineColor);
            for (int j = 0; j < catalysts.size(); j++) {
                Substance s = (Substance) (catalysts.get(j));
                // System.out.println(s);

                if (!(s.getSystem() instanceof AbstractModule)) {
                    AbstractNode sn = s.getNode();
                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();
                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                } else if (((AbstractModule) s.getSystem()).isPopedOut()) {
                    AbstractNode sn = s.getNode();
                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();
                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                } else {
                    if (((AbstractModule) s.getSystem()).getNode() != null) {
                        double x2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterX();
                        double y2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterY();
                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }

                }

            }

        }

        //
        // draw lines connecting an ExpressionNode to its dependent substances
        // and expressions.
        //

        g.setColor(expressionLineColor);
        for (int i = 0; i < expressionNodes.length; i++) {
            ENode en = (ENode) expressionNodes[i];
            if (en != null) {
                double x1 = en.getCenterX();
                double y1 = en.getCenterY();
                java.util.List substances = ((ExpressionVariable) (en
                        .getEntity())).getSubstances();
                for (int j = 0; j < substances.size(); j++) {
                    Substance s = (Substance) substances.get(j);
                    // System.out.println(substances.get(j));
                    if (!(s.getSystem() instanceof AbstractModule)) {
                        AbstractNode sn = ((Substance) (substances.get(j)))
                                .getNode();
                        if (sn != null) {
                            double x2 = sn.getCenterX();
                            double y2 = sn.getCenterY();
                            new ArrowedLine(x2, y2, x1, y1, layout.arrowSize,
                                    0.4, drawArrows, false).draw(g);
                        }
                    } else if (((AbstractModule) s.getSystem()).isPopedOut()) {
                        AbstractNode sn = ((Substance) (substances.get(j)))
                                .getNode();
                        if (sn != null) {
                            double x2 = sn.getCenterX();
                            double y2 = sn.getCenterY();
                            new ArrowedLine(x2, y2, x1, y1, layout.arrowSize,
                                    0.4, drawArrows, false).draw(g);
                        }
                    } else {
                        if (((AbstractModule) s.getSystem()).getNode() != null) {
                            double x2 = ((AbstractModule) s.getSystem())
                                    .getNode().getCenterX();
                            double y2 = ((AbstractModule) s.getSystem())
                                    .getNode().getCenterY();
                            new ArrowedLine(x2, y2, x1, y1, layout.arrowSize,
                                    0.4, drawArrows, false).draw(g);
                        }

                    }

                }

            }
        }

    }

    protected void drawLinesFromModularSystem(Graphics2D g) {
        g.setStroke(stroke);
        for (int i = 0; i < reactionNodesForModules.length; i++) {

            RNode rn = (RNode) reactionNodesForModules[i];

            AbstractModule superSystem = (AbstractModule) rn.getReaction()
                    .getSystem();
            MNode md = (MNode) superSystem.getNode();

            double x1 = md.getCenterX();
            double y1 = md.getCenterY();

            java.util.List reactants = rn.getReaction().getReactants();
            java.util.List products = rn.getReaction().getProducts();
            java.util.List catalysts = rn.getReaction().getCatalysts();

            g.setColor(reactantLineColor);

            for (int j = 0; j < reactants.size(); j++) {
                Substance s = (Substance) reactants.get(j);
                AbstractSystem substanceSystem = system.get(s.getName())
                        .getSystem();

                if (!(substanceSystem instanceof AbstractModule)) {
                    AbstractNode sn = (AbstractNode) s.getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                } else if (!substanceSystem
                        .equals(rn.getReaction().getSystem())
                        && ((AbstractModule) s.getSystem()).isPopedOut()) {
                    AbstractNode sn = (AbstractNode) s.getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                }

                else if (!substanceSystem.equals(rn.getReaction().getSystem())
                        && !((AbstractModule) s.getSystem()).isPopedOut()) {
                    double x2 = ((AbstractModule) s.getSystem()).getNode()
                            .getCenterX();
                    double y2 = ((AbstractModule) s.getSystem()).getNode()
                            .getCenterY();
                    new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                            drawArrows, false).draw(g);
                }

            }
            g.setColor(productLineColor);

            for (int j = 0; j < products.size(); j++) {
                Substance s = (Substance) products.get(j);
                AbstractSystem substanceSystem = system.get(s.getName())
                        .getSystem();
                if (!(substanceSystem instanceof AbstractModule)) {
                    AbstractNode sn = (AbstractNode) s.getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x1, y1, x2, y2, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }

                } else if (!substanceSystem
                        .equals(rn.getReaction().getSystem())
                        && ((AbstractModule) s.getSystem()).isPopedOut()) {
                    AbstractNode sn = (AbstractNode) s.getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x1, y1, x2, y2, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                }

                else if (!substanceSystem.equals(rn.getReaction().getSystem())
                        && !((AbstractModule) s.getSystem()).isPopedOut()) {
                    double x2 = ((AbstractModule) s.getSystem()).getNode()
                            .getCenterX();
                    double y2 = ((AbstractModule) s.getSystem()).getNode()
                            .getCenterY();
                    new ArrowedLine(x1, y1, x2, y2, layout.arrowSize, 0.4,
                            drawArrows, false).draw(g);
                }

            }
            g.setColor(catalystLineColor);
            for (int j = 0; j < catalysts.size(); j++) {
                Substance s = (Substance) catalysts.get(j);
                AbstractSystem substanceSystem = system.get(s.getName())
                        .getSystem();
                if (!(substanceSystem instanceof AbstractModule)) {
                    AbstractNode sn = (AbstractNode) s.getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                } else if (!substanceSystem
                        .equals(rn.getReaction().getSystem())
                        && ((AbstractModule) s.getSystem()).isPopedOut()) {
                    AbstractNode sn = (AbstractNode) s.getNode();

                    if (sn != null) {
                        double x2 = sn.getCenterX();
                        double y2 = sn.getCenterY();

                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }
                }

                else if (!substanceSystem.equals(rn.getReaction().getSystem())
                        && !((AbstractModule) s.getSystem()).isPopedOut()) {
                    double x2 = ((AbstractModule) s.getSystem()).getNode()
                            .getCenterX();
                    double y2 = ((AbstractModule) s.getSystem()).getNode()
                            .getCenterY();
                    new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                            drawArrows, false).draw(g);
                }

            }
        }
        g.setColor(expressionLineColor);
        for (int i = 0; i < expressionNodesForModules.length; i++) {
            ENode en = (ENode) expressionNodesForModules[i];
            if (en != null) {
                AbstractModule superSystem = (AbstractModule) en.getEntity()
                        .getSystem();
                MNode md = (MNode) superSystem.getNode();

                double x1 = md.getCenterX();
                double y1 = md.getCenterY();

                java.util.List substances = ((ExpressionVariable) (en
                        .getEntity())).getSubstances();

                for (int j = 0; j < substances.size(); j++) {
                    Substance s = (Substance) substances.get(j);
                    AbstractSystem substanceSystem = system.get(s.getName())
                            .getSystem();
                    if (!(substanceSystem instanceof AbstractModule)) {
                        AbstractNode sn = ((Substance) (substances.get(j)))
                                .getNode();
                        if (sn != null) {
                            double x2 = sn.getCenterX();
                            double y2 = sn.getCenterY();
                            new ArrowedLine(x2, y2, x1, y1, layout.arrowSize,
                                    0.4, drawArrows, false).draw(g);
                        }
                    } else if (!substanceSystem.equals(en.getEntity()
                            .getSystem())
                            && ((AbstractModule) s.getSystem()).isPopedOut()) {
                        AbstractNode sn = ((Substance) (substances.get(j)))
                                .getNode();
                        if (sn != null) {
                            double x2 = sn.getCenterX();
                            double y2 = sn.getCenterY();
                            new ArrowedLine(x2, y2, x1, y1, layout.arrowSize,
                                    0.4, drawArrows, false).draw(g);
                        }
                    } else if (!substanceSystem.equals(en.getEntity()
                            .getSystem())
                            && !((AbstractModule) s.getSystem()).isPopedOut()) {
                        double x2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterX();
                        double y2 = ((AbstractModule) s.getSystem()).getNode()
                                .getCenterY();
                        new ArrowedLine(x2, y2, x1, y1, layout.arrowSize, 0.4,
                                drawArrows, false).draw(g);
                    }

                }

            }

        }

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the FormEditor.
     */
    // <editor-fold defaultstate="collapsed"
    // desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        systemPopup = new javax.swing.JPopupMenu();
        refreshItem = new javax.swing.JMenuItem();
        showNodeNameBox = new javax.swing.JCheckBoxMenuItem();
        showArrowBox = new javax.swing.JCheckBoxMenuItem();
        nodeSizeMenu = new javax.swing.JMenu();
        growNodeItem = new javax.swing.JMenuItem();
        shrinkNodeItem = new javax.swing.JMenuItem();
        lineWidthMenu = new javax.swing.JMenu();
        lineWidthIncreaseItem = new javax.swing.JMenuItem();
        lineWidthDecreaseItem = new javax.swing.JMenuItem();
        arrowSizeMenu = new javax.swing.JMenu();
        increaseArrowItem = new javax.swing.JMenuItem();
        decreaseArrowItem = new javax.swing.JMenuItem();
        graphSizeMenu = new javax.swing.JMenu();
        growGraph = new javax.swing.JMenuItem();
        shrinkGraph = new javax.swing.JMenuItem();
        trimItem = new javax.swing.JMenuItem();
        entityMenu = new javax.swing.JMenu();
        renameEntity = new javax.swing.JMenuItem();
        editEntity = new javax.swing.JMenuItem();
        deleteEntity = new javax.swing.JMenuItem();
        cloneEntity = new javax.swing.JMenuItem();
        popOutItem = new javax.swing.JMenuItem();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        jMenu2 = new javax.swing.JMenu();

        refreshItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        refreshItem.setText("Refresh System");
        refreshItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshItemActionPerformed(evt);
            }
        });
        systemPopup.add(refreshItem);

        showNodeNameBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        showNodeNameBox.setSelected(true);
        showNodeNameBox.setText("Show Node Names");
        showNodeNameBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hideNameBoxActionPerformed(evt);
            }
        });
        systemPopup.add(showNodeNameBox);

        showArrowBox.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        showArrowBox.setSelected(true);
        showArrowBox.setText("Show Arrows");
        showArrowBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                showArrowBoxActionPerformed(evt);
            }
        });
        systemPopup.add(showArrowBox);

        nodeSizeMenu.setText("Node Size");
        nodeSizeMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        growNodeItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        growNodeItem.setText("Increase");
        growNodeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                growNodeItemActionPerformed(evt);
            }
        });
        nodeSizeMenu.add(growNodeItem);

        shrinkNodeItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        shrinkNodeItem.setText("Decrease");
        shrinkNodeItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shrinkNodeItemActionPerformed(evt);
            }
        });
        nodeSizeMenu.add(shrinkNodeItem);

        systemPopup.add(nodeSizeMenu);

        lineWidthMenu.setText("Line Width");
        lineWidthMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        lineWidthIncreaseItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lineWidthIncreaseItem.setText("Increase");
        lineWidthIncreaseItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        lineWidthIncreaseItemActionPerformed(evt);
                    }
                });
        lineWidthMenu.add(lineWidthIncreaseItem);

        lineWidthDecreaseItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        lineWidthDecreaseItem.setText("Decrease");
        lineWidthDecreaseItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        lineWidthDecreaseItemActionPerformed(evt);
                    }
                });
        lineWidthMenu.add(lineWidthDecreaseItem);

        systemPopup.add(lineWidthMenu);

        arrowSizeMenu.setText("Arrow Size");
        arrowSizeMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        increaseArrowItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        increaseArrowItem.setText("Increase");
        increaseArrowItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        increaseArrowItemActionPerformed(evt);
                    }
                });
        arrowSizeMenu.add(increaseArrowItem);

        decreaseArrowItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        decreaseArrowItem.setText("Decrease");
        decreaseArrowItem
                .addActionListener(new java.awt.event.ActionListener() {
                    public void actionPerformed(java.awt.event.ActionEvent evt) {
                        decreaseArrowItemActionPerformed(evt);
                    }
                });
        arrowSizeMenu.add(decreaseArrowItem);

        graphSizeMenu.setText("Graph Size");
        graphSizeMenu.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N

        growGraph.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        growGraph.setText("Increase");
        growGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                growGraphActionPerformed(evt);
            }
        });
        graphSizeMenu.add(growGraph);

        shrinkGraph.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        shrinkGraph.setText("Decrease");
        shrinkGraph.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                shrinkGraphActionPerformed(evt);
            }
        });
        graphSizeMenu.add(shrinkGraph);

        trimItem.setFont(new java.awt.Font("Dialog", 0, 12)); // NOI18N
        trimItem.setText("Remove margins");
        trimItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trimItemActionPerformed(evt);
            }
        });
        graphSizeMenu.add(trimItem);

        arrowSizeMenu.add(graphSizeMenu);

        systemPopup.add(arrowSizeMenu);

        entityMenu.setText("Entity Menu");
        entityMenu.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N

        renameEntity.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        renameEntity.setText("Rename");
        renameEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                renameEntityActionPerformed(evt);
            }
        });
        entityMenu.add(renameEntity);

        editEntity.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        editEntity.setText("Edit");
        editEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editEntityActionPerformed(evt);
            }
        });
        entityMenu.add(editEntity);

        deleteEntity.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        deleteEntity.setText("Delete");
        deleteEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                deleteEntityActionPerformed(evt);
            }
        });
        entityMenu.add(deleteEntity);

        cloneEntity.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        cloneEntity.setText("Clone");
        cloneEntity.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cloneEntityActionPerformed(evt);
            }
        });
        entityMenu.add(cloneEntity);

        systemPopup.add(entityMenu);

        popOutItem.setFont(new java.awt.Font("Tahoma", 0, 12)); // NOI18N
        popOutItem.setText("Pop Out Module");
        popOutItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                popOutItemActionPerformed(evt);
            }
        });
        systemPopup.add(popOutItem);

        jMenu1.setText("File");
        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents

    private void cloneEntityActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_cloneEntityActionPerformed
        system.cloneEntity(system.get(currentNode.getNodeName()));
    }// GEN-LAST:event_cloneEntityActionPerformed

    private void deleteEntityActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_deleteEntityActionPerformed
        if (currentNode.getEntity().getSystem().equals(system)) {
            String currentNodeName = currentNode.getNodeName();
            system.remove(currentNodeName);
        }
    }// GEN-LAST:event_deleteEntityActionPerformed

    private void editEntityActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_editEntityActionPerformed
        String name = currentNode.getNodeName();
        if (system.contains(name)) {
            JPanel editor = system.get(name).editor();
            EntityEditorFrame editorFrame = new EntityEditorFrame();
            editorFrame.addEditor(name, editor);
            editorFrame.pack();

            if (editorFrame.getComponentCount() == 1) {
                editorFrame.setLocation(this.getLocationOnScreen());
            }

            if (!editorFrame.isVisible())
                editorFrame.setVisible(true);
        }
    }// GEN-LAST:event_editEntityActionPerformed

    private void renameEntityActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_renameEntityActionPerformed

        String currentNodeName = currentNode.getNodeName();

        String newNodeName = (String) JOptionPane.showInputDialog(this,
                "Enter the new name", "Input", JOptionPane.QUESTION_MESSAGE,
                null, null, currentNodeName);

        if (newNodeName != null)
            system.get(currentNodeName).setName(newNodeName);

    }// GEN-LAST:event_renameEntityActionPerformed

    private void trimItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_trimItemActionPerformed
        trim();
        repaint();
    }// GEN-LAST:event_trimItemActionPerformed

    private void lineWidthDecreaseItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_lineWidthDecreaseItemActionPerformed
        changeLineWidth(0.6f);
        relativeLineWidth *= 0.6f;
        repaint();
    }// GEN-LAST:event_lineWidthDecreaseItemActionPerformed

    private void lineWidthIncreaseItemActionPerformed(
            java.awt.event.ActionEvent evt) {// GEN-FIRST:event_lineWidthIncreaseItemActionPerformed
        changeLineWidth(1.6f);
        relativeLineWidth *= 1.6f;
        repaint();
    }// GEN-LAST:event_lineWidthIncreaseItemActionPerformed

    private void shrinkGraphActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_shrinkGraphActionPerformed
        setPreferredSize(new Dimension((int) (getWidth() * 0.85),
                (int) (getHeight() * 0.85)));
        fireSizeChange();
        repaint();

    }// GEN-LAST:event_shrinkGraphActionPerformed

    private void growGraphActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_growGraphActionPerformed
        setPreferredSize(new Dimension((int) (getWidth() * 1.15),
                (int) (getHeight() * 1.15)));
        repaint();
        fireSizeChange();

    }// GEN-LAST:event_growGraphActionPerformed

    private void decreaseArrowItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_decreaseArrowItemActionPerformed
        layout.arrowSize *= 0.8;
        repaint();
    }// GEN-LAST:event_decreaseArrowItemActionPerformed

    private void increaseArrowItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_increaseArrowItemActionPerformed
        layout.arrowSize *= 1.2;
        repaint();
    }// GEN-LAST:event_increaseArrowItemActionPerformed

    private void showArrowBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_showArrowBoxActionPerformed
        drawArrows = showArrowBox.getState();
        repaint();
    }// GEN-LAST:event_showArrowBoxActionPerformed

    private void shrinkNodeItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_shrinkNodeItemActionPerformed
        changeNodeSize(0.85);
        relativeNodeSize *= 0.85;
        repaint();
    }// GEN-LAST:event_shrinkNodeItemActionPerformed

    private void growNodeItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_growNodeItemActionPerformed
        changeNodeSize(1.2);
        relativeNodeSize *= 1.2;
        repaint();
    }// GEN-LAST:event_growNodeItemActionPerformed

    private void refreshItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_refreshItemActionPerformed
        reset();
        repaint();
    }// GEN-LAST:event_refreshItemActionPerformed

    private void hideNameBoxActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_hideNameBoxActionPerformed
        if (drawNodeName != showNodeNameBox.getState()) {
            drawNodeName = showNodeNameBox.getState();
            repaint();
        }
    }// GEN-LAST:event_hideNameBoxActionPerformed

    private void popOutItemActionPerformed(java.awt.event.ActionEvent evt) {// GEN-FIRST:event_popOutItemActionPerformed
        if (currentNode.getEntity() instanceof AbstractModule) {
            AbstractModule mod = (AbstractModule) currentNode.getEntity();
            mod.setPopOut(true);
            reset();
            repaint();
        } else
            System.out.println("Not Module:This should not happen!");
    }// GEN-LAST:event_popOutItemActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JMenu arrowSizeMenu;
    private javax.swing.JMenuItem cloneEntity;
    private javax.swing.JMenuItem decreaseArrowItem;
    private javax.swing.JMenuItem deleteEntity;
    private javax.swing.JMenuItem editEntity;
    private javax.swing.JMenu entityMenu;
    private javax.swing.JMenu graphSizeMenu;
    private javax.swing.JMenuItem growGraph;
    private javax.swing.JMenuItem growNodeItem;
    private javax.swing.JMenuItem increaseArrowItem;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JMenuItem lineWidthDecreaseItem;
    private javax.swing.JMenuItem lineWidthIncreaseItem;
    private javax.swing.JMenu lineWidthMenu;
    private javax.swing.JMenu nodeSizeMenu;
    private javax.swing.JMenuItem popOutItem;
    private javax.swing.JMenuItem refreshItem;
    private javax.swing.JMenuItem renameEntity;
    private javax.swing.JCheckBoxMenuItem showArrowBox;
    private javax.swing.JCheckBoxMenuItem showNodeNameBox;
    private javax.swing.JMenuItem shrinkGraph;
    private javax.swing.JMenuItem shrinkNodeItem;
    private javax.swing.JPopupMenu systemPopup;
    private javax.swing.JMenuItem trimItem;

    // End of variables declaration//GEN-END:variables

    public void changeLineWidth(float ratio) {
        layout.lineWidth *= ratio;
        stroke = new BasicStroke(layout.lineWidth);
        fireSizeChange();
    }

    public void changeNodeSize(double ratio) {
        AbstractNode node;
        for (int i = 0; i < substanceNodes.length; i++) {
            node = (AbstractNode) (substanceNodes[i]);
            node.setChangeRatio(ratio);
        }

        for (int i = 0; i < reactionNodes.length; i++) {
            node = (AbstractNode) (reactionNodes[i]);
            node.setChangeRatio(ratio);
        }

        for (int i = 0; i < expressionNodes.length; i++) {
            node = (AbstractNode) (expressionNodes[i]);
            node.setChangeRatio(ratio);
        }

        for (int i = 0; i < moduleNodes.length; i++) {
            node = (AbstractNode) (moduleNodes[i]);
            node.setChangeRatio(ratio);
        }

        fireSizeChange();

    }

    public class GraphMouseListener implements MouseListener {
        public void mouseClicked(java.awt.event.MouseEvent e) {
            if (currentNode != null && e.getClickCount() == 2) {
                javax.swing.JFrame jf = new javax.swing.JFrame();
                jf.getContentPane().add(currentNode.editor());
                jf.pack();
                jf.show();
            }
        }

        public void mousePressed(java.awt.event.MouseEvent e) {
            if (currentNode != null)
                currentNode.setSelected(false);

            currentNode = getCurrentNode(e);
            if (currentNode != null) {
                lastX = currentNode.getX() - e.getX();

                lastY = currentNode.getY() - e.getY();
            } else {
                lastX = e.getX();
                lastY = e.getY();
            }
            repaint();
            showPopup(e);
        }

        public void mouseReleased(java.awt.event.MouseEvent e) {

            addGesture(e);

            showPopup(e);

        }

        public void mouseExited(java.awt.event.MouseEvent e) {
        }

        public void mouseEntered(java.awt.event.MouseEvent e) {
        }

        private void showPopup(java.awt.event.MouseEvent e) {

            if (e.isPopupTrigger()) {

                try {
                    if (currentNode.getEntity() instanceof AbstractModule)
                        popOutItem.setVisible(true);
                    else
                        popOutItem.setVisible(false);

                } catch (Exception exp) {
                    System.out.println("Current Node is null");
                    currentNode = null;
                }

                systemPopup.show(e.getComponent(), e.getX(), e.getY());
            }

        }
    }

    protected void addGesture(java.awt.event.MouseEvent e) {
        if (currentNode != null
                && !(currentNode.getEntity() instanceof AbstractModule)) {
            for (int i = 0; i < moduleNodes.length; i++)
                if (((AbstractNode) (moduleNodes[i])).contains(
                        (double) e.getX(), (double) e.getY())) {
                    AbstractModule mod = (AbstractModule) ((AbstractNode) moduleNodes[i])
                            .getEntity();
                    Entity entityToTransfer = currentNode.getEntity();
                    AbstractSystem transferingFromSystem = currentNode
                            .getEntity().getSystem();
                    entityToTransfer.setSystem(mod);
                    if (transferingFromSystem instanceof AbstractModule)
                        ((AbstractModule) transferingFromSystem)
                                .removeEntityFromTreeModel(entityToTransfer);
                    else if (transferingFromSystem instanceof ModularSystem)
                        ((ModularSystem) transferingFromSystem)
                                .removeEntityFromTreeModel(entityToTransfer);
                    mod.insertEntityIntoTreeModel(entityToTransfer);
                    // Setting a negative value of X repositions the entity
                    // automatically
                    entityToTransfer.setX(-1.0);
                    mod.fireSystemStructureChange();

                    currentNode = (AbstractNode) moduleNodes[i];
                    system.fireSystemStructureChange();
                    currentNode = (AbstractNode) moduleNodes[i];
                    currentNode.setSelected(true);
                    repaint();
                    System.out.println("Add Gesture Successful!");
                }
        }

    }

    public class GraphMouseMotionListener implements MouseMotionListener {

        public void mouseDragged(MouseEvent e) {
            if (currentNode != null) {
                double x = lastX + e.getX();
                if (x > getWidth())
                    x = 0;
                else if (x < 0)
                    x = 0;

                double y = lastY + e.getY();
                if (y > getHeight())
                    y = 0;
                else if (y < 0)
                    y = 0;

                currentNode.setLocation(x, y);
                repaint();

                system.fireSystemStateChange();
            }

            else {
                double endX = e.getX();
                double endY = e.getY();
            }

        }

        public void mouseMoved(MouseEvent e) {
            AbstractNode hoverNode = getHoverNode(e);

            if (hoverNode != null) {
                if (currentHoverNode == null) {
                    currentHoverNode = hoverNode;
                    currentHoverNode.drawInformationBox(true);
                    repaint();
                    // System.out.println("This is executed once.");
                }
            } else {
                if (currentHoverNode != null) {
                    currentHoverNode.drawInformationBox(false);
                    currentHoverNode = null;
                    repaint();
                    // System.out.println("This is also executed only once!");
                }
            }

        }

    }

    private AbstractNode getHoverNode(MouseEvent e) {
        AbstractNode node = null;
        for (int i = 0; i < substanceNodes.length; i++) {
            if (((AbstractNode) (substanceNodes[i])).contains(
                    (double) e.getX(), (double) e.getY())) {
                node = (AbstractNode) (substanceNodes[i]);
                return node;
            }
        }

        for (int i = 0; i < reactionNodes.length; i++) {
            if (((AbstractNode) (reactionNodes[i])).contains((double) e.getX(),
                    (double) e.getY())) {
                node = (AbstractNode) (reactionNodes[i]);
                return node;
            }
        }

        for (int i = 0; i < expressionNodes.length; i++) {
            if (((AbstractNode) (expressionNodes[i])).contains(
                    (double) e.getX(), (double) e.getY())) {
                node = (AbstractNode) expressionNodes[i];
                return node;
            }
        }

        for (int i = 0; i < moduleNodes.length; i++) {
            if (((AbstractNode) (moduleNodes[i])).contains((double) e.getX(),
                    (double) e.getY())) {
                node = (AbstractNode) moduleNodes[i];
                return node;
            }
        }

        return null;
    }

    public int print(Graphics g, PageFormat pf, int pi) throws PrinterException {
        if (pi >= 1) {
            return Printable.NO_SUCH_PAGE;
        }
        double rw = pf.getImageableWidth() / getWidth();
        double rh = pf.getImageableHeight() / getHeight();
        double r = Math.min(rw, rh);
        int w = (int) (getWidth() * r);
        int h = (int) (getHeight() * r);
        layout.arrowSize *= r;
        changeNodeSize(r);
        changeLineWidth((float) r);

        storeCurrentCoordinates();
        drawGraphForModularSystem((Graphics2D) g, w, h,
                (int) pf.getImageableX(), (int) pf.getImageableY());
        restoreCoordinates();
        changeNodeSize(1.0 / r);
        changeLineWidth((float) (1.0 / r));
        layout.arrowSize /= r;

        return Printable.PAGE_EXISTS;
    }

    public interface SizeChangeListener extends java.util.EventListener {
        void sizeChanged(SizeChangeEvent e);
    }

}
