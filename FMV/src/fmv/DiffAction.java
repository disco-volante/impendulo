package fmv;

public class DiffAction {

	public enum Operation {
		SKIP, ADD, DEL, CHANGE, NADA
	};

	private final Operation op;

	private final int count;

	public DiffAction(final Operation op, final int count) {
		this.op = op;
		this.count = count;
	}

	public int getCount() {
		return count;
	}

	public Operation getOp() {
		return op;
	}

	@Override
	public String toString() {
		final StringBuffer b = new StringBuffer();
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
