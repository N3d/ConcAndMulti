package data_structures.implementation;

public class LockFreeInfo {
	
	public class DInfo<T extends Comparable<T>> extends LockFreeInfo{
		public LockFreeNode<T> gp;
		public LockFreeNode<T> p;
		public LockFreeNode<T> l;
		public StateInfo si;
		
		public DInfo(){
		}
	}
	
	public class IInfo<T extends Comparable<T>> extends LockFreeInfo{
		public LockFreeNode<T> newInternal;
		public LockFreeNode<T> p;
		public LockFreeNode<T> l;
		
		public IInfo(LockFreeNode<T> newInternal, LockFreeNode<T> p, LockFreeNode<T> l){
			this.newInternal=newInternal;
			this.p=p;
			this.l=l;
		}
	}

}
