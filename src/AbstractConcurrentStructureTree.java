
public interface AbstractConcurrentStructureTree<T extends Comparable<T>> {
	
	public boolean add(Node<T> node);
	
	public boolean remove(T value);
	
	public String toString();
	
}
