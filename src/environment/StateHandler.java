/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package environment;

import java.util.ArrayList;

/**
 *
 * @author Plumbly
 */
public class StateHandler {
    private ArrayList<String> state;
    public StateHandler(ArrayList<String> state)
    {
        this.state = state;
    }
    
    public void updateState()
    {
        
    }
    public ArrayList<String> getState()
    {
        return state;
    }
    
}
