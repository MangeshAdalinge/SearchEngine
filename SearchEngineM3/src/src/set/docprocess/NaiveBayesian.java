package src.set.docprocess;

import src.set.beans.TermInfo;
import src.set.beans.TokenDetails;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.*;

/**
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit *
 *
 *         Bayesian Classifier
 * 
 */
public class NaiveBayesian {

	private String[] dictionary;
	// map to store term and its p(t|c) value for terms in T
	private Map<String, Double> hamprob = new HashMap<>();
	private Map<String, Double> madprob = new HashMap<>();
	private Map<String, Double> jayprob = new HashMap<>();

	private int[] classPtcDenom = new int[3];
	private int selectedWords = 50;
	// set that stores important vocab terms from all the classes
	private ArrayList<TermInfo> itcTerms = new ArrayList<>();

	public NaiveBayesian(Indexing ind, Rocchio rocchio) {
		dictionary = Rocchio.getAllIndex().getInvertedIndexDictionary();
	}

	/**
	 * The method to perform training of the conflicted documents
	 */
	public void train() {

		Set<String> important = new HashSet<String>();
		for (int i = 0; i < dictionary.length; i++) {
			calculateItc("MADISON", dictionary[i], Rocchio.getMadisonFileList());
			calculateItc("HAMILTON", dictionary[i], Rocchio.getHamiltonFileList());
			calculateItc("JAY", dictionary[i], Rocchio.getJayFileList());
		}
		Collections.sort(itcTerms, Collections.reverseOrder());

		double ftcMadison = 0;
		double ftcHamilton = 0;
		double ftcJay = 0;

		for (int i = 0; i < selectedWords; i++) {
			important.add(itcTerms.get(i).getTerm());
		}
		for (String str : important) {

			ftcHamilton = Collections.frequency(Rocchio.getHamiltonVocab(), str) + 1;
			classPtcDenom[0] += ftcHamilton;
			hamprob.put(str, ftcHamilton);

			ftcMadison = (Collections.frequency(Rocchio.getMadisonVocab(), str) + 1);
			classPtcDenom[1] += ftcMadison;
			madprob.put(str, ftcMadison);

			ftcJay = (Collections.frequency(Rocchio.getJayVocab(), str) + 1);
			classPtcDenom[2] += ftcJay;
			jayprob.put(str, ftcJay);
		}
	}

	/**
	 * The method to calculate mutual information of each vocab term
	 * 
	 * @param classname
	 * @param term
	 * @param classfilelist
	 */
	public void calculateItc(String classname, String term, Set<String> classfilelist) {
		Set<String> commonDocs = new HashSet<>();
		double N00 = 0, N01 = 0, N10 = 0, N11 = 0, N1x = 0, Nx1 = 0, N0x = 0, Nx0 = 0, N = 0;
		int numOfDocs = 0;
		double itcValue;
		commonDocs = findCommonDocuments(Rocchio.getAllIndex().getInvertedIndexPostings(term), classfilelist);
		for (String docName : commonDocs) {
			numOfDocs = classfilelist.contains(docName) ? numOfDocs + 1 : numOfDocs;

		}
		N11 = numOfDocs;
		N10 = Rocchio.getAllIndex().getInvertedIndexPostings(term).size() - numOfDocs;
		N01 = classfilelist.size() - N11;
		N00 = Rocchio.getFileNameLists().size() - classfilelist.size() - N10;
		N = N11 + N10 + N01 + N00;
		N0x = N00 + N01;
		N1x = N10 + N11;
		Nx0 = N00 + N10;
		Nx1 = N01 + N11;

		itcValue = ((N11 / N) * (Math.log((N * N11) / (N1x * Nx1)) / Math.log(2))
				+ (N10 / N) * (Math.log((N * N10) / (N1x * Nx0)) / Math.log(2))
				+ (N01 / N) * (Math.log((N * N01) / (N0x * Nx1)) / Math.log(2))
				+ (N00 / N) * (Math.log((N * N00) / (N0x * Nx0)) / Math.log(2)));

		if (!Double.isNaN(itcValue)) {
			itcTerms.add(new TermInfo(term, itcValue));
		}
	}

	/**
	 * The method to find the intersection of documents
	 * 
	 * @param postings
	 * @param file
	 * @return
	 */
	private Set<String> findCommonDocuments(List<TokenDetails> postings, Set<String> file) {
		Set<String> docs = new HashSet<>();
		for (TokenDetails tokendet : postings) {
			docs.add(Rocchio.getFileNameLists().get(tokendet.getDocId()));
		}
		return docs;
	}

	/**
	 * 
	 * Method to test the classifier on the unknown documents
	 * 
	 * @throws IOException
	 */
	public void test() throws IOException {

		Path path = Paths.get("C:/Users/mange/OneDrive/Documents/Federalist_ByAuthors/HAMILTONORMADISON/");
		int trainingSize = Rocchio.getMadisonFileList().size() + Rocchio.getHamiltonFileList().size()
				+ Rocchio.getJayFileList().size();
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			double hamDocClassresult = 0, madDocClassresult = 0, jayDocClassresult = 0;

			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				// make sure we only process the current working

				if (path != null) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws FileNotFoundException {

				// only process .txt files
				if (file.toString().endsWith(".txt")) {
					try {
						// for the remaining 11 conflicted files
						// Get a token stream on the file
						TokenStream stream = new SimpleTokenStream(file.toFile());
						hamDocClassresult = 0;
						madDocClassresult = 0;
						jayDocClassresult = 0;
						while (stream.hasNextToken()) {
							String str = Rocchio.getAllIndex().processWord(stream.nextToken());

							// for each term
							if (hamprob.containsKey(str)) {
								hamDocClassresult += Math.log((double) hamprob.get(str) / (classPtcDenom[0]));
							}
							if (madprob.containsKey(str)) {
								madDocClassresult += Math.log((double) madprob.get(str) / (classPtcDenom[1]));
							}
							if (jayprob.containsKey(str)) {
								jayDocClassresult += Math.log((double) jayprob.get(str) / (classPtcDenom[2]));
							}

						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

				hamDocClassresult = hamDocClassresult
						+ Math.log((double) Rocchio.getHamiltonFileList().size() / trainingSize);
				madDocClassresult = madDocClassresult
						+ Math.log((double) Rocchio.getMadisonFileList().size() / trainingSize);
				jayDocClassresult = jayDocClassresult
						+ Math.log((double) Rocchio.getJayFileList().size() / trainingSize);

				// System.out.println(hamDocClassresult+" "+madDocClassresult+"
				// "+jayDocClassresult);
				if (hamDocClassresult > madDocClassresult && hamDocClassresult > jayDocClassresult)
					System.out.println(file.getFileName() + " is written by " + "HAMILTON");
				if (madDocClassresult > hamDocClassresult && madDocClassresult > jayDocClassresult)
					System.out.println(file.getFileName() + " is written by " + "MADISON");
				if (jayDocClassresult > hamDocClassresult && jayDocClassresult > madDocClassresult)
					System.out.println(file.getFileName() + " is written by " + "JAY");

				return FileVisitResult.CONTINUE;
			}

		});

	}

}
