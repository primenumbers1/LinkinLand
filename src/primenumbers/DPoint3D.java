package primenumbers;

public class DPoint3D extends DPoint2D {

	public double z;

	public DPoint3D(){
	}

	public DPoint3D(double x, double y, double z) {
		super();
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public double getZ() {
		return z;
	}

	public void setZ(double z) {
		this.z = z;
	}

	public static double calculateDotProduct(DPoint3D a,
			DPoint3D b) {
		return a.x*b.x+a.y*b.y+a.z*b.z;
	}

	public static double calculateDotProduct(double px,double py,double pz,
			DPoint3D b) {
		return px*b.x+py*b.y+pz*b.z;
	}

	public static double calculateNorm(DPoint3D a) {
		return Math.sqrt(calculateDotProduct(a,a));
	}

	public static double calculateSquareNorm(DPoint3D a) {
		return calculateDotProduct(a,a);
	}

	public static double squareDistance(double x0, double y0, double z0, double x1, double y1, double z1) {
		return (x0-x1)*(x0-x1)+(y0-y1)*(y0-y1)+(z0-z1)*(z0-z1);
	}

	/**
	 * Difference function which do not alter the current Point
	 * and creates a new Point
	 * */
	public DPoint3D substract(DPoint3D p0) {
		DPoint3D pRes=new DPoint3D(this.x-p0.x,this.y-p0.y,this.z-p0.z);
		return pRes;
	}

	public static void calculateCrossProduct(
			double a_x,double a_y,double a_z,
			double b_x,double b_y,double b_z,
			DPoint3D r) {
		r.x=a_y*b_z-b_y*a_z;
		r.y=b_x*a_z-a_x*b_z;
		r.z=a_x*b_y-b_x*a_y;
	}

	public static void calculateCrossProduct(DPoint3D a, DPoint3D b, DPoint3D r) {
		calculateCrossProduct( a.x,a.y,a.z,b.x,b.y,b.z, r) ;
	}

	public static DPoint3D calculateCrossProduct(DPoint3D a, DPoint3D b) {

		DPoint3D r=new DPoint3D();
		r.x=a.y*b.z-b.y*a.z;
		r.y=b.x*a.z-a.x*b.z;
		r.z=a.x*b.y-b.x*a.y;

		return r;
	}

	public static double calculateCosin(DPoint3D a, DPoint3D b) {

		double prod=-(calculateSquareNorm(b.substract(a))-calculateSquareNorm(a)-calculateSquareNorm(b))
				/(2*calculateNorm(a)*calculateNorm(b));
		return prod;
	}


	public static final double foundXIntersection(DPoint3D p1, DPoint3D p2,
			double y) {

		if(p2.y-p1.y<1 && p2.y-p1.y>-1) {
			return p1.x;
		}

		return p1.x+((p2.x-p1.x)*(y-p1.y))/(p2.y-p1.y);
	}

	@Override
	public DPoint3D clone()  {
		DPoint3D p=new DPoint3D(this.x,this.y,this.z);
		return p;
	}

	public DPoint3D invertPoint(){
		return new DPoint3D(-x,-y,-z);
	}

	/**
	 * Create a new point which is the versor of the current
	 * */
	public DPoint3D calculateVersor(){

		double norm=calculateNorm(this);
		if(norm==0) {
			return new DPoint3D(0,0,0);
		}
		double i_norm=1.0/norm;
		DPoint3D versor=new DPoint3D(this.x*i_norm,this.y*i_norm,this.z*i_norm);

		return versor;
	}

	/**
	 * Change current point to a versor
	 * */
	public DPoint3D reduceToVersor(){

		double norm=calculateNorm(this);
		if(norm==0) {
			return this;
		}
		double i_norm=1.0/norm;
		this.x=this.x*i_norm;
		this.y=this.y*i_norm;
		this.z=this.z*i_norm;

		return this;
	}

	public void rotate(double x0, double y0,double cos, double sin ) {

		double xx=this.x;
		double yy=this.y;
		double zz=this.z;

		this.x=x0+(xx-x0)*cos-(yy-y0)*sin;
		this.y=y0+(yy-y0)*cos+(xx-x0)*sin;
	}

	public void rotate(double x0, double y0, double z0,
			double viewDirectionCos, double viewDirectionSin,
			double sightLatDirectionCos, double sightLatDirectionSin) {

		double xx=this.x-x0;
		double yy=this.y-y0;
		double zz=this.z-z0;

		this.x=x0+(viewDirectionCos*xx-viewDirectionSin*sightLatDirectionCos*yy+viewDirectionSin*sightLatDirectionSin*zz);
		this.y=y0+(viewDirectionSin*xx+viewDirectionCos*sightLatDirectionCos*yy-viewDirectionCos*sightLatDirectionSin*zz);
		this.z=z0+(sightLatDirectionSin*yy+sightLatDirectionCos*zz);


		/*this.x=x0+(viewDirectionCos*xx+viewDirectionSin*yy);
		this.y=y0+(-sightLatDirectionCos*viewDirectionSin*xx+sightLatDirectionCos*viewDirectionCos*yy+sightLatDirectionSin*zz);
		this.z=z0+(+sightLatDirectionSin*viewDirectionSin*xx-sightLatDirectionSin*viewDirectionCos*yy+sightLatDirectionCos*zz);*/
	}

	/**
	 * Rotate of cos, sin around y axis (x0,z0)
	 *
	 * @param x0
	 * @param z0
	 * @param cos
	 * @param sin
	 */
	public void rotateX(double y0, double z0, double cos, double sin) {

		double xx=this.x;
		double yy=this.y;
		double zz=this.z;

		this.y=y0+(yy-y0)*cos-(zz-z0)*sin;
		this.z=z0+(zz-z0)*cos+(yy-y0)*sin;
	}

	public void translate(double dx, double dy, double dz) {
		setX(this.getX()+dx);
		setY(this.getY()+dy);
		setZ(this.getZ()+dz);
	}

	/**
	 * Difference function which alter the current point
	 * */
	public DPoint3D minus(DPoint3D p0) {

		this.x=this.x-p0.x;
		this.y=this.y-p0.y;
		this.z=this.z-p0.z;

		return this;
	}

	@Override
	public String toString() {
		return x+" "+y+" "+z;
	}
	
}