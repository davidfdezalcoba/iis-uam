import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class NaiveBayesLog {

	private static HashMap<String, Double> posSet;
	private static HashMap<String, Double> negSet;

	// private static HashMap<String, Double> spamProbs;
	// private static HashMap<String, Double> nonSpamProbs;

	private static double numPosEmails;
	private static double numNegEmails;
	private static double numEmails;
	private static double numTestEmails;
	private static double numPosWords;
	private static double numNegWords;
	private static double numWords;

	public static void main(String[] args) {

		numPosEmails = 0;
		numNegEmails = 0;
		numEmails = 0;
		numTestEmails = 0;
		numPosWords = 0;
		numNegWords = 0;
		numWords = 0;

		posSet = new HashMap<String, Double>();
		negSet = new HashMap<String, Double>();

		try {

			trainPos();
			trainNeg();
			double p = testPos();
			double n = testNeg();
			double res = (p + n) / numTestEmails * 100;
			System.out.println((p + n) + "/" + numTestEmails + " correct.");
			System.out.println("Accuracy: " + res + "%");

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Something happened while reading files...");
			e.printStackTrace();
		}
	}

	private static void trainPos() throws IOException {
		File dir = new File("sets/train/pos");
		for (File entry : dir.listFiles()) {
			numPosEmails++;
			numEmails++;
			char[] buff = new char[1024];
			FileReader fs = new FileReader(entry);
			BufferedReader bs = new BufferedReader(fs);
			int len = bs.read(buff);
			String s = String.copyValueOf(buff, 0, len);
			String[] tokens = s.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			for (int i = 1; i < tokens.length; i++) {
				String word = tokens[i];
				if (posSet.containsKey(word))
					posSet.put(word, posSet.get(word) + 1);
				else {
					posSet.put(word, 1.0);
					if (!negSet.containsKey(word))
						numWords++;
				}
				numPosWords++;
			}
			bs.close();
			fs.close();
		}
	}

	private static void trainNeg() throws IOException {
		File dir = new File("sets/train/neg");
		for (File entry : dir.listFiles()) {
			numNegEmails++;
			numEmails++;
			char[] buff = new char[1024];
			FileReader fs = new FileReader(entry);
			BufferedReader bs = new BufferedReader(fs);
			int len = bs.read(buff);
			String mess = String.copyValueOf(buff, 0, len);
			String[] tokens = mess.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");
			for (int i = 1; i < tokens.length; i++) {
				String word = tokens[i];
				if (negSet.containsKey(word))
					negSet.put(word, negSet.get(word) + 1);
				else {
					negSet.put(word, 1.0);
					if (!posSet.containsKey(word))
						numWords++;
				}
				numNegWords++;
			}
			bs.close();
			fs.close();
		}
	}

	private static double testPos() throws IOException {

		double cont = 0.0;

		double ps = Math.log(numPosEmails / numEmails);
		double pn = Math.log(numNegEmails / numEmails);

		File dir = new File("sets/test/pos");
		for (File entry : dir.listFiles()) {
			numTestEmails++;
			FileReader fs = new FileReader(entry);
			BufferedReader bs = new BufferedReader(fs);

			char[] buff = new char[1024];
			int len = bs.read(buff);
			String mess = String.copyValueOf(buff, 0, len);

			String[] tokens = mess.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

			for (String tok : tokens) {
				ps += Math.log(probPos(tok));
				pn += Math.log(probNeg(tok));
			}

			if (ps >= pn)
				cont++;

			bs.close();
			fs.close();
		}
		return cont;
	}

	private static double testNeg() throws IOException {

		double cont = 0.0;

		double ps = Math.log(numPosEmails / numEmails);
		double pn = Math.log(numNegEmails / numEmails);

		File dir = new File("sets/test/neg");
		for (File entry : dir.listFiles()) {
			numTestEmails++;
			FileReader fs = new FileReader(entry);
			BufferedReader bs = new BufferedReader(fs);

			char[] buff = new char[1024];
			int len = bs.read(buff);
			String mess = String.copyValueOf(buff, 0, len);

			String[] tokens = mess.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

			for (String tok : tokens) {
				ps += Math.log(probPos(tok));
				pn += Math.log(probNeg(tok));
			}

			if (ps < pn)
				cont++;

			bs.close();
			fs.close();
		}
		return cont;
	}

	private static double probPos(String s) {
		if (posSet.containsKey(s))
			return (posSet.get(s) + 1) / (numPosWords + numWords);
		else
			return 1.0 / (numPosWords + numWords);
	}

	private static double probNeg(String s) {
		if (negSet.containsKey(s))
			return (double) (negSet.get(s) + 1) / (numNegWords + numWords);
		else
			return 1.0 / (numNegWords + numWords);
	}

}