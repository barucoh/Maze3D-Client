package view;

import algorithms.mazeGenerators.Maze3D;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import model.Model;
import presenter.Command;

/**
 * View interface
 * <p>Defines all the functions needed to be defined in a basic View (MVC).
 * The View will handle all the UI\UX of the program, this is the only part accessible to the customer.</p>
 *
 * @author Afik & Ohad
 * @version 1.0
 * @see Command
 * @see Model
 * @see Controller
 */
public interface View {
    void start();
    void displaySolution(Solution<Position> solution);
    void displayDirectory(String path);
    void notifyMazeIsReady(String name);
    void displayMaze(Maze3D maze);
    void displayCrossSection(int [][] mazeSection, int [][] mazeFloorUp, int [][] mazeFloorDown);
    void displayMessage(String msg);
    void setSelectedMaze(String name, Maze3D maze);
    void setSolutionAvailable(boolean solutionAvailable);
    void setNextStep(Position nextStep);
    //void printMenu(HashMap<String, Command> cliMapper);
	void moveCharacter(Position position);
}
