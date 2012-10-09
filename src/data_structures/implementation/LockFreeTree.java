package data_structures.implementation;

import java.util.concurrent.atomic.AtomicStampedReference;

import data_structures.Sorted;

public class LockFreeTree<T extends Comparable<T>> implements Sorted<T> {

	private AtomicStampedReference<LockFreeNode<T>> root;

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

	public void add(T t) {
		LockFreeNode<T> newNode=new LockFreeNode<T>(t);//new leaf
		SearchReturnValues ret;
		if(Sorted.DEBUG){
			System.out.println(this.toString());
			System.out.println("New element"+t);
		}
		while(true){
			/*if(this.root.getReference()==null){
				if(root.compareAndSet(null, newNode, 0, 1))
					return;
			}*/
			ret=search(newNode);

			//if(((LockFreeNode<T>)ret.l.getReference()).compareTo(newNode)==0)
			if(newNode.compareTo(ret.l)==0)
				return;

			if(!ret.si.isClean()){
				Help(ret.si, ret.stamps);
			}else{
				T key=(newNode.compareTo(ret.l)<0)?ret.l.value:newNode.value;
				LockFreeNode<T> nl=new LockFreeNode<T>(ret.l.value);//new internal
				LockFreeNode<T> ni=new LockFreeNode<T>(key);//new internal
				ni.left.set((newNode.compareTo(nl)<0)?newNode:nl, 0);
				ni.right.set((newNode.compareTo(nl)<0)?nl:newNode,0);
				boolean ris;
				// This is the case where there is only one element in the tree. So root points to the found leaf.
				//ris=root.compareAndSet(ret.l.getReference(), ni, ret.stamps.rootStamp, ret.stamps.rootStamp+1);
				//if(!ris){
					IInfo<T> op= new IInfo<T>(ni,ret.p,ret.l);
					StateInfo<T> newStateInfo = new StateInfo<T>(StateInfo.IFlag,op);
					ris=ret.p.si.compareAndSet(ret.si, newStateInfo, ret.stamps.siStamp, ++ret.stamps.siStamp);
					if(ris){
						HelpInsert(op,ret.stamps);
						return;
					}else{
						Help(ret.p.si.getReference(), ret.stamps);
					}
				//}else{
				//	return;
				//}
				

			}
		}	
	}

