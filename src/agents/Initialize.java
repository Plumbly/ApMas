/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import PddlParser.Parser;
import jade.core.Agent;
import jade.wrapper.AgentController;
import jade.wrapper.ContainerController;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.logging.Level;
import java.util.logging.Logger;






/**
 *
 * @author Plumbly
 */
public class Initialize extends Agent{
    String task1 = "src/Planning/task06.pddl";
    String task2 = "src/Planning/task07.pddl";
    public void setup(){ 
        Parser p = new Parser();
        try {
            p.parseDomain();
            p.parseTask();
            //createAgents(3);
        } catch (IOException ex) {
            Logger.getLogger(Initialize.class.getName()).log(Level.SEVERE, null, ex);
        }
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