package primenumbers;

import java.io.File;
import java.util.ArrayList;

public class CubicMesh extends PolygonMesh {

	private int deltaX=0;
	private int deltaY=0;
	private int deltaY2=0;
	private int deltaX2=0;

	public DPoint3D point000=null;
	public DPoint3D point010=null;
	public DPoint3D point001=null;
	public DPoint3D point100=null;
	public DPoint3D point011=null;
	public DPoint3D point110=null;
	public DPoint3D point101=null;
	public DPoint3D point111=null;

	private double lenDX=0;
	private double lenDY=0;
	private double lenDZ=0;

	public CubicMesh(Point3D[] points, ArrayList<LineData> polygonData) {
		super(points,polygonData);
	}

	public CubicMesh() {
		//NOTHING TO DO
	}

	public static CubicMesh loadMeshFromFile(File file) {

		PolygonMesh pm= PolygonMesh.loadMeshFromFile(file);

		CubicMesh cm=buildCubicMesh(pm);
		return cm;
	}

	public static CubicMesh buildCubicMesh(PolygonMesh pm) {

		CubicMesh cm=new CubicMesh(pm.points,pm.polygonData);

		double dx=0;
		double dy=0;
		double dz=0;

		double minx=0;
		double miny=0;
		double minz=0;

		double maxx=0;
		double maxy=0;
		double maxz=0;

		for(int j=0;j<cm.points.length;j++){

			Point3D point= cm.points[j];

			if(j==0){

				minx=point.x;
				miny=point.y;
				minz=point.z;

				maxx=point.x;
				maxy=point.y;
				maxz=point.z;
			}
			else{

				maxx=(int)Math.max(point.x,maxx);
				maxz=(int)Math.max(point.z,maxz);
				maxy=(int)Math.max(point.y,maxy);

				minx=(int)Math.min(point.x,minx);
				minz=(int)Math.min(point.z,minz);
				miny=(int)Math.min(point.y,miny);
			}
		}


		dx=maxx-minx;
		dy=maxy-miny;
		dz=maxz-minz;

		cm.setLenDX(dx);
		cm.setLenDY(dy);
		cm.setLenDZ(dz);

		cm.setDeltaX((int) dz+1);
		cm.setDeltaX2((int) (dx+dz)+1);
		cm.setDeltaY((int) dz+1);
		cm.setDeltaY2((int) (dy+dz)+1);

		cm.point000=new DPoint3D(0,0,0);
		cm.point100=new DPoint3D(dx,0,0);
		cm.point010=new DPoint3D(0,dy,0);
		cm.point001=new DPoint3D(0,0,dz);
		cm.point110=new DPoint3D(dx,dy,0);
		cm.point011=new DPoint3D(0,dy,dz);
		cm.point101=new DPoint3D(dx,0,dz);
		cm.point111=new DPoint3D(dx,dy,dz);

		//only positive data in the cubic mesh

		for (int i = 0; i < cm.points.length; i++) {
			cm.points[i].x-=minx;
			cm.points[i].y-=miny;
			cm.points[i].z-=minz;
		}


		cm.setDescription(pm.getDescription());

		return cm;

	}
	@Override
	public CubicMesh clone() {

		CubicMesh cm=new CubicMesh();
		cm.points=new Point3D[this.points.length];
		cm.boxFaces=new int[this.boxFaces.length];

		for(int i=0;i<this.points.length;i++){

			cm.points[i]=points[i].clone();

		}

		for(int i=0;i<this.polygonData.size();i++){

			cm.addPolygonData(polygonData.get(i).clone());
		}
		for(int i=0;i<this.normals.size();i++){

			cm.normals.add(normals.get(i).clone());
		}
		for(int i=0;i<this.boxFaces.length;i++){
			cm.boxFaces[i]=this.boxFaces[i];
		}



		cm.setDeltaX(getDeltaX());
		cm.setDeltaX2(getDeltaX2());
		cm.setDeltaY(getDeltaY());
		cm.setDeltaY2(getDeltaY2());

		cm.setLenDX(lenDX);
		cm.setLenDY(lenDY);
		cm.setLenDZ(lenDZ);

		cm.point000=point000.clone();
		cm.point100=point100.clone();
		cm.point010=point010.clone();
		cm.point001=point001.clone();
		cm.point110=point110.clone();
		cm.point011=point011.clone();
		cm.point101=point101.clone();
		cm.point111=point111.clone();

		return cm;
	}
	@Override
	public void translate(double i, double j, double k) {


		super.translate(i,j,k);
		point000.translate(i,j,k);
		point100.translate(i,j,k);
		point010.translate(i,j,k);
		point001.translate(i,j,k);
		point110.translate(i,j,k);
		point011.translate(i,j,k);
		point101.translate(i,j,k);
		point111.translate(i,j,k);
	}

