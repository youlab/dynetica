/*
 * SystemBuilder.java
 *
 * Created on April 14, 2001
 */



package dynetica.system;
import dynetica.entity.*;
import dynetica.expression.*;
import dynetica.exception.*;
import dynetica.algorithm.*;
import dynetica.util.*;
import java.util.*;
import java.lang.reflect.*;
import java.io.*;
/**
 *
 * @author  Lingchong You
 * @version 1.5
 *
 * Modified by L. You on 06 Aug 2002 to make the builder more flexible so that it 
 * allows the user to write a propertyValue for an Entity on multiple lines.
 * 
 * The major modification involves the rewriting of the method getPropertiesString().
 */

 public class ModularSystemBuilder extends java.lang.Object implements SystemProperties {
 //
 // get the String enclosed by a "{" and its matching "}"
 //
 
     //Kanishk Asthana: this class is private in the class systembuilder because of which I had to
     //clone the whole class.
     public static String getPropertiesString (LineNumberReader reader) throws IOException{
     int numberOfLeft = 0;
     StringBuffer temp = new StringBuffer("");
     String line;
     int currentLIndex, currentRIndex;
     line = reader.readLine();
     for (;;) { 
         if (line != null) {
             if (line.trim().length() > 0  &&  line.trim().charAt(0)  !=  '#' ) {
                 currentLIndex = line.indexOf('{');
		     currentRIndex = line.indexOf('}');  
                     
                     //
                     // if this line doesn't contain either curly bracket
                     //
		     if (currentLIndex == -1 && currentRIndex == -1) {
                         if (numberOfLeft == 0) {
                                System.out.println("Shouldn'd be here!!!:");
                                break;
                         }
                         else {
				temp.append(line + "\n");
				line = reader.readLine();
                         }
		     } 
                     
                     //
                     // if this line contains '{' only
                     //
                     else if( currentLIndex != -1 && currentRIndex == -1) {
                         //
                         // if this is the first '{' found so far
                         //
				if(numberOfLeft == 0) {
					temp.append(line.substring(currentLIndex + 1) + "\n");
				}
                                //
                                // otherwise, continue on
                                //
                                else {
					temp.append(line + "\n");
				}
				line = reader.readLine();
				numberOfLeft++;
		     } 
                     
                     //
                     // if this line contains '}' only
                     //
                     else if( currentLIndex == -1 && currentRIndex != -1){
				numberOfLeft--;
				if(numberOfLeft == 0) {
                                    //
                                    // the matching '}' for the first '{' is found
                                    //
                                    temp.append(line.substring(0, currentRIndex)+ "\n");
				    break;
				}
                                else if(numberOfLeft > 0) {
					temp.append(line.substring(0,currentRIndex+1) + "\n");
					line = line.substring(currentRIndex+1);
				} 
                                
                                else {
                                    //
                                    // shouldn't be here
                                    //
					break;
				}
		     }
                     
                     //
                     // if this line contains both '{' & '}'
                     //
                     else {
                               if (numberOfLeft == 0) {
					temp.append(line.substring(currentLIndex + 1, currentRIndex));
                                        break;
                               }
                               else  {
                                     temp.append(line + "\n");
                                     line = reader.readLine();
                               }
		     }
             }
             else {
			line = reader.readLine();
         	}		 
         }
         else {
		temp.append(line + "\n");
		line = reader.readLine();
	   }
     }
    //System.out.println(temp);
     return temp.toString();

 }

 public static void set (SimpleSystem system, int offset, String properties) 
 throws InvalidPropertyValueException, UnknownPropertyException, IOException,
 IllegalAccessException, ClassNotFoundException, InstantiationException{

     System.out.println("Setting up System " + system.getName()); 
     LineNumberReader reader = new LineNumberReader(new StringReader(properties));
     String line;
 //    System.out.println(properties);
     try {
      for (;;)  {
              reader.mark(100000); 
              line = reader.readLine();
              if (line == null) break;
             if (line.trim().length() > 0 && line.trim().charAt(0) != '#') {
	        StringTokenizer stringToken = new
                StringTokenizer(line, " \t{");
                String className = stringToken.nextToken().trim();
                String entityName = stringToken.nextToken().trim();
                Entity e;
                
                if ( ! system.contains(entityName)) {
                    e = (Entity) (Class.forName(className).newInstance());
                    e.setName(entityName);
                    e.setSystem(system);
                }

                else {
                    e = (Entity) system.get(entityName);
                }
                
                //
                
                // if an entity is in the input file already, it is by default 
                // included in the output.
                //
                e.setToPrint(true);
               // System.out.println("Setting up " + e.getFullName());

                if (e instanceof AbstractModule) {
                    reader.reset();
                    AbstractModule mod= (AbstractModule)e;
                    mod.addEntitiesFromSuperSystem();
                    set((AbstractModule) e, reader.getLineNumber(), getPropertiesString(reader));
                }
                else {
                    reader.reset();
                    set(e, reader.getLineNumber(), getPropertiesString(reader)); 
                }
             }
         }
     }
     
     catch (IllegalAccessException iae) {
         int lineNumber = offset + reader.getLineNumber();
         throw new IllegalAccessException(iae.getMessage() + "at line #" + lineNumber);
     }
     
 }


 public static void set(Entity entity, int offset, String properties) 
 throws InvalidPropertyValueException, UnknownPropertyException, IOException {
//     System.out.println(properties);
     LineNumberReader reader = new LineNumberReader(new StringReader(properties));
     String line;
     try {
         for (;;) {
             reader.mark(1000000);
             line = reader.readLine();
             if (line == null) 
                 break;
             if (line.trim().length() > 0 && line.trim().charAt(0) != '#') {
                 StringTokenizer stringToken = new StringTokenizer(line, "\t{");
                 String propertyName = stringToken.nextToken().trim();
 //                System.out.println(propertyName);
                 reader.reset();
                 String propertyValue = getPropertiesString(reader).trim();
                 if ( (propertyName.length()  > 0) && (propertyValue.length() > 0) ) {
                     entity.setProperty(propertyName, propertyValue);
                 }
             }
         }
     }
    catch (InvalidPropertyValueException ipve) {
         int lineNumber = reader.getLineNumber() + offset;
         throw new InvalidPropertyValueException(ipve.getMessage() + " at line #" + lineNumber);
     }

     catch (UnknownPropertyException upe) {
         int lineNumber = reader.getLineNumber() + offset;
         throw new UnknownPropertyException(upe.getMessage() + " at line #" + lineNumber);
     } 
 }

/** Sets up a new system from the input file sysFile with a given super system.

 * or unknown property names are provided.

 * @throws InvalidPropertyValueException

 * This method can be used to build ReactiveSystem, GeneticSystem, and Genome.

 */
 public static AbstractSystem build (File systemFile, AbstractSystem superSystem) 
  throws FileNotFoundException, IOException, ClassNotFoundException, 

 InstantiationException, IllegalAccessException, 

 UnknownPropertyException, InvalidPropertyValueException {
        SimpleSystem system;
        LineNumberReader reader = new
        LineNumberReader(new FileReader(systemFile));
        String line;

        //
        // first find the name of the system
        // this requires that the first class that appear be a System
        //
        
        reader.mark(100000);
        line = reader.readLine();
        StringTokenizer stringToken = new   StringTokenizer(line, " \t{");
        String firstString = stringToken.nextToken();
        system = (SimpleSystem) (Class.forName(firstString).newInstance());
        system.setName(stringToken.nextToken().trim());
        system.setSystem(superSystem);
        reader.reset();
        set(system, 0, getPropertiesString(reader));
        if (system instanceof GeneticSystem)
           ( (GeneticSystem) system).buildGeneticInteractions();
        system.setFile(systemFile);
        system.setSaved(true);
        return system;
 }


 //
 // Added 5/14/2005 LY
 // Allow rebuilding of system from the input file if the latter is modified.
 //
public static void rebuild(ReactiveSystem system, File systemFile){
    try{
        LineNumberReader reader = new
            LineNumberReader(new FileReader(systemFile));
        String line;

        //
        // first find the name of the system
        // this requires that the first class that appear be a System
        //
        reader.mark(100000);
         line = reader.readLine();
        StringTokenizer stringToken = new   StringTokenizer(line, " \t{");
        String firstString = stringToken.nextToken(); // skip the string "dynetica.system.ReactiveSystem"
   //     system = (ReactiveSystem) (Class.forName(firstString).newInstance());
     //   system.clearEntities(); //remove all entities from system.
        system.setName(stringToken.nextToken().trim());
        reader.reset();
        set(system, 0, getPropertiesString(reader));
        if (system instanceof GeneticSystem)
           ( (GeneticSystem) system).buildGeneticInteractions();
        system.setFile(systemFile);
        system.setSaved(true);
    }
    catch (Exception e){
        e.printStackTrace();
    }
} 

public static void rebuild(ReactiveSystem system, String fileText){
    try{
        LineNumberReader reader = new
            LineNumberReader(new StringReader(fileText));
        String line;

        //
        // first find the name of the system
        // this requires that the first class that appear be a System
        //
        reader.mark(100000);
         line = reader.readLine();
        StringTokenizer stringToken = new   StringTokenizer(line, " \t{");
        String firstString = stringToken.nextToken(); // skip the string "dynetica.system.ReactiveSystem"
   //     system = (ReactiveSystem) (Class.forName(firstString).newInstance());
     //   system.clearEntities(); //remove all entities from system.
        system.setName(stringToken.nextToken().trim());
        reader.reset();
        set(system, 0, getPropertiesString(reader));
        if (system instanceof GeneticSystem)
           ( (GeneticSystem) system).buildGeneticInteractions();
//        system.setFile(systemFile);
//        system.setSaved(true);
    }
    catch (Exception e){
        e.printStackTrace();
    }
} 

 /** Sets up a new system from the input file sysFile.

 * This method is intended to replace the older method setup(File sysFile), which is

 * less flexible.

 * @param sysFile The input file that defines the reactive system

 * @throws UnknownPropertyException Throws this exception if a class fails to be initiated

 * or unknown property names are provided.

 * @throws InvalidPropertyValueException

 */

 

 public static AbstractSystem build (File systemFile) 
 throws FileNotFoundException, IOException, ClassNotFoundException, 
 InstantiationException, IllegalAccessException, 
 UnknownPropertyException, InvalidPropertyValueException {
    return build(systemFile, null);
 }
}