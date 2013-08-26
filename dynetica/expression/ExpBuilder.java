//=================================================================

// ExpBuilder.java

// Last updated: Mon Aug 7, 1:34 2001

//	details

//	   -- Add comments

// @author Apirak Hoonlor

// @version 0.1

//=================================================================



package dynetica.expression;

import dynetica.system.*;

import dynetica.util.*;

import java.util.*;

import java.lang.*;

import java.io.*;



//=================================================================

// ExpBuilder will create the expression for the custom function 

// base on the data provided in the input file ,"function.dat".

//=================================================================





public class ExpBuilder {



    // Data member 

    // inputFunc -- the input file which contains custom functions' data

    // expBuilderMap -- it keeps all the custom functions' data

    // instance -- the current data of all custom functions

    // functionType -- the precedence of all custom functions.

    // type -- type of function.

    

    private static String inputFunc = new String();

    private static StringBuffer fileHeader = new StringBuffer(DyneticaProperties.getProperty("home"));

    protected static Map expMap = new HashMap();

    protected static ExpBuilder instance = new ExpBuilder();

    protected static List funcName; 

    protected static String funcLocation;

    public static int functionType = 5;

    public static int type = 60;

    

    protected ExpBuilder(){

	try{

	    getFuncDat();

	} 
        
        catch( FileNotFoundException err1){

	    System.out.println("Warning: function.d not found");

	} 
        
        catch( IOException err2){

	    System.out.println("Warning: Incorrect from of input at ExpBuilder.");

	} 
        
        catch(IllegalExpressionException err3){

	    System.out.println("Warning: Incorrect term of custom expression.");

	}

    }

    

    // getDataSet

    // getDataSet will retrieved all custom functions' data from 

    // inputFunc. It will put all the data into expBuilderMap.

    // Parameters: None

    // Return: None

    // Precondition: None

    // Postcondition: None

    

    public static void getDataSet(String func_name) throws  FileNotFoundException, IOException,

							  IllegalExpressionException{ 

	BufferedReader stdin = new BufferedReader(new FileReader(func_name)); 

	StringTokenizer data, expr, opList;

	String tempName, tempExp; 

	String name, tempOpList;

	String testName, tempDat;

	String endFunc;

	StringBuffer tExp = new StringBuffer();

	String[] operand_list;

	int keyType, length;

	List funcDat = new LinkedList();

	do{

	    tempName = stdin.readLine(); 

	    if(tempName != null && tempName.length() > 1){

		data = new StringTokenizer(tempName, "(){");

		name = (data.nextToken()).trim();

		tempOpList = (data.nextToken()).trim();

		opList = new StringTokenizer(tempOpList, ",");

		length = opList.countTokens();

		funcDat.add(new Integer(length));

		operand_list = new String[length];

		for(int i = 0; i< length; i++){

		    operand_list[i] = (opList.nextToken()).trim();

		}

		funcDat.add(operand_list);

		tempExp  = stdin.readLine().trim();

		tExp.append(" ");

		tExp.append(tempExp);

		endFunc = (stdin.readLine()).trim();

		while((endFunc != null)&& (!endFunc.equalsIgnoreCase("}//end"))){

		    tExp.append(" ");

		    tExp.append(endFunc);

		    endFunc = (stdin.readLine()).trim();

		}

		if(!endFunc.equalsIgnoreCase("}//end")){

		    throw new IllegalExpressionException( "} is missing at " + name +

							  " of the function in function.d .");

		}

		funcDat.add(tExp.toString());

		Object temp = expMap.remove(name);    

		expMap.put(name, funcDat);

	    }

	}while(tempName != null);

	stdin.close();

    }

    public static void getFuncDat() throws FileNotFoundException, IOException,

					   IllegalExpressionException{

	fileHeader.append("/input/CustomFunc/");

	String file_header = fileHeader.toString();

	StringBuffer tempHeader = new StringBuffer(file_header);

	tempHeader.append("function.d");

	inputFunc = tempHeader.toString();

	BufferedReader stdin = new BufferedReader(new FileReader(inputFunc));

	String line, name;

	funcName = new LinkedList();

	do{

	    line = stdin.readLine();

	    if(line != null && line.length() > 1){

		name = line.trim();

		funcName.add(name);

	    }

	} while(line != null);

	stdin.close();

    }

    

    public static boolean isCustomFunc(String name){

	StringBuffer tempName = new StringBuffer(name);

	tempName.append(".d");

	String realName = tempName.toString();

	int index = -1;
        
        if (funcName!=null) index = funcName.indexOf(realName);

	if(index == -1)

	    return false;

	return true;

    }

    

    public static int getType(String name){

	return type;

    }

    

    // getLength

    // It will return # of operands in the given custom function

    

    public static int getLength(String name)throws FileNotFoundException, IOException,

