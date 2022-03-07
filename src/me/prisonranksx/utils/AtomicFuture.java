package me.prisonranksx.utils;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class AtomicFuture<T> {

	private CompletableFuture<T> completableFuture;
	
	public AtomicFuture() {
		this.completableFuture = new CompletableFuture<T>();
	}
	
	public AtomicFuture(CompletableFuture<T> completableFuture) {
		this.completableFuture = completableFuture;
	}
	
	public static AtomicFuture<?> create() {
		return new AtomicFuture<>();
	}
	
	public CompletableFuture<T> get() {
		return this.completableFuture;
	}
	
	public void set(CompletableFuture<T> completableFuture) {
		this.completableFuture = completableFuture;
	}
	
	public CompletableFuture<T> setAndGet(CompletableFuture<T> completableFuture) {
		return this.completableFuture = completableFuture;
	}
	
	public CompletableFuture<T> getAndSet(CompletableFuture<T> completableFuture) {
		CompletableFuture<T> previousCompletableFuture = completableFuture;
		this.completableFuture = completableFuture;
		return previousCompletableFuture;
	}
	
	public CompletableFuture<T> supplyAsync(Supplier<T> supplier) {
		return this.completableFuture = CompletableFuture.supplyAsync(supplier);
	}
	
	@SuppressWarnings("unchecked")
	public CompletableFuture<Void> runAsync(Runnable runnable) {
		return (CompletableFuture<Void>) (this.completableFuture = (CompletableFuture<T>) CompletableFuture.runAsync(runnable));
	}
	
}
