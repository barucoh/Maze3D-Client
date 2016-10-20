package presenter;

import java.util.HashMap;

import algorithms.mazeGenerators.Maze3D;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import model.Model;
import view.CLI;
import view.View;


/**
 * Commands Manager
 * <p>Class based on the Command Design Pattern. Aggregates many underlying commands to be processed and sent to the
 * Presenter for further execution. The Commands are received from a CLI / GUI input.</p>
 * 
 * @author Afik & Ohad
 * @version 1.0
 * @see Command
 * @see CLI
 * @see Presenter
 */
public class CommandsManagerClient {
	private Model model;
	private View view;

    public CommandsManagerClient(Model model, View  view) {
        this.model = model;
        this.view = view;
    }

    public HashMap<String, Command> getCommandsMap() {
        HashMap<String, Command> commands = new HashMap<String, Command>();
        commands.put("dir", new DirCommand());
        commands.put("generate_3d_maze", new Generate3DMazeCommand());
        //commands.put("display", new DisplayMazeCommand());
        commands.put("display_cross_section", new DisplayCrossSectionCommand());
        commands.put("save_maze", new SaveMazeCommand());
        commands.put("load_maze", new LoadMazeCommand());
        commands.put("solve", new SolveMazeCommand());
        commands.put("display_solution", new DisplaySolutionCommand());
        commands.put("get_clue", new GetClueCommand());
        commands.put("get_maze", new GetMazeCommand());
        commands.put("character_moved", new characterMoveCommand());
        commands.put("save_properties", new SavePropertiesCommand());
        //commands.put("print_menu", new PrintMenuCommand());
        commands.put("exit", new ExitCommand());

        //--------Visibility false--------
        commands.put("maze_ready", new MazeReadyCommand());
        commands.put("solution_ready", new SolutionReadyCommand());
        commands.put("maze_saved", new MazeSavedCommand());
        commands.put("maze_not_found", new MazeNotFoundCommand());
        commands.put("maze_loaded", new MazeLoadedCommand());
        commands.put("maze_already_exists", new MazeAlreadyExistsCommand());
        commands.put("properties_saved", new PropertiesSavedCommand());
        commands.put("properties_loaded", new PropertiesLoadedCommand());
        commands.put("mazes_solutions_saved", new MazeAlreadyExistsCommand());
        commands.put("mazes_solutions_save_failed", new MazeAlreadyExistsCommand());
        commands.put("mazes_solutions_loaded", new MazeAlreadyExistsCommand());
        commands.put("mazes_solutions_load_failed", new MazeAlreadyExistsCommand());

        return commands;
    }

    public class DirCommand extends CommonCommand {
    	private DirCommand() { this.setVisibility(true); }
        
    	@Override
        public void doCommand(Object[] args) {
    		try {
    			//view.displayDirectory(args[0]);
    		}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
    	@Override
        public void doCommand(String[] args) { }
    }

    public class DisplayCrossSectionCommand extends CommonCommand {
    	private DisplayCrossSectionCommand() { this.setVisibility(true); }
    	
        @Override
        public void doCommand(String[] args) {
        	try {
        		model.getCrossSection(args[0], args[1], Integer.parseInt(args[2]));
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
        @Override
        public void doCommand(Object[] args) {
        	try {
        		int [][] section = (int[][])args[0];
        		int [][] sectionUP = (int[][])args[1];
        		int [][] sectionDOWN = (int[][])args[2];
        		view.displayCrossSection(section, sectionUP, sectionDOWN);
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
    }

   /* public class DisplayMazeCommand extends CommonCommand {
    	private DisplayMazeCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	try {
        		Maze3D maze = model.getMaze(args[0]);
		    	view.displayMaze(maze);
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or maze not found");
        	}
        }
    }
*/
    
    public class DisplaySolutionCommand extends CommonCommand {
    	private DisplaySolutionCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
    		try {
    			model.getSolution(args[0]);
    		}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
	    	catch (Exception ex) {
	    		view.displayMessage("Argument(s) invalid or solution not found");
	    	}
    	}
        @SuppressWarnings("unchecked")
		@Override
        public void doCommand(Object[] args) {
        	try{
        		view.displaySolution((Solution<Position>)args[0]);
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or solution not found");
        	}
        }
    }

    public class GetClueCommand extends CommonCommand {
    	private GetClueCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
    		try {
    			model.getClue(args[0]);
    		}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or solution not found");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        	try{
        		view.setNextStep((Position)args[0]);
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or solution not found");
        	}
        }
    }