						   IllegalExpressionException{

	List temp = new LinkedList();

	if(expMap.containsKey(name)){

	    temp = (List) expMap.get(name);

	}else {

	    StringBuffer tempName = new StringBuffer(fileHeader.toString());

	    tempName.append(name);

	    tempName.append(".d");

	    getDataSet(tempName.toString());

	    temp = (List) expMap.get(name);

	}

	Integer tempInt = (Integer) temp.get(0);

	return tempInt.intValue();

    }

    

    public static int functionType(){

	return functionType;

    }

    

    // getIndex

    // It will find out where tempTarget should locate in the real

    // operands' list of the current custom function

    

    public int getIndex(String[] list, GeneralExpression tempTarget){

	String target = (tempTarget.toString()).trim();

	int i = 0;

	for(i = 0; i < list.length; i++){

	    if(target.equalsIgnoreCase(list[i]))

		return i;

	}

	return -1;

    }

    

    public void upDateDat(String name)throws FileNotFoundException, IOException,

					     IllegalExpressionException{

	StringBuffer tempName = new StringBuffer(fileHeader.toString());

	tempName.append(name);

	tempName.append(".d");

	getDataSet(tempName.toString());

    }

    // setParameters return true if there exist fName & data is successfully 
    // updated, false otherwise.

    public static boolean setParameters(String fName, String parameters, String exp){
	StringTokenizer para = new StringTokenizer(parameters, "(), ");
	String[] tempPar = new String[para.countTokens()];
	int index = 0;
	while(para.hasMoreTokens()){
		tempPar[index] = (para.nextToken()).trim();
		index++;
	}
	List tempList = new LinkedList();
	if(!(expMap.containsKey(fName)))
		return false;
	tempList = (List) expMap.get(fName);
	Integer temp = new Integer(tempPar.length);
	tempList.set(0, temp);
	tempList.set(1, tempPar);
	tempList.set(2, exp);
	return true;
    }

/*	ExpBuilder.saveDat(fName, parameters, exp)
    public static boolean saveDat(String fName, String parameters, String exp) 
		throws IOException, FileNotFoundException{
	fName <==> the function name that we want to update
	parameters <==> the string in the form "a,b,c,d" or "(a,b,c,d)" where a,b,c, and d are the parameters of the new
				expression.
	exp <==> the expression string that we want to update.
Precondition the parameter appears in exp if and only if it appear in parameters.  
Postcondition if fName does not exit, the false value is return. Otherwise, the task is completed */

    public static boolean saveDat(String fName, String parameters, String exp) 
		throws IOException, FileNotFoundException{
	StringTokenizer para = new StringTokenizer(parameters, "(), ");
	String[] tempPar = new String[para.countTokens()];
	int index = 0;
	while(para.hasMoreTokens()){
		tempPar[index] = (para.nextToken()).trim();
		index++;
	}
	if(!(setParameters(fName, parameters, exp))){
		return false;
	} else {
	   StringBuffer tempName = new StringBuffer(fileHeader.toString());
	   tempName.append(fName);
	   tempName.append(".d");
	   StringBuffer tempOutput = new StringBuffer();
 	   tempOutput.append(fName);
	   tempOutput.append("(");
	   tempOutput.append(tempPar[0]);
	   for(int i = 1; i < tempPar.length; i++){
		tempOutput.append(", ");
		tempOutput.append(tempPar[i]);
	   }
	   tempOutput.append(") {\n");
	   tempOutput.append(exp);
	   tempOutput.append("\n}//end");
	PrintWriter fileOut = new PrintWriter( new BufferedWriter(new FileWriter(tempName.toString())));
	   fileOut.write(tempOutput.toString());
	   fileOut.close();
	}
	return true;
    }


    public static String[] getParameters(String fName){
	List tempList = new LinkedList();
	if(!(expMap.containsKey(fName)))
		return null;
	tempList = (List) expMap.get(fName);
	return (String[]) tempList.get(1);
    }

    public static String getExpression(String fName){
	List tempList = new LinkedList();
	if(!(expMap.containsKey(fName)))
		return null;
	tempList = (List) expMap.get(fName);
	return (String) tempList.get(2);

    }

    public static List getFuncName(){
	return funcName;
    }

/*	 ExpBuilder.createFunction(fName, parameters, exp)
    public static void createFunction(String fName, String parameters, String exp)
	throws IOException, FileNotFoundException{
	fName <==> the function name that we want to update
	parameters <==> the string in the form "a,b,c,d" or "(a,b,c,d)" where a,b,c, and d are the parameters of the new
				expression.
	exp <==> the expression string that we want to update.
Precondition the parameter appears in exp if and only if it appear in parameters.  
Postcondition if fName does not exit, the false value is return. Otherwise, the task is completed
			Current List of fileName has also been updated and saved. */

