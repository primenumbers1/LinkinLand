package primenumbers.main;

import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;

import primenumbers.BarycentricCoordinates;
import primenumbers.CubicMesh;
import primenumbers.DPoint2D;
import primenumbers.DPoint3D;
import primenumbers.DrawObject;
import primenumbers.LineData;
import primenumbers.Plain;
import primenumbers.Point3D;
import primenumbers.Polygon3D;
import primenumbers.PolygonMesh;
import primenumbers.Texture;
import primenumbers.ZBuffer;

public abstract class Renderer3D {

	public int XFOCUS=0;
	public int YFOCUS=0;
	public static final int SCREEN_DISTANCE=300;

	public DPoint3D observerPoint=null;

	public int HEIGHT=0;
	public int WIDTH=0;

	//REFERENCE SYSTEM COORDINATES
	public int POSX=0;
	public int POSY=0;
	public int MOVZ=0;

	public double viewDirection=0;
	public double viewDirectionCos=1.0;
	public double viewDirectionSin=0.0;

	public double sightLatDirection=0;
	public double sightLatDirectionCos=1.0;
	public double sightLatDirectionSin=0.0;

	public static ZBuffer roadZbuffer;
	public static ZBuffer roadZbuffer2;

	private int greenRgb= Color.GREEN.getRGB();

	static final int FRONT_VIEW=+1;
	static final int REAR_VIEW=-1;
	protected int VIEW_TYPE=FRONT_VIEW;

	public static final int CAR_BACK=0;
	public static final int CAR_TOP=1;
	public static final int CAR_LEFT=2;
	public static final int CAR_RIGHT=3;
	public static final int CAR_FRONT=4;
	public static final int CAR_BOTTOM=5;

	private boolean isShadowMap=false;
	private boolean isStencilBuffer=false;

	LightSource lightPoint=null;
	private double lightIntensity=1.0;

	void buildNewZBuffers() {

		for(int i=0;i<roadZbuffer.getSize();i++){
			roadZbuffer.setRgbColor(greenRgb,i);
			roadZbuffer.setEmpty(true,i);

			roadZbuffer2.setRgbColor(greenRgb,i);
			roadZbuffer2.setEmpty(true,i);
		}
	}

	public void buildScreen(BufferedImage buf) {

		int length=roadZbuffer.rgbColor.length;
		for(int i=0;i<length;i++){
			boolean empty0=roadZbuffer.isEmpty(i);
			double rzb0 = roadZbuffer.getZ(i);
			boolean empty1=roadZbuffer2.isEmpty(i);
			double rzb1 = roadZbuffer2.getZ(i);
			if(!empty1 && (empty0 || rzb0>rzb1)){
				roadZbuffer.rgbColor[i]=roadZbuffer2.rgbColor[i];
			}
		}

		buf.getRaster().setDataElements( 0,0,WIDTH,HEIGHT,roadZbuffer.rgbColor);



		for(int i=0;i<length;i++){
			//set
			//rgb[i]=roadZbuffer.getRgbColor(i);
			//clean
			roadZbuffer.set(0,0,0,greenRgb,true,i);
			roadZbuffer2.set(0,0,0,greenRgb,true,i);
		}
	}

	public double calculPerspY( double x, double y, double z) {
		double newy0= ((z)*SCREEN_DISTANCE/y)+YFOCUS;
		return HEIGHT-newy0;
	}

	public double calculPerspX(double x, double y, double z) {

		return ((x)*SCREEN_DISTANCE/y)+XFOCUS;

	}

	private double calculPerspY(DPoint3D point) {
		return calculPerspY( point.x,  point.y, point.z);
	}

