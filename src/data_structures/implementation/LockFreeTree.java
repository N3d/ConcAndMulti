package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

import data_structures.Sorted;
import data_structures.implementation.LockFreeInfo.IInfo;

public class LockFreeTree<T extends Comparable<T>> implements Sorted<T> {
	
	private LockFreeNode<T> root;
	
	public LockFreeTree(){
		this.root=null;
	}

	public void add(T t) {
		LockFreeNode<T> newNode=new LockFreeNode<T>(t);//new leaf
		SearchReturnValues ret;
		while(true){
			if(this.root==null){
				//do something
			}
			ret=search(newNode);
			if(ret.l!=null && ret.l.compareTo(newNode)==0){
				return;
				
			}
			if(ret.si!=null && !ret.si.getReference().isClean()){
				//HELP
				return;
			}else{
				if(ret.l == null){
					
				}else{
					T key=(newNode.compareTo(ret.l)<0)?ret.l.value:newNode.value;
					LockFreeNode<T> nl=new LockFreeNode<T>(ret.l.value);//new internal
					LockFreeNode<T> ni=new LockFreeNode<T>(key);//new internal
					ni.left=(nl.compareTo(newNode)<0)?nl:newNode;
					ni.right=(nl.compareTo(newNode)<0)?newNode:nl;
					IInfo<T> op= new LockFreeInfo().new IInfo<T>(ni,ret.p,ret.l);
					boolean ris=ret.p.si.compareAndSet(ret.si.getReference(), new StateInfo(StateInfo.IFlag,op), ret.siStamp, ret.siStamp+1);
					if(ris){
						//helpInsert
						return;
					}else{
						//help
					}
					 
				}
					
				
			}
			
		
		}
		
	}
	
	private class SearchReturnValues{
		public LockFreeNode<T> gp;
		public LockFreeNode<T> p;
		public LockFreeNode<T> l;
		public AtomicStampedReference<StateInfo> si;
		public AtomicStampedReference<StateInfo> gsi;
		public int siStamp;
		public int gsiStamp;
		
		public SearchReturnValues(LockFreeNode<T> gp,LockFreeNode<T> p,LockFreeNode<T> l,
				AtomicStampedReference<StateInfo> si, AtomicStampedReference<StateInfo> gsi){
			this.gp=gp;
			this.p=p;
			this.l=l;
			this.si=si;
			this.gsi=gsi;
			this.siStamp=si.getStamp();
			this.gsiStamp=gsi.getStamp();
		}
	}
	
	private SearchReturnValues search(LockFreeNode<T> node){
		
		LockFreeNode<T> gp=null,p=null,l=null;
		AtomicStampedReference<StateInfo> gsi=null;
		AtomicStampedReference<StateInfo> si=null;
		
		l=this.root;
		
		while( l != null && !l.isLeaf()){
			gp=p;
			p=l;
			gsi=si;
			si=p.si;
			l=(LockFreeNode<T>) ((node.compareTo(l)<0)?l.left:l.right);
		}
		return new SearchReturnValues(gp, p, l, si, gsi);
	}

	public void remove(T t) {
		throw new UnsupportedOperationException();
	}

	public String toString() {
		throw new UnsupportedOperationException();
	}
}
