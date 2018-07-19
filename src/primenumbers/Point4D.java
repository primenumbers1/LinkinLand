package primenumbers;

public class Point4D extends Point3D {

	private String hexColor=null;
	private int index=0;
	
	public Point4D(double x, double y, double z) {
		super(x, y, z);
	}
	
	private Point4D(double x, double y, double z, String hexColor, int index) {
		super(x, y, z);
		this.hexColor = hexColor;
		this.index = index;
	}
	
	public Point4D() {
		super();
	}
	@Override
	public Point4D clone(){
		
		Point4D p4=new Point4D(this.x,this.y,this.z,this.hexColor,this.index);
		return p4;
	}
	
	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}
	
	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}
	
}