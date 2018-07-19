package primenumbers;

public class BarycentricCoordinates {

	private double p0_x=0;
	private double p0_y=0;
	private double p0_z=0;

	private double p1_x=0;
	private double p1_y=0;
	private double p1_z=0;

	private double p2_x=0;
	private double p2_y=0;
	private double p2_z=0;

	public DPoint2D pt0=null;
	public DPoint2D pt1=null;
	public DPoint2D pt2=null;

	private DPoint3D n=new DPoint3D();
	private double inv_nSquare=0;

	private double nda_x=0;
	private double nda_y=0;
	private double nda_z=0;

	private double ndb_x=0;
	private double ndb_y=0;
	private double ndb_z=0;

	private double ndc_x=0;
	private double ndc_y=0;
	private double ndc_z=0;

	public static void main(String[] args) {

		BarycentricCoordinates bc=new BarycentricCoordinates(
				new DPoint3D(0,0.0,0.0),
				new DPoint3D(1.0,0.0,0.0),
				new DPoint3D(0,2.0,0.0));

		DPoint3D p=new DPoint3D(0.0,1.0,0.0);
		System.out.println("p0="+p);

		DPoint3D p0=bc.getBarycentricCoordinates(p.x,p.y,p.z);
		System.out.println("pc="+p0);

		DPoint3D p1=bc.getRealCoordinates(p0);
		System.out.println("p1="+p1);

	}

	/**
	 * TEST constructor
	 *
	 * @param p0
	 * @param p1
	 * @param p2
	 */
	private BarycentricCoordinates(DPoint3D p0,DPoint3D p1,DPoint3D p2) {

		p0_x=p0.x;
		p0_y=p0.y;
		p0_z=p0.z;

		p1_x=p1.x;
		p1_y=p1.y;
		p1_z=p1.z;

		p2_x=p2.x;
		p2_y=p2.y;
		p2_z=p2.z;

		DPoint3D.calculateCrossProduct(p1.substract(p0), p2.substract(p0),n);
		inv_nSquare=1.0/DPoint3D.calculateDotProduct(n,n);

		nda_x=p2.x-p1.x;
		nda_y=p2.y-p1.y;
		nda_z=p2.z-p1.z;

		ndb_x=p0.x-p2.x;
		ndb_y=p0.y-p2.y;
		ndb_z=p0.z-p2.z;

		ndc_x=p1.x-p0.x;
		ndc_y=p1.y-p0.y;
		ndc_z=p1.z-p0.z;
	}

	private BarycentricCoordinates(
			double p0_x,double p0_y,double p0_z,
			double p1_x,double p1_y,double p1_z,
			double p2_x,double p2_y,double p2_z,
			DPoint2D pt0,DPoint2D pt1,DPoint2D pt2) {

		this.p0_x=p0_x;
		this.p0_y=p0_y;
		this.p0_z=p0_z;

		this.p1_x=p1_x;
		this.p1_y=p1_y;
		this.p1_z=p1_z;

		this.p2_x=p2_x;
		this.p2_y=p2_y;
		this.p2_z=p2_z;

		this.pt0=pt0;
		this.pt1=pt1;
		this.pt2=pt2;

		DPoint3D.calculateCrossProduct(p1_x-p0_x, p1_y-p0_y, p1_z-p0_z, p2_x-p0_x, p2_y-p0_y, p2_z-p0_z,n);
		inv_nSquare=1.0/DPoint3D.calculateDotProduct(n,n);

		nda_x=p2_x-p1_x;
		nda_y=p2_y-p1_y;
		nda_z=p2_z-p1_z;

		ndb_x=p0_x-p2_x;
		ndb_y=p0_y-p2_y;
		ndb_z=p0_z-p2_z;

		ndc_x=p1_x-p0_x;
		ndc_y=p1_y-p0_y;
		ndc_z=p1_z-p0_z;
	}

	public BarycentricCoordinates(Polygon3D triangle) {
		this(
				triangle.xpoints[0],triangle.ypoints[0],triangle.zpoints[0],
				triangle.xpoints[1],triangle.ypoints[1],triangle.zpoints[1],
				triangle.xpoints[2],triangle.ypoints[2],triangle.zpoints[2],
				triangle.getTexturePoint(0),triangle.getTexturePoint(1),triangle.getTexturePoint(2));
	}

	//variables for calculus
	DPoint3D pOutput=new DPoint3D();
	DPoint3D na=new DPoint3D();
	DPoint3D nb=new DPoint3D();
	DPoint3D nc=new DPoint3D();

	public DPoint3D getBarycentricCoordinates(double p_x, double p_y, double p_z) {

		DPoint3D.calculateCrossProduct(nda_x,nda_y,nda_z,p_x-p1_x,p_y-p1_y,p_z-p1_z,na);
		DPoint3D.calculateCrossProduct(ndb_x,ndb_y,ndb_z,p_x-p2_x,p_y-p2_y,p_z-p2_z,nb);
		DPoint3D.calculateCrossProduct(ndc_x,ndc_y,ndc_z,p_x-p0_x,p_y-p0_y,p_z-p0_z,nc);

		pOutput.x=DPoint3D.calculateDotProduct(n,na)*inv_nSquare;
		pOutput.y=DPoint3D.calculateDotProduct(n,nb)*inv_nSquare;
		pOutput.z=DPoint3D.calculateDotProduct(n,nc)*inv_nSquare;

		return pOutput;
	}

	private DPoint3D getRealCoordinates(DPoint3D p){

		DPoint3D pr=new DPoint3D();
		pr.x=p.x*(p0_x)+p.y*p1_x+(1-p.x-p.y)*p2_x;
		pr.y=p.x*(p0_y)+p.y*p1_y+(1-p.x-p.y)*p2_y;
		pr.z=p.x*(p0_z)+p.y*p1_z+(1-p.x-p.y)*p2_z;

		return pr;
	}

	public double getRealTriangleArea(){

		return getTriangleArea(
				p0_x,p0_y,p0_z,
				p1_x,p1_y,p1_z,
				p2_x,p2_y,p2_z
				);
	}


	/**
	 * Using the module of the cross product divided by 2:
	 *
	 * @param x0
	 * @param y0
	 * @param x1
	 * @param y1
	 * @param x2
	 * @param y2
	 * @return
	 */
	public static double getTriangleArea(
			double x0,double y0,double z0,
			double x1,double y1,double z1,
			double x2,double y2,double z2
			){

		//calculus points
		DPoint3D a=new DPoint3D();
		DPoint3D b=new DPoint3D();

		a.x=x1-x0;
		a.y=y1-y0;
		a.z=z1-z0;

		b.x=x2-x0;
		b.y=y2-y0;
		b.z=z2-z0;

		double cx=(a.y*b.z-a.z*b.y);
		double cy=-(a.x*b.z-a.z*b.x);
		double cz=(a.x*b.y-b.x*a.y);

		return 0.5*Math.sqrt(cx*cx+cy*cy+cz*cz);
	}
	
}