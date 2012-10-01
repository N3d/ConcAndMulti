package data_structures.implementation;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
	
	
	/*
	 * This list creates an fineElement with empty value to consider a null element.
	 * This is due to the fact that the head has to have a lock even if it is null.
	 * So a empty list has one single element with value null.
	 * FineElement has a method called isNull to test if its value is null.
	 * NO!
	 */

	private FineElement<T> head;

	public FineGrainedList(){
		this.head=new FineElement<T>();
	}

	@SuppressWarnings("null")
	public void add(T t) {
		FineElement<T> newEle = new FineElement<T>(t);

		if(Sorted.DEBUG){
			System.out.println(this.toString());
			System.out.println("New element"+t);
		}

		if(t == null)
			return;


		head.lock();
		if(head.isNull()){
			try{
				this.head=newEle;
				return;
			}finally{
				this.head.unlock();
			}
		}
		FineElement<T> pred = head;
		try{
		
			FineElement<T> curr = (FineElement<T>) pred;
			//curr.lock();
			try{
				while(curr!=null && !curr.isNull() && newEle.compareTo(curr)<0){
					if(pred.equals(curr)){
						curr=(FineElement<T>) curr.next;
						curr.lock();
					}
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

/*	private boolean recoursiveAdd(FineElement<T> pred,FineElement<T> curr,FineElement<T> newEle){
		if (newEle==null || newEle.isNull()){
			pred.unlock();
			return false;
		}

		if(curr == null || curr.isNull()){
			pred.next=newEle;
			pred.unlock();
			return true;	
		}

		//System.out.println("Current:"+index.value+" new:"+newNode.value);
		curr.lock();
		// It is not necessary to check the pred again because as long as it is locked none can 
		// add new item next to it or remove it. So it is safe.
		if(newEle.compareTo(curr)>=0){
			newEle.next=curr;
			pred.next=newEle;
			pred.unlock();
			curr.unlock();
			return true;
		}else{
			pred.unlock();
			return recoursiveAdd(curr,(FineElement<T>) curr.next,newEle);
		}
	}
*/ 
	public void remove(T t) {
		FineElement<T> newEle = new FineElement<T>(t);

		if(Sorted.DEBUG){
			System.out.println(this.toString());
			System.out.println("Remove element"+t);
		}

		if(t == null)
			return;

		head.lock();
		if(head.isNull()){
			try{
				return;
			}finally{
				head.unlock();
			}
		}
		FineElement<T> pred = head;
		try{
			FineElement<T> curr = (FineElement<T>) pred.next;
			curr.lock();
			try{
				while((curr!=null || !curr.isNull()) && newEle.compareTo(curr)<0){
					pred.unlock();
					pred=curr;
					curr=(FineElement<T>) curr.next;
					if(curr.next!=null || !curr.next.isNull()){
						curr.lock();
					}
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

	public String toString() {
		throw new UnsupportedOperationException();
	}
}
