package data_structures.implementation;


public class DInfo<T extends Comparable<T>> implements LockFreeInfo{
	
	public LockFreeNode<T> gp;
	public LockFreeNode<T> p;
	public LockFreeNode<T> l;
	
	public StateInfo<T> si;
	
	public Stamps stamps;
	
	public DInfo(LockFreeNode<T> gp,LockFreeNode<T> p,
			LockFreeNode<T> l, StateInfo<T> si,Stamps stamps){
		this.gp=gp;
		this.p=p;
		this.l=l;
		this.si=si;
		this.stamps=stamps;
	}
}
