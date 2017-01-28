package set.queryprocessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.PriorityQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import set.beans.RankComparator;
import set.beans.TokenDetails;

/**
 * Class QueryLiterals
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class QueryLiterals {
	private QueryResultProcessing qrp = new QueryResultProcessing();
	// Modified new variable
	private Pattern phrasePattern = Pattern.compile("-?\\s*\"[^\"]+\"");
	private Pattern wildCardPattern = Pattern.compile("\\S*\\*\\S*");
	private Matcher phraseMatcher;
	private Matcher wildCardMatcher;

	/**
	 * Query parsing
	 * 
	 * @param queryString
	 * @return
	 * @throws IOException
	 */
	public List<TokenDetails> splitQueryString(String queryString) throws IOException {
		phraseMatcher = phrasePattern.matcher(queryString.trim());
		wildCardMatcher = wildCardPattern.matcher(queryString.trim());
		// wild card token detection
		while (wildCardMatcher.find()) {
			qrp.getWildCardQueryResult(wildCardMatcher.group());
		}
		// phrase detection
		while (phraseMatcher.find()) {
			qrp.getPhraseQueryresult(phraseMatcher.group());
			queryString = queryString.replaceAll(phraseMatcher.group(), phraseMatcher.group().replaceAll(" ", ""));
		}
		String[] orOperation = queryString.split("\\+");
		String finalAndString = "";
		String finalOrString = "";
		for (int j = 0; j < orOperation.length; j++) {
			String[] AndOperation = orOperation[j].trim().split(" ");
			String prevousAnd = "";
			for (int i = 0; i < AndOperation.length; i++) {
				if (i < AndOperation.length - 1 && AndOperation.length > 1) {
					prevousAnd += AndOperation[i].trim() + " ";
					if (!AndOperation[i + 1].trim().startsWith("-") || !prevousAnd.trim().startsWith("-")) {
						qrp.getAndQueryresult(prevousAnd.trim(), AndOperation[i + 1].trim());
					} else {
						if (AndOperation[i + 1].trim().startsWith("-")) {
							qrp.getAndNotQueryresult(prevousAnd.trim(), AndOperation[i + 1].trim());
						} else if (prevousAnd.trim().startsWith("-")) {
							qrp.getAndNotQueryresult(AndOperation[i + 1], prevousAnd);
						}
					}
					finalAndString = prevousAnd.trim() + " " + AndOperation[i + 1].trim();
				}
			}
		}
		finalOrString = finalAndString;
		boolean temp = true;
		for (int z = 0; z < orOperation.length; z++) {
			if (z < orOperation.length - 1 && orOperation.length > 1) {
				if (temp) {
					finalOrString = "";
					finalOrString += orOperation[z].trim();
					temp = false;
				}
				if (!finalOrString.trim().startsWith("-") || !orOperation[z + 1].trim().startsWith("-")) {
					qrp.getOrQueryresult(finalOrString.trim(), orOperation[z + 1].trim());
				} else {
					if (finalOrString.trim().startsWith("-")) {
						qrp.getOrNotQueryresult(orOperation[z + 1].trim(), finalOrString.trim());
					} else if (orOperation[z + 1].trim().startsWith("-")) {
						qrp.getAndNotQueryresult(finalOrString.trim(), orOperation[z + 1].trim());
					}
				}
				finalOrString = finalOrString.trim() + " + " + orOperation[z + 1].trim();
			} else if (z == 0 && !orOperation[z].contains(" ")) {
				finalOrString += orOperation[z];
				qrp.getSingleOprandResult(finalOrString.trim());
			}
		}

		if (qrp.getResults() != null) {
			System.out.println(finalOrString + ": " + qrp.getResults().get(finalOrString).size());
			return qrp.getResults().get(finalOrString);
		} else
			return null;
	}

	/**
	 * ranked query results
	 * 
	 * @param rankQuery
	 * @return
	 * @throws FileNotFoundException
	 */
	public PriorityQueue<RankComparator> rankQueryString(String rankQuery) throws FileNotFoundException {
		PriorityQueue<RankComparator> wqtValue = qrp.getAdValue(rankQuery);
		return wqtValue;
	}
}
