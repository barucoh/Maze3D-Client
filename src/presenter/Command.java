package presenter;

/**
 * Command interface
 * <p>An interface to implement in case a new Command needs to be programmed</p>
 *
 * @author Afik & Ohad
 * @version 1.0
 * @see Command
 */
public interface Command {
    void doCommand(String[] args);
    void doCommand(Object[] args);
    public boolean isVisible();
    public void setVisibility(boolean visible);
}