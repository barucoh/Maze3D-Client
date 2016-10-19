package presenter;

import java.io.Serializable;

/**
 * Properties
 * <p>Defines the maze properties of our application</p>
 * 
 * @author Afik & Ohad
 *
 */
public class Properties implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int numOfThreads;
	private String generateMazeAlgorithm;
	private String solveMazeAlgorithm;
	private String mazeSolutionsFileName;
	
	public Properties() {
		
	}
			
	public int getNumOfThreads() {
		return numOfThreads;
	}
	public void setNumOfThreads(int numOfThreads) {
		this.numOfThreads = numOfThreads;
	}
	public String getGenerateMazeAlgorithm() {
		return generateMazeAlgorithm;
	}
	public void setGenerateMazeAlgorithm(String generateMazeAlgorithm) {
		this.generateMazeAlgorithm = generateMazeAlgorithm;
	}
	public String getSolveMazeAlgorithm() {
		return solveMazeAlgorithm;
	}
	public void setSolveMazeAlgorithm(String solveMazeAlgorithm) {
		this.solveMazeAlgorithm = solveMazeAlgorithm;
	}

	public String getMazeSolutionsFileName() {
		return mazeSolutionsFileName;
	}

	public void setMazeSolutionsFileName(String mazeSolutionsFileName) {
		this.mazeSolutionsFileName = mazeSolutionsFileName;
	}
}
