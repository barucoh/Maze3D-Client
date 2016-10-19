package view;

import java.util.Observable;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

/**
 * DialogWindow
 * <p>A new shell for a dialog window.</p>
 * <p>This window is a SWT widget.</p>
 * 
 * @author Afik & Ohad
 *
 */
public abstract class DialogWindow extends Observable {
	protected Shell shell;	
	
	protected abstract void initWidgets();
	
	public void start(Display display) {		
		shell = new Shell(display);
		
		initWidgets();
		shell.open();		
	}
}
