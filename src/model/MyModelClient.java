package model;

import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import algorithms.IO.MyCompressorOutputStream;
import algorithms.IO.MyDecompressorInputStream;
import algorithms.demo.Maze3DSearchable;
import algorithms.mazeGenerators.Maze3D;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.SimpleMaze3DGenerator;
import algorithms.search.Solution;
import presenter.Properties;
import presenter.PropertiesLoader;
import presenter.PropertiesSaver;
import view.View;

/**
 * MyModel
 * <p>Implements all of Model's functions and handling all the program's backend tasks and IO using multi threading.</p>
 * 
 * @author Afik & Ohad
 * @see View
 * @see Controller
 */
public class MyModelClient extends Observable implements Model {
    NetworkHandler networkHandler;
    
    private Map<String, Maze3DSearchable<Position>> mazes;
    private Map<String, Solution<Position>> solutions;
    private Map<String, Integer> mazeClues;
    private String clientIdentification;
    
    public Properties properties;
    
	private ExecutorService executor;

    public MyModelClient() {
    	PropertiesSaver.getInstance();		
        this.mazes = new ConcurrentHashMap<>();
        this.solutions = new HashMap<>();
        this.mazeClues = new HashMap<String, Integer>();
    }
    
    public void setNetworkHandler(NetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
    	this.clientIdentification = networkHandler.getSocket().getInetAddress().getHostName() + "-" + networkHandler.getSocket().getLocalPort();
		try {
			properties = PropertiesLoader.getInstance(clientIdentification).getProperties();
			executor = Executors.newFixedThreadPool(properties.getNumOfThreads());
		}
		catch (Exception ex) {
			properties = new Properties();
			properties.setGenerateMazeAlgorithm("Growing_Tree");
			properties.setNumOfThreads(10);
			properties.setSolveMazeAlgorithm("BFS");
			executor = Executors.newFixedThreadPool(50);
		}
    }
    
    public void updateFromServer(Object objRecieved) {
    	setChanged();
    	notifyObservers(objRecieved);
    }
    
    public Properties getProperties() {
    	return properties;
    }
    
	@Override
    public void generateMazeGrowingTree(String name, int cols, int rows, int layers) {
		executor.execute(new Runnable() {
			@Override
			public void run() {
				Object [] objToSend;
		        objToSend = new Object[6];
		        objToSend[0] = "generate_3d_maze";
		        objToSend[1] = name;
		        objToSend[2] = cols;
		        objToSend[3] = rows;
		        objToSend[4] = layers;
		        objToSend[5] = properties.getGenerateMazeAlgorithm();
	    		networkHandler.sendToServer(objToSend);
			}
		});
    }

	@Override
    public void generateMazeSimple(String name, int cols, int rows, int layers) {
		executor.execute(new Runnable() {
			@Override
			public void run() {				
				Object [] objToSend;
		        objToSend = new Object[6];
		        objToSend[0] = "generate_3d_maze";
		        objToSend[1] = name;
		        objToSend[2] = cols;
		        objToSend[3] = rows;
		        objToSend[4] = layers;
		        objToSend[5] = properties.getGenerateMazeAlgorithm();
	    		networkHandler.sendToServer(objToSend);
			}
		});
    }
    
    @Override
    public void solveMaze(String mazeName, String strategy) {
    	try {
    		if (solutions.get(mazeName) != null) { }
    		else {
    			mazeClues.put(mazeName, 0);
    			executor.execute(new Runnable() {
					@Override
					public void run() {
						Object [] objToSend;
				        objToSend = new Object[3];
				        objToSend[0] = "solve";
				        objToSend[1] = mazeName;
				        objToSend[2] = strategy;
			    		networkHandler.sendToServer(objToSend);
					}
    			});
			}
    	}
    	catch (Exception ex) {
    		ex.printStackTrace();
    	}
    }

    @Override
    public Maze3D getMaze(String name) throws NullPointerException{
		return mazes.get(name).getMaze();
    }
    @Override
    public void addMaze(String name, Maze3D maze) {
        mazes.put(name, new Maze3DSearchable<Position>(maze));
    }

    @Override
    public void exit() throws InterruptedException {
		PropertiesSaver.saveProperties(clientIdentification, this.properties);
        //saveMazesAndSolutions(this.properties.getMazeSolutionsFileName());
        
        this.executor.shutdown();
        this.executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
        this.networkHandler.terminateClient();
    }
    
