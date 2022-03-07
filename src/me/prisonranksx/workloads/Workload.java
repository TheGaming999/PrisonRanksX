package me.prisonranksx.workloads;

public interface Workload {

	void compute();
	
	default boolean shouldBeRescheduled() {
		return false;
	}
	
	default boolean computeThenCheckForScheduling() {
		this.compute();
		return !this.shouldBeRescheduled();
	}
}
