package me.prisonranksx.utils;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

public class NonRepeatingNumber {

	private final static int DEFAULT_LIMIT = 10;
	private final static AtomicInteger RANDOM_LIMIT = new AtomicInteger(15);
	private final static List<Integer> LIST = new LinkedList<>();

	public static synchronized int generateDefault() {
		if(!LIST.isEmpty()) {
			return LIST.remove(0);
		}
		for (int j = 1; j <= DEFAULT_LIMIT; ++j)
			LIST.add(j);
		Collections.shuffle(LIST);
		return LIST.remove(0);
	}

	public static synchronized int generate() {
		if(!LIST.isEmpty()) {
			return LIST.remove(0);
		}
		for (int j = 1; j <= RANDOM_LIMIT.get(); ++j)
			LIST.add(j);
		Collections.shuffle(LIST);
		return LIST.remove(0);
	}

	public static CompletableFuture<Integer> generateAsync() {
		return CompletableFuture.supplyAsync(() -> {
			if(!LIST.isEmpty()) return LIST.remove(0);
			for (int j = 1; j <= RANDOM_LIMIT.get(); ++j) LIST.add(j);
			Collections.shuffle(LIST);
			return LIST.remove(0);
		});
	}
	
	public static int generateAsyncAndGet() {
		return generateAsync().join();
	}

	public static synchronized int generate(int factor) {
		if(!LIST.isEmpty()) {
			return LIST.remove(0);
		}
		RANDOM_LIMIT.set(factor);
		for (int j = 1; j <= RANDOM_LIMIT.get(); ++j)
			LIST.add(j);
		Collections.shuffle(LIST);
		return LIST.remove(0);
	}

	public static int setRandomLimit(int factor) {
		RANDOM_LIMIT.set(factor);
		return factor;
	}
}
