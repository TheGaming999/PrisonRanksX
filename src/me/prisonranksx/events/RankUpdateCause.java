package me.prisonranksx.events;


	public enum RankUpdateCause{NORMAL_RANKUP, FORCE_RANKUP, NORMAL_RANKUPMAX, NORMAL_RANKUPBY, RANKSET, RANKSET_BYPRESTIGE, RANKSET_BYCONVERT;
		
	private static RankUpdateCause rankUpdateCause;
	

	public static void setRankUpdateCauseAction(RankUpdateCause rankUpdateCause)
	{
		RankUpdateCause.rankUpdateCause = rankUpdateCause;
	}

	public static RankUpdateCause getRankUpdateCause()
	{
			return rankUpdateCause;
	}

	
}
