/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PddlParser;

import environment.StateHandler;
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
    public void writeFile(StateHandler state){
        Writer writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(
            new FileOutputStream("src/Planning/task.pddl"), "utf-8"));
            String output = "";
            output += "(define (problem PROBLEM_X)\n";
            output += "(:domain airport_fixed_structure)\n";
            output += "(:objects)\n";
            output += "(:init\n";
            for (String a : state.getState())
            {
                output += a + "\n";
            }
            output += ")\n";
            output += "(:goal     (and\n";
            output += "(airborne airplane_DFBOY seg_09_0_150)\n";
            output += ")\n" + ")\n" + ")";           
            writer.write(output);
        } catch (IOException ex) {
        } finally {
            try {writer.close();} catch (Exception ex) {}
        }
    }
    
}
