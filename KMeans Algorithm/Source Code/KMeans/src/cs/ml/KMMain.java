package cs.ml;

/* Class : KMMain
 * Description : This class provides the implementation of Kmeans 
 * 				 Algorithm for a given set of points
 * @author Sneha.Bangar
 */

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import au.com.bytecode.opencsv.CSVReader;

public class KMMain {

	private List<Point> pointDataLst = new ArrayList<Point>();
	private static int dataSize = 0;
	private static List<Point> centroidLst = new ArrayList<Point>();
	private static HashMap<String, Cluster> clusterMap;
	private double sSE = 0.0;

	public static void main(String[] args) throws IOException {
		KMMain kmMain = new KMMain();
		String inputFile = "";
		String outputFile = "";
		int kCluster = 0;
		
		//get commandline input
		if (args.length > 0 && args.length == 3) {
			kCluster = Integer.parseInt(args[0]);
			inputFile = args[1];
			outputFile = args[2];
			System.out.println(" K :" + kCluster + " Input File:" + inputFile + " Output File:  " + outputFile);
			
			// get data
			kmMain.pointDataLst = kmMain.extractDataFrmCSV(inputFile);
			
			// create K clusters
			clusterMap = new HashMap<String, Cluster>();
			
			// Generate initial clusters
			kmMain.generateClusters(kCluster);
			
			// call K-Means
			kmMain.kMeans(kmMain.pointDataLst, kCluster);
			
			// calculate sum of squared Error
			kmMain.computeSSE();
			
			// Output cluster details
			kmMain.outputClusterDetails(outputFile);

		} else {
			System.out.println("Please specify the arguments in below order");
			System.out.println("<K>    <Input File>    <Output File>");
		}

	}

	
	/**
	 * processes given points and assigns them into clusters
	 *
	 * @param pointDataLst
	 *            Points to be assigned into clusters
	 * @param k
	 *            Number of clusters to be formed
	 *  
	 */
	public void kMeans(List<Point> pointDataLst, int k) {
		System.out.println("Starting K-Means..");
		selectCentroid(k);// select initial k random centroids

		// for (int i = 0; i < 25; i++) {
		boolean isSameCentroid = false;
		
		//until the centroid is changing
		while (!isSameCentroid) {
			// assign data points to centroid
			for (int j = 0; j < pointDataLst.size(); j++) {
				int currentClusterId = pointDataLst.get(j).getClusterId();
				int cId = getClosestCentroid(pointDataLst.get(j));
				if (currentClusterId != cId) {
					// put the data point in the cluster corresponding to the
					// cluster id
					clusterMap.get(Integer.toString(cId)).clusterDataMap.put(pointDataLst.get(j).getId(),
							pointDataLst.get(j));
					pointDataLst.get(j).setClusterId(cId);
					if (currentClusterId != -1)
						clusterMap.get(Integer.toString(currentClusterId)).clusterDataMap
								.remove(pointDataLst.get(j).getId());
				}
			}

			// recalculate centroid for all the clusters
			isSameCentroid = recalculateCentroid();
			if (isSameCentroid) {
				// if centroid not changing ,stop k-means iterations
				break;
			}
			// }
		}
		System.out.println("K-Means successfully Executed !");
	}

