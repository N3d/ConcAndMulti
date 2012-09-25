package data_structures.implementation;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
	
	private Element<T> head;
	
	public FineGrainedList(){
		this.head=null;
	}

	public void add(T t) {
		throw new UnsupportedOperationException();
	}

	public void remove(T t) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		throw new UnsupportedOperationException();
	}
}
