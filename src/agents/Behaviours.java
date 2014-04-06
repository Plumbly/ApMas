package agents;


import Argumentation.ArgFw;
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
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;



public class Behaviours
{   
    private static int noReplied;
    private static int noAgents;  
    public static HashMap<AID, ArrayList<String>> plans = new HashMap<>();
    static class sendArguments extends SimpleBehaviour 
    {   
        public sendArguments(Agent a) { 
             super(a);  
        }
        
        public void action()
        {
            
                      
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            System.out.println("Sending Arguments.");
            //Object[] arg = myAgent.getArguments();
            //String temp  = arg[0].toString();
            
            try{
                msg.setContentObject(invokePlan());
            }catch (Exception e){                
            }            
            msg.addReceiver(new AID("agent0", AID.ISLOCALNAME));
            myAgent.send(msg);
        }
        
        public  boolean done() {  return (true); }
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
                        Logger.getLogger(Behaviours.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("I, " + myAgent.getLocalName()+ " have received a message from " 
                        + msg.getSender().getLocalName() + " with Content: " + msg.getContent() + ".");
            }else{
                block();
            }
        }
        
    }
    static class Leader extends SimpleBehaviour
    {
        private boolean isDone = false;
        private final long timeout = 5000;
        private long wakeupTime;
        public Leader(Agent a){           
            super(a);
        }
        public void onStart() {
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
                    DFAgentDescription[] result = DFService.search(myAgent, dfd);
                    //System.out.println(result.length + " results" );
                if (result.length>0)
                {
                    for (int i = 0; i < result.length;i++)
                    {
                        msg.addReceiver(result[i].getName());
                    }
                    msg.setContent("Send Me your Arguments!");
                    myAgent.send(msg);
                    isDone=true;
                }
                wakeupTime = System.currentTimeMillis() + timeout; 
                } catch (FIPAException ex) {
                    Logger.getLogger(Behaviours.class.getName()).log(Level.SEVERE, null, ex);
                }
                }else{
                    block(dt);
                }
        }
        public boolean done(){
            return isDone;
        }
    } 
    
    
    static class computeArgs extends SimpleBehaviour
    {
        private final long timeout = 2000;
        private boolean isDone = false;
        private long wakeupTime;
        public static ArgFw af;       
        public computeArgs(Agent a)
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
                ArrayList<HashMap> plan = af.computeArguments(plans);            
                myAgent.addBehaviour(new giveOrders(myAgent, plan));
                isDone = true;
                plans.clear();
                wakeupTime = System.currentTimeMillis() + timeout;
                
            }else{
                block(dt);
            }
            System.out.println("Sending arguments to AF.");            
            
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
    
    private static ArrayList<String> invokePlan() 
        {   
            ArrayList<String> plan = new ArrayList();
            try{
                String line;                                           
                ProcessBuilder pb = new ProcessBuilder("python", "src/Planning/pyperplan.py", "src/Planning/domain06.pddl", "src/Planning/task.pddl");
                Process p = pb.start();
                
                BufferedReader ereader = new BufferedReader (new InputStreamReader (p.getErrorStream()));
                while ((line = ereader.readLine()) != null) {
                    System.out.println(line);
                }
                ereader.close();
                
                BufferedReader reader = new BufferedReader (new InputStreamReader (p.getInputStream()));
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(" ");
                    
                    int indexOfOpenBracket = line.indexOf("(");
                    int indexOfLastBracket = line.lastIndexOf(")");
                    String action = line.substring(indexOfOpenBracket+1, indexOfLastBracket);
                    plan.add(action);
                System.out.println(action);
                
                }
            }catch (Exception e)
            {
                System.out.println(e.getMessage());
            }
            return plan;
        }
    

}
