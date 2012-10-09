package data_structures.implementation;

import java.util.concurrent.locks.ReentrantLock;

public class FineNode<T extends Comparable<T>> extends Node<T> {

	private final ReentrantLock lock;
	public FineNode<T> left;
	public FineNode<T> right;
	public FineNode<T> parent;

	public FineNode() {
		super();
		lock = new ReentrantLock();
	}

	public FineNode(T t) {
		super(t);
		lock = new ReentrantLock();
	}
	
	public FineNode(T t, FineNode<T> parent) {
		super(t, parent);
		lock = new ReentrantLock();
	}

	public FineNode(T value, FineNode<T> left, FineNode<T> right) {
		super(value, left, right);
		lock = new ReentrantLock();
	}

	public void lock() {
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}

}
