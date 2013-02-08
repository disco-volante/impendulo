package fmv;

public class DiffAction {

	public enum Operation {
		SKIP, ADD, DEL, CHANGE, NADA
	};

	private Operation op;

	private int count;

	public DiffAction(Operation op, int count) {
		this.op = op;
		this.count = count;
	}

	public Operation getOp() {
		return op;
	}

	public int getCount() {
		return count;
	}

	public String toString() {
		StringBuffer b = new StringBuffer();
		if (op == Operation.ADD) {
			b.append("A[" + count + "]");
		} else if (op == Operation.DEL) {
			b.append("D[" + count + "]");
		} else if (op == Operation.SKIP) {
			b.append("S[" + count + "]");
		}
		return b.toString();
	}

}
