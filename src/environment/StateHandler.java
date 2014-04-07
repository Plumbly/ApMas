/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package environment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

/**
 *
 * @author Plumbly
 */
public class StateHandler {
    private ArrayList<String> state;
    private HashMap<String, ArrayList<String>> effects;
    public StateHandler(ArrayList<String> state,  HashMap<String, ArrayList<String>> effects)
    {
        this.state = state;
        this.effects = effects;
        updateState("move_seg_Rwy_0_1300_seg_27_0_150_south_south_medium", "airplane_CFBEG");
        updateState("move_seg_27_0_150_seg_B_27_0_100_south_north_medium", "airplane_CFBEG");
        
    }
    
    public void updateState(String action, String parameter)
    {
        ArrayList<String> tmp = effects.get(action);
        for (String s : tmp)
        {
            if (s.contains("?a"))
            {
                 s = s.replaceAll("\\?a", parameter);
            }
            String[] parts = s.trim().split(" ");
            if (parts[0].trim().equals("(not"))
            {
                
               String t = s.trim().substring(5, s.length()-1);               
               state.remove(t);                
            }else{
                state.add(s);              
            }
        }
        Collections.sort(state);
        for (String a : state)
        {
            System.out.println(a);
        }
    }
    public ArrayList<String> getState()
    {
        return state;
    }
    
    
}
