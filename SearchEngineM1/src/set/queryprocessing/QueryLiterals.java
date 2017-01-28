package set.queryprocessing;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import set.beans.TokenDetails;
import set.docprocess.Indexing;

/**
 * Class QueryLiterals
 * 
 * @author Durvijay Sharma
 * @author Mangesh Adalinge
 * @author Surabhi Dixit
 */

public class QueryLiterals {

	public int processCnt = 0;
	public Boolean phraseQueryFlag = false;
	public String oldProcessString = null;
	public String[] split;
	public String oprand1 = null;
	public String oprand2 = null;
	public int i = 0;
	public Boolean processCntFlag = false;
	private List<String> oprands = new ArrayList<>();
	private List<String> operators = new ArrayList<>();
	private static final Pattern ltrs = Pattern.compile("[a-z\"]");
	private Indexing index;
	private QueryResultProcessing qrp = new QueryResultProcessing();
	private String completeQuery = "";

	/**
	 * Splits the query string
	 * 
	 * @param queryString:
	 *            Query string to process for search results
	 * @param indexObj:
	 *            Contains both objects of PI and BI
	 * @return
	 */
	public List<TokenDetails> splitQueryString(String queryString, Indexing indexObj) {
		split = queryString.split("\\s+");
		index = indexObj;
		splitQueryStringLiterals();
		if (qrp.getResults() != null) {
			System.out.println(("(" + completeQuery.replace(" ", "") + ")").toLowerCase() + "--" + qrp.getResults());
			return qrp.getResults().get(("(" + completeQuery.replace(" ", "") + ")").toLowerCase());
		} else
			return null;

	}

