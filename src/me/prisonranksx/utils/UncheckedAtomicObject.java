package me.prisonranksx.utils;

class UncheckedType<S> {
	
	public S type;
	
	public UncheckedType(S type) {
		this.type = type;
	}
	
	@SuppressWarnings({ "unchecked", "hiding" })
	public <S> S get() {
		return (S)type;
	}
	
	@SuppressWarnings({ "unchecked", "hiding" })
	public <S> S get(S s) {
		return (S)type;
	}
	
	public void set(S t) {
		this.type = t;
	}
	
}

@SuppressWarnings("unchecked")
public class UncheckedAtomicObject {

	@SuppressWarnings("rawtypes")
	private UncheckedType type;
	
	public <T> UncheckedAtomicObject(T t) {
		type = new UncheckedType<>(t);
		type.set(t);
	}
	
	public <T> T get() {
		return (T)type.get();
	}
	
	public <T> void set(T t) {
		type.set(t);
	}
	
}
