package src.set.docprocess;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import src.set.beans.TokenDetails;

/**
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 * 
 *         Rocchio Classifier
 *
 */
public class Rocchio {
	private static HashMap<Integer, String> fileNameLists = new HashMap<>();

	// for all folder and Hamilton OR Madison
	private static Indexing allIndex = new Indexing();
	private int l = 0;

	// Hashmap for files and their vectors
	private HashMap<String, List<Double>> vectorList = new HashMap<>();
	private double finalsum = 0;
	private static Set<String> hamiltonFileList = new HashSet<>();
	private static Set<String> madisonFileList = new HashSet<>();
	private static Set<String> jayFileList = new HashSet<>();
	// vocab of the three classes
	private static List<String> hamiltonVocab = new ArrayList<>();
	private static List<String> madisonVocab = new ArrayList<>();
	private static List<String> jayVocab = new ArrayList<>();

	/**
	 * This method returns the complete vector list of all documents
	 * 
	 * @return
	 */
	public HashMap<String, List<Double>> getVectorList() {
		return vectorList;
	}

	/**
	 * setter for complete vectorlist
	 * 
	 * @param vectorList
	 */
	public void setVectorList(HashMap<String, List<Double>> vectorList) {
		this.vectorList = vectorList;
	}

	/**
	 * getter for complete vectorlist
	 * 
	 * @return
	 */
	public static HashMap<Integer, String> getFileNameLists() {
		return fileNameLists;
	}

	/**
	 * setter for filenamelist map
	 * 
	 * @param fileNameLists
	 */
	public void setFileNameLists(HashMap<Integer, String> fileNameLists) {
		Rocchio.fileNameLists = fileNameLists;
	}

	/**
	 * getter for Indexing class object
	 * 
	 * @return
	 */
	public static Indexing getAllIndex() {
		return allIndex;
	}

	/**
	 * 
	 * @param allIndex
	 */
	public static void setAllIndex(Indexing allIndex) {
		Rocchio.allIndex = allIndex;
	}

	/**
	 * getter for Hamilton vocab
	 * 
	 * @return
	 */
	public static List<String> getHamiltonVocab() {
		return hamiltonVocab;
	}

	/**
	 * getter for Madison vocab
	 * 
	 * @return
	 */
	public static List<String> getMadisonVocab() {
		return madisonVocab;
	}

	/**
	 * getter for Hamilton file list
	 * 
	 * @return
	 */
	public static Set<String> getHamiltonFileList() {
		return hamiltonFileList;
	}

	/**
	 * setter for Hamilton file list
	 * 
	 * @param hamiltonFileList
	 */
	public static void setHamiltonFileList(Set<String> hamiltonFileList) {
		Rocchio.hamiltonFileList = hamiltonFileList;
	}

	/**
	 * getter for Madison file list
	 * 
	 * @return
	 */
	public static Set<String> getMadisonFileList() {
		return madisonFileList;
	}

	/**
	 * setter for Madison file list
	 * 
	 * @param madisonFileList
	 */
	public static void setMadisonFileList(Set<String> madisonFileList) {
		Rocchio.madisonFileList = madisonFileList;
	}

	/**
	 * getter for Jay file list
	 * 
	 * @return
	 */
	public static Set<String> getJayFileList() {
		return jayFileList;
	}

	/**
	 * setter for Jay file list
	 * 
	 * @param jayFileList
	 */
	public static void setJayFileList(Set<String> jayFileList) {
		Rocchio.jayFileList = jayFileList;
	}

	/**
	 * getter for Jay vocab
	 * 
	 * @return
	 */
	public static List<String> getJayVocab() {
		return jayVocab;
	}

	/**
	 * setter for Jay vocab
	 * 
	 * @param jayVocab
	 */
	public static void setJayVocab(List<String> jayVocab) {
		Rocchio.jayVocab = jayVocab;
	}