    public static void createFunction(String fName, String parameters, String exp)
	throws IOException, FileNotFoundException{
	StringTokenizer para = new StringTokenizer(parameters, "(), ");
	String[] tempPar = new String[para.countTokens()];
	int index = 0;
	while(para.hasMoreTokens()){
		tempPar[index] = (para.nextToken()).trim();
		index++;
	}
	List tempList = new LinkedList();
	Integer parLength = new Integer(tempPar.length);
	tempList.add(parLength);
	tempList.add(tempPar);
	tempList.add(exp);
	expMap.put(fName, tempList);
	boolean result = saveDat(fName, parameters, exp);
	StringBuffer tempName = new StringBuffer(fName);
	tempName.append(".d");
	funcName.add(tempName.toString());
	saveFunc();
	if(!result)
		System.out.println("Unable to create function call " + fName);
    }

    public static void saveFunc()throws IOException, FileNotFoundException{
	String file_header = fileHeader.toString();
	StringBuffer tempHeader = new StringBuffer(file_header);
	tempHeader.append("function.d");
	PrintWriter fileOut = new PrintWriter( new BufferedWriter(new FileWriter(tempHeader.toString())));
	fileOut.write((String) funcName.get(0));	
	for(int i = 1; i < funcName.size(); i++){
		String name = (String) funcName.get(i);
		fileOut.write("\n");
		fileOut.write(name);
	}
	fileOut.close();
    }

    // mapVariables

    // It will create the GeneralExpression of the given customfunction 

    // by mapping the real operands to their place based on the real 

    // expression. The real expression is created based on the data in 

    // "function.dat".



    protected GeneralExpression mapVariables(String func_name, GeneralExpression[] realOp)

	throws IllegalExpressionException{

	CustomParser tempParser = new CustomParser();

	AbstractSystem tempSys = new ReactiveSystem();

	List funcDat = (List) expMap.get(func_name);

	String tempExpr = (String) funcDat.get(2);

	String[] tempOpList = (String[]) funcDat.get(1);

	GeneralExpression exp = tempParser.parse(tempSys,tempExpr);

	GeneralExpression result = mapVar(exp, realOp, tempOpList);

	return result;

	}

    

    // mapVar 

    // mapVar will recursively replace all the temporaly operands by the real operands.

    

    private GeneralExpression mapVar (GeneralExpression expr, GeneralExpression[] realOp, String[] opList){

	if(DMath.isParaConstSub(expr)){

	    return realOp[getIndex(opList, expr)];

	} else if(expr instanceof Expression){

	    if(((Expression) expr).isUnary()){

		GeneralExpression tempA = ((Expression) expr).getA();

		if( DMath.isParaConstSub(tempA)){

		    ((Expression) expr).setA(realOp[getIndex(opList, tempA)]);

		} else {

		    ((Expression) expr).setA(mapVar(tempA, realOp, opList));

		}

	    }else {

		GeneralExpression tempA = ((Expression) expr).getA();

		GeneralExpression tempB = ((Expression) expr).getB();

		if( DMath.isParaConstSub(tempA) && DMath.isParaConstSub(tempB)){

		    ((Expression) expr).setA(realOp[getIndex(opList, tempA)]);

		    ((Expression) expr).setB(realOp[getIndex(opList, tempB)]);

		} else if( DMath.isParaConstSub(tempA) && (!DMath.isParaConstSub(tempB))){

		    ((Expression) expr).setA(realOp[getIndex(opList, tempA)]);

		    ((Expression) expr).setB(mapVar(tempB, realOp, opList));

		} else if( (!DMath.isParaConstSub(tempA)) && DMath.isParaConstSub(tempB)){

		    ((Expression) expr).setA(mapVar(tempA, realOp, opList));

		    ((Expression) expr).setB(realOp[getIndex(opList, tempB)]);

		} else {

		    ((Expression) expr).setA(mapVar(tempA, realOp, opList));

		    ((Expression) expr).setB(mapVar(tempB, realOp, opList));

		}

	    }

	    return expr;

	} else if(expr instanceof FunctionExpression){

	    int i = 0;

	    int length = ((FunctionExpression) expr).getLength();

	    for(i = 0; i < length; i++){

		GeneralExpression temp = ((FunctionExpression) expr).getVariables(i);

		if(DMath.isParaConstSub(temp))

		    ((FunctionExpression) expr).setA(realOp[getIndex(opList, temp)], i);

		else

		    ((FunctionExpression) expr).setA(mapVar(temp, realOp, opList), i);

	    }

	    return expr;

	}

	return expr;

    }

    

    // creatExp

    // It will create the given custom function with the given operands.	

    

    public GeneralExpression creatExp(String func_name, GeneralExpression[] realOp)

	throws FileNotFoundException, IOException, IllegalExpressionException{

	if(!isCustomFunc(func_name))

	    throw new IllegalExpressionException("Not found function name: " + func_name);

	else {

	    int varSize = getLength(func_name);

	    if(realOp.length != varSize){ 

		throw new IllegalExpressionException("Incorrect parameters for: " + func_name);

	    }else {

		GeneralExpression result = mapVariables(func_name, realOp); 

		return result;

	    }

	}

	}

    

    public static ExpBuilder getInstance(){ return instance;}

}

