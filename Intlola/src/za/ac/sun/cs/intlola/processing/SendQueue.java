package za.ac.sun.cs.intlola.processing;

import java.util.LinkedList;

import za.ac.sun.cs.intlola.Intlola;

public class SendQueue {
	private final LinkedList<Runnable> queue;

	public SendQueue() {
		queue = new LinkedList<Runnable>();
		new PoolWorker().start();
	}

	public void execute(Runnable r) {
		synchronized (queue) {
			queue.addLast(r);
			queue.notifyAll();
		}
	}

	private class PoolWorker extends Thread {
		public void run() {
			Runnable r;
			while (true) {
				synchronized (queue) {
					while (queue.isEmpty()) {
						try {
							queue.wait();
						} catch (InterruptedException ignored) {
						}
					}
					r = queue.removeFirst();
				}
				try {
					r.run();
				} catch (RuntimeException e) {
					Intlola.log(e, r);
				}
				if (r instanceof Quiter) {
					break;
				}
			}
		}
	}
}
