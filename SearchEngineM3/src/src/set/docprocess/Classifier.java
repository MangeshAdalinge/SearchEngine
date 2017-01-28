package src.set.docprocess;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 * 
 *         Main class Calls and executes bot Rocchio and Bayesian classification
 * 
 */
public class Classifier {

	public static void main(String[] args) throws IOException {
		Rocchio rocchio = new Rocchio();
		HashMap<String, List<Double>> vector1 = new HashMap<>();
		HashMap<String, List<Double>> vector2 = new HashMap<>();
		HashMap<String, List<Double>> vector3 = new HashMap<>();
		HashMap<String, List<Double>> vector4 = new HashMap<>();
		List<Double> centroid1 = new ArrayList<>();
		List<Double> centroid2 = new ArrayList<>();
		List<Double> centroid3 = new ArrayList<>();

		Path path = Paths.get("C:/Users/mange/OneDrive/Documents/Federalist_ByAuthors/ALL/");
		rocchio.visitFiles(path);

		path = Paths.get("C:/Users/mange/OneDrive/Documents/Federalist_ByAuthors/HAMILTONORMADISON/");
		rocchio.visitFiles(path);
		rocchio.createAllVectors();
		vector3 = rocchio.createVector(path, path.getFileName().toString());

		path = Paths.get("C:/Users/mange/OneDrive/Documents/Federalist_ByAuthors/HAMILTON/");
		vector1 = rocchio.createVector(path, path.getFileName().toString());

		path = Paths.get("C:/Users/mange/OneDrive/Documents/Federalist_ByAuthors/MADISON");
		vector2 = rocchio.createVector(path, path.getFileName().toString());

		// create vector for Jay
		path = Paths.get("C:/Users/mange/OneDrive/Documents/Federalist_ByAuthors/JAY");
		vector4 = rocchio.createVector(path, path.getFileName().toString());

		System.out.println(
				"----------------------------------------------------------------------------------------------");
		System.out.println("                                     Rocchio Classification");
		System.out.println(
				"----------------------------------------------------------------------------------------------");

		final int vector1Size = vector1.size();
		// centroid for Hamilton
		centroid1 = rocchio.addVectors(vector1);
		centroid1 = centroid1.stream().filter(i -> i != null).map(i -> i / vector1Size).collect(Collectors.toList());

		// centroid for Madison
		final int vector2Size = vector2.size();
		centroid2 = rocchio.addVectors(vector2);
		centroid2 = centroid2.stream().filter(i -> i != null).map(i -> i / vector2Size).collect(Collectors.toList());

		// centroid for Jay
		final int vector4Size = vector4.size();
		centroid3 = rocchio.addVectors(vector4);
		centroid3 = centroid3.stream().filter(i -> i != null).map(i -> i / vector4Size).collect(Collectors.toList());

		rocchio.calculateDistance(centroid1, centroid2, centroid3, vector3);

		System.out.println();
		System.out.println(
				"----------------------------------------------------------------------------------------------");
		System.out.println("                                 Bayesian Classification");
		System.out.println(
				"----------------------------------------------------------------------------------------------");
		System.out.println();
		NaiveBayesian nb = new NaiveBayesian(Rocchio.getAllIndex(), rocchio);
		nb.train();
		nb.test();

	}

}
