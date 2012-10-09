package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

import data_structures.Sorted;

public class LockFreeTree<T extends Comparable<T>> implements Sorted<T> {

	private final AtomicStampedReference<LockFreeNode<T>> root;

	public LockFreeTree(){
		AtomicStampedReference<LockFreeNode<T>> left=
				new AtomicStampedReference<LockFreeNode<T>>(new LockFreeNode<T>(),0);
		AtomicStampedReference<LockFreeNode<T>> right=
				new AtomicStampedReference<LockFreeNode<T>>(new LockFreeNode<T>(),0);
		AtomicStampedReference<LockFreeNode<T>> ro=
				new AtomicStampedReference<LockFreeNode<T>>(new LockFreeNode<T>(),0);
		ro.getReference().left=left;
		ro.getReference().right=right;
		this.root=ro;
	}

	@Override
	public void add(T t) {
		LockFreeNode<T> newNode=new LockFreeNode<T>(t);//new leaf
		SearchReturnValues ret;

		while(true){
			ret=search(newNode);
			if(newNode.compareTo(ret.l)==0)
				return;
			if(!ret.si.isClean()){
				Help(ret.si);
			}else{
				T key=(newNode.compareTo(ret.l)<0)?ret.l.value:newNode.value;
				LockFreeNode<T> nl=new LockFreeNode<T>(ret.l.value);//new internal
				LockFreeNode<T> ni=new LockFreeNode<T>(key);//new internal
				ni.left.set((newNode.compareTo(nl)<0)?newNode:nl, 0);
				ni.right.set((newNode.compareTo(nl)<0)?nl:newNode,0);
				boolean ris;

				IInfo<T> op= new IInfo<T>(ni,ret.p,ret.l,ret.stamps);
				StateInfo<T> newStateInfo = new StateInfo<T>(StateInfo.IFlag,op);
				ris=ret.p.si.compareAndSet(ret.si, newStateInfo, op.stamps.siStamp, ++op.stamps.siStamp);
				if(ris){
					HelpInsert(op);
					return;
				}else{
					Help(ret.p.si.getReference());
				}
			}
		}	
	}

	private void HelpInsert(IInfo<T> op){
		if(op==null || !(op instanceof IInfo) )
			return;
		CAS_CHILD(op.p, op.l, op.newInternal,op.stamps.pLeftStamp,op.stamps.pRightStamp);
		StateInfo<T> app = op.p.si.getReference();
		if(app.isIFlag() && app.info == op){
			op.p.si.compareAndSet(app, new StateInfo<T>(StateInfo.CLEAN,op), op.stamps.siStamp, ++op.stamps.siStamp);
		}
	}

	private void CAS_CHILD(LockFreeNode<T> p, LockFreeNode<T> l,
			LockFreeNode<T> ni,int pLeftStamp,int pRightStamp){
		if(p.isLeaf() || ni==null)
			return;
		boolean ris;

		if(ni.compareTo(p)<0)
			ris=p.left.compareAndSet(l, ni, pLeftStamp, ++pLeftStamp);
		else
			ris=p.right.compareAndSet(l, ni, pRightStamp, ++pRightStamp);
	}

	private boolean HelpDelete(DInfo<T> op){
		if(op==null || !(op instanceof DInfo))
			return false;
		boolean res = op.p.si.compareAndSet(op.si, new StateInfo<T>(StateInfo.MARKED,op), op.stamps.siStamp, ++op.stamps.siStamp);
		StateInfo<T> app2 = op.p.si.getReference();
		if(res || (app2.isMarked() && app2.info == op)){
			HelpMarked(op);
			return true;
		}else{
			Help(op.p.si.getReference());
			StateInfo<T> app = op.gp.si.getReference();
			if(app.isDFlag() && app.info == op){
				op.gp.si.compareAndSet(app, new StateInfo<T>(StateInfo.CLEAN,op), op.stamps.gsiStamp, ++op.stamps.gsiStamp);
			}
			return false;
		}	
	}

	private void HelpMarked(DInfo<T> op){
		if(op==null || !(op instanceof DInfo))
			return ;
		LockFreeNode<T> other;
		if(op.l.compareTo(op.p.right.getReference())==0)
			other=op.p.left.getReference();
		else
			other=op.p.right.getReference();

		CAS_CHILD(op.gp, op.p, other, op.stamps.gpLeftStamp,op.stamps.gpRightStamp);

		StateInfo<T> app = op.gp.si.getReference();
		if(app.isDFlag() && app.info == op){
			op.gp.si.compareAndSet(app, new StateInfo<T>(StateInfo.CLEAN,op), op.stamps.gsiStamp, ++op.stamps.gsiStamp);
		}
	}


