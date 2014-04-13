/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Argumentation;

import environment.StateHandler;
import jade.core.AID;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Plumbly
 */
public class ArgFw {
    
    public ArrayList<HashMap> computeArguments(HashMap<AID, ArrayList<String>> args)
    {
        ArrayList<String> ar = new ArrayList();
        ArrayList<HashMap> plan = new ArrayList(); 
        int totalSize=0;
        for (Map.Entry<AID, ArrayList<String>> e : args.entrySet())
        { 
                totalSize += e.getValue().size();
        }
        
        while(totalSize != 0)
        {
            HashMap<AID, String> agentActions = new HashMap<>();
            for (Map.Entry<AID, ArrayList<String>> e : args.entrySet())
            {                                      
                if (!e.getValue().isEmpty())
                {
                    if (!agentActions.containsValue(e.getValue().get(0)) && !plan.contains(e.getValue().get(0))) // if same argument then just use first occurence
                    {
                        agentActions.put(e.getKey(), e.getValue().get(0));                        
                    }else{
                        totalSize--;
                    }
                }                                                          
            }
            
            if (agentActions.size() > 1)
            {
               for (Map.Entry<AID, String> e : agentActions.entrySet())
               {                
                    for (Map.Entry<AID, String> f : agentActions.entrySet())
                    {
                        if(!e.getKey().getLocalName().equals(f.getKey().getLocalName())) // makes sure not the same agent
                        {
                                 String tmp;
                                if (checkParameters(e.getValue(), f.getValue()))
                                {
                                    ar.add(e.getValue() + "," + f.getValue() + " (Parameter Conflict)");
                                }
                                tmp = checkPreandEffects(e.getValue(), f.getValue());
                                if (tmp != null){ar.add(tmp);}                            
                                                                                                      
                        }                                                           
                    }
                }
               if (!ar.isEmpty())
               {                                                      
                   System.out.println("Calculating Semantics");
                   //agentActions = constructAttackRelation(ar, agentActions);                                           
               }
               
            }
            for (AID a : agentActions.keySet())
            {
                   args.get(a).remove(0);
            }    
            plan.add(agentActions);
            totalSize -= agentActions.size();
        }                                                                   
        return plan;
    }
    
    private HashMap<AID, String> constructAttackRelation(ArrayList<String> ar, HashMap<AID, String> agentActions)
    {
        String[] parts = ar.get(0).split(",");
        AID agent = null;
        for (Map.Entry<AID, String> e : agentActions.entrySet())
        { 
             if (e.getValue().equals(parts[1].trim()))
             {
                agent = e.getKey();
             }
        }
        agentActions.remove(agent);
        
        return agentActions;
    }
    


    private boolean checkParameters(String a1, String a2){
        String[] parts1 = a1.split(" ");
        String[] parts2 = a2.split(" ");
        if (parts1[1].equals(parts2[1]))
        {
            return true;
        }
        return false;
    
}
    
  


    private void checkPrecon(String a1, String a2)
    {
        ArrayList<String> a1pc = StateHandler.get_Action_Preconditions(a1);
        ArrayList<String> a2pc = StateHandler.get_Action_Preconditions(a2);
        for (String p : a1pc)
        {
            if (a2pc.contains(p))
            {
                            
            }
        }
    }
    private void checkEffects()
    {
        
    }
    
    private String checkPreandEffects(String a1, String a2)
    {
        String[] parts1 = a1.split(" ");
        String[] parts2 = a2.split(" ");
        ArrayList<String> a1pc = StateHandler.get_Action_Preconditions(parts1[0].trim());
        ArrayList<String> a2e = StateHandler.get_Action_Effects(parts2[0].trim());
        for (String e : a1pc)
        {
            if (e.contains("?a"))
            {
                e = e.replaceAll("\\?a", parts1[1]);
            }
            e = "(not " + e + ")";           
            
            for (String f : a2e)
            {
                if (f.contains("?a"))
                {
                    f = f.replaceAll("\\?a", parts2[1]);
                }
                if (e.equals(f))
                {
                    System.out.println("Conflict!");
                    return (a1 + "," + a2 + " (Preconditions and Effects Conflict)");
                }
            }      
        }
        return null;
    }   
}
