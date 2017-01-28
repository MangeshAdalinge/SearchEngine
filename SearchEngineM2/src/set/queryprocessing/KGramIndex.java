package set.queryprocessing;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import set.beans.TokenDetails;
import set.docprocess.PorterStemmer;

/**
 * Generates Kgrams
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class KGramIndex {
	public static HashMap<String, List<String>> kgrams = new HashMap<>();
	private String tokenSubstring = "";
	public static TreeMap<Integer, List<String>> topDictionarySuggestions = new TreeMap<>();

	public HashMap<String, List<String>> getkgramlist() {
		return kgrams;
	}

	/**
	 * generateKgram This functions generates and stores the kgram index for
	 * whole corpus
	 * 
	 * @param token
	 */
	public void generateKgram(String token) {
		int i = 0;
		String token1 = "$" + token + "$";
		while (i < token1.length() - 1) {
			tokenSubstring = token1.substring(i, i + 1);
			if (!tokenSubstring.equals("$")) {
				if (!kgrams.containsKey(tokenSubstring)) {
					kgrams.put(tokenSubstring, new ArrayList<>(Arrays.asList(token)));
				} else {
					kgrams.get(tokenSubstring).add(token);
				}
			}
			if (i < token1.length() - 1) {
				tokenSubstring = token1.substring(i, i + 2);
				if (!kgrams.containsKey(tokenSubstring)) {
					kgrams.put(tokenSubstring, new ArrayList<>(Arrays.asList(token)));
				} else {
					kgrams.get(tokenSubstring).add(token);
				}
			}
			if (i < token1.length() - 2) {
				tokenSubstring = token1.substring(i, i + 3);
				if (!kgrams.containsKey(tokenSubstring)) {
					kgrams.put(tokenSubstring, new ArrayList<>(Arrays.asList(token)));
				} else {
					kgrams.get(tokenSubstring).add(token);
				}
			}

			i++;
		}
	}

	/**
	 * readKGramsFromFile This function reads kgrams from disk
	 * 
	 * @param path
	 * @return kgrams
	 */
	@SuppressWarnings("unchecked")
	public static HashMap<String, List<String>> readKGramsFromFile(String path) {
		try {
			File toRead = new File(path + "/" + "KGram");
			FileInputStream fis = new FileInputStream(toRead);
			ObjectInputStream ois = new ObjectInputStream(fis);

			kgrams = (HashMap<String, List<String>>) ois.readObject();

			ois.close();
			fis.close();
			return kgrams;
		} catch (Exception e) {
			System.out.println(e);
		}
		return kgrams;

	}

	/**
	 * getWildCardKGrams This function generates kgrams for wild card queries
	 * 
	 * @param token1
	 * @return wildCardResult
	 */
	public static List<TokenDetails> getWildCardKGrams(String token1) {
		HashSet<String> nGramCandidates = new HashSet<>();
		List<TokenDetails> wildCardResult = new ArrayList<>();
		boolean threegramCondition = false;
		boolean twogramCondition = false;
		String[] splitWords;
		List<String> kgramCollection = new ArrayList<>();
		if (!token1.startsWith("*") && !token1.endsWith("*")) {
			token1 = "$" + token1 + "$";

		} else if (token1.startsWith("*")) {
			token1 = token1 + "$";
		} else if (token1.endsWith("*")) {
			token1 = "$" + token1;
		}
		splitWords = token1.split("\\*");
		for (String token : splitWords) {
			threegramCondition = false;
			twogramCondition = false;
			int i = 0;
			while (i < token.length()) {
				// if threegram is present in the kgram
				if (i < token.length() - 2) {
					if (kgrams.containsKey(token.substring(i, i + 3))) {
						nGramCandidates.addAll(kgrams.get(token.substring(i, i + 3)));
						threegramCondition = true;
						kgramCollection.add(token.substring(i, i + 3));
						i = i + 2;
					}
					// if twogram is present in the kgram
				} else if (i < token.length() - 1 && !threegramCondition) {
					if (kgrams.containsKey(token.substring(i, i + 2))) {
						nGramCandidates.addAll(kgrams.get(token.substring(i, i + 2)));
						kgramCollection.add(token.substring(i, i + 2));
						i = i + 1;
					}
					// if onegram is present in the kgram
				} else if (kgrams.containsKey(token.substring(i, i + 1)) && !threegramCondition && !twogramCondition) {
					nGramCandidates.addAll(kgrams.get(token.substring(i, i + 1)));
					kgramCollection.add(token.substring(i, i + 1));
				}
				i++;
			}

		}
		int i = 0;
		for (String candidateTerm : nGramCandidates) {
			if (starsMatching(splitWords, candidateTerm)) {
				if (i == 0) {
					wildCardResult = QueryResultProcessing
							.getPostingsResult(PorterStemmer.processWordAndStem(candidateTerm));
				} else {

					wildCardResult = QueryResultProcessing.getOrresult(
							null != wildCardResult ? wildCardResult : new ArrayList<>(),
							QueryResultProcessing.getPostingsResult(PorterStemmer.processWordAndStem(candidateTerm)));
				}
				i++;
			}
		}
		return wildCardResult;
	}

	/**
	 * starsMatching This function matches wild card pattern with candidate
	 * terms
	 * 
	 * @param splitWords
	 * @param testWord
	 * @return boolean of candidate matching
	 */
	public static boolean starsMatching(String[] splitWords, String testWord) {
		for (String token : splitWords) {
			if (token.startsWith("$")) {
				if (!testWord.startsWith(token.replace("$", ""))) {
					return false;
				}
			} else if (token.endsWith("$")) {
				if (!testWord.endsWith(token.replace("$", ""))) {
					return false;
				}

			} else {
				if (!testWord.contains(token)) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * getSpellingCorrectionKGrams This function provides corrected list of
	 * words using Jaccard Coefficient and Edit Distance
	 * 
	 * @param token
	 */
	public void getSpellingCorrectionKGrams(String token) {
		topDictionarySuggestions = new TreeMap<Integer, List<String>>();
		List<String> combinedGramsResult = new ArrayList<String>();

		int i = 0;
		token = ("$" + token + "$").toLowerCase();
		while (i < token.length() - 1) {
			tokenSubstring = token.substring(i, i + 1);
			// Generate onegrams
			if (!tokenSubstring.equals("$") && kgrams.containsKey(tokenSubstring)) {
				combinedGramsResult.addAll(kgrams.get(tokenSubstring));
			}
			// Generate twograms
			if (i < token.length() - 2) {
				tokenSubstring = token.substring(i, i + 3);
				if (kgrams.containsKey(tokenSubstring)) {
					combinedGramsResult.addAll(kgrams.get(tokenSubstring));
				}
			}
			// Generate threegrams
			if (i < token.length() - 1) {
				tokenSubstring = token.substring(i, i + 2);
				if (kgrams.containsKey(tokenSubstring)) {
					combinedGramsResult.addAll(kgrams.get(tokenSubstring));
				}
			}

			i++;
		}
		token = token.replace("$", "");
		List<String> usertoken = getnGrams(token);
		HashMap<Integer, List<String>> frequency = new HashMap<>();
		Collections.sort(combinedGramsResult);
		int k = 0;
		for (int j = 0; j < combinedGramsResult.size(); j++) {
			List<String> list = new ArrayList<String>();
			String tempWord = combinedGramsResult.get(j);
			if (tempWord.startsWith(token.substring(0, 1))
					&& (tempWord.length() > (token.length() - 2) && (tempWord.length() < (token.length() + 2)))) {
				if (j != 0 && combinedGramsResult.get(j - 1) == tempWord) {
					++k;

				} else {
					k = 0;
				}
				if (null != frequency.get(k)) {
					list = frequency.get(k);
					list.add(tempWord);
					frequency.put(k, list);
				} else {
					list.add(tempWord);
					frequency.put(k, list);
				}
			}
		}

		for (String word : frequency.get(token.length() + 3)) {
			decideThresholdForJC(word, usertoken, token);
		}
	}

	/**
	 * calulateJaccardCoefficent This function decides threshold for the JC
	 * value
	 * 
	 * @param finalCandidates
	 * @param usertoken
	 * @param token
	 * @throws NoSuchElementException
	 */
	private void decideThresholdForJC(String finalCandidates, List<String> usertoken, String token) {
		int previousValue = 100;
		List<String> cadidategram = getnGrams(finalCandidates);
		float calculatedValue = getJCvalue(usertoken, cadidategram);
		// System.out.println(finalCandidates + " : " + calculatedValue);
		// Threshold value for JC
		if (calculatedValue >= 0.39) {
			int editDisRes = editDistance(token, finalCandidates, token.length(), finalCandidates.length());
			if (editDisRes <= previousValue) {
				if (!topDictionarySuggestions.containsKey(editDisRes)) {
					topDictionarySuggestions.put(editDisRes, new ArrayList<>(Arrays.asList(finalCandidates)));
				} else {
					topDictionarySuggestions.get(editDisRes).add(finalCandidates);
				}
				previousValue = editDisRes;
			}
		}
	}

	/**
	 * editDistance THis function calculates Edit Distance uses Levenshtein
	 * distance
	 * 
	 * @param userString
	 * @param candidateString
	 * @param usrStrLen
	 * @param candidateStrLen
	 * @return
	 */
	private int editDistance(String userString, String candidateString, int usrStrLen, int candidateStrLen) {
		if (usrStrLen == 0)
			return candidateStrLen;
		if (candidateStrLen == 0)
			return usrStrLen;
		if (userString.charAt(usrStrLen - 1) == candidateString.charAt(candidateStrLen - 1))
			return editDistance(userString, candidateString, usrStrLen - 1, candidateStrLen - 1);
		return 1 + Math.min(
				Math.min(editDistance(userString, candidateString, usrStrLen, candidateStrLen - 1),
						editDistance(userString, candidateString, usrStrLen - 1, candidateStrLen)),
				editDistance(userString, candidateString, usrStrLen - 1, candidateStrLen - 1));
	}

	/**
	 * getJCvalue This function calculates Jaccard coefficient uses JC = (P(A)
	 * Intersection P(B))/(P(A U B))
	 * 
	 * @param usertoken
	 * @param cadidategram
	 * @return JC value
	 */
	private float getJCvalue(List<String> usertoken, List<String> cadidategram) {
		List<?> union = Stream.concat(usertoken.stream(), cadidategram.stream()).distinct()
				.collect(Collectors.toList());
		cadidategram.retainAll(usertoken);
		return (float) cadidategram.size() / union.size();
	}

	/**
	 * getnGrams This funcion generates ngrams for JC calculations
	 * 
	 * @param token
	 * @return List of grams
	 */

	private List<String> getnGrams(String token) {
		List<String> nGramResult = new ArrayList<>();
		token = "$" + token + "$";
		for (int i = 0; i < token.length() - 1; i++) {

			tokenSubstring = token.substring(i, i + 1);
			if (!tokenSubstring.equals("$"))
				nGramResult.add(token.substring(i, i + 1));

			if (i < token.length() - 1) {
				tokenSubstring = token.substring(i, i + 2);
				if (!nGramResult.contains(tokenSubstring))
					nGramResult.add(tokenSubstring);
			}

			if (i < token.length() - 2) {
				tokenSubstring = token.substring(i, i + 3);
				if (!nGramResult.contains(tokenSubstring))
					nGramResult.add(tokenSubstring);
			}

		}
		return nGramResult;
	}

}