    @Override
    public void saveMaze(String mazeName, String fileName) {
        SaveMazeRunnable saveMazeRunnable = new SaveMazeRunnable(mazeName, fileName);
        executor.execute(saveMazeRunnable);
    }
    @Override
    public void saveMazesAndSolutions(String fileName) {
		SaveMazeSolutionsRunnable saveMazeSolutionsRunnable = new SaveMazeSolutionsRunnable(fileName);
        executor.execute(saveMazeSolutionsRunnable);
    }
    @Override
    public void loadMaze(String name, String fileName) {
        LoadMazeRunnable loadMazeRunnable = new LoadMazeRunnable(name, fileName);
        executor.execute(loadMazeRunnable);
    }
	public void loadMazesAndSolutions(String fileName) {
		LoadMazeSolutionsRunnable loadMazeSolutionsRunnable = new LoadMazeSolutionsRunnable(fileName);
        executor.execute(loadMazeSolutionsRunnable);
	}
    
    public void getCrossSection(String name, String section, int index) {
    	Object[] objToSend;
    	
    	if (mazes.get(name) != null) {
	        objToSend = new Object[4];
	        objToSend[0] = name;
	        section = section.toUpperCase();
	        switch (section) {
	            case "X": 
	            	{
	            		objToSend[1] = mazes.get(name).getMaze().getCrossSectionByX(index);
	            		objToSend[2] = mazes.get(name).getMaze().getCrossSectionByX(index + 1);
	            		objToSend[3] = mazes.get(name).getMaze().getCrossSectionByX(index - 1);
	            	}
	                break;
	            case "Y": 
	            	{
	            		objToSend[1] = mazes.get(name).getMaze().getCrossSectionByY(index);
	            		objToSend[2] = mazes.get(name).getMaze().getCrossSectionByY(index + 1);
	            		objToSend[3] = mazes.get(name).getMaze().getCrossSectionByY(index - 1);
	            	}
	                break;
	            case "Z": 
	            	{
	            		objToSend[1] = mazes.get(name).getMaze().getCrossSectionByZ(index);
	            		objToSend[2] = mazes.get(name).getMaze().getCrossSectionByZ(index + 1);
	            		objToSend[3] = mazes.get(name).getMaze().getCrossSectionByZ(index - 1);
	            	}
	                break;
	            default:
	            	objToSend[1] = new int[0][0];
	        }
	        setChanged();
	        notifyObservers(objToSend);
    	}
    	else {
	        objToSend = new Object[3];
	        objToSend[0] = name;
	        objToSend[1] = section;
	        objToSend[2] = index;
    		this.networkHandler.sendToServer(objToSend);
    	}
    }
    
    private static void close(Closeable c) {
		if (c == null) return;
		try {
		    c.close();
		} catch (IOException e) {
		    e.printStackTrace();
		}
    }
    
    @Override
    public void setSolution(String name, Solution<Position> solution) {
        solutions.put(name, solution);
    }
    
    @Override
    public void getSolution(String name) {
		Object [] objToSend;
        objToSend = new Object[2];
        objToSend[0] = "display_solution";
        objToSend[1] = name;
		networkHandler.sendToServer(objToSend);
    }
    
    @Override
    public void getClue(String name) {
		Object [] objToSend;
        objToSend = new Object[2];
        objToSend[0] = "get_clue";
        objToSend[1] = name;
		networkHandler.sendToServer(objToSend);
    }
    
    @Override
    public void saveProperties(Properties properties) {
    	this.properties = properties;
    	PropertiesSaver.saveProperties(clientIdentification, properties);
    	setChanged();
    	Object [] objToSend = new Object[1];
    	objToSend[0] = "properties_saved";
    	notifyObservers(objToSend);
    }
    @Override
    public void loadProperties() {
		try {
			properties = PropertiesLoader.getInstance(clientIdentification).getProperties();		
			executor = Executors.newFixedThreadPool(properties.getNumOfThreads());
		} catch (Exception ex) {
			ex.printStackTrace();
		}		
    	setChanged();
    	notifyObservers("properties_loaded");
    }
    
    class SaveMazeRunnable implements Runnable {

        private String mazeName;
        private String fileName;
        MyCompressorOutputStream out = null;

