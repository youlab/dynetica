/**
 * ApplicationFrame.java
 *
 *
 * Created: Sun Sep 03 10:56:38 2000
 *
 * @author Lingchong You
 * @version 0.1
 */
package dynetica.gui;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class ApplicationFrame extends JFrame {
    
    public ApplicationFrame() {
	this("ApplicationFrame");
    }
    public ApplicationFrame(String title) {
	super(title);
	buildUI();
//        center();
    }

    protected void buildUI() {
	addWindowListener (new WindowAdapter() {
		public void windowClosing(WindowEvent e) {
		    dispose();
		}
	    });
    }

    public void center() {
	Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
	Dimension frameSize = getSize();
	int x = (screenSize.width - frameSize.width) / 2;
	int y = (screenSize.height - frameSize.height) / 2;
	setLocation(x, y);
    }
} // ApplicationFrame
