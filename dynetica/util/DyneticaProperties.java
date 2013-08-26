/*
 * DyneticaProperties.java
 *
 * Created on April 12, 2001, 10:14 PM
 * Revised
 *  4/18/2005, Lingchong You
 */

package dynetica.util;
import java.io.*;
/** This class is defined to store information about Dynetica itself. Useful
 * information include the directory it is in, and the mapping of the class names.
 *
 * @author Lingchong You
 * @version 2.0
 */


public class DyneticaProperties extends java.lang.Object {
    public final static File config = new java.io.File(System.getProperty("user.home") + 
        System.getProperty("file.separator") +  "dynetica.cfg");
    public static String HOME = System.getProperty("user.home");
    public static String VERSION = "2.0 beta";
    public static String CODENAME = "Resurrection";
    static java.util.Map  properties = new java.util.HashMap();
    static boolean initialized = false;

    
    /** Creates new DyneticaProperties */
    public DyneticaProperties() {
    }
    
    public static void setDefault() {
        setProperty("version", VERSION);
        setProperty("home", HOME);
        setProperty("workingDirectory", HOME);
        setProperty("outputDirectory", HOME);
        setProperty("codeName", CODENAME);
        setProperty("lookAndFeel", javax.swing.UIManager.getSystemLookAndFeelClassName());
        initialized = true;
    }
    
    public static String getClassName(String name) {
        return name;
        // apparently needs much work
    }
    
    public static void readProperities() {
        setDefault();
        try {
           java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader(config));
           
           String line = reader.readLine();
           for (;;) {
              if (line != null) {
                 if (line.trim().length() > 0 && line.trim().charAt(0) != '#') {
                     java.util.StringTokenizer tokenizer = new java.util.StringTokenizer(line, "=");
                     String propertyName = tokenizer.nextToken().trim();
                     String propertyValue = tokenizer.nextToken().trim();
                     setProperty(propertyName, propertyValue);
                }
                line = reader.readLine();
              }
              else 
                  break;
           }
           reader.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static java.util.Map getProperties() {
        if (! initialized) setDefault();
        return properties;
    }
   
    public static String getProperty(String propertyName) {
        if (! initialized) setDefault();
        return (String) ( properties.get(propertyName) );
    }
    
    public static void setProperty(String propertyName, String propertyValue) {
        properties.put(propertyName, propertyValue);
    }
    
    public static void saveProperties() {
       try {
       java.io.PrintWriter out = new java.io.PrintWriter(
                    new java.io.FileWriter(config));
       //out.flush();
       Object [] keys = properties.keySet().toArray();
       System.out.println(keys.length);
       for (int i = 0; i < keys.length; i++) {
           out.print(keys[i]);
           out.print(" = ");
           out.println(properties.get(keys[i]));
           System.out.println(keys[i] + " = " + properties.get(keys[i]));
       }
       out.close();
       }
       catch (Exception e) {}
   }
}
