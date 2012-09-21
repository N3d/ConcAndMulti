
/**
 * @author Matteo Casenove and Nicola Mularoni
 * 
 * This class represents the structure Coarse Grained Tree.
 *
 */

public class CoarseGrainedTree<T> implements AbstractConcurrentStructureTree<T> {
	
	private Node<T> root;
	
	public CoarseGrainedTree(){
		this.root=null;
	}

	@Override
	public boolean add(Node<T> node) {
		return false;
	}

	@Override
	public boolean remove(Object value) {
		return false;
	}
	
	public String toString(){
		return null;
		
	}

}
