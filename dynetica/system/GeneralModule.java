/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.system;

import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.algorithm.*;
import dynetica.reaction.*;
import dynetica.gui.*;
import java.util.*;
import java.io.*;
import javax.swing.*;

/**
 * 
 * @author Kanishk Asthana
 * 
 *         Created 13 June 2013 1:06PM
 */
public class GeneralModule extends AbstractModule {

    private static int GeneralModuleIndex = 0;

    public GeneralModule() {
        super("GeneralModule" + GeneralModuleIndex++);
    }

}