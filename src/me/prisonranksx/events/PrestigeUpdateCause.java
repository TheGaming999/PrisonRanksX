package me.prisonranksx.events;

public enum PrestigeUpdateCause {PRESTIGEUP, SETPRESTIGE, DELPRESTIGE, AUTOPRESTIGE, SETPRESTIGE_BY_REBIRTH
	,PRESTIGE_BY_RANKUPMAX, OTHER;

	private static PrestigeUpdateCause cause;
	
	public static void setPrestigeUpdateCause(PrestigeUpdateCause cause) {
		PrestigeUpdateCause.cause = cause;
	}
	
	public static PrestigeUpdateCause getPrestigeUpdateCause() {
		return PrestigeUpdateCause.cause;
	}
}
