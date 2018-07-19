package primenumbers.editors.scene.panels;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import primenumbers.DPoint3D;
import primenumbers.DrawObject;
import primenumbers.LineData;
import primenumbers.Point3D;
import primenumbers.Polygon3D;
import primenumbers.ZBuffer;
import primenumbers.editors.scene.SceneEditor;
import primenumbers.scene.Block;
import primenumbers.scene.Scene;

public class SceneEditorTopPanel extends EditorPanel{

	private double alfa=Math.PI/3;
	private double sinAlfa=Math.sin(alfa);
	private double cosAlfa=Math.cos(alfa);
	private double fi=0;
	private double sinf=Math.sin(fi);
	private double cosf=Math.cos(fi);

	private int y0=250;
	private int x0=150;

	private double deltay=10;
	private double deltax=10;

	private Font sFont=null;

	private int minMovement=5;

	private Area totArea;
	private Rectangle totAreaNounds;

	private Color colEmpty=new Color(255,0,0,200);
	private Color colFull=new Color(0,255,0,200);


	public SceneEditorTopPanel(SceneEditor editor, int cENTER_WIDTH,int cENTER_HEIGHT) {
		super(editor, cENTER_WIDTH,cENTER_HEIGHT);
		sFont=new Font("Verdana",Font.BOLD,13);

		xMovement=2*minMovement;
		yMovement=2*minMovement;

		totArea=new Area(new Rectangle(0,0,cENTER_WIDTH,cENTER_HEIGHT));
		totAreaNounds=totArea.getBounds();
	}

	@Override
	public void drawScene(Scene scene,int layer,Graphics graph ) {

		double mizZObjects=0;

		graph.setFont(sFont);

		if(scene.map!=null){

			mizZObjects=SceneEditor.getMinZDrawingObjects(scene.map,layer,null);

			Block[] blocks = scene.map.blocks;

			for (int i = 0; i <blocks.length; i++) {

				Block bl = blocks[i];
				if(layer==SceneEditor.ALL_LAYERS && bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL ) {
					continue;
				}
				else if(layer!=Block.BLOCK_GROUND_LAYER && bl.getLayer()==Block.BLOCK_GROUND_LAYER ){

					drawPolygon(bl,graph,null,true);
					continue;

				}else if(layer!=SceneEditor.ALL_LAYERS && bl.getLayer()!=layer) {
					continue;
				}

				Color color=ZBuffer.fromHexToColor(bl.getHexColor());
				drawPolygon(bl,graph,color);
			}
		}

		ArrayList<DrawObject> objs=scene.objects;

		if(!hide_objects) {
			for (int i = 0; i < objs.size(); i++) {
				DrawObject dro = objs.get(i);

				if(dro.getZ()<mizZObjects) {
					continue;
				}

				for (int j = 0; j < dro.polygons.size(); j++) {

					Polygon3D pol3D = dro.polygons.get(j).clone();

					DPoint3D center=Polygon3D.findCentroid(pol3D);
					Polygon3D.rotate(pol3D,center,dro.getRotation_angle());


					Color col=ZBuffer.fromHexToColor(dro.getHexColor());

					if(dro.isSelected()) {
						col=Color.RED;
					}

					Color acol=new Color(col.getRed(),col.getGreen(),col.getBlue(),150);
					graph.setColor(acol);

					fillPolygon(graph,pol3D);
				}
			}
		}
	}


	private void drawPolygon(Block bl, Graphics graph, Color color) {
		drawPolygon( bl,  graph,  color,false);
	}

