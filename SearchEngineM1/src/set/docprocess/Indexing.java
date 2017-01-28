package set.docprocess;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.regex.Pattern;

import set.beans.TokenDetails;
import set.gui.MainJFrame;
/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */
public class Indexing {
	// biWord index list
	private HashMap<String, List<TokenDetails>> mBiWordIndex;
	// PI index list
	private HashMap<String, List<TokenDetails>> mIndex = new HashMap<>();
	private PorterStemmer pStem = new PorterStemmer();
	// regex for detecting multiple hypen in string
	private Pattern hyphen = Pattern.compile("[-]{1,}");

	public HashMap<String, List<TokenDetails>> getmBiWordIndex() {
		return mBiWordIndex;
	}

	public void setmBiWordIndex(HashMap<String, List<TokenDetails>> mBiWordIndex) {
		this.mBiWordIndex = mBiWordIndex;
	}

	public HashMap<String, List<TokenDetails>> getmIndex() {
		return mIndex;
	}

	public void setmIndex(HashMap<String, List<TokenDetails>> mIndex) {
		this.mIndex = mIndex;
	}

	public Indexing() {
		mBiWordIndex = new HashMap<>();
	}

	/**
	 * processes the token by removing all alphanumeric from first and last position
	 * removing all singles quotes from the query
	 * converting into lowercase
	 * @param next
	 * @return
	 */
	public String processWord(String next) {
		next = next.replaceAll("'", "");
		return pStem.processToken(next.replaceAll("^[^a-zA-Z0-9\\s]+|[^a-zA-Z0-9\\s]+$", "").toLowerCase());
	}

	/**
	 * Replaces all multi hyphens into single hyphen
	 * @return
	 */
	public String[] processWordHypen(String next) {
		String[] splitTok = new String[] { next };
		if (next.contains("-")) {
			if (hyphen.matcher(next).find()) {
				next = next.replaceAll("--+", "-");
				splitTok = next.split("-");
			}

		}

		return splitTok;
	}

	/**
	 * maps Positon of the token and documentId to the tokenDetail Object 
	 * and stores it in hashmap
	 * @param term
	 * @param docID
	 * @param wordPosition
	 */
	public void addTermInvertedIndex(String term, int docID, int wordPosition) {

		List<TokenDetails> list = new ArrayList<>();
		TokenDetails docList = new TokenDetails(docID, wordPosition);

		try {
			// term=pStem.processToken(term);
			if (mIndex.containsKey(term)) {
				list = mIndex.get(term);

				docList = list.get(list.size() - 1);
				if (docList.getDocId() == docID) {
					docList.setPosition(wordPosition);
				} else {

					docList = new TokenDetails(docID, wordPosition);
					list.add(docList);
					mIndex.put(term, list);

				}
			} else {

				list.add(docList);
				mIndex.put(term, list);
			}

		} catch (Exception e) {
			Logger.getLogger(Indexing.class.getName()).log(java.util.logging.Level.SEVERE, null,e);
			System.out.print("addTerm  " + e);
		}

	}

	/**
	 * maps Positon of the token by combining first and second token and documentId to the tokenDetail Object 
	 * and stores it in hashmap
	 * @param term1
	 * @param term2
	 * @param documentID
	 */
	public void AddBiWordTerm(String term1, String term2, Integer documentID) {

		String term = term1 + " " + term2;
		List<TokenDetails> mArr = new ArrayList<>();
		TokenDetails biDocList = new TokenDetails();

		if (mBiWordIndex.containsKey(term)) {
			mArr = mBiWordIndex.get(term);
			int docId = mArr.get(mArr.size() - 1).getDocId();
			if (documentID != docId) {
				biDocList.setDocId(documentID);
				mBiWordIndex.get(term).add(biDocList);
			}

		} else {
			biDocList.setDocId(documentID);

			mArr.add(biDocList);
			mBiWordIndex.put(term, mArr);
		}

	}

	/**
	 * returns list of documents id containing the term for Biword index
	 * @param term
	 * @return
	 */
	public List<TokenDetails> getPostingsBiWord(String term) {

		return mBiWordIndex.get(term);
	}


	/**
	 * returns list of documents id containing the term for PI index
	 * @param term
	 * @return
	 */
	public List<TokenDetails> getInvertedIndexPostings(String term) {
		// TO-DO: return the postings list for the given term from the index
		// map.
		return mIndex.get(term);

	}

	/**
	 * returns size of the PI index
	 * @return
	 */
	public int getTermCountPII() {
		// TO-DO: return the number of terms in the index.

		return mIndex.size();
	}

	/**
	 * returns complete list of the vocabulary term of the PI index
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	public String[] getInvertedIndexDictionary() {
		// TO-DO: fill an array of Strings with all the keys from the hashtable.
		// Sort the array and return it.
		String[] dictionary = new String[mIndex.size()];
		Iterator it = mIndex.entrySet().iterator();
		int i = 0;

		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			dictionary[i] = pair.getKey().toString();
			
			i++;

		}
		Arrays.sort(dictionary);
		return dictionary;
	}

}
