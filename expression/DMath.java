/**

 * DMath.java.java

 *

 *

 * Created: Mon July 9 22:30:00 2001

 * LastUpdated: Tues 18 15:00:00 2001

 * @author Apirak Hoonlor

 * @version 0.2

 */

package dynetica.expression;

import dynetica.entity.*;

import dynetica.system.*;

import java.util.*;

/**
 * 
 * DMath contains some of the static methods which will build expressions.
 * 
 * It also supports buildBinary and buildUnary of ExpressionBuilder by
 * 
 * providing static method to build all the expression in both binary and
 * 
 * unary form.
 */

// last update

// Adjust the simplification on Min, Max function so that if all the parameters
// are

// constants only constants will be return.

public class DMath {

    public static GeneralExpression sum(GeneralExpression a, GeneralExpression b) {

        if (a instanceof Constant && b instanceof Constant)

            return new Constant(a.getValue() + b.getValue());

        else

            return new Sum(a, b);

    }

    public static GeneralExpression multiply(GeneralExpression a,
            GeneralExpression b) {

        if (a instanceof Constant && b instanceof Constant)

            return new Constant(a.getValue() * b.getValue());

        else

            return new Multiply(a, b);

    }

    public static GeneralExpression substract(GeneralExpression a,
            GeneralExpression b) {

        if (a instanceof Constant && b instanceof Constant)

            return new Constant(a.getValue() - b.getValue());

        else

            return new Substract(a, b);

    }

    public static GeneralExpression divide(GeneralExpression a,
            GeneralExpression b) {

        if (a instanceof Constant && b instanceof Constant)

            return new Constant(a.getValue() / b.getValue());

        else

            return new Divide(a, b);

    }

    public static GeneralExpression pow(GeneralExpression a, GeneralExpression b) {

        if (a instanceof Constant && b instanceof Constant)

            return new Constant(Math.pow(a.getValue(), b.getValue()));

        else

            return new Pow(a, b);

    }

    public static GeneralExpression max(GeneralExpression a, GeneralExpression b) {

        if (a instanceof Constant && b instanceof Constant)

            return new Constant(Math.max(a.getValue(), b.getValue()));

        else

            return new Max(a, b);

    }

    public static GeneralExpression min(GeneralExpression a, GeneralExpression b) {

        if (a instanceof Constant && b instanceof Constant)

            return new Constant(Math.min(a.getValue(), b.getValue()));

        else

            return new Min(a, b);

    }

    public static GeneralExpression log(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.log(a.getValue()));

        else

