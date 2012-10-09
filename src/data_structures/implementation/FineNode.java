package data_structures.implementation;

import java.util.concurrent.locks.ReentrantLock;

public class FineNode<T extends Comparable<T>> {

	private ReentrantLock lock;
	private T value;
	private FineNode<T> left;
	private FineNode<T> right;
	private FineNode<T> parent;

	public FineNode() {
		this.value = null;
		this.left = null;
		this.right = null;
		this.parent = null;
		lock = new ReentrantLock();
	}

	public FineNode(T value) {
		this();
		this.setValue(value);
	}
	
	public FineNode(T value, FineNode<T> parent) {
		this();
		this.setValue(value);
		this.parent = parent;
	}

	public FineNode(T value, FineNode<T> left, FineNode<T> right) {
		this();
		this.setValue(value);
		this.left = left;
		this.right = right;
	}

	public FineNode<T> getLeft() {
		return left;
	}

	public void setLeft(FineNode<T> left) {
		this.left = left;
	}

	public FineNode<T> getRight() {
		return right;
	}

	public void setRight(FineNode<T> right) {
		this.right = right;
	}

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

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public ReentrantLock getLock() {
		return lock;
	}

}
