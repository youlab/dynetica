/*
 * Dynetica.java
 *
 * Created on April 17, 2001, 11:37 PM
 * Modified by Kanishk Asthana on 12 June 2013 3:17 PM
 *
 * @author  Lingchong You
 * @version 1.2
 */

package dynetica;

public class Dynetica extends Object {

    /** Creates new Dynetica */
    public Dynetica() {
    }

    /**
     * @param args
     *            the command line arguments
     */
    public static void main(String args[]) {

        dynetica.util.DyneticaProperties.readProperities();

        try {
            javax.swing.UIManager
                    .setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
        } catch (Exception e) {
        }

        if (args.length > 0) {
            dynetica.util.DyneticaProperties.setProperty("workingDirectory",
                    args[0]);
        }
        // Modified by Kanishk Asthana to handle Modular Systems
        new dynetica.gui.systems.ModularSystemManager(
                dynetica.util.DyneticaProperties
                        .getProperty("workingDirectory")).setVisible(true);

    }
}