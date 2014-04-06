
import agents.Initialize;
import jade.Boot;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Plumbly
 */
public class Run {
    
    public static void main(String args[])
    {       
        String[] param = new String[ 2 ];
        param[ 0 ] = "-gui";
        param[ 1 ] = "Initialize:agents.Initialize";
        Boot.main( param );
        
    }
    
}
