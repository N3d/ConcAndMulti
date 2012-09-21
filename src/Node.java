/**
 * @author Matteo Casenove and Nicola Mularoni
 * 
 * This class represents the generic node in the tree.
 *
 */

public class Node<T> {
	
	private T value;
	private Node<T> left;
	private Node<T> right;
	
	public Node(){
		this.value=null;
		this.left=null;
		this.right=null;
	}
	
	public Node(T value,Node<T> left,Node<T> right){
		this.value=value;
		this.left=left;
		this.right=right;
	}
	
	public T getValue(){
		return this.value;
	}
	
	public Node<T> getLeft(){
		return this.left;
	}
	
	public Node<T> getRight(){
		return this.right;
	}
	
	public void setValue(T value){
		this.value=value;
	}
	
	public void setLeft(Node<T> left){
		this.left=left;
	}
	
	public void setRight(Node<T> right){
		this.right=right;
	}
	
}
