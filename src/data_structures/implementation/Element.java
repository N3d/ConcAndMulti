package data_structures.implementation;
/**
 * @author Matteo Casenove and Nicola Mularoni
 * 
 * This class represents the generic element in the list.
 *
 */

public class Element<T extends Comparable<T>> implements Comparable<Element<T>>{
	
	public T value;
	public Element<T> next;
	
	public Element(){
		this.value=null;
		this.next=null;
	}
	
	public Element(T value){
		this.value=value;
		this.next=null;
	}
	
	public Element(T value, Element<T> next){
		this.value=value;
		this.next=next;
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

	@Override
	public int compareTo(Element<T> o) {
		return (this.value).compareTo(o.value);
	}
}
