package primenumbers.editors.scene.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import primenumbers.BarycentricCoordinates;
import primenumbers.CubicMesh;
import primenumbers.DPoint2D;
import primenumbers.DPoint3D;
import primenumbers.DrawObject;
import primenumbers.LineData;
import primenumbers.Point3D;
import primenumbers.Polygon3D;
import primenumbers.PolygonMesh;
import primenumbers.Texture;
import primenumbers.ZBuffer;
import primenumbers.editors.EditorData;
import primenumbers.editors.scene.SceneEditor;
import primenumbers.main.Renderer3D;
import primenumbers.scene.Block;
import primenumbers.scene.Scene;

public class SceneEditorIsoPanel extends EditorPanel {

	private double alfa=Math.PI/3;
	private double sinAlfa=Math.sin(alfa);
	private double cosAlfa=Math.cos(alfa);
	private double fi=0;
	private double sinf=Math.sin(fi);
	private double cosf=Math.cos(fi);

	private static int y0=50;
	private static int x0=350;

	private static int POSX=0;
	private static int POSY=-100;

	private double deltay=10;
	private double deltax=10;

	private ZBuffer zBuffer=null;
	private int greenRgb= Color.GREEN.getRGB();
	private int[] rgb=null;

	private BufferedImage buf=null;

	private static Texture[] wallTextures;
	private static Texture[] worldTextures;
	private static Texture[] worldDecals;

	private int minMovement=5;

	private Point3D projectionNormal=new Point3D(-1/Math.sqrt(3),-1/Math.sqrt(3),1/Math.sqrt(3));

	public SceneEditorIsoPanel(SceneEditor editor, int cENTER_WIDTH,int cENTER_HEIGHT) {

		super(editor,cENTER_WIDTH, cENTER_HEIGHT);

		buf=new BufferedImage(CENTER_WIDTH,CENTER_HEIGHT,BufferedImage.TYPE_INT_RGB);
		zBuffer=new ZBuffer(cENTER_HEIGHT*cENTER_WIDTH);
		buildNewZBuffers();
		loadTextures();

		xMovement=2*minMovement;
		yMovement=2*minMovement;
	}

	private void buildScreen(BufferedImage buf) {

		int length=rgb.length;
		for(int i=0;i<length;i++){
			//set
			rgb[i]=zBuffer.getRgbColor(i);
			//clean
			zBuffer.set(0,0,0,greenRgb,true,i);
		}

		buf.getRaster().setDataElements( 0,0,CENTER_WIDTH,CENTER_HEIGHT,rgb);
		//buf.setRGB(0,0,WIDTH,HEIGHT,rgb,0,WIDTH);
	}

	private void buildNewZBuffers() {

		rgb=new int[zBuffer.getSize()];

		for(int i=0;i<zBuffer.getSize();i++){
			zBuffer.setRgbColor(greenRgb,i);
		}
	}

	@Override
	public void drawScene(Scene scene,int layer, Graphics graph) {

		draw(buf,scene,layer);
		buildScreen(buf);
		graph.drawImage(buf,0,0,this);
	}

