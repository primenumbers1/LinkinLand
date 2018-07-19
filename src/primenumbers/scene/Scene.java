package primenumbers.scene;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import primenumbers.BlocksMesh;
import primenumbers.CubicMesh;
import primenumbers.DPoint3D;
import primenumbers.DrawObject;
import primenumbers.Monster;
import primenumbers.Point3D;
import primenumbers.Texture;
import primenumbers.editors.Editor;
import primenumbers.editors.EditorData;
import primenumbers.main.Game;
import primenumbers.main.loader.GameLoader;

public class Scene {

	public BlocksMesh map=null;
	public ArrayList<DrawObject> objects=null;

	private Game game=null;
	private CubicMesh[][] object3D;
	public Texture[] objectTextures;
	public static Texture[] wallTextures;
	public static  Texture[] worldTextures;
	public static Texture[] worldDecals;
	public static Texture[] ceilingTextures;
	public static Texture background=null;

	public int startx=0;
	public int starty=0;

	public Scene(Game game){
		this.game=game;
		objects=new ArrayList<DrawObject>();
	}

	public void init(){

		GameLoader gameLoader = game.getMainFrame().getGameLoader();
		loadObjectMeshes();
		loadTextures();
		loadScene(gameLoader);
	}

	/**
	 * Translating elements at the start spares a lot of calculus simplifying the formulas
	 *  from (z-YFOCUS)*SCREEN_DISTANCE/y)+YFOCUS to (z*SCREEN_DISTANCE/y)+YFOCUS
	 *  y translation necessary to see the whole scene at the start.
	 * @param gameLoader
	 *
	 */

	private void loadScene(GameLoader gameLoader) {

		Scene sc = Editor.loadScene(new File("lib"+File.separator+"scene_"+game.map_name));

		for (int i = 0; i < sc.objects.size(); i++) {

			DrawObject dro=sc.objects.get(i);
			if(dro instanceof Monster){
				((Monster)dro).setMonsterData(gameLoader);
			}

			//rotate using a fixed centroid, that on the start:
			DPoint3D center=null;

			for(int k=0;k<DrawObject.MESHES_SIZE;k++){

				if(object3D[dro.getIndex()][k]==null) {
					continue;
				}
				CubicMesh cm=object3D[dro.getIndex()][k].clone();

				DPoint3D point = cm.point000;

				double dx=-point.x+dro.x;
				double dy=-point.y+dro.y;
				double dz=-point.z+dro.z;

				cm.translate(dx,dy,dz);
				cm.translate(0,Game.SCREEN_DISTANCE,0);

				if(k==0) {
					center=cm.findCentroid();
				}

				if(dro.getRotation_angle()!=0) {
					cm.rotate(center.x,center.y,Math.cos(dro.getRotation_angle()),Math.sin(dro.getRotation_angle()));
				}
				dro.setMesh(cm,k);
			}
			addObject(dro);
		}


		if(sc.map!=null){

			Block[] blocks = sc.map.blocks;

			for (int i = 0; i < blocks.length; i++) {

				Block bl = blocks[i];

				for (int j = 0; j < bl.getMesh().points.length; j++) {

					Point3D p=bl.getMesh().points[j];
					//translate world in the point (XFOCUS,SCREEN_DISTANCE,YFOCUS)
					p.x=p.x;
					p.y=p.y+Game.SCREEN_DISTANCE;
					p.z=p.z;
				}
			}
		}

		this.startx=sc.startx;
		this.starty=sc.starty;

		this.map=sc.map;
	}

	private void loadObjectMeshes(){

		File directoryImg=new File("lib");
		File[] files=directoryImg.listFiles();

		int numFiles=files.length;

		ArrayList<File> v3DObjects=new ArrayList<File>();

		for (int i = 0; i < numFiles; i++) {
			File file=new File("lib"+File.separator+"object3D_"+i+"_0");
			if(file.exists()) {
				v3DObjects.add(file);
			}
		}

		object3D=new CubicMesh[v3DObjects.size()][DrawObject.MESHES_SIZE];
		objectTextures=new Texture[v3DObjects.size()];

		for(int i=0;i<v3DObjects.size();i++){

			try {
				object3D[i][0]=CubicMesh.loadMeshFromFile(new File("lib"+File.separator+"object3D_"+i+"_0"));

				boolean recenterMeshes=false;
				for(int k=1;k<DrawObject.MESHES_SIZE;k++){
					File f1=new File("lib"+File.separator+"object3D_"+i+"_"+k);
					if(f1.exists()){
						object3D[i][k]=CubicMesh.loadMeshFromFile(new File("lib"+File.separator+"object3D_"+i+"_"+k));
						recenterMeshes=true;
					}
				}

				if(recenterMeshes){
					DrawObject.recenterMeshes(object3D[i]);
				}

				objectTextures[i]=new Texture(ImageIO.read(
						new File("lib"+File.separator+"object3D_texture_"+i+".jpg")
						));
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	private void loadTextures() {

		File directoryImg=new File("lib");
		File[] files=directoryImg.listFiles();


		try {

			worldTextures=EditorData.loadWorldTextures(files);
			wallTextures=EditorData.loadWallTextures(files);
			worldDecals=EditorData.loadWorldDecals(files);

			ArrayList<File> vCeilingTextures=new ArrayList<File>();
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("ceiling_texture_")){
					vCeilingTextures.add(files[i]);
				}
			}

			ceilingTextures=new Texture[vCeilingTextures.size()];
			for(int i=0;i<vCeilingTextures.size();i++){
				File file=vCeilingTextures.get(i);
				ceilingTextures[i]=new Texture(ImageIO.read(file));
			}

			background=new Texture(ImageIO.read(new File("lib"+File.separator+"background.jpg")));

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	/**
	 * It sets empty blocks to null, to lower memory load
	 * on rendering
	 *
	 */
	public void deleteEmptyBlocks() {
		Block[] blocks = map.blocks;

		ArrayList<Block> blocksToSave=new ArrayList<Block>();
		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];

			if(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL){
				continue;
			}
			blocksToSave.add(bl);
		}

		Block[] newBlocks =new Block[blocksToSave.size()];
		for (int i = 0; i < blocksToSave.size(); i++) {
			newBlocks[i]=blocksToSave.get(i);
		}
		map.blocks=newBlocks;


	}

	public void addObject(DrawObject draw_object){
		objects.add(draw_object);
	}

	public int getStartx() {
		return startx;
	}

	public void setStartx(int startx) {
		this.startx = startx;
	}

	public int getStarty() {
		return starty;
	}

	public void setStarty(int starty) {
		this.starty = starty;
	}
	
}