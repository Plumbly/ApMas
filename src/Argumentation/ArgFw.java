/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Argumentation;

import environment.StateHandler;
import jade.core.AID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 *
 * @author Plumbly
 */
public class ArgFw {
    
    public ArrayList<HashMap> computeArguments(HashMap<AID, ArrayList<String>> args)
    {
        HashMap<AID, ArrayList<String>> argument = args;
        ArrayList<String> ar = new ArrayList();
        ArrayList<HashMap> plan = new ArrayList();
        ArrayList<AID> blocked;
        
        int totalSize=0;
        
        for (Map.Entry<AID, ArrayList<String>> e : argument.entrySet())
        { 
                totalSize += e.getValue().size();
        }
        ArrayList<String> closed = new ArrayList();
        while(totalSize != 0)
        {
            blocked = new ArrayList<>();
            HashMap<AID, String> agentActions = new HashMap<>();
            
            for (Map.Entry<AID, ArrayList<String>> e : argument.entrySet())
            {                                      
                if (!e.getValue().isEmpty())
                {
                    if (!agentActions.containsValue(e.getValue().get(0)) && !closed.contains(e.getValue().get(0))) // if same argument then just use first occurence
                    {
                        agentActions.put(e.getKey(), e.getValue().get(0));                        
                    }else{
                        totalSize--;
                        e.getValue().remove(0);
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
                            if (!blocked.contains(f.getKey()) && !blocked.contains(e.getKey()))
                            {
                                if(checkPreandEffects(e.getValue(), f.getValue()))
                                {                                   
                                    ar.add(e.getKey().getLocalName()+ "," + f.getKey().getLocalName());
                                }                                 
                                if(checkParameters(e.getValue(), f.getValue()))                              
                                {
                                    blocked.add(f.getKey());
                                }
                            }
                        }                         
                    }
                }
               
               
               if (!ar.isEmpty())
               {                                                      
                   System.out.println("Calculating Semantics");
                   ArrayList<String> arguments = getAllArguments(ar);
                   agentActions = constructAttackRelation(arguments,ar, agentActions);                                           
               }
               for (AID k : blocked)
               {
                   agentActions.remove(k);
               }
               
            }
            for (AID a : agentActions.keySet())
            {
                   argument.get(a).remove(0);
                   closed.add(agentActions.get(a));
            }
            if (!agentActions.isEmpty()){plan.add(agentActions);}           
            totalSize -= agentActions.size();
        }
        for (HashMap<AID, String> p : plan)
        {
            for (Map.Entry<AID, String> f : p.entrySet())
            {
                System.out.println(f.getValue());
            }           
        }
        return plan;
    }
    
    private HashMap<AID, String> constructAttackRelation(ArrayList<String> arguments, ArrayList<String> ar, HashMap<AID, String> agentActions)
    {
        
                      
        ArrayList<String> IN = new ArrayList(getUnattacked(arguments, ar));
        ArrayList<String> OUT = new ArrayList();
        ArrayList<String> UNDEC = new ArrayList();       
        List<ArrayList<String>> label = Arrays.asList(IN,OUT,UNDEC);
        List<ArrayList<String>> label1 = null ;
        while (!label.equals(label1))
        {
            label1 = Arrays.asList(new ArrayList<>(IN),new ArrayList<>(OUT),new ArrayList<>(UNDEC));
            IN.addAll(getIn(OUT,IN, ar));
            OUT.addAll(getOut(IN,OUT, ar));            
        }
        arguments.removeAll(IN);
        arguments.removeAll(OUT);
        UNDEC.addAll(arguments);
        for (String out : OUT)
        {
            agentActions.remove(new AID(out, AID.ISLOCALNAME));
        }
        //getChosenAction(IN, agentActions);
        
        return agentActions;
    }
    
    private ArrayList<String> getUnattacked(List<String> arguments, List<String> ar)
    {
        ArrayList<String> unattacked = new ArrayList();
        for (String a : arguments)
        {
            boolean isAttacked = false;
            for (String r : ar)
            {                
                String[] parts = r.split(",");
                if (a.equals(parts[1]))
                {
                    isAttacked = true;
                    break;
                }
            }
            if (!isAttacked)
            {
                unattacked.add(a);
            }          
        }
        return unattacked;
    }
    
    private ArrayList<String> getIn(ArrayList<String> OUT,ArrayList<String> IN, List<String> ar)
    {
        ArrayList<String> in = new ArrayList();
        for (String o : OUT)
        {           
            for (String f : ar)
            {
                String[] parts = f.split(",");
                if (o.equals(parts[0]))
                {
                    if (!IN.contains(parts[1]) && !in.contains(parts[1]))
                    {
                        in.add(parts[1]);
                    }                    
                }
            }
        }
        return in;
    }
    private ArrayList<String> getOut(ArrayList<String> IN,ArrayList<String> OUT, List<String> ar)
    {
        ArrayList<String> out = new ArrayList();
        for (String in : IN)
        {            
            for (String r : ar)
            {
                String[] parts = r.split(",");
                if (in.equals(parts[0]))
                {
                    if (!OUT.contains(parts[1]) && !out.contains(parts[1]))
                    {
                        out.add(parts[1]);
                    }
                    
                }
            }           
        }
        return out;
    }   

    private ArrayList<String> getAllArguments(ArrayList<String> ar)
    {
        ArrayList<String> args = new ArrayList();
        for (String a : ar)
        {
            String[] parts = a.split(",");
            if(!args.contains(parts[0]))
            {
                args.add(parts[0]);
            }
            if(!args.contains(parts[1]))
            {
                args.add(parts[1]);
            }
        }
        return args;
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
    private boolean checkReverse(ArrayList<String> ar, String a)
    {
        String[] parts = a.split(",");
        a = parts[1] + "," + parts[0];
        if (ar.contains(a))
        {
            ar.remove(a);
            return true;
        }
        return false;
    }
    private boolean checkPreandEffects(String a1, String a2)
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
                    return true;
                }
            }      
        }
        return false;
    }   
}
