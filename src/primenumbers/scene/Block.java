package primenumbers.scene;

import java.util.ArrayList;
import java.util.HashMap;

import primenumbers.CubicMesh;
import primenumbers.DrawObject;
import primenumbers.LineData;
import primenumbers.Point3D;
import primenumbers.Polygon3D;
import primenumbers.PolygonMesh;
import primenumbers.main.Renderer3D;

public class Block extends DrawObject {

	private static final String HEX_GRAY_COLOR = "AAAAAA";
	private int nz=2;
	private int oldNz=0;

	public static final int zBlocks=5;
	public static final int side=200;
	public static final int zSide=200;

	private int layer=-1;

	public static final int BLOCK_TYPE_EMPTY_WALL=0;
	public static final int BLOCK_TYPE_FULL_WALL=1;
	public static final int BLOCK_TYPE_WINDOW=2;
	public static final int BLOCK_TYPE_GROUND=3;
	public static final int BLOCK_TYPE_DOOR=4;
	public static final int BLOCK_TYPE_GRID=5;

	public static final int BLOCK_GROUND_LAYER = 0;
	public static final int NO_DECAL_INDEX = -1;

	private int selectedZ=-1;

	private int block_type=BLOCK_TYPE_EMPTY_WALL;

	private int texture_index=0;
	private int decal_index=NO_DECAL_INDEX;

	private static final int[] faceNumber={

			Renderer3D.CAR_BACK,
			Renderer3D.CAR_TOP,
			Renderer3D.CAR_LEFT,
			Renderer3D.CAR_RIGHT,
			Renderer3D.CAR_FRONT,
			Renderer3D.CAR_BOTTOM
	};


	private static final int[][] faces={
			{0,1,5,4},//back
			{4,5,6,7},//top
			{3,0,4,7},//left
			{1,2,6,5},//right
			{2,3,7,6},//front
			{3,2,1,0} //bottom
	};

	public static final double[][] vt={
			{0,0},
			{200,0},
			{200,200},
			{0,200},
	};



	public Block(int block_type,double x,double y,double z,double dx,double dy,double dz,int layer) {
		super();
		setX(x);
		setY(y);
		setZ(z);
		setDx(dx);
		setDy(dy);
		setDz(dz);
		setLayer(layer);
		this.block_type = block_type;
		texture_index=0;
		buildCubicMesh();
		setHexColor(HEX_GRAY_COLOR);
	}


	public Block(){
		super();
	}

	public LineData getInvertedUpperBase() {
		//take the normal orientation into consideration...
		LineData ld=new LineData();

		int df=4*(nz);

		ld.addIndex(0+df,0,vt[0][0],vt[0][1]);
		ld.addIndex(3+df,0,vt[3][0],vt[3][1]);
		ld.addIndex(2+df,0,vt[2][0],vt[2][1]);
		ld.addIndex(1+df,0,vt[1][0],vt[1][1]);

		ld.setTexture_index(getTexture_index());
		ld.setDecal_index(decal_index);
		ld.setSelected(isSelected());
		return ld;
	}


	public LineData getInvertedLowerBase() {

		//take the normal orientation into consideration...
		LineData ld=new LineData();
		ld.addIndex(0,0,vt[0][0],vt[0][1]);
		ld.addIndex(1,0,vt[1][0],vt[1][1]);
		ld.addIndex(2,0,vt[2][0],vt[2][1]);
		ld.addIndex(3,0,vt[3][0],vt[3][1]);


		ld.setTexture_index(texture_index);
		ld.setDecal_index(decal_index);
		ld.setSelected(isSelected());
		return ld;
	}

	public void buildCubicMesh(){
		buildCubicMesh(null);
	}

