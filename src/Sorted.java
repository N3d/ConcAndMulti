
public interface Sorted<T extends Comparable<T>> {
	
	public boolean add(T t);
	
	public boolean remove(T t);
	
	public String toString();
	
}
