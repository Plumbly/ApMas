/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Argumentation;

import environment.Action;
import jade.core.AID;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Plumbly
 */
public class Argument implements Serializable{
    private AID agent;
    private ArrayList<Action> plan;
    
    
    public Argument(AID agent, ArrayList<Action> plan)
    {
        this.agent = agent;
        this.plan = plan;
    }
    
    public int getSize()
    {
        return plan.size();
    }
    public boolean isPlanEmpty()
    {        
        return plan.isEmpty();            
    }
    
    public Action get_Next_Action()
    {
        return plan.get(0);
    }
    public void remove_Action()
    {
        plan.remove(0);
    }
    public AID get_Agent()
    {
        return agent;
    }
}
