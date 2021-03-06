package data_structures.implementation;


public class IInfo<T extends Comparable<T>> implements LockFreeInfo{
	
	public LockFreeNode<T> newInternal;
	public LockFreeNode<T> p;
	public LockFreeNode<T> l;
	
	public Stamps stamps;
	
	public IInfo(LockFreeNode<T> newInternal,LockFreeNode<T> p,
			LockFreeNode<T> l,Stamps stamps){
		this.newInternal=newInternal;
		this.p=p;
		this.l=l;
		this.stamps=stamps;
	}
}
