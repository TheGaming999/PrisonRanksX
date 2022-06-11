package me.prisonranksx.utils;

import javax.annotation.Nullable;

public class HolidayUtils {

	/**
	 * 
	 * Only holidays with a constant date.
	 *
	 */
	public enum Holiday {
		NONE, HALLOWEEN_DAY, CHRISTMAS_EVE, NEW_YEAR_EVE, VALENTINE_DAY, VETERANS_DAY, 
		THANKSGIVING_DAY, SIBLINGS_DAY, EARTH_DAY, APRIL_FOOLS_DAY, FATHERS_DAY, MOTHERS_DAY,
		SAINT_PATRICK_DAY;
	}

	private Holiday holiday;
	private boolean isCeleberation;

	public HolidayUtils() {
		this.holiday = Holiday.NONE;
		this.findHoliday();	
	}
	
	public HolidayUtils(boolean findHoliday) {
		this.holiday = Holiday.NONE;
		if(findHoliday) this.findHoliday();	
	}

	public void findHoliday() {
		int month = java.time.LocalDate.now().getMonthValue();
		int day = java.time.LocalDate.now().getDayOfMonth();
		switch(month) {
		case 1:
			if(day <= 7) this.holiday = Holiday.NEW_YEAR_EVE;
			break;
		case 2:
			if(day >= 7 && day <= 17) this.holiday = Holiday.VALENTINE_DAY;
			break;
		case 3:
			if(day >= 19 && day <= 20) this.holiday = Holiday.FATHERS_DAY;
			if(day >= 13 && day <= 18) this.holiday = Holiday.SAINT_PATRICK_DAY; 
			if(day >= 21 && day <= 22) this.holiday = Holiday.MOTHERS_DAY;
			break;
		case 4:
			if(day == 1) this.holiday = Holiday.APRIL_FOOLS_DAY;
			if(day >= 7 && day < 13) this.holiday = Holiday.SIBLINGS_DAY;
			if(day >= 19 && day < 28) this.holiday = Holiday.EARTH_DAY;
			break;
		case 5:
			
			break;
		case 6:
			
			break;
		case 7:
			
			break;
		case 8:
			
			break;
		case 9:
			
			break;
		case 10:
			if(day >= 24) this.holiday = Holiday.HALLOWEEN_DAY;
			break;
		case 11:
			if(day == 1) this.holiday = Holiday.HALLOWEEN_DAY;
			if(day < 16 && day > 8) this.holiday = Holiday.VETERANS_DAY;
			if(day > 20 && day <= 27) this.holiday = Holiday.THANKSGIVING_DAY;
			break;
		case 12:
			if(day == 31) this.holiday = Holiday.NEW_YEAR_EVE;
			if(day < 31 && day > 22) this.holiday = Holiday.CHRISTMAS_EVE;
			break;
		}
	}

	public Holiday getHoliday() {
		return holiday;
	}
	
	public boolean isHoliday(Holiday holiday) {
		return this.holiday == holiday;
	}
	
	@Nullable
	public Holiday matchHoliday(@Nullable String name) {
		if(name == null) return null;
		name = name.toUpperCase();
		if(name.equals("NONE")) return Holiday.NONE;
		for(Holiday holi : Holiday.values()) {
			String holiName = holi.name();
			if(name.equals(holiName)) return holi;
			if(name.equals(holiName.replace("_", ""))) return holi;
			if(name.equals(holiName.split("_")[0])) return holi;
			if(name.contains(holiName)) return holi;
			if(name.contains(holiName.split("_")[0])) return holi;
			if(holiName.contains(name)) return holi;
			if(holiName.replace("_", "").contains(name)) return holi;
			if(holiName.startsWith(name)) return holi;
		}
		return null;
	}
	
	public Holiday parseHoliday(String name) {
		return Holiday.valueOf(name.toUpperCase());
	}
	
	public void setHoliday(Holiday holiday) {
		this.holiday = holiday;
	}
	
	public boolean isCeleberation() {
		return isCeleberation;
	}

	public void setCeleberation(boolean isCeleberation) {
		this.isCeleberation = isCeleberation;
	}

}
