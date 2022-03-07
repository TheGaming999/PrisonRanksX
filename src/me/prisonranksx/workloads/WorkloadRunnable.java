package me.prisonranksx.workloads;

import java.util.ArrayDeque;
import java.util.Deque;

public class WorkloadRunnable implements Runnable {

	private static final double MAX_MILLIS_PER_TICK = 2.5;
	private static final int MAX_NANOS_PER_TICK = (int) (MAX_MILLIS_PER_TICK * 1E6);

	private final Deque<Workload> workloadDeque = new ArrayDeque<>();

	public void addWorkload(Workload workload) {
		this.workloadDeque.add(workload);
	}

	@Override
	public void run() {
		long stopTime = System.nanoTime() + MAX_NANOS_PER_TICK;

		Workload nextLoad;

		// Note: Don't permute the conditions because sometimes the time will be over but the queue will still be polled then.
		while (System.nanoTime() <= stopTime && (nextLoad = this.workloadDeque.poll()) != null) {
			nextLoad.compute();
		}
	}



}
