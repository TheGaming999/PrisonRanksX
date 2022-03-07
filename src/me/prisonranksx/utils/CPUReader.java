package me.prisonranksx.utils;

import java.io.IOException;
import java.lang.management.ManagementFactory;

import javax.management.MBeanServerConnection;

import com.sun.management.OperatingSystemMXBean;

public class CPUReader {

	private long nanoStart;
	private long cpuStart;
	private long nanoFinish;
	private long cpuFinish;
	private long percentResult;
	private MBeanServerConnection mbsc = ManagementFactory.getPlatformMBeanServer();
	private OperatingSystemMXBean osMBean = null;
	
	private CPUReader() {
		try {
			osMBean = ManagementFactory.newPlatformMXBeanProxy(
					mbsc, ManagementFactory.OPERATING_SYSTEM_MXBEAN_NAME, OperatingSystemMXBean.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		nanoStart = System.nanoTime();
		cpuStart = osMBean.getProcessCpuTime();
	}
	
	public static CPUReader start() {
		return new CPUReader();
	}
	
	public void restart() {
		nanoStart = System.nanoTime();
		cpuStart = osMBean.getProcessCpuTime();
	}
	
	public void processResult() {
		cpuFinish = osMBean.getProcessCpuTime();
		nanoFinish = System.nanoTime();

		if (nanoFinish > nanoStart)
		 percentResult = ((cpuFinish-cpuStart)*100L)/
		   (nanoFinish-nanoStart);
		else percentResult = 0;
	}
	
	public void restartAndProcessResult() {
		restart();
		cpuFinish = osMBean.getProcessCpuTime();
		nanoFinish = System.nanoTime();

		if (nanoFinish > nanoStart)
		 percentResult = ((cpuFinish-cpuStart)*100L)/
		   (nanoFinish-nanoStart);
		else percentResult = 0;
	}
	
	public long getProcessedResult() {
		return percentResult / osMBean.getAvailableProcessors();
	}
	
	public long getResult() {
		processResult();
		return percentResult / (osMBean.getAvailableProcessors() / 2);
	}
	
	public long getPlainResult() {
		return percentResult;
	}
	
	public int getAvailabeProcesssors() {
		return this.osMBean.getAvailableProcessors();
	}
	
}