	private void draw(BufferedImage buf2, Scene scene,int layer) {

		if(scene.map!=null){

			Block[] blocks = scene.map.blocks;

			for (int i = 0; i < blocks.length; i++) {

				Block bl = blocks[i];

				if(layer!=SceneEditor.ALL_LAYERS && bl.getLayer()!=layer) {
					continue;
				}

				Color color=ZBuffer.fromHexToColor(bl.getHexColor());

				ArrayList<LineData> lines=bl.getPolygons(false);

				for (int j = 0; j < lines.size(); j++) {

					LineData ld = lines.get(j);
					Polygon3D polReal= PolygonMesh.getBodyPolygon(bl.getMesh().points,ld);

					Texture image=null;
					Texture decal=null;

					int indexZ=0;
					try{
						indexZ=Integer.parseInt(ld.getData2());
					}catch (Exception e) {}

					if(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL
							|| bl.getBlock_type()==Block.BLOCK_TYPE_GROUND
							){

						image=worldTextures[ld.getTexture_index()];
						if(ld.getDecal_index()!=Block.NO_DECAL_INDEX){
							decal=worldDecals[ld.getDecal_index()];
						}
						if(bl.isSelected()){
							image=null;
							color=Color.CYAN;
						}
					}
					else{
						image=wallTextures[ld.getTexture_index()];
						if(bl.isSelected() && bl.getSelectedZ()==indexZ){
							image=null;
							color=Color.CYAN;
						}
					}
					decomposeClippedPolygonIntoZBuffer(polReal,color,image,decal,zBuffer,null,null,null,0,0);
				}
			}
		}

		ArrayList<DrawObject> objs=scene.objects;

		if(!hide_objects) {
			for (int i = 0; i < objs.size(); i++) {
				DrawObject dro = objs.get(i);

				PolygonMesh mesh = dro.getMesh();

				Color col=ZBuffer.fromHexToColor(dro.getHexColor());

				if(dro.isSelected()) {
					col=Color.RED;
				}

				decomposeCubicMesh((CubicMesh) mesh,col,null,null,zBuffer);

				/*for (int j = 0; j < dro.polygons.size(); j++) {
					Polygon3D pol3D = ((Polygon3D) dro.polygons.elementAt(j)).clone();
					decomposeClippedPolygonIntoZBuffer(pol3D,col,null,zBuffer,null,null,null,0,0);
				}*/
			}
		}
	}

	private DPoint3D lightDirection=new DPoint3D(-Math.sqrt(2)/2,-Math.sqrt(2)/2,0);

	private double calculateCosin(Polygon3D polReal) {

		DPoint3D normal = Polygon3D.findNormal(polReal);

		double cosin=DPoint3D.calculateCosin(normal,lightDirection);
		return cosin;
	}
	@Override
	public int calcAssX(double x, double y, double z) {

		/*double xx=(cosf*(x-POSX)-sinf*(y-POSY));
		double yy=(cosf*(y-POSY)+sinf*(x-POSX));*/
		double xx=x-POSX;
		double yy=y-POSY;
		double zz=z;

		return (int) ((-yy*sinAlfa+xx*sinAlfa) /deltax+x0);
	}
	@Override
	public int calcAssY(double x, double y, double z) {

		double xx=x-POSX;
		double yy=y-POSY;
		double zz=z;

		return CENTER_HEIGHT-(int) ((z+yy*cosAlfa+xx*cosAlfa)/deltay+y0);
	}
	@Override
	public int calcAssInverseX(double x, double y, double z) {
		return  (int) ((x-x0) *deltax);
	}
	@Override
	public int calcAssInverseY(double x, double y, double z) {
		return (int) ((CENTER_HEIGHT-(int) (y+y0))*deltay);
	}
	@Override
	public Polygon3D builProjectedPolygon(LineData ld, Point3D[] points) {

		Polygon3D polReal= PolygonMesh.getBodyPolygon(points,ld);
		return builProjectedPolygon(polReal);
	}
	@Override
	public Polygon3D builProjectedPolygon(Polygon3D p3d) {

		Polygon3D pol=new Polygon3D();

		int size=p3d.npoints;

		for(int i=0;i<size;i++){

			double x=p3d.xpoints[i];
			double y=p3d.ypoints[i];
			double z=p3d.zpoints[i];

			DPoint3D p=new DPoint3D(x,y,z);

			p.rotate(POSX,POSY,cosf,sinf);

			int xx=calcAssX(p.x,p.y,p.z);
			int yy=calcAssY(p.x,p.y,p.z);

			pol.addPoint(xx,yy);
		}
		return pol;
	}

	private void decomposeClippedPolygonIntoZBuffer(Polygon3D p3d,Color color,Texture texture,Texture decal,ZBuffer zbuffer,
			DPoint3D xDirection,DPoint3D yDirection,DPoint3D origin,int deltaX,int deltaY){

		DPoint3D normal=Polygon3D.findNormal(p3d);

		if(texture!=null && xDirection==null && yDirection==null){

			DPoint3D p0=new DPoint3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0]);
			DPoint3D p1=new DPoint3D(p3d.xpoints[1],p3d.ypoints[1],p3d.zpoints[1]);
			xDirection=(p1.substract(p0)).calculateVersor();

			yDirection=DPoint3D.calculateCrossProduct(normal,xDirection).calculateVersor();

