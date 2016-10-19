package view;

import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;

import algorithms.mazeGenerators.Position;

/**
 * Character
 * 	<p>Creates a new character for the maze game.</p>
 *  <p>This class requires an image called "character.jpg" to be presented on the assets directory</p>
 * 
 * @author Afik & Ohad
 *
 */
public class Character {
	private Position pos;
	public Image img;
	
	/**
	 * Initialize the Character with it's image
	 */
	public Character() {
		try {
			img = new Image(null, new FileInputStream("assets/character.jpg"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}//getClass().getClassLoader().getResourceAsStream("assets/character.jpg"));
	}

	public Position getPos() {
		return pos;
	}

	public void setPos(Position pos) {
		this.pos = pos;
	}
	
	/**
	 * Render the character on the canvas at the position.
	 * 
	 * @param cellWidth
	 * @param cellHeight
	 * @param gc
	 */
	public void draw(int cellWidth, int cellHeight, GC gc) {
		gc.drawImage(img, 0, 0, img.getBounds().width, img.getBounds().height, 
				cellWidth * pos.x, cellHeight * pos.y, cellWidth, cellHeight);
	}
	
	/**
	 * Controls the character movement
	 */
	public void moveRight() {
		pos.x++;
	}
	public void moveLeft() {
		pos.x--;
	}
	public void moveUp() {
		pos.y--;
	}
	public void moveDown() {
		pos.y++;
	}
}
