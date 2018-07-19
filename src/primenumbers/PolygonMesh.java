package primenumbers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PolygonMesh implements Cloneable {

	public Point3D[] points=null;
	public ArrayList<LineData>polygonData=null;
	public ArrayList <DPoint3D>normals=null;
	public int[] boxFaces=null;
	/**Cache for light polygon projections**/
	public static HashMap<Integer, Polygon3D> cachedPolygons=new HashMap<Integer, Polygon3D>();
	public static HashMap<Integer, Polygon3D> cachedShotPolygons=new HashMap<Integer, Polygon3D>();
	public static HashMap<Integer, Polygon3D> cachedBlockingPolygons=new HashMap<Integer, Polygon3D>();
	public static HashMap<Integer, Polygon3D> cachedZLevelPolygons=new HashMap<Integer, Polygon3D>();

	private String description="";

	public PolygonMesh() {
		points= null;
		polygonData= new ArrayList<LineData>();
		normals= new ArrayList <DPoint3D>();
	}

	PolygonMesh(Point3D[] points, ArrayList<LineData> polygonData) {
		this.points=points;
		this.polygonData=polygonData;
		calculateNormals();
	}

	public PolygonMesh(List <Point3D> points, ArrayList <LineData> polygonData) {

		if(points!=null){
			this.points=new Point3D[points.size()];
			for(int i=0;i<points.size();i++) {
				this.points[i] = points.get(i);
			}
		} else {
			this.points= null;
		}

		if(polygonData!=null) {
			this.polygonData = polygonData;
		} else {
			this.polygonData= new ArrayList <LineData>();
		}
		calculateNormals();
	}

	private void calculateNormals() {

		normals=new ArrayList<DPoint3D>();
		boxFaces=new int[polygonData.size()];
		for(int l=0;l<polygonData.size();l++){

			LineData ld=polygonData.get(l);

			DPoint3D normal = getNormal(0,ld,points);
			normals.add(normal);

			//if(ld.getData()!=null){

			boxFaces[l]=Integer.parseInt(ld.getData());
			/*}
			else
				boxFaces[l]=Renderer3D.findBoxFace(normal);*/
		}
	}

	private static DPoint3D getNormal(int position, LineData ld,
			Point3D[] points) {

		int n=ld.size();

		Point3D p0=points[ld.getIndex((n+position-1)%n)];
		Point3D p1=points[ld.getIndex(position)];
		Point3D p2=points[ld.getIndex((1+position)%n)];

		DPoint3D normal=DPoint3D.calculateCrossProduct(p1.substract(p0),p2.substract(p1));
		return normal.calculateVersor();
	}

	void addPolygonData(LineData polygon){
		polygonData.add(polygon);
	}
	@Override
	public PolygonMesh clone() {

		PolygonMesh pm=new PolygonMesh();
		pm.points=new Point3D[this.points.length];

		for(int i=0;i<this.points.length;i++){

			pm.points[i]=points[i].clone();

		}

		for(int i=0;i<this.polygonData.size();i++){

			pm.addPolygonData(polygonData.get(i).clone());
		}
		for(int i=0;i<this.normals.size();i++){

			pm.normals.add(normals.get(i).clone());
		}

		return pm;
	}

	public static Polygon3D getBodyPolygon(Point3D[] points,LineData ld){
		return getBodyPolygon(points,ld,null);
	}

	public static Polygon3D getBodyPolygon(Point3D[] points,LineData ld,HashMap<Integer, Polygon3D> polygonsMap) {

		int size=ld.size();
		Polygon3D pol=null;

		if(polygonsMap!=null){
			pol=getCachedPolygon(size,polygonsMap);
		}else{
			pol=new Polygon3D(size);
		}

		for(int i=0;i<size;i++){
			int index=ld.getIndex(i);

			pol.xpoints[i]=(int) points[index].x;
			pol.ypoints[i]=(int) points[index].y;
			pol.zpoints[i]=(int) points[index].z;

			DPoint2D pt = ld.getVertexTexturePoint(i);

			pol.xtpoints[i]=(int) pt.x;
			pol.ytpoints[i]=(int) pt.y;
		}

		pol.setHexColor(ld.getHexColor());
		pol.setIndex(ld.getTexture_index());
		pol.setIndex1(ld.getDecal_index());
		return pol;

	}

	private static Polygon3D getCachedPolygon(int size, HashMap<Integer, Polygon3D> polygonsMap) {
		Polygon3D poli=polygonsMap.get(size);
		if(poli==null){
			poli=new Polygon3D(size);
			polygonsMap.put(size, poli);
		}else{
			poli.invalidate();
		}
		return poli;
	}

	public static void buildPoint(List <Point3D> aPoints,String str) {

		String[] vals =str.split(" ");

		Point3D p=new Point3D();
		p.x=Double.parseDouble(vals[0]);
		p.y=Double.parseDouble(vals[1]);
		p.z=Double.parseDouble(vals[2]);

		if(vals.length==4) {
			p.data=vals[3];
		}

		aPoints.add(p);
	}

	private static void buildLine(ArrayList<LineData> polygonData, String token, ArrayList<Point3D> vTexturePoints) {

		LineData ld=new LineData();

		if(token.indexOf("]")>0){

			String extraData=token.substring(token.indexOf("[")+1,token.indexOf("]"));
			token=token.substring(token.indexOf("]")+1);
			ld.setData(extraData);
		}

		String[] vals = token.split(" ");
		for(int i=0;i<vals.length;i++){

			String val=vals[i];
			if(val.indexOf("/")>0){

				String val0=val.substring(0,val.indexOf("/"));
				String val1=val.substring(1+val.indexOf("/"));

				int indx0=Integer.parseInt(val0);
				int indx1=Integer.parseInt(val1);
				Point3D pt=vTexturePoints.get(indx1);

				ld.addIndex(indx0,indx1,pt.x,pt.y);
			} else {
				ld.addIndex(Integer.parseInt(val));
			}
		}

		polygonData.add(ld);
	}

	public static void buildTexturePoint(ArrayList<Point3D> vTexturePoints,String str) {

		String[] vals=str.split(" ");

		double x=Double.parseDouble(vals[0]);
		double y=Double.parseDouble(vals[1]);
		Point3D p=new Point3D(x,y,0);

		vTexturePoints.add(p);
	}

	public void translate(double i, double j, double k) {

		for(int p=0;p<points.length;p++){
			points[p].translate(i,j,k);
		}
	}

	public static PolygonMesh loadMeshFromFile(File file) {

		ArrayList <Point3D> points=new ArrayList <Point3D>();
		ArrayList<LineData> lines=new ArrayList<LineData>();
		ArrayList<Point3D>  vTexturePoints=new ArrayList<Point3D>();
		String description="";

		PolygonMesh pm=null;
		BufferedReader br=null;

		try {
			br=new BufferedReader(new FileReader(file));
			String str=null;
			int rows=0;
			while((str=br.readLine())!=null){
				if(str.indexOf("#")>=0 || str.length()==0) {
					continue;
				}

				if(str.startsWith("v=")) {
					buildPoint(points,str.substring(2));
				} else if(str.startsWith("vt=")) {
					PolygonMesh.buildTexturePoint(vTexturePoints,str.substring(3));
				} else if(str.startsWith("f=")) {
					PolygonMesh.buildLine(lines,str.substring(2),vTexturePoints);
				}
				else if(str.startsWith("DESCRIPTION=")) {
					description=str.substring(str.indexOf("=")+1);
				}

			}
			//checkNormals();
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		pm=new PolygonMesh(points,lines);
		pm.setDescription(description);
		return pm;
	}

	/**
	 *
	 * Function to test, not working!
	 * @param pm
	 */
	public static void dividePolygonsInTriangles(PolygonMesh pm) {

		ArrayList<LineData> oldPolygonData=pm.polygonData;
		ArrayList<LineData> newPolygonData=new ArrayList<LineData> ();

		int psz=oldPolygonData.size();

		for (int index = 0; index < psz; index++) {

			LineData oldLineData=oldPolygonData.get(index);

			int osz=oldLineData.size();
			for(int i=1;i<osz-1;i++){

				LineData newLineData= oldLineData.clone();
				newLineData.resetIndexes();

				DPoint2D pt0=oldLineData.getVertexTexturePoint(0);
				newLineData.addIndex(oldLineData.getIndex(0),
						oldLineData.getVertex_texture_index(0),
						pt0.x,pt0.y);

				DPoint2D pt1=oldLineData.getVertexTexturePoint(i);
				newLineData.addIndex(oldLineData.getIndex(i),
						oldLineData.getVertex_texture_index(i),
						pt1.x,pt1.y);

				DPoint2D pt2=oldLineData.getVertexTexturePoint(i+1);
				newLineData.addIndex(oldLineData.getIndex(i+1),
						oldLineData.getVertex_texture_index(i+1),
						pt2.x,pt2.y);

				newPolygonData.add(newLineData);
			}

		}

		pm.setPolygonData(newPolygonData);
	}

	public ArrayList<LineData> getPolygonData() {
		return polygonData;
	}

	public void setPolygonData(ArrayList<LineData> polygonData) {
		this.polygonData = polygonData;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
}