package me.prisonranksx.workloads;

public class FixedCountRunnable implements ScheduledWorkload {

	private final int delay;
	private final int maxTicks;
	private final Runnable runnable;
	private int ticksAlive = 0;
	
	public FixedCountRunnable(int delay, int maxTicks, Runnable runnable) {
		this.delay = delay;
		this.maxTicks = maxTicks;
		this.runnable = runnable;
	}
	
	@Override
	public void compute() {
		if(this.ticksAlive++ % this.delay == 0) {
			this.runnable.run();
		}
	}
	
	@Override
	public boolean shouldBeRescheduled() {
		return this.ticksAlive < this.maxTicks;
	}

}
