package data_structures.implementation;

public class Window<T extends Comparable<T>> {
	private LockFreeElement<T> pred;
	private LockFreeElement<T> curr;

	public Window(LockFreeElement<T> pred, LockFreeElement<T> curr) {
		super();
		this.pred = pred;
		this.curr = curr;
	}
	
	public LockFreeElement<T> getPred() {
		return pred;
	}

	public void setPred(LockFreeElement<T> pred) {
		this.pred = pred;
	}

	public LockFreeElement<T> getCurr() {
		return curr;
	}

	public void setCurr(LockFreeElement<T> curr) {
		this.curr = curr;
	}
}
