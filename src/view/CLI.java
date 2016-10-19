package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Observable;

/**
 * CLI
 * <p>Set up a new Command line interface to interact with the application.</p>
 * <p>This loop will run as a separate Thread and dispatch commands from the given input.</p>
 * 
 * @author Afik & Ohad
 *
 */
public class CLI extends Observable{
    BufferedReader in;
    PrintWriter out;

    public CLI(BufferedReader in, PrintWriter out) {
        this.in = in;
        this.out = out;
    }
    
    /**
     * This is the main command loop
     * every command that given will be executed ro
     * all observers
     */
    public void Start() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String input = "";
                setChanged();
                notifyObservers("print_menu");
                do
                {
                    input = "";
                    try {
                    	input = in.readLine();
                    	setChanged();
                    	notifyObservers(input);
                    }
                    catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }while (!input.split(" ")[0].toUpperCase().equals("EXIT"));
            }
        });
        thread.start();
    }
}
