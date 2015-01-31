/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package dynetica.algorithm;

import dynetica.entity.Substance;
import java.util.HashMap;

/**
 *
 * @author chrismurphy
 */
public class CustomDataFit extends DataFit{
    
    private HashMap<String, Double> parameters;
    
    public CustomDataFit(){
        super();
    }
    
    public CustomDataFit(Substance independent, Substance dependent){
        super(independent, dependent);
    }
    
    public void addParameter(String name){
        parameters.put(name, null);
    }

    @Override
    public void run() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void createBestFitLine() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
