package view;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.MessageBox;
import org.eclipse.swt.widgets.Text;

/**
 * GenerateMazeWindow
 * <p>This dialog will accept the required fields</p>
 * <p>from the user in order to generate a new maze.</p>
 * 
 * @author Afik & Ohad
 * @see DialogWindow
 */
public class GenerateMazeWindow extends DialogWindow {
	
	private static final String[] MAZE_TYPES = { "Simple", "Growing Tree" };
	
	
	@Override
	protected void initWidgets() {
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
		
		Label lblMazeType = new Label(shell, SWT.NONE);
		lblMazeType.setText("Maze type: ");
		
		Combo combo = new Combo(shell, SWT.DROP_DOWN | SWT.READ_ONLY);
		combo.setItems(MAZE_TYPES);
		combo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		
				
		Button btnGenerateMaze = new Button(shell, SWT.PUSH);
		shell.setDefaultButton(btnGenerateMaze);
		btnGenerateMaze.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		btnGenerateMaze.setText("Generate maze");
		
		btnGenerateMaze.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {	
				
				MessageBox msg = new MessageBox(shell, SWT.OK);
				msg.setText("Maze Generator");
				int rows = Integer.parseInt(txtRows.getText());
				int cols = Integer.parseInt(txtCols.getText());
				int layers = Integer.parseInt(txtLayers.getText());
				String name = txtName.getText();
				
				setChanged();
				notifyObservers("dslkjdlskd");
				
				msg.setMessage("The maze " +  name + " has been generated for you. \n \n Rows: " + rows + " Columns: " + cols + " Layers: " + layers);
				
				msg.open();
				shell.close();
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {			
				
			}
		});	
		
	}

}
