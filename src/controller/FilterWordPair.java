package controller;

public class FilterWordPair {

	private String firstFilterWord;
	
	private String secondFilterWord;
	
	/**	
	 * Creates an object that can/will be used as the JavaFX-control table elements
	 * 
	 * @param firstFilterWord - will be provided by the first textfield in the "VisFXController"-class
	 * @param secondFilterWord - will be provided by the second textfield in the "VisFXController"-class
	 * 
	 * @author maximiliansell
	 */
	public FilterWordPair(String firstFilterWord, String secondFilterWord) {
		this.firstFilterWord = firstFilterWord;
		this.secondFilterWord = secondFilterWord;
	}
	
	/**
	 * Returns the first String saved in the table for the regex-filter
	 * 
	 * @return String for regex-filter
	 */ 
	public String getFirstFilterWord() {
		return firstFilterWord;
	}
	
	/**
	 * Returns the first String saved in the table for the regex-filter
	 * 
	 * @return String for regex-filter
	 */ 
	public String getSecondFilterWord() {
		return secondFilterWord;
	}
	
	/**
	 * Sets the instance variable firstFilterWord 
	 * 
	 * @param firstFilterWord - string for regex-filter
	 */
	public void setFirstFilterWord(String firstFilterWord) {
		this.firstFilterWord = firstFilterWord;
	}
	/**
	 * Sets the instance variable secondFilterWord 
	 * 
	 * @param firstFilterWord - string for regex-filter
	 */
	public void setSecondFilterWord(String secondFilterWord) {
		this.secondFilterWord = secondFilterWord;
	}
	
}
