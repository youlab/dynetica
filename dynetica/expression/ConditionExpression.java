
/**
 * ConditionExpression.java
 *
 *
 * Created: Tue Aug 29 12:34:01 2000
 *
 * @author Lingchong You
 * @version 0.01
 * This seems to be replaced by LogicExpression
 */

package dynetica.expression;

abstract public class ConditionExpression implements GeneralExpression{
    protected GeneralExpression[] data;
    protected int type = -1; // the precedence of an operator should be embodied here.
    protected double value;

    public ConditionExpression() {	
 	data = null;
    }

    public ConditionExpression(GeneralExpression[] a) {
	data = a;
    }

    public GeneralExpression[] getData() { return data; }
    public GeneralExpression getData(int index) { return data[index]; }
    
    public void setA(GeneralExpression a, int index) {
	data[index] = a;
    }


    public void setB(GeneralExpression[] b) {
	data = b;
    }

    
    abstract public void compute(); // this should set the value.
    public double getValue() {
	compute();
	return value;
    }

    public int getType() { return type; }

    public String toString() {
	int i = 0;
	StringBuffer sb = new StringBuffer(getClass().getName());
	sb.append("( ");
	for(i = 0; i < (data.length - 1); i++){
		sb.append(data[i] + " , ");
	}
	sb.append(data[i] + " )");
	return sb.toString();
    }    


} // ConditionExpression