	private void HelpInsert(IInfo<T> op, Stamps stamps){
		if(op==null || !(op instanceof IInfo) )
			return;
		CAS_CHILD(op.p, op.l, op.newInternal,stamps.pLeftStamp,stamps.pRightStamp);
		StateInfo<T> app = op.p.si.getReference();
		if(app.isIFlag() && app.info == op){
			op.p.si.compareAndSet(app, new StateInfo<T>(StateInfo.CLEAN,null), stamps.siStamp, ++stamps.siStamp);
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
		//if(!ris)
		//	System.out.println("Errore");
		
		//root.compareAndSet(l.getReference(), p.getReference(), rootStamp, ++rootStamp);
	}

	private boolean HelpDelete(DInfo<T> op, Stamps stamps){
		if(op==null || !(op instanceof DInfo))
			return false;
		boolean res = op.p.si.compareAndSet(op.si, new StateInfo<T>(StateInfo.MARKED,op), stamps.siStamp, ++stamps.siStamp);
		StateInfo<T> app2 = op.p.si.getReference();
		if(res || (app2.isMarked() && app2.info == op)){
			HelpMarked(op, stamps);
			return true;
		}else{
			Help(op.p.si.getReference(), stamps);
			//THIS IS A FUCKING TRICK BUT I'VE NO IDEA HOW TO MADE IT IN ANOTHER WAY...
			StateInfo<T> app = op.gp.si.getReference();
			if(app.isMarked() && app.info == op){
				op.gp.si.compareAndSet(app, new StateInfo<T>(StateInfo.CLEAN,op), stamps.gsiStamp, ++stamps.gsiStamp);
			}
			return false;
		}	
	}

	private void HelpMarked(DInfo<T> op, Stamps stamps){
		if(op==null || !(op instanceof DInfo))
			return ;
		LockFreeNode<T> other;
		if(op.l.compareTo(op.p.right.getReference())==0)
			other=op.p.left.getReference();
		else
			other=op.p.right.getReference();
		//if(other.getReference()==null)
		//	return false;
		//This is necessary in case there is no gp, so the root as to be updated manually.
		//boolean res=root.compareAndSet(op.p.getReference(), other.getReference(), stamps.rootStamp, stamps.rootStamp+1);
		//if(!res){
			CAS_CHILD(op.gp, op.p, other, stamps.gpLeftStamp,stamps.gpRightStamp);

			StateInfo<T> app = op.gp.si.getReference();
			if(app.isDFlag() && app.info == op){
				op.gp.si.compareAndSet(app, new StateInfo<T>(StateInfo.CLEAN,op), stamps.gsiStamp, ++stamps.gsiStamp);
			}
		//}
		//return true;
	}


	private void Help(StateInfo<T> u, Stamps stamps){
		//System.out.println("AIUTOOOO");
	
		if(u==null)
			return;
		switch(u.state){
		case StateInfo.IFlag:
			if(u.info instanceof  IInfo)
				HelpInsert((IInfo<T>) u.info, stamps);
			//HelpInsert(null, stamps);
			break;
		case StateInfo.MARKED:
			if(u.info instanceof  DInfo)
				HelpMarked((DInfo<T>)u.info, stamps);
			break;
		case StateInfo.DFlag:
			if(u.info instanceof  DInfo)
				HelpDelete((DInfo<T>)u.info, stamps);
			break;
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
		//public int rootStamp;

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
			//this.rootStamp=root.getStamp();
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
			this.si=si.getReference();//(p==null)?null:p.getReference().si;
			this.gsi=gsi.getReference();//(gp==null)?null:gp.getReference().si;
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

		while( l.getReference() != null && !l.getReference().isLeaf()){
			gp=p;
			p=l;
			gsi=si;
			si=p.getReference().si;
			l=((node.compareTo(l.getReference())<0)?l.getReference().left:l.getReference().right);
		}
		return new SearchReturnValues(gp, p, l, si, gsi);
	}

	public void remove(T t) {
		DInfo<T> op;
		SearchReturnValues ret;
		LockFreeNode<T> newNode=new LockFreeNode<T>(t);//node to find

		if(Sorted.DEBUG){
			System.out.println(this.toString());
			System.out.println("Remove element"+t);
		}

		while(true){
			/*if(this.root.getReference()==null){
				return;
			}*/
			ret=search(newNode);

			if(newNode.compareTo(ret.l)!=0){
				System.out.println("Fail!"+t);
				return;
			}

			if(!ret.gsi.isClean())
				Help(ret.gsi, ret.stamps);
			else if(!ret.si.isClean())
				Help(ret.si, ret.stamps);
			else{
				boolean res;
				//boolean res=root.compareAndSet(ret.l.getReference(), null, ret.stamps.rootStamp, ret.stamps.rootStamp+1);
				//if(!res){
					op= new DInfo<T>(ret.gp,ret.p,ret.l,ret.si);
					/*if(root.getReference()==ret.p.getReference()){
						if(HelpDelete(info,ret.stamps))
							return;
					}else{*/
					StateInfo<T> newStateInfo = new StateInfo<T>(StateInfo.DFlag,op);
					res=ret.gp.si.compareAndSet(ret.gsi, newStateInfo , ret.stamps.gsiStamp, ++ret.stamps.gsiStamp);
					if(res){
						if(HelpDelete(op,ret.stamps))
							return;
					}else{
						Help(ret.gp.si.getReference(),ret.stamps);						
					}
				//}else{
				//	return;
				//}
			}


		}
		
	}

	public String toString(){
		// it is a debugging method so it doesn't need to lock the structure.
		AtomicStampedReference<LockFreeNode<T>> node=this.root;
		String str=recursivePrint(node);
		return (str.compareTo("")==0)?"[]":str;
		//if(node ==null || node.getReference()==null)
		//	return "[]";
		//node= findFirst(node);
		//if(node ==null || node.getReference()==null)
		//	return "[]";
		//return "["+node.getReference().getValue()+recursivePrint(node.getReference().left)+recursivePrint(node.getReference().right)+"]";
		//return "["+node.getReference().getValue()+recursivePrint(node.getReference().left)+recursivePrint(node.getReference().right)+"]";
	    
	}
	
	private String recursivePrint(AtomicStampedReference<LockFreeNode<T>> node){
		if(node == null || node.getReference()==null )
			return "";
		//if(node.getReference().isDummy())
		//	return recursivePrint(node.getReference().left)+recursivePrint(node.getReference().right);
		//if(node.getReference().isLeaf())
		//	return "["+node.getReference().value+"]";
		if(node.getReference().getValue()==null)
			return recursivePrint(node.getReference().left)+recursivePrint(node.getReference().right);
		return "["+node.getReference().getValue()+recursivePrint(node.getReference().left)+recursivePrint(node.getReference().right)+"]";
	}
}
