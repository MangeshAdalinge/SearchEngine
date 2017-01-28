package set.queryprocessing;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import set.beans.RankComparator;
import set.beans.TokenDetails;
import set.disk.DiskInvertedIndex;
import set.docprocess.BiWordIndexing;
import set.docprocess.PorterStemmer;
import set.docprocess.PositionalInvertedIndex;
import set.gui.IndexPanel;

/**
 * Class QueryResultProcessing
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 *
 */
public class QueryResultProcessing {

	private static HashMap<String, List<TokenDetails>> results = new HashMap<>();
	private Integer kNearVal = 0;
	private PositionalInvertedIndex pindexobj;
	private BiWordIndexing bindexobj;

	public PositionalInvertedIndex getPindexobj() {
		return pindexobj;
	}

	public void setPindexobj(PositionalInvertedIndex pindexobj) {
		this.pindexobj = pindexobj;
	}

	public BiWordIndexing getBindexobj() {
		return bindexobj;
	}

	public void setBindexobj(BiWordIndexing bindexobj) {
		this.bindexobj = bindexobj;
	}

	public HashMap<String, List<TokenDetails>> getResults() {
		return results;
	}

	/**
	 * gets phrase query results
	 * 
	 * @param kIndex
	 * 
	 * @param oldProcessString:
	 *            input query
	 */
	public void getPhraseQueryresult(String oldProcessString) {
		String[] phraseQuery = oldProcessString.split(" ");
		if (phraseQuery.length == 1) {
			results.put("\"" + phraseQuery[0].trim() + "\"", getPostingsResult(phraseQuery[0]));
		} else if (phraseQuery.length < 3 && kNearVal == 0) {
			getPhraseQueryBiword(phraseQuery[0], phraseQuery[1]);
		} else {

			String firstElement = phraseQuery[0];
			List<TokenDetails> firstPhraseElement = new ArrayList<>();
			firstPhraseElement = getPostingsResult(firstElement);
			for (int k = 1; k < phraseQuery.length; k++) {
				String nextElement = phraseQuery[k];
				if (nextElement.toLowerCase().startsWith("near/")) {
					String[] temp = nextElement.split("/");
					kNearVal = Integer.parseInt(temp[1]);
					nextElement = phraseQuery[k + 1];
					k = k + 1;
				} else {
					kNearVal = k;
				}
				List<TokenDetails> temp = new ArrayList<>();
				List<TokenDetails> nextPhraseElement = new ArrayList<>();
				nextPhraseElement = getPostingsResult(nextElement);
				int i = 0, j = 0;
				while (i < firstPhraseElement.size() && j < nextPhraseElement.size()) {
					TokenDetails docList1 = firstPhraseElement.get(i);
					TokenDetails docList2 = nextPhraseElement.get(j);
					if (docList1.getDocId() == docList2.getDocId()) {
						List<Integer> temp1 = matchPosition(docList1.getPosition(), docList2.getPosition(), k,
								kNearVal);
						if (temp1.size() > 0) {
							docList1.setPosition(temp1);
							temp.add(docList1);
						}
						i++;
						j++;
					} else if (docList1.getDocId() > docList2.getDocId()) {
						j++;
					} else {
						i++;
					}
				}
				firstPhraseElement = temp;
				temp = new ArrayList<>();
			}
			results.put(oldProcessString.replaceAll(" ", ""), firstPhraseElement);
		}
	}

	/**
	 * getPostingsResult This function gets positional posting results for given
	 * term
	 * 
	 * @param query
	 * @return posting list
	 */
	public static List<TokenDetails> getPostingsResult(String query) {

		if (results.containsKey(query)) {
			return results.get(query);
		}
		query = PorterStemmer.processToken(PorterStemmer.processWord(query.replaceAll("-", "")));
		long postingsPosition = IndexPanel.dIndexP.binarySearchVocabulary(query);
		if (postingsPosition >= 0) {
			return IndexPanel.dIndexP.readPostingsFromFile(IndexPanel.dIndexP.getmPostings(), postingsPosition, query);
		}

		return new ArrayList<TokenDetails>();
	}

