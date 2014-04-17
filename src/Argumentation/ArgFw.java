/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Argumentation;

import environment.Action;
import jade.core.AID;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 *
 * @author Plumbly
 */
public class ArgFw {
    private ArrayList<AID> blocked = new ArrayList();
    public ArrayList<HashMap<AID, Action>> computeArguments(ArrayList<Argument> args)
    {       
        ArrayList<String> att = new ArrayList();
        ArrayList<HashMap<AID, Action>> plan = new ArrayList();              
        int totalSize= getTotalSize(args);              
        ArrayList<String> closed = new ArrayList(); // Actions that have already been added to the plan.
        while(totalSize != 0)
        {          
            HashMap<AID, Action> agentActions = new HashMap<>();            
            for (Argument a : args)
            {                                      
                if (!a.isPlanEmpty())
                {
                    Action action = a.get_Next_Action();
                    if (agentActions.containsValue(action) || closed.contains(action.toString())) // if same argument then just use first occurence
                    {
                        totalSize--;
                        a.remove_Action();
                    }else{
                        agentActions.put(a.get_Agent(), action);                        
                    }
                }                                                          
            }
            
            if (agentActions.size() > 1)
            {  
               att = detect_Conflicts(agentActions);
               if (!att.isEmpty())
               {                                                                        
                   ArrayList<String> arguments = getAllArguments(att);
                   agentActions = computeGroundedExtension(arguments,att, agentActions);                                           
               }
               for (AID k : blocked)
               {
                   agentActions.remove(k);
               }               
            }
            for (Argument a : args)
            {
                if (agentActions.containsKey(a.get_Agent()))
                {
                   closed.add(a.get_Next_Action().toString());
                   a.remove_Action();                   
                }
            }           
            plan.add(agentActions);           
            totalSize -= agentActions.size();
        }        
        return plan;
    }
    
    private HashMap<AID, Action> computeGroundedExtension(ArrayList<String> arguments, ArrayList<String> att, HashMap<AID, Action> agentActions)
    {
        
                      
        ArrayList<String> IN = new ArrayList(getUnattacked(arguments, att));
        ArrayList<String> OUT = new ArrayList();
        ArrayList<String> UNDEC = new ArrayList();       
        List<ArrayList<String>> label = Arrays.asList(IN,OUT,UNDEC);
        List<ArrayList<String>> label1 = null ;
        while (!label.equals(label1))
        {
            label1 = Arrays.asList(new ArrayList<>(IN),new ArrayList<>(OUT),new ArrayList<>(UNDEC));
            IN.addAll(getIn(OUT,IN, att));
            OUT.addAll(getOut(IN,OUT, att));            
        }
        arguments.removeAll(IN);
        arguments.removeAll(OUT);
        UNDEC.addAll(arguments);
        for (String out : OUT)
        {
            agentActions.remove(new AID(out, AID.ISLOCALNAME));
        }
        
        
        return agentActions;
    }
    private ArrayList<String> detect_Conflicts(HashMap<AID,Action> agentActions)
    {
        blocked = new ArrayList();
        ArrayList<String> att = new ArrayList();
        for (Map.Entry<AID, Action> e : agentActions.entrySet())
        {                
            for (Map.Entry<AID, Action> f : agentActions.entrySet())
            {                       
                if(!e.getKey().getLocalName().equals(f.getKey().getLocalName())) // makes sure not the same agent
                { 
                    if (!blocked.contains(f.getKey()) && !blocked.contains(e.getKey()))
                    {
                        if(checkPreandEffects(e.getValue(), f.getValue()))
                        {                                   
                            att.add(e.getKey().getLocalName()+ "," + f.getKey().getLocalName());
                        }                                 
                        if(e.getValue().compareParameters(f.getValue()))                              
                        {
                            blocked.add(f.getKey());
                        }
                    }
                }                         
            }
        }
        return att;
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

    private ArrayList<String> getAllArguments(ArrayList<String> att)
    {
        ArrayList<String> args = new ArrayList();
        for (String a : att)
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
    
    
    private boolean checkPreandEffects(Action a1, Action a2)
    {        
        ArrayList<String> a1pc = a1.get_Preconditions();
        ArrayList<String> a2e = a2.get_Effects();
        for (String e : a1pc)
        {          
            e = "(not " + e + ")";                       
            for (String f : a2e)
            {               
                if (e.equals(f))
                {
                    System.out.println("Conflict!");                   
                    return true;
                }
            }      
        }
        return false;
    }
    
    private int getTotalSize(ArrayList<Argument> args)
    {
        int totalSize= 0;
        for (Argument a : args)
        { 
                totalSize += a.getSize();
        }
        return totalSize;
    }
}
