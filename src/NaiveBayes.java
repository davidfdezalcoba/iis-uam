import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class NaiveBayes {

	private static HashMap<String, Double> spamSet;
	private static HashMap<String, Double> nonSpamSet;
	private static double numSpam;
	private static double numNonSpam;
	private static double numEmails;
	private static double numSpamWords;
	private static double numNonSpamWords;
	private static double numWords;

	public static void main(String[] args) {

		spamSet = new HashMap<String, Double>();
		nonSpamSet = new HashMap<String, Double>();

		try {

			train();
			test();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Something happened while reading files.");
			e.printStackTrace();
		}
	}

	private static void train() throws IOException {

		File f = new File("TrainingSet.txt");
		FileReader fs = new FileReader(f);
		BufferedReader bs = new BufferedReader(fs);

		while (bs.ready()) {
			String ex = bs.readLine();
			ex = ex.trim();
			ex = ex.toLowerCase();
			String[] tokens = ex.split("\\ ");

			if (tokens[0].equals("spam")) {
				for (int i = 1; i < tokens.length; i++) {
					String s = tokens[i];
					if (spamSet.containsKey(s))
						spamSet.put(s, spamSet.get(s) + 1);
					else {
						spamSet.put(s, 1.0);
						if (!nonSpamSet.containsKey(s))
							numWords++;
					}
					numSpamWords++;
				}
				numSpam++;
			} else {
				for (int i = 1; i < tokens.length; i++) {
					String s = tokens[i];
					if (nonSpamSet.containsKey(s))
						nonSpamSet.put(s, nonSpamSet.get(s) + 1);
					else {
						nonSpamSet.put(s, 1.0);
						if (!spamSet.containsKey(s))
							numWords++;
					}
					numNonSpamWords++;
				}
				numNonSpam++;
			}
			numEmails++;
		}

		bs.close();
		fs.close();
	}

	private static void test() throws IOException {

		File f = new File("TestSet.txt");
		FileReader fs = new FileReader(f);
		BufferedReader bs = new BufferedReader(fs);

		while (bs.ready()) {
			String ex = bs.readLine();
			String aux;
			aux = ex.trim();
			aux = aux.toLowerCase();
			String[] tokens = aux.split("\\ ");

			double ps = numSpam / numEmails;
			double pn = numNonSpam / numEmails;

			for (String s : tokens) {
				ps *= probSpam(s);
				pn *= probNonSpam(s);
			}

			if (ps > pn)
				System.out.println(ex + " -> SPAM. " + ps);
			else
				System.out.println(ex + " -> NON-SPAM. ");

		}

		bs.close();
		fs.close();
	}

	private static double probSpam(String s) {
		if (spamSet.containsKey(s))
			return (spamSet.get(s) + 1) / (numSpamWords + numWords);
		else
			return 1.0 / (numSpamWords + numWords);
	}

	private static double probNonSpam(String s) {
		if (nonSpamSet.containsKey(s))
			return (double) (nonSpamSet.get(s) + 1) / (numNonSpamWords + numWords);
		else
			return 1.0 / (numNonSpamWords + numWords);
	}

}
