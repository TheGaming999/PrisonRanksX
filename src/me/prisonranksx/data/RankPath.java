package me.prisonranksx.data;

public class RankPath {

	private String rankName;
	private String pathName;
	private String full;

	public RankPath(String rankName, String pathName) {
		this.rankName = rankName;
		this.pathName = pathName;
		full = rankName + "#~#" + pathName;
	}

	/**
	 * 
	 * @param fullPath { "%rank%#~#%path%" } example: { "A#~#default" }
	 * @return RankPath from String by splitting
	 * @deprecated use: new RankPath(String rankName, String pathName);
	 */
	public static RankPath getRankPath(String fullPath) {
		return new RankPath(fullPath.split("#~#")[0], fullPath.split("#~#")[1]);
	}

	public static RankPath getRankPath(String rank, String path) {
		return new RankPath(rank, path);
	}

	public void set(String rankName, String pathName) {
		this.rankName = rankName;
		this.pathName = pathName;
		full = rankName + "#~#" + pathName;
	}

	public String get() {
		return full;
	}

	/**
	 * 
	 * @return rank by splitting
	 * @deprecated
	 */
	public String getRank() {
		return get().split("#~#")[0];
	}

	/**
	 * 
	 * @return path by splitting
	 * @deprecated
	 */
	public String getPath() {
		return get().split("#~#")[1];
	}

	/**
	 * 
	 * @return path from variable
	 */
	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}

	/**
	 * 
	 * @return rank from variable
	 */
	public String getRankName() {
		return rankName;
	}

	public void setRankName(String rankName) {
		this.rankName = rankName;
	}

}
