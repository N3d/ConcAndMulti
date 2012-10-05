package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

/**
 * @author Matteo Casenove and Carmine Paolino
 * 
 * This class represents the generic node in the LockFreeTree.
 *
 */

public class LockFreeNode<T extends Comparable<T>> extends Node<T> {
	
	public AtomicStampedReference<StateInfo> si; //stamped to avoid aba.
	public int expectedStamp=0;
	
    public AtomicStampedReference<LockFreeNode<T>> left;
	public AtomicStampedReference<LockFreeNode<T>> right;

	public LockFreeNode(T value) {
		super(value);
		si=new AtomicStampedReference<StateInfo>(new StateInfo(StateInfo.CLEAN,null),expectedStamp);
		left=new AtomicStampedReference<LockFreeNode<T>>(null,expectedStamp);
		right=new AtomicStampedReference<LockFreeNode<T>>(null,expectedStamp);
	}
	
	public boolean isLeaf(){
		return (left.getReference()==null && right.getReference()==null);
	}
	
	@Override
	public int compareTo(Node<T> o){
		return 0;
	}

}
