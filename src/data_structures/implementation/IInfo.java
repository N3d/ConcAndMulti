package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

public class IInfo<T extends Comparable<T>> implements LockFreeInfo{
	
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
