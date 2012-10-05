package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

public class LockFreeInfo {
	
	public class DInfo<T extends Comparable<T>> extends LockFreeInfo{
		public AtomicStampedReference<LockFreeNode<T>> gp;
		public AtomicStampedReference<LockFreeNode<T>> p;
		public AtomicStampedReference<LockFreeNode<T>> l;
		public StateInfo si;
		
		public DInfo(AtomicStampedReference<LockFreeNode<T>> gp,AtomicStampedReference<LockFreeNode<T>> p,
				AtomicStampedReference<LockFreeNode<T>> l, StateInfo si){
		}
	}
	
	public class IInfo<T extends Comparable<T>> extends LockFreeInfo{
		public AtomicStampedReference<LockFreeNode<T>> newInternal;
		public AtomicStampedReference<LockFreeNode<T>> p;
		public AtomicStampedReference<LockFreeNode<T>> l;
		
		public IInfo(AtomicStampedReference<LockFreeNode<T>> newInternal,AtomicStampedReference<LockFreeNode<T>> p,
				AtomicStampedReference<LockFreeNode<T>> l){
			this.newInternal=newInternal;
			this.p=p;
			this.l=l;
		}
	}

}
