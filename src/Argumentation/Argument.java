/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Argumentation;

/**
 *
 * @author Plumbly
 */
public class Argument {
    private final String action;
    private final String parameter;
    //private final ArrayList<String> preConds;
    //private final ArrayList<String> Effects;
    
    public Argument(String action, String parameter)
    {
        this.action = action;
        this.parameter = parameter;
    }
}
