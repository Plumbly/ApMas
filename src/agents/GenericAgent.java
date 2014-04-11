/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;
import PddlParser.TaskWriter;
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
            Object[] arg = getArguments();
            goal = (String) arg[0];
            
            
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
        
        public class Receive extends CyclicBehaviour 
    {
        public Receive(Agent a) { 
             super(a);  
        }
        
        public void action()
        {
            ACLMessage msg = receive();
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
        
        public static class sendArguments extends OneShotBehaviour 
        {   
            public sendArguments(Agent a) { 
                super(a);  
            }
        
        public void action()
        {                                  
            ACLMessage msg = new ACLMessage(ACLMessage.PROPOSE);
            System.out.println("Sending Arguments.");                     
            try{
                msg.setContentObject(invokePlan());
            }catch (Exception e){                
            }            
            msg.addReceiver(new AID("Leader", AID.ISLOCALNAME));
            myAgent.send(msg);                     
        }
        
        
        
        public ArrayList<String> invokePlan() 
        {   
            TaskWriter tw = new TaskWriter();
            Object[] arg = myAgent.getArguments();           
            String file = tw.writeFile((String) arg[0], myAgent);            
            ArrayList<String> plan = new ArrayList();
            try{
                String line;                                           
                ProcessBuilder pb = new ProcessBuilder("python", "src/Planning/pyperplan.py", "src/Planning/domain06.pddl", file);
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
                    plan.add(action.toLowerCase());
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
    