    public class ExitCommand extends CommonCommand {
    	private ExitCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
            try {
            	model.exit();
            }
            catch (Exception ex) {
            	ex.printStackTrace();
            }    		
    	}
        @Override
        public void doCommand(Object[] args) {
            try {
            	model.exit();
            }
            catch (Exception ex) {
            	ex.printStackTrace();
            }
        }
    }

    public class Generate3DMazeCommand extends CommonCommand {
    	private Generate3DMazeCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
    		try {
	            String name = args[0];
	            int cols = Integer.parseInt(args[1]);
	            int rows = Integer.parseInt(args[2]);
	            int layers = Integer.parseInt(args[3]);
	            //if (model.getProperties().getGenerateMazeAlgorithm().equals("Growing_Tree"))
	            	model.generateMazeGrowingTree(name, cols, rows, layers);
	            //else
	            	//model.generateMazeSimple(name, cols, rows, layers);
    		}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        	try {
	            
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
    }

    public class LoadMazeCommand extends CommonCommand {
    	private LoadMazeCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try {
        		model.loadMaze(args[0], args[1]);
	    	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}    		
    	}
        @Override
        public void doCommand(Object[] args) {
        	try {
        		
	    	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
    }

    public class SaveMazeCommand extends CommonCommand {
    	private SaveMazeCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try{
        		model.saveMaze(args[0], args[1]);
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        	try{
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
    }

    public class SolveMazeCommand extends CommonCommand {
    	private SolveMazeCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try {
        		//if (model.getProperties().getSolveMazeAlgorithm().equals("BFS"))
        			model.solveMaze(args[0], "BFS");
        		//else
            		//model.solveMaze(args[0], "DFS");
        	}
        	catch (ArrayIndexOutOfBoundsException ex) {
        		view.displayMessage("Not enough arguments!");
        	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        	try {
        		
        	}
        	catch (ArrayIndexOutOfBoundsException ex) {
        		view.displayMessage("Not enough arguments!");
        	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
    }

    public class GetMazeCommand extends CommonCommand {
    	private GetMazeCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try {
	        	model.getMaze(args[0]);
        	}
        	catch (ArrayIndexOutOfBoundsException ex) {
        		view.displayMessage("Not enough arguments!");
        	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        	try {
	        	view.setSelectedMaze((String)args[0], (Maze3D)args[1]);
	        	model.addMaze((String)args[0], (Maze3D)args[1]);
        	}
        	catch (ArrayIndexOutOfBoundsException ex) {
        		view.displayMessage("Not enough arguments!");
        	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
        }
    }
    
//    public class PrintMenuCommand extends CommonCommand {
//    	private PrintMenuCommand() { this.setVisibility(true); }
//    	
//        @Override
//        public void doCommand(Object[] args) {
//        	view.printMenu(getCommandsMap());
//        }
//    }
    
    public class SaveMazesSolutionsCommand extends CommonCommand {
    	private SaveMazesSolutionsCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try {
        		model.saveMazesAndSolutions(args[0]);
        	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}    		
    	}
        @Override
        public void doCommand(Object[] args) {
        	
        }
    }
    
    public class LoadMazesSolutionsCommand extends CommonCommand {
    	private LoadMazesSolutionsCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try {
        		model.loadMazesAndSolutions(args[0]);
        	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        }
    }
    
    public class characterMoveCommand extends CommonCommand {
		private characterMoveCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try {
        		String mazeName = args[0];
        		int x = Integer.parseInt(args[1]);
        		int y = Integer.parseInt(args[2]);
        		int z = Integer.parseInt(args[3]);
        		
    		    Maze3D maze = model.getMaze(mazeName);
    		    
    		    if(maze.getMaze()[x][y][z] != Maze3D.WALL){
		    		view.displayCrossSection(maze.getCrossSectionByZ(z), maze.getCrossSectionByZ(z + 1), maze.getCrossSectionByZ(z - 1));
		    		view.moveCharacter(new Position(x, y, z));
    		    }
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or maze not found");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        	try {
        		
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or maze not found");
        	}
        }
    }
    
    public class SavePropertiesCommand extends CommonCommand {
		private SavePropertiesCommand() { this.setVisibility(true); }

    	@Override
        public void doCommand(String[] args) {
        	try {
        		Properties properties = new Properties();
        		properties.setNumOfThreads(Integer.parseInt(args[0]));
        		properties.setGenerateMazeAlgorithm(args[1]);
        		properties.setSolveMazeAlgorithm(args[2]);
        		model.saveProperties(properties);
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or maze not found");
        	}
    	}
        @Override
        public void doCommand(Object[] args) {
        	try {
        		
        	}
	    	catch (ArrayIndexOutOfBoundsException ex) {
	    		view.displayMessage("Not enough arguments!");
	    	}
        	catch (Exception ex) {
        		view.displayMessage("Argument(s) invalid or maze not found");
        	}
        }
    }
    
    
    //-------------------------------------------
    //Visibility false
    //-------------------------------------------
    
    
    
    public class MazeReadyCommand extends CommonCommand {
    	private MazeReadyCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.notifyMazeIsReady((String)args[0]);
        	//view.displayMessage("Maze " + args[0] + " has been generated successfully!");
        }
    }
    
    public class SolutionReadyCommand extends CommonCommand {
    	private SolutionReadyCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Maze " + (String)args[0] + " has been solved!");
        	view.setSolutionAvailable(true);
        }
    }
    
    public class MazeSavedCommand extends CommonCommand {
    	private MazeSavedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Maze " + (String)args[0] + " has been saved succesfully to file " + (String)args[1]);
        }
    }
    
    public class MazeNotFoundCommand extends CommonCommand {
    	private MazeNotFoundCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Save operation failed: Maze " + (String)args[0] + " cannot be found");
        }
    }
    
