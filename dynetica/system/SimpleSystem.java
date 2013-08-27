/*
 * SimpleSystem.java
 *
 * Created on April 13, 2001, 1:38 AM
 * Modification (L. You): 9/7/2001. Extended SimpleSystem so that it can fire an event when
 * entities are added to or removed from the system.
 */
/**
 * SimpleSystem is a trivial implementation of the interface AbstractSystem
 * @author  Lingchong You
 * @version 0.5
 *
 */
package dynetica.system;

import dynetica.entity.*;
import java.util.*;
import java.io.*;
import java.beans.*;
import dynetica.event.*;
import dynetica.gui.visualization.AbstractNode;

public class SimpleSystem extends Entity implements AbstractSystem {

    // everything that appears in a system is stored in a hashmap and indexed
    // by its name.
    protected Map entities = new HashMap();

    /** Holds value of property saved. */
    private boolean saved;

    /** Holds value of property file. */
    protected File file;

    javax.swing.event.EventListenerList listenerList = new javax.swing.event.EventListenerList();
    SystemStateChangeEvent stateChangeEvent = null;
    SystemStructureChangeEvent structureChangeEvent = null;

    public void addSystemStateChangeListener(SystemStateChangeListener l) {
        listenerList.add(SystemStateChangeListener.class, l);
    }

    public void removeSystemStateChangeListener(SystemStateChangeListener l) {
        listenerList.remove(SystemStateChangeListener.class, l);
    }

    public void addSystemStructureChangeListener(SystemStructureChangeListener l) {
        listenerList.add(SystemStructureChangeListener.class, l);
    }

    public void removeSystemStructureChangeListener(
            SystemStructureChangeListener l) {
        listenerList.remove(SystemStructureChangeListener.class, l);
    }

    // Notify all listeners that have registered interest for
    // notification on this event type. The event instance
    // is lazily created using the parameters passed into
    // the fire method.

    public void fireSystemStateChange() {
        this.saved = false;
        EventListener[] listeners = listenerList
                .getListeners(SystemStateChangeListener.class);
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = 0; i < listeners.length; i++) {
            if (stateChangeEvent == null)
                stateChangeEvent = new SystemStateChangeEvent(this);
            ((SystemStateChangeListener) listeners[i])
                    .systemStateChanged(stateChangeEvent);
        }

        if (getSystem() != null)
            getSystem().fireSystemStateChange();
    }

    public void fireSystemStructureChange() {
        this.saved = false;
        //
        // a structure change for a system implies a state change. but not the
        // other way around
        //
        fireSystemStateChange();
        EventListener[] listeners = listenerList
                .getListeners(SystemStructureChangeListener.class);
        // Process the listeners last to first, notifying
        // those that are interested in this event
        for (int i = 0; i < listeners.length; i++) {
            if (structureChangeEvent == null)
                structureChangeEvent = new SystemStructureChangeEvent(this);
            ((SystemStructureChangeListener) listeners[i])
                    .systemStructureChanged((SystemStructureChangeEvent) structureChangeEvent);
        }
        if (getSystem() != null)
            getSystem().fireSystemStructureChange();
    }

    /** Creates new SimpleSystem */
    public SimpleSystem() {
    }

    public void add(Entity entity) {
        if (!contains(entity.getName())) {
            entities.put(entity.getName(), entity);
            fireSystemStructureChange();
        } else {
            System.out.println("Error: " + entity.getName()
                    + " is already defined.");
        }
    }

    // Added 9/17/2001 by Lingchong You
    // add Entity e to the master map (entities) indexed by String name.
    // This method is defined to make it easier to add some entities to the
    // system while not showing them in the final system tree or map.
    //
    // The original purpose of this method is to allow me to add GeneticElements
    // to the master list using a long name (Genome's name + "." +
    // GeneticElemnt's name).
    // This way, I can later access such a GeneticElement using its long name
    // while
    // hiding the GeneticElement from the system tree. This certainly needs more
    // thought and probably more elegant design.
    //
    public void put(String name, Entity e) {
        entities.put(name, e);
    }

    public void addEntity(Entity e) {
        add(e);
    }

    // clone an Entity and insert into the system
    public void cloneEntity(Entity e) {
        Entity en = (Entity) (e.clone());
        addEntity(en);
    }

    public boolean contains(String name) {
        return entities.containsKey(name);
    }

    public Entity get(String name) {
        return (Entity) entities.get(name);
    }

    public void remove(String name) {
        entities.remove(name);
        System.out.println("Removing " + name);
        fireSystemStructureChange();
    }

    public void remove(Entity e) {
        entities.remove(e.getName());
    }

    public void rename(String oldName, String newName) {
        Entity e = get(oldName);
        if (!contains(newName.trim())) {
            entities.remove(oldName);
            entities.put(e.getName(), e);
            fireSystemStructureChange();
        } else {
            System.out
                    .println("Error: The system already has " + newName + ".");
        }
    }

    public boolean isEmpty() {
        return entities.isEmpty();
    }

    public Entity getEntity(String name) {
        return get(name);
    }

    public Substance getSubstance(String name) {
        return null;
    }

    public Parameter getParameter(String name) {
        return null;
    }

    /**
     * @deprecated
     */
    public boolean hasEntity(String name) {
        return contains(name);
    }

    /**
     * Getter for property saved.
     * 
     * @return Value of property saved.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Setter for property saved.
     * 
     * @param saved
     *            New value of property saved.
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    /**
     * Getter for property file.
     * 
     * @return Value of property file.
     */
    public File getFile() {
        return file;
    }

    /**
     * Setter for property file.
     * 
     * @param file
     *            New value of property file.
     */
    public void setFile(File file) {
        this.file = file;
    }

    public void save() {
        if (!isSaved()) {
            try {
                PrintWriter out = new PrintWriter(new FileOutputStream(file));
                out.println(this);
                out.close();
                setSaved(true);
            } catch (FileNotFoundException fnfe) {
                System.out.println(fnfe);
                // I should rethrow the exception!!! (L. You).
            }
        }
    }

    public void saveAs(File file) {
        this.file = file;
        setSaved(false);
        save();
    }

    public AbstractNode getNode() {
        return null;
    }

    // added 5/2/2005 by L. You
    // need to redefine to allow proper cloning.
    public Object clone() {
        return null;
    }
}
