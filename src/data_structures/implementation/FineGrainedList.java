package data_structures.implementation;

import data_structures.Sorted;

public class FineGrainedList<T extends Comparable<T>> implements Sorted<T> {
	
	private FineElement<T> head;
	
	public FineGrainedList(){
		this.head=new FineElement<T>();
	}

	public void add(T t) {
		FineElement<T> newEle = new FineElement<T>(t);
		
		if(Sorted.DEBUG){
			System.out.println(this.toString());
			System.out.println("New element"+t);
		}
		if(t == null)
			return;
		while(true){	
			if(head.isNull()){
				this.head.lock();
				if(head.isNull()){
					try{
						this.head=newEle;
						return;
					}finally{
						this.head.unlock();
					}
					
				}
					
			}

		}
	}

	private boolean recoursiveAdd(FineElement<T> parent,FineElement<T> index,FineElement<T> newEle){
		if (newEle==null)
			return false;
		
		if(index == null || index.isNull()){
			parent.lock();
			if(parent.next.isNull() || parent.next == null){
				try{
					parent=newEle;
					return true;
				}finally{
					parent.unlock();
				}
				
			}
		}
			
		//System.out.println("Current:"+index.value+" new:"+newNode.value);

		if(newEle.compareTo(index)>=0){
			parent.lock();
			try{
				index.lock();
				try{
					if(parent.next.equals(index) && parent)
					
				}finally{
					index.unlock();
				}
			}finally{
				parent.unlock();
			}
			
		}
		return true;
	}
	public void remove(T t) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		throw new UnsupportedOperationException();
	}
}
