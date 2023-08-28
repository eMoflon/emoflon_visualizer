package controller;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFieldPatternMatcher {
	
	public static boolean matchTextFieldInput(String regex, String text) {
	    Pattern pattern = Pattern.compile(regex);
	    Matcher matcher = pattern.matcher(text);
	    boolean match = false;
	    if(matcher.find()) {
	        match = true;
	    }
	    return match;
	}
}
