package primenumbers;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import primenumbers.main.Game;

public class DrawObject {

	public static boolean IS_3D=true;

	public double x;
	public double y;
	public double z;

	public double dx;
	public double dy;
	public double dz;

	private int index=0;
	private String hexColor="0000AA";
	private boolean selected=false;

	public ArrayList<Polygon3D> polygons=new ArrayList<Polygon3D>();
	public PolygonMesh[] meshes=null;
	public int active_mesh=0;

	public static int MESHES_SIZE=12;

	private int deltaX=0;
	private int deltaY=0;
	private int deltaX2=0;

	private boolean visible=true;

	private double rotation_angle=0;

	public static final int MONSTER0_INDEX_0=5;
	public static final int MONSTER0_INDEX_1=6;
	public static final int MONSTER0_INDEX_2=30;
	public static final int AMMO_INDEX=18;
	public static final int FIRST_AID_KIT_INDEX=19;

	private Polygon3D border=null;

	@Override
	public Object clone()  {
		DrawObject dro=new DrawObject();

		dro.setX(getX());
		dro.setY(getY());
		dro.setZ(getZ());
		dro.setDx(getDx());
		dro.setDy(getDy());
		dro.setDz(getDz());
		dro.setIndex(getIndex());
		dro.setHexColor(getHexColor());

		for(int i=0;i<polygons.size();i++){

			Polygon3D polig=polygons.get(i);
			dro.addPolygon(polig.clone());
		}
		dro.setMesh(getMesh().clone(),0);
		dro.setRotation_angle(rotation_angle);

		return dro;
	}

	public void setDimensionFromCubicMesh(){

		CubicMesh cm=(CubicMesh) getMesh();
		dx=cm.getDeltaX2()-cm.getDeltaX();
		dy=cm.getDeltaY2()-cm.getDeltaY();
		dz=cm.getDeltaY();


	}

	public void addPolygon(Polygon3D poly){

		polygons.add(poly);
	}

	public ArrayList<Polygon3D> getPolygons() {
		return polygons;
	}

	public void setPolygons(ArrayList<Polygon3D> polygons) {
		this.polygons = polygons;
	}

	public void buildPolygons() {

		DPoint3D p000=new DPoint3D(x,y,z);
		DPoint3D p001=new DPoint3D(x,y,z+dz);
		DPoint3D p010=new DPoint3D(x,y+dy,z);
		DPoint3D p100=new DPoint3D(x+dx,y,z);
		DPoint3D p110=new DPoint3D(x+dx,y+dy,z);
		DPoint3D p011=new DPoint3D(x,y+dy,z+dz);
		DPoint3D p101=new DPoint3D(x+dx,y,z+dz);
		DPoint3D p111=new DPoint3D(x+dx,y+dy,z+dz);

		polygons=new ArrayList<Polygon3D>();

		DPoint3D[][] faces={
				{p000,p010,p110,p100},
				{p001,p101,p111,p011},
				{p000,p001,p011,p010},
				{p100,p110,p111,p101},
				{p000,p100,p101,p001},
				{p010,p011,p111,p110},
		};

		for (int i = 0; i < faces.length; i++) {


			Polygon3D p=new Polygon3D();

			for (int j = 0; j < faces[i].length; j++) {
				p.addPoint(faces[i][j]);
			}
			p.setHexColor(getHexColor());
			polygons.add(p);
		}
	}

	public Polygon3D getBase() {

		CubicMesh cm=(CubicMesh) getMesh();
		Polygon3D p3D=new Polygon3D();
		p3D.addPoint(cm.point000);
		p3D.addPoint(cm.point100);
		p3D.addPoint(cm.point110);
		p3D.addPoint(cm.point010);
		return p3D;
	}

	private static BufferedImage fromImageToBufferedImage(Image image,Color color){

		image=Transparency.makeColorTransparent(image, color);

		int width=image.getWidth(null);
		int height=image.getHeight(null);

		BufferedImage bufImage=
				new BufferedImage(width,height,
						BufferedImage.TYPE_INT_ARGB);

		Graphics2D g2 = (Graphics2D) bufImage.getGraphics();

		g2.setColor(new Color(255,255,255,0));
		g2.fillRect(0,0,width,height);

		g2.drawImage(image,0,0,
				width,height,null);


		return bufImage;
	}

	public static Texture fromImageToTexture(Image image,Color color){

		BufferedImage bi=fromImageToBufferedImage(image, color);
		return new Texture(bi);
	}

	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	/**
	 * @return the dx
	 */
	public double getDx() {
		return dx;
	}
	/**
	 * @param dx the dx to set
	 */
	public void setDx(double dx) {
		this.dx = dx;
	}
	/**
	 * @return the dy
	 */
	public double getDy() {
		return dy;
	}
	/**
	 * @param dy the dy to set
	 */
	public void setDy(double dy) {
		this.dy = dy;
	}
	/**
	 * @return the dz
	 */
	public double getDz() {
		return dz;
	}
	/**
	 * @param dz the dz to set
	 */
	public void setDz(double dz) {
		this.dz = dz;
	}
	/**
	 * @return the x
	 */
	public double getX() {
		return x;
	}
	/**
	 * @param x the x to set
	 */
	public void setX(double x) {
		this.x = x;
	}
	/**
	 * @return the y
	 */
	public double getY() {
		return y;
	}
	/**
	 * @param y the y to set
	 */
	public void setY(double y) {
		this.y = y;
	}
	/**
	 * @return the z
	 */
	public double getZ() {
		return z;
	}
	/**
	 * @param z the z to set
	 */
	public void setZ(double z) {
		this.z = z;
	}

