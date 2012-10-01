package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import data_structures.Sorted;

public class CoarseGrainedList<T extends Comparable<T>> implements Sorted<T> {

	private Element<T> head;
	private Lock lock = new ReentrantLock();

	public CoarseGrainedList() {
		head = new Element<T>();
		head.next = new Element<T>();
	}

	public void add(T item) {
		Element<T> pred, curr;
		lock.lock();
		try {
			pred = head;
			curr = pred.next;
			while (curr.value != null && curr.value.compareTo(item) == -1) {
				pred = curr;
				curr = curr.next;
			}
			Element<T> elem = new Element<T>(item);
			elem.next = curr;
			pred.next = elem;
		} finally {
			lock.unlock();
		}
	}

	public void remove(T item) {
		Element<T> pred, curr;
		lock.lock();
		try {
			pred = head;
			curr = pred.next;
			while (curr.getValue().compareTo(item) == -1) {
				pred = curr;
				curr = curr.next;
			}
			pred.next = curr.next;
		} finally {
			lock.unlock();
		}
	}

	public String toString() {
		return this.toArrayList().toString();
	}

	private ArrayList<T> toArrayList() {
		Element<T> pred, curr;
		ArrayList<T> array = new ArrayList<T>();
//		lock.unlock();
//		try {
			pred = head;
			curr = pred.next;
			while (curr.next != null) {
				array.add(curr.value);
				pred = curr;
				curr = curr.next;
			}

			return array;
//		} finally {
//			lock.unlock();
//		}
	}
}
