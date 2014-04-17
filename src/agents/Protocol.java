/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.GenericAgent.sendArguments;
import agents.LeaderAgent.computeArgs;
import environment.Action;
import environment.StateHandler;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Plumbly
 */
public class Protocol {
    
    private static Behaviour b = null;
    public static void exectuteAction(Agent a, int perf, ACLMessage msg) throws UnreadableException
    {    
        
         switch(perf)
         {
             case(ACLMessage.CFP):                
                 a.addBehaviour(new sendArguments(a));
                 break;
             case(ACLMessage.PROPOSE):                 
                 break;                                  
             case (ACLMessage.INFORM):
                 ACLMessage reply = msg.createReply();
                 Action act  = (Action)msg.getContentObject();
                 System.out.println(act.get_Name()+ " " + act.get_Parameters());
                 reply.setPerformative(ACLMessage.CONFIRM);
                 reply.setContent("Action Complete!");
                 a.doWait(5000);
                 StateHandler.updateState(act);
                 a.send(reply); 
                 break;
             case (ACLMessage.CONFIRM):                 
                 break;
         }
    }
    
}
