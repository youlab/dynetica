
/**
 * DoubleList.java
 *
 *
 * Created: Sat Sep  2 14:59:46 2000
 *
 * @author Lingchong You
 * @version 0.01
 */
package dynetica.util;

import java.util.*;
public class DoubleList  {
    List data = new ArrayList();

    public DoubleList() {	
    }

    public void add(double value) {
	data.add(new Double(value));
    }

    public void clear() {data.clear();}
    public double get(int i) {
	return ((Double) (data.get(i))).doubleValue();
    }

    public void set(int i, double value) {
	data.set(i, new Double(value));
    }

    public boolean isEmpty() {
	return data.isEmpty();
    }

    public Iterator iterator() {
	return data.iterator();
    }

    public double[] doubleValues() {
	int n = data.size();
	double [] temp = new double [n];
	for (int i = 0; i < n ; i++) temp[i] = get(i);
	return temp;
    }

    public void setValues(double[] newValues) {
	for (int i = 0; i < newValues.length; i ++) 
	    set(i, newValues[i]);
    }
    
} // DoubleList
