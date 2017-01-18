package cs.ml;

/* Class : Point
 * Description: This class represents a point
 * @author Sneha.Bangar
 */
public class Point implements Comparable<Point> {
	int id;
	double x;
	double y;
	//id of the cluster this point belongs to
	int clusterId;

	/**
	 * @return id
	 * */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	/**
	 * @return x
	 * */
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	/**
	 * @return y
	 * */
	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	/**
	 * @return clusterId
	 * */
	public int getClusterId() {
		return clusterId;
	}

	public void setClusterId(int clusterId) {
		this.clusterId = clusterId;
	}

	/**
	 * @return result
	 * 		calculate and return hashcode
	 * */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(x);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(y);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/**
	 * @return 
	 * 		return true if the 2 objects are equal
	 * */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Point other = (Point) obj;
		if (Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x))
			return false;
		if (Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y))
			return false;
		return true;
	}

	/**
	 * @return 
	 * 		return 1 if id greater else 0
	 * */
	@Override
	public int compareTo(Point o) {
		if (this.id > o.id)
			return 1;
		return 0;
	}

}
