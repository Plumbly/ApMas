/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import Argumentation.ArgFw;
import environment.StateHandler;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Plumbly
 */
public class LeaderAgent extends Agent
    {
        private static int noReplied;
        private static int noAgents;
        public static HashMap<AID, ArrayList<String>> plans = new HashMap<>();
        protected void setup()
        {
            addBehaviour(new Receive(this));
            addBehaviour(new requestArguments(this));            
        }
        
        static class Leader extends OneShotBehaviour
    {
        public void action()
        {
            
        }
    }
        
    static class Receive extends CyclicBehaviour 
    {
        public Receive(Agent a) { 
             super(a);  
        }
        
        public void action()
        {
            ACLMessage msg = this.myAgent.receive();
            if (msg != null)
            {                
                if (msg.getPerformative() == ACLMessage.CONFIRM)
                {                 
                    noReplied++;
                }else{
                    try {                    
                        Protocol.exectuteAction(myAgent, msg.getPerformative(), msg);
                    } catch (UnreadableException ex) {
                        Logger.getLogger(LeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                   
                }
                System.out.println("I, " + myAgent.getLocalName()+ " have received a message from " 
                        + msg.getSender().getLocalName() + " with Content: " + msg.getContent() + ".");
            }else{
                block();
            }
        }
        
    }
    static class requestArguments extends SimpleBehaviour
    {
        private boolean isDone = false;
        private final long timeout = 5000;
        private long wakeupTime;
        public requestArguments(Agent a){           
            super(a);
        }
        public void onStart() 
        {
            wakeupTime = System.currentTimeMillis() + timeout;            
        }
        public void action()
        {
            long dt = wakeupTime - System.currentTimeMillis();
            if (dt <= 0) {
                ACLMessage msg = new ACLMessage(ACLMessage.CFP);
                DFAgentDescription dfd = new DFAgentDescription();
                ServiceDescription sd  = new ServiceDescription();
                sd.setType( "GenericAgent");
                dfd.addServices(sd);
                       
                
                    
                try {
                    DFAgentDescription[] result;
                    result = DFService.search(myAgent, dfd);
                    if (result.length>0)
                    {
                        for (int i = 0; i < result.length;i++)
                        {
                            msg.addReceiver(result[i].getName());
                        }
                        msg.setContent("Send Me your Arguments!");
                        myAgent.send(msg);
                        isDone=true;
                    
                    wakeupTime = System.currentTimeMillis() + timeout; 
                
                    }else{
                        block(dt);
                    }
                } catch (FIPAException ex) {
                    Logger.getLogger(LeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                }
                    //System.out.println(result.length + " results" );
                
            }
        }
        public boolean done(){return isDone;}
    }
    
    static class computeArgs extends SimpleBehaviour
    {
        private final long timeout = 2000;
        private boolean isDone = false;
        private long wakeupTime;
        private ACLMessage msg;
        public static ArgFw af;       
        public computeArgs(Agent a, ACLMessage msg)
        {
            super(a);
            this.msg = msg;           
            af = new ArgFw();           
        }
        
        public void onStart() {
            wakeupTime = System.currentTimeMillis() + timeout;
        }
        public void action()
        {
            long dt = wakeupTime - System.currentTimeMillis();
            if (dt <= 0) {
                ArrayList<HashMap> plan = af.computeArguments(plans);            
                myAgent.addBehaviour(new giveOrders(myAgent, plan));
                isDone = true;
                plans.clear();
                wakeupTime = System.currentTimeMillis() + timeout;
                
            }else{
                if (msg != null)
                {
                    try {
                        ArrayList<String> plan = new ArrayList();
                        plan =(ArrayList<String>) msg.getContentObject();
                        HashMap<AID, ArrayList<String>> temp = new HashMap();
                        temp.put(msg.getSender(), plan);
                        plans.putAll(temp);                      
                    } catch (UnreadableException ex) {
                        Logger.getLogger(LeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                block(dt);
            }
            //System.out.println("Sending arguments to AF.");            
            
        }
        public boolean done(){return isDone;}
    }
        static class giveOrders extends SimpleBehaviour
    {
        public boolean isComplete = false;
        
        private static ArrayList<HashMap> plan;
               
        public giveOrders(Agent a, ArrayList<HashMap> plan)
        {
            super(a);
            this.plan = plan;            
        }              
        
        public void action()
        {            
            HashMap<AID,String> actions = null;
            
            
            if (noReplied == noAgents) 
            {             
                noAgents = 0;
                noReplied = 0;
                if(!plan.isEmpty()){
                    actions = plan.get(0);
                    plan.remove(0);
                    for (Map.Entry<AID, String> entry : actions.entrySet())
                    {
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        msg.setContent("Do action " + entry.getValue());
                        msg.addReceiver(entry.getKey());
                        noAgents++;
                        myAgent.send(msg);                   
                    }  
                }else{
                    isComplete = true;
                } 
                
                                                          
            }else{               
            }
        }
        public  boolean done() { 
            if(isComplete){
                noAgents = 0;
                noReplied = 0;
                System.out.println("Plan has been carried out!");
            }
            return isComplete;
        }
    }
    }      
