package data_structures.implementation;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
	

	private final FineElement<T> head;
	private final FineElement<T> tail;

	public FineGrainedList(){
		this.head=new FineElement<T>();
		this.head.setHead();
		this.tail=new FineElement<T>();
		this.tail.setTail();
		this.head.next=this.tail;
	}

	@Override
	public void add(T t) {
		FineElement<T> newEle = new FineElement<T>(t);

		if(t == null)
			return;

		head.lock(); //this is because, we cannot permit that the head changes between this and the
					// next instruction.
		FineElement<T> pred=head;
		try{
			FineElement<T> curr=(FineElement<T>) pred.next;
			curr.lock();
			try{
				while(newEle.compareTo(curr)<0){
					pred.unlock();
					pred=curr;
					curr=(FineElement<T>) curr.next;
					curr.lock();
				}
				newEle.next=curr;
				pred.next=newEle;
				return;
			}finally{
				curr.unlock();
			}	
		}finally{
			pred.unlock();
		}
	}

	@Override
	public void remove(T t) {
		FineElement<T> newEle = new FineElement<T>(t);

		if(t == null)
			return;

		head.lock();
		FineElement<T> pred=head;
		try{
			FineElement<T> curr=(FineElement<T>) pred.next;
			curr.lock();
			try{
				while(newEle.compareTo(curr)<0){
					pred.unlock();
					pred=curr;
					curr=(FineElement<T>) curr.next;
					curr.lock();
				}
				if(curr.isTail())
					return;
				pred.next=curr.next;
				return;
			}finally{
				curr.unlock();
			}
			
		}finally{
			pred.unlock();
		}
	}

	@Override
	public String toString() {
		String ris="";
		for(FineElement<T> curr=(FineElement<T>) this.head.next;!curr.isTail();curr=(FineElement<T>) curr.next){
			ris+="["+curr.getValue()+"]";
			if(!curr.next.isTail())
				ris+="->";
		}
		if("".compareTo(ris)==0){
			ris="[]";
		}
		return ris; 
	}
}
