package io.github.u2ware.test.example8;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;

public class RegexTests {

	 static final String[] testcases = new String[] {
		"'Tumblr' is an amazing app",
		"Tumblr is an amazing 'app'",
		"Tumblr is an 'amazing' app",
		"Tumblr is 'awesome' and 'amazing' ",
		"Tumblr's users' are disappointed ",
		"Tumblr's 'acquisition' complete but users' loyalty doubtful"
	 };

	 
	 public static void main (String[] args) throws java.lang.Exception {
		Pattern p = Pattern.compile("(?:^|\\s)'([^']*?)'(?:$|\\s)", Pattern.MULTILINE);
		for (String arg : testcases) {
			System.out.print("Input: "+arg+" -> Matches: ");
			Matcher m = p.matcher(arg);
			if (m.find()) {
				System.out.print(m.group());
			while (m.find()) System.out.print(", "+m.group());
				System.out.println();
			} else {
				System.out.println("NONE");
			}
		} 
	 }
	 protected Log logger = LogFactory.getLog(getClass());

	 @Test
	 public void text() {
		
		String text = "abcd1234ef'gh5678ijk9abcd1234efgh56'78ijk9\n";
		
		logger.info("1: "+text.replaceAll("[a]", "&"));
		logger.info("2: "+text.replaceAll("[a-c[m-p]]", "&"));
		logger.info("3: "+text.replaceAll("^[0-9]*$", "&"));
		logger.info("4: "+text.replaceAll("[f-h]", "&"));
		
		logger.info("5: "+text.replaceAll("^b", "&"));
		logger.info("6: "+text.replaceAll("^b|$f", "&"));
		logger.info("7: "+text.replaceAll("^b|f$", "&"));
		logger.info("8: "+text.replaceAll("^b&&$f", "&"));
		
		logger.info("9: "+text.replaceAll("/\\-/g", "&"));
		
		logger.info("1: "+text.replaceAll("\\n", "&"));
		logger.info("1: "+text.replaceAll("\\'", "&"));
		logger.info("2: "+text.replaceAll("\\'-\\'", "&"));
		logger.info("3: "+text.replaceAll("^\\'-\\'$", "&"));
		
		logger.info("4: "+text.replaceAll("^\\'|\\'$", "&"));
		logger.info("5: "+text.replaceAll("^\\'|$\\'", "&"));
		logger.info("6: "+text.replaceAll("[\\'-\\']", "&"));
		logger.info("6: "+text.replaceAll("[\\']", "&"));
		logger.info("7: "+text.replaceAll("(\')^(\')$", "&"));
		logger.info("8: "+text.replaceAll("(\\')^(\\')$", "&"));
		logger.info("9: "+text.replaceAll("^(\')|(\')$", "&"));
		
		logger.info("0: "+text.replaceAll("^[']-[']$", "&"));

		logger.info("0: "+text.replaceAll("^['\"]*", ""));
		
		logger.info("0: "+text.replaceAll("['\"]*$", ""));
		
		    
		logger.info("0: "+text.replaceAll("'([^']*?)'", "''"));
		    
	        
		    
	 }
	 
}
