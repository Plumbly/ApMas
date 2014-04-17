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
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Plumbly
 */
public class StateHandler {
    private static ArrayList<String> state;
    private static ArrayList<Action> actions;    
    private static ArrayList<String> goals;
    private static ReadWriteLock lock = new ReentrantReadWriteLock();
    public StateHandler()
    {                    
        
    }
    public static void initEnv()
    {
        Parser p = new Parser();
        try {            
            p.parseDomain("src/Planning/domain06.pddl");
            p.parseTask("src/Planning/task07.pddl");
        } catch (IOException ex) {
            Logger.getLogger(StateHandler.class.getName()).log(Level.SEVERE, null, ex);
        }     
        state = p.getInit();
        actions = p.getActions();        
        goals = p.getGoals();
    }
    
    public static void updateState(Action action)
    {   
        boolean updated = false;
        while(!updated)
        {
            lock.writeLock().lock();
            try{
                for (String s : action.get_Effects())
                {                        
                    if (s.trim().substring(0,6).equals("(not ("))
                    {                
                        String t = s.trim().substring(5, s.length()-1);               
                        state.remove(t);                
                    }else{
                        state.add(s);              
                    }
                }
                Collections.sort(state);
                System.out.println("\n State After Applying action: " + action.get_Name() + " With Parameter: " + action.get_Parameters());
                for (String a : state)
                {           
                    System.out.println(a);
                } 
                System.out.println(" ");
            }finally{
                lock.writeLock().unlock();
                updated = true;
            }
        }
        
        
    }
    public static ArrayList<String> getState()
    {
        return state;
    }
    public static ArrayList<String> getGoals()
    {
        return goals;
    }
    public static Action get_Action_By_Name(String action)
    {
        for (Action a : actions)
        {
            if (a.get_Name().equals(action))
            {
                return a;
            }
        }
        return null;
    }
      
    
    
}
