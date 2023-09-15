package controller;

public class FilterWordPair {

	private String firstFilterWord;
	private String secondFilterWord;
	
	public FilterWordPair(String firstFilterWord, String secondFilterWord) {
		this.firstFilterWord = firstFilterWord;
		this.secondFilterWord = secondFilterWord;
	}
	
	public String getFirstFilterWord() {
		return firstFilterWord;
	}
	
	public String getSecondFilterWord() {
		return secondFilterWord;
	}
	
	public void setFirstFilterWord(String firstFilterWord) {
		this.firstFilterWord = firstFilterWord;
	}
	
	public void setSecondFilterWord(String secondFilterWord) {
		this.secondFilterWord = secondFilterWord;
	}
	
}
