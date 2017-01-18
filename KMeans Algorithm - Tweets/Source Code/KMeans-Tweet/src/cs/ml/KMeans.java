
package cs.ml;
/* Class : KMeans
 * Description : This class provides the implementation of Kmeans 
 * 				 Algorithm for a given set of tweets
 * @author Sneha.Bangar
 */
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonReader;

public class KMeans {

	private HashMap<String, Tweet> tweetMap = new HashMap<String, Tweet>();
	private List<Tweet> centroidLst = new ArrayList<Tweet>();
	private static HashMap<String, Cluster> clusterMap;
	private int kCluster = 25;
	private List<Tweet> tweetLst = new ArrayList<Tweet>();
	private double sSE = 0.0;
	private DecimalFormat df = new DecimalFormat("#.###");

	public static void main(String[] args) throws IOException {
		KMeans twkm = new KMeans();
		String dataFile = "";
		String outputFile = "";
		String seedFile = "";
		
		//get commandline input
		if (args.length > 0 && args.length == 4) {

			twkm.kCluster = Integer.parseInt(args[0]);
			if (twkm.kCluster > 25) {
				System.out.println("Input Format : <K>   <Seed File>   <Data File> <Output File>");
				System.out.println("Value for k should be between 1-25");
				System.exit(0);
			}
			seedFile = args[1];
			dataFile = args[2];
			outputFile = args[3];
			System.out.println(" K :" + twkm.kCluster + " Seed File:" + seedFile + " Data File:" + dataFile
					+ " Output File:  " + outputFile);

			// create K clusters
			clusterMap = new HashMap<String, Cluster>();

			// extract tweets data from json file
			twkm.extractJsonTweets(dataFile);

			// get initial centroid values
			twkm.extractInitialSeeds(seedFile);

			// generate k clusters
			twkm.generateClusters(twkm.kCluster);

			// call kmeans
			twkm.kMeans();

			// calculate sum of squared Error
			twkm.computeSSE();

			// output cluster details
			twkm.outputClusterDetails(outputFile);

		} else {
			System.out.println("Please specify the arguments in below order");
			System.out.println("<K>    <Data File>   <Seed File>  <Output File>");
		}

	}

	/**
	 * processes given set of tweets and assigns them into clusters
	 *   
	 */
	public void kMeans() {
		System.out.println("Starting K-Means..");
		boolean isSameCentroid = false;
		
		//until the centroid is changing - not same as previous
		while (!isSameCentroid) {
			Iterator<Entry<String,Tweet>> it = tweetMap.entrySet().iterator();
			
			while (it.hasNext()) {

				Entry<String,Tweet> entry = (Entry<String,Tweet>) it.next();

				// retrieve tweet details
				Tweet t = (Tweet) entry.getValue();
				String tweetText = t.getTweetText();
				String tweetId = t.getId();
				String currentClusterId = t.getClusterId();

				// get id for closest centroid
				String cId = getClosestCentroid(tweetText);

				if (currentClusterId != cId) {
					
					// assign point tweet to cluster for new centroid
					if (clusterMap.containsKey(cId))
						clusterMap.get(cId).getDataLst().add(tweetId);
					
					// set new cluster id for tweet
					tweetMap.get(tweetId).setClusterId(cId);
					
					// remove the point from its current cluster
					if (currentClusterId != "") {
						if (clusterMap.containsKey(currentClusterId))
							clusterMap.get(currentClusterId).getDataLst().remove(tweetId);
					}
				}
			}

			// recalculate centroid for all the clusters
			isSameCentroid = recalculateCentroid();
			
			if (isSameCentroid) {
				// if centroid not changing ,stop k-means iterations
				break;
			}
		}
		System.out.println("K-Means successfully Executed !");
	}

	/**
	 * Outputs the computed clusters to a file
	 *
	 * @param fileName
	 *            output file to save cluster details
	 * @throws IOException
	 */
	public void outputClusterDetails(String fileName) throws IOException {
		File f = new File(fileName);
		if (!f.isFile()) {
			try {
				f.createNewFile();
			} catch (Exception e) {
				System.out.println("Error : " + e.getMessage());
			}
		}
		//file writer
		FileWriter fileWriter = new FileWriter(fileName, true);
		List<String> clusterData = new ArrayList<String>();
		String newLine = System.getProperty("line.separator");
		String result = "";
		fileWriter.write(newLine);
		fileWriter.write("Cluster -" + " Data Points");
		fileWriter.write(newLine);
		fileWriter.write("----------------------------------------------------------------------------");
		fileWriter.write(newLine);
		
		//get data for each centroid and write into file
		for (int i = 0; i < centroidLst.size(); i++) {
			clusterData = clusterMap.get(centroidLst.get(i).getId()).getDataLst();
			if (null != clusterData && !clusterData.isEmpty()) {
				// result = "{ " + centroidLst.get(i).getId() + " } - " +
				// clusterData.toString();
				result = "{ " + (i + 1) + " }   - " + clusterData.toString();
				fileWriter.write(result);
				fileWriter.write(newLine);
				fileWriter.write(newLine);
			}
		}
		
		//write SSE into file
		result = "Sum of Squared Error :  " + Double.toString(sSE);
		fileWriter.write(result);
		fileWriter.write(newLine);
		fileWriter.close();
	}