	/**
	 * gets result for biword phrase query
	 * 
	 * @param term1
	 * @param term2
	 */
	private void getPhraseQueryBiword(String term1, String term2) {

		String term = PorterStemmer
				.processToken(PorterStemmer.processWordHypen(PorterStemmer.processWordAndStem(term1))[0]) + " "
				+ PorterStemmer
						.processToken(PorterStemmer.processWordHypen(PorterStemmer.processWordAndStem(term2))[0]);
		long postingsPosition = IndexPanel.dIndexB.binarySearchVocabulary(term);
		if (postingsPosition >= 0) {
			List<TokenDetails> firstPhraseElement = IndexPanel.dIndexB
					.readPostingsFromFile(IndexPanel.dIndexB.getmPostings(), postingsPosition, term);
			results.put(term1 + term2, firstPhraseElement);
		} else {
			results.put(term1 + term2, new ArrayList<>());
		}
	}

	/**
	 * Matches the token position in document
	 * 
	 * @param position
	 * @param position2
	 * @param positionDiff
	 * @param kNearVal2
	 * @return
	 */
	private List<Integer> matchPosition(List<Integer> position, List<Integer> position2, int positionDiff,
			Integer kNearVal2) {
		int x = 0, y = 0;
		/*
		 * if (kNearVal2 > 0) { positionDiff = kNearVal2 * positionDiff; }
		 */
		positionDiff = kNearVal2;
		List<Integer> result = new ArrayList<>();
		while (x < position.size() && y < position2.size()) {
			int firstElementPosition = position.get(x);
			int secondElementPosition = position2.get(y);
			if (secondElementPosition - firstElementPosition <= positionDiff
					&& secondElementPosition - firstElementPosition >= 0) {
				result.add(firstElementPosition);
				x++;
				y++;
			} else if (firstElementPosition > secondElementPosition) {
				y++;
			} else {
				x++;
			}
		}
		return result;
	}

	/**
	 * Gets result of and query
	 * 
	 * @param oprand12
	 * @param oprand22
	 */
	public void getAndQueryresult(String oprand12, String oprand22) {
		if (oprand12.startsWith("-") && !oprand12.contains(" ")) {
			getAndNotQueryresult(oprand22, oprand12);
			return;
		} else if (oprand22.startsWith("-") && !oprand22.contains(" ")) {
			getAndNotQueryresult(oprand12, oprand22);
			return;
		}
		List<TokenDetails> oprand1DocList = getPostingsResult(oprand12);
		List<TokenDetails> oprand2DocList = getPostingsResult(oprand22);
		List<TokenDetails> andQueryresult = new ArrayList<>();
		int i = 0, j = 0;
		while (oprand1DocList != null && oprand2DocList != null && i < oprand1DocList.size()
				&& j < oprand2DocList.size()) {
			TokenDetails docList1 = oprand1DocList.get(i);
			TokenDetails docList2 = oprand2DocList.get(j);
			if (docList1.getDocId() == docList2.getDocId()) {
				andQueryresult.add(docList1);
				i++;
				j++;
			} else if (docList1.getDocId() > docList2.getDocId()) {
				j++;
			} else {
				i++;
			}
		}
		results.put(oprand12 + " " + oprand22, andQueryresult);
	}