	public DPoint3D getXAxis(){
		return point100.substract(point000).calculateVersor();
	}

	public DPoint3D getYAxis(){
		return point010.substract(point000).calculateVersor();
	}

	public DPoint3D getZAxis(){
		return point001.substract(point000).calculateVersor();
	}

	public int getDeltaX() {
		return deltaX;
	}

	public void setDeltaX(int deltaX) {
		this.deltaX = deltaX;
	}

	public int getDeltaY() {
		return deltaY;
	}

	public void setDeltaY(int deltaY) {
		this.deltaY = deltaY;
	}

	public int getDeltaX2() {
		return deltaX2;
	}

	public void setDeltaX2(int deltaX2) {
		this.deltaX2 = deltaX2;
	}

	public int getDeltaY2() {
		return deltaY2;
	}

	public void setDeltaY2(int deltaY2) {
		this.deltaY2 = deltaY2;
	}

	public void rotate(double x0, double y0,double cos, double sin ) {

		for (int i = 0; i < points.length; i++) {

			Point3D p=points[i];
			p.rotate(x0,  y0, cos, sin );
		}

		point000.rotate(x0,  y0, cos, sin );
		point100.rotate(x0,  y0, cos, sin );
		point010.rotate(x0,  y0, cos, sin );
		point001.rotate(x0,  y0, cos, sin );
		point110.rotate(x0,  y0, cos, sin );
		point011.rotate(x0,  y0, cos, sin );
		point101.rotate(x0,  y0, cos, sin );
		point111.rotate(x0,  y0, cos, sin );
	}


	public void rotate(double x0, double y0,double z0,
			double viewDirectionCos, double viewDirectionSin,
			double sightLatDirectionCos, double sightLatDirectionSin) {

		for (int i = 0; i < points.length; i++) {

			Point3D p=points[i];
			p.rotate(x0,  y0, z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		}

		point000.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		point100.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		point010.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		point001.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		point110.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		point011.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		point101.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		point111.rotate(x0,y0,z0, viewDirectionCos, viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);

	}

	public DPoint3D findCentroid() {

		double x=(point000.x+point100.x+point010.x+point110.x)*0.25;
		double y=(point000.y+point100.y+point010.y+point110.y)*0.25;
		return new DPoint3D(x,y,0);
	}

	/**
	 * Rotate CubicMesh of cos, sin around y axis (x0,z0)
	 *
	 * @param x0
	 * @param z0
	 * @param cos
	 * @param sin
	 */
	public void rotateX(double y0, double z0,double cos, double sin ) {

		for (int i = 0; i < points.length; i++) {


			double xx=points[i].x;
			double yy=points[i].y;
			double zz=points[i].z;

			points[i].x=xx;
			points[i].y=y0+(yy-y0)*cos-(zz-z0)*sin;
			points[i].z=z0+(zz-z0)*cos+(yy-y0)*sin;
		}

		point000.rotateX(y0,  z0, cos, sin );
		point100.rotateX(y0,  z0, cos, sin );
		point010.rotateX(y0,  z0, cos, sin );
		point001.rotateX(y0,  z0, cos, sin );
		point110.rotateX(y0,  z0, cos, sin );
		point011.rotateX(y0,  z0, cos, sin );
		point101.rotateX(y0,  z0, cos, sin );
		point111.rotateX(y0,  z0, cos, sin );


	}

	public double getLenDX() {
		return lenDX;
	}

	public void setLenDX(double lenDX) {
		this.lenDX = lenDX;
	}

	public double getLenDY() {
		return lenDY;
	}

	public void setLenDY(double lenDY) {
		this.lenDY = lenDY;
	}

	public double getLenDZ() {
		return lenDZ;
	}

	public void setLenDZ(double lenDZ) {
		this.lenDZ = lenDZ;
	}
	
}