	public static DrawObject[] cloneObjectsArray(DrawObject[] drawObjects) {
		DrawObject[] newDrawObjects=new DrawObject[drawObjects.length];

		for(int i=0;i<drawObjects.length;i++){
			newDrawObjects[i] =(DrawObject) drawObjects[i].clone();
		}

		return newDrawObjects;
	}


	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
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

	public double getRotation_angle() {
		return rotation_angle;
	}

	public void setRotation_angle(double rotation_angle) {
		this.rotation_angle = rotation_angle;
	}

	public PolygonMesh getMesh() {

		if(meshes==null) {
			return null;
		}
		return meshes[active_mesh];
	}

	public void setMesh(PolygonMesh mesh,int index) {

		if(meshes==null) {
			meshes=new PolygonMesh[MESHES_SIZE];
		}
		this.meshes[index] = mesh;
		if(index==0) {
			calculateBorder();
		}
	}

	public PolygonMesh[] getMeshes() {
		return meshes;
	}

	public void setMeshes(PolygonMesh[] meshes) {
		this.meshes = meshes;
	}

	public void update(Game game, boolean animate) {

		if(!visible){
			return;
		}

		if(animate) {
			updateActiveMesh();
		}
	}

	void updateActiveMesh(){

		if(active_mesh+1==MESHES_SIZE || meshes[active_mesh+1]==null){

			active_mesh=0;
			return;
		}

		active_mesh+=1;
	}

	public double getMaxZ(double zLevel) {
		return z+dz;
	}

	public Polygon3D getBorder() {
		return border;
	}

	public void setBorder(Polygon3D border) {
		this.border = border;
	}

	protected void calculateBorder() {

		if(meshes[0]==null || meshes[0].points==null) {
			return;
		}

		double maxX=0;
		double minX=0;
		double maxY=0;
		double minY=0;

		boolean start=true;

		for(int i=0;i<meshes[0].points.length;i++){

			Point3D point= meshes[0].points[i];

			if(point.z-z>1) {
				continue;
			}

			if(start){

				maxX=point.x;
				minX=point.x;
				maxY=point.y;
				minY=point.y;

				start=false;
				continue;
			}

			maxX=Math.max(point.x,maxX);
			minX=Math.min(point.x,minX);
			maxY=Math.max(point.y,maxY);
			minY=Math.min(point.y,minY);

		}

		double dx=maxX-minX;
		double dy=maxY-minY;


		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];

		cx[0]=(int) minX;
		cy[0]=(int) minY;
		cz[0]=(int) z;
		cx[1]=(int) (minX+dx);
		cy[1]=(int) minY;
		cz[1]=(int) z;
		cx[2]=(int) (minX+dx);
		cy[2]=(int) (minY+dy);
		cz[2]=(int) z;
		cx[3]=(int) minX;
		cy[3]=(int) (minY+dy);
		cz[3]=(int) z;

		border=new Polygon3D(4,cx,cy,cz);

		DPoint3D center=Polygon3D.findCentroid(border);
		Polygon3D.rotate(border,center,rotation_angle);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public static void recenterMeshes(CubicMesh[] cubicMeshes) {

		double maxLenDX=0;
		double minLenDX=0;
		double maxLenDY=0;
		double minLenDY=0;
		double maxLenDZ=0;
		double minLenDZ=0;

		for(int k=0;k<DrawObject.MESHES_SIZE;k++){
			if(cubicMeshes[k]==null) {
				break;
			}
			double lDX=cubicMeshes[k].getLenDX();
			double lDY=cubicMeshes[k].getLenDY();
			double lDZ=cubicMeshes[k].getLenDZ();
			if(k==0){
				maxLenDX=lDX;
				minLenDX=lDX;
				maxLenDY=lDY;
				minLenDY=lDY;
				maxLenDZ=lDZ;
				minLenDZ=lDZ;
			}

			if(lDX<minLenDX) {
				minLenDX=lDX;
			}
			else if(lDX>maxLenDX) {
				maxLenDX=lDX;
			}

			if(lDY<minLenDY) {
				minLenDY=lDY;
			}
			else if(lDY>maxLenDY) {
				maxLenDY=lDY;
			}

			if(lDZ<minLenDZ) {
				minLenDZ=lDZ;
			}
			else if(lDZ>maxLenDZ) {
				maxLenDZ=lDZ;
			}

		}
		for(int k=0;k<DrawObject.MESHES_SIZE;k++){
			if(cubicMeshes[k]==null) {
				break;
			}
			double lDX=cubicMeshes[k].getLenDX();
			double lDY=cubicMeshes[k].getLenDY();
			double lDZ=cubicMeshes[k].getLenDZ();
			cubicMeshes[k].translate(
					(maxLenDX-lDX)*0.5,
					(maxLenDY-lDY)*0.5,
					(maxLenDZ-lDZ)*0.5
					);
		}



	}

	public static boolean isMonster(int index2) {
		return index2==DrawObject.MONSTER0_INDEX_0 || index2==DrawObject.MONSTER0_INDEX_1  || index2==DrawObject.MONSTER0_INDEX_2;
	}
	
}