	/**
	 * Get operands and operators from query string
	 */
	public void splitQueryStringLiterals() {
		Boolean flag = false;
		Boolean notflag = false;
		Boolean notCheckFlag = false;
		Boolean orFlag = false;
		Boolean andFlag = false;
		for (int j = 0; j < split.length; j++) {
			if (split[j].equalsIgnoreCase("-") && j == 0) {
				if (split.length == j + 2) {

					System.out.println("search query shoud not start with NOT");
					notflag = true;
					break;
				} else {
					if (split.length > j + 2)
						if (split[j + 2].equals("+")) {
							// operators.add("Phrase");
							oprands.add(split[j + 1]);
							operators.add("NotOr");
							String s = "";
							if (split.length > j + 3)
								if (split[j + 3].startsWith("\"")) {
									for (int a = j + 3; a < split.length; a++) {
										s = s + split[a] + " ";
										if (split[a].endsWith("\"")) {
											oprands.add(s.trim());
											operators.add("Phrase");
											j = a;
											break;
										}
									}
								} else {
									oprands.add(split[j + 1].trim());
									j++;
								}
							orFlag = true;
							j++;
						} else if (split.length > j + 1) {
							if (split[j + 1].startsWith("\"")) {

								String s = "";
								for (int a = j + 1; a < split.length; a++) {
									s = s + split[a] + " ";
									if (split[a].endsWith("\"")) {
										oprands.add(s.trim());
										operators.add("Phrase");
										j = a;
										break;
									}
								}
								if (split[j + 1].equalsIgnoreCase("+")) {

									operators.add("NotOr");
									operators.add("Phrase");
								} else {

									operators.add("NotAnd");
									operators.add("Phrase");
								}

							} else {
								operators.add("NotAnd");
								andFlag = true;
							}
							notCheckFlag = true;
						}
				}
			} else if (j > 0 && split[j].equalsIgnoreCase("-")) {
				operators.add("AndNot");
				flag = true;
			} else if (split[j].equalsIgnoreCase("+")) {
				if (split[j + 1].equals("-")) {
					operators.add("OrNot");
					j++;
					if (j < split.length)
						if (split[j + 1].equalsIgnoreCase("\""))
							notCheckFlag = true;

				} else {
					if (!notCheckFlag) {
						operators.add("OR");
						orFlag = true;
					} else {
						if (!(operators.get(operators.size() - 1).equalsIgnoreCase("NotOr"))) {
							if (!(operators.get(operators.size() - 1).equalsIgnoreCase("PHRASE"))) {
								operators.add("OR");
								// j++;
								flag = true;
							}
						}
					}

				}

			} else if (split[j].startsWith("\"")) {
				if ((j - 1) != -1) {
					if (ltrs.matcher(split[j - 1].toString()).find()) {
						if (!split[j - 1].equalsIgnoreCase("-")) {
							if (!split[j - 1].endsWith("\"")) {
								if (operators.size() != 0)
									if (!(operators.get(operators.size() - 1).equalsIgnoreCase("NotAnd"))) {
										operators.add("AND");
										flag = true;
									}
							}
						}
					}
				}
				if (!notCheckFlag)
					operators.add("Phrase");
				String oprandString = null;
				oprandString = split[j].substring(1);
				if (oprandString.endsWith("\"")) {
					oprandString = oprandString.substring(0, oprandString.length() - 1);
				} else {

					for (int k = j; k < split.length; k++) {
						if (split[k].endsWith("\"")) {

							for (int l = j + 1; l < k; l++) {
								oprandString = oprandString + " " + split[l];
							}
							oprandString = oprandString + " " + split[k].substring(0, split[k].length() - 1);
							j = k;
							if (j != split.length - 1)
								if (!split[j + 1].equalsIgnoreCase("+") && !split[j + 1].equalsIgnoreCase("-")) {

									operators.add("AND");
									flag = true;

								}
							break;
						}
					}
				}
				oprandString = "\"" + oprandString + "\"";
				oprands.add(oprandString);
			} else {
				if (j != 0)
					if (!flag && !notCheckFlag && !orFlag && !andFlag)
						operators.add("AND");
				oprands.add(split[j]);
				flag = false;
				notCheckFlag = false;
				orFlag = false;
				if (operators.indexOf("NotAnd") < oprands.size() - 1)
					andFlag = false;
			}
		}
		if (!notflag) {

			printLiterals();
			if (oprands.size() < 2) {
				String[] s = oprands.toString().split("\\s");
				System.out.println("No of spaces ..... " + s.length);
				if (s.length > 1) {
					solveQuery();
					// printLiterals();

				} else {
					search(oprands.get(0), "", " SingleOprandQuery ");
				}
			} else {
				solveQuery();
				// printLiterals();
			}
		}
	}

