package dynetica.system;

import dynetica.gui.visualization.NetworkLayout;
import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.algorithm.*;
import dynetica.reaction.*;
import dynetica.gui.*;
import java.util.*;
import java.io.*;
import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

/**
 * 
 * @author Kanishk Asthana Created April 2013
 */

public class ModularSystem extends ReactiveSystem {

    protected dynetica.gui.systems.ModularSystemGraph graph;

    protected List modules = new ArrayList();

    protected DefaultMutableTreeNode moduleNode = null;

    protected Map moduleNodesMap = new HashMap();

    public ModularSystem(String name) {
        super(name);
    }

    public ModularSystem() {
        super();
    }

    public Map getModuleNodesMap() {
        return moduleNodesMap;
    }

    @Override
    public javax.swing.JPanel editor() {
        return new dynetica.gui.systems.ModularSystemEditor(this);
    }

    public List getModules() {
        return modules;
    }

    @Override
    public void add(Entity entity) {
        if (!contains(entity.getName())) {
            entities.put(entity.getName(), entity);
            // System.out.println("This is the entity name:"+entity.getName());

            if (entity instanceof Substance) {
                substances.add(entity);

                if (entity instanceof ExpressionVariable) {
                    expressions.add(entity);
                    // addExpressiontoModule(entity);
                }
            }

            else if (entity instanceof Progressive) {
                progressiveReactions.add(entity);
                // addProgressivetoModule(entity);
            }

            else if (entity instanceof Equilibrated) {
                equilibratedReactions.add(entity);
                // addEquilibratedtoModule(entity);
            }

            else if (entity instanceof Parameter) {
                parameters.add(entity);
            }

            else if (entity instanceof NetworkLayout) {
                networkLayout = (NetworkLayout) entity;
            } else if (entity instanceof Annotation) {
                systemInformation = (Annotation) entity;
            }

            else if (entity instanceof AbstractModule) {
                modules.add(entity);
            }
            try {
                addToModules(entity);
            } catch (Exception exp) {
                System.out.println("Addition of " + entity.getName()
                        + " to modules failed");
            }

            if (entity.getSystem().equals(this)) {
                try {
                    insertEntityIntoTreeModel(entity);
                } catch (Exception treeexp) {
                    treeexp.printStackTrace();
                }
            }
            fireSystemStructureChange();
        }
    }

    @Override
    public boolean insertEntityIntoTreeModel(Entity entity) {
        if (entity instanceof Substance) {
            //
            // the following if statement is added 4/19/05 by LY
            //
            if (entity instanceof ExpressionVariable) {
                if (expressionNode == null) {
                    expressionNode = new DefaultMutableTreeNode("Expressions");
                    treeModel.insertNodeInto(expressionNode, systemNode,
                            Math.min(systemNode.getChildCount(), 2));
                }
                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        expressionNode, expressionNode.getChildCount());
                return true;
            } else if (substanceNode == null) {
                substanceNode = new DefaultMutableTreeNode("Substances");
                treeModel.insertNodeInto(substanceNode, systemNode,
                        Math.min(systemNode.getChildCount(), 0));
            }
            treeModel.insertNodeInto(
                    new DefaultMutableTreeNode(entity.getName()),
                    substanceNode, substanceNode.getChildCount());
            return true;
        }
        if (entity instanceof Reaction) {
            if (reactionNode == null) {
                reactionNode = new DefaultMutableTreeNode("Reactions");
                treeModel.insertNodeInto(reactionNode, systemNode,
                        Math.min(systemNode.getChildCount(), 3));
            }

            if (entity instanceof Progressive) {
                if (progressiveNode == null) {
                    progressiveNode = new DefaultMutableTreeNode(
                            "Progressive Reactions");
                    treeModel.insertNodeInto(progressiveNode, reactionNode, 0);
                }
                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        progressiveNode, progressiveNode.getChildCount());
                return true;
            }