	public void buildCubicMesh(HashMap<Integer,HashMap<String,String>> inVisibleFaces){

		PolygonMesh pm=new PolygonMesh();

		pm.points=new Point3D[2*2*nz];

		for (int k = 0; k <nz; k++) {

			double z=k*dz;

			Point3D p000=null;
			Point3D p010=null;
			Point3D p100=null;
			Point3D p110=null;

			if(block_type==BLOCK_TYPE_GRID){
				p000=new Point3D(x+dx*0.25,y+dy*0.25,z);
				p010=new Point3D(x+dx*0.25,y+dy*0.75,z);
				p100=new Point3D(x+dx*0.75,y+dy*0.25,z);
				p110=new Point3D(x+dx*0.75,y+dy*0.75,z);
			}else{
				p000=new Point3D(x,y,z);
				p010=new Point3D(x,y+dy,z);
				p100=new Point3D(x+dx,y,z);
				p110=new Point3D(x+dx,y+dy,z);
			}

			pm.points[0+k*4]=p000;
			pm.points[1+k*4]=p100;
			pm.points[2+k*4]=p110;
			pm.points[3+k*4]=p010;
		}

		for (int k = 0; k < nz; k++) {
			for (int i = 0; i < faces.length; i++) {

				if(block_type==BLOCK_TYPE_WINDOW){

					continue;
					/* Behavior to define:*/
				} else if(block_type==BLOCK_TYPE_FULL_WALL){
					if(i==Renderer3D.CAR_TOP && k!=nz-1) {
						continue;
					} else if (i==Renderer3D.CAR_BOTTOM && k!=0) {
						continue;
					}

				} else if(block_type==BLOCK_TYPE_DOOR){
					continue;
					/* Behavior to define:*/
				} else if(block_type==BLOCK_TYPE_GROUND){

					if(k==0 && i==Renderer3D.CAR_BOTTOM){

						LineData ld=getInvertedLowerBase();
						ld.setData(Integer.toString(faceNumber[Renderer3D.CAR_BOTTOM]));
						ld.setHexColor(getHexColor());
						pm.polygonData.add(ld);

					}
					continue;

				} else if(block_type==BLOCK_TYPE_GRID){

				}else if(block_type==BLOCK_TYPE_EMPTY_WALL){
					continue;
					/* Behavior to define:*/
				}

				if(inVisibleFaces!=null && inVisibleFaces.get(Integer.toString(i))!=null){
					continue;
				}

				LineData ld=new LineData();
				for (int j = 0; j < faces[i].length; j++) {
					int index=faces[i][j];
					ld.addIndex(index,0,vt[j][0],vt[j][1]);
				}

				ld.setData(Integer.toString(faceNumber[i]));
				ld.setData2(Integer.toString(k));
				ld.setHexColor(getHexColor());
				ld.setTexture_index(texture_index);
				ld.setDecal_index(decal_index);
				pm.polygonData.add(ld);
			}
		}

		meshes=new PolygonMesh[1];
		meshes[active_mesh]=CubicMesh.buildCubicMesh(pm);
		meshes[active_mesh].translate(x,y,z);
		if(block_type==BLOCK_TYPE_GRID) {
			meshes[active_mesh].translate(dx*0.25,dy*0.25,0);
		}
	}




	@Override
	public Object clone(){

		Block block=new Block();
		block.setX(getX());
		block.setY(getY());
		block.setZ(getZ());
		block.setDx(getDx());
		block.setDy(getDy());
		block.setDz(getDz());
		block.setNz(getNz());
		block.setTexture_index(getTexture_index());
		block.setDecal_index(getDecal_index());
		block.setHexColor(getHexColor());
		block.setBlock_type(getBlock_type());
		block.setLayer(layer);
		block.setOldNz(oldNz);

		for(int i=0;i<polygons.size();i++){

			Polygon3D polig=polygons.get(i);
			block.addPolygon(polig.clone());
		}
		block.setMesh(getMesh().clone(),block.active_mesh);

		return block;

	}
	@Override
	public ArrayList getPolygons(){
		return getPolygons(true);
	}

	public ArrayList<LineData> getPolygons(boolean ceiling){

		ArrayList<LineData> lines=new ArrayList<LineData>();

		//for the editor
		if(block_type==BLOCK_TYPE_EMPTY_WALL && layer==BLOCK_GROUND_LAYER){

			if(!ceiling) {
				lines.add(getInvertedLowerBase());
			} else {
				lines=getMesh().polygonData;
			}
		}
		else if(block_type==BLOCK_TYPE_GROUND ||
				block_type==BLOCK_TYPE_FULL_WALL ||
				block_type==BLOCK_TYPE_WINDOW ||
				block_type==BLOCK_TYPE_DOOR ||
				block_type==BLOCK_TYPE_GRID){
			lines=getMesh().polygonData;
		}
		return lines;
	}

	public int getBlock_type() {
		return block_type;
	}

	public void setBlock_type(int block_type) {
		this.block_type = block_type;
	}

	@Override
	public double getMaxZ(double zLevel) {

		if(block_type==BLOCK_TYPE_GROUND){
			return 0;
		}else if(block_type==BLOCK_TYPE_DOOR || block_type==BLOCK_TYPE_EMPTY_WALL
				){

			if(zLevel>(layer+1)*zSide) {
				return (layer+1)*zSide;
			} else {
				return (layer)*zSide;
			}

		}else{
			return (layer+1)*zSide;
		}
	}

	public double getMinZ(double zLevel) {

		if(block_type==BLOCK_TYPE_GROUND){
			return 0;

		}else if(block_type==BLOCK_TYPE_DOOR || block_type==BLOCK_TYPE_EMPTY_WALL
				){
			return layer*zSide;
		}else{
			return layer*zSide;
		}
	}

	public int getSelectedZ() {
		return selectedZ;
	}

	public void setSelectedZ(int selectedZ) {
		this.selectedZ = selectedZ;
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	public int getTexture_index() {
		return texture_index;
	}

	public void setTexture_index(int texture_index) {
		this.texture_index = texture_index;
	}

	public int getNz() {
		return nz;
	}

	public void setNz(int nz) {
		this.nz = nz;
	}


	public int getOldNz() {
		return oldNz;
	}

	public void setOldNz(int oldNz) {
		this.oldNz = oldNz;
	}


	public int getDecal_index() {
		return decal_index;
	}


	public void setDecal_index(int decal_index) {
		this.decal_index = decal_index;
	}
	
}