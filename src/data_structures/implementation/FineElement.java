package data_structures.implementation;

import java.util.concurrent.locks.ReentrantLock;

public class FineElement<T extends Comparable<T>> extends Element<T>{
	
	private ReentrantLock lock;

	public FineElement() {
		super();
		this.lock = new ReentrantLock();
	}
	
	public FineElement(T t){
		super(t);
		this.lock = new ReentrantLock();
	}
	
	public void lock(){
		this.lock.lock();
	}
	
	public void unlock(){
		this.lock.unlock();
	}

}