	private void drawPolygon(Block bl, Graphics graph, Color color,boolean isGrayScale) {

		int block_type=bl.getBlock_type();
		LineData ld=bl.getInvertedLowerBase();
		Point3D[] points=bl.getMesh().points;

		if(ld.isSelected() && block_type==Block.BLOCK_TYPE_EMPTY_WALL) {
			graph.setColor(colEmpty);
		} else if(ld.isSelected() && block_type!=Block.BLOCK_TYPE_EMPTY_WALL) {
			graph.setColor(colFull);
		} else {
			graph.setColor(color);
		}

		Polygon3D pol = builProjectedPolygon(ld,points);

		if(!Polygon3D.isIntersect(pol,totAreaNounds)) {
			return;
		}

		Rectangle rect=pol.getBounds();

		BufferedImage image=null;
		BufferedImage decal=null;

		if(block_type==Block.BLOCK_TYPE_EMPTY_WALL){
			image=null;
		}
		else if(block_type==Block.BLOCK_TYPE_GROUND){
			image=SceneEditor.worldImages[ld.getTexture_index()];
			if(ld.getDecal_index()!=Block.NO_DECAL_INDEX){
				decal=SceneEditor.worldDecals[ld.getDecal_index()];
			}
		}
		else if(block_type==Block.BLOCK_TYPE_FULL_WALL){
			image=editor.wallImages[ld.getTexture_index()];
		}
		else if(block_type==Block.BLOCK_TYPE_WINDOW){
			image=editor.otherTextures[SceneEditor.OTHER_IMAGES_INDEX_WINDOW];
		}else if(block_type==Block.BLOCK_TYPE_DOOR){
			image=editor.otherTextures[SceneEditor.OTHER_IMAGES_INDEX_DOOR];
		}else if(block_type==Block.BLOCK_TYPE_GRID){
			image=editor.otherTextures[SceneEditor.OTHER_IMAGES_INDEX_GRID];
		}

		if(isGrayScale && image!=null){
			if(editor.isUseBlueScale()) {
				image=buildBlueScale(image);
			}else{
				image=buildGrayScale(image);
			}
		}

		if(image!=null){
			graph.drawImage(image,
					rect.x,
					rect.y,
					(int)rect.getWidth(),
					(int)rect.getHeight(),
					null);
		}
		if(decal!=null){
			graph.drawImage(decal,
					rect.x,
					rect.y,
					(int)rect.getWidth(),
					(int)rect.getHeight(),
					null);
		}

		if(editor.checkDrawBlockHeight.isSelected()

				&& block_type!=Block.BLOCK_TYPE_GROUND
				){

			Color col=graph.getColor();

			graph.setColor(Color.BLACK);
			int xx=(rect.x);
			int yy=rect.y+sFont.getSize();

			graph.drawString(Integer.toString(bl.getNz()),xx,yy);
			graph.setColor(col);
		}

		if(ld.isSelected()) {
			graph.fillPolygon(pol);
		}
	}