            if (entity instanceof Equilibrated) {
                if (equilibratedNode == null) {
                    equilibratedNode = new DefaultMutableTreeNode(
                            "Equilibrated Reactions");
                    treeModel.insertNodeInto(equilibratedNode, reactionNode,
                            Math.min(reactionNode.getChildCount(), 1));
                }

                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        equilibratedNode, equilibratedNode.getChildCount());
                return true;
            }
        }

        if (entity instanceof Parameter) {
            if (parameterNode == null) {
                parameterNode = new DefaultMutableTreeNode("Parameters");
                treeModel.insertNodeInto(parameterNode, systemNode,
                        Math.min(systemNode.getChildCount(), 1));
            }
            treeModel.insertNodeInto(
                    new DefaultMutableTreeNode(entity.getName()),
                    parameterNode, parameterNode.getChildCount());
            return true;
        }

        if (entity instanceof AbstractModule) {
            if (moduleNode == null) {
                moduleNode = new DefaultMutableTreeNode("Modules");
                treeModel.insertNodeInto(moduleNode, systemNode,
                        Math.min(systemNode.getChildCount(), 4));
            }
            DefaultMutableTreeNode modNode = new DefaultMutableTreeNode(
                    entity.getName());
            treeModel.insertNodeInto(modNode, moduleNode,
                    moduleNode.getChildCount());
            moduleNodesMap.put(entity, modNode);

            return true;

        } else
            return false;
    }

    public boolean removeEntityFromTreeModel(Entity entity) {
        Enumeration e;
        List mainNodes = new ArrayList();
        mainNodes.add(systemNode);
        if (entity instanceof Substance) {
            if (entity instanceof ExpressionVariable) {
                e = expressionNode.children();
                mainNodes.add(expressionNode);
            } else {
                e = substanceNode.children();
                mainNodes.add(substanceNode);
            }
        }

        else if (entity instanceof Progressive) {
            e = progressiveNode.children();
            mainNodes.add(progressiveNode);
        }

        else if (entity instanceof Equilibrated) {
            e = equilibratedNode.children();
            mainNodes.add(equilibratedNode);
        }

        else if (entity instanceof Parameter) {
            e = parameterNode.children();
            mainNodes.add(parameterNode);
        } else if (entity instanceof AbstractModule) {
            e = moduleNode.children();
            mainNodes.add(moduleNode);
        } else
            return false;

        DefaultMutableTreeNode node;
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            String nodeName = node.getUserObject().toString();
            if (nodeName.compareTo(entity.getName()) == 0
                    && mainNodes.contains(node.getParent())) {
                TreeNode[] pathToRoot = treeModel.getPathToRoot(node);
                //
                // remove the node first
                //
                treeModel.removeNodeFromParent(node);

                //
                // following the path to the root, remove all nodes that doesn't
                // contain
                // a child. Reset the major nodes to null if necessary.
                //
                for (int i = pathToRoot.length - 2; i > 0; i--) {
                    if (pathToRoot[i].getChildCount() == 0) {
                        treeModel
                                .removeNodeFromParent((DefaultMutableTreeNode) pathToRoot[i]);
                        if (substanceNode == pathToRoot[i])
                            substanceNode = null;
                        else if (expressionNode == pathToRoot[i])
                            expressionNode = null;
                        else if (reactionNode == pathToRoot[i])
                            reactionNode = null;
                        else if (progressiveNode == pathToRoot[i])
                            progressiveNode = null;
                        else if (equilibratedNode == pathToRoot[i])
                            equilibratedNode = null;
                        else if (parameterNode == pathToRoot[i])
                            parameterNode = null;
                        else if (moduleNode == pathToRoot[i])
                            moduleNode = null;
                    } else
                        return true;
                }
                return true;
            }
        }
        return false;
    }

    public void addToModules(Entity entity) {

        if (!(entity instanceof AbstractModule)
                && !(entity instanceof NetworkLayout)
                && !(entity instanceof Annotation) && !(modules.isEmpty())) {
            for (int i = 0; i < modules.size(); i++) {
                AbstractModule mod = ((AbstractModule) modules.get(i));
                mod.add(entity);
            }
        }

    }

    public void removeFromModules(String name) {
        for (int i = 0; i < modules.size(); i++) {
            AbstractModule mod = ((AbstractModule) modules.get(i));
            mod.remove(name);
        }
    }

    public void setGraph(dynetica.gui.systems.ModularSystemGraph graph) {
        this.graph = graph;
    }

    public dynetica.gui.systems.ModularSystemGraph getModularSystemGraph() {
        return this.graph;
    }

    public void remove(String name) {
        if (this.contains(name)) {
            Entity entity = (Entity) get(name);
            entities.remove(name);
            if (entity instanceof Substance) {
                substances.remove(entity);
                System.out.println("removing" + name);
                if (entity instanceof ExpressionVariable) {
                    expressions.remove(entity);
                }
            }

            else if (entity instanceof Progressive) {
                progressiveReactions.remove(entity);
            }

            else if (entity instanceof Equilibrated) {
                equilibratedReactions.remove(entity);
            }

            else if (entity instanceof AbstractModule) {
                modules.remove(entity);
                ((AbstractModule) entity).clearModule();
            } else {
                parameters.remove(entity);
            }
            // Kanishk Asthana 12 July 2013: This is temporary will be fixed
            // later
            if (entity.getSystem().equals(this))
                removeEntityFromTreeModel(entity);

            fireSystemStructureChange();

            removeFromModules(name);
        }
    }

    @Override
    public String toString() {
        StringBuilder str = new StringBuilder(getFullName() + " {" + NEWLINE);
        str.append(NEWLINE);
        str.append(systemInformation.getCompleteInfo());

        // Initializing modules
        for (int i = 0; i < modules.size(); i++) {

            AbstractModule module = (AbstractModule) modules.get(i);
            str.append(NEWLINE);
            str.append(module.getFullName() + " {" + NEWLINE);
            str.append(NEWLINE);
            str.append(module.systemInformation.getCompleteInfo());
            str.append(NEWLINE);
            str.append(module.networkLayout.getCompleteInfo()).append(NEWLINE);
            str.append("}").append(NEWLINE);

        }

        for (int i = 0; i < modules.size(); i++) {
            AbstractModule module = (AbstractModule) modules.get(i);
            str.append(NEWLINE);
            str.append(module.getFullName() + " {" + NEWLINE);

            for (int j = 0; j < module.getSubstances().size(); j++) {
                str.append(NEWLINE);
                Substance s = (Substance) module.getSubstances().get(j);
                if (!(s instanceof ExpressionVariable)
                        && s.getSystem().equals(module))
                    str.append(s.getCompleteInfo()).append(NEWLINE);
            }

            for (int j = 0; j < module.getParameters().size(); j++) {
                str.append(NEWLINE);
                Parameter p = (Parameter) module.getParameters().get(j);
                if (p.getSystem().equals(module))
                    str.append(p.getCompleteInfo()).append(NEWLINE);
            }

            str.append("}").append(NEWLINE);

        }

        str.append(NEWLINE);

        for (int i = 0; i < substances.size(); i++) {
            Substance s = ((Substance) substances.get(i));
            if (!(s instanceof ExpressionVariable)
                    && s.getSystem().equals(this)) {
                str.append(s.getCompleteInfo()).append(NEWLINE);
            }
        }

        str.append(NEWLINE);

        for (int i = 0; i < parameters.size(); i++) {
            Parameter p = ((Parameter) parameters.get(i));
            if (p.getSystem().equals(this))
                str.append(p.getCompleteInfo()).append(NEWLINE);
        }

        for (int i = 0; i < modules.size(); i++) {
            AbstractModule module = (AbstractModule) modules.get(i);
            str.append(NEWLINE);

            str.append(module.getFullName() + " {" + NEWLINE);

            for (int j = 0; j < module.getExpressions().size(); j++) {
                str.append(NEWLINE);
                ExpressionVariable ev = (ExpressionVariable) module
                        .getExpressions().get(j);
                if (ev.getSystem().equals(module))
                    str.append(ev.getCompleteInfo()).append(NEWLINE);
            }

            str.append("}").append(NEWLINE);

        }

        str.append(NEWLINE);
        
//        for (int i = 0; i < expressions.size(); i++) {
//            ExpressionVariable ev = ((ExpressionVariable) expressions.get(i));
//            if (ev.getSystem().equals(this)){
//                str.append(ev.getCompleteInfo()).append(NEWLINE);
//            }
//        }
        
        // Topological Sort added by Billy Wan March 2015
        Map<ExpressionVariable, List<ExpressionVariable>> evDependencies = 
                new HashMap<>();
        Map<ExpressionVariable, Integer> inDegree = new HashMap<>();
        
        for (int i = 0; i < expressions.size(); i++) {
            ExpressionVariable ev = ((ExpressionVariable) expressions.get(i));
            if (ev.getSystem().equals(this)){
                inDegree.put(ev, 0);
                // get list of substances that this ev depends on
                List evDep = ev.getSubstances();
                for (int j = 0; j < evDep.size(); j++){
                    Substance s = (Substance) evDep.get(j);
                    if (s instanceof ExpressionVariable){
                        ExpressionVariable sev = (ExpressionVariable) s;
                        if (!evDependencies.containsKey(sev)){
                            List<ExpressionVariable> dependencies = new 
                                    ArrayList<>();
                            dependencies.add(ev);
                            evDependencies.put(sev, dependencies);
                        }
                        else {
                            List<ExpressionVariable> dependencies =
                                    evDependencies.get(sev);
                            dependencies.add(ev);
                            evDependencies.put(sev, dependencies);
                        }
                    }
                }
            }
        }
        
        for (ExpressionVariable ev: evDependencies.keySet()){
            List<ExpressionVariable> dependencies = evDependencies.get(ev);
            for (ExpressionVariable dev:dependencies){
                if (!inDegree.containsKey(dev)){
                    Integer degree = 1;
                    inDegree.put(dev, degree);
                }
                else {
                    Integer degree = inDegree.get(dev);
                    degree += 1;
                    inDegree.put(dev, degree);
                }
            }
        }
        
        // Queue of expression variables with indegree 0
        Queue<ExpressionVariable> inDegree0 = new LinkedList<>();
        for (ExpressionVariable ev: inDegree.keySet()){
            if (inDegree.get(ev).equals(0)) inDegree0.add(ev);
        }
        while (!inDegree0.isEmpty()){
            ExpressionVariable ev = inDegree0.poll();
            str.append(ev.getCompleteInfo()).append(NEWLINE);
            if (evDependencies.containsKey(ev)){
                List<ExpressionVariable> dependencies = evDependencies.get(ev);
                for (ExpressionVariable dev:dependencies){
                    Integer degree = inDegree.get(dev);
                    degree += -1;
                    if (degree.equals(0)) inDegree0.add(dev);
                    inDegree.put(dev, degree);
                }
            }
        }

        str.append(NEWLINE);

        for (int i = 0; i < modules.size(); i++) {
            AbstractModule module = (AbstractModule) modules.get(i);
            str.append(NEWLINE);
            str.append(module.getFullName() + " {" + NEWLINE);

            for (int j = 0; j < module.getProgressiveReactions().size(); j++) {
                Reaction reaction = (Reaction) module.getProgressiveReactions()
                        .get(j);
                if (reaction.getSystem().equals(module))
                    str.append(reaction).append(NEWLINE);
            }

            for (int j = 0; j < module.getEquilibratedReactions().size(); j++) {
                Reaction reaction = (Reaction) module
                        .getEquilibratedReactions().get(j);
                if (reaction.getSystem().equals(module))
                    str.append(reaction).append(NEWLINE);
            }

            str.append("}").append(NEWLINE);
        }
        str.append(NEWLINE);

        for (int i = 0; i < progressiveReactions.size(); i++) {
            Reaction reaction = (Reaction) progressiveReactions.get(i);
            if (reaction.getSystem().equals(this))
                str.append(reaction).append(NEWLINE);
        }

        for (int i = 0; i < equilibratedReactions.size(); i++) {
            Reaction reaction = (Reaction) equilibratedReactions.get(i);
            if (reaction.getSystem().equals(this))
                str.append(reaction).append(NEWLINE);
        }

        str.append(networkLayout.getCompleteInfo()).append(NEWLINE);
        str.append("}").append(NEWLINE);

        return str.toString();

    }

    public void merge(File systemFile) throws FileNotFoundException,
            IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnknownPropertyException,
            InvalidPropertyValueException {

        ModularSystem tempSystem = new ModularSystem();

        ModuleMerger.rebuild(tempSystem, systemFile);

        System.out.println("Temporarily creating independent System!");

        java.util.List conflictedEntities = ModuleMerger.findConflicts(this,
                tempSystem);

        String warningMessage = "The System you are trying to import has "
                + conflictedEntities.size()
                + " conflicts with current ModularSystem";

        Object[] options = { "Rename All", "Rename", "Overwrite All" };
        Object[] options2 = { "Rename", "Overwrite" };
        if (conflictedEntities.size() > 0) {

            int answer = JOptionPane
                    .showOptionDialog(
                            null,
                            (warningMessage
                                    + "\nClick Rename All if you wish to rename every conflicted entity with the same suffix"
                                    + "\nClick Rename if you wish to handle each conflicted entity separately.\n" + "Click Overwrite All if you wish to overwrite all conflicted entities in the current ModularSystem."),
                            "Conflicts Detected",
                            JOptionPane.YES_NO_CANCEL_OPTION,
                            JOptionPane.WARNING_MESSAGE, null, options,
                            JOptionPane.YES_OPTION);

            if (answer == JOptionPane.YES_OPTION
                    || answer == JOptionPane.CLOSED_OPTION) {
                String suffix;

                do {

                    suffix = JOptionPane
                            .showInputDialog(
                                    null,
                                    "Please enter a suffix for all conflicted Entities\n"
                                            + "Ensure that there are no curly brackets or spaces in the suffix",
                                    "Enter Suffix",
                                    JOptionPane.QUESTION_MESSAGE);

                } while (suffix.isEmpty() || suffix.contains("{")
                        || suffix.contains("}") || suffix.contains(" "));

                for (int i = 0; i < conflictedEntities.size(); i++) {
                    Entity en = (Entity) conflictedEntities.get(i);
                    String originalName = en.getName();
                    StringBuilder newName = new StringBuilder(originalName);
                    newName.append(suffix);

                    if (this.contains(newName.toString())
                            || tempSystem.contains(newName.toString())) {
                        String suffix2 = suffix;
                        do {

                            suffix2 = JOptionPane
                                    .showInputDialog(
                                            null,
                                            "The current system or the new one already has an entity"
                                                    + " with the same name as: "
                                                    + ((new StringBuilder(
                                                            originalName))
                                                            .append(suffix2))
                                                            .toString()
                                                    + "\nPlease enter a new suffix for this entity."
                                                    + "\nMake sure there are no spaces or curly brackets in the suffix",
                                            "Problem!",
                                            JOptionPane.QUESTION_MESSAGE);

                        } while (suffix2.isEmpty()
                                || suffix2.contains("{")
                                || suffix2.contains("}")
                                || suffix2.contains(" ")
                                || this.contains(((new StringBuilder(
                                        originalName)).append(suffix2))
                                        .toString())
                                || tempSystem.contains(((new StringBuilder(
                                        originalName)).append(suffix2))
                                        .toString()));
                        newName.delete(0, newName.length());
                        newName.append(originalName).append(suffix2);
                    }
                    en.setName(newName.toString());

                    System.out.println("The new name of the Entity "
                            + originalName + " is: " + en.getName());
                }

            } else if (answer == JOptionPane.NO_OPTION) {
                for (int i = 0; i < conflictedEntities.size(); i++) {
                    Entity en = (Entity) conflictedEntities.get(i);

                    String tempEntityName = en.getName();
                    String tempClassName = en.getClass().getName();

                    String originalEntityName = tempEntityName;
                    String originalClassName = this.get(tempEntityName)
                            .getClass().getName();

                    if (originalClassName.equals(tempClassName)) {
                        String conflictedEntityType = originalClassName
                                .substring(originalClassName.lastIndexOf('.') + 1);
                        String newName = null;
                        String message1 = "This ModularSystem already contains a "
                                + conflictedEntityType
                                + " with the name: "
                                + originalEntityName;

                        String message2 = "\nPlease enter a new name for the imported "
                                + conflictedEntityType
                                + " : "
                                + originalEntityName
                                + "\n"
                                + "Make sure there are no spaces or curly brackets in the new name";

                        int response = JOptionPane
                                .showOptionDialog(
                                        null,
                                        (message1
                                                + "\nDo you wish to rename this entity"
                                                + "\nClick Rename if you want to rename." + " \nClick Overwrite if you want the current entity to be overwritten with this."),
                                        "Conflict Detected",
                                        JOptionPane.YES_NO_OPTION,
                                        JOptionPane.WARNING_MESSAGE, null,
                                        options2, JOptionPane.YES_OPTION);

                        if (response == JOptionPane.YES_OPTION
                                || response == JOptionPane.CLOSED_OPTION) {
                            do {

                                newName = JOptionPane.showInputDialog(null,
                                        message2, "Rename",
                                        JOptionPane.QUESTION_MESSAGE);

                            } while (newName.contains("{")
                                    || newName.contains("}")
                                    || newName.contains(" ")
                                    || newName.equals(originalEntityName)
                                    || this.contains(newName));

                            if (!newName.isEmpty())
                                tempEntityName = newName;
                        }
                        if (!originalEntityName.equals(tempEntityName))
                            tempSystem.get(originalEntityName).setName(
                                    tempEntityName);

                    }

                    else {
                        String conflictedEntityType = originalClassName
                                .substring(originalClassName.lastIndexOf('.') + 1);
                        String tempEntityType = tempClassName
                                .substring(tempClassName.lastIndexOf('.') + 1);
                        String newName = null;
                        String message1 = "This ModularSystem already contains a "
                                + conflictedEntityType
                                + " with the name: "
                                + originalEntityName;

                        String message2 = "\nPlease enter a new name for the imported "
                                + tempEntityType
                                + " : "
                                + originalEntityName
                                + "\n"
                                + "Make sure there are no spaces or curly brackets in the new name";
                        do {

                            newName = JOptionPane.showInputDialog(null,
                                    message1 + message2, "Rename",
                                    JOptionPane.QUESTION_MESSAGE);

                        } while (newName.contains("{") || newName.contains("}")
                                || newName.contains(" ")
                                || newName.equals(originalEntityName)
                                || this.contains(newName));

                        if (!newName.isEmpty())
                            tempEntityName = newName;

                        if (!originalEntityName.equals(tempEntityName))
                            tempSystem.get(originalEntityName).setName(
                                    tempEntityName);

                    }

                    System.out.println("New name is " + en.getName());
                }
            }
        }

        String systemName = this.getName();
        ModuleMerger.rebuild(this, tempSystem.toString());
        this.setName(systemName);
        this.setSaved(false);
        fireSystemStructureChange();
    }

    public void addModule(File systemFile) throws FileNotFoundException,
            IOException, ClassNotFoundException, InstantiationException,
            IllegalAccessException, UnknownPropertyException,
            InvalidPropertyValueException {
        SimpleSystem system;
        GeneralModule mod = new GeneralModule();
        ModularSystem tempSystem = new ModularSystem();
        LineNumberReader reader = new LineNumberReader(new FileReader(
                systemFile));
        String line;
        reader.mark(100000);
        line = reader.readLine();
        StringTokenizer stringToken = new StringTokenizer(line, " \t{");
        String firstString = stringToken.nextToken();
        system = (SimpleSystem) (Class.forName(firstString).newInstance());
        if (!((system instanceof ModularSystem) || (system instanceof GeneticSystem))) {
            String modName = stringToken.nextToken().trim();
            mod.setName(modName.toString());
            mod.setSystem(tempSystem);
            mod.addEntitiesFromSuperSystem();
            reader.reset();
            ModuleMerger.set((SimpleSystem) mod, reader.getLineNumber(),
                    ModuleMerger.getPropertiesString(reader));
            java.util.List conflictedEntities = ModuleMerger.findConflicts(
                    this, tempSystem);

            String warningMessage = "The System you are trying to import has "
                    + conflictedEntities.size()
                    + " conflicts with current ModularSystem";

            Object[] options = { "Rename All", "Rename", "Overwrite All" };
            Object[] options2 = { "Rename", "Overwrite" };

            if (conflictedEntities.size() > 0) {

                int answer = JOptionPane
                        .showOptionDialog(
                                null,
                                (warningMessage
                                        + "\nClick Rename All if you wish to rename every conflicted entity with the same suffix"
                                        + "\nClick Rename if you wish to handle each conflicted entity separately.\n" + "Click Overwrite All if you wish to overwrite all conflicted entities in the current ModularSystem."),
                                "Conflicts Detected",
                                JOptionPane.YES_NO_CANCEL_OPTION,
                                JOptionPane.WARNING_MESSAGE, null, options,
                                JOptionPane.YES_OPTION);

                if (answer == JOptionPane.YES_OPTION
                        || answer == JOptionPane.CLOSED_OPTION) {
                    String suffix;

                    do {

                        suffix = JOptionPane
                                .showInputDialog(
                                        null,
                                        "Please enter a suffix for all conflicted Entities\n"
                                                + "Ensure that there are no curly brackets or spaces in the suffix",
                                        "Enter Suffix",
                                        JOptionPane.QUESTION_MESSAGE);

                    } while (suffix.isEmpty() || suffix.contains("{")
                            || suffix.contains("}") || suffix.contains(" "));

                    for (int i = 0; i < conflictedEntities.size(); i++) {
                        Entity en = (Entity) conflictedEntities.get(i);
                        String originalName = en.getName();
                        StringBuilder newName = new StringBuilder(originalName);
                        newName.append(suffix);

                        if (this.contains(newName.toString())
                                || tempSystem.contains(newName.toString())) {
                            String suffix2 = suffix;
                            do {

                                suffix2 = JOptionPane
                                        .showInputDialog(
                                                null,
                                                "The current system or the new one already has an entity"
                                                        + " with the same name as: "
                                                        + ((new StringBuilder(
                                                                originalName))
                                                                .append(suffix2))
                                                                .toString()
                                                        + "\nPlease enter a new suffix for this entity."
                                                        + "\nMake sure there are no spaces or curly brackets in the suffix",
                                                "Problem!",
                                                JOptionPane.QUESTION_MESSAGE);

                            } while (suffix2.isEmpty()
                                    || suffix2.contains("{")
                                    || suffix2.contains("}")
                                    || suffix2.contains(" ")
                                    || this.contains(((new StringBuilder(
                                            originalName)).append(suffix2))
                                            .toString())
                                    || tempSystem.contains(((new StringBuilder(
                                            originalName)).append(suffix2))
                                            .toString()));
                            newName.delete(0, newName.length());
                            newName.append(originalName).append(suffix2);
                        }
                        en.setName(newName.toString());

                        System.out.println("The new name of the Entity "
                                + originalName + " is: " + en.getName());
                    }

                } else if (answer == JOptionPane.NO_OPTION) {
                    for (int i = 0; i < conflictedEntities.size(); i++) {
                        Entity en = (Entity) conflictedEntities.get(i);

                        String tempEntityName = en.getName();
                        String tempClassName = en.getClass().getName();

                        String originalEntityName = tempEntityName;
                        String originalClassName = this.get(tempEntityName)
                                .getClass().getName();

                        if (originalClassName.equals(tempClassName)) {
                            String conflictedEntityType = originalClassName
                                    .substring(originalClassName
                                            .lastIndexOf('.') + 1);
                            String newName = null;
                            String message1 = "This ModularSystem already contains a "
                                    + conflictedEntityType
                                    + " with the name: "
                                    + originalEntityName;

                            String message2 = "\nPlease enter a new name for the imported "
                                    + conflictedEntityType
                                    + " : "
                                    + originalEntityName
                                    + "\n"
                                    + "Make sure there are no spaces or curly brackets in the new name";

                            int response = JOptionPane
                                    .showOptionDialog(
                                            null,
                                            (message1
                                                    + "\nDo you wish to rename this entity"
                                                    + "\nClick Rename if you want to rename." + " \nClick Overwrite if you want the current entity to be overwritten with this."),
                                            "Conflict Detected",
                                            JOptionPane.YES_NO_OPTION,
                                            JOptionPane.WARNING_MESSAGE, null,
                                            options2, JOptionPane.YES_OPTION);

                            if (response == JOptionPane.YES_OPTION
                                    || response == JOptionPane.CLOSED_OPTION) {
                                do {

                                    newName = JOptionPane.showInputDialog(null,
                                            message2, "Rename",
                                            JOptionPane.QUESTION_MESSAGE);

                                } while (newName.contains("{")
                                        || newName.contains("}")
                                        || newName.contains(" ")
                                        || newName.equals(originalEntityName)
                                        || this.contains(newName));

                                if (!newName.isEmpty())
                                    tempEntityName = newName;
                            }
                            if (!originalEntityName.equals(tempEntityName))
                                tempSystem.get(originalEntityName).setName(
                                        tempEntityName);

                        }

                        else {
                            String conflictedEntityType = originalClassName
                                    .substring(originalClassName
                                            .lastIndexOf('.') + 1);
                            String tempEntityType = tempClassName
                                    .substring(tempClassName.lastIndexOf('.') + 1);
                            String newName = null;
                            String message1 = "This ModularSystem already contains a "
                                    + conflictedEntityType
                                    + " with the name: "
                                    + originalEntityName;

                            String message2 = "\nPlease enter a new name for the imported "
                                    + tempEntityType
                                    + " : "
                                    + originalEntityName
                                    + "\n"
                                    + "Make sure there are no spaces or curly brackets in the new name";
                            do {

                                newName = JOptionPane.showInputDialog(null,
                                        message1 + message2, "Rename",
                                        JOptionPane.QUESTION_MESSAGE);

                            } while (newName.contains("{")
                                    || newName.contains("}")
                                    || newName.contains(" ")
                                    || newName.equals(originalEntityName)
                                    || this.contains(newName));

                            if (!newName.isEmpty())
                                tempEntityName = newName;

                            if (!originalEntityName.equals(tempEntityName))
                                tempSystem.get(originalEntityName).setName(
                                        tempEntityName);

                        }

                        System.out.println("New name is " + en.getName());
                    }
                }
            }
            String tempLayout = networkLayout.getCompleteInfo();
            String systemName = this.getName();
            ModuleMerger.rebuild(this, tempSystem.toString());
            ModuleMerger.set(this, 0, tempLayout);
            this.setName(systemName);
            this.setSaved(false);
            fireSystemStructureChange();
        } else {
            JOptionPane.showMessageDialog(null, "System not compatible!",
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void rename(String oldName, String newName) {
        Entity en = get(oldName);
        if (!contains(newName.trim())) {
            entities.remove(oldName);
            entities.put(en.getName(), en);
            fireSystemStructureChange();
        } else {
            System.out
                    .println("Error: The system already has " + newName + ".");
        }

        Entity entity = get(newName);

        Enumeration e;
        if (entity instanceof Substance) {
            if (entity instanceof ExpressionVariable)
                e = expressionNode.children();
            else
                e = substanceNode.children();
        }

        else if (entity instanceof Progressive) {
            e = progressiveNode.children();
        }

        else if (entity instanceof Equilibrated) {
            e = equilibratedNode.children();
        }

        else if (entity instanceof AbstractModule) {
            e = moduleNode.children();
        } else {
            e = parameterNode.children();
        }

        DefaultMutableTreeNode node;
        while (e.hasMoreElements()) {
            node = (DefaultMutableTreeNode) e.nextElement();
            String nodeName = node.getUserObject().toString();
            if (nodeName.compareTo(oldName) == 0) {
                node.setUserObject(newName);
                treeModel.reload(node);
                break;
            }
        }
        setSaved(false);
    }

}
