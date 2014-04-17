package start;


import gui.MainWindow;
import jade.Boot;
import javax.swing.JFrame;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Plumbly
 */
public class Run {
    
    public static void startSystem()
    {
        String[] param = new String[ 2 ];
        param[ 0 ] = "-gui";
        param[ 1 ] = "Leader:agents.LeaderAgent";
        Boot.main( param );
    }
    
    public static void main(String args[])
    {       
        JFrame gui = new MainWindow();
        gui.setVisible(true);
    }
    
}
