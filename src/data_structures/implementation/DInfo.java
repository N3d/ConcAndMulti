package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

public class DInfo<T extends Comparable<T>> implements LockFreeInfo{
	
	public AtomicStampedReference<LockFreeNode<T>> gp;
	public AtomicStampedReference<LockFreeNode<T>> p;
	public AtomicStampedReference<LockFreeNode<T>> l;
	
	public StateInfo<T> si;
	
	public DInfo(AtomicStampedReference<LockFreeNode<T>> gp,AtomicStampedReference<LockFreeNode<T>> p,
			AtomicStampedReference<LockFreeNode<T>> l, StateInfo<T> si){
		this.gp=gp;
		this.p=p;
		this.l=l;
		this.si=si;
	}
}
