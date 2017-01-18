package cs.ml;

import java.util.HashMap;

/* Class : Cluster
 * Description: This class represents a cluster
 * @author Sneha.Bangar
 */
public class Cluster {
	int id; //centroid id
	double centX; // x coordinate
	double centY; // y coordinate
	
	//cluster map
	HashMap<Integer, Point> clusterDataMap = new HashMap<Integer, Point>();

	public double getCentX() {
		return centX;
	}

	public void setCentX(double centX) {
		this.centX = centX;
	}

	public double getCentY() {
		return centY;
	}

	public void setCentY(double centY) {
		this.centY = centY;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public HashMap<Integer, Point> getClusterDataMap() {
		return clusterDataMap;
	}

	public void setClusterDataMap(HashMap<Integer, Point> clusterDataMap) {
		this.clusterDataMap = clusterDataMap;
	}

}
