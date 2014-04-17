/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import Argumentation.ArgFw;
import Argumentation.Argument;
import environment.Action;
import environment.StateHandler;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.CyclicBehaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.SimpleBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.UnreadableException;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.io.BufferedReader;
import java.io.IOException;
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
        public static ArrayList<Argument> plans = new ArrayList();
        private static Boolean isBargaining = false;
        
               
        protected void setup()
        {
            addBehaviour(new Receive(this));
            addBehaviour(new Leader(this));
            addBehaviour(new requestArguments(this));            
        }
        
        public class Leader extends OneShotBehaviour
    {
            public Leader(Agent a) { 
             super(a);  
        }
        public void action()
        {
            StateHandler.initEnv();
            ArrayList<String> goals = StateHandler.getGoals();
            createAgents(goals);
        }
        
        public void createAgents(ArrayList<String> goals) 
    {          
        AgentController agent;
        ContainerController cont = getContainerController();
        for (int i = 0; i < goals.size(); i++)
        {   
            Object[] args = new Object[1];
            args[0] = goals.get(i);
            try {                                                                                    
                agent = cont.createNewAgent("agent" + i,"agents.GenericAgent", args);                                    
                agent.start();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    }
        
    static class Receive extends CyclicBehaviour 
    {
        Behaviour b;
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
                }else if (msg.getPerformative() == ACLMessage.PROPOSE){
                    try {
                        if (!isBargaining)
                        {
                            b = new computeArgs(myAgent, msg);                    
                            myAgent.addBehaviour(b);                     
                            isBargaining = true;                                                                                                                                                              
                        }                        
                        Argument plan =(Argument) msg.getContentObject();                                                
                        plans.add(plan);
                    }catch (UnreadableException ex) {
                        Logger.getLogger(LeaderAgent.class.getName()).log(Level.SEVERE, null, ex);                                                   
                    }                    
                }else{
                    try {                    
                        Protocol.exectuteAction(myAgent, msg.getPerformative(), msg);
                    }catch (UnreadableException ex) {
                        Logger.getLogger(LeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }                                                  
                }
                //System.out.println("I, " + myAgent.getLocalName()+ " have received a message from " 
                        //+ msg.getSender().getLocalName() + " with Content: " + msg.getContent() + ".");
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
        public static ArgFw af;
        
        public computeArgs(Agent a, ACLMessage msg)
        {
            super(a);                      
            af = new ArgFw();           
        }
        
        public void onStart() {
            wakeupTime = System.currentTimeMillis() + timeout;
        }
        public void action()
        {
            long dt = wakeupTime - System.currentTimeMillis();
            if (dt <= 0) {
                ArrayList<HashMap<AID,Action>> plan = af.computeArguments(plans);            
                myAgent.addBehaviour(new giveOrders(myAgent, plan));
                isDone = true;
                plans.clear();
                
                
            }else{
                block(dt);
            }                                            
        }
              
          public boolean done(){return isDone;}      
        }
        
    
        static class giveOrders extends SimpleBehaviour
    {
        public boolean isComplete = false;
        private static int noAgents;
        private static ArrayList<HashMap<AID, Action>> plan;
               
        public giveOrders(Agent a, ArrayList<HashMap<AID, Action>> plan)
        {
            super(a);
            this.plan = plan;            
        }              
        
        public void action()
        {            
            HashMap<AID,Action> actions = null;
            
            
            if (noReplied == noAgents) 
            {             
                noAgents = 0;
                noReplied = 0;
                if(!plan.isEmpty()){
                    actions = plan.get(0);
                    plan.remove(0);
                    for (Map.Entry<AID, Action> entry : actions.entrySet())
                    {
                        ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
                        try {
                            msg.setContentObject(entry.getValue());
                        } catch (IOException ex) {
                            Logger.getLogger(LeaderAgent.class.getName()).log(Level.SEVERE, null, ex);
                        }
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
