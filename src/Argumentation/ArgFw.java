/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Argumentation;

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
        Random rn = new Random();
        for (Map.Entry<AID, ArrayList<String>> e : args.entrySet())
        { 
                totalSize += e.getValue().size();
        }
        
        while(totalSize != 0)
        {
            HashMap<AID, String> agentActions = new HashMap<AID, String>();
            for (Map.Entry<AID, ArrayList<String>> e : args.entrySet())
            {   
                double r = rn.nextDouble();
                if (r <= 0.5)
                {
                    if (!e.getValue().isEmpty())
                    {
                        agentActions.put(e.getKey(), e.getValue().get(0));
                        totalSize--;
                        e.getValue().remove(0);
                    }                                      
                }                       
            }
            if (!agentActions.isEmpty()){plan.add(agentActions);}
            
        }                                                                   
        return plan;
    }
    
    private void constructAttackRelation()
    {
        
    }
    
    private void checkPrecon()
    {
    
    }
    private void checkEffects()
    {
        
    }
    
}
