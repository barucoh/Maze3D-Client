package presenter;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

import model.Model;
import view.View;

/**
 * Presenter
 * <p>This is the "bridge" between our view and model.
 * It will get notification from both and send them to the right location to be executed.</p>
 * 
 * @author Afik & Ohad
 * @see View
 * @see Model
 */
public class Presenter implements Observer {
	private Model model;
	private View view;
	private CommandsManager commandsManager;
	private HashMap<String, Command> cliMapper;
	
	public Presenter(Model model, View view) {
		this.model = model;
		this.view = view;
		
		commandsManager = new CommandsManager(model, view);
		cliMapper = commandsManager.getCommandsMap();
	}
	
	/**
	 * This method will check if the given command
	 * exists in the commands manager or throws an
	 * exception incase its not.
	 * 
	 * @param o The instance of the observable
	 * @param args The actual command.
	 */
	@Override
	public void update(Observable o, Object arg) {
		//System.out.println(arg);
	    Command command = null;
		
		/*if (!cliMapper.containsKey(cmdStr)) {
			view.displayMessage("Command doesn't exist");
			//command = cliMapper.get("print_menu");
		}*/
		if (o == view) {
			//Coming from View
		    String input = (String)arg;
		    String cmdStr = input.split(" ")[0];
	        String[] argsStr = null;
	        
	        argsStr = input.substring(input.indexOf(" ") + 1).split(" ");
			command = cliMapper.get(cmdStr);
			command.doCommand(argsStr);
		}
		else if (o == model) { // || cliMapper.get(cmdStr).isVisible()) {
	        //Coming from Model (Server)
			Object [] argArr = (Object[])arg;
			String cmdObj = (String)argArr[0];
			Object [] argsObj = new Object[argArr.length - 1];
			
			for (int i = 1; i < argArr.length; i++)
				argsObj[i - 1] = argArr[i];
			command = cliMapper.get((String)cmdObj);
			command.doCommand(argsObj);
		}
	}
}
