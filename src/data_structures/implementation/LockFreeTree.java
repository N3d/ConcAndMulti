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
			if(ret.l.getReference().compareTo(newNode)==0){
				return;
				
			}
			if(ret.si!=null && !ret.si.getReference().isClean()){
				//HELP
				return;
			}else{
				if(ret.l == null){
					
				}else{
					T key=(newNode.compareTo(ret.l.getReference())<0)?ret.l.getReference().value:newNode.value;
					LockFreeNode<T> nl=new LockFreeNode<T>(ret.l.getReference().value);//new internal
					LockFreeNode<T> ni=new LockFreeNode<T>(key);//new internal
					ni.left.set((nl.compareTo(newNode)<0)?nl:newNode, 0);
					ni.right.set((nl.compareTo(newNode)<0)?newNode:nl,0);
					IInfo<T> op= new LockFreeInfo().new IInfo<T>(new AtomicStampedReference<LockFreeNode<T>>(ni,0),ret.p,ret.l);
					StateInfo newStateInfo = new StateInfo(StateInfo.IFlag,op);
					boolean ris=ret.p.getReference().si.compareAndSet(ret.si.getReference(), newStateInfo, ret.siStamp, ret.siStamp+1);
					if(ris){
						HelpInsert(op,newStateInfo,ret.siStamp+1,ret.p.getReference().left.getStamp(),ret.p.getReference().right.getStamp());
						return;
					}else{
						//help
					}
				}
			}
		}	
	}
	
	private void HelpInsert(IInfo<T> op, StateInfo oldSi,int oldStamp, int pLeftStamp, int pRightStamp){
		CAS_CHILD(op.p, op.l, op.newInternal,pLeftStamp,pRightStamp);
		op.p.getReference().si.compareAndSet(oldSi, new StateInfo(StateInfo.CLEAN,null), oldStamp, oldStamp+1);
	}
	
	private void CAS_CHILD(AtomicStampedReference<LockFreeNode<T>> p, AtomicStampedReference<LockFreeNode<T>> l,
			AtomicStampedReference<LockFreeNode<T>> ni,int pLeftStamp, int pRightStamp){
		if(p.getReference().isLeaf() || ni==null)
			return;
		if(ni.getReference().compareTo(p.getReference())<0)
			p.getReference().left.compareAndSet(l.getReference(), ni.getReference(), pLeftStamp, pLeftStamp+1);
		else
			p.getReference().right.compareAndSet(l.getReference(), ni.getReference(), pRightStamp, pRightStamp+1);
	}
	
	private boolean HelpDelete(DInfo<T> op,StateInfo oldSi, int pOldStamp,int gpOldStamp){
		if(op==null)
			return false;
		boolean res = op.p.getReference().si.compareAndSet(op.si, new StateInfo(StateInfo.MARKED,op), pOldStamp, pOldStamp+1);
		if(res || op.p.getReference().si.getReference().isMarked()){
			//helpMarked
			return true;
		}else{
			//help
			//THIS IS A FUCKING TRICK BUT I'VE NO IDEA HOW TO MADE IT IN ANOTHER WAY...
			StateInfo app = op.gp.getReference().si.getReference();
			if(app.isMarked() && app.info == op){
				op.gp.getReference().si.compareAndSet(app, new StateInfo(StateInfo.CLEAN,op), gpOldStamp, gpOldStamp+1);
			}
			return false;
			
		}
		
	}
	
	
	
	
	
	
	
	
	
	private class SearchReturnValues{
		public AtomicStampedReference<LockFreeNode<T>> gp;
		public AtomicStampedReference<LockFreeNode<T>> p;
		public AtomicStampedReference<LockFreeNode<T>> l;
		public AtomicStampedReference<StateInfo> si;
		public AtomicStampedReference<StateInfo> gsi;
		public int siStamp;
		public int gsiStamp;
		
		public SearchReturnValues(AtomicStampedReference<LockFreeNode<T>> gp,AtomicStampedReference<LockFreeNode<T>> p,
				AtomicStampedReference<LockFreeNode<T>> l,
				AtomicStampedReference<StateInfo> si, AtomicStampedReference<StateInfo> gsi){
			this.gp=gp;
			this.p=p;
			this.l=l;
			this.si=si;
			this.gsi=gsi;
			this.siStamp=si.getStamp();
			this.gsiStamp=gsi.getStamp();
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
