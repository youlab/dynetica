//EntityEditor.java

package dynetica.gui.entities;

import dynetica.entity.*;
import dynetica.reaction.*;
import java.beans.*;
import java.lang.reflect.*;
import javax.swing.*;
import java.awt.event.*;

public class EntityEditor extends JPanel {

    /** Holds value of property entity. */
    private Entity entity;
    PropertyDescriptor[] properties;
    JLabel[] labels;
    JTextField[] values;
    boolean[] editable;
    //
    // entityInfo stores every piece of information we need
    // to know about an Entity.
    //
    BeanInfo entityInfo = null;

    public EntityEditor(Entity entity) {
        setEntity(entity);
        initComponents();
    }

    private void initComponents() {
        try {
            entityInfo = Introspector.getBeanInfo(entity.getClass(),
                    java.lang.Object.class);
            properties = entityInfo.getPropertyDescriptors();
            int numberOfRows = properties.length + 2;

            JLabel propertyLabel = new JLabel("Property");
            JLabel propertyValueLabel = new JLabel("Value");

            add(propertyLabel);
            add(propertyValueLabel);

            labels = new JLabel[properties.length];
            values = new JTextField[properties.length];
            editable = new boolean[properties.length];
            for (int i = 0; i < properties.length; i++) {
                Method setter = properties[i].getWriteMethod();
                Method getter = properties[i].getReadMethod();
                if (setter != null && getter != null) {
                    try {
                        labels[i] = new JLabel(properties[i].getName());
                        //
                        // if the field happens to be the system of the entity
                        // treat it differently: instead of printing out the
                        // details of the system, print out the system name only
                        //
                        if (properties[i].getName().compareTo("system") == 0) {
                            if (entity.getSystem() != null)
                                values[i] = new JTextField(entity.getSystem()
                                        .getName());
                            else
                                values[i] = new JTextField();
                            values[i].setEditable(false);
                            editable[i] = false;
                        } else {
                            Object currentValue = properties[i].getReadMethod()
                                    .invoke(entity, null);
                            if (currentValue != null)
                                values[i] = new JTextField(
                                        currentValue.toString());
                            else
                                values[i] = new JTextField();

                            editable[i] = true;
                        }
                        add(labels[i]);
                        add(values[i]);
                    } catch (Exception e) {
                        System.out.println(properties[i].getReadMethod());
                        e.printStackTrace();
                        numberOfRows--;
                        editable[i] = false;
                    }
                    ;
                } else {
                    numberOfRows--;
                    editable[i] = false;
                }
            }

            JButton ok = new JButton("OK");
            ok.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    okActionPerformed(evt);
                }
            });
            JButton cancel = new JButton("Cancel");
            cancel.addActionListener(new java.awt.event.ActionListener() {
                public void actionPerformed(java.awt.event.ActionEvent evt) {
                    cancelActionPerformed(evt);
                }
            });
            add(ok);
            add(cancel);
            setLayout(new java.awt.GridLayout(numberOfRows, 2));
        }

        catch (IntrospectionException ex) {
            System.out.println("Couldn't introspect "
                    + entity.getClass().getName());
            System.exit(1);
        }
    }

    private void cancelActionPerformed(java.awt.event.ActionEvent evt) {
        // dispose();
    }

    private void okActionPerformed(java.awt.event.ActionEvent evt) {
        try {
            for (int i = 0; i < properties.length; i++) {
                if (editable[i]) {
                    Method setter = properties[i].getWriteMethod();
                    Class p = properties[i].getPropertyType();
                    Object[] args = new Object[1];
                    if (p.getName().compareTo("double") == 0)
                        args[0] = Double.valueOf(values[i].getText());
                    else if (p.getName().compareTo("int") == 0)
                        args[0] = Integer.valueOf(values[i].getText());
                    else if (p.getName().compareTo("boolean") == 0)
                        args[0] = Boolean.valueOf(values[i].getText());
                    else
                        args[0] = values[i].getText();

                    setter.invoke(entity, args);
                }
            }
            // sysPanel.updateTree();
            System.out.println(entity);
        } catch (IllegalArgumentException iae) {
            System.out.println("IllegalArgumentException: " + iae.getMessage());
        } catch (IllegalAccessException iae) {
            System.out.println("IllegalAccessException: " + iae.getMessage());
        } catch (InvocationTargetException ite) {
            System.out
                    .println("InvocationTargetException: " + ite.getMessage());
        }
    }

    /**
     * Getter for property entity.
     * 
     * @return Value of property entity.
     */
    public Entity getEntity() {
        return entity;
    }

    /**
     * Setter for property entity.
     * 
     * @param entity
     *            New value of property entity.
     */
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    /*
     * public static class Tester { public static void main(String[] args) {
     * dynetica.system.ReactiveSystem rs = new
     * dynetica.system.ReactiveSystem("RS"); dynetica.entity.Substance en = new
     * dynetica.entity.Substance();
     * 
     * en.setName("XYZ"); en.setSystem(rs);
     * en.setAnnotation("A test substance");
     * 
     * dynetica.reaction.ProgressiveReaction pr = new
     * dynetica.reaction.ProgressiveReaction("TestReaction", rs); try {
     * pr.setProperty("stoichiometry", "   A + B -> 3 C");
     * pr.setProperty("kinetics", " k1 * [A] * [B]/[C]^2");
     * System.out.println("parsing properties OK\n"); System.out.println(pr); }
     * catch (Exception e) { System.out.println(e); }
     * 
     * EquilibratedMassAction ema = new EquilibratedMassAction ("TestReaction",
     * rs); try { ema.setProperty("stoichiometry", "A + 3 B -> C + 2 D");
     * ema.setProperty("K", "10000"); System.out.println(ema); //
     * ema.printStatus(); ema.update(); // ema.printStatus(); } catch (Exception
     * e) { System.out.println(e); }
     * 
     * Parameter vmax = new Parameter("vmax",rs, 10, 10, 10); Parameter km = new
     * Parameter("Km",rs, 20, 20, 20); MichaelisMentenReaction r = new
     * MichaelisMentenReaction("MichaelisMentenReaction", rs, vmax, km);
     * Substance e = new Substance("E", rs); Substance s = new Substance("S",
     * rs); Substance p = new Substance("P", rs); try {
     * r.setProperty("substrate", "S"); r.setProperty("enzyme", "E");
     * r.setProperty("product", "P");
     * 
     * e.setValue(0.01); //h2.setConcentration(1.5); s.setValue(0.01);
     * System.out.println(r); System.out.println("Rate:" + r.getRate()); } catch
     * (Exception upe) { System.out.println(upe); }
     * 
     * JFrame jf = new ApplicationFrame("EntityEditor");
     * //jf.getContentPane().add(r.editor());
     * 
     * JPanel jp = new EntityEditor(en); jf.getContentPane().add(jp);
     * jf.addWindowListener (new WindowAdapter() { public void
     * windowClosing(WindowEvent e) { System.exit(0); } }); //jf.setSize(400,
     * 400);
     * 
     * jf.pack(); jf.show(); } }
     */

} // /:~