	/**
	 * Recalculate the centroid for all the points 
	 * @return flag
	 * 		returns true if the newly calculated centroids are same as old centroids
	 */
	public boolean recalculateCentroid() {
		List<String> clusterData = new ArrayList<String>();
		List<Tweet> newCentLst = new ArrayList<Tweet>();
		boolean flag = true;

		for (int ct = 0; ct < centroidLst.size(); ct++) {
			// get all data points from a cluster
			clusterData = clusterMap.get(centroidLst.get(ct).getId()).getDataLst(); // get data points for current cluster
																					
			if (null != clusterData && !clusterData.isEmpty()) {
				int centLstSize = clusterData.size();
				double minDist = Double.MAX_VALUE;
				String newCentId = "";
				for (int i = 0; i < centLstSize; i++) {
					String txt = tweetMap.get(clusterData.get(i)).getTweetText();
					double sum = 0.0;
					for (int j = 0; j < centLstSize; j++) {
						String ptTxt = tweetMap.get(clusterData.get(j)).getTweetText();
						double dist = calculateJaccardDist(txt, ptTxt);
						sum = sum + dist;
					}
					if (sum < minDist) {
						minDist = sum;
						newCentId = tweetMap.get(clusterData.get(i)).getId();
					}
				}

				// remove current cluster from cluster map
				clusterMap.remove(centroidLst.get(ct).getId());

				// set new clusterId in all the twets in the cluster
				for (int i = 0; i < centLstSize; i++) {
					tweetMap.get(clusterData.get(i)).setClusterId(newCentId);
				}

				Cluster c = new Cluster();
				c.setId(newCentId);
				c.setCentText(tweetMap.get(newCentId).getTweetText());
				c.setDataLst(clusterData);
				
				// add a cluster for new centroid in the cluster map
				clusterMap.put(newCentId, c);

				// add new centroid to centroidLst
				Tweet t = new Tweet();
				t.setClusterId(newCentId);
				t.setId(newCentId);
				t.setTweetText(tweetMap.get(newCentId).getTweetText());
				newCentLst.add(t);

			}
		}
		// modify current centroid list with new centroids
		for (int d = 0; d < newCentLst.size(); d++) {
			if (!centroidLst.contains(newCentLst.get(d))) {
				flag = false;
			}
		}
		centroidLst = newCentLst;
		return flag;
	}
	
	/**
	 * Computes SSE - Error Sum of Square
	*/
	public void computeSSE() {
		List<String> clusterData = new ArrayList<String>();
		double dist = 0.0;
		double tempSSE = 0.0;
		// for each cluster
		for (int i = 0; i < centroidLst.size(); i++) {
			
			tempSSE = 0.0;
			clusterData = clusterMap.get(centroidLst.get(i).getId()).getDataLst();
			int lstSize = clusterData.size();
			
			//get text of the centroid
			String centText = tweetMap.get(centroidLst.get(i).getId()).getTweetText();
			
			
			for (int j = 0; j < lstSize; j++) {
				Tweet t = tweetMap.get(clusterData.get(j));
				String tweetText = t.getTweetText();
				
				//calculate jaccard distance between tweet and centroid
				dist = Double.parseDouble(df.format(calculateJaccardDist(tweetText, centText)));
				tempSSE = Double.parseDouble(df.format(tempSSE + (dist * dist)));
			}

			//add distance of all the tweets from centroid of a cluster 
			sSE = Double.parseDouble(df.format(sSE + tempSSE));
		}

	}

	
	/**
	 * Given a point, this function finds out the closest centroid for it
	 * @param tweetText
	 *            text of a tweet
	 * @return centId
	 * 		returns id of the closest centroid
	 */
	public String getClosestCentroid(String tweetText) {
		double temp = 0;
		double dist = Double.MAX_VALUE;
		String centId = "";
		for (int j = 0; j < centroidLst.size(); j++) {
			temp = Double.parseDouble(df
					.format(calculateJaccardDist(tweetText, tweetMap.get(centroidLst.get(j).getId()).getTweetText())));
			if (temp < dist) {
				dist = temp;
				centId = centroidLst.get(j).getId();
			}
		}
		return centId;
	}