	/**
	 * Solves the query: will solve query in PHRASE, AND and OR order
	 */
	public void solveQuery() {
		List<Integer> phraseIndexArray = new ArrayList<>();
		List<Integer> andIndexArray = new ArrayList<>();
		List<Integer> orIndexArray = new ArrayList<>();
		List<Integer> notIndexArray = new ArrayList<>();

		for (int i = 0; i < operators.size(); i++) {
			if (operators.get(i).equalsIgnoreCase("NOT") || operators.get(i).equalsIgnoreCase("OrNOT")
					|| operators.get(i).equalsIgnoreCase("AndNOT") || operators.get(i).equalsIgnoreCase("NotOr")
					|| operators.get(i).equalsIgnoreCase("NotAnd")) {
				notIndexArray.add(i);

			}
			if (operators.get(i).equalsIgnoreCase("Phrase")) {
				phraseIndexArray.add(i);

			}
			if (operators.get(i).equalsIgnoreCase("AND")) {
				andIndexArray.add(i);

			}
			if (operators.get(i).equalsIgnoreCase("OR")) {
				orIndexArray.add(i);

			}

		}
		System.out.println("NOT: " + notIndexArray.size() + " Phrase: " + phraseIndexArray.size() + " AND: "
				+ andIndexArray.size() + " Or: " + orIndexArray.size());

		for (int i = 0; i < phraseIndexArray.size(); i++) {
			int indexPhrase = phraseIndexArray.get(i);
			oprand1 = oprands.get(indexPhrase);
			if (operators.get(indexPhrase).endsWith("NotPHRASE")) {
				oprand2 = oprands.get(indexPhrase + 1);
				oldProcessString = search(oprand1, oprand2, " " + operators.get(indexPhrase).toUpperCase() + " ");
				oprands.add(indexPhrase, "(" + oldProcessString + ")");
				oprands.remove(indexPhrase + 1);
				oprands.remove(indexPhrase + 1);
				operators.remove(indexPhrase);
			} else {
				oldProcessString = search(oprand1, "", " " + " PHRASE " + " ");
				oprands.add(indexPhrase, "(" + oldProcessString + ")");
				oprands.remove(indexPhrase + 1);
				operators.remove(indexPhrase);
			}

			for (int j = 0; j < phraseIndexArray.size(); j++) {
				int val = phraseIndexArray.get(j);
				if (indexPhrase < phraseIndexArray.get(j)) {
					phraseIndexArray.add(j, val - 1);
					phraseIndexArray.remove(j + 1);
				}
			}
			for (int j = 0; j < andIndexArray.size(); j++) {
				int val = andIndexArray.get(j);
				if (indexPhrase < andIndexArray.get(j)) {
					andIndexArray.add(j, val - 1);
					andIndexArray.remove(j + 1);
				}
			}
			for (int j = 0; j < orIndexArray.size(); j++) {
				int val = orIndexArray.get(j);
				if (indexPhrase < orIndexArray.get(j)) {
					orIndexArray.add(j, val - 1);
					orIndexArray.remove(j + 1);
				}
			}
			for (int j = 0; j < notIndexArray.size(); j++) {
				int val = notIndexArray.get(j);
				if (indexPhrase < notIndexArray.get(j)) {
					notIndexArray.add(j, val - 1);
					notIndexArray.remove(j + 1);
				}
			}

		}
		// AND
		for (int i = 0; i < andIndexArray.size(); i++) {
			int indexAnd = andIndexArray.get(i);
			oldProcessString = AndOperator(oprands.get(indexAnd), oprands.get(indexAnd + 1));
			oprands.add(indexAnd, "(" + oldProcessString + ")");
			oprands.remove(indexAnd + 1);
			oprands.remove(indexAnd + 1);
			operators.remove(indexAnd);
			for (int j = 0; j < andIndexArray.size(); j++) {
				int val = andIndexArray.get(j);
				if (indexAnd < andIndexArray.get(j)) {
					andIndexArray.add(j, val - 1);
					andIndexArray.remove(j + 1);
				}
			}
			for (int j = 0; j < orIndexArray.size(); j++) {
				int val = orIndexArray.get(j);
				if (indexAnd < orIndexArray.get(j)) {
					orIndexArray.add(j, val - 1);
					orIndexArray.remove(j + 1);
				}
			}
			for (int j = 0; j < notIndexArray.size(); j++) {
				int val = notIndexArray.get(j);
				if (indexAnd < notIndexArray.get(j)) {
					notIndexArray.add(j, val - 1);
					notIndexArray.remove(j + 1);
				}
			}

		}
		// OR
		for (int i = 0; i < orIndexArray.size(); i++) {
			int indexOr = orIndexArray.get(i);

			oldProcessString = OrOperator(oprands.get(indexOr), oprands.get(indexOr + 1));
			oprands.add(indexOr, "(" + oldProcessString + ")");
			oprands.remove(indexOr + 1);
			oprands.remove(indexOr + 1);
			operators.remove(indexOr);

			for (int j = 0; j < orIndexArray.size(); j++) {
				int val = orIndexArray.get(j);
				if (indexOr < orIndexArray.get(j)) {
					orIndexArray.add(j, val - 1);
					orIndexArray.remove(j + 1);
				}
			}
			for (int j = 0; j < notIndexArray.size(); j++) {
				int val = notIndexArray.get(j);
				if (indexOr < notIndexArray.get(j)) {
					notIndexArray.add(j, val - 1);
					notIndexArray.remove(j + 1);
				}
			}

		}
		// NOT Query
		for (int i = 0; i < notIndexArray.size(); i++) {
			int indexNot = notIndexArray.get(i);
			oldProcessString = NotOperator(oprands.get(indexNot), oprands.get(indexNot + 1), operators.get(indexNot));
			oprands.add(indexNot, "(" + oldProcessString + ")");
			oprands.remove(indexNot + 1);
			oprands.remove(indexNot + 1);
			operators.remove(indexNot);

			for (int j = 0; j < notIndexArray.size(); j++) {
				int val = notIndexArray.get(j);
				if (indexNot < notIndexArray.get(j)) {
					notIndexArray.add(j, val - 1);
					notIndexArray.remove(j + 1);
				}
			}
		}
	}