			//yDirection=Point3D.calculateOrthogonal(xDirection);
		}

		Polygon3D[] triangles = Polygon3D.divideIntoTriangles(p3d);

		for(int i=0;i<triangles.length;i++){

			BarycentricCoordinates bc=new BarycentricCoordinates(triangles[i]);

			Polygon3D[] clippedTriangles = Polygon3D.divideIntoTriangles(triangles[i]);

			for (int j = 0; j < clippedTriangles.length; j++) {
				decomposeTriangleIntoZBufferEdgeWalking( clippedTriangles[j],color, texture,decal,zbuffer,
						xDirection,yDirection,origin, deltaX, deltaY,bc);
			}
		}
	}


	/**
	 *
	 * DECOMPOSE PROJECTED TRIANGLE USING EDGE WALKING AND
	 * PERSPECTIVE CORRECT MAPPING
	 *
	 * @param p3d
	 * @param color
	 * @param texture
	 * @param useLowResolution
	 * @param xDirection
	 * @param yDirection
	 * @param origin
	 */
	private void decomposeTriangleIntoZBufferEdgeWalking(Polygon3D p3d,Color color,Texture texture,Texture decal,ZBuffer zb,
			DPoint3D xDirection, DPoint3D yDirection, DPoint3D origin,int deltaX,int deltaY,
			BarycentricCoordinates bc) {

		//create variables for Edge Walking calculus
		Point3D[] points=new Point3D[3];
		Point3D pstart = new Point3D();
		Point3D pend = new Point3D();
		double l=0;
		double xi =0;
		double yi =0;
		double zi =0;
		double texture_x =0;
		double texture_y =0;

		int rgbColor=color.getRGB();

		DPoint3D normal=Polygon3D.findNormal(p3d).calculateVersor();

		//boolean isFacing=isFacing(p3d,normal,observerPoint);

		double cosin=calculateCosin(p3d);

		Point3D po0=new Point3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0]);
		Point3D po1=new Point3D(p3d.xpoints[1],p3d.ypoints[1],p3d.zpoints[1]);
		Point3D po2=new Point3D(p3d.xpoints[2],p3d.ypoints[2],p3d.zpoints[2]);

		Point3D p0=new Point3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0]);
		Point3D p1=new Point3D(p3d.xpoints[1],p3d.ypoints[1],p3d.zpoints[1]);
		Point3D p2=new Point3D(p3d.xpoints[2],p3d.ypoints[2],p3d.zpoints[2]);

		p0.rotate(POSX,POSY,cosf,sinf);
		p1.rotate(POSX,POSY,cosf,sinf);
		p2.rotate(POSX,POSY,cosf,sinf);

		double x0=calcAssX(p0.x,p0.y,p0.z);
		double y0=calcAssY(p0.x,p0.y,p0.z);
		double z0=p0.y;

		double x1=calcAssX(p1.x,p1.y,p1.z);
		double y1=calcAssY(p1.x,p1.y,p1.z);
		double z1=p1.y;

		double x2=calcAssX(p2.x,p2.y,p2.z);
		double y2=calcAssY(p2.x,p2.y,p2.z);
		double z2=p2.y;

		//check if triangle is visible
		double maxX=Math.max(x0,x1);
		maxX=Math.max(x2,maxX);
		double maxY=Math.max(y0,y1);
		maxY=Math.max(y2,maxY);
		double minX=Math.min(x0,x1);
		minX=Math.min(x2,minX);
		double minY=Math.min(y0,y1);
		minY=Math.min(y2,minY);

		if(maxX<0 || minX>CENTER_WIDTH || maxY<0 || minY>CENTER_HEIGHT) {
			return;
		}

		points[0]=new Point3D(x0,y0,z0,p0.x,p0.y,p0.z);
		points[1]=new Point3D(x1,y1,z1,p1.x,p1.y,p1.z);
		points[2]=new Point3D(x2,y2,z2,p2.x,p2.y,p2.z);

		int mip_map_level = 0;

		if(texture!=null){

			mip_map_level = (int) (0.5
					* Math.log(
							bc.getRealTriangleArea() / BarycentricCoordinates.getTriangleArea(x0, y0,0, x1, y1,0, x2, y2,0))
					/ Math.log(2));

			int w=texture.getWidth();
			int h=texture.getHeight();

			DPoint2D pt0=bc.pt0;
			DPoint2D pt1=bc.pt1;
			DPoint2D pt2=bc.pt2;

			DPoint3D p=bc.getBarycentricCoordinates(po0.x,po0.y,po0.z);
			double x= (p.x*(pt0.x)+p.y*pt1.x+(1-p.x-p.y)*pt2.x);
			double y= (p.x*(pt0.y)+p.y*pt1.y+(1-p.x-p.y)*pt2.y);
			points[0].setTexurePositions(x,texture.getHeight()-y);

			p=bc.getBarycentricCoordinates(po1.x,po1.y,po1.z);
			x= (p.x*(pt0.x)+p.y*pt1.x+(1-p.x-p.y)*pt2.x);
			y= (p.x*(pt0.y)+p.y*pt1.y+(1-p.x-p.y)*pt2.y);
			points[1].setTexurePositions(x,texture.getHeight()-y);

			p=bc.getBarycentricCoordinates(po2.x,po2.y,po2.z);
			x= (p.x*(pt0.x)+p.y*pt1.x+(1-p.x-p.y)*pt2.x);
			y= (p.x*(pt0.y)+p.y*pt1.y+(1-p.x-p.y)*pt2.y);
			points[2].setTexurePositions(x,texture.getHeight()-y);

			/*
			points[0].setTexurePositions(ZBuffer.pickTexturePositionPCoordinates(texture,points[0].p_x,points[0].p_y,points[0].p_z,xDirection,yDirection,origin,deltaX,deltaY));
			points[1].setTexurePositions(ZBuffer.pickTexturePositionPCoordinates(texture,points[1].p_x,points[1].p_y,points[1].p_z,xDirection,yDirection,origin,deltaX,deltaY));
			points[2].setTexurePositions(ZBuffer.pickTexturePositionPCoordinates(texture,points[2].p_x,points[2].p_y,points[2].p_z,xDirection,yDirection,origin,deltaX,deltaY));
			 */
		}

		int upper=0;
		int middle=1;
		int lower=2;

		for(int i=0;i<3;i++){

			if(points[i].y>points[upper].y) {
				upper=i;
			}

			if(points[i].y<points[lower].y) {
				lower=i;
			}

		}
		for(int i=0;i<3;i++){
			if(i!=upper && i!=lower) {
				middle=i;
			}
		}
		//double i_depth=1.0/zs;
		//UPPER TRIANGLE

		Point3D lowP=points[lower];
		Point3D upP=points[upper];
		Point3D midP=points[middle];

		int j0=midP.y>0?(int)midP.y:0;
		int j1=upP.y<CENTER_HEIGHT?(int)upP.y:CENTER_HEIGHT;

		for(int j=j0;j<j1;j++){

			double middlex=Point3D.foundXIntersection(upP,lowP,j);
			Point3D intersects = foundPX_PY_PZ_TEXTURE_Intersection(upP,lowP,j);

			double middlex2=Point3D.foundXIntersection(upP,midP,j);
			Point3D intersecte = foundPX_PY_PZ_TEXTURE_Intersection(upP,midP,j);

			pstart=new Point3D(middlex,j,intersects.p_z,intersects.p_x,intersects.p_y,intersects.p_z,intersects.texture_x,intersects.texture_y);
			pend=new Point3D(middlex2,j,intersecte.p_z,intersecte.p_x,intersecte.p_y,intersecte.p_z,intersecte.texture_x,intersecte.texture_y);

			//pstart.p_y=pstart.p_x*projectionNormal.x+pstart.p_y*projectionNormal.y+pstart.p_z*projectionNormal.z;
			//pend.p_y=pend.p_x*projectionNormal.x+pend.p_y*projectionNormal.y+pend.p_z*projectionNormal.z;

			if(pstart.x>pend.x){
				Point3D swap= pend;
				pend= pstart;
				pstart=swap;
			}

			int start=(int)pstart.x;
			int end=(int)pend.x;

			double inverse=1.0/(end-start);

			int i0=start>0?start:0;

			for(int i=i0;i<end;i++){

				if(i>=CENTER_WIDTH) {
					break;
				}

				int tot=CENTER_WIDTH*j+i;

				l=(i-start)*inverse;

				yi=((1-l)*pstart.p_y+l*pend.p_y);

				if(!zb.isToUpdate(yi,tot)){
					continue;
				}

				zi=((1-l)*pstart.p_z+l*pend.p_z);
				xi=((1-l)*pstart.p_x+l*pend.p_x);

				texture_x=(1-l)*pstart.texture_x+l*pend.texture_x;
				texture_y=(1-l)*pstart.texture_y+l*pend.texture_y;

				if(texture!=null) {
					rgbColor=Texture.getRGBMip(texture,decal,(int)texture_x,(int) texture_y,mip_map_level);
				}
				//rgbColor=ZBuffer.pickRGBColorFromTexture(texture,xi,yi,zi,xDirection,yDirection,origin,deltaX, deltaY);
				if(rgbColor==greenRgb) {
					continue;
				}
				zb.set(xi,yi,zi,calculateShadowColor(xi,yi,zi,cosin,rgbColor),false,tot);
			}
		}
		//LOWER TRIANGLE
		j0=lowP.y>0?(int)lowP.y:0;
		j1=midP.y<CENTER_HEIGHT?(int)midP.y:CENTER_HEIGHT;

		for(int j=j0;j<j1;j++){

			double middlex=Point3D.foundXIntersection(upP,lowP,j);

			Point3D intersects = foundPX_PY_PZ_TEXTURE_Intersection(upP,lowP,j);

			double middlex2=Point3D.foundXIntersection(lowP,midP,j);

			Point3D insersecte = foundPX_PY_PZ_TEXTURE_Intersection(lowP,midP,j);

			pstart=new Point3D(middlex,j,intersects.p_z,intersects.p_x,intersects.p_y,intersects.p_z,intersects.texture_x,intersects.texture_y);
			pend=new Point3D(middlex2,j,insersecte.p_z,insersecte.p_x,insersecte.p_y,insersecte.p_z,insersecte.texture_x,insersecte.texture_y);

			//pstart.p_y=pstart.p_x*projectionNormal.x+pstart.p_y*projectionNormal.y+pstart.p_z*projectionNormal.z;
			//pend.p_y=pend.p_x*projectionNormal.x+pend.p_y*projectionNormal.y+pend.p_z*projectionNormal.z;

			if(pstart.x>pend.x){

				Point3D swap= pend;
				pend= pstart;
				pstart=swap;
			}

			int start=(int)pstart.x;
			int end=(int)pend.x;

			double inverse=1.0/(end-start);

			int i0=start>0?start:0;

			for(int i=i0;i<end;i++){

				if(i>=CENTER_WIDTH) {
					break;
				}

				int tot=CENTER_WIDTH*j+i;

				l=(i-start)*inverse;

				yi=((1-l)*pstart.p_y+l*pend.p_y);

				if(!zb.isToUpdate(yi,tot) ){
					continue;
				}

				zi=((1-l)*pstart.p_z+l*pend.p_z);
				xi=((1-l)*pstart.p_x+l*pend.p_x);

				texture_x=(1-l)*pstart.texture_x+l*pend.texture_x;
				texture_y=(1-l)*pstart.texture_y+l*pend.texture_y;

				if(texture!=null) {
					rgbColor=Texture.getRGBMip(texture,decal,(int)texture_x,(int) texture_y,mip_map_level);
				}
				if(rgbColor==greenRgb) {
					continue;
				}
				zb.set(xi,yi,zi,calculateShadowColor(xi,yi,zi,cosin,rgbColor),false,tot);
			}
		}
	}

	private int calculateShadowColor(double xi, double yi, double zi, double cosin, int argbs) {

		double factor=(1*(0.75+0.25*cosin));

		int alphas=0xff & (argbs>>24);
		int rs = 0xff & (argbs>>16);
		int gs = 0xff & (argbs >>8);
		int bs = 0xff & argbs;

		rs=(int) (factor*rs);
		gs=(int) (factor*gs);
		bs=(int) (factor*bs);

		return alphas <<24 | rs <<16 | gs <<8 | bs;
	}

	private Point3D foundPX_PY_PZ_TEXTURE_Intersection(Point3D pstart, Point3D pend,
			double y) {

		Point3D intersect=new Point3D();

		double l=(y-pstart.y)/(pend.y-pstart.y);

		intersect.p_x= (1-l)*pstart.p_x+l*pend.p_x;
		intersect.p_y=  (1-l)*pstart.p_y+l*pend.p_y;
		intersect.p_z=  (1-l)*pstart.p_z+l*pend.p_z;

		intersect.texture_x=  (1-l)*pstart.texture_x+l*pend.texture_x;
		intersect.texture_y=  (1-l)*pstart.texture_y+l*pend.texture_y;

		return intersect;
	}

	private void decomposeCubicMesh(CubicMesh cm,Color col, Texture texture,Texture decal,ZBuffer zBuffer){

		DPoint3D xDirection=null;
		DPoint3D yDirection=null;

		//versors to transform in the system:
		DPoint3D xVersor0=cm.getXAxis();
		DPoint3D yVersor0=cm.getYAxis();

		DPoint3D xVersor=cm.getXAxis();
		DPoint3D yVersor=cm.getYAxis();
		DPoint3D zVersor=cm.getZAxis();

		DPoint3D zMinusVersor=zVersor.invertPoint();

		int polSize=cm.polygonData.size();
		for(int i=0;i<polSize;i++){

			DPoint3D rotateOrigin=cm.point000;
			LineData ld=cm.polygonData.get(i);

			int deltaWidth=0;
			int deltaHeight=cm.getDeltaY();

			Polygon3D polRotate=PolygonMesh.getBodyPolygon(cm.points,ld);
			polRotate.setShadowCosin(ld.getShadowCosin());

			int face=cm.boxFaces[i];

			int deltaTexture=0;

			if(face==Renderer3D.CAR_BOTTOM ){
				deltaTexture=cm.getDeltaX()+cm.getDeltaX2();
				xDirection=xVersor;
				yDirection=yVersor;
			}
			else if(face==Renderer3D.CAR_FRONT){

				rotateOrigin=cm.point011;

				deltaWidth=cm.getDeltaX();
				deltaHeight=cm.getDeltaY2();
				xDirection=xVersor;
				yDirection=zMinusVersor;
			}
			else if(face==Renderer3D.CAR_BACK){
				deltaWidth=cm.getDeltaX();
				deltaHeight=0;
				xDirection=xVersor;
				yDirection=zVersor;
			}
			else if(face==Renderer3D.CAR_LEFT) {
				xDirection=zVersor;
				yDirection=yVersor;
			}
			else if(face==Renderer3D.CAR_TOP){
				deltaWidth=cm.getDeltaX();
				xDirection=xVersor;
				yDirection=yVersor;
			}
			else if(face==Renderer3D.CAR_RIGHT) {
				xDirection=zMinusVersor;
				yDirection=yVersor;
				rotateOrigin=cm.point001;
				deltaWidth=cm.getDeltaX2();
			}
			decomposeClippedPolygonIntoZBuffer(polRotate,col,texture,decal,zBuffer,
					xDirection,yDirection,rotateOrigin,deltaTexture+deltaWidth,deltaHeight);
		}
	}

	@Override
	public void translate(int i, int j) {
		POSX-=xMovement*i*deltax;
		POSY-=yMovement*j*deltay;
	}

	@Override
	public void translateAxes(int i, int j) {
		x0+=10*i;
		y0+=10*j;
	}
	@Override
	public void zoomOut(){
		deltay=deltax=deltax*2;
	}
	@Override
	public void zoomIn(){
		deltay=deltax=deltax/2;
	}

	@Override
	public void rotate(double df){
		fi+=df;
		sinf=Math.sin(fi);
		cosf=Math.cos(fi);
	}
	@Override
	public ArrayList<Block> getClickedPolygons(int x, int y,Block[] blocks) {

		ArrayList<Block> ret=selectPolygons(x, y,blocks,false);
		return ret;
	}
	@Override
	public void selectPolygons(int x, int y, Block[] blocks) {
		selectPolygons( x,  y,blocks,true);
	}
	@Override
	public ArrayList<Block> selectPolygons(int x, int y,Block[] blocks,boolean toSelect) {

		ArrayList<Block> ret=new ArrayList<Block>();

		ArrayList<BlockToOrder> selectableBlocks=new ArrayList<BlockToOrder>();
		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];

			ArrayList<LineData> lines=bl.getPolygons(false);

			for (int j = 0; j < lines.size(); j++) {

				LineData ld = lines.get(j);
				Polygon3D polReal= PolygonMesh.getBodyPolygon(bl.getMesh().points,ld);

				int indexZ=0;
				try{indexZ=Integer.parseInt(ld.getData2());}catch (Exception e) {}

				Polygon3D polProjectd=builProjectedPolygon(polReal);

				if(polProjectd.contains(x,y)){
					BlockToOrder bot=new BlockToOrder(bl,Polygon3D.findCentroid(polReal),i,indexZ);
					selectableBlocks.add(bot);
				}
			}
		}

		BlockToOrder selectedBlockOrder=null;
		double maxDistance=0;

		/////here calculate the nearest block.
		for (int i = 0; i < selectableBlocks.size(); i++) {
			BlockToOrder bot = selectableBlocks.get(i);

			if(i==0){
				selectedBlockOrder=bot;
				maxDistance=DPoint3D.calculateDotProduct(bot.getCentroid(),projectionNormal);
			}else{

				double distance=DPoint3D.calculateDotProduct(bot.getCentroid(),projectionNormal);
				if(distance>maxDistance){
					selectedBlockOrder=bot;
					maxDistance=distance;
				}
			}
		}
		/////
		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];

			if(selectedBlockOrder!=null && i==selectedBlockOrder.getIndex()){

				if(toSelect){
					editor.setCellPanelData(bl);
					bl.setSelected(true);
					bl.setSelectedZ(selectedBlockOrder.getIndexZ());

				}else{
					bl.setSelectedZ(selectedBlockOrder.getIndexZ());
					ret.add(bl);
				}

			}else if(!editor.checkMultiselection.isSelected()){

				if(toSelect){
					bl.setSelected(false);
					bl.setSelectedZ(-1);
				}
			}
		}

		return ret;
	}

	@Override
	public boolean isPolygonClicked(int x, int y, Block bl) {

		Polygon3D p3d=builProjectedPolygon(bl.getInvertedLowerBase(),bl.getMesh().points);
		if(p3d.contains(x,y)){
			return true;
		}

		return false;
	}
	@Override
	public void fillPolygon(Graphics graph, Polygon3D pol3D) {
		graph.fillPolygon(builProjectedPolygon(pol3D));
	}
	@Override
	public void selectObjects(int x, int y, ArrayList<DrawObject> objects) {
		for (int i = 0; i < objects.size(); i++) {

			DrawObject dro=objects.get(i);

			boolean selected=selectObject(x,y,dro);

			if(selected){
				dro.setSelected(true);
				editor.setObjectPanelData(dro);
			}
			else if(!editor.checkMultiselection.isSelected()) {
				dro.setSelected(false);
			}
		}
	}

	@Override
	public boolean selectObject(int x, int y, DrawObject dro) {
		for (int j = 0; j < dro.polygons.size(); j++) {

			Polygon3D pol3D = dro.polygons.get(j).clone();
			Polygon3D poly = builProjectedPolygon(pol3D);

			if(poly.contains(x,y)){
				return true;
			}
		}
		return false;
	}

	private void loadTextures() {

		File directoryImg=new File("lib");
		File[] files=directoryImg.listFiles();
		try {
			worldTextures=EditorData.loadWorldTextures(files);
			worldDecals=EditorData.loadWorldDecals(files);
			wallTextures=EditorData.loadWallTextures(files);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private class BlockToOrder{

		private Block block=null;
		private DPoint3D centroid=null;
		private int index=-1;
		private int indexZ=-1;

		private BlockToOrder(Block block, DPoint3D centroid, int index, int indexZ) {
			super();
			this.block = block;
			this.centroid = centroid;
			this.index = index;
			this.indexZ = indexZ;
		}

		public DPoint3D getCentroid() {
			return centroid;
		}

		public int getIndex() {
			return index;
		}

		public int getIndexZ() {
			return indexZ;
		}
	}


	@Override
	public void changeMotionIncrement(int i) {
		if(i>0){
			xMovement=2*xMovement;
			yMovement=2*yMovement;
		}else{
			if(xMovement==minMovement) {
				return;
			}
			xMovement=xMovement/2;
			yMovement=yMovement/2;
		}
	}
	
}