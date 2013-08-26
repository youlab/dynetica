/***************************************************************************
                          Variable.java  -  description
                             -------------------
    begin                : Mon Mar 24 2000
    copyright            : (C) 2000 by Lingchong You
    email                : you@cae.wisc.edu
 ***************************************************************************/
package dynetica.expression;
import java.util.*;
import dynetica.system.*;

public interface Variable extends GeneralExpression {  
    public void setValue(double v);
    public double getMax();
    public void setMax(double max);
    public double getMin();
    public void setMin(double min);
}
