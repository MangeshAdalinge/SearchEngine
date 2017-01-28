package set.beans;

public class RankComparator implements Comparable<RankComparator>{

	private int docId;
	private Double score;
	
	
	public RankComparator(int docId2, double accumuatorAd) {
		this.docId=docId2;
		this.score=accumuatorAd;
	}


	public int getDocId() {
		return docId;
	}


	public void setDocId(int docId) {
		this.docId = docId;
	}

	public Double getScore() {
		return score;
	}


	public void setScore(Double score) {
		this.score = score;
	}


	@Override
	public int compareTo(RankComparator o) {
		// TODO Auto-generated method stub
		return o.getScore().compareTo(this.getScore());
	}

}