            return new Log(a);

    }

    public static GeneralExpression exp(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.exp(a.getValue()));

        else

            return new Exp(a);

    }

    public static GeneralExpression sqrt(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.sqrt(a.getValue()));

        else

            return new Sqrt(a);

    }

    public static GeneralExpression sin(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.sin(a.getValue()));

        else

            return new Sin(a);

    }

    //
    // added by Lingchong 4/30/2005
    public static GeneralExpression abs(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.abs(a.getValue()));

        else

            return new Abs(a);

    }

    public static GeneralExpression cos(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.cos(a.getValue()));

        else

            return new Cos(a);

    }

    //
    // added by LY. August 2013
    //
    public static GeneralExpression round(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.round(a.getValue()));

        else

            return new Round(a);

    }

    //
    // added by LY. August 2013
    //
    public static GeneralExpression floor(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.floor(a.getValue()));

        else

            return new Floor(a);

    }

    //
    // added by LY. August 2013
    //
    public static GeneralExpression ceil(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.ceil(a.getValue()));

        else

            return new Ceil(a);

    }

    public static GeneralExpression tan(GeneralExpression a) {

        if (a instanceof Constant)

            return new Constant(Math.tan(a.getValue()));

        else

            return new Tan(a);

    }

    public static GeneralExpression compare(GeneralExpression x,
            GeneralExpression y) {

        if ((x instanceof Constant) && (y instanceof Constant)) {

            double xx = x.getValue();

            double yy = y.getValue();

            if (xx > yy)
                return new Constant(1.0);

            else if (xx < yy)
                return new Constant(-1.0);

            else
                return new Constant(0.0);

        } else {

            GeneralExpression[] op = new GeneralExpression[2];

            op[0] = x;

            op[1] = y;

            return new Compare(op);

        }

    }

    public static GeneralExpression step(GeneralExpression x,
            GeneralExpression y) {

        if ((x instanceof Constant) && (y instanceof Constant)) {

            if (x.getValue() > y.getValue())
                return new Constant(1.0);

            else
                return new Constant(0.0);

        } else {

            return new Step(x, y);

        }

    }

    public static GeneralExpression sum(GeneralExpression[] data) {

        double total = 0.0;

        int i, j = 0;

        GeneralExpression temp = null;

        while ((j < data.length) && (data[j] instanceof Constant)) {

            total = total + data[j].getValue();

            j++;

        }

        if (j < data.length) {

            temp = data[j];

            j++;

        } else {

            return new Constant(total);

        }

        for (i = j; i < data.length; i++) {

            if (data[i] instanceof Constant)

                total = total + data[i].getValue();

            else

                temp = new Sum(temp, data[i]);

        }

        if (total != 0.0)

            return new Sum(temp, new Constant(total));

        else

            return temp;

    }

    public static GeneralExpression multiply(GeneralExpression[] data) {

        double total = 1.0;

        int i, j = 0;

        GeneralExpression temp = null;

        while ((j < data.length) && (data[j] instanceof Constant)) {

            total = total * data[j].getValue();

            j++;

        }

        if (j < data.length) {

            temp = data[j];

            j++;

        } else {

            return new Constant(total);

        }

        for (i = j; i < data.length; i++) {

            if (data[i] instanceof Constant)

                total = total * data[i].getValue();

            else

                temp = new Multiply(temp, data[i]);

        }

        if (total != 1.0)

            return new Multiply(temp, new Constant(total));

        else

            return temp;

    }

    public static GeneralExpression min(GeneralExpression[] data) {

        double total = 0.0;

        int i = 0;

        boolean isFirstConst = false;

        List tempList = new LinkedList();

        for (i = 0; i < data.length; i++) {

            if (data[i] instanceof Constant) {

                if (!isFirstConst) {

                    total = data[i].getValue();

                    isFirstConst = true;

                } else {

                    if (data[i].getValue() < total)

                        total = data[i].getValue();

                }

            } else {

                tempList.add(data[i]);

            }

        }

        Constant temp;

        if (isFirstConst) {

            temp = new Constant(total);

            tempList.add(temp);

        }

        return new MinL(linkToArray(tempList));

    }

    public static GeneralExpression max(GeneralExpression[] data) {

        double total = 0.0;

        int i = 0;

        boolean isFirstConst = false;

        List tempList = new LinkedList();

        for (i = 0; i < data.length; i++) {

            if (data[i] instanceof Constant) {

                if (!isFirstConst) {

                    total = data[i].getValue();

                    isFirstConst = true;

                } else {

                    if (data[i].getValue() > total)

                        total = data[i].getValue();

                }

            } else {

                tempList.add(data[i]);

            }

        }

        Constant temp;

        if (isFirstConst) {

            temp = new Constant(total);

            tempList.add(temp);

        }

        return new MaxL(linkToArray(tempList));

    }

    // isParaConstSub

    // this function will check whether the given GeneralExpression is

    // Constant, Parameter or Substance

    // Parameter: exp - the GeneralExpression to be checked

    // Return: ture if exp is either Constant, Parameter or Substance

    // false otherwise

    // Precondition: None

    // Postcondition: None

    public static boolean isParaConstSub(GeneralExpression exp) {

        if (exp instanceof Constant)
            return true;

        else if (exp instanceof Parameter)
            return true;

        else if (exp instanceof Substance)
            return true;

        else
            return false;

    }

    // toArray

    // This function will put all Parameters, Substances and Constants

    // on exp to the GeneralExpression[].

    // Parameter: exp - GeneralExpresion to be searched for Parameters,

    // Substances, and Constants.

    // Return: GeneralExpression[] - the array containing all for Parameters,

    // Substances, and Constants in exp

    // null is return if exp contains no for Parameters,

    // Substances, and Constants.

    // Precondition: exp != null

    // Postcondition: None

    public static GeneralExpression[] toArray(GeneralExpression exp) {

        GeneralExpression[] resultArray;

        Object[] temp;

        List data = new LinkedList();

        data = toArray_re(exp);

        if (data.size() > 0) {

            temp = data.toArray();

            resultArray = new GeneralExpression[temp.length];

            for (int i = 0; i < temp.length; i++)

                resultArray[i] = (GeneralExpression) temp[i];

            return resultArray;

        }

        return null;

    }

    // toArray_re := the recursive version of toArray.

    public static List toArray_re(GeneralExpression exp) {

        List data = new LinkedList();

        List listA = new LinkedList();

        List listB = new LinkedList();

        if (isParaConstSub(exp)) {

            data.add(exp);

        } else if (exp instanceof FunctionExpression) {
            GeneralExpression[] tempVar = ((FunctionExpression) exp)
                    .getVariables();
            for (int i = 0; i < tempVar.length; i++) {
                listA = toArray_re(tempVar[i]);
                if (listA.size() > 0) {
                    for (int j = 0; j < listA.size(); j++) {
                        if (!data.contains(listA.get(j)))
                            data.add(listA.get(j));
                    }
                }
            }

        } else if (exp instanceof NonExpression) {
            data.add(exp);

        } else if (exp instanceof Expression) {

            if (((Expression) exp).isUnary()) {

                GeneralExpression tempA = ((Expression) exp).getA();

                listA = toArray_re(tempA);

            } else {

                GeneralExpression tempA = ((Expression) exp).getA();

                GeneralExpression tempB = ((Expression) exp).getB();

                listA = toArray_re(tempA);

                listB = toArray_re(tempB);

            }

            if (listA.size() > 0) {

                for (int i = 0; i < listA.size(); i++) {

                    if (!data.contains(listA.get(i)))

                        data.add(listA.get(i));

                }

            }

            if (listB.size() > 0) {

                for (int i = 0; i < listB.size(); i++) {

                    if (!data.contains(listB.get(i)))

                        data.add(listB.get(i));

                }

            }

        }

        return data;

    }

    // ===========================================================================

    public static GeneralExpression step(List operandList) {

        Object[] operands = operandList.toArray();

        GeneralExpression a = (GeneralExpression) operands[0];

        GeneralExpression b = (GeneralExpression) operands[1];

        return step(a, b);

    }

    public static GeneralExpression compare(List operandList) {

        Object[] operands = operandList.toArray();

        GeneralExpression a = (GeneralExpression) operands[0];

        GeneralExpression b = (GeneralExpression) operands[1];

        return compare(a, b);

    }

    public static GeneralExpression max(List operandList) {

        Object[] operands = operandList.toArray();

        GeneralExpression[] expr = new GeneralExpression[operands.length];

        for (int i = 0; i < expr.length; i++) {

            expr[i] = (GeneralExpression) operands[i];

        }

        return max(expr);

    }

    public static GeneralExpression min(List operandList) {

        Object[] operands = operandList.toArray();

        GeneralExpression[] expr = new GeneralExpression[operands.length];

        for (int i = 0; i < expr.length; i++) {

            expr[i] = (GeneralExpression) operands[i];

        }

        return min(expr);

    }

    public static GeneralExpression sum(List operandList) {

        Object[] operands = operandList.toArray();

        GeneralExpression[] expr = new GeneralExpression[operands.length];

        for (int i = 0; i < expr.length; i++) {

            expr[i] = (GeneralExpression) operands[i];

        }

        return sum(expr);

    }

    public static GeneralExpression mul(List operandList) {

        Object[] operands = operandList.toArray();

        GeneralExpression[] expr = new GeneralExpression[operands.length];

        for (int i = 0; i < expr.length; i++) {

            expr[i] = (GeneralExpression) operands[i];

        }

        return multiply(expr);

    }

    public static GeneralExpression[] linkToArray(List operandList) {

        Object[] operands = operandList.toArray();

        GeneralExpression[] expr = new GeneralExpression[operands.length];

        for (int i = 0; i < expr.length; i++) {

            expr[i] = (GeneralExpression) operands[i];

        }

        return expr;

    }

    // ===========================================================================

    public static class Tester {

        public static void main(String args[]) {

            ReactiveSystem sys = new ReactiveSystem("RSystem");

            sys.setVolume(1);

            Substance s1 = new Substance("H2", sys);

            s1.setValue(1.0);

            Substance s2 = new Substance("O2", sys);

            Substance s3 = new Substance("N2", sys);

            s3.setValue(5);

            Parameter k = new Parameter("r2345", sys);

            GeneralExpression e1 = ExpressionBuilder.buildBinary("+", s1, s2);

            GeneralExpression e2 = ExpressionBuilder.buildBinary("^", e1, k);

            GeneralExpression e4 = ExpressionBuilder.buildBinary("/", s2,

            ExpressionBuilder.buildBinary("-", new Constant(10), s3));

            GeneralExpression e3 = ExpressionBuilder.buildUnary("sqrt",

            ExpressionBuilder.buildBinary("max", e2, e4));

            System.out.println(e3);

            System.out.println(e3.getValue());

            s2.setValue(10);

            System.out.println(e3.getValue());

            k.setValue(2);

            System.out.println(e3.getValue());

            GeneralExpression[] data = new GeneralExpression[4];

            data[0] = e1;

            data[1] = e2;

            data[2] = e3;

            data[3] = e4;

            GeneralExpression a1 = DMath.sum(data);

            GeneralExpression a2 = DMath.sum(data);

            GeneralExpression a3 = DMath.sum(data);

            GeneralExpression a4 = DMath.sum(data);

            System.out.println(a1);

            System.out.println(a2);

            System.out.println(a3);

            System.out.println(a4);

        }

    }

} // ExpressionBuilder

