package model;

/**
 * Represents an exceptional situation specific to this project.
 * 
 */
@SuppressWarnings("serial")
public class TivooException extends RuntimeException {
 
    public static enum Type {
	BAD_FORMAT, BAD_DATA;
    }

    private Type myType;

    /**
     * Create exception with given meesage
     * 
     * @param message
     *            explaination of problem
     */
    public TivooException(String message) {
	this(message, Type.BAD_FORMAT);
    }

    public TivooException(String message, Type type) {
	super(message);
	myType = type;
    }

    public Type getType() {
	return myType;
    }
    
}
