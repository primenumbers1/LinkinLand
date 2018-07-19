package primenumbers;

public class DPoint2D {

	public double x;
	public double y;

	public DPoint2D(){
	}

	public DPoint2D(double x, double y) {
		super();
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	@Override
	public String toString() {
		return x+" "+y;
	}
	
}