	/**
	 * Given two tweets, this function finds out the distance between them using jaccard similarity.
	 * @param tweet1
	 *            text of first tweet
	 * @param tweet2
	 *            text of second tweet
	 * @return dist
	 * 		returns jaccard distance
	 */
	public double calculateJaccardDist(String tweet1, String tweet2) {
		// eliminate all the delimiters from tweets
		tweet1 = eliminateDelimiters(tweet1);
		tweet2 = eliminateDelimiters(tweet2);

		List<String> tweet1Lst = Arrays.asList(tweet1.split(" "));
		Set<String> tweet1Set = new HashSet<String>(tweet1Lst);
		List<String> tweet2Lst = Arrays.asList(tweet2.split(" "));
		Set<String> tweet2Set = new HashSet<String>(tweet2Lst);

		Set<String> union = new HashSet<String>(tweet1Set);
		union.addAll(tweet2Set);

		Set<String> intersection = new HashSet<String>();
		for (String s : tweet1Set) {
			if (tweet2Set.contains(s)) {
				intersection.add(s);
			}
		}

		// calculate total no of words in both strings
		double unionSize = union.size();
		double intSize = intersection.size();
		double jaccardDist = 0.0;
		if (unionSize != intSize)
			jaccardDist = (unionSize - intSize) / unionSize;

		return jaccardDist;
	}
	/**
	 * Given a tweets, this function removes special characters and multiple spaces from it
	 * @param tweet
	 *            text of tweet
	 * @return text
	 * 		returns cleaned text
	 */
	
	public String eliminateDelimiters(String text) {
		// Remove all the special characters
		text = text.replaceAll("[+^:,-?;=%#&~`\'\"|$!@*_)/(}{\\.]", "");
		
		// Remove multiple white spaces
		text = text.replaceAll("\\s+", " ");
		
		return text;
	}

	/**
	 * This function retrieves initial seed (tweet ids) from input seed file
	 * @param fileName
	 * 			name of seed file
	 */
	public void extractInitialSeeds(String fileName) {
		BufferedReader br;
		try {
			List<Tweet> tLst = new ArrayList<Tweet>();
			int count = 0;
			File f = new File(fileName);
			Tweet t = null;
			br = new BufferedReader(new FileReader(f));
			String line;
			while (((line = br.readLine()) != null) && (count < kCluster)) {
				t = new Tweet();
				t.setClusterId("");
				t.setId(line.replace(",", ""));
				t.setTweetText(tweetMap.get(t.getId()).getTweetText());
				centroidLst.add(t);
				count++;
			}
			// br.close();

			if (kCluster < 25) {
				int i = 0;
				while (i != kCluster) {
					Tweet newTweet = new Tweet();
					int randomCent = 0;
					do {
						Random r = new Random();
						randomCent = r.nextInt(kCluster);
						t = centroidLst.get(randomCent);
					} while (tLst.contains(t));

					newTweet.setId(t.getId());
					newTweet.setTweetText(t.getTweetText());
					newTweet.setClusterId("");
					tLst.add(newTweet);
					i++;
				}
				centroidLst = tLst;
			}

		} catch (Exception e) {
			System.out.println("error:" + e.getMessage());
		}
		
	}

	/**
	 * This function initializes clusterMap with given number of clusters 
	 * @param kCluster
	 *            Number of clusters
	 * */
	public void generateClusters(int kCluster) {
		for (int i = 0; i < kCluster; i++) {
			Cluster c = new Cluster();
			c.setId(centroidLst.get(i).getId());
			List<String> lst = new ArrayList<String>();
			c.setDataLst(lst);
			clusterMap.put(centroidLst.get(i).getId(), c);
		}
	}

	/**
	 * This function extracts tweets from input data file
	 * @param fileName
	 * 			name of seed file
	 */
	/* Function to extract tweet text and Ids */
	public void extractJsonTweets(String fileName) {
		File f = new File(fileName);
		String id = "";
		String tweetText = "";
		try {
			BufferedReader br = new BufferedReader(new FileReader(f));
			String line;
			while ((line = br.readLine()) != null) {

				InputStream is = new ByteArrayInputStream(line.getBytes());
				JsonReader rdr = Json.createReader(is);
				JsonObject jo = rdr.readObject();
				tweetText = jo.getString("text");
				tweetText = tweetText.replaceAll("\\n", " ");
				id = jo.getString("id_str");

				Tweet t = new Tweet();
				t.setId(id);
				t.setTweetText(tweetText);
				t.setClusterId("");
				tweetMap.put(id, t);
				tweetLst.add(t);
			}
			br.close();
		} catch (Exception e) {
			System.out.println("error:" + e.getMessage());
		}
	}

}
