package me.prisonranksx.workloads;

public interface ScheduledWorkload {

	void compute();
	
	default boolean shouldBeRescheduled() {
		return false;
	}
	
}
