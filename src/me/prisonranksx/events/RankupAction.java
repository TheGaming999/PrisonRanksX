package me.prisonranksx.events;


	public enum RankupAction{NORMAL_RANKUP, FORCE_RANKUP, NORMAL_RANKUPMAX, NORMAL_RANKUPBY, RANKSET, RANKSET_BYPRESTIGE, RANKSET_BYCONVERT;
		
	private static RankupAction rankupAction;
	

	public static void setRankupAction(RankupAction rankupAction)
	{
		RankupAction.rankupAction = rankupAction;
	}

	public static RankupAction getRankupAction()
		{
			return rankupAction;
		}

	
}
