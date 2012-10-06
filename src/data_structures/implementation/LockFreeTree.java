package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

import data_structures.Sorted;
import data_structures.implementation.LockFreeInfo.DInfo;
import data_structures.implementation.LockFreeInfo.IInfo;

public class LockFreeTree<T extends Comparable<T>> implements Sorted<T> {
	
	private AtomicStampedReference<LockFreeNode<T>> root;
	
	public LockFreeTree(){
		this.root=new AtomicStampedReference<LockFreeNode<T>>(null,0);;
	}

	public void add(T t) {
		LockFreeNode<T> newNode=new LockFreeNode<T>(t);//new leaf
		SearchReturnValues ret;
		while(true){
			if(this.root.getReference()==null){
				//do something
			}
			ret=search(newNode);
			
			if(ret.l.getReference().compareTo(newNode)==0)
				return;
			
			
			if(ret.si!=null && !ret.si.getReference().isClean()){
				Help(ret.si.getReference(), ret.stamps);
			}else{
				if(ret.l == null){
					
				}else{
					T key=(newNode.compareTo(ret.l.getReference())<0)?ret.l.getReference().value:newNode.value;
					LockFreeNode<T> nl=new LockFreeNode<T>(ret.l.getReference().value);//new internal
					LockFreeNode<T> ni=new LockFreeNode<T>(key);//new internal
					ni.left.set((nl.compareTo(newNode)<0)?nl:newNode, 0);
					ni.right.set((nl.compareTo(newNode)<0)?newNode:nl,0);
					LockFreeInfo<T>.IInfo<T> op= new LockFreeInfo<T>().new IInfo<T>(new AtomicStampedReference<LockFreeNode<T>>(ni,0),ret.p,ret.l);
					StateInfo newStateInfo = new StateInfo(StateInfo.IFlag,op);
					boolean ris=ret.p.getReference().si.compareAndSet(ret.si.getReference(), newStateInfo, ret.stamps.siStamp, ++ret.stamps.siStamp);
					if(ris){
						HelpInsert(op,ret.stamps);
						return;
					}else{
						Help(ret.si.getReference(), ret.stamps);
					}
				}
			}
		}	
	}
	
	private void HelpInsert(LockFreeInfo<T>.IInfo<T> op, Stamps stamps){
		CAS_CHILD(op.p, op.l, op.newInternal,stamps.pLeftStamp,stamps.pRightStamp);
		StateInfo app = op.p.getReference().si.getReference();
		if(app.isIFlag() && app.info == op){
			op.p.getReference().si.compareAndSet(app, new StateInfo(StateInfo.CLEAN,null), stamps.siStamp, ++stamps.siStamp);
		}
	}
	
	private void CAS_CHILD(AtomicStampedReference<LockFreeNode<T>> p, AtomicStampedReference<LockFreeNode<T>> l,
			AtomicStampedReference<LockFreeNode<T>> ni,int pLeftStamp,int pRightStamp){
		if(p.getReference().isLeaf() || ni==null)
			return;
		if(ni.getReference().compareTo(p.getReference())<0)
			p.getReference().left.compareAndSet(l.getReference(), ni.getReference(), pLeftStamp, ++pLeftStamp);
		else
			p.getReference().right.compareAndSet(l.getReference(), ni.getReference(), pRightStamp, ++pRightStamp);
	}
	
	private boolean HelpDelete(LockFreeInfo<T>.DInfo<T> op, Stamps stamps){
		if(op==null)
			return false;
		boolean res = op.p.getReference().si.compareAndSet(op.si, new StateInfo(StateInfo.MARKED,op), stamps.pStamp, ++stamps.pStamp);
		if(res || op.p.getReference().si.getReference().isMarked()){
			HelpMarked(op, stamps);
			return true;
		}else{
			Help(op.si, stamps);
			//THIS IS A FUCKING TRICK BUT I'VE NO IDEA HOW TO MADE IT IN ANOTHER WAY...
			StateInfo app = op.gp.getReference().si.getReference();
			if(app.isMarked() && app.info == op){
				op.gp.getReference().si.compareAndSet(app, new StateInfo(StateInfo.CLEAN,op), stamps.gsiStamp, ++stamps.gsiStamp);
			}
			return false;
		}	
	}
	
