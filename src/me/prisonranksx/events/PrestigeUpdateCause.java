package me.prisonranksx.events;

public enum PrestigeUpdateCause {PRESTIGEUP, SETPRESTIGE, DELPRESTIGE;

	private static PrestigeUpdateCause cause;
	
	public static void setPrestigeUpdateCause(PrestigeUpdateCause cause) {
		PrestigeUpdateCause.cause = cause;
	}
	
	public static PrestigeUpdateCause getPrestigeUpdateCause() {
		return PrestigeUpdateCause.cause;
	}
}
