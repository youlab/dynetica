/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.gui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.*;
/**
 *
 * @author lingchong 6/9/2016
 * This class is developed to include general functions for Dynetica GUI components.
 */
public class DyneticaGUITools {
    public DyneticaGUITools () {};
    
    public static void installContextMenu(Container comp) {  
    for (Component c : comp.getComponents()) {
        if (c instanceof JTextComponent) {
            c.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(final MouseEvent e) {
                    if (e.isPopupTrigger()) {
                        final JTextComponent component = (JTextComponent)e.getComponent();
                        final JPopupMenu menu = new JPopupMenu();
                        JMenuItem item;
                        item = new JMenuItem(new DefaultEditorKit.CopyAction());
                        item.setText("Copy");
                        item.setEnabled(component.getSelectionStart() != component.getSelectionEnd());
                        menu.add(item);
                        item = new JMenuItem(new DefaultEditorKit.CutAction());
                        item.setText("Cut");
                        item.setEnabled(component.isEditable() && component.getSelectionStart() != component.getSelectionEnd());
                        menu.add(item);
                        item = new JMenuItem(new DefaultEditorKit.PasteAction());
                        item.setText("Paste");
                        item.setEnabled(component.isEditable());
                        menu.add(item);
                        menu.show(e.getComponent(), e.getX(), e.getY());
                    }
                }
            });
        } else if (c instanceof Container)
            installContextMenu((Container) c);
    }
}
    
}
