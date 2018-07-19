package primenumbers;

import java.awt.Point;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.geom.Line2D;
import java.util.ArrayList;

public class Polygon3D extends Polygon {

	private String hexColor="FFFFFF";
	private int index=0;
	private int index1=-1;

	private double shadowCosin=1;

	//textures point:
	public int[] xtpoints=null;
	public int[] ytpoints=null;
	public int[] zpoints=null;

	public Polygon3D(int npoints, int[] xpoints, int[] ypoints, int[] zpoints,int[] xtpoints, int[] ytpoints) {

		super(xpoints,ypoints,npoints);
		this.zpoints = zpoints;

		this.xtpoints = xtpoints;
		this.ytpoints = ytpoints;
	}

	public Polygon3D(int npoints, int[] xpoints, int[] ypoints, int[] zpoints) {

		super(xpoints,ypoints,npoints);
		this.zpoints = zpoints;

		this.xtpoints = new int[npoints];
		this.ytpoints = new int[npoints];
	}

	public Polygon3D(int npoints) {
		this.xpoints = new int[npoints];
		this.ypoints = new int[npoints];
		this.zpoints = new int[npoints];
		this.xtpoints = new int[npoints];
		this.ytpoints = new int[npoints];
		this.npoints=npoints;
	}

	void addPoint(DPoint3D p) {
		addPoint((int)p.x,(int)p.y,(int)p.z);
	}

	private void addPoint(int x, int y,int z, int xt,int yt) {

		Polygon3D new_pol=new Polygon3D(this.npoints+1);

		for(int i=0;i<this.npoints;i++){
			new_pol.xpoints[i]=this.xpoints[i];
			new_pol.ypoints[i]=this.ypoints[i];
			new_pol.zpoints[i]=this.zpoints[i];
			new_pol.xtpoints[i]=this.xtpoints[i];
			new_pol.ytpoints[i]=this.ytpoints[i];
		}
		new_pol.xpoints[this.npoints]=x;
		new_pol.ypoints[this.npoints]=y;
		new_pol.zpoints[this.npoints]=z;
		new_pol.xtpoints[this.npoints]=xt;
		new_pol.ytpoints[this.npoints]=yt;

		this.setNpoints(new_pol.npoints);

		this.setXpoints(new_pol.xpoints);
		this.setYpoints(new_pol.ypoints);
		this.setZpoints(new_pol.zpoints);
		this.setXtpoints(new_pol.xtpoints);
		this.setYtpoints(new_pol.ytpoints);
	}

	private void addPoint(int x, int y,int z) {
		addPoint(x,  y, z,0,0);
	}
	@Override
	public Polygon3D clone(){
		return buildTranslatedPolygon(0,0,0);
	}


	public Polygon3D() {
	}

	public static Polygon3D[] divideIntoTriangles(Polygon3D pol){

		if(pol.npoints<3) {
			return new Polygon3D[0];
		}

		Polygon3D[] triangles=new Polygon3D[pol.npoints-2];

		if(pol.npoints==3){
			triangles[0]=pol;
			return triangles;
		}

		for(int i=1;i<pol.npoints-1;i++){

			Polygon3D triangle=new Polygon3D(3);
			triangle.setShadowCosin(pol.getShadowCosin());

			triangle.xpoints[0]=pol.xpoints[0];
			triangle.ypoints[0]=pol.ypoints[0];
			triangle.zpoints[0]=pol.zpoints[0];
			triangle.xtpoints[0]=pol.xtpoints[0];
			triangle.ytpoints[0]=pol.ytpoints[0];

			triangle.xpoints[1]=pol.xpoints[i];
			triangle.ypoints[1]=pol.ypoints[i];
			triangle.zpoints[1]=pol.zpoints[i];
			triangle.xtpoints[1]=pol.xtpoints[i];
			triangle.ytpoints[1]=pol.ytpoints[i];

			triangle.xpoints[2]=pol.xpoints[i+1];
			triangle.ypoints[2]=pol.ypoints[i+1];
			triangle.zpoints[2]=pol.zpoints[i+1];
			triangle.xtpoints[2]=pol.xtpoints[i+1];
			triangle.ytpoints[2]=pol.ytpoints[i+1];

			triangles[i-1]=triangle;

		}
		return triangles;
	}

	public static Polygon3D extractSubPolygon3D(Polygon3D pol,int numAngles,int startAngle){

		int[] xpoints = new int[numAngles];
		int[] ypoints = new int[numAngles];
		int[] zpoints = new int[numAngles];

		int counter=0;

		for(int i=startAngle;i<numAngles+startAngle;i++){

			xpoints[counter]=pol.xpoints[i%pol.npoints];
			ypoints[counter]=pol.ypoints[i%pol.npoints];
			zpoints[counter]=pol.zpoints[i%pol.npoints];

			counter++;
		}

		Polygon3D new_pol = new Polygon3D(numAngles,xpoints,ypoints,zpoints);
		new_pol.setHexColor(pol.getHexColor());
		return new_pol;
	}


