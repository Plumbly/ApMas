/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package environment;

import PddlParser.Parser;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Plumbly
 */
public class StateHandler {
    private ArrayList<String> state;
    private HashMap<String, ArrayList<String>> effects;
    public StateHandler()
    {     
        
        //updateState("move_seg_rwy_0_1300_seg_27_0_150_south_south_medium", "airplane_cfbeg");
        //updateState("move_seg_27_0_150_seg_b_27_0_100_south_north_medium", "airplane_cfbeg");
        
    }
    public void initEnv()
    {
        Parser p = new Parser();
        try {            
            p.parseDomain("src/Planning/domain06.pddl");
            p.parseTask("src/Planning/task07.pddl");
        } catch (IOException ex) {
            Logger.getLogger(StateHandler.class.getName()).log(Level.SEVERE, null, ex);
        }     
        state = p.getInit();
        effects = p.getEffects();
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
    public void setState(ArrayList<String> state)
    {
       this.state = state; 
    }
    public void setEffects(HashMap<String, ArrayList<String>> effects)
    {
        this.effects = effects;
    }
    
}
