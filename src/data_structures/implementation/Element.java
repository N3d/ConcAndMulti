package data_structures.implementation;
/**
 * @author Matteo Casenove, Nicola Mularoni, and Carmine Paolino
 * 
 * This class represents the generic element in the list.
 *
 */

public class Element<T extends Comparable<T>> implements Comparable<Element<T>>{
<<<<<<< HEAD
	
	static final int ELEMENT = 0;
	static final int HEAD = 1;
	static final int TAIL = 2;
	
	public T value;
	public Element<T> next;
	private int type;
	
=======

	public T value;
	public Element<T> next;

>>>>>>> CoarseGrainedList
	public Element(){
		this.value=null;
		this.next=null;
	}

	public Element(T value){
		this.value=value;
		this.type=ELEMENT;
		this.next=null;
	}
<<<<<<< HEAD
	
	public Element(T value, int type){
=======

	public Element(T value, Element<T> next){
>>>>>>> CoarseGrainedList
		this.value=value;
		this.type=type;
		this.next=null;
	}
	
	public void setHead(){
		this.type=Element.HEAD;
	}
	
	public void setTail(){
		this.type=Element.TAIL;
	}
	
	public boolean isHead(){
		return (this.type==Element.HEAD);
	}
	
	public boolean isTail(){
		return (this.type==Element.TAIL);
	}

	public T getValue(){
		return this.value;
	}

	public boolean isNull(){
		return (this.value==null);
	}

	public Element<T> getNext(){
		return this.next;
	}

	public void setValue(T value){
		this.value=value;
	}

	public void setNext(Element<T> next){
		this.next=next;
	}

	public String toString() {
		return value.toString();
	}

	@Override
	public int compareTo(Element<T> o) {
		if(this.isHead()) return 1;
		if(this.isTail()) return -1;
		if(o.isHead()) return -1;
		if(o.isTail()) return 1;
		return (this.value).compareTo(o.value);
	}
}
