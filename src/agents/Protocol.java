/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import agents.GenericAgent.sendArguments;
import agents.LeaderAgent.computeArgs;
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
                 reply.setPerformative(ACLMessage.CONFIRM);
                 reply.setContent("Carrying out action!");
                 a.doWait(5000);
                 a.send(reply); 
                 break;
             case (ACLMessage.CONFIRM):                 
                 break;
         }
    }
    
}
