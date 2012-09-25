package data_structures;



public interface Sorted<T extends Comparable<T>> {
	
	// debugging flag
	static boolean DEBUG = true;
	
	public void add(T t);
	
	public void remove(T t);
	
	public String toString();
}