	/**
	 * Computes SSE - Error Sum of Square
	*/
	public void computeSSE() {
		// for each cluster
		HashMap<Integer, Point> clusterData = new HashMap<Integer, Point>();
		DecimalFormat df = new DecimalFormat("#.###");
		
		double dist = 0.0;
		for (int i = 0; i < centroidLst.size(); i++) {
			clusterData = clusterMap.get(Integer.toString(i)).getClusterDataMap();
			Iterator<Entry<Integer,Point>> it = clusterData.entrySet().iterator();

			// calculate sum of Xs and Ys for all data points in a cluster
			while (it.hasNext())								
			{
				Entry<Integer,Point> entry = (Entry<Integer,Point>) it.next();
				Point p = (Point) entry.getValue();
				dist = Double.parseDouble(df.format(calculateEucledianDistance(p, centroidLst.get(i))));
				sSE = Double.parseDouble(df.format(sSE + (dist * dist)));
			}
		}
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
		FileWriter fileWriter = new FileWriter(fileName, true);
		HashMap<Integer, Point> clusterData = new HashMap<Integer, Point>();
		String newLine = System.getProperty("line.separator");
		String result = "";
		fileWriter.write(newLine);
		fileWriter.write("Cluster -" + " Data Points");
		fileWriter.write(newLine);
		fileWriter.write("--------------------------");
		fileWriter.write(newLine);
		for (int i = 0; i < centroidLst.size(); i++) {
			clusterData = clusterMap.get(Integer.toString(i)).getClusterDataMap();
			result = "{ " + Integer.toString(i + 1) + " }   - " + clusterData.keySet().toString();
			fileWriter.write(result);
			fileWriter.write(newLine);
			fileWriter.write(newLine);
		}
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
		HashMap<Integer, Point> clusterData = new HashMap<Integer, Point>();
		DecimalFormat df = new DecimalFormat("#.###");
		double sumX = 0.0;
		double sumY = 0.0;
		boolean flag = true;
		List<Point> newCentLst = new ArrayList<Point>();
		for (int l = 0; l < centroidLst.size(); l++) {
			sumX = 0.0;
			sumY = 0.0;
			clusterData = clusterMap.get(Integer.toString(l)).getClusterDataMap(); // get data points for current cluster
			Iterator<Entry<Integer, Point>> it = clusterData.entrySet().iterator();
			double count = clusterData.entrySet().size();
			while (it.hasNext())// calculate sum of Xs and Ys for all data points in a cluster					
			{
				Entry<Integer,Point> entry = (Entry<Integer,Point>) it.next();
				Point p = (Point) entry.getValue();
				sumX = sumX + p.getX();
				sumY = sumY + p.getY();
			}
			// average X co-ordinates			
			double avgX = Double.parseDouble(df.format((sumX / count)));
			
			// average Y co-ordinates		
			double avgY = Double.parseDouble(df.format((sumY / count)));

			clusterMap.get(Integer.toString(l)).setCentX(avgX);
			clusterMap.get(Integer.toString(l)).setCentY(avgY);

			// add new centroid to centroidLst
			Point p1 = new Point();
			p1.setX(avgX);
			p1.setY(avgY);
			p1.setId(l);
			newCentLst.add(p1);

		}
		
		//check if newly calculated centroid is not changed
		for (int d = 0; d < newCentLst.size(); d++) {
			if (!centroidLst.contains(newCentLst.get(d))) {
				flag = false;
			}
		}
		centroidLst = newCentLst;
		return flag;
	}
	/**
	 * Given a point, this function finds out the closest centroid for it
	 * @param p
	 *            point p
	 * @return centId
	 * 		returns Id of the closest centroid
	 */
	public int getClosestCentroid(Point p) {
		double temp = 0;
		double dist = Double.MAX_VALUE;
		int centId = -1;
		for (int j = 0; j < centroidLst.size(); j++) {
			temp = calculateEucledianDistance(p, centroidLst.get(j));
			if (temp < dist) {
				dist = temp;
				centId = centroidLst.get(j).getId();
			}
		}

		return centId;
	}
    
	/**
	 * This function selects initial seed points(centroids)
	 * @param k
	 * 			total number of clusters
	 */
	public void selectCentroid(int k) {
		int i = 0;
		Point p = null;
		while (i != k) {
			Point newPoint = new Point();
			do {
				Random r = new Random();
				int randomCent = r.nextInt(dataSize) + 1;
				p = pointDataLst.get(randomCent - 1);
			} while (centroidLst.contains(p));

			newPoint.setId(i);
			newPoint.setX(p.getX());
			newPoint.setY(p.getY());
			newPoint.setClusterId(-1);
			centroidLst.add(newPoint);
			i++;
		}
	}
	
	/**
	 * Given a point, this function finds out the distance between the point and the given centroid.
	 * @param p
	 *            point p
	 * @param centroid
	 *            centroid
	 * @return dist
	 * 		returns eucledian distance
	 */
	public double calculateEucledianDistance(Point p, Point centroid) {
		double dist = 0.0;
		DecimalFormat df = new DecimalFormat("#.###");
		double resultY = Double.parseDouble(df.format(Math.abs(p.getY() - centroid.getY())));
		double resultX = Double.parseDouble(df.format(Math.abs(p.getX() - centroid.getX())));
		//calculate distance
		dist = Double.parseDouble(df.format(Math.sqrt((resultY) * (resultY) + (resultX) * (resultX)))); 
		return dist;
	}

	/**
	 * This function initializes clusterMap with given number of clusters 
	 * @param kCluster
	 *            Number of clusters
	 * */
	public void generateClusters(int kCluster) {
		for (int i = 0; i < kCluster; i++) {
			Cluster c = new Cluster();
			c.setId(i);
			clusterMap.put(Integer.toString(i), c);
		}
	}

	/**
	 * This function reads point data from csv file and creates a list of ponts 
	 * @param fileName
	 *            name of input file
	 * @return pointLst
	 * 			  returns a list of Points
	 * @throws IOException
	 * */
	// save the data from csv file into a list of points
	public List<Point> extractDataFrmCSV(String fileName) throws IOException {
		CSVReader csvReader = new CSVReader(new FileReader(new File(fileName)));
		List<String[]> list = csvReader.readAll();

		List<Point> pointLst = new ArrayList<Point>();
		dataSize = list.size() - 1;
		for (int j = 1; j < list.size(); j++) // j is counter for no of
												// instances in the data set
		{
			Point p = new Point();
			p.id = Integer.parseInt(list.get(j)[0]);
			p.x = Double.parseDouble(list.get(j)[1]);
			p.y = Double.parseDouble(list.get(j)[2]);
			p.clusterId = -1;
			pointLst.add(p);
		}
		
		csvReader.close();
		return pointLst;

	}

}