	private void HelpMarked(LockFreeInfo<T>.DInfo<T> op, Stamps stamps){
		if(op==null)
			return;
		AtomicStampedReference<LockFreeNode<T>> other;
		if(op.p.getReference().right.getReference()==op.l.getReference())
			other=op.p.getReference().left;
		else
			other=op.p.getReference().right;
		CAS_CHILD(op.gp, op.p, other, stamps.gpLeftStamp,stamps.gpRightStamp);
		
		StateInfo app = op.gp.getReference().si.getReference();
		if(app.isDFlag() && app.info == op){
			op.gp.getReference().si.compareAndSet(app, new StateInfo(StateInfo.CLEAN,op), stamps.gpStamp, ++stamps.gpStamp);
		}
	}
	
	@SuppressWarnings("unchecked")
	private void Help(StateInfo u, Stamps stamps){
		if(u==null)
			return;
		switch(u.state){
		case StateInfo.IFlag: 
			HelpInsert((LockFreeInfo<T>.IInfo<T>)u.info, stamps);
		case StateInfo.MARKED:
			HelpMarked((LockFreeInfo<T>.DInfo<T>)u.info, stamps);
		case StateInfo.DFlag:
			HelpDelete((LockFreeInfo<T>.DInfo<T>)u.info, stamps);
		}
	}
	
	/*
	 * Class necessary to take care of the stamps of each node and state.
	 */
	private class Stamps{
		
		public int siStamp;
		public int gsiStamp;
		public int pStamp;
		public int gpStamp;
		public int pLeftStamp;
		public int pRightStamp;
		public int gpLeftStamp;
		public int gpRightStamp;
		
		public Stamps(int siStamp, int gsiStamp, int pStamp,
		 int gpStamp,int pLeftStamp,int pRightStamp,int gpLeftStamp,int gpRightStamp){
			this.siStamp=siStamp;
			this.gsiStamp=gsiStamp;
			this.pStamp=pStamp;
			this.gpStamp=gpStamp;
			this.pLeftStamp=pLeftStamp;
			this.pRightStamp=pRightStamp;
			this.gpLeftStamp=gpLeftStamp;
			this.gpRightStamp=gpRightStamp;
		}
	}
	
	
	private class SearchReturnValues{
		public AtomicStampedReference<LockFreeNode<T>> gp;
		public AtomicStampedReference<LockFreeNode<T>> p;
		public AtomicStampedReference<LockFreeNode<T>> l;
		public AtomicStampedReference<StateInfo> si;
		public AtomicStampedReference<StateInfo> gsi;
		public Stamps stamps;
		
		public SearchReturnValues(AtomicStampedReference<LockFreeNode<T>> gp,AtomicStampedReference<LockFreeNode<T>> p,
				AtomicStampedReference<LockFreeNode<T>> l,
				AtomicStampedReference<StateInfo> si, AtomicStampedReference<StateInfo> gsi){
			this.gp=gp;
			this.p=p;
			this.l=l;
			this.si=si;
			this.gsi=gsi;
			this.stamps=new Stamps(si.getStamp(), gsi.getStamp(), p.getStamp(), gp.getStamp(), p.getReference().left.getStamp(),
							p.getReference().right.getStamp(), gp.getReference().left.getStamp(), gp.getReference().right.getStamp());
		}
	}
	
	private SearchReturnValues search(LockFreeNode<T> node){
		
		AtomicStampedReference<LockFreeNode<T>> gp=null,p=null;
		AtomicStampedReference<LockFreeNode<T>> l=null;
		AtomicStampedReference<StateInfo> gsi=null;
		AtomicStampedReference<StateInfo> si=null;
		
		l=this.root;
		
		while( l != null && !l.getReference().isLeaf()){
			gp=p;
			p=l;
			gsi=si;
			si=p.getReference().si;
			l=((node.compareTo(l.getReference())<0)?l.getReference().left:l.getReference().right);
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
