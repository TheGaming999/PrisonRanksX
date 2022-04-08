package me.prisonranksx.utils;

public class TypedAtomicObject<T> {

	private T object;
	
	public TypedAtomicObject() {
		this.object = null;
	}
	
	public TypedAtomicObject(T t) {
		this.object = t;
	}
	
	public void set(T t) {
		this.object = t;
	}
	
	public T get() {
		return this.object;
	}
	
	public T setAndGet(T t) {
		return this.object = t;
	}
	
}