	@SuppressWarnings("unchecked")
	private void Help(StateInfo<T> u){
		if(u==null)
			return;
		switch(u.state){
		case StateInfo.IFlag:
			if(u.info instanceof  IInfo)
				HelpInsert((IInfo<T>) u.info);
			break;
		case StateInfo.MARKED:
			if(u.info instanceof  DInfo)
				HelpMarked((DInfo<T>)u.info);
			break;
		case StateInfo.DFlag:
			if(u.info instanceof  DInfo){
				HelpDelete((DInfo<T>)u.info);
			}
			break;
		}
	}


	private class SearchReturnValues{
		public LockFreeNode<T> gp;
		public LockFreeNode<T> p;
		public LockFreeNode<T> l;
		public StateInfo<T> si;
		public StateInfo<T> gsi;
		public Stamps stamps;

		public SearchReturnValues(AtomicStampedReference<LockFreeNode<T>> gp,AtomicStampedReference<LockFreeNode<T>> p,
				AtomicStampedReference<LockFreeNode<T>> l,
				AtomicStampedReference<StateInfo<T>> si, AtomicStampedReference<StateInfo<T>> gsi){
			this.gp=gp.getReference();
			this.p=p.getReference();
			this.l=l.getReference();
			this.si=si.getReference();
			this.gsi=gsi.getReference();
			this.stamps=new Stamps(si.getStamp(), gsi.getStamp(), p.getStamp(), gp.getStamp(), p.getReference().left.getStamp(),
					p.getReference().right.getStamp(), gp.getReference().left.getStamp(), gp.getReference().right.getStamp());
		}
	}

	private SearchReturnValues search(LockFreeNode<T> node){

		AtomicStampedReference<LockFreeNode<T>> gp=new AtomicStampedReference<LockFreeNode<T>>(new LockFreeNode<T>(),0),
				p=new AtomicStampedReference<LockFreeNode<T>>(new LockFreeNode<T>(),0);
		AtomicStampedReference<LockFreeNode<T>> l=new AtomicStampedReference<LockFreeNode<T>>(new LockFreeNode<T>(),0);
		AtomicStampedReference<StateInfo<T>> gsi=new AtomicStampedReference<StateInfo<T>>(new StateInfo<T>(StateInfo.CLEAN,null),0);
		AtomicStampedReference<StateInfo<T>> si=new AtomicStampedReference<StateInfo<T>>(new StateInfo<T>(StateInfo.CLEAN,null),0);
		
		l=this.root;
		while(!l.getReference().isLeaf()){
			gp=p;
			p=l;
			gsi=si;
			si=p.getReference().si;
			l=((node.compareTo(l.getReference())<0)?l.getReference().left:l.getReference().right);
		}
		return new SearchReturnValues(gp, p, l, si, gsi);
	}

	@Override
	public void remove(T t) {
		DInfo<T> op;
		SearchReturnValues ret;
		LockFreeNode<T> newNode=new LockFreeNode<T>(t);//node to find

		while(true){
			ret=search(newNode);
			if(newNode.compareTo(ret.l)!=0){
				return;
			}
			if(!ret.gsi.isClean())
				Help(ret.gsi);
			else if(!ret.si.isClean())
				Help(ret.si);
			else{
				boolean res;
				op= new DInfo<T>(ret.gp,ret.p,ret.l,ret.si,ret.stamps);
				StateInfo<T> newStateInfo = new StateInfo<T>(StateInfo.DFlag,op);
				res=ret.gp.si.compareAndSet(ret.gsi, newStateInfo , op.stamps.gsiStamp, ++op.stamps.gsiStamp);
				if(res){
					if(HelpDelete(op))
						return;
				}else{
					Help(ret.gp.si.getReference());						
				}
			}
		}
	}

	@Override
	public String toString(){
		// it is a debugging method so it doesn't need to lock the structure.
		AtomicStampedReference<LockFreeNode<T>> node=this.root;
		String str=recursivePrint(node);
		return (str.compareTo("")==0)?"[]":str;

	}

	private String recursivePrint(AtomicStampedReference<LockFreeNode<T>> node){
		if(node == null || node.getReference()==null )
			return "";
		if(node.getReference().getValue()==null)
			return recursivePrint(node.getReference().left)+recursivePrint(node.getReference().right);
		return "["+node.getReference().getValue()+recursivePrint(node.getReference().left)+recursivePrint(node.getReference().right)+"]";
	}
}
