package view;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Observable;
import java.util.Observer;

import algorithms.mazeGenerators.Maze3D;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import model.Model;

/**
 * MyView
 * <p>Implements all of View's functions and handling all the program's UI / UX.
 * 
 * @author Afik & Ohad
 * @see Model
 * @see Controller
 */
public class MyView extends Observable implements View, Observer{
    //private BufferedReader in;
    private PrintWriter out;
    private CLI cli;
    
    private String selectedMazeName;
    private Maze3D selectedMaze;
    
    public MyView(BufferedReader in, PrintWriter out) {
        //this.in = in;
        this.out = out;
        
        this.cli = new CLI(in, out);
        this.cli.addObserver(this);
    }

    @Override
    public void start() {
        this.cli.Start();
    }

    @Override
    public void displaySolution(Solution<Position> solution) {
        //this.displayMessage(solution);
    }
    
    @Override
    public void setSolutionAvailable(boolean solutionAvailable) {} ;
    
    @Override
    public void setNextStep(Position nextStep) {};

    @Override
    public void displayDirectory(String path) {
        Path dir = FileSystems.getDefault().getPath(path);
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            for (Path file: stream) {
                out.println(file.getFileName());
            }
            out.flush();
        } catch (IOException | DirectoryIteratorException x) {
            x.printStackTrace();
        }
    }

    @Override
    public void notifyMazeIsReady(String name) {
        this.displayMessage("Maze " + name + " is ready!");
    }

    @Override
    public void displayMaze(Maze3D maze) {
    	if (maze == null)
    		this.displayMessage("No such maze found");
    	else
    		this.displayMessage(maze.toString());
    }

    @Override
    public void displayCrossSection(int [][] mazeSection, int [][] floorUp, int [][] floorDown) {
        for(int i = 0; i < mazeSection.length; i++) {
            out.println("{");
            for (int j = 0; j < mazeSection[i].length; j++)
                out.print(mazeSection[i][j]);
            out.println("}");
        }
        out.flush();
    }

    @Override
    public void displayMessage(String msg) {
        out.println(msg);
        out.flush();
    }
    
    
    @Override
    public void setSelectedMaze(Maze3D maze) {
    		this.selectedMaze = maze;
    }
        
    @Override
	public void update(Observable o, Object arg) {
	    	if (o == this.cli) {
	    		setChanged();
	    		notifyObservers(arg);
	    	}
	}

	@Override
	public void moveCharacter(Position position) {
		// TODO Auto-generated method stub
		
	}
}