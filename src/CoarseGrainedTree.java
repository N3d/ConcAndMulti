import java.util.concurrent.locks.Lock;


/**
 * @author Matteo Casenove and Nicola Mularoni
 * 
 * This class represents the structure Coarse Grained Tree.
 *
 */

public class CoarseGrainedTree<T extends Comparable<T>> implements AbstractConcurrentStructureTree<T> {

	private Node<T> root;

	private Lock lock;

	public CoarseGrainedTree(){
		this.root=null;
	}

	@Override
	public boolean add(Node<T> node) {
		lock.lock();
		try{
			if(node == null)
				return false;

			if(root == null)
				this.root= node;

			return recoursiveAdd(root, node);
		}finally{
			lock.unlock();
		}

	}

	private boolean recoursiveAdd(Node<T> index,Node<T> newNode){
		if(index == null || newNode == null)
			return false;
	
		if(newNode.compareTo(index)==0){
			//do something
		}
		if( newNode.compareTo(index)<0){
			if(index.left == null)
				index.left = newNode;
			else
				return recoursiveAdd(index.left, newNode);
		}
		if( newNode.compareTo(index)>0){
			if(index.right == null)
				index.right = newNode;
			else
				return recoursiveAdd(index.right, newNode);
		}
		return true;
	}

	@Override
	public boolean remove(T value) {
		return false;
	}

	public String toString(){
		return null;

	}


}
