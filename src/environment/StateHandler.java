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
    private static ArrayList<String> state;
    private static HashMap<String, ArrayList<String>> effects;
    private static HashMap<String, ArrayList<String>> preCond;
    private static ArrayList<String> goals;
    public StateHandler()
    {                    
        
    }
    public static void initEnv()
    {
        Parser p = new Parser();
        try {            
            p.parseDomain("src/Planning/domain30.pddl");
            p.parseTask("src/Planning/task30.pddl");
        } catch (IOException ex) {
            Logger.getLogger(StateHandler.class.getName()).log(Level.SEVERE, null, ex);
        }     
        state = p.getInit();
        preCond = p.getPreconds();
        effects = p.getEffects();
        goals = p.getGoals();
    }
    
    public static void updateState(String action, String parameter)
    {       
        ArrayList<String> tmp = effects.get(action);
        for (String s : tmp)
        {
            if (s.contains("?a"))
            {
                s = s.replaceAll("\\?a", parameter);
            }
            
            if (s.trim().substring(0,6).equals("(not ("))
            {
                
               String t = s.trim().substring(5, s.length()-1);               
               state.remove(t);                
            }else{
                state.add(s);              
            }
        }
        Collections.sort(state);
        System.out.println("\n State After Applying action: " + action + " With Parameter: " + parameter);
        for (String a : state)
        {           
            System.out.println(a);
        } 
        System.out.println(" ");
    }
    public static ArrayList<String> getState()
    {
        return state;
    }
    public static ArrayList<String> getGoals()
    {
        return goals;
    }
    
    public static ArrayList<String> get_Action_Preconditions(String action)
    {
        return preCond.get(action);
    }
    
    public static ArrayList<String> get_Action_Effects(String action)
    {
        return effects.get(action);
    }
    
    
}
