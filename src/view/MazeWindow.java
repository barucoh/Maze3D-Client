package view;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import algorithms.mazeGenerators.Maze3D;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import algorithms.search.State;

/**
 * This is the main game window.
 * All the widgets (buttons and display canvas)
 * are defined and initialized in here.
 * 
 * @author Afik & Ohad
 * @see BaseWindow
 * @see View
 */
public class MazeWindow extends BaseWindow implements View {
	
	private MazeDisplay mazeDisplay;
	private static final String[] MAZE_TYPES = { "Simple", "Growing_Tree" };
	private static final String[] SOLUTION_TYPES = { "BFS", "DFS" };
	private static final int MAZE_FLOOR_DOWN = 0;
	private static final int MAZE_FLOOR_CURRENT = 1;
	private static final int MAZE_FLOOR_UP = 2;
	private boolean solutionAvailable = false;
	
	Button btnGenerateMaze, btnSolveMaze, btnSaveMaze, btnLoadMaze, btnSaveProperties, btnGetClue;
	Label lblName, lblFloorNumber;
	Combo comboMazes;

	private String selectedMazeName;
	private Maze3D selectedMaze;
	private Position startPosition;
	private Position goalPosition;
	
	
	//-------Inherited methods------------
	@Override
	protected void initWidgets() {

		//--------Configuring layouts-------
		shell.setLayout(new GridLayout(2, false));
		
		btnGenerateMaze = new Button(shell, SWT.PUSH);
		btnGenerateMaze.setText("Generate maze");
		btnGenerateMaze.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));

		mazeDisplay = new MazeDisplay(shell, SWT.BORDER);
		mazeDisplay.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 9));
		mazeDisplay.setFocus();
		
		btnSolveMaze = new Button(shell, SWT.PUSH);
		btnSolveMaze.setText("Solve maze");
		btnSolveMaze.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));
		btnSolveMaze.setEnabled(false);
		
		btnGetClue = new Button(shell, SWT.PUSH);
		btnGetClue.setText("Get clue");
		btnGetClue.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));
		btnGetClue.setEnabled(false);
		
		btnSaveMaze = new Button(shell, SWT.PUSH);
		btnSaveMaze.setText("Save current maze");
		btnSaveMaze.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));
		btnSaveMaze.setEnabled(false);
		
		btnLoadMaze = new Button(shell, SWT.PUSH);
		btnLoadMaze.setText("Load maze");
		btnLoadMaze.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));
		
		btnSaveProperties = new Button(shell, SWT.PUSH);
		btnSaveProperties.setText("Save propeties");
		btnSaveProperties.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));
		
		lblName = new Label(shell, SWT.NONE);
		lblName.setText("Mazes:");
		lblName.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));
		
		comboMazes = new Combo(shell, SWT.BORDER | SWT.READ_ONLY);
		comboMazes.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));
		
		lblFloorNumber = new Label(shell, SWT.NONE);
		lblFloorNumber.setText("Floor:");
		lblFloorNumber.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false, 1, 1));

		//------Adding listeners-----		
		
		//--------Generate Event-----------
		btnGenerateMaze.addSelectionListener(new SelectionListener() {
			
			/**
			 * Once the user clicks on this button
			 * He'll be presented with the dialog to enter the 
			 * new maze requirements
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showGenerateMazeWindow();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {}
		});
		
		//--------Solve Maze Event---------
		btnSolveMaze.addSelectionListener(new SelectionListener() {
			
			/**
			 * Triggers a notification to the model the solve
			 * the maze.
			 * Once the solution is ready we trigger another 
			 * command to display it to the user.
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) { 
				setChanged();
				notifyObservers("solve " + selectedMazeName + " BFS");
				while (solutionAvailable == false)
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
	            setChanged();
	            notifyObservers("display_solution " + selectedMazeName);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { }
		});
		
		//--------Get Clue Event---------
		btnGetClue.addSelectionListener(new SelectionListener() {
			
			/**
			 * In order to generate a clue to the user
			 * we have to solve the maze first.
			 * Once its solved, we send a command to
			 * display the next move.
			 */
			@Override
			public void widgetSelected(SelectionEvent arg0) { 
				setChanged();
				notifyObservers("solve " + selectedMazeName + " BFS");
				while (solutionAvailable == false)
					try {
						Thread.sleep(250);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				setChanged();
				notifyObservers("get_clue " + selectedMazeName);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { }
		});
		
		//--------Save Event-----------
		btnSaveMaze.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showSaveMazeWindow(selectedMazeName);
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
		
		//--------Load Event-----------
		btnLoadMaze.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showLoadMazeWindow();
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		//--------Combo Event-----------
		comboMazes.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				selectedMazeName = comboMazes.getItem(comboMazes.getSelectionIndex());
				System.out.println(selectedMazeName);
				setChanged();
				notifyObservers("get_maze " + selectedMazeName);
				
				setChanged();
				notifyObservers("display_cross_section " + selectedMazeName + " Z 1"); //instead of 1 it's supposed to be nubmer of floor
			}
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
			}
		});
		
		//--------Maze Display Paint Event---------
		mazeDisplay.addPaintListener(new PaintListener() {
			@Override
			public void paintControl(PaintEvent e) {
				e.gc.setForeground(new Color(null,0,0,0));
			   	e.gc.setBackground(new Color(null,0,0,0));
			   	
			   	int width = mazeDisplay.getSize().x;
			   	int height = mazeDisplay.getSize().y;

			   	if (mazeDisplay.getMazeData(MAZE_FLOOR_CURRENT).length != 0)
			   	{
				   	int h=height / mazeDisplay.getMazeData(MAZE_FLOOR_CURRENT)[0].length;
				   	int w=width / mazeDisplay.getMazeData(MAZE_FLOOR_CURRENT).length;
				   	
				   	for(int i = 0; i < mazeDisplay.getMazeData(MAZE_FLOOR_CURRENT)[0].length; i++)
				   		for(int j = 0; j < mazeDisplay.getMazeData(MAZE_FLOOR_CURRENT).length; j++){
				   			int x = j * w;
				   			int y = i * h;
				   			if(mazeDisplay.getMazeData(MAZE_FLOOR_CURRENT)[j][i] != 0)
				   				e.gc.fillRectangle(x, y, w, h);
				   			else if (mazeDisplay.getMazeData(MAZE_FLOOR_UP)[j][i] == 0 || mazeDisplay.getMazeData(MAZE_FLOOR_DOWN)[j][i] == 0)
				   				mazeDisplay.drawStairs(j, i, w, h, e.gc);
				   			else if (mazeDisplay.getCharacterPos().getZ() == startPosition.getZ() && startPosition.getX() == j && startPosition.getY() == i)
				   				mazeDisplay.drawEntrance(j, i, w, h, e.gc);
				   			else if (mazeDisplay.getCharacterPos().getZ() == goalPosition.getZ() && goalPosition.getX() == j && goalPosition.getY() == i)
				   				mazeDisplay.drawGoal(j, i, w, h, e.gc);
				   			}
				   	mazeDisplay.drawCharacterPosition(w, h, e.gc);
			   	}
			}
		});
		
		//--------Maze Display Key Pressed Event---------
		mazeDisplay.addKeyListener(new KeyListener() {	
			@Override
			public void keyReleased(KeyEvent arg0) { }
			@Override
			public void keyPressed(KeyEvent e) {
				Position pos = mazeDisplay.getCharacterPos();
				switch (e.keyCode) {
					
					//-------Right--------
					case SWT.ARROW_RIGHT:
						setChanged();
						notifyObservers("character_moved " + selectedMazeName + " " + (pos.x + 1) + " " + pos.y + " " + pos.z);
						break;

					//-------Left--------
					case SWT.ARROW_LEFT:
						setChanged();
						notifyObservers("character_moved " + selectedMazeName + " " + (pos.x - 1) + " " + pos.y + " " + pos.z);
						break;
						
					case SWT.ARROW_UP:
						setChanged();
						notifyObservers("character_moved " + selectedMazeName + " " + pos.x + " " + (pos.y - 1) + " " + pos.z);
						break;
					
					case SWT.ARROW_DOWN:
						setChanged();
						notifyObservers("character_moved " + selectedMazeName + " " + pos.x + " " + (pos.y + 1) + " " + pos.z);
						break;
						
					case SWT.F1:
						setChanged();
			   			if (mazeDisplay.getMazeData(MAZE_FLOOR_DOWN)[pos.x][pos.y] == 0)
			   			{
			   				notifyObservers("character_moved " + selectedMazeName + " " + pos.x + " " + pos.y + " " + (pos.z - 2));
			   				int value = pos.z - 2;
			   				display.asyncExec(new Runnable() {
			   					@Override
			   					public void run() {
		   							lblFloorNumber.setText("Floor: " + value);
			   					}
			   				});
			   			}
						break;
						
					case SWT.F2:
						setChanged();
			   			if (mazeDisplay.getMazeData(MAZE_FLOOR_UP)[pos.x][pos.y] == 0)
			   			{
			   				notifyObservers("character_moved " + selectedMazeName + " " + pos.x + " " + pos.y + " " + (pos.z + 2));
			   				int value = pos.z + 2;
			   				display.asyncExec(new Runnable() {
			   					@Override
			   					public void run() {
		   							lblFloorNumber.setText("Floor: " + value);
			   					}
			   				});
			   			}
						break;
				}
			}
		});
	
		//--------Save Properties Button Event---------
		btnSaveProperties.addSelectionListener(new SelectionListener() {		
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				showSavePropertiesWindow();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { }
		});
	}
	
	/**
	 * Given a new position, A command sent to 
	 * move the character their.
	 */
	@Override
	public void setNextStep(Position nextStep) {
		setChanged();
		notifyObservers("character_moved " + selectedMazeName + " " + nextStep.x + " " + nextStep.y + " " + nextStep.z);
		mazeDisplay.redraw();
	}
	/**
	 * This method will start a timer task and every 500 ms
	 * move the character to the next position based on the 
	 * solution
	 * 
	 * @param solution	The maze solution sent from the model.
	 * @see Solution
	 */
	@Override
	public void displaySolution(Solution<Position> solution) {
		List<State<Position>> states = solution.getStates();
		TimerTask task = new TimerTask() {
			int count = 0;
			@Override
			public void run() {
				display.asyncExec(new Runnable() {
					State<Position> curState;
					@Override
					public void run() {
						if (count == states.size() - 1)
							cancel();
						curState = states.get(count);
						Position pos = curState.getValue();
						setChanged();
						notifyObservers("character_moved " + selectedMazeName + " " + pos.x + " " + pos.y + " " + pos.z);
						count += 1;
						mazeDisplay.redraw();
					}
				});
			}
		};
		Timer timer = new Timer();
		timer.scheduleAtFixedRate(task, 0, 500);
	}
	@Override
	public void displayDirectory(String path) {	}
	
	/**
	 * Once the new generated maze is ready, we can enable all
	 * the related buttons and save it to the combo box
	 * 
	 * @param mazeName The name of the maze that is ready
	 */
	@Override
	public void notifyMazeIsReady(String mazeName) {
		displayMessage("Maze " + mazeName + " is ready");
/*
		setChanged();
		notifyObservers("get_maze " + mazeName);
		
		setChanged();
		notifyObservers("display_cross_section " + mazeName + " Z 1"); //instead of 1 it's supposed to be nubmer of floor
*/		
	}
	@Override
	public void displayMaze(Maze3D maze) {
		
	}
	
	/**
	 * Set the data of the current running maze to what we got
	 * back from the model.
	 * 
	 * @param mazeSection The level which the character is currently on.
	 * @param mazeFloorUp The floor above
	 * @param mazeFloorDown The floor below
	 */
	@Override
	public void displayCrossSection(int[][] mazeSection, int [][] mazeFloorUp, int [][] mazeFloorDown) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				mazeDisplay.setMazeData(mazeSection, mazeFloorUp, mazeFloorDown);
			}
		});
	}
	
	/**
	 * Set the current active maze
	 * 
	 * @param Maze3D Instance of the current maze
	 */
	@Override
	public void setSelectedMaze(String mazeName, Maze3D maze) {
		this.selectedMaze = maze;
		this.goalPosition = selectedMaze.getGoalPosition();
		this.startPosition = selectedMaze.getStartPosition();
		mazeDisplay.setCharacterStartPosition(startPosition);
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				lblFloorNumber.setText("Floor: 1");
				comboMazes.add(mazeName);
				comboMazes.select(comboMazes.getItemCount() - 1);
				btnSaveMaze.setEnabled(true);
				btnSolveMaze.setEnabled(true);
				btnGetClue.setEnabled(true);
			}
		});
	}
	
	/**
	 * A better way to interact with the user.
	 * 
	 * @param msg A string to display to the user.
	 */
	@Override
	public void displayMessage(String msg) {
		display.asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageBox msgBox = new MessageBox(shell);
				msgBox.setMessage(msg);
				msgBox.open();
			}
		});
	}
	@Override
    public void setSolutionAvailable(boolean solutionAvailable) {
		this.solutionAvailable = solutionAvailable;
	}
	
	//--------Local methods-------
	private void showGenerateMazeWindow() {
		Shell shell = new Shell();
		shell.setText("Generate maze window");
		shell.setSize(300, 200);		
				
		shell.setLayout(new GridLayout(2, false));	
				
		Label lblName = new Label(shell, SWT.NONE);
		lblName.setText("Maze Name: ");
		
		Text txtName = new Text(shell, SWT.BORDER);
		txtName.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblRows = new Label(shell, SWT.NONE);
		lblRows.setText("Rows: ");
		
		Text txtRows = new Text(shell, SWT.BORDER);
		txtRows.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblCols = new Label(shell, SWT.NONE);
		lblCols.setText("Cols: ");
		
		Text txtCols = new Text(shell, SWT.BORDER);
		txtCols.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblLayers = new Label(shell, SWT.NONE);
		lblLayers.setText("Layers: ");
		
		Text txtLayers = new Text(shell, SWT.BORDER);
		txtLayers.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Button btnGenerateMaze = new Button(shell, SWT.PUSH);
		shell.setDefaultButton(btnGenerateMaze);
		btnGenerateMaze.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		btnGenerateMaze.setText("Generate maze");
		
		btnGenerateMaze.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {	
				
				MessageBox msg = new MessageBox(shell, SWT.OK);
				msg.setText("Maze Generator");
				int cols = Integer.parseInt(txtCols.getText());
				int rows = Integer.parseInt(txtRows.getText());
				int layers = Integer.parseInt(txtLayers.getText());
				String name = txtName.getText();
				
				setChanged();
				notifyObservers("generate_3d_maze " + name + " " + cols + " " + rows + " " + layers);
				
				//msg.setMessage("The maze " +  name + " is being generated for you. \n \n Rows: " + rows + " Columns: " + cols + " Layers: " + layers);
				
				//msg.open();
				
				selectedMazeName = name;
				
				shell.close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {			
				
			}
		});

		//mazeDisplay = new MazeDisplay(this.shell, SWT.NONE);
		shell.open();
	}
	
	/**
	 * Open a new window for the user to choose 
	 * a location on the local machine to save the maze.
	 * We save it as .maz file
	 * 
	 * @param mazeName The required maze name
	 */
	private void showSaveMazeWindow(String mazeName) {
		
		FileDialog saveMaze = new FileDialog(shell, SWT.SAVE);
		saveMaze.setText("Save maze");
		saveMaze.setFilterPath("C:/");
		String[] filterExt = { "*.maz" };
		saveMaze.setFilterExtensions(filterExt);
		String fileName = saveMaze.open();

		if (fileName != null || fileName != "") {
			setChanged();
			notifyObservers("save_maze " + mazeName + " " + fileName);
		}
	}
	
	/**
	 * Open a file selection dialog so we can 
	 * get a .maz file from the user and display it.
	 */
	private void showLoadMazeWindow() {
		
		FileDialog saveMaze = new FileDialog(shell, SWT.OPEN);
		saveMaze.setText("Load maze");
		saveMaze.setFilterPath("C:/");
		String[] filterExt = { "*.maz" };
		saveMaze.setFilterExtensions(filterExt);
		String fileName = saveMaze.open();
		if (fileName != null || fileName != "") {
			int length = fileName.split("\\\\").length;
			String mazeName = fileName.split("\\\\")[length - 1].split("\\.")[0];
			System.out.println(mazeName);
			selectedMazeName = mazeName;
			setChanged();
			notifyObservers("load_maze " + mazeName + " " + fileName);
		}
	}
	
	/**
	 * Dialog to save the current properties as XML
	 */
	private void showSavePropertiesWindow() {
		Shell shell = new Shell();
		shell.setText("Save properties window");
		shell.setSize(300, 200);		
				
		shell.setLayout(new GridLayout(2, false));	
				
		Label lblNumOfThreads = new Label(shell, SWT.NONE);
		lblNumOfThreads.setText("Number of threads: ");
		
		Text txtNumOfThreads = new Text(shell, SWT.BORDER);
		txtNumOfThreads.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
		Label lblGenMazeAlgo = new Label(shell, SWT.NONE);
		lblGenMazeAlgo.setText("Generate Maze Algorithm: ");

		Combo comboGenMazeAlgo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboGenMazeAlgo.setItems(MAZE_TYPES);
		comboGenMazeAlgo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboGenMazeAlgo.select(1);
		
		Label lblSolveMazeAlgo = new Label(shell, SWT.NONE);
		lblSolveMazeAlgo.setText("Solve Maze Algorithm: ");

		Combo comboSolveMazeAlgo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		comboSolveMazeAlgo.setItems(SOLUTION_TYPES);
		comboSolveMazeAlgo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		comboSolveMazeAlgo.select(1);
		
		Button saveProperties = new Button(shell, SWT.PUSH);
		shell.setDefaultButton(saveProperties);
		saveProperties.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		saveProperties.setText("Save");
		
		saveProperties.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				
				String numOfThreads = txtNumOfThreads.getText();
				String genAlgorithm = comboGenMazeAlgo.getItem(comboGenMazeAlgo.getSelectionIndex());
				String solveAlgorithm = comboSolveMazeAlgo.getItem(comboSolveMazeAlgo.getSelectionIndex());
				
				setChanged();
				notifyObservers("save_properties " + numOfThreads + " " + genAlgorithm + " " + solveAlgorithm);
				
				shell.close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) { }
		});
		shell.open();
	}
	
	/**
	 * Given a new Position, this method will set the
	 * character position.
	 * 
	 * @param position The desired location for the character.
	 */
	@Override
	public void moveCharacter(Position position) {
		mazeDisplay.getCharacter().setPos(position);
	}
	
	@Override
	public void exit() {
		display.asyncExec(new Runnable() {
			public void run() {
				if (!shell.isDisposed())
					shell.dispose();
			}
		});
	}
}