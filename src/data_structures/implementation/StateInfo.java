package data_structures.implementation;

public class StateInfo<T extends Comparable<T>>{
	
	public static final int CLEAN=1;
	public static final int DFlag=2;
	public static final int IFlag=3;
	public static final int MARKED=4;
	
	public int state;
	public LockFreeInfo info;
	
	public StateInfo(int state,LockFreeInfo info) {
		this.state=state;
		this.info=info;
	}
	
	public boolean isClean(){
		return state==CLEAN;
	}
	
	public boolean isDFlag(){
		return state==DFlag;
	}
	
	public boolean isIFlag(){
		return state==IFlag;
	}
	
	public boolean isMarked(){
		return state==MARKED;
	}
}
