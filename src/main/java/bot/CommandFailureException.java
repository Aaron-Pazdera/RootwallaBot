package bot;

@SuppressWarnings("serial")
public class CommandFailureException extends Exception {
	
	public CommandFailureException(String message) 
    { 
        super(message); 
    } 
}
