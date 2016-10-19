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
import algorithms.demo.MazeSearcherFactory;
import algorithms.mazeGenerators.ChooseRandomNode;
import algorithms.mazeGenerators.GrowingTreeGenerator3D;
import algorithms.mazeGenerators.Maze3D;
import algorithms.mazeGenerators.Position;
import algorithms.mazeGenerators.SimpleMaze3DGenerator;
import algorithms.search.Searcher;
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
public class MyModel extends Observable implements Model {
    NetworkHandler networkHandler;
    
    private Map<String, Maze3DSearchable<Position>> mazes;
    private Map<String, Solution<Position>> solutions;
    private Map<String, Integer> mazeClues;
    
    public Properties properties;
    
	private ExecutorService executor;

    public MyModel() {
    	PropertiesSaver.getInstance();
		properties = PropertiesLoader.getInstance().getProperties();
		if (properties != null) {
			executor = Executors.newFixedThreadPool(properties.getNumOfThreads());
			//loadMazesAndSolutions(properties.getMazeSolutionsFileName());
		}
		else
			executor = Executors.newFixedThreadPool(50);
		
        this.mazes = new ConcurrentHashMap<>();
        this.solutions = new HashMap<>();
        this.mazeClues = new HashMap<String, Integer>();
    }
    
    public void setNetworkHandler(NetworkHandler networkHandler) {
		this.networkHandler = networkHandler;
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
		        objToSend = new Object[5];
		        objToSend[0] = "generate_3d_maze";
		        objToSend[1] = name;
		        objToSend[2] = cols;
		        objToSend[3] = rows;
		        objToSend[4] = layers;
	    		networkHandler.sendToServer(objToSend);
				/*GrowingTreeGenerator3D generator = new GrowingTreeGenerator3D(new ChooseRandomNode());
	            Maze3D maze = generator.generate(cols, rows, layers);
	            mazes.put(name, new Maze3DSearchable<Position>(maze));
				setChanged();
				notifyObservers("maze_ready " + name);
				return name;*/
			}
		});
    }

	@Override
    public void generateMazeSimple(String name, int cols, int rows, int layers) {
		executor.submit(new Callable<String>() {
			@Override
			public String call() throws Exception {
				SimpleMaze3DGenerator generator = new SimpleMaze3DGenerator();
	            Maze3D maze = generator.generate(cols, rows, layers);
	            mazes.put(name, new Maze3DSearchable<Position>(maze));
				setChanged();
				notifyObservers("maze_ready " + name);
				return name;
			}
		});
    }
    
    @Override
    public void solveMaze(String mazeName, String strategy) {
    	try {
    		if (solutions.get(mazeName) != null) { }
    		else {
    			mazeClues.put(mazeName, 0);
    			executor.submit(new Callable<Solution<Position>>() {
					@Override
					public Solution<Position> call() throws Exception {
				        Maze3DSearchable<Position> maze = mazes.get(mazeName);
				        Searcher<Position> searcher;
			            MazeSearcherFactory msf = new MazeSearcherFactory();
			            searcher = msf.getSearcher(strategy);
		
			            Solution<Position> sol = searcher.search(maze);
			            setChanged();
			            notifyObservers("solution_ready " + mazeName);
			            solutions.put(mazeName, sol);
			            return sol;
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
    public void exit() throws InterruptedException {
		PropertiesSaver.saveProperties(this.properties);
        //saveMazesAndSolutions(this.properties.getMazeSolutionsFileName());
        
        this.executor.shutdown();
        this.executor.awaitTermination(5000, TimeUnit.MILLISECONDS);
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
    public Solution<Position> getSolution(String name) {
    	return solutions.get(name);
    }
    
    @Override
    public Position getClue(String name) {
    	mazeClues.put(name, mazeClues.get(name).intValue() + 1);
    	return solutions.get(name).getStates().get(mazeClues.get(name)).getValue();
    }
    
    @Override
    public void saveProperties(Properties properties) {
    	this.properties = properties;
    	PropertiesSaver.saveProperties(properties);
    	setChanged();
    	notifyObservers("properties_saved");
    }
    @Override
    public void loadProperties() {
		properties = PropertiesLoader.getInstance().getProperties();
		executor = Executors.newFixedThreadPool(properties.getNumOfThreads());
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
    	            setChanged();
    	            notifyObservers("maze_saved " + mazeName + " " + fileName);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    close(out);
                }
            } else {
	            setChanged();
	            notifyObservers("maze_not_found " + mazeName);
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
    	            setChanged();
    	            notifyObservers("maze_loaded " + mazeName);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                } catch (IOException ex) {
                    ex.printStackTrace();
                } finally {
                    close(in);
                }
            } else{
	            setChanged();
	            notifyObservers("maze_already_exists " + mazeName);
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