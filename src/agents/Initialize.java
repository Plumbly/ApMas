/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import PddlParser.Parser;
import PddlParser.TaskWriter;
import environment.StateHandler;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;






/**
 *
 * @author Plumbly
 */
public class Initialize extends Agent{
    public void setup(){        
            
            
            //TaskWriter t = new TaskWriter();
            //t.writeFile(sh);
            createAgents(3);
        
        
    }   
    public void createAgents(int noAgents) 
    {   
        AgentController agent;
        ContainerController cont = getContainerController();
        boolean isLeader = false;
        for (int i = 0; i < noAgents; i++)
        {              
            try {                
                if (!isLeader)
                {   
                    agent = cont.createNewAgent(("agent" + i),"agents.LeaderAgent", null);
                    isLeader = true;                 
                }else{
                    
                    agent = cont.createNewAgent("agent" + i,"agents.GenericAgent", null);                    
                }
                agent.start();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    
    
    
    
    
}