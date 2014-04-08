/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
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
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Plumbly
 */

    public class GenericAgent extends Agent 
    {   
        private String goal;
        protected void setup()
        {   
            String goal = null;
            Object[] arg = new Object[1]; 
            arg[0] = goal;
            this.setArguments(arg);
            
            System.out.println("Registering " + getLocalName() + " with the DFservice.");
            DFAgentDescription dfd = new DFAgentDescription();
            ServiceDescription sd  = new ServiceDescription();
            
            dfd.setName( getAID() );
            sd.setType( "GenericAgent" );
            sd.setName("Agents");
            dfd.addServices(sd);
            try {  
                DFService.register( this, dfd );  
            }
            catch (FIPAException fe) {
                fe.printStackTrace(); 
            }            
            addBehaviour(new Receive(this));        
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
                    try {
                        Protocol.exectuteAction(myAgent, msg.getPerformative(), msg);
                    } catch (UnreadableException ex) {
                        Logger.getLogger(GenericAgent.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    System.out.println("I, " + myAgent.getLocalName()+ " have received a message from " 
                        + msg.getSender().getLocalName() + " with Content: " + msg.getContent() + ".");
            }
                
            
        }
        
    }
        
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
        
        private static ArrayList<String> invokePlan() 
        {   
            ArrayList<String> plan = new ArrayList();
            try{
                String line;                                           
                ProcessBuilder pb = new ProcessBuilder("python", "src/Planning/pyperplan.py", "src/Planning/domain06.pddl", "src/Planning/task07.pddl");
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
    }   
    