    public class MazeLoadedCommand extends CommonCommand {
    	private MazeLoadedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.notifyMazeIsReady((String)args[0]);
        	//view.displayMessage("Maze " + args[0] + " was loaded successfully!");
        }
    }
    
    public class MazeAlreadyExistsCommand extends CommonCommand {
    	private MazeAlreadyExistsCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Maze " + (String)args[0] + " already exists");
        }
    }
    
    public class PropertiesSavedCommand extends CommonCommand {
    	private PropertiesSavedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Properties saved successfully!");
        }
    }
    
    public class PropertiesLoadedCommand extends CommonCommand {
    	private PropertiesLoadedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Properties loaded successfully!");
        }
    }
    
    public class MazesSolutionsLoadFailedCommand extends CommonCommand {
    	private MazesSolutionsLoadFailedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Mazes solutions failed to load from file " + (String)args[0]);
        }
    }
    
    public class MazesSolutionsLoadedCommand extends CommonCommand {
    	private MazesSolutionsLoadedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Mazes solutions loaded successfully!");
        }
    }
    
    public class MazesSolutionsSavedCommand extends CommonCommand {
    	private MazesSolutionsSavedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Mazes solutions saved successfully to " + (String)args[0]);
        }
    }
    
    public class MazesSolutionsSaveFailedCommand extends CommonCommand {
    	private MazesSolutionsSaveFailedCommand() { this.setVisibility(false); }

    	@Override
        public void doCommand(String[] args) { }
        @Override
        public void doCommand(Object[] args) {
        	view.displayMessage("Mazes solutions failed to save to " + (String)args[0]);
        }
    }
}
