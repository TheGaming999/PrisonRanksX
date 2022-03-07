package me.prisonranksx.workloads;

import java.util.LinkedList;
import java.util.List;

public class WorkloadTask implements Runnable {

	private final List<Workload> workloads = new LinkedList<>();
	
	public void addWorkload(final Workload workload) {
		this.workloads.add(workload);
	}
	
	@Override
	public void run() {
		this.workloads.removeIf(Workload::computeThenCheckForScheduling);
	}

}
