package me.prisonranksx.utils;

import java.util.Collection;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class AccessibleString {

	@Nonnull
	private String string;
	
	@Nonnull
	public AccessibleString() {
		this.string = "";
	}

	private AccessibleString(@Nullable String string, boolean nullable) {
		this.string = string;
	}
	
	public AccessibleString(@Nonnull String string) {
		this.string = string;
	}
	
	public void setString(@Nonnull String string) {
		this.string = string;
	}
	
	public AccessibleString setAndGet(String string) {
		this.string = string;
		return this;
	}
	
	@Nonnull
	public String getString() {
		return this.string;
	}
	
	public String getString(boolean nullable) {
		return this.string;
	}
	
	public static boolean isNullOrEmpty(AccessibleString accessibleString) {
		return accessibleString == null || accessibleString.getString() == null || accessibleString.getString().isEmpty();
	}
	
	/**
	 * 
	 * @param endIndex
	 * @return characters that the string start with till endIndex
	 */
	@Nullable
	public String getStartsWith(int endIndex) {
		int counter = -1;
		if(this.string.isEmpty()) return null;
		char[] charArray = this.string.toCharArray();
		if(endIndex > charArray.length) return null;
		StringBuilder sb = new StringBuilder();
		for(char character : charArray) {
			counter++;
			if(counter >= endIndex)
				break;
			sb.append(character);
		}
		return sb.toString();
	}
	
	public String substring(int beginIndex) {
		return this.string.substring(beginIndex);
	}
	
	public String substring(int beginIndex, int endIndex) {
		return this.string.substring(beginIndex, endIndex);
	}
	
	@Nullable
	public static AccessibleString createNullable(String string) {
		return new AccessibleString(string, true);
	}
	
	public static AccessibleString create() {
		return new AccessibleString();
	}
	
	@Nullable
	public static AccessibleString parse(Object object) {
		String newString = null;
		if(object instanceof Number) {
			newString = String.valueOf(object);
			return new AccessibleString(newString);
		} else if (object instanceof Collection) {
			object = (Collection)object;
			newString = object.toString();
			return new AccessibleString(newString);
		} else if (object instanceof Map) {
			object = (Map)object;
			newString = ((Map) object).entrySet().toString();
			return new AccessibleString(newString);
		} else if (object instanceof UUID) {
			object = (UUID)object;
			newString = object.toString();
			return new AccessibleString(newString);
		}
		return new AccessibleString(String.valueOf(object), true);
	}
	
	public String toString() {
		return this.string;
	}
	
}
