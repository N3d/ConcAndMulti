package data_structures.implementation;
import java.util.concurrent.locks.Lock;
import data_structures.Sorted;


/**
 * @author Matteo Casenove and Nicola Mularoni
 * 
 * This class represents the structure Coarse Grained Tree.
 *
 */

public class CoarseGrainedTree<T extends Comparable<T>> implements Sorted<T> {

	private Node<T> root;

	private Lock lock;

	public CoarseGrainedTree(){
		this.root=null;
	}

	@Override
	public void add(T t) {
		Node<T> newNode = new Node<T>(t);
		lock.lock();
		try{
			if(t == null)
				return;

			if(root == null)
				this.root=newNode;

			 recoursiveAdd(root,root, newNode);
		}finally{
			lock.unlock();
		}

	}

	private boolean recoursiveAdd(Node<T> parent,Node<T> index,Node<T> newNode){
		if(parent == null || index == null || newNode == null)
			return false;

		if(newNode.compareTo(index)==0){
			//the new node will take the place of the 
			// node with the same value
			newNode.left=index;
			// the right subtree of the current node
			// will be the new right subtree of the new node
			newNode.right=index.right; 
			// Update the parent child with the new node
			if(index.equals(parent.left))
				parent.left=newNode;
			else
				parent.right=newNode;
			// update the current node
			index.right=null;
		}
		if( newNode.compareTo(index)<0){
			if(index.left == null)
				index.left = newNode;
			else
				return recoursiveAdd(index,index.left, newNode);
		}
		if( newNode.compareTo(index)>0){
			if(index.right == null)
				index.right = newNode;
			else
				return recoursiveAdd(index,index.right, newNode);
		}
		return true;
	}

	@Override
	public void remove(T t) {
		Node<T> node = new Node<T>(t);
		lock.lock();
		try{

			if(root == null)
				return;

			recoursiveRemove(root,root, node);
		}finally{
			lock.unlock();
		}
	}

	private boolean recoursiveRemove(Node<T> parent,Node<T> index,Node<T> newNode){
		if(parent == null || index == null || newNode == null)
			return false;

		if(newNode.compareTo(index)==0){
			//check if the it can remove the node easly.
			if(index.left==null){
				if(index.equals(parent.left))
					parent.left=index.right;
				else
					parent.right=index.right;
			}else if(index.right==null){
				if(index.equals(parent.left))
					parent.left=index.left;
				else
					parent.right=index.left;
			}else{
				//search for the first left leaf on its right subtree
				Node<T> substitute = substituteLookUp(index, index.right);
				//the substitute node takes its place.
				substitute.left=index.left;
				substitute.right=index.right;
				//update the parent's child with the substitute
				if(index.equals(parent.left))
					parent.left=substitute;
				else
					parent.right=substitute;
				return true;
			}
		}
		//recursively looks for the node
		if( newNode.compareTo(index)<0){
			if(index.left == null)
				return false;
			else
				return recoursiveRemove(index,index.left, newNode);
		}
		if( newNode.compareTo(index)>0){
			if(index.right == null)
				return false;
			else
				return recoursiveRemove(index,index.right, newNode);
		}
		return true;
	}

	private Node<T> substituteLookUp(Node<T> parent,Node<T> node){
		if(parent == null || node == null)
			return null;
		if(node.isLeaf()){
			if(node.equals(parent.left))
				parent.left=null;
			else
				parent.right=null;
			return node;
		}else{
			return substituteLookUp(node, node.left);
		}
	}

	public String toString(){
		// it is a debugging method so it doesn't need to lock the structure.
		Node<T> node=this.root;
		if(node==null)
			return "[]";
		return "["+node.getValue()+recursivePrint(node.left)+recursivePrint(node.right)+"]";
	}
	
	private String recursivePrint(Node<T> node){
		if(node==null)
			return "";
		return "["+node.getValue()+recursivePrint(node.left)+recursivePrint(node.right)+"]";
	}

}
