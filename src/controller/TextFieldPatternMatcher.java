package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Patternmatcher for the regex provided by the user while using the
 * emoflon-view visualisation
 * 
 * @author maximiliansell
 *
 */
public class TextFieldPatternMatcher {

	/**
	 * Searches for potential matches in the text using the provided regex
	 * 
	 * @param regex - input from JavaFX-control textfield
	 * @param text  - string used by the "model"-classes which contains data of the
	 *              nodes from the emoflon-view visualisation
	 * @return if there was a match found
	 */
	public static boolean matchTextFieldInput(String regex, String text) {
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(text);
		boolean match = false;
		if (matcher.find()) {
			match = true;
		}
		return match;
	}
}