	/**
	 * Prints operands and operators list
	 */
	public void printLiterals() {
		System.out.println("");
		System.out.println("All OPRANDS");
		System.out.println("");
		for (int i = 0; i < oprands.size(); i++) {
			System.out.print(oprands.get(i) + " ");
		}
		System.out.println("");
		for (int i = 0; i < operators.size(); i++) {
			System.out.print(operators.get(i) + " ");
		}
		System.out.println("");

	}

	/*
	 * public void IsPhraseQuery() { if (split[i].startsWith("\"")) {
	 * phraseQueryFlag = true; } }
	 */

	/**
	 * Solves AND query
	 * 
	 * @param andOprand1:
	 *            first operand
	 * @param andOprand2:
	 *            second operand
	 * @return: returns processed query output
	 */
	public String AndOperator(String andOprand1, String andOprand2) {
		oprand1 = andOprand1;
		oprand2 = andOprand2;
		if (oprand2.startsWith("\"")) {
			// PhraseQueryWithAnd(oprand1, oprand2);
			oldProcessString = search(oprand1, oprand2, " AND PHRASE ");
		} else if (oprand1.startsWith("\"")) {
			oldProcessString = search(oprand1, oprand2, " PHRASE AND ");
		} else {
			// i = i + 1;
			oldProcessString = search(oprand1, oprand2, " AND ");
			processCnt++;
		}

		return oldProcessString;

	}

	/**
	 * Solves OR query
	 * 
	 * @param orOprand1:
	 *            first operand
	 * @param orOprand2:
	 *            second operand
	 * @return: returns processed query output
	 */
	public String OrOperator(String orOprand1, String orOprand2) {
		oprand1 = orOprand1;
		oprand2 = orOprand2;

		if (oprand2.startsWith("\"")) {
			// PhraseQueryWithOr(oprand1,oprand2);
			oldProcessString = search(oprand1, oprand2, " OR PHRASE ");
		} else if (oprand1.startsWith("\"")) {
			oldProcessString = search(oprand1, oprand2, " PHRASE OR ");
		} else {
			// i = i + 2;
			oldProcessString = search(oprand1, oprand2, " OR ");
			processCnt++;

		}

		return oldProcessString;
	}

	/**
	 * Solves NOT query
	 * 
	 * @param Oprand1:
	 *            first operand
	 * @param Oprand2:
	 *            second operand
	 * @param operator
	 * @return: returns processed query output
	 */
	public String NotOperator(String Oprand1, String Oprand2, String operator) {
		oprand1 = Oprand1;
		oprand2 = Oprand2;

		if (oprand2.startsWith("\"")) {
			// PhraseQueryWithOr(oprand1,oprand2);
			oldProcessString = search(oprand1, oprand2, " " + operator + "PHRASE ");
		} else if (oprand1.startsWith("\"")) {
			oldProcessString = search(oprand1, oprand2, " PHRASE" + operator + " ");
		} else {
			System.out.println("");
			oldProcessString = search(oprand1, oprand2, operator);
			processCnt++;

		}

		return oldProcessString;

	}