	private BufferedImage buildGrayScale(BufferedImage image) {

		int index=image.hashCode();

		if(grayImages.get(index)!=null) {
			return grayImages.get(index);
		}

		int w=image.getWidth();
		int h=image.getHeight();

		BufferedImage retImage=new BufferedImage(w, h, image.getType());

		for (int x = 0; x <w;  ++x) {
			for (int y = 0; y < h; ++y)
			{
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);

				int grayLevel = (int) ((r + g + b)*0.33333);
				int gray = (grayLevel << 16) + (grayLevel << 8) + grayLevel;
				retImage.setRGB(x, y, gray);
			}
		}
		grayImages.put(index, retImage);
		return retImage;
	}

	private BufferedImage buildBlueScale(BufferedImage image) {

		int index=image.hashCode();

		if(grayImages.get(index)!=null) {
			return grayImages.get(index);
		}

		int w=image.getWidth();
		int h=image.getHeight();

		BufferedImage retImage=new BufferedImage(w, h, image.getType());

		for (int x = 0; x <w;  ++x) {
			for (int y = 0; y < h; ++y)
			{
				int rgb = image.getRGB(x, y);
				int r = (rgb >> 16) & 0xFF;
				int g = (rgb >> 8) & 0xFF;
				int b = (rgb & 0xFF);
				retImage.setRGB(x, y, b);
			}
		}
		grayImages.put(index, retImage);
		return retImage;
	}

	@Override
	public int calcAssX(double x, double y, double z) {
		double xx=x;
		double yy=y;
		double zz=z;

		return (int) (xx /deltax+x0);
	}
	@Override
	public int calcAssY(double x, double y, double z) {
		double xx=x;
		double yy=y;
		double zz=z;

		return CENTER_HEIGHT-(int) (yy/deltay+y0);
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

		Polygon3D pol=new Polygon3D();

		int size=ld.size();

		for(int i=0;i<size;i++){

			int num=ld.getIndex(i);
			Point3D p=points[num];
			int xx=calcAssX(p.x,p.y,p.z);
			int yy=calcAssY(p.x,p.y,p.z);

			pol.addPoint(xx,yy);
		}
		return pol;
	}
	@Override
	public Polygon3D builProjectedPolygon(Polygon3D p3d) {

		Polygon3D pol=new Polygon3D();
		int size=p3d.npoints;

		for(int i=0;i<size;i++){
			double x=p3d.xpoints[i];
			double y=p3d.ypoints[i];
			double z=p3d.zpoints[i];

			int xx=calcAssX(x,y,z);
			int yy=calcAssY(x,y,z);

			pol.addPoint(xx,yy);
		}
		return pol;
	}
	@Override
	public void translate(int i, int j) {
		x0+=xMovement*i;
		y0+=yMovement*j;
	}
	@Override
	public void translateAxes(int i, int j) {
		translate(i,j);
	}
	@Override
	public void zoomOut(){
		zoom(-1);
	}
	@Override
	public void zoomIn(){
		zoom(+1);
	}

	public void zoom(int i) {

		double alfa=1.0;
		if(i>0){
			alfa=0.5;
			if(deltax==1 || deltay==1) {
				return;
			}
		}
		else {
			alfa=2.0;
		}
		deltax=(int) (deltax*alfa);
		deltay=(int) (deltay*alfa);

		x0+=(int) ((-CENTER_WIDTH/2+x0)*(1.0/alfa-1.0));
		y0+=(int) ((-CENTER_HEIGHT/2+y0)*(1.0/alfa-1.0));
	}

	@Override
	public void rotate(double df){

		fi+=df;
		sinf=Math.sin(fi);
		cosf=Math.cos(fi);

	}
	@Override
	public void fillPolygon(Graphics graph, Polygon3D pol3D) {
		graph.fillPolygon(builProjectedPolygon(pol3D));
	}
	@Override
	public void selectPolygons(int x, int y, Block[] blocks) {

		int layer=editor.getSelectedLayer();
		selectPolygons(x,y,blocks,true,layer);
	}
	@Override
	public  ArrayList<Block> getClickedPolygons(int x, int y, Block[] blocks) {

		int layer=editor.getSelectedLayer();
		ArrayList<Block> ret=selectPolygons(x,y,blocks,false,layer);
		return ret;
	}

	private  ArrayList<Block> selectPolygons(int x, int y,Block[] blocks,boolean toSelect,int layer) {

		ArrayList<Block> ret=new  ArrayList<Block>();

		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];

			if(bl.getLayer()!=layer) {
				continue;
			}

			boolean clicked=isPolygonClicked(x,y,bl);

			if(clicked){
				if(toSelect){
					editor.setCellPanelData(bl);
					bl.setSelected(true);
				}else{
					ret.add(bl);
				}
			}
			else
				if(!editor.checkMultiselection.isSelected()){
					if(toSelect){
						bl.setSelected(false);
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
			DPoint3D center=Polygon3D.findCentroid(pol3D);
			Polygon3D.rotate(pol3D,center,dro.getRotation_angle());
			Polygon3D poly = builProjectedPolygon(pol3D);

			if(poly.contains(x,y)){
				return true;
			}
		}
		return false;
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