package primenumbers.editors.scene.panels;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JPanel;

import primenumbers.DrawObject;
import primenumbers.LineData;
import primenumbers.Point3D;
import primenumbers.Polygon3D;
import primenumbers.editors.scene.SceneEditor;
import primenumbers.scene.Block;
import primenumbers.scene.Scene;

public abstract class EditorPanel extends JPanel {

	SceneEditor editor=null;

	int CENTER_HEIGHT=0;
	int CENTER_WIDTH=0;

	boolean hide_objects=false;

	protected int xMovement=0;
	protected int yMovement=0;

	protected HashMap<Integer,BufferedImage> grayImages=new HashMap<Integer,BufferedImage>();


	EditorPanel(SceneEditor editor, int cENTER_WIDTH,int cENTER_HEIGHT) {
		super();
		this.editor=editor;
		CENTER_HEIGHT = cENTER_HEIGHT;
		CENTER_WIDTH = cENTER_WIDTH;
	}



	public abstract void drawScene(Scene scene,int layer,Graphics graph );


	public abstract Polygon3D builProjectedPolygon(LineData ld, Point3D[] points);

	public abstract Polygon3D builProjectedPolygon(Polygon3D p3d);

	public abstract void translate(int i, int j);

	public abstract void zoomOut();

	public abstract void zoomIn();

	public abstract void rotate(double df);

	public void fillPolygon(Graphics graph, Polygon3D pol3D) {

		graph.fillPolygon(builProjectedPolygon(pol3D));

	}

	public abstract boolean selectObject(int x, int y, DrawObject dro);

	public abstract void selectPolygons(int x, int y,Block[] blocks);

	public ArrayList<Block> selectPolygons(int x, int y,Block[] blocks,boolean isSelect){return new  ArrayList<Block>();};

	public abstract  ArrayList<Block> getClickedPolygons(int x, int y,Block[] blocks);

	public abstract boolean isPolygonClicked(int x, int y,Block blocks);

	public abstract void selectObjects(int x, int y,ArrayList<DrawObject> objects);

	public abstract int calcAssX(double x, double y, double z);

	public abstract int calcAssY(double x, double y, double z);

	public abstract int calcAssInverseX(double x, double y, double z);

	public abstract int calcAssInverseY(double x, double y, double z);

	public abstract void translateAxes(int i, int j);


	public boolean isHide_objects() {
		return hide_objects;
	}

	public void setHide_objects(boolean hide_objects) {
		this.hide_objects = hide_objects;
	}

	public abstract void changeMotionIncrement(int i);

	public void emptyGrayScale() {
		grayImages.clear();
	}
	
}