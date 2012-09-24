package data_structures.implementation;
/**
 * @author Matteo Casenove and Nicola Mularoni
 * 
 * This class represents the generic element in the list.
 *
 */

public class Element<T> {
	
	private T value;
	private Element<T> next;
	
	public Element(){
		this.value=null;
		this.next=null;
	}
	
	public Element(T value,Element<T> next){
		this.value=value;
		this.next=next;
	}
	
	public T getValue(){
		return this.value;
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
}
