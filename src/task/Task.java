package task;

/**
 * Interface which is used to execute various tasks the script must perform.
 * A subclass of this must implement the activate() and execution methods. When activate is true
 * for this task execute is called. Logic pertaining to the execution of this task should be in the #execute() method
 * while the logic to tell the script when this should execute should be stored in #activate(). This class
 * is evaluated in the primary loop for the script.
 * Created by cbartram on 2019-08-09.
 * http://github.com/cbartram
 */

public interface Task {
	String getStatus();
	boolean activate() throws InterruptedException;
	void execute() throws InterruptedException; // Throws exception so we can non conditionally sleep within the script
}