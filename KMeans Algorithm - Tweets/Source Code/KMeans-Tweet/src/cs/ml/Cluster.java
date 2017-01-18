package cs.ml;

/* Class : Cluster
 * Description: This class represents a cluster
 * @author Sneha.Bangar
 */
import java.util.ArrayList;
import java.util.List;

public class Cluster {
	/*Id*/
	private String id;
	
	/*Centroid text*/
	private String centText;
	
	//List of points in cluster
	private List<String> dataLst = new ArrayList<String>();

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCentText() {
		return centText;
	}

	public void setCentText(String centText) {
		this.centText = centText;
	}

	public List<String> getDataLst() {
		return dataLst;
	}

	public void setDataLst(List<String> dataLst) {
		this.dataLst = dataLst;
	}

}
