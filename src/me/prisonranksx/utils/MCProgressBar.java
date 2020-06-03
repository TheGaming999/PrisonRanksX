package me.prisonranksx.utils;

public class MCProgressBar {

	private String style;
	private final String xs = "x";
	private int maxValue;
	private String leftPrefix;
	private String completedPrefix;
	private String progressBar;
	private String defaultProgressBar;
	private int value;
	
    private String getChars(String character, int amount) {
    	if(amount <= 0) {
    		return "";
    	}
    	StringBuilder builder = new StringBuilder(character);
    	for(int i = 0; i < amount - 1; i++) {
    		 builder.append(character);
    	}
    	return builder.toString();
    }
	
	public MCProgressBar(String style, String leftPrefix, String completedPrefix, int maxValue) {
		this.style = style;
		this.leftPrefix = leftPrefix;
		this.completedPrefix = completedPrefix;
		this.maxValue = maxValue;
	}
	
	public MCProgressBar(String style, String leftPrefix, String completedPrefix) {
		this.style = style;
		this.leftPrefix = leftPrefix;
		this.completedPrefix = completedPrefix;
	}
	
	public MCProgressBar(String style) {
		this.style = style;
	}
	
	public MCProgressBar() {}
	
	/**
	 * build the progressbar, give access to setValue(int) method.
	 */
	public void build() {
		progressBar = getChars(xs, maxValue);
		defaultProgressBar = getChars(xs, maxValue);
	}
	
	/**
	 * 
	 * @return defaultProgressBar without leftPrefix & completedPrefix applied
	 */
	public String getPlainProgressBar() {
		return this.defaultProgressBar;
	}
	
	public void setStyle(String style) {
		this.style = style;
	}
	
	public String getStyle() {
		return this.style;
	}
	
	public void setLeftPrefix(String leftPrefix) {
		this.leftPrefix = leftPrefix;
	}
	
	public String getLeftPrefix() {
		return this.leftPrefix;
	}
	
	public void setCompletedPrefix(String completedPrefix) {
		this.completedPrefix = completedPrefix;
	}
	
	public String getCompletedPrefix() {
		return this.completedPrefix;
	}
	
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	
	public int getMaxValue() {
		return this.maxValue;
	}
	
	/**
	 * Supports additional chars,
	 * Must build the progress bar before using this.
	 * value {
	 * 0 <= value <= maxValue
	 * }
	 */
	public void setValue(int value) {
		this.value = value;
		int i = value;
		String leftProgressBar = getPlainProgressBar().substring(i);
		String completedProgressBar = getPlainProgressBar().substring(maxValue - i);
		String finalProgressBar = getCompletedPrefix() + completedProgressBar
				+ getLeftPrefix() + leftProgressBar;
		this.progressBar = finalProgressBar;
	}
	
	public int getValue() {
		return this.value;
	}
	
	@Deprecated
	public void increment() {
		if(1 + getValue() > maxValue) {
			System.out.println("Couldn't reach max value.");
			throw new IllegalStateException();
		}
		this.value = getValue() + 1;
		String leftProgressBar = getPlainProgressBar().substring(getValue() + 1);
		String completedProgressBar = getPlainProgressBar().substring(maxValue - getValue() - 1);
		String finalProgressBar = getCompletedPrefix() + completedProgressBar
				+ getLeftPrefix() + leftProgressBar;
		this.progressBar = finalProgressBar;
	}
	
	@Deprecated
	public void increment(int incrementValue) {
		if(incrementValue + getValue() > maxValue) {
			System.out.println("Couldn't reach max value.");
			throw new IllegalStateException();
		}
		int i = incrementValue;
		this.value = getValue() + i;
		String leftProgressBar = getPlainProgressBar().substring(getValue() + i);
		String completedProgressBar = getPlainProgressBar().substring(maxValue - getValue() - i);
		String finalProgressBar = getCompletedPrefix() + completedProgressBar
				+ getLeftPrefix() + leftProgressBar;
		this.progressBar = finalProgressBar;
	}
	
	/**
	 * 
	 * @return final progress bar with settings applied
	 */
	public String getProgressBar() {
		return this.progressBar.replaceAll(xs, style);
	}
}