        public SaveMazeRunnable(String mazeName, String fileName) {
            this.mazeName = mazeName;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            if (mazes.containsKey(mazeName)) {
                try {
                    out = new MyCompressorOutputStream(new FileOutputStream(fileName));
                    byte[] arr = mazes.get(mazeName).getMaze().toByteArray();
                    out.write(arr.length / 255);
                    out.write(arr.length % 255);
                    out.write(arr);
            		Object [] objToSend;
                    objToSend = new Object[3];
                    objToSend[0] = "maze_saved";
                    objToSend[1] = mazeName;
                    objToSend[2] = fileName;
    	            setChanged();
    	            notifyObservers(objToSend);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    close(out);
                }
            } else {
        		Object [] objToSend;
                objToSend = new Object[2];
                objToSend[0] = "maze_not_found";
                objToSend[1] = mazeName;
	            setChanged();
	            notifyObservers(objToSend);
            }
        }

        public void terminate() {
            out.setDone(true);
        }
    }
    class SaveMazeSolutionsRunnable implements Runnable {

        private String fileName;
        GZIPOutputStream out = null;

        public SaveMazeSolutionsRunnable(String fileName) {
            this.fileName = fileName;
        }

        @Override
        public void run() {
        	ObjectOutputStream oos = null;
    		try {
    		    oos = new ObjectOutputStream(new GZIPOutputStream(new FileOutputStream("solutions.dat")));
    			oos.writeObject(mazes);
    			oos.writeObject(solutions);
                setChanged();
                notifyObservers("mazes_solutions_saved " + fileName);
    		} catch (FileNotFoundException e) {
                setChanged();
                notifyObservers("mazes_solutions_save_failed " + fileName);
    			e.printStackTrace();
    		} catch (IOException e) {
                setChanged();
                notifyObservers("mazes_solutions_save_failed " + fileName);
    			e.printStackTrace();
    		} finally {
    			try {
    				oos.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
        }
    }
    class LoadMazeRunnable implements Runnable {

        private String mazeName;
        private String fileName;
        MyDecompressorInputStream in = null;

        public LoadMazeRunnable(String mazeName, String fileName) {
            this.mazeName = mazeName;
            this.fileName = fileName;
        }

        @Override
        public void run() {
            if (!mazes.containsKey(mazeName)) {
                try {
                    in = new MyDecompressorInputStream(new FileInputStream(fileName));
                    int size = in.read() * 255;
                    size += in.read();
                    byte[] mazeInBytes = new byte[size];
                    in.read(mazeInBytes);
                    mazes.put(mazeName, new Maze3DSearchable<Position>(new Maze3D(mazeInBytes)));
            		Object [] objToSend;
                    objToSend = new Object[2];
                    objToSend[0] = "maze_loaded";
                    objToSend[1] = mazeName;
    	            setChanged();
    	            notifyObservers(objToSend);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    close(in);
                }
            } else{
        		Object [] objToSend;
                objToSend = new Object[2];
                objToSend[0] = "maze_already_exists";
                objToSend[1] = mazeName;
	            setChanged();
	            notifyObservers(objToSend);
            }
        }

        public void terminate() {
            in.setDone(true);
        }
    }
    class LoadMazeSolutionsRunnable implements Runnable {
        File file;
        GZIPOutputStream out = null;

        public LoadMazeSolutionsRunnable(String fileName) {
            file = new File(fileName);
        }

        @SuppressWarnings("unchecked")
		@Override
        public void run() {
    		if (!file.exists()) {
                setChanged();
                notifyObservers("mazes_solutions_load_failed " + file.getName());
    		}
    		ObjectInputStream ois = null;
    		try {
    			ois = new ObjectInputStream(new GZIPInputStream(new FileInputStream("solutions.dat")));
    			mazes = (Map<String, Maze3DSearchable<Position>>)ois.readObject();
    			solutions = (Map<String, Solution<Position>>)ois.readObject();
                setChanged();
                notifyObservers("mazes_solutions_loaded");	
    		} catch (FileNotFoundException e) {
    			e.printStackTrace();
                setChanged();
                notifyObservers("mazes_solutions_load_failed " + file.getName());
    		} catch (IOException e) {
    			e.printStackTrace();
                setChanged();
                notifyObservers("mazes_solutions_load_failed " + file.getName());
    		} catch (ClassNotFoundException e) {
    			e.printStackTrace();
                setChanged();
                notifyObservers("mazes_solutions_load_failed " + file.getName());
    		} finally{
    			try {
    				ois.close();
    			} catch (IOException e) {
    				e.printStackTrace();
    			}
    		}
        }
    }
}