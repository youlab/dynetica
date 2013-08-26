


package dynetica.expression;
/**
 * Usage: Delay (Substance, GeneralExpression ge) returns the value of a substance back in history with the specified timeDelay.
 * For example, if the current time is Tc, the function will return the value of the Substance at Tc - ge. If Tc-ge <=0, 
 * the function returns the initialValue of the substance.
 * 
 * 
 */

public class Delay extends FunctionExpression {
    public static int parameter_num = 2;
    private dynetica.entity.Substance substance = null;
    private GeneralExpression timeDelay = null;
    private dynetica.entity.SimulationTimer timer = null;
    private dynetica.system.ReactiveSystem system = null;
//    private dynetica.algorithm.Algorithm algorithm = null;
    
    public Delay( GeneralExpression [] a) {
	super(a);
        this.substance = (dynetica.entity.Substance) a[0];
        this.timeDelay = a[1];
        this.system = (dynetica.system.ReactiveSystem)  substance.getSystem();
        this.timer =  system.getTimer();
// this.algorithm = system.getAlgorithm();
        type = ExpressionConstants.DELAY;
//        value = variables[0].getValue();
    }
    
    //
    // 1) determine the indices of the time points that embrace the time point of interest.
    // 2) Get the corresponding value of the substance by interpolation.
    
    
    public void compute() {
       // if (algorithm == null) algorithm = system.getAlgorithm();
        
        double td = timeDelay.getValue();
        double currentTime = timer.getTime();
        
        //in current implemention, I'm returning the initial value of the substance if the time delay is too large.
        if (td >= currentTime) {
            value = substance.getInitialValue();
        }
        
        //better to throw an exception in the future.
        else if (td <0 ) { 
            value = substance.getValue();
            System.out.println("Warning: Trying to get future values (negative time delay). Using present value instead");
        } 
        
        else {
          //  double samplingStep = algorithm.getSamplingStep();
          //  int numberOfPoints = substance.getValues().length;
          //  int numberOfSteps = (int) Math.ceil(td/samplingStep);
          //  int a = numberOfPoints - numberOfSteps;
           // System.out.println("index of history data point:" + a);
            //basic interpolation
            int a = Math.max(getLowerIndex(timer.getTimePoints(), currentTime-td), 0);
            
  //          System.out.print(timer.getTimePoint(a));
  //          System.out.print(currentTime - td);
  //          System.out.println(timer.getTimePoint(a+1));
            
            value = substance.getValue(a) + 
                    (currentTime - td - timer.getTimePoint(a))/(timer.getTimePoint(a + 1) - timer.getTimePoint(a)) * 
                    (substance.getValue(a + 1) - substance.getValue(a));
        }
       
    }
    
    //
    // uses binary search to find the index of an element immediate preceeding the value "key".
    // 
    private static int getLowerIndex(double[] a, double key) {
        int low = 0;
        int high = a.length - 1;
        while (low < high) {
             int mid = (low + high)/2;
         //    System.out.print(low);
         //    System.out.print(" " + mid);
         //    System.out.println(" " + high);
             
             if (a[mid] <= key) {
                if (a[mid + 1] > key) return mid;
                low = mid + 1;
            }
            else {
                high = mid; 
            }
        }
        return -(low + 1); //not found
    }
    
    @Override
  public String toString () { return "delay(" + substance.toString() 
					+ ", " + timeDelay.toString() 
                                        + ")"; }
    
    public static class Test {
        public static void main(String args[]){
            double [] a = {0.0, 0.2, 0.3, 0.4, 0.5, 0.6, 0.7, 0.9, 100.0, 129.0};
            double key1 = -0.1;
            double key2 = 0.29333;
            double key3 = 0.01;
            double key5 = 0.45;
            double key6 = 100.01;
            
            System.out.print(key1); 
            System.out.println(":" + Delay.getLowerIndex(a, key1));
            System.out.print(key2); 
            System.out.println(":" + Delay.getLowerIndex(a, key2));
            System.out.print(key3); 
            System.out.println(":" + Delay.getLowerIndex(a, key3));
            System.out.print(key5); 
            System.out.println(":" + Delay.getLowerIndex(a, key5));
            System.out.print(key6); 
            System.out.println(":" + Delay.getLowerIndex(a, key6));
            
        }
    }
} // End