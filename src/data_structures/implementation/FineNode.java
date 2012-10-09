package data_structures.implementation;

import java.util.concurrent.locks.ReentrantLock;

public class FineNode<T extends Comparable<T>> extends Node<T> {

	public final ReentrantLock lock;
	private FineNode<T> left;
	private FineNode<T> right;
	private FineNode<T> parent;

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

	@Override
	public FineNode<T> getLeft() {
		return left;
	}

	public void setLeft(FineNode<T> left) {
		this.left = left;
	}

	@Override
	public FineNode<T> getRight() {
		return right;
	}

	public void setRight(FineNode<T> right) {
		this.right = right;
	}

	@Override
	public FineNode<T> getParent() {
		return parent;
	}

	public void setParent(FineNode<T> parent) {
		this.parent = parent;
	}
	public void lock() {
		lock.lock();
	}

	public void unlock() {
		lock.unlock();
	}

}