	private static Polygon3D removeRedundant(Polygon3D pol) {

		boolean redundant=false;

		if(pol.xpoints[0]==pol.xpoints[pol.npoints-1]
				&&    pol.ypoints[0]==pol.ypoints[pol.npoints-1]
				) {
			redundant=true;
		}

		if(!redundant) {
			return pol;
		} else{
			Polygon3D new_pol=new Polygon3D(pol.npoints-1);

			for(int i=0;i<pol.npoints-1;i++){

				new_pol.xpoints[i]=pol.xpoints[i];
				new_pol.ypoints[i]=pol.ypoints[i];

			}
			return new_pol;
		}
	}

	@Override
	public String toString() {
		StringBuffer sb=new StringBuffer();

		if(zpoints!=null) {
			for(int i=0;i<npoints;i++){
				sb.append(xpoints[i]+","+ypoints[i]+","+zpoints[i]+"_");

			}
		} else {
			for(int i=0;i<npoints;i++){
				sb.append(xpoints[i]+","+ypoints[i]+"_");
			}
		}
		return sb.toString();
	}

	public static Polygon3D clipPolygon3DInY(Polygon3D  p_old,int y){

		boolean foundValidPoint=false;
		for(int i=0;i<p_old.npoints;i++){
			int yi=p_old.ypoints[i];
			if(yi>=y){
				foundValidPoint=true;
				break;
			}
		}

		if(!foundValidPoint) {
			return null;
		}

		ArrayList<Point3D> newPoints=new ArrayList<Point3D>();

		for(int i=0;i<p_old.npoints;i++){

			int x1=p_old.xpoints[i];
			int y1=p_old.ypoints[i];
			int z1=p_old.zpoints[i];

			int ii=(i+1<p_old.npoints?i+1:0);

			int x2=p_old.xpoints[ii];
			int y2=p_old.ypoints[ii];
			int z2=p_old.zpoints[ii];

			if((y1-y)>=0 && (y2-y)>=0){
				newPoints.add(new Point3D(x1,y1,z1));
			}
			else if(((y1-y)<0 && (y2-y)>=0) || ((y1-y)>=0 && (y2-y)<0)){

				if((y1-y)>=0) {
					newPoints.add(new Point3D(x1,y1,z1));
				}

				if(y1!=y2){

					double l=(y-y1)*1.0/(y2-y1);
					double yn=y;
					double zn=z1+l*(z2-z1);
					double xn=x1+l*(x2-x1);

					newPoints.add(new Point3D(xn,yn,zn));
				}
			}
		}

		Polygon3D p_new=new Polygon3D(newPoints.size());
		p_new.setShadowCosin(p_old.getShadowCosin());

		int new_size=newPoints.size();

		for(int j=0;j<new_size;j++){

			Point3D p=newPoints.get(j);

			p_new.xpoints[j]=(int) p.x;
			p_new.ypoints[j]=(int) p.y;
			p_new.zpoints[j]=(int) p.z;
		}
		return p_new;
	}

	public static Polygon3D clipPolygon3DInX(Polygon3D  p_old,int x){

		ArrayList<Point3D> newPoints=new ArrayList<Point3D>();

		for(int i=0;i<p_old.npoints;i++){

			int x1=p_old.xpoints[i];
			int y1=p_old.ypoints[i];
			int z1=p_old.zpoints[i];


			int x2=p_old.xpoints[(i+1)%p_old.npoints];
			int y2=p_old.ypoints[(i+1)%p_old.npoints];
			int z2=p_old.zpoints[(i+1)%p_old.npoints];


			if((x1-x)>=0 && (x2-x)>=0){

				newPoints.add(new Point3D(x1,y1,z1));

			}
			else if(((x1-x)<0 && (x2-x)>=0) || ((x1-x)>=0 && (x2-x)<0)){

				if((x1-x)>=0) {
					newPoints.add(new Point3D(x1,y1,z1));
				}

				if(x1!=x2){

					double l=(x-x1)*1.0/(x2-x1);
					double xn=x;
					double zn=z1+l*(z2-z1);
					double yn=y1+l*(y2-y1);

					newPoints.add(new Point3D(xn,yn,zn));
				}
			}
		}

		Polygon3D p_new=new Polygon3D(newPoints.size());

		int new_size=newPoints.size();

		for(int j=0;j<new_size;j++){

			Point3D p=newPoints.get(j);

			p_new.xpoints[j]=(int) p.x;
			p_new.ypoints[j]=(int) p.y;
			p_new.zpoints[j]=(int) p.z;
		}

		return p_new;
	}

