package view;

import java.util.Observable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * BaseWindow
 * <p>The game loop of our application.</p>
 * 
 * @author Afik & Ohad
 *
 */
public abstract class BaseWindow extends Observable {
	protected Display display;
	protected Shell shell;
	
	protected abstract void initWidgets();
	
	public void start() {
		display = new Display();
		shell = new Shell(display);
		
		shell.setSize(500, 400);
		
		initWidgets();
		shell.open();		
		
		// main event loop
		while(!shell.isDisposed()){ // window isn't closed
			if(!display.readAndDispatch()){
				display.sleep();
			}
		}
		setChanged();
		notifyObservers("exit");
		display.dispose();
	}
}
