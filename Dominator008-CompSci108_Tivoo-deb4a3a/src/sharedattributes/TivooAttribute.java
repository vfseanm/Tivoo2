package sharedattributes;

public abstract class TivooAttribute {

    public abstract String toString();

    public boolean equals(Object o) {
	TivooAttribute t = (TivooAttribute) o;
	return toString().equals(t.toString());
    }

    public int hashCode() {
	return toString().hashCode();
	
    }
    
}