	public static boolean isIntersect(double p_x,double p_y, Rectangle bounds) {

		if(p_y>=bounds.getMinY() &&
				p_y<=bounds.getMaxY() &&
				p_x>=bounds.getMinX() &&
				p_x<=bounds.getMaxX()
				) {
			return true;
		}
		return false;
	}

	private static Point insersect(Point p1, Point p2, int x2, int x1, int y2, int y1) {

		Line2D.Double line1=new Line2D.Double(x2,y2,x1,y1);
		Line2D.Double line2=new Line2D.Double(p2.x,p2.y,p1.x,p1.y);

		//if(!line1.intersectsLine(line2))
		//	return null;

		Point insersection=new Point();

		if(x2!=x1 && p2.x!=p1.x){

			double a1=(y2-y1)/(x2-x1);
			double a2=(p2.y-p1.y)/(p2.x-p1.x);
			double b1=(-x1*y2+y1*x2)/(x2-x1);
			double b2=(-p2.y*p1.x+p1.y*p2.x)/(p2.x-p1.x);


			insersection.x=(int)((-b2+b1)/(a2-a1));
			insersection.y=(int)((a2*b1-b2*a1)/(a2-a1));

		}
		else if(x2==x1 && p2.x!=p1.x){

			double a2=(p2.y-p1.y)/(p2.x-p1.x);
			double b2=(-p2.y*p1.x+p1.y*p2.x)/(p2.x-p1.x);

			insersection.x=x2;
			insersection.y=(int) (a2*x2+b2);
		}
		else if(x2!=x1 && p2.x==p1.x){

			double a1=(y2-y1)/(x2-x1);
			double b1=(-x1*y2+y1*x2)/(x2-x1);

			insersection.x=p2.x;
			insersection.y=(int) (a1*p2.x+b1);
		}
		return insersection;
	}

	private static boolean isInsideClipPlane(int pox,int poy, int ax, int ay) {
		return (ax*poy-ay*pox)>=0;
	}

	public static boolean isFacing(Polygon3D pol,Point3D observer){

		///calculus variables
		DPoint3D pf0=new DPoint3D();
		DPoint3D vectorObs=new DPoint3D();

		pf0.x=pol.xpoints[0];
		pf0.x=pol.ypoints[0];
		pf0.x=pol.zpoints[0];

		vectorObs.x=observer.x-pf0.x;
		vectorObs.y=observer.y-pf0.y;
		vectorObs.z=observer.z-pf0.z;

		DPoint3D normal=findNormal(pol);

		double cosin=Point3D.calculateCosin(normal,vectorObs);

		return cosin>=0;
	}


	public static DPoint3D findNormal(Polygon3D pol){

		///calculus variables
		DPoint3D normal=new DPoint3D();
		DPoint3D p0=new DPoint3D(pol.xpoints[0],pol.ypoints[0],pol.zpoints[0]);
		DPoint3D p1=new DPoint3D(pol.xpoints[1],pol.ypoints[1],pol.zpoints[1]);
		DPoint3D p2=new DPoint3D(pol.xpoints[2],pol.ypoints[2],pol.zpoints[2]);

		DPoint3D.calculateCrossProduct(p1.minus(p0),p2.minus(p0),normal);

		return normal;
	}


	public boolean hasInsidePoint(double x,double y){

		for(int i=0;i<npoints;i++){

			AnalyticLine line=new AnalyticLine(xpoints[i],ypoints[i],xpoints[(i+1)%npoints],ypoints[(i+1)%npoints]);

			double valPoint=line.signum(x,y);

			//near the border the precise calcutation is very difficult
			if(Math.abs(valPoint)<0.01) {
				valPoint=0;
			}

			for(int j=2;j<npoints;j++){

				double valVertex = line.signum(xpoints[(j+i)%npoints],ypoints[(j+i)%npoints]);
				if(valVertex*valPoint<0) {
					return false;
				}
			}
		}

		return true;
	}

	public static class AnalyticLine{

		private double a;
		private double b;
		private double c;

		private AnalyticLine(double x1, double y1, double x0,double y0) {
			super();
			this.a = (y0-y1);
			this.b = -(x0-x1);
			this.c = (x0*y1-y0*x1);
		}

		private double signum(double x,double y){
			return a*x+b*y+c;
		}
	}

	public static void main(String[] args) {

		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];

		cx[0]=10;
		cy[0]=10;
		cx[1]=0;
		cy[1]=50;
		cx[2]=50;
		cy[2]=60;
		cx[3]=50;
		cy[3]=-10;

		int[] cx1=new int[3];
		int[] cy1=new int[3];
		int[] cz1=new int[3];

		Polygon3D p1=new Polygon3D(4,cx,cy,cz);

