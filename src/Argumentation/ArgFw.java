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
        ArrayList<HashMap> plan = new ArrayList(); 
        int totalSize=0;
        for (Map.Entry<AID, ArrayList<String>> e : args.entrySet())
        { 
                totalSize += e.getValue().size();
        }
        
        while(totalSize != 0)
        {
            HashMap<AID, String> agentActions = new HashMap<AID, String>();
            for (Map.Entry<AID, ArrayList<String>> e : args.entrySet())
            {   
                agentActions.put(e.getKey(), e.getValue().get(0));                                                      
            }
            
            for (Map.Entry<AID, String> e : agentActions.entrySet())
            {
                
                for (Map.Entry<AID, String> f : agentActions.entrySet())
                {
                    checkPreandEffects(e.getValue(), f.getValue());
                    checkPrecon(e.getValue(), f.getValue());
                }
            }
            if (!agentActions.isEmpty()){plan.add(agentActions);}
            
        }                                                                   
        return plan;
    }
    
    private void constructAttackRelation()
    {
        
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
    
    private void checkPreandEffects(String a1, String a2)
    {
        String[] parts1 = a1.split(" ");
        String[] parts2 = a2.split(" ");
        ArrayList<String> a1pc = StateHandler.get_Action_Effects(parts1[0].trim());
        ArrayList<String> a2pc = StateHandler.get_Action_Preconditions(parts2[0].trim());
        for (String e : a1pc)
        {
            String tmp = "";
            if (e.substring(0, 5).equals("(not "))
            {               
                tmp = e.substring(5, e.length()-1);
                if(a2pc.contains(tmp))
                {
                    System.out.println("Conflict");
                }
            }else
            {
                //tmp = new StringBuilder(e).insert(1, "not_").toString();
            }
            /**
            if (a2pc.contains(tmp))
            {
                System.out.println("Conflict!");
                break;
            }
            */
        }
        int i = 0;
    }
<<<<<<< HEAD

=======
>>>>>>> 734988aa98bcdc6cb90160eeed4e09f148bdf1da
    
    private void getNegative()
    {
        
    }
    
}