	/**
	 * 
	 * @param hamiltonVocab
	 */
	public static void setHamiltonVocab(List<String> hamiltonVocab) {
		Rocchio.hamiltonVocab = hamiltonVocab;
	}

	/**
	 * setter for Madison vocab
	 * 
	 * @param madisonVocab
	 */
	public static void setMadisonVocab(List<String> madisonVocab) {
		Rocchio.madisonVocab = madisonVocab;
	}

	/**
	 * indexing of the files and creating filenamelist
	 * 
	 * @param path
	 * @return
	 * @throws IOException
	 */
	public FileVisitResult visitFiles(Path path) throws IOException {

		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {

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

					fileNameLists.put(l, file.getFileName().toString());

					try {
						// for the remaining 11 conflicted files
						// Get a token stream on the file
						TokenStream stream = new SimpleTokenStream(file.toFile());
						int i = 0;
						while (stream.hasNextToken()) {
							String token = allIndex.processWord(stream.nextToken()).trim();
							invertedIndexTerm(token, l, i, allIndex);
							i++;
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
					l++;

				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other
			// errors
			// occur
			public FileVisitResult visitFileFailed(Path file, IOException e) {

				return FileVisitResult.CONTINUE;
			}
		});

		return null;
	}

	/**
	 * passing token to Index file for PI Index
	 * 
	 * @param token1
	 * @param docid
	 * @param i
	 * @param index
	 */
	private void invertedIndexTerm(String token1, Integer docid, Integer i, Indexing index) {

		index.addTermInvertedIndex(index.processWord(token1), docid, i);

	}

	/**
	 * creating vectors for all the documents mapped as (filename,vectorlist)
	 */
	public void createAllVectors() {

		HashMap<String, List<TokenDetails>> mainIndex = allIndex.getmIndex();
		String[] dict = allIndex.getInvertedIndexDictionary();
		int vectorDim = 0;
		for (String term : dict) {
			TokenDetails tokdet;
			List<Double> value = null;
			// iterate over the full vocab and set the vectorlists
			// vectorDim is the position of the term in the vectorlist
			for (int i = 0; i < mainIndex.get(term).size(); i++) {
				tokdet = mainIndex.get(term).get(i);
				// get the filename corresponding the document id
				String filename = fileNameLists.get(tokdet.getDocId()).toString();
				if (vectorList.containsKey(filename)) {
					value = vectorList.get(filename);
				} else {
					value = new ArrayList<Double>(Collections.nCopies(8899, 0.0));
					vectorList.put(filename, value);
				}

				value.set(vectorDim, 1 + Math.log(tokdet.getPosition().size()));
			}

			vectorDim++;
		}
	}

	/**
	 * method to create length normalized vectorlist of Hamilton, Jay and
	 * Madison classes using the vectorlist already created
	 * 
	 * @param path
	 * @param authorName
	 * @return
	 * @throws IOException
	 */
	public HashMap<String, List<Double>> createVector(Path path, String authorName) throws IOException {

		HashMap<String, List<Double>> hash = new HashMap<>();
		Set<String> filelist = new HashSet<>();
		List<String> vocab = new ArrayList<>();
		Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
			public FileVisitResult preVisitDirectory(Path dir, BasicFileAttributes attrs) {
				// make sure we only process the current working
				if (path != null) {
					return FileVisitResult.CONTINUE;
				}
				return FileVisitResult.SKIP_SUBTREE;
			}

			public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws FileNotFoundException {
				String str;

				List<Double> arr;
				// only process .txt files
				if (file.toString().endsWith(".txt")) {
					try {
						// for the remaining 11 conflicted files
						str = file.getFileName().toString();

						TokenStream stream = new SimpleTokenStream(file.toFile());
						// to create vocab of the three classes and the filelist
						// of each class
						while (stream.hasNextToken()) {
							String st = allIndex.processWord(stream.nextToken());
							if (filelist == null)
								break;
							else {
								filelist.add(str);
								vocab.add(st);
							}
						}
						arr = vectorList.get(str);
						// sum all the square values

						// calculate Ld
						finalsum = arr.stream().mapToDouble(val -> Math.pow(val, 2)).sum();

						finalsum = Math.sqrt(finalsum);
						arr = arr.stream().map(i -> i = i / finalsum).collect(Collectors.toList());
						// each vector contains filename mapped to the frequency
						// of a term in the document
						hash.put(str, arr);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				return FileVisitResult.CONTINUE;
			}

			// don't throw exceptions if files are locked/other
			// errors
			// occur
			public FileVisitResult visitFileFailed(Path file, IOException e) {

				return FileVisitResult.CONTINUE;
			}
		});

		if (authorName.equalsIgnoreCase("MADISON")) {
			madisonFileList = filelist;
			madisonVocab = vocab;
		} else if (authorName.equalsIgnoreCase("HAMILTON")) {
			hamiltonFileList = filelist;
			hamiltonVocab = vocab;
		} else if (authorName.equalsIgnoreCase("JAY")) {
			jayFileList = filelist;
			jayVocab = vocab;
		}
		return hash;

	}

	/**
	 * to iterate over the vectorlist of class to add the vectors
	 * 
	 * @param vec
	 * @return
	 */
	public List<Double> addVectors(HashMap<String, List<Double>> vec) {
		List<Double> res = new ArrayList<Double>(Collections.nCopies(8899, 0.0));
		Iterator<?> it = vec.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String,List> pair = (Map.Entry) it.next();
			String key = pair.getKey();
			compute(res, vec.get(key));
		}
		return res;
	}

