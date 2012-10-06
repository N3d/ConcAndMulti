package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeInfo<T extends Comparable<T>> {
	
	public AtomicStampedReference<LockFreeNode<T>> p;
	public AtomicStampedReference<LockFreeNode<T>> l;
	
	public class DInfo<T extends Comparable<T>> extends LockFreeInfo<T>{
		public AtomicStampedReference<LockFreeNode<T>> gp;
		
		public StateInfo si;
		
		public DInfo(AtomicStampedReference<LockFreeNode<T>> gp,AtomicStampedReference<LockFreeNode<T>> p,
				AtomicStampedReference<LockFreeNode<T>> l, StateInfo si){
		}
	}
	
	public class IInfo<T extends Comparable<T>> extends LockFreeInfo<T>{
		
		public AtomicStampedReference<LockFreeNode<T>> newInternal;
		
		public IInfo(AtomicStampedReference<LockFreeNode<T>> newInternal,AtomicStampedReference<LockFreeNode<T>> p,
				AtomicStampedReference<LockFreeNode<T>> l){
			this.newInternal=newInternal;
			super.p=p;
			super.l=l;
		}
	}

}
