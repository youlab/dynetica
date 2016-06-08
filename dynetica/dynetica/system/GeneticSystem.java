/**
 * GeneticSystem.java
 *
 *
 * Created: Wed Aug 23 23:40:23 2000
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.system;

import dynetica.entity.*;
import dynetica.reaction.*;
import java.util.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.algorithm.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
import javax.swing.tree.*;

/**
 * A GeneticSystem is a ReactiveSystem that contains at least one Genome.
 */

public class GeneticSystem extends ReactiveSystem {
    List rnas = new ArrayList();
    List proteins = new ArrayList();
    List RNAPs = new ArrayList();
    List transcriptions = new ArrayList();
    List translations = new ArrayList();
    List genomes = new ArrayList();
    GenomeTranslocation translocation = null;
    // there can be only one kind of ribosomes for now.
    Ribosome ribosome = null;
    Substance NTP = null;
    Substance aminoAcid = null;

    DefaultMutableTreeNode genomeNode = null;

    public GeneticSystem() {
        this("GeneticSystem");
    }

    public GeneticSystem(String name) {
        super(name);
        // every genetic system will have a ribosome
        new Ribosome("ribosome", this);
        //
        // by default, the aminoacid source of the system is "AminoAcid"
        // and the NTP source is "NTP"
        // by default, both sources are essentially infinitely large.
        //
    }

    public List getGenomes() {
        return genomes;
    }

    public void add(Entity entity) {
        if (!contains(entity.getName())) {
            entities.put(entity.getName(), entity);
            if (entity instanceof Substance) {
                substances.add(entity);
                if (entity instanceof RNA)
                    rnas.add(entity);
                if (entity instanceof Protein)
                    proteins.add(entity);
                if (entity instanceof RNAPolymerase)
                    RNAPs.add(entity);
                //
                // note that we have assumed there is only one ribosome object.
                //
                else if (entity instanceof Ribosome)
                    ribosome = (Ribosome) entity;
            }

            else if (entity instanceof Progressive) {
                progressiveReactions.add(entity);
                if (entity instanceof Transcription)
                    transcriptions.add(entity);
                else if (entity instanceof Translation)
                    translations.add(entity);
                else if (entity instanceof GenomeTranslocation)
                    translocation = (GenomeTranslocation) entity;
            }

            else if (entity instanceof Equilibrated) {
                equilibratedReactions.add(entity);
            } else if (entity instanceof dynetica.entity.Parameter) {
                dynetica.entity.Parameter name = (dynetica.entity.Parameter) entity;
                parameters.add(entity);
            }

            else if (entity instanceof Genome) {
                genomes.add(entity);
            }

            insertEntityIntoTreeModel(entity);
            fireSystemStructureChange();
        }
    }

    protected boolean insertEntityIntoTreeModel(Entity entity) {
        if (!super.insertEntityIntoTreeModel(entity)) {
            if (entity instanceof Genome) {
                if (genomeNode == null) {
                    genomeNode = new DefaultMutableTreeNode("Genomes");
                    treeModel.insertNodeInto(genomeNode, systemNode,
                            Math.min(systemNode.getChildCount(), 3));
                }
                treeModel.insertNodeInto(
                        new DefaultMutableTreeNode(entity.getName()),
                        genomeNode, genomeNode.getChildCount());
                return true;
            }
            //
            // the following shouldn't happen
            //
            return false;
        }
        return true;
    }

    public void setupGeneticInteractions() {
        for (int i = 0; i < transcriptions.size(); i++) {
            ((Transcription) transcriptions.get(i)).setup();
        }

        for (int i = 0; i < translations.size(); i++) {
            ((Translation) translations.get(i)).setup();
        }
    }

    public void buildGeneticInteractions() {
        for (int j = 0; j < genomes.size(); j++) {
            Genome genome = (Genome) genomes.get(j);
            for (int i = 0; i < genome.getGenes().size(); i++) {
                Gene g = (Gene) genome.getGenes().get(i);
                g.setProducts();
                g.setRibosome(ribosome);
                //
                // set up transcription, translation, and decay reactions.
                //
                if (g.getTranscription() == null) {
                    Reaction t1 = new Transcription(g, getNTP());
                    dynetica.entity.Parameter kdm = new dynetica.entity.Parameter("RNA_Decay_Constant", this);
                    kdm.setValue(2e-4);
                    Reaction d1 = new Decay("Decay_" + g.getRnaName(), this,
                            g.getRna(), kdm);
                }

                if (g.getGeneType() == Gene.mRNA_GENE
                        && g.getTranslation() == null) {
                    Reaction t2 = new Translation(g, getAminoAcid());
                    dynetica.entity.Parameter kdp = new dynetica.entity.Parameter("Protein_Decay_Constant",
                            this);
                    kdp.setValue(2e-5);
                    Reaction d2 = new Decay("Decay_" + g.getProteinName(),
                            this, g.getProtein(), kdp);
                }
            }
        }
        setupGeneticInteractions();
    }

    public Protein getProtein(String name) {
        return (Protein) get(name);
    }

