/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

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
    private static Boolean isBargaining = false;
    private static Behaviour b = null;
    public static void exectuteAction(Agent a, int perf, ACLMessage msg) throws UnreadableException
    {    
        
         switch(perf)
         {
             case(ACLMessage.CFP):
                 a.addBehaviour(new Behaviours.sendArguments(a));
                 break;
             case(ACLMessage.PROPOSE):
                 
                 if (!isBargaining)
                 {
                     b = new Behaviours.computeArgs(a);
                     a.addBehaviour(b);
                     isBargaining = true;                                                                                                                                                              
                 }
                 ArrayList<String> plan = new ArrayList();
                 plan =(ArrayList<String>) msg.getContentObject();
                 HashMap<AID, ArrayList<String>> temp = new HashMap();
                 temp.put(msg.getSender(), plan);
                 Behaviours.plans.putAll(temp);
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
