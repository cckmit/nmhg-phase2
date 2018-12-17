package tavant.twms.integration.layer.component.global.exception;

public class GlobalSyncException extends Exception{

	
	private static final long serialVersionUID = -7361913297585192637L;

	public GlobalSyncException(String message) {
        super(message);
    }
	
	 public GlobalSyncException(String message, Throwable cause) {
	        super(message, cause);
	    }

	    /**
	     * @param cause
	     */
	    public GlobalSyncException(Throwable cause) {
	        super(cause);
	    }
}
