package me.prisonranksx.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ListUtils {
    private List<String> xx;
    private String hic;
    private String cic;
    private String all;
	public ListUtils(List<String> dd) {
		xx = dd;
	}
	public String getLastIndexString() {
		return xx.get(xx.size() - 1);
	}
	public Integer getLastIndex() {
		return xx.size() - 1;
	}
	/**
	 * A string list that contains: ["2","5","3"]
	 * Will return: 10
	 */
	public Integer getSumOfIntegers() {
		List<Integer> ff = new ArrayList<>();
		for(String str : xx) {
			ff.add(Integer.valueOf(str));
		}
		return ff.stream().mapToInt(Integer::intValue).sum();
	}
	public boolean HasIgnoreCase(String string) {
	for(String de : xx) {
		if(de.equalsIgnoreCase(string)) {
			hic = de;
			all = de;
			return true;
		}
	}
		return false;
	}
	public String getReturnedString_HasIgnoreCase() {
		return hic;
	}
	public boolean ContainsIgnoreCase(String string) {
		for(String de : xx) {
			if(StringUtils.containsIgnoreCase(de, string)) {
				cic = de;
				all = de;
				return true;
			}
		}
		return false;
	}
	public String getReturnedIgnoreCase(String string) {
		if(ContainsIgnoreCase(string)) {
			return all;
		}
		return null;
	}
	public String getReturnedString_ContainsIgnoreCase() {
		return cic;
	}
	public String getAsString() {
		for(String ff : xx) {
			return ff;
		}
		return xx.toString();
	}
}
