package src.set.beans;

/**
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 * 
 */
public class TermInfo implements Comparable<TermInfo> {
	private String term;
	private double score;

	public TermInfo(String t, double s) {
		this.term = t;
		this.score = s;

	}

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	public int compareTo(TermInfo o) {
		return Double.compare(this.score, o.score);
	}

}
