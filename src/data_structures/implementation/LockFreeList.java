package data_structures.implementation;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicMarkableReference;

import data_structures.Sorted;

public class LockFreeList<T extends Comparable<T>> implements Sorted<T> {

	private final LockFreeElement<T> head;

	public LockFreeList() {
		head = new LockFreeElement<T>(LockFreeElement.HEAD);
		LockFreeElement<T> tail = new LockFreeElement<T>(LockFreeElement.TAIL);
		while (!head.getNext().compareAndSet(null, tail, false, false));
	}

	@Override
	public void add(T t) {
		while (true) {
			Window<T> window = find(head, t);
			LockFreeElement<T> pred = window.getPred(), curr = window.getCurr();

			LockFreeElement<T> elem = new LockFreeElement<T>(t);
			elem.setNext(new AtomicMarkableReference<LockFreeElement<T>>(curr, false));
			if (pred.getNext().compareAndSet(curr, elem, false, false)) {
				return;
			}
		}
	}

	@Override
	public void remove(T t) {
		boolean snip;
		while (true) {
			Window<T> window = find(head, t);
			LockFreeElement<T> pred = window.getPred(), curr = window.getCurr();

			if (t.compareTo(curr.getValue()) != 0) {
				return;
			} else {
				LockFreeElement<T> succ = curr.getNext().getReference();
				snip = curr.getNext().attemptMark(succ, true);
				if (!snip) {
					continue;
				}
				pred.getNext().compareAndSet(curr, succ, false, false);
				return;
			}
		}
	}

	@Override
	public String toString() {
		return this.toArrayList().toString();
	}

	private ArrayList<T> toArrayList() {
		LockFreeElement<T> pred, curr;
		ArrayList<T> array = new ArrayList<T>();
		pred = head;
		curr = pred.getNext().getReference();
		while (curr.getNext().getReference() != null) {
			array.add(curr.getValue());
			pred = curr;
			curr = curr.getNext().getReference();
		}

		return array;
	}

	public Window<T> find(LockFreeElement<T> headd, T value) {
		LockFreeElement<T> pred = null, curr = null, succ = null;
		boolean[] marked = {false};
		boolean snip;
		retry: while (true) {
			pred = headd;
			curr = pred.getNext().getReference();
			while (true) {
				succ = curr.getNext().get(marked);
				while (marked[0]) {
					snip = pred.getNext().compareAndSet(curr, succ, false, false);
					if (!snip) continue retry;
					curr = pred.getNext().getReference();
					succ = curr.getNext().get(marked);
				}
				if ((!curr.isHead()) || curr.isTail() || curr.getValue().compareTo(value) >= 0) {
					return new Window<T>(pred, curr);
				}
				pred = curr;
				curr = succ;
			}
		}
	}
}
