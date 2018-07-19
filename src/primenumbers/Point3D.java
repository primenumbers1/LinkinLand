package primenumbers;

public class Point3D extends DPoint3D {

	public double p_x;
	public double p_y;
	public double p_z;

	public double texture_x;
	public double texture_y;

	public Object data=null;

	private boolean isSelected=false;

	public Point3D(double x, double y, double z, double pX, double pY,
			double pZ, double textureX, double textureY) {
		super(x,y,z);
		p_x = pX;
		p_y = pY;
		p_z = pZ;
		texture_x = textureX;
		texture_y = textureY;
	}

	public Point3D(double x, double y, double z, double p_x, double p_y,
			double p_z) {
		super(x,y,z);
		this.p_x = p_x;
		this.p_y = p_y;
		this.p_z = p_z;
	}

	public Point3D() {
		//NOTHING TO DO
	}

	public Point3D(double x, double y, double z) {
		super(x,y,z);
	}

	public boolean equals(Point3D p){
		return this.x==p.x && this.y==p.y && this.z==p.z;

	}
	@Override
	public Point3D clone()  {
		Point3D p=new Point3D(this.x,this.y,this.z,this.p_x,this.p_y,this.p_z,this.texture_x,this.texture_y);
		p.setData(getData());
		return p;
	}

	public boolean isSelected() {
		return isSelected;
	}


	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public Point3D substract(Point3D p0) {
		Point3D pRes=new Point3D(this.x-p0.x,this.y-p0.y,this.z-p0.z);
		return pRes;
	}

	public double getP_z() {
		return p_z;
	}

	public void setP_z(double p_z) {
		this.p_z = p_z;
	}

	public static void foundPX_PY_PZ_TEXTURE_Intersection(Point3D pstart, Point3D pend,
			double y,Point3D intersect) {


		double i_pstart_p_y=1.0/(pstart.p_y);
		double i_end_p_y=1.0/(pend.p_y);

		double l=(y-pstart.y)/(pend.y-pstart.y);

		double yi=1.0/((1-l)*i_pstart_p_y+l*i_end_p_y);

		intersect.p_x= ((1-l)*pstart.p_x*i_pstart_p_y+l*pend.p_x*i_end_p_y)*yi;
		intersect.p_y=  1.0/((1-l)*i_pstart_p_y+l*i_end_p_y);
		intersect.p_z=  ((1-l)*pstart.p_z*i_pstart_p_y+l*pend.p_z*i_end_p_y)*yi;

		intersect.texture_x=  ((1-l)*pstart.texture_x*i_pstart_p_y+l*pend.texture_x*i_end_p_y)*yi;
		intersect.texture_y=  ((1-l)*pstart.texture_y*i_pstart_p_y+l*pend.texture_y*i_end_p_y)*yi;
	}

	public static final double foundPXIntersection(Point3D pstart, Point3D pend,
			double y) {

		double i_pstart_p_y=1.0/(pstart.p_y);
		double i_end_p_y=1.0/(pend.p_y);

		double l=(y-pstart.y)/(pend.y-pstart.y);

		double yi=1.0/((1-l)*i_pstart_p_y+l*i_end_p_y);

		return ((1-l)*pstart.p_x*i_pstart_p_y+l*pend.p_x*i_end_p_y)*yi;
	}

	public void setTexurePositions(double textureX, double textureY){
		texture_x = textureX;
		texture_y = textureY;
	}

	public void setTexurePositions(Point3D p){
		texture_x = p.x;
		texture_y = p.y;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}
	
}