	/**
	 * to add all the document vectors
	 * 
	 * @param vector1
	 * @param vector2
	 */
	public static void compute(List<Double> vector1, List<Double> vector2) {
		for (int i = 0; i < Math.max(vector1.size(), vector2.size()); i++) {
			vector1.set(i, vector1.get(i) + vector2.get(i));
		}
		// return x;
	}

	/**
	 * To find the Euclidean distance of the conflicted documents from each of
	 * the three centroids
	 * 
	 * @param centroid1
	 * @param centroid2
	 * @param centroid3
	 * @param vector3
	 */
	public void calculateDistance(List<Double> centroid1, List<Double> centroid2, List<Double> centroid3,
			HashMap<String, List<Double>> vector3) {
		HashMap<String, String> classify = new HashMap<>();
		Iterator<?> it = vector3.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry pair = (Map.Entry) it.next();
			String key = (String) pair.getKey();
			double euc1 = findEuclideanDistance(centroid1, ((List<Double>) pair.getValue()));
			double euc2 = findEuclideanDistance(centroid2, ((List<Double>) pair.getValue()));
			double euc3 = findEuclideanDistance(centroid3, ((List<Double>) pair.getValue()));
			// Vector4.forEach((k,v)->System.out.println(k+" "+v));
			// centroid3.stream().forEach(System.out::println);

			if (euc1 < euc2 && euc1 < euc3) {
				classify.put(pair.getKey().toString(), "HAMILTON");
				System.out.println(pair.getKey().toString() + " is written by HAMILTON");
			} else if (euc2 < euc1 && euc2 < euc3) {
				classify.put(pair.getKey().toString(), "MADISON");
				System.out.println(pair.getKey().toString() + " is written by MADISON");
			} else {
				classify.put(pair.getKey().toString(), "JAY");
				System.out.println(pair.getKey().toString() + " is written by JAY");
			}
		}
	}

	/**
	 * Euclidean calculation method
	 * 
	 * @param vector1
	 * @param vector2
	 * @return
	 */
	public static double findEuclideanDistance(List<Double> vector1, List<Double> vector2) {
		double sum = 0;

		for (int i = 0; i < Math.max(vector1.size(), vector2.size()); i++) {
			sum += Math.pow(Math.abs(vector2.get(i) - vector1.get(i)), 2);
		}
		return Math.sqrt(sum);
	}

}
