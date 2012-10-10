package data_structures.implementation;

import java.util.logging.Level;
import java.util.logging.Logger;

import data_structures.Sorted;

/**
 * @author Carmine Paolino
 * 
 * @param <T>
 */
public class FineGrainedTree<T extends Comparable<T>> implements Sorted<T> {
	private FineNode<T> root;
	private static final Logger logger = Logger
			.getLogger(Logger.GLOBAL_LOGGER_NAME);

	public FineGrainedTree() {
		root = new FineNode<T>();
	}

	@Override
	public void add(T t) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Current tree: " + sequentialToString());
			logger.fine("Adding " + t.toString());
		}

		root.lock();
		try {
			if (root.getValue() == null) {
				root.setValue(t);
			} else {
				fineAdd(root, t);
			}
		} finally {
			if (root.getLock().isHeldByCurrentThread()) {
				root.unlock();
			}
		}
	}

	@Override
	public void remove(T t) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Current tree: " + sequentialToString());
			logger.fine("Removing " + t.toString());
		}

		fineRemove(root, t);
	}

	@Override
	public String toString() {
		PreorderToStringOperation toString = new PreorderToStringOperation();
		sequentialTraverse(root, toString);
		return toString.toString();
	}

	private void fineRemove(FineNode<T> curr, T t) {
		if (curr == null) {
			return;
		}
		if (curr != root && curr.getParent() != root) {
			curr.getParent().getParent().unlock();
		}
		curr.lock();
		try {
			switch (t.compareTo(curr.getValue())) {
			case 0:
				if (curr.getLeft() != null && curr.getRight() != null) {
					FineNode<T> successor = findMinChild(curr.getRight());
					curr.setValue(successor.getValue());

					FineNode<T> _new = successor.getRight();
					if (_new != null) {
						_new.lock();
					}
					try {
						replaceNodeInParent(successor, _new);
					} finally {
						if (_new != null && _new.getLock().isHeldByCurrentThread()) {
							_new.unlock();
						}
					}
				} else if (curr.getLeft() != null) {
					FineNode<T> left = curr.getLeft();
					left.lock();
					try {
						replaceNodeInParent(curr, left);
					} finally {
						left.unlock();
					}
				} else if (curr.getRight() != null) {
					FineNode<T> right = curr.getRight();
					right.lock();
					try {
						replaceNodeInParent(curr, right);
					} finally {
						right.unlock();
					}
				} else {
					replaceNodeInParent(curr, null);
				}
				break;
			case -1:
				fineRemove(curr.getLeft(), t);
				break;
			case 1:
				fineRemove(curr.getRight(), t);
				break;
			}
		} finally {
			if (curr.getLock().isHeldByCurrentThread()) {
				curr.unlock();
			}
		}
	}

	private void replaceNodeInParent(FineNode<T> curr, FineNode<T> _new) {
		if (curr.getParent() != null) {
			if (curr == curr.getParent().getLeft()) {
				curr.getParent().setLeft(_new);
			} else {
				curr.getParent().setRight(_new);
			}
		} else {
			root = _new;
		}
		if (_new != null) {
			_new.setParent(curr.getParent());
		}
		if (curr.getLock().isHeldByCurrentThread()) {
			curr.unlock();
		}
	}

	private FineNode<T> findMinChild(FineNode<T> right) {
		right.lock();
		try {
			if (right.getLeft() == null) {
				return right;
			} else {
				return findMinChild(right.getLeft());
			}
		} finally {
			right.unlock();
		}
	}

	private void fineAdd(FineNode<T> curr, T t) {
		if (curr != root) {
			if (curr.getParent() != root) {
				curr.getParent().getParent().unlock();
			}
			curr.lock();
		}
		try {
			switch (t.compareTo(curr.getValue())) {
			case -1:
				logger.finer("curr.left = " + curr.getLeft());
				fineInternalAddLeft(curr, t);
				break;
			case 1:
				logger.finer("curr.right = " + curr.getRight());
				fineInternalAddRight(curr, t);
				break;
			case 0:
				logger.finer("curr.left = " + curr.getLeft());
				fineInternalAddLeft(curr, t);
				break;
			}
		} finally {
			if (curr.getLock().isHeldByCurrentThread()) {
				curr.unlock();
			}
		}
	}

	private void fineInternalAddRight(FineNode<T> curr, T t) {
		if (curr.getRight() == null) {
			curr.setRight(new FineNode<T>(t, curr));
		} else {
			fineAdd(curr.getRight(), t);
		}
	}

	private void fineInternalAddLeft(FineNode<T> curr, T t) {
		if (curr.getLeft() == null) {
			curr.setLeft(new FineNode<T>(t, curr));
		} else {
			fineAdd(curr.getLeft(), t);
		}
	}

	private void sequentialTraverse(FineNode<T> curr, Operation<T> operation) {
		if (curr == null || curr.getValue() == null) {
			return;
		}
		operation.start(curr);
		sequentialTraverse(curr.getLeft(), operation);
		operation.middle(curr);
		sequentialTraverse(curr.getRight(), operation);
		operation.end(curr);
	}

	private interface Operation<T extends Comparable<T>> {
		public void start(FineNode<T> node);

		public void middle(FineNode<T> node);

		public void end(FineNode<T> node);
	}

	private class PreorderToStringOperation implements Operation<T> {
		StringBuilder stringBuilder;

		public PreorderToStringOperation() {
			stringBuilder = new StringBuilder();
		}

		@Override
		public String toString() {
			return stringBuilder.toString();
		}

		@Override
		public void middle(FineNode<T> node) {
			// nothing
		}

		@Override
		public void start(FineNode<T> node) {
			stringBuilder.append('[' + node.getValue().toString());
		}

		@Override
		public void end(FineNode<T> node) {
			stringBuilder.append(']');
		}
	}

	private String sequentialToString() {
		PreorderToStringOperation toString = new PreorderToStringOperation();
		sequentialTraverse(root, toString);
		return toString.toString();
	}
}