	/**
	 * Gets result of and not query
	 * 
	 * @param oprand12
	 * @param oprand22
	 */
	public void getAndNotQueryresult(String oprand12, String oprand22) {
		List<TokenDetails> oprand1DocList;
		List<TokenDetails> oprand2DocList;
		List<TokenDetails> andNotQueryresult = new ArrayList<>();
		oprand1DocList = getPostingsResult(oprand12);
		oprand2DocList = getPostingsResult(oprand22);
		int i = 0, j = 0;
		TokenDetails docList1 = new TokenDetails();
		TokenDetails docList2 = new TokenDetails();
		while ((oprand1DocList.size() > i || (oprand2DocList.size() > j))) {
			if (oprand1DocList.size() <= i && oprand2DocList != null && oprand2DocList.size() > j) {
				break;
			}
			if (oprand2DocList.size() <= j && oprand1DocList != null && oprand1DocList.size() > i) {
				for (int k = i; k < oprand1DocList.size(); k++) {
					docList1 = oprand1DocList.get(k);
					andNotQueryresult.add(docList1);
				}
				break;
			}
			docList1 = oprand1DocList.get(i);
			docList2 = oprand2DocList.get(j);
			if (docList1.getDocId() > docList2.getDocId()) {
				j++;
			} else if (docList2.getDocId() > docList1.getDocId()) {
				andNotQueryresult.add(docList1);
				i++;
			} else {
				i++;
				j++;
			}
		}
		results.put(oprand12 + " " + oprand22, andNotQueryresult);
	}

	/**
	 * Gets result for single word query
	 * 
	 * @param oprand12
	 */
	public void getSingleOprandResult(String oprand12) {
		results.put(oprand12.trim(), getPostingsResult(oprand12));
	}

	/**
	 * Gets results for or query
	 * 
	 * @param oprand12
	 * @param oprand22
	 */
	public void getOrQueryresult(String oprand12, String oprand22) {
		if (oprand22.startsWith("-") && !oprand22.contains(" ")) {
			getOrNotQueryresult(oprand12, oprand22);
			return;
		} else if (oprand12.startsWith("-") && !oprand12.contains(" ")) {
			getOrNotQueryresult(oprand22, oprand12);
			return;
		}
		List<TokenDetails> oprand1DocList = getPostingsResult(oprand12);
		List<TokenDetails> oprand2DocList = getPostingsResult(oprand22);
		results.put(oprand12 + " + " + oprand22, getOrresult(oprand1DocList, oprand2DocList));
	}

	/**
	 * Unions two posting lists
	 * 
	 * @param oprand1DocList
	 * @param oprand2DocList
	 * @return
	 */
	public static List<TokenDetails> getOrresult(List<TokenDetails> oprand1DocList, List<TokenDetails> oprand2DocList) {
		List<TokenDetails> orQueryresult = new ArrayList<>();
		int i = 0, j = 0;
		TokenDetails docList1 = new TokenDetails();
		TokenDetails docList2 = new TokenDetails();
		while ((oprand1DocList.size() > i || (oprand2DocList.size() > j))) {
			if (oprand1DocList.size() <= i && oprand2DocList != null && oprand2DocList.size() > j) {
				for (int k = j; k < oprand2DocList.size(); k++) {
					docList2 = oprand2DocList.get(k);
					orQueryresult.add(docList2);
				}
				break;
			}
			if (oprand2DocList.size() <= j && oprand1DocList != null && oprand1DocList.size() > i) {
				for (int k = i; k < oprand1DocList.size(); k++) {
					docList1 = oprand1DocList.get(k);
					orQueryresult.add(docList1);
				}
				break;
			}
			docList1 = oprand1DocList.get(i);
			docList2 = oprand2DocList.get(j);
			if (docList1.getDocId() > docList2.getDocId()) {
				orQueryresult.add(docList2);
				j++;
			} else if (docList2.getDocId() > docList1.getDocId()) {
				orQueryresult.add(docList1);
				i++;
			} else {
				orQueryresult.add(docList1);
				i++;
				j++;
			}
		}
		return orQueryresult;
	}

