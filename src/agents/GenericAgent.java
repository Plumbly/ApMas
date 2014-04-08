/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;
import jade.core.Agent;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;

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
            addBehaviour(new Behaviours.Receive(this));        
        }  
        
    }   
    

