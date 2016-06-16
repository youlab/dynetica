/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dynetica.expression;

/**
 * 
 * @author lingchong
 * 
 */
public class Assignment extends SimpleOperator {
    public Assignment(GeneralExpression a, GeneralExpression b) {
        super('=', a, b);
        type = ExpressionConstants.ASSIGNMENT;
    } 

    @Override
    public void compute() {
        
    //expects an EntityVariable (a parameter, substance, or expression variable) on the left-hand side
          if (a instanceof dynetica.entity.EntityVariable) {
            dynetica.entity.EntityVariable ev = (dynetica.entity.EntityVariable) a;
            ev.setValue(b.getValue());
            this.value = 0; 
           }
          else {
   // for now, do nothing if a is not an entity variable.
           this.value = -1.0; 
          }
        }
        
}
