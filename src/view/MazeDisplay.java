package view;

import java.io.FileInputStream;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;

import algorithms.mazeGenerators.Position;

/**
 * MazeDisplay
 * <p>Main display canvas widget.</p>
 * <p>This display will be rendered on every </p>
 * <p>event that affects the main canvas</p>
 * 
 * @author Afik & Ohad
 * @see Canvas
 */
public class MazeDisplay extends Canvas {
	
	private int[][] mazeData = new int[0][0];
	private int[][] mazeDataFloorUp = new int[0][0];
	private int[][] mazeDataFloorDown = new int[0][0];
	
	private Character character;
	private Image stairs, entrance, goal;
	
	/**
	 * In order to generate the maze correctly,
	 * we need the maze floor for the user to play in, 
	 * and the above and below floors to show stairs to 
	 * the user
	 * 				
	 * @param mazeData	The current floor cross section
	 * @param mazeDataFloorUp	The floor above the character (To show stairs if necessary)
	 * @param mazeDataFloorDown	The floor below the character (To show stairs if necessary)
	 */
	public void setMazeData(int[][] mazeData, int [][] mazeDataFloorUp, int [][] mazeDataFloorDown) {
		try {
			stairs = new Image(null, new FileInputStream("assets/2D_stairs.png"));//getClass().getClassLoader().getResourceAsStream("assets/2D_stairs.png"));
			entrance = new Image(null, new FileInputStream("assets/entrance.png"));//getClass().getClassLoader().getResourceAsStream("assets/entrance.png"));
			goal = new Image(null, new FileInputStream("assets/goal.jpg"));//getClass().getClassLoader().getResourceAsStream("assets/goal.jpg"));
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
		this.mazeData = mazeData;
		this.mazeDataFloorUp = mazeDataFloorUp;
		this.mazeDataFloorDown = mazeDataFloorDown;
		this.redraw();
	}
	
	/**
	 * Given a level, this method will return it (if available)
	 * Otherwise, we return the above floor.
	 * 
	 * @param level	What level in the maze you wish to get
	 * @return	An array that represents the requested floor
	 */
	public int[][] getMazeData(int level) {
		if (level == 0)
			return this.mazeDataFloorDown;
		else if (level == 1)
			return this.mazeData;
		else
			return this.mazeDataFloorUp;
	}
	
	public MazeDisplay(Composite parent, int style) {
		super(parent, style);
		
		character = new Character();
		character.setPos(new Position(1, 1, 1));
	}
	
	/**
	 * Based on position object, this method will set
	 * the character initial position
	 * 
	 * @param startPos Position instance for the Character location
	 */
	public void setCharacterStartPosition(Position startPos) {
		character = new Character();
		character.setPos(startPos);
	}
	
	/**
	 * Draws the character on the canvas an the
	 * given position
	 * 
	 * @param w Width of the widget
	 * @param h Height of the widget
	 * @param gc 
	 */
	public void drawCharacterPosition(int w, int h, GC gc) {
		character.draw(w, h, gc);
	}
	
	public Character getCharacter(){
		return this.character;
	}
	
	public Position getCharacterPos() {
		return character.getPos();
	}
	
	/**
	 * Draw the stairs image to indicate to the user
	 * that he can go up or down in the maze
	 * 
	 * @param x	Maze row
	 * @param y Maze Column
	 * @param w Canvas width
	 * @param h Canvas height
	 * @param gc 
	 */
	public void drawStairs(int x, int y, int w, int h, GC gc) {
		gc.drawImage(stairs, 0, 0, stairs.getBounds().width, stairs.getBounds().height, 
				w * x, h * y, w, h);
	}
	
	/**
	 * Draw the entrance image.
	 * 
	 * @param x	Maze row
	 * @param y Maze Column
	 * @param w Canvas width
	 * @param h Canvas height
	 * @param gc 
	 */
	public void drawEntrance(int x, int y, int w, int h, GC gc) {
		gc.drawImage(entrance, 0, 0, entrance.getBounds().width, entrance.getBounds().height, 
				w * x, h * y, w, h);
	}
	
	/**
	 * Draw the Goal position image.
	 * 
	 * @param x	Maze row
	 * @param y Maze Column
	 * @param w Canvas width
	 * @param h Canvas height
	 * @param gc 
	 */
	public void drawGoal(int x, int y, int w, int h, GC gc) {
		gc.drawImage(goal, 0, 0, goal.getBounds().width, goal.getBounds().height, 
				w * x, h * y, w, h);
	}
}
