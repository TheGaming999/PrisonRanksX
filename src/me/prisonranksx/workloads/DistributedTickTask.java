package me.prisonranksx.workloads;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.google.common.base.Preconditions;


public class DistributedTickTask implements Runnable {

	public DistributedTickTask(final int distributionSize) {
		Preconditions.checkArgument(distributionSize > 0);
		this.workloadMatrix = new ArrayList<>();
		this.distributionSize = distributionSize;
		for (int i = 0; i < distributionSize; i++) {
			this.workloadMatrix.add(new LinkedList<>());
		}
	}
	
	private final List<LinkedList<Workload>> workloadMatrix;
	private final int distributionSize;
	private int currentPosition = 0;
	
	public void add(final Workload workload) {
		List<Workload> smallestList = this.workloadMatrix.get(0);
		for (int index = 0; index < this.distributionSize; index++) {
			if(smallestList.size() == 0) {
				break;
			}
			final List<Workload> next = this.workloadMatrix.get(index);
			final int size = next.size();
			if (size < smallestList.size()) {
				smallestList = next;
			}
		}
		smallestList.add(workload);
	}
	
	private void proceedPosition() {
		if (++this.currentPosition == this.distributionSize) {
			this.currentPosition = 0;
		}
	}
	
	@Override
	public void run() {
		this.workloadMatrix.get(this.currentPosition).removeIf(load -> {
			load.compute();
			return !load.shouldBeRescheduled();
		});
		this.proceedPosition();
	}

}