	/**
	 * search results according to query
	 * 
	 * @param oprand11
	 * @param oprand22
	 * @param operator
	 * @return: returns processed query output
	 */
	public String search(String oprand11, String oprand22, String operator) {
		String oprand1 = oprand11.toLowerCase();
		String oprand2 = oprand22.toLowerCase();
		// String operator = operator1.trim().toLowerCase();

		if (!operator.trim().equalsIgnoreCase("phrase")) {
			oprand1 = oprand1.replaceAll(" ", "");
			oprand2 = oprand2.replaceAll(" ", "");
		}
		String oldProcessString = null;
		if (operator.replace(" ", "").equalsIgnoreCase("PHRASE")) {
			completeQuery = oprand1.replaceAll("\"", "");
		} else {
			completeQuery = oprand1 + operator + oprand2;
		}

		System.out.println("Query: " + oprand11 + "--" + oprand22 + "--" + operator + "--");

		qrp.setBiWordIndex(index);
		System.out.println(oldProcessString);
		switch (operator.trim()) {
		case "PHRASE":
			oldProcessString = oprand1.replaceAll("\"", "");
			qrp.getPhraseQueryresult(oldProcessString);
			break;
		case "AND":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "") + oprand2.replaceAll(" ", "");
			qrp.getAndQueryresult(oprand1.replaceAll(" ", ""), oprand2.replaceAll(" ", ""));
			break;
		case "OR":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "") + oprand2.replaceAll(" ", "");
			qrp.getOrQueryresult(oprand1.replaceAll(" ", ""), oprand2.replaceAll(" ", ""));
			break;
		case "PHRASE AND":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "") + oprand2.replaceAll(" ", "");
			qrp.getAndQueryresult(oprand1, oprand2);
			System.out.println(oldProcessString);
			break;
		case "PHRASE OR":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "") + oprand2.replaceAll(" ", "");
			qrp.getOrQueryresult(oprand1, oprand2);
			System.out.println(oldProcessString);
			break;
		case "NOTPHRASE":
			oldProcessString = oprand1.replaceAll("\"", "");
			qrp.getPhraseQueryresult(oldProcessString);
			break;
		case "OrNot":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "") + oprand2.replaceAll(" ", "");
			qrp.getOrNotQueryresult(oprand1.replaceAll(" ", ""), oprand2.replaceAll(" ", ""),
					operator.replaceAll(" ", "").toLowerCase());
			break;
		case "AndNot":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "").toLowerCase()
					+ oprand2.replaceAll(" ", "");
			qrp.getAndNotQueryresult(oprand1.replaceAll(" ", ""), oprand2.replaceAll(" ", ""),
					operator.replaceAll(" ", ""));
			break;
		case "NotOr":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "") + oprand2.replaceAll(" ", "");
			completeQuery = oprand2.replaceAll(" ", "") + operator.replaceAll(" ", "").toLowerCase()
					+ oprand1.replaceAll(" ", "");
			qrp.getOrNotQueryresult(oprand2.replaceAll(" ", ""), oprand1.replaceAll(" ", ""),
					operator.replaceAll(" ", "").toLowerCase());
			break;
		case "NotAnd":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "") + oprand2.replaceAll(" ", "");
			completeQuery = oprand2.replaceAll(" ", "") + operator.replaceAll(" ", "").toLowerCase()
					+ oprand1.replaceAll(" ", "");
			qrp.getAndNotQueryresult(oprand2.replaceAll(" ", ""), oprand1.replaceAll(" ", ""),
					operator.replaceAll(" ", "").toLowerCase());
			break;
		case "SingleOprandQuery":
			oldProcessString = oprand1.replaceAll(" ", "") + operator.replaceAll(" ", "");
			qrp.getSingleOprandResult(oprand1.replaceAll(" ", ""), operator.replaceAll(" ", ""));

			break;
		}
		return oldProcessString;

	}

	/**
	 * Clears all operands and operators
	 */
	public void clear() {
		// TODO Auto-generated method stub
		processCnt = 0;
		oldProcessString = null;
		split = null;
		oprand1 = null;
		oprand2 = null;
		oprands.removeAll(oprands);
		operators.removeAll(operators);

		i = 0;

	}

}
