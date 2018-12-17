package tavant.twms.common;

public class NoValuesDefinedException extends RuntimeException {
	public NoValuesDefinedException(String param) {
		super("There are no values defined for:" + param);
	}
}