		cx1[0]=10;
		cy1[0]=-20;
		cx1[1]=50;
		cy1[1]=40;
		cx1[2]=30;
		cy1[2]=-40;

		Polygon3D p2=new Polygon3D(3,cx1,cy1,cz1);

		//System.out.println(p2.hasInsidePoint(20,0));
		//System.out.println(p2.hasInsidePoint(30,40));
		//System.out.println(p2.hasInsidePoint(40,10));

		/*Area out=new Area(p1);
		Area in=new Area(p2);

		Polygon3D p3=fromAreaToPolygon2D(out);

		System.out.println(p3);
		out.intersect(in);
		Polygon3D p_res=clipPolygon3D(p1,p2);*/
		System.out.println(p2);
		Polygon3D p3=clipPolygon3DInY(p2,0);
		System.out.println(p3);
	}


	public Polygon3D buildTranslatedPolygon(double dx,double dy,double dz){

		Polygon3D translatedPolygon=new Polygon3D(this.npoints);

		for(int i=0;i<this.npoints;i++){

			translatedPolygon.xpoints[i]=(int) (this.xpoints[i]+dx);
			translatedPolygon.ypoints[i]=(int) (this.ypoints[i]+dy);
			translatedPolygon.zpoints[i]=(int) (this.zpoints[i]+dz);
			translatedPolygon.xtpoints[i] = this.xtpoints[i];
			translatedPolygon.ytpoints[i] = this.ytpoints[i];

		}
		translatedPolygon.setHexColor(getHexColor());
		translatedPolygon.setIndex(getIndex());
		translatedPolygon.setIndex1(getIndex1());

		return translatedPolygon;
	}

	public void invertY(int y0) {
		for(int i=0;i<this.npoints;i++){
			this.ypoints[i]=y0-this.ypoints[i];
		}
	}

	public static boolean isIntersect(Polygon p_in, Rectangle bounds) {
		Rectangle polBounds =p_in.getBounds();

		if(polBounds.getMaxY()>=bounds.getMinY() &&
				polBounds.getMinY()<=bounds.getMaxY() &&
				polBounds.getMaxX()>=bounds.getMinX() &&
				polBounds.getMinX()<=bounds.getMaxX()
				) {
			return true;
		}

		return false;
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

	public void setNpoints(int npoints) {
		this.npoints = npoints;
	}

	public int[] getXpoints() {
		return xpoints;
	}

	public void setXpoints(int[] xpoints) {
		this.xpoints = xpoints;
	}

	public int[] getYpoints() {
		return ypoints;
	}

	public void setYpoints(int[] ypoints) {
		this.ypoints = ypoints;
	}

	public int[] getZpoints() {
		return zpoints;
	}

	public void setZpoints(int[] zpoints) {
		this.zpoints = zpoints;
	}

	public Point3D getPoint(int index){

		if(index>=npoints) {
			return null;
		}

		return new Point3D(xpoints[index],ypoints[index],zpoints[index]);
	}

	public DPoint2D getTexturePoint(int index){

		if(index>=npoints) {
			return null;
		}

		return new DPoint2D(xtpoints[index],ytpoints[index]);
	}

	public static DPoint3D findCentroid(Polygon3D p3d) {

		double x=0;
		double y=0;
		double z=0;

		int n=p3d.npoints;
		for (int i = 0; i <n;  i++) {
			x+=p3d.xpoints[i];
			y+=p3d.ypoints[i];
			z+=p3d.zpoints[i];
		}

		return new DPoint3D(x/n,y/n,z/n);
	}

	public static void rotate(Polygon p3d, DPoint3D p0,
			double rotation_angle) {

		int n=p3d.npoints;

		double ct=Math.cos(rotation_angle);
		double st=Math.sin(rotation_angle);

		for (int i = 0; i <n;  i++) {
			double xx=p3d.xpoints[i];
			double yy=p3d.ypoints[i];

			p3d.xpoints[i]=(int) (ct*(xx-p0.x)-st*(yy-p0.y)+p0.x);
			p3d.ypoints[i]=(int) (st*(xx-p0.x)+ct*(yy-p0.y)+p0.y);
		}
	}

	public double getShadowCosin() {
		return shadowCosin;
	}

	public void setShadowCosin(double shadowCosin) {
		this.shadowCosin = shadowCosin;
	}

	public int[] getXtpoints() {
		return xtpoints;
	}

	public void setXtpoints(int[] xtpoints) {
		this.xtpoints = xtpoints;
	}

	public int[] getYtpoints() {
		return ytpoints;
	}

	public void setYtpoints(int[] ytpoints) {
		this.ytpoints = ytpoints;
	}

	public int getIndex1() {
		return index1;
	}

	public void setIndex1(int index1) {
		this.index1 = index1;
	}
	
}