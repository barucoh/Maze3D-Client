package presenter;

import presenter.Command;

/**
 * CommonCommand
 * <p>Defines common method the every command must have.</p>
 * 
 * @author Afik & Ohad
 * @see Command
 */
public abstract class CommonCommand implements Command {
    protected boolean visible;
    
    @Override
    public boolean isVisible() {
    	return this.visible;
    }

    @Override
    public void setVisibility(boolean visible) {
    	this.visible = visible;
    }
}
