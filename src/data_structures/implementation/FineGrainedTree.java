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
			if (root.value == null) {
				root.value = t;
			} else {
				fineAdd(root, t);
			}
		} finally {
			root.unlock();
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

	// sequential methods
	private void fineRemove(FineNode<T> curr, T t) {
		if (curr == null) {
			return;
		}
		switch (t.compareTo(curr.value)) {
		case 0:
			if (curr.left != null && curr.right != null) {
				FineNode<T> successor = findMinChild(curr.right);
				curr.value = successor.value;
				replaceNodeInParent(successor, successor.right);
			} else if (curr.left != null) {
				replaceNodeInParent(curr, curr.left);
			} else if (curr.right != null) {
				replaceNodeInParent(curr, curr.right);
			} else {
				replaceNodeInParent(curr, null);
			}
			break;
		case -1:
			fineRemove(curr.left, t);
			break;
		case 1:
			fineRemove(curr.right, t);
			break;
		}
	}

	private void replaceNodeInParent(FineNode<T> curr, FineNode<T> _new) {
		if (curr.parent != null) {
			if (curr == curr.parent.left) {
				curr.parent.left = _new;
			} else {
				curr.parent.right = _new;
			}
		} else {
			root = _new;
		}
		if (_new != null) {
			_new.parent = curr.parent;
		}
	}

	private FineNode<T> findMinChild(FineNode<T> right) {
		while (right.left != null) {
			right = right.left;
		}
		return right;
	}

	private void fineAdd(FineNode<T> curr, T t) {
		if (curr != root) {
			curr.lock();
		}
		try {
			switch (t.compareTo(curr.value)) {
			case -1:
				logger.finer("curr.left = " + curr.left);
				fineInternalAddLeft(curr, t);
				break;
			case 1:
				logger.finer("curr.right = " + curr.right);
				fineInternalAddRight(curr, t);
				break;
			case 0:
				logger.finer("curr.left = " + curr.left);
				fineInternalAddLeft(curr, t);
				break;
			}
		} finally {
			if (curr != root) {
				curr.unlock();
			}
		}
	}

	private void fineInternalAddRight(FineNode<T> curr, T t) {
		if (curr.right == null) {
			curr.right = new FineNode<T>(t, curr);
		} else {
			fineAdd(curr.right, t);
		}
	}

	private void fineInternalAddLeft(FineNode<T> curr, T t) {
		if (curr.left == null) {
			curr.left = new FineNode<T>(t, curr);
		} else {
			fineAdd(curr.left, t);
		}
	}

	private void sequentialTraverse(FineNode<T> curr, Operation<T> operation) {
		if (curr == null || curr.value == null) {
			return;
		}
		operation.start(curr);
		sequentialTraverse(curr.left, operation);
		operation.middle(curr);
		sequentialTraverse(curr.right, operation);
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
			stringBuilder.append('[' + node.value.toString());
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