    /**
     * Getter for property NTP.
     * 
     * @return Value of property NTP.
     */
    public Substance getNTP() {
        return getSubstance("NTP");
    }

    /**
     * Getter for property aminoAcid.
     * 
     * @return Value of property aminoAcid.
     */
    public Substance getAminoAcid() {
        return getSubstance("AminoAcid");
    }

    /**
     * Setter for property aminoAcid.
     * 
     * @param aminoAcid
     *            New value of property aminoAcid.
     */
    // public void setAminoAcid(Substance aminoAcid) {
    // this.aminoAcid = aminoAcid;
    // }

    public void remove(String name) {
        // ToDo:
        // need to check dependence before removing stuff
        // from a system
        //
        Entity entity = (Entity) get(name);
        System.out.println("Removing " + entity);
        entities.remove(name);
        if (entity instanceof Substance) {
            substances.remove(entity);
            if (entity instanceof Ribosome)
                ribosome = null;
            else if (entity instanceof RNAPolymerase)
                RNAPs.remove(entity);
            else if (entity instanceof Protein)
                proteins.remove(entity);
            else if (entity instanceof MessengerRNA)
                rnas.remove(entity);
        }

        else if (entity instanceof Progressive) {
            progressiveReactions.remove(entity);
            if (entity instanceof Transcription)
                transcriptions.remove(entity);
            else if (entity instanceof Translation)
                translations.remove(entity);
        }

        else if (entity instanceof Equilibrated) {
            equilibratedReactions.remove(entity);
        } else if (entity instanceof dynetica.entity.Parameter) {
            parameters.remove(entity);
        }

        else if (entity instanceof Genome) {
            genomes.remove(entity);
        }

        removeEntityFromTreeModel(entity);
        fireSystemStructureChange();
    }

    protected boolean removeEntityFromTreeModel(Entity entity) {
        if (!super.removeEntityFromTreeModel(entity)) {
            Enumeration e;
            if (entity instanceof Genome) {
                e = genomeNode.children();
                DefaultMutableTreeNode node;
                while (e.hasMoreElements()) {
                    node = (DefaultMutableTreeNode) e.nextElement();
                    String nodeName = node.getUserObject().toString();
                    if (nodeName.compareTo(entity.getName()) == 0) {
                        TreeNode[] pathToRoot = treeModel.getPathToRoot(node);
                        //
                        // remove the node first
                        //
                        treeModel.removeNodeFromParent(node);

                        //
                        // following the path to the root, remove all nodes that
                        // doesn't contain
                        // a child.
                        //

                        for (int i = pathToRoot.length - 2; i > 0; i--) {
                            if (pathToRoot[i].getChildCount() == 0) {
                                treeModel
                                        .removeNodeFromParent((DefaultMutableTreeNode) pathToRoot[i]);
                                genomeNode = null;
                            } else
                                return true;
                        }
                        return true;
                    }

                }
            }
            return false;
        }
        return true;
    }

    public void rename(String oldName, String newName) {
        Entity entity = get(oldName);
        super.rename(oldName, newName);
        if (entity instanceof Genome) {
            Enumeration e = genomeNode.children();
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
        }

    }

    /**
     * Getter for property ribosome.
     * 
     * @return Value of property ribosome.
     */
    public Ribosome getRibosome() {
        return ribosome;
    }

    /**
     * Setter for property ribosome.
     * 
     * @param ribosome
     *            New value of property ribosome.
     */
    public void setRibosome(Ribosome ribosome) {
        this.ribosome = ribosome;
    }

    /**
     * Converts the GeneticSystem into a String for output. the format of this
     * output should be compatible with the input file of the system
     */
    public String toString() {
        StringBuffer str = new StringBuffer(getFullName() + " {" + NEWLINE);
        str.append(NEWLINE);
        for (int i = 0; i < substances.size(); i++) {
            Substance s = ((Substance) substances.get(i));
            // if (s.isVisible())
            str.append(s.getCompleteInfo() + NEWLINE);
        }

        str.append(NEWLINE);
        for (int i = 0; i < parameters.size(); i++) {
            dynetica.entity.Parameter p = ((dynetica.entity.Parameter) parameters.get(i));
            // if (p.isVisible())
            str.append(p.getCompleteInfo() + NEWLINE);
        }

        str.append(NEWLINE);
        for (int i = 0; i < genomes.size(); i++)
            str.append(((Genome) (genomes.get(i))).getFullGenome() + NEWLINE);

        str.append(NEWLINE);
        for (int i = 0; i < progressiveReactions.size(); i++) {
            Object o = progressiveReactions.get(i);
            // if (((Entity) o).isVisible())
            str.append(progressiveReactions.get(i) + NEWLINE);
        }

        str.append(NEWLINE);
        for (int i = 0; i < equilibratedReactions.size(); i++)
            str.append(equilibratedReactions.get(i) + NEWLINE);

        str.append("}" + NEWLINE);
        return str.toString();
    }

    public void updateSpecialReactions(double dt) {
        updateEquilibratedReactions();
        if (translocation != null)
            translocation.update(dt);
    }

} // GeneticSystem
