package data_structures.implementation;

import java.util.concurrent.atomic.AtomicMarkableReference;


public class LockFreeElement<T extends Comparable<T>> implements Comparable<LockFreeElement<T>> {

	static final int ELEMENT = 0;
	static final int HEAD = 1;
	static final int TAIL = 2;

	private T value;
	private AtomicMarkableReference<LockFreeElement<T>> next;
	private int type;

	public LockFreeElement(T value, LockFreeElement<T> next, int type) {
		super();
		this.value = value;
		this.next = new AtomicMarkableReference<LockFreeElement<T>>(next, false);
		this.type = type;
	}
	
	public LockFreeElement(int type) {
		this(null, null, type);
	}
	
	public LockFreeElement(T value) {
		this(value, null, ELEMENT);
	}

	public T getValue() {
		return value;
	}

	public void setValue(T value) {
		this.value = value;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public boolean isHead(){
		return (this.type==Element.HEAD);
	}
	
	public boolean isTail(){
		return (this.type==Element.TAIL);
	}

	public AtomicMarkableReference<LockFreeElement<T>> getNext() {
		return next;
	}

	public void setNext(AtomicMarkableReference<LockFreeElement<T>> next) {
		this.next = next;
	}

	@Override
	public int compareTo(LockFreeElement<T> o) {
		// TODO Auto-generated method stub
		return 0;
	}

}
