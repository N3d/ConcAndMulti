package data_structures;



public interface Sorted<T extends Comparable<T>> {
	
	// debugging flag
	static boolean DEBUG = false;
	
	public void add(T t);
	
	public void remove(T t);
	
	@Override
	public String toString();
}