	/**
	 * Gets the result for or not query
	 * 
	 * @param oprand12
	 * @param oprand22
	 */
	public void getOrNotQueryresult(String oprand12, String oprand22) {
		List<TokenDetails> orQueryresult = new ArrayList<>();
		List<TokenDetails> oprand1DocList = getPostingsResult(oprand12);
		List<TokenDetails> oprand2DocList = getPostingsResult(oprand22);
		int i = 0, j = 0;
		TokenDetails docList1 = new TokenDetails();
		TokenDetails docList2 = new TokenDetails();
		while ((oprand1DocList.size() > i || (oprand2DocList.size() > j))) {
			if (oprand1DocList.size() <= i && oprand2DocList != null && oprand2DocList.size() > j) {
				break;
			}
			if (oprand2DocList.size() <= j && oprand1DocList != null && oprand1DocList.size() > i) {
				for (int k = i; k < oprand1DocList.size(); k++) {
					docList1 = oprand1DocList.get(k);
					orQueryresult.add(docList1);
				}
				break;
			}
			docList1 = oprand1DocList.get(i);
			docList2 = oprand2DocList.get(j);
			if (docList1.getDocId() > docList2.getDocId()) {
				j++;
			} else if (docList2.getDocId() > docList1.getDocId()) {
				orQueryresult.add(docList1);
				i++;
			} else {
				orQueryresult.add(docList1);
				i++;
				j++;
			}
		}
		results.put(oprand12 + " + " + oprand22, orQueryresult);
	}

	/**
	 * Generate the wild card query results
	 * 
	 * @param group
	 * @throws IOException
	 */
	public void getWildCardQueryResult(String wildCardTerm) throws IOException {
		List<TokenDetails> kgramQueryresult = new ArrayList<>();
		kgramQueryresult = KGramIndex.getWildCardKGrams(PorterStemmer.processWord(wildCardTerm));
		results.put(wildCardTerm, kgramQueryresult);
	}

	/**
	 * Generates the accumulators value and calculates the score of document
	 * 
	 * @param rankQuery
	 * @return
	 * @throws FileNotFoundException
	 */
	public PriorityQueue<RankComparator> getAdValue(String rankQuery) throws FileNotFoundException {
		String[] splitQuery = rankQuery.split(" ");
		HashMap<Integer, RankComparator> wqtResults = new HashMap<>();

		for (String term : splitQuery) {
			List<TokenDetails> termlist = new ArrayList<>();
			if (term.contains("*")) {
				termlist = KGramIndex.getWildCardKGrams(term);
			}
			long postingsPosition = IndexPanel.dIndexP
					.binarySearchVocabulary(PorterStemmer.processWordAndStem(term.replace("-", "")));
			if (postingsPosition >= 0 || termlist.size() > 0) {
				if (!(termlist.size() > 0)) {
					termlist = IndexPanel.dIndexP.readPostingsFromFile(IndexPanel.dIndexP.getmPostings(),
							postingsPosition, term);
					System.out.println(term + " term size :" + termlist.size());
				}
				double wqtSingleTerm = Math.log(1 + (double) IndexPanel.fileNameLists.size() / termlist.size());
				System.out.println("WQT: " + wqtSingleTerm);
				for (int i = 0; i < termlist.size(); i++) {
					double accumuatorAd = wqtSingleTerm * termlist.get(i).getDocWeight();
					if (wqtResults.containsKey(termlist.get(i).getDocId())) {
						wqtResults.put(termlist.get(i).getDocId(), new RankComparator(termlist.get(i).getDocId(),
								accumuatorAd + (wqtResults.get(termlist.get(i).getDocId())).getScore()));
					} else {
						wqtResults.put(termlist.get(i).getDocId(),
								new RankComparator(termlist.get(i).getDocId(), accumuatorAd));
					}
				}
			}
		}
		PriorityQueue<RankComparator> pQ = new PriorityQueue<RankComparator>();
		for (RankComparator rc : wqtResults.values()) {
			rc.setScore(rc.getScore() / DiskInvertedIndex.readLdValueFromFile(rc.getDocId() * 8));
			pQ.add(rc);
		}
		return pQ;
	}
}
