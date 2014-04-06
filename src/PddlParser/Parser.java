/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package PddlParser;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Plumbly
 */
public class Parser {
    
    private ArrayList<String> preds;
    private HashMap<String, ArrayList<String>> preCond;
    private HashMap<String, ArrayList<String>> effects;
    private ArrayList<String> goals;
    private ArrayList<String> env;
    
    public Parser(){                   
            preds = new ArrayList();
            goals = new ArrayList();
            env = new ArrayList();
            preCond = new HashMap();
            effects = new HashMap();
        }
    public void parseDomain() throws IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader("src/Planning/domain06.pddl"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            switch (parts[0].trim()) {
                case "(:predicates":
                    preds = readIn(reader);
                    break;
                case "(:action":
                    readAction(reader, parts[1].trim());
                    break;
            }                    
        }        
    }
    public void parseTask() throws FileNotFoundException, IOException
    {
        BufferedReader reader = new BufferedReader(new FileReader("src/Planning/task07.pddl"));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(" ");
            switch (parts[0].trim()) {
                case "(:init":                   
                    env = readIn(reader);
                    break;
                case "(:goal":
                    goals = readIn(reader);
                    break;
            }                    
        }
        int i = 0;
    }
    private void readAction(BufferedReader reader, String action) throws IOException
    { 
        boolean isDone = false;
        while(!isDone)
        {             
           String line = reader.readLine().trim();
           String[] parts = line.split(" ");
            switch (parts[0].trim()) {
                case ":precondition":
                    preCond.put(action, readIn(reader));
                    break;
                case ":effect":
                    effects.put(action, readIn(reader));
                    isDone = true;
                    break;
            }
        }
                  
    }
    private ArrayList<String> readIn(BufferedReader reader) throws IOException
    {
        ArrayList<String> temp = new ArrayList();
        String line;
        int brCount = 1;
        while (brCount != 0)
        {
            line= reader.readLine().trim();
            if (line.trim().length() > 0)
            {                        
                if (line.charAt(0) == '(')
                {
                    temp.add(line);
                    System.out.println(line);
                }else if (line.charAt(0) == ')')
                {
                    brCount--;
                }
            }
        }
        return temp;
    }
}
        
    
