/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PddlParser;

import environment.StateHandler;
import jade.core.Agent;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

/**
 *
 * @author Plumbly
 */
public class TaskWriter {
    public String writeFile(String goal, Agent agent){
        Writer writer = null;
        String file = "src/Planning/" + agent.getLocalName()+"task.pddl";
        try {          
            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), "utf-8"));
            String output = "";
            output += "(define (problem PROBLEM_X)\n";
            output += "(:domain airport_fixed_structure)\n";
            output += "(:objects)\n";
            output += "(:init\n";
            for (String a : StateHandler.getState())
            {
                output += "\t" + a + "\n";
            }
            output += ")\n";
            output += "(:goal     (and\n";
            output += "\t" + goal;
            output += ")\n" + ")\n" + ")";           
            writer.write(output);
        } catch (IOException ex) {
        } finally {
            try {writer.close();} catch (Exception ex) {}
        }
        return file;
    }
    
}