	private double calculPespX(DPoint3D point) {
		return calculPerspX( point.x,  point.y, point.z);
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
	public final void decomposeTriangleIntoZBufferEdgeWalking(Polygon3D p3d,Color color,
			Texture texture,Texture decal,ZBuffer zb,
			DPoint3D xDirection, DPoint3D yDirection, DPoint3D origin,int deltaX,int deltaY,
			BarycentricCoordinates bc
			) {

		//create variables for Edge Walking calculus
		Point3D[] points={new Point3D(),new Point3D(),new Point3D()};
		Point3D intersects =new Point3D();
		Point3D intersecte =new Point3D();
		Point3D pstart = new Point3D();
		Point3D pend = new Point3D();
		double l=0;
		double xi =0;
		double yi =0;
		double zi =0;
		double texture_x =0;
		double texture_y =0;
		double cosin;
		DPoint3D p0=new DPoint3D();
		DPoint3D p1=new DPoint3D();
		DPoint3D p2=new DPoint3D();
		int x0;
		int y0;
		double z0;
		int x1;
		int y1;
		double z1;
		int x2;
		int y2;
		double z2;
		int maxX;
		int maxY;
		int minX;
		int minY;
		double xbc;
		double ybc;

		int rgbColor=color.getRGB();

		DPoint3D normal=Polygon3D.findNormal(p3d).calculateVersor();

		boolean isFacing=isFacing(p3d,normal,observerPoint);

		cosin=p3d.getShadowCosin();

		p0.x=p3d.xpoints[0];
		p0.y=p3d.ypoints[0];
		p0.z=p3d.zpoints[0];

		p1.x=p3d.xpoints[1];
		p1.y=p3d.ypoints[1];
		p1.z=p3d.zpoints[1];

		p2.x=p3d.xpoints[2];
		p2.y=p3d.ypoints[2];
		p2.z=p3d.zpoints[2];

		//System.out.println(p3d+" "+rgbColor);

		x0=(int)calculPespX(p0);
		y0=(int)calculPerspY(p0);
		z0=p0.y;

		x1=(int)calculPespX(p1);
		y1=(int)calculPerspY(p1);
		z1=p1.y;

		x2=(int)calculPespX(p2);
		y2=(int)calculPerspY(p2);
		z2=p2.y;

		//check if triangle is visible
		maxX=Math.max(x0,x1);
		maxX=Math.max(x2,maxX);
		maxY=Math.max(y0,y1);
		maxY=Math.max(y2,maxY);
		minX=Math.min(x0,x1);
		minX=Math.min(x2,minX);
		minY=Math.min(y0,y1);
		minY=Math.min(y2,minY);

		if(maxX<0 || minX>WIDTH || maxY<0 || minY>HEIGHT) {
			return;
		}

		double minPY = Math.min(p0.y, p1.y);
		minPY = Math.min(minPY, p2.y);
		if(!checkTriangleVisible(minY,maxY,minX,maxX,minPY,zb)){
			return;
		}


		points[0].x=x0;
		points[0].y=y0;
		points[0].z=z0;
		points[0].p_x=p0.x;
		points[0].p_y=p0.y;
		points[0].p_z=p0.z;

		points[1].x=x1;
		points[1].y=y1;
		points[1].z=z1;
		points[1].p_x=p1.x;
		points[1].p_y=p1.y;
		points[1].p_z=p1.z;

		points[2].x=x2;
		points[2].y=y2;
		points[2].z=z2;
		points[2].p_x=p2.x;
		points[2].p_y=p2.y;
		points[2].p_z=p2.z;


		int mip_map_level = 0;

		if(texture!=null){

			mip_map_level = (int) (0.5*
					Math.log(
							bc.getRealTriangleArea() / BarycentricCoordinates.getTriangleArea(x0, y0,0, x1, y1,0, x2, y2,0))
					/ Math.log(2));

			int w=texture.getWidth();
			int h=texture.getHeight();

			DPoint2D pt0=bc.pt0;
			DPoint2D pt1=bc.pt1;
			DPoint2D pt2=bc.pt2;

			DPoint3D p=bc.getBarycentricCoordinates(points[0].p_x,points[0].p_y,points[0].p_z);
			xbc= (p.x*(pt0.x)+p.y*pt1.x+(1-p.x-p.y)*pt2.x);
			ybc= (p.x*(pt0.y)+p.y*pt1.y+(1-p.x-p.y)*pt2.y);
			points[0].setTexurePositions(xbc,texture.getHeight()-ybc);

			p=bc.getBarycentricCoordinates(points[1].p_x,points[1].p_y,points[1].p_z);
			xbc= (p.x*(pt0.x)+p.y*pt1.x+(1-p.x-p.y)*pt2.x);
			ybc= (p.x*(pt0.y)+p.y*pt1.y+(1-p.x-p.y)*pt2.y);
			points[1].setTexurePositions(xbc,texture.getHeight()-ybc);

			p=bc.getBarycentricCoordinates( points[2].p_x,points[2].p_y,points[2].p_z);
			xbc= (p.x*(pt0.x)+p.y*pt1.x+(1-p.x-p.y)*pt2.x);
			ybc= (p.x*(pt0.y)+p.y*pt1.y+(1-p.x-p.y)*pt2.y);
			points[2].setTexurePositions(xbc,texture.getHeight()-ybc);

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
		int j1=upP.y<HEIGHT?(int)upP.y:HEIGHT;


		for(int j=j0;j<j1;j++){

			double middlex = Point3D.foundXIntersection(upP, lowP, j);
			Point3D.foundPX_PY_PZ_TEXTURE_Intersection(upP, lowP, j,intersects);
			double middlex2 = Point3D.foundXIntersection(upP, midP, j);
			Point3D.foundPX_PY_PZ_TEXTURE_Intersection(upP, midP, j,intersecte);

			pstart.x =middlex;
			pstart.y=j;
			pstart.z=intersects.p_z;
			pstart.p_x=intersects.p_x;
			pstart.p_y=intersects.p_y;
			pstart.p_z=intersects.p_z;
			pstart.texture_x=intersects.texture_x;
			pstart.texture_y=intersects.texture_y;

			pend.x =middlex2;
			pend.y=j;
			pend.z=intersecte.p_z;
			pend.p_x=intersecte.p_x;
			pend.p_y=intersecte.p_y;
			pend.p_z=intersecte.p_z;
			pend.texture_x=intersecte.texture_x;
			pend.texture_y=intersecte.texture_y;

			if(pstart.x>pend.x){
				Point3D swap= pend;
				pend= pstart;
				pstart=swap;
			}

			int start=(int)pstart.x;
			int end=(int)pend.x;

			double inverse=1.0/(end-start);
			double i_pstart_p_y=1.0/(pstart.p_y);
			double i_end_p_y=1.0/(pend.p_y);

			int i0=start>0?start:0;
			l=(i0-start-1)*inverse;

			for(int i=i0;i<end;i++){

				if(i>=WIDTH) {
					break;
				}

				int tot=WIDTH*j+i;
				l += inverse;

				yi=1.0/((1-l)*i_pstart_p_y+l*i_end_p_y);

				if(!zb.isToUpdate(yi,tot) || isStencilBuffer){
					//z-fail stencil buffer with bias
					if(isStencilBuffer && !zb.isToUpdate(yi+4,tot)){

						stencilBuffer(tot,isFacing);
					}
					continue;
				}

				zi=((1-l)*i_pstart_p_y*pstart.p_z+l*i_end_p_y*pend.p_z)*yi;
				xi=((1-l)*i_pstart_p_y*pstart.p_x+l*i_end_p_y*pend.p_x)*yi;

				texture_x=((1-l)*i_pstart_p_y*pstart.texture_x+l*i_end_p_y*pend.texture_x)*yi;
				texture_y=((1-l)*i_pstart_p_y*pstart.texture_y+l*i_end_p_y*pend.texture_y)*yi;



				if(texture!=null) {
					rgbColor=Texture.getRGBMip(texture,decal,(int)texture_x,(int) texture_y,mip_map_level);
				}
				//rgbColor=ZBuffer.pickRGBColorFromTexture(texture,xi,yi,zi,xDirection,yDirection,origin,deltaX, deltaY);
				if(rgbColor==greenRgb) {
					continue;
				}

				//System.out.println(x+" "+y+" "+tot);

				zb.set(xi,yi,zi,calculateShadowColor(xi,yi,zi,cosin,rgbColor),false,tot);

			}
		}
		//LOWER TRIANGLE
		j0=lowP.y>0?(int)lowP.y:0;
		j1=midP.y<HEIGHT?(int)midP.y:HEIGHT;


		for(int j=j0;j<j1;j++){

			double middlex = Point3D.foundXIntersection(upP, lowP, j);

			Point3D.foundPX_PY_PZ_TEXTURE_Intersection(upP, lowP, j,intersects);

			double middlex2 = Point3D.foundXIntersection(lowP, midP, j);

			Point3D.foundPX_PY_PZ_TEXTURE_Intersection(lowP, midP, j,intersecte);

			pstart.x =middlex;
			pstart.y=j;
			pstart.z=intersects.p_z;
			pstart.p_x=intersects.p_x;
			pstart.p_y=intersects.p_y;
			pstart.p_z=intersects.p_z;
			pstart.texture_x=intersects.texture_x;
			pstart.texture_y=intersects.texture_y;

			pend.x =middlex2;
			pend.y=j;
			pend.z=intersecte.p_z;
			pend.p_x=intersecte.p_x;
			pend.p_y=intersecte.p_y;
			pend.p_z=intersecte.p_z;
			pend.texture_x=intersecte.texture_x;
			pend.texture_y=intersecte.texture_y;


			if(pstart.x>pend.x){
				Point3D swap= pend;
				pend= pstart;
				pstart=swap;
			}

			int start=(int)pstart.x;
			int end=(int)pend.x;

			double inverse=1.0/(end-start);

			double i_pstart_p_y=1.0/(pstart.p_y);
			double i_end_p_y=1.0/(pend.p_y);

			int i0=start>0?start:0;
			l=(i0-start-1)*inverse;

			for(int i=i0;i<end;i++){
				if(i>=WIDTH) {
					break;
				}

				int tot=WIDTH*j+i;
				l += inverse;

				yi=1.0/((1-l)*i_pstart_p_y+l*i_end_p_y);

				if(!zb.isToUpdate(yi,tot) || isStencilBuffer){

					//z-fail stencil buffer with bias
					if(isStencilBuffer && !zb.isToUpdate(yi+4,tot)){
						stencilBuffer(tot,isFacing);
					}
					continue;
				}

				zi=((1-l)*i_pstart_p_y*pstart.p_z+l*i_end_p_y*pend.p_z)*yi;
				xi=((1-l)*i_pstart_p_y*pstart.p_x+l*i_end_p_y*pend.p_x)*yi;

				texture_x=((1-l)*i_pstart_p_y*pstart.texture_x+l*i_end_p_y*pend.texture_x)*yi;
				texture_y=((1-l)*i_pstart_p_y*pstart.texture_y+l*i_end_p_y*pend.texture_y)*yi;


				if(texture!=null) {
					rgbColor=Texture.getRGBMip(texture,decal,(int)texture_x,(int) texture_y,mip_map_level);
				}
				if(rgbColor==greenRgb) {
					continue;
				}
				//System.out.println(x+" "+y+" "+tot);
				zb.set(xi,yi,zi,calculateShadowColor(xi,yi,zi,cosin,rgbColor),false,tot);
			}
		}
	}

	/**
	 * Check if the triangle is not completely covered by the rendered tile
	 *
	 * */
	private boolean checkTriangleVisible(int minY, int maxY, int minX, int maxX, double minPY, ZBuffer zb) {

		if(minY<0) {
			minY=0;
		}
		if(minX<0) {
			minX=0;
		}
		if(maxY>HEIGHT-1) {
			maxY=HEIGHT-1;
		}
		if(maxX>WIDTH-1) {
			maxX=WIDTH-1;
		}

		for (int i = minX; i <= maxX; i++) {
			for (int j = minY; j <= maxY; j++) {

				int tot=WIDTH*j+i;
				if(zb.isEmpty(tot)) {
					return true;
				}
				double mz=zb.getZ(tot);
				if(mz>minPY) {
					return true;
				}
			}
		}
		return false;
	}

	public void stencilBuffer(int tot, boolean isFacing) {

	}

	private final int calculateShadowColor(double xi, double yi, double zi, double cosin, int argbs) {
		//variables used for calculus
		double li=0;
		double factor=0;
		int alphas=0;
		int rs=0;
		int gs=0;
		int bs=0;

		li=lightIntensity*(0.9/(1.0+yi*0.0016)+0.1);

		factor=(li*(0.75+0.25*cosin));

		alphas=0xff & (argbs>>24);
		rs = 0xff & (argbs>>16);
		gs = 0xff & (argbs >>8);
		bs = 0xff & argbs;

		rs=(int) (factor*rs);
		gs=(int) (factor*gs);
		bs=(int) (factor*bs);

		return alphas <<24 | rs <<16 | gs <<8 | bs;
	}


	void calculateShadowCosines(CubicMesh cm) {

		int polSize=cm.polygonData.size();
		for(int i=0;i<polSize;i++){

			LineData ld=cm.polygonData.get(i);

			Polygon3D polygon = PolygonMesh.getBodyPolygon(cm.points,ld);

			DPoint3D centroid = Polygon3D.findCentroid(polygon);

			DPoint3D normal=(Polygon3D.findNormal(polygon)).calculateVersor();

			ld.setShadowCosin(DPoint3D.calculateCosin(lightPoint.getPosition().substract(centroid),normal));
		}
	}

	//calculus point

	public void decomposeClippedPolygonIntoZBuffer(Polygon3D p3d,Color color,Texture texture,Texture decal,ZBuffer zbuffer){

		DPoint3D origin=new DPoint3D(p3d.xpoints[0],p3d.ypoints[0],p3d.zpoints[0]);
		decomposeClippedPolygonIntoZBuffer(p3d, color, texture,decal,zbuffer,null,null,origin,0,0);
	}

	public void decomposeClippedPolygonIntoZBuffer(Polygon3D p3d,Color color,Texture texture,Texture decal,ZBuffer zbuffer,
			DPoint3D xDirection,DPoint3D yDirection,DPoint3D origin,int deltaX,int deltaY){

		DPoint3D normal=Polygon3D.findNormal(p3d);

		if(!isStencilBuffer && !isFacing(p3d,normal,observerPoint)) {
			return;
		}

		if(texture!=null && xDirection==null && yDirection==null){

			xDirection=new Point3D( p3d.xpoints[1]- p3d.xpoints[0],
					p3d.ypoints[1]- p3d.ypoints[0],
					p3d.zpoints[1]- p3d.zpoints[0]
					).reduceToVersor();
			yDirection=DPoint3D.calculateCrossProduct(normal,xDirection).reduceToVersor();
		}

		Polygon3D[] triangles = Polygon3D.divideIntoTriangles(p3d);

		for(int i=0;i<triangles.length;i++){

			BarycentricCoordinates bc=new BarycentricCoordinates(triangles[i]);

			Polygon3D clippedPolygon=Polygon3D.clipPolygon3DInY(triangles[i],(int) (SCREEN_DISTANCE*0.25));

			if(clippedPolygon==null || clippedPolygon.npoints==0) {
				continue;
			}

			Polygon3D[] clippedTriangles = Polygon3D.divideIntoTriangles(clippedPolygon);

			for (int j = 0; j < clippedTriangles.length; j++) {
				decomposeTriangleIntoZBufferEdgeWalking(
						clippedTriangles[j],color, texture,decal,zbuffer,
						xDirection,yDirection,origin, deltaX, deltaY,bc);
			}
		}
	}

	private void drawPolygonMesh(DrawObject dro,Rectangle rect,ZBuffer zbuffer) {

		//if(!totalVisibleField.contains(dro.x-POSX,VIEW_DIRECTION*(dro.y-POSY)))

		if(rect.y+rect.height<dro.y-POSY) {
			return;
		}

		PolygonMesh mesh = dro.getMesh();
		decomposeCubicMesh((CubicMesh) mesh,ZBuffer.fromHexToColor(dro.getHexColor()),null,null,zbuffer);
	}

	public void decomposeCubicMesh(CubicMesh cm,Color col, Texture texture,Texture decal,ZBuffer zBuffer){

		DPoint3D xDirection=null;
		DPoint3D yDirection=null;

		//versors to transform in the system:
		DPoint3D xVersor0=buildTransformedVersor(cm.getXAxis());
		DPoint3D yVersor0=buildTransformedVersor(cm.getYAxis());

		DPoint3D xVersor=buildTransformedVersor(cm.getXAxis());
		DPoint3D yVersor=buildTransformedVersor(cm.getYAxis());
		DPoint3D zVersor=buildTransformedVersor(cm.getZAxis());

		DPoint3D zMinusVersor=zVersor.invertPoint();

		if(VIEW_TYPE==REAR_VIEW){
			///???
			yVersor=new Point3D(-yVersor0.x,-yVersor0.y,yVersor0.z);
			xVersor=new Point3D(-xVersor0.x,-xVersor0.y,xVersor0.z);
			zVersor=new Point3D(-zVersor.x,-zVersor.y,zVersor.z);
			zMinusVersor=new Point3D(-zMinusVersor.x,-zMinusVersor.y,zMinusVersor.z);
		}
		int polSize=cm.polygonData.size();
		for(int i=0;i<polSize;i++){

			DPoint3D rotateOrigin=cm.point000;
			rotateOrigin=buildTransformedPoint(rotateOrigin);

			LineData ld=cm.polygonData.get(i);
			//Point3D normal= cm.normals.elementAt(i).clone();

			int deltaWidth=0;
			int deltaHeight=cm.getDeltaY();


			Polygon3D polRotate=PolygonMesh.getBodyPolygon(cm.points,ld);
			polRotate.setShadowCosin(ld.getShadowCosin());

			int face=cm.boxFaces[i];
			buildTransformedPolygon(polRotate);

			int deltaTexture=0;

			if(face==CAR_BOTTOM ){
				deltaTexture=cm.getDeltaX()+cm.getDeltaX2();
				xDirection=xVersor;
				yDirection=yVersor;
			}
			else if(face==CAR_FRONT){

				rotateOrigin=cm.point011;
				rotateOrigin=buildTransformedPoint(rotateOrigin);

				deltaWidth=cm.getDeltaX();
				deltaHeight=cm.getDeltaY2();
				xDirection=xVersor;
				yDirection=zMinusVersor;
			}
			else if(face==CAR_BACK){
				deltaWidth=cm.getDeltaX();
				deltaHeight=0;
				xDirection=xVersor;
				yDirection=zVersor;
			}
			else if(face==CAR_LEFT) {

				xDirection=zVersor;
				yDirection=yVersor;
			}
			else if(face==CAR_TOP){
				deltaWidth=cm.getDeltaX();
				xDirection=xVersor;
				yDirection=yVersor;
			}
			else if(face==CAR_RIGHT) {

				xDirection=zMinusVersor;
				yDirection=yVersor;

				rotateOrigin=cm.point001;
				rotateOrigin=buildTransformedPoint(rotateOrigin);

				deltaWidth=cm.getDeltaX2();
			}
			decomposeClippedPolygonIntoZBuffer(polRotate,col,texture,decal,zBuffer,
					xDirection,yDirection,rotateOrigin,deltaTexture+deltaWidth,deltaHeight);
		}
	}

	public static boolean isFacing(Polygon3D pol,DPoint3D normal,DPoint3D observer){

		//Point3D p0=new Point3D(pol.xpoints[0],pol.ypoints[0],pol.zpoints[0]);
		double cosin=DPoint3D.calculateCosin(normal,observer.substract(Polygon3D.findCentroid(pol)));

		return cosin>=0;
	}

	private void decomposePointIntoZBuffer(double xs, double ys, double zs,
			double ds, int rgbColor,ZBuffer zbuffer,boolean showAlways) {

		if(!showAlways && ys<=SCREEN_DISTANCE/2) {
			return;
		}

		//double gamma=SCREEN_DISTANCE/(SCREEN_DISTANCE+ys);

		double xd=calculPerspX(xs,ys,zs);
		double yd=calculPerspY(xs,ys,zs);

		if(xd<0 || xd>=WIDTH || yd<0 || yd>= HEIGHT) {
			return;
		}

		int x=(int) xd;
		int y=(int) yd;

		//double i_ys=1.0/ys;

		int tot=y*WIDTH+x;

		if(showAlways) {
			zbuffer.set(xs,ys,zs,rgbColor,false,tot);
		} else {
			zbuffer.update(xs,ys,zs,rgbColor,false,tot);
		}
	}

	public Polygon3D buildRectangleBase3D(double x,double y,double z,double dx,double dy,double dz){

		int[] cx=new int[4];
		int[] cy=new int[4];
		int[] cz=new int[4];

		cx[0]=(int) x;
		cy[0]=(int) y;
		cz[0]=(int) z;
		cx[1]=(int) (x+dx);
		cy[1]=(int) y;
		cz[1]=(int) z;
		cx[2]=(int) (x+dx);
		cy[2]=(int) (y+dy);
		cz[2]=(int) z;
		cx[3]=(int) x;
		cy[3]=(int) (y+dy);
		cz[3]=(int) z;

		Polygon3D base=new Polygon3D(4,cx,cy,cz);
		return base;
	}

	private DPoint3D buildTransformedVersor(DPoint3D point) {

		DPoint3D newPoint=new DPoint3D();
		double x=point.x;
		double y=point.y;
		double z=point.z;

		/*newPoint.x= (viewDirectionCos*x+viewDirectionSin*y);
		newPoint.y= (viewDirectionCos*y-viewDirectionSin*x);
		newPoint.z=z;*/

		newPoint.x=(viewDirectionCos*x+viewDirectionSin*y);
		newPoint.y=(sightLatDirectionCos*viewDirectionCos*y-sightLatDirectionCos*viewDirectionSin*x+sightLatDirectionSin*z);
		newPoint.z=(sightLatDirectionSin*viewDirectionSin*x-sightLatDirectionSin*viewDirectionCos*y+sightLatDirectionCos*z);
		return newPoint;
	}

	public DPoint3D buildTransformedPoint(DPoint3D point) {

		DPoint3D newPoint=new DPoint3D();

		if(VIEW_TYPE==FRONT_VIEW){

			double x=point.x-POSX;
			double y=point.y-POSY;
			double z=point.z-MOVZ;

			newPoint.x= (viewDirectionCos*x+viewDirectionSin*y);
			newPoint.y= (sightLatDirectionCos*viewDirectionCos*y-sightLatDirectionCos*viewDirectionSin*x+sightLatDirectionSin*z);
			newPoint.z= (sightLatDirectionSin*viewDirectionSin*x-sightLatDirectionSin*viewDirectionCos*y+sightLatDirectionCos*z);
		}
		else{

			double x=point.x-POSX;
			double y=point.y-POSY;
			double z=point.z-MOVZ;

			newPoint.x=-(viewDirectionCos*x+viewDirectionSin*y);
			newPoint.y=2*SCREEN_DISTANCE-(sightLatDirectionCos*viewDirectionCos*y-sightLatDirectionCos*viewDirectionSin*x+sightLatDirectionSin*z);
			newPoint.z= (sightLatDirectionSin*viewDirectionSin*x-sightLatDirectionSin*viewDirectionCos*y+sightLatDirectionCos*z);
		}
		return newPoint;
	}


	public Point3D[] buildTrasformedPoints(Point3D[] points) {
		Point3D[] newPoints=new Point3D[points.length];

		for(int i=0;i<points.length;i++){

			newPoints[i]=new Point3D();

			double x=points[i].x-POSX;
			double y=points[i].y-POSY;
			double z=points[i].z-MOVZ;

			if(VIEW_TYPE==FRONT_VIEW){

				newPoints[i].x=(int) (viewDirectionCos*x+viewDirectionSin*y);
				newPoints[i].y=(int) (sightLatDirectionCos*viewDirectionCos*y-sightLatDirectionCos*viewDirectionSin*x+sightLatDirectionSin*z);
				newPoints[i].z=(int) (sightLatDirectionSin*viewDirectionSin*x-sightLatDirectionSin*viewDirectionCos*y+sightLatDirectionCos*z);

				//base.xpoints[i]=(int) (viewDirectionCos*(x-observerPoint.x)+viewDirectionSin*(y-observerPoint.y)+observerPoint.x);
				//base.ypoints[i]=(int) (viewDirectionCos*(y-observerPoint.y)-viewDirectionSin*(x-observerPoint.x)+observerPoint.y);
			}
			else{

				newPoints[i].x=-(int) (viewDirectionCos*x+viewDirectionSin*y);
				newPoints[i].y=2*SCREEN_DISTANCE-(int)(sightLatDirectionCos*viewDirectionCos*y-sightLatDirectionCos*viewDirectionSin*x+sightLatDirectionSin*z);
				newPoints[i].z=(int) (sightLatDirectionSin*viewDirectionSin*x-sightLatDirectionSin*viewDirectionCos*y+sightLatDirectionCos*z);
			}
		}

		return newPoints;
	}


	public void buildTransformedPolygon(Polygon3D base) {
		buildTransformedPolygon(base,POSX,POSY);
	}

	public void buildTransformedPolygon(Polygon3D base,int PX,int PY) {

		for(int i=0;i<base.npoints;i++){

			if(VIEW_TYPE==FRONT_VIEW){

				double x=base.xpoints[i]-PX;
				double y=base.ypoints[i]-PY;
				double z=base.zpoints[i]-MOVZ;

				base.xpoints[i]=(int) (viewDirectionCos*x+viewDirectionSin*y);
				base.ypoints[i]=(int) (sightLatDirectionCos*viewDirectionCos*y-sightLatDirectionCos*viewDirectionSin*x+sightLatDirectionSin*z);
				base.zpoints[i]=(int) (sightLatDirectionSin*viewDirectionSin*x-sightLatDirectionSin*viewDirectionCos*y+sightLatDirectionCos*z);

				//base.xpoints[i]=(int) (viewDirectionCos*(x-observerPoint.x)+viewDirectionSin*(y-observerPoint.y)+observerPoint.x);
				//base.ypoints[i]=(int) (viewDirectionCos*(y-observerPoint.y)-viewDirectionSin*(x-observerPoint.x)+observerPoint.y);
			}
			else{

				double x=base.xpoints[i]-PX;
				double y=base.ypoints[i]-PY;
				double z=base.zpoints[i]-MOVZ;

				base.xpoints[i]=-(int) (viewDirectionCos*x+viewDirectionSin*y);
				base.ypoints[i]=2*SCREEN_DISTANCE-(int)(sightLatDirectionCos*viewDirectionCos*y-sightLatDirectionCos*viewDirectionSin*x+sightLatDirectionSin*z);
				base.zpoints[i]=(int) (sightLatDirectionSin*viewDirectionSin*x-sightLatDirectionSin*viewDirectionCos*y+sightLatDirectionCos*z);
			}
		}
	}

	private void rotatePoint(Point3D p,double xo,double yo,double cosTur,double sinTur){


		double x=p.x;
		double y=p.y;

		p.x=xo+(x-xo)*cosTur-(y-yo)*sinTur;
		p.y=yo+(y-yo)*cosTur+(x-xo)*sinTur;
	}

	protected double interpolate(double px, double py, Polygon3D p3d) {

		/*Plain plain=Plain.calculatePlain(p3d);
		return plain.calculateZ(px,py);

		 */
		Polygon3D p1=Polygon3D.extractSubPolygon3D(p3d,3,0);

		if(p1.hasInsidePoint(px,py)){
			Plain plane1=Plain.calculatePlain(p1);
			return plane1.calculateZ(px,py);
		}

		Polygon3D p2=Polygon3D.extractSubPolygon3D(p3d,3,2);

		if(p2.hasInsidePoint(px,py)){
			Plain plane2=Plain.calculatePlain(p2);
			return plane2.calculateZ(px,py);
		}
		return 0;
	}

	public static int findBoxFace(Point3D normal,Point3D versusx,Point3D versusy,Point3D versusz) {

		double normx=Point3D.calculateDotProduct(normal,versusx);
		double normy=Point3D.calculateDotProduct(normal,versusy);
		double normz=Point3D.calculateDotProduct(normal,versusz);

		double absx = Math.abs(normx);
		double absy = Math.abs(normy);
		double absz = Math.abs(normz);

		if(absx>=absy && absx>=absz ){

			if(normx>0) {
				return CAR_RIGHT;
			} else {
				return CAR_LEFT;
			}
		}
		if(absy>=absx && absy>=absz ){

			if(normy>0) {
				return CAR_FRONT;
			} else {
				return CAR_BACK;
			}
		}
		if(absz>=absx && absz>=absy ){

			if(normz>0) {
				return CAR_TOP;
			} else {
				return CAR_BOTTOM;
			}
		}

		return -1;
	}

	public int getMOVZ() {
		return MOVZ;
	}

	public void setPOSX(int posx) {
		POSX = posx;
	}


	public static void showMemoryUsage() {

		MemoryMXBean xbean = ManagementFactory.getMemoryMXBean();
		MemoryUsage heap=xbean.getHeapMemoryUsage();
		MemoryUsage nonheap=xbean.getNonHeapMemoryUsage();
		Runtime run=Runtime.getRuntime();

		System.out.println("free:"+run.freeMemory()+" , total: "+run.totalMemory());

		System.out.println("heap:"+heap);
		System.out.println("non heap:"+nonheap);
		System.out.println("*************");
	}

	public boolean isShadowMap() {
		return isShadowMap;
	}

	public void setShadowMap(boolean isShadowMap) {
		this.isShadowMap = isShadowMap;
	}

	public boolean isStencilBuffer() {
		return isStencilBuffer;
	}

	public void setStencilBuffer(boolean isStencilBuffer) {
		this.isStencilBuffer = isStencilBuffer;
	}

	public double getViewDirection() {
		return viewDirection;
	}

	public void setViewDirection(double viewDirection) {
		this.viewDirection = viewDirection;
		viewDirectionCos=Math.cos(this.viewDirection);
		viewDirectionSin=Math.sin(this.viewDirection);
	}
	public double getSightLatDirection() {
		return sightLatDirection;
	}

	public void setSightLatDirection(double sightLatDirection) {
		this.sightLatDirection = sightLatDirection;
		sightLatDirectionCos=Math.cos(sightLatDirection);
		sightLatDirectionSin=Math.sin(sightLatDirection);
	}

	public int getVIEW_TYPE() {
		return VIEW_TYPE;
	}

	public void setVIEW_TYPE(int vIEW_TYPE) {
		VIEW_TYPE = vIEW_TYPE;
	}

	/*public double getMoveDirection() {
		return moveDirection;
	}

	public void setMoveDirection(double moveDirection) {

		this.moveDirection = moveDirection;
		moveDirectionCos=Math.cos(moveDirection);
		moveDirectionSin=Math.sin(moveDirection);

		viewDirectionCos=Math.cos(moveDirection+sightLongDirection);
		viewDirectionSin=Math.sin(moveDirection+sightLongDirection);
	}

	public double getSightLongDirection() {
		return sightLongDirection;
	}

	public void setSightLongDirection(double sightLongDirection) {

		this.sightLongDirection = sightLongDirection;

		viewDirectionCos=Math.cos(moveDirection+sightLongDirection);
		viewDirectionSin=Math.sin(moveDirection+sightLongDirection);
	}*/
	
}