package me.prisonranksx.utils;

public class AtomicObject {

	private Object object;
	
	public AtomicObject() {
		this.object = null;
	}
	
	public AtomicObject(Object object) {
		this.object = object;
	}
	
	public void set(Object object) {
		this.object = object;
	}
	
	public Object get() {
		return this.object;
	}
	
	public Object setAndGet(Object object) {
		return this.object = object;
	}
	
}
