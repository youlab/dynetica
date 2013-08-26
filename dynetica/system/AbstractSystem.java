/*
 * AbstractSystem.java
 *
 * Created on April 13, 2001, 10:37 AM
 */

package dynetica.system;

import dynetica.entity.*;

/**
 * AbstractSystem is anything that you can add somethings to it
 * and get things (using a String as the index) from it.
 * @author  Lingchong You
 * @version 0.1
 */
public interface AbstractSystem {
    
    public AbstractSystem getSystem();
    public void setSystem(AbstractSystem system);
    public String getName();
    public void setName(String name);
    
    public void add(Entity e);
    public Entity get(String name);
    public void fireSystemStateChange();
    public void fireSystemStructureChange();
    /**
     *@deprecated
     * use add(Entity e)
     */
    public void addEntity(Entity e);    
     /**
     *@deprecated
     * use get(Entity e)
     */
    public Entity getEntity(String name);
    
    public boolean contains(String name);
    /**
     *@deprecated
     * use contains(String name)
     */
    public boolean hasEntity(String name);
    public void remove(String name);
    public void remove(Entity e);
    
    /**
     * Change the indexing name of Entity (indexed with oldName) to a newName
     * (It's assumed that the actual name of the Entity has already been changed.)
     */
    
    public void rename(String oldName, String newName);
    public boolean isEmpty();
    public Substance getSubstance(String name);
    public Parameter getParameter(String name);
    public void setSaved(boolean saved);
    public boolean isSaved();

}

