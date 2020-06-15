package me.prisonranksx.events;

public enum RebirthUpdateCause {REBIRTHUP, SETREBIRTH, DELREBIRTH, OTHER;

	private static RebirthUpdateCause cause;
	
	public static void setRebirthUpdateCause(RebirthUpdateCause cause) {
		RebirthUpdateCause.cause = cause;
	}
	
	public RebirthUpdateCause getRebirthUpdateCause() {
		return cause;
	}
}
