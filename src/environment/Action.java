/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package environment;

import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author Plumbly
 */
public class Action implements Serializable {
    private final String name;    
    private final String parameters;
    private final ArrayList<String> preCons;
    private final ArrayList<String> effects;
    public Action (String name, ArrayList<String> preCons, ArrayList<String> effects)
    {
        this.name = name;
        this.preCons = preCons;
        this.effects = effects;       
        parameters = null;
    }
    public Action (Action action, String parameters)
    {
        this.name = action.get_Name();
        this.preCons = action.get_Preconditions();
        this.effects = action.get_Effects();
        this.parameters = parameters;
        instantiate_Preconditions();
        instantiate_Effects();
               
    }
    private void instantiate_Preconditions()
    {
        for (String p : preCons)
        {
            if (p.contains("?a"))
            {
                int index = preCons.indexOf(p);
                p = p.replaceAll("\\?a", parameters);
                preCons.set(index, p);
            }
        }        
    }
    private void instantiate_Effects()
    {
        for (String e : effects)
        {
            if (e.contains("?a"))
            {
                int index = effects.indexOf(e);
                e = e.replaceAll("\\?a", parameters);
                effects.set(index, e);
            }
        }  
    }
    public String get_Parameters()
    {
        return parameters;
    }
    
    public ArrayList<String> get_Preconditions()
    {
        return preCons;
    }
    public ArrayList<String> get_Effects()
    {
        return effects;
    }
    public String get_Name()
    {
        return name;
    }
    public boolean compareParameters(Action a)
    {
        if (a.get_Parameters().equals(parameters))
        {
            return true;
        }
        return false;
    }
    public String toString()
    {
        return name + " " + parameters;
    }
    
    
}
