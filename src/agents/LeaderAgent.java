/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

import jade.core.Agent;
import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 *
 * @author Plumbly
 */
public class LeaderAgent extends Agent
    {
        protected void setup()
        {
            addBehaviour(new Behaviours.Receive(this));
            addBehaviour(new Behaviours.requestArguments(this));            
        }
        
        
    }      
