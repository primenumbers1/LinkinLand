package primenumbers.editors;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import primenumbers.BlocksMesh;
import primenumbers.DrawObject;
import primenumbers.Monster;
import primenumbers.main.Renderer3D;
import primenumbers.scene.Block;
import primenumbers.scene.Scene;

public abstract class Editor extends JFrame implements ActionListener, MenuListener, MouseListener, MouseWheelListener, PropertyChangeListener, KeyListener, MouseMotionListener {

	protected JFileChooser fc= new JFileChooser();
	protected File currentDirectory=null;
	protected File currentFile=null;
	protected boolean redrawAfterMenu=false;

	public static final int MAX_STACK_SIZE=10;

	private void saveBlocks(BlocksMesh bm,PrintWriter pr) {

		try {

			pr.println("NX="+bm.getNumx());
			pr.println("NY="+bm.getNumy());
			pr.println("NZ="+bm.getNumz());
			pr.println("SIDE="+bm.getSide());
			pr.println("ZSIDE="+bm.getZside());
			pr.println("X0="+bm.getX0());
			pr.print("Y0="+bm.getY0());

			Block[] blocks = bm.blocks;

			for (int i = 0; i < blocks.length; i++) {

				pr.print("\nv=");
				Block bl = blocks[i];
				pr.print(decomposeBlock(i,bl));
			}

			for (int i = 0; i < Block.vt.length; i++) {
				pr.print("\nvt="+Block.vt[i][0]+" "+Block.vt[i][1]);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private String decomposeBlock(int i, Block bl) {

		String str=i+" ";
		str+=bl.getOldNz()+" ";

		for(int j=0;j<bl.getNz();j++){

			if(j>0) {
				str+="|";
			}
			str+=Integer.toString(bl.getTexture_index());
		}
		str+=" "+bl.getDecal_index();
		str+=" "+bl.getBlock_type();
		str+=" "+bl.getLayer();

		return str;
	}

	private static BlocksMesh loadBlocksFromFile(BufferedReader br) {
		try {

			int numx=0;
			int numy=0;
			int side=0;
			int zside=0;
			int numz=0;
			double x0=0;
			double y0=0;

			String str=null;
			boolean read=false;

			ArrayList<Block> aBlocks=new ArrayList<Block>();

			while((str=br.readLine())!=null){

				if(str.indexOf("#")>=0 || str.length()==0) {
					continue;
				}

				if(str.indexOf("map")>=0){
					read=!read;
					continue;
				}

				if(!read) {
					continue;
				}

				if(str.startsWith("v=")) {
					buildBlock(aBlocks,str.substring(2));
				} else if(str.startsWith("NX=")) {
					numx=Integer.parseInt(str.substring(3));
				} else if(str.startsWith("NY=")) {
					numy=Integer.parseInt(str.substring(3));
				} else if(str.startsWith("NZ=")) {
					numz=Integer.parseInt(str.substring(3));
				} else if(str.startsWith("SIDE=")) {
					side=Integer.parseInt(str.substring(5));
				} else if(str.startsWith("ZSIDE=")) {
					zside=Integer.parseInt(str.substring(6));
				} else if(str.startsWith("X0=")) {
					x0=Double.parseDouble(str.substring(3));
				} else if(str.startsWith("Y0=")) {
					y0=Double.parseDouble(str.substring(3));
				}
			}

			//calculate not visible faces

			HashMap<Integer,HashMap<String,String>> inVisible=new HashMap<Integer,HashMap<String,String>>();

			for (int i = 0; i < numx; i++) {
				for (int j = 0; j < numy; j++) {

					for (int kLayer = 0; kLayer < numz; kLayer++) {


						if(i<numx-1){

							int i00=(i+j*numx)+kLayer*numx*numy;
							int i10=(i+1+j*numx)+kLayer*numx*numy;

							Block vBlock00 = aBlocks.get(i00);
							Block vBlock10 = aBlocks.get(i10);

							if(vBlock00.getBlock_type()==vBlock10.getBlock_type() && vBlock00.getBlock_type()!=Block.BLOCK_TYPE_GRID
									){

								HashMap<String,String> inVisibleFaces00=inVisible.get(new Integer(i00));

								if(inVisibleFaces00==null) {
									inVisibleFaces00=new HashMap<String,String>();
								}

								inVisibleFaces00.put(Integer.toString(Renderer3D.CAR_RIGHT),"");
								inVisible.put(new Integer(i00),inVisibleFaces00);


								HashMap<String,String> inVisibleFaces10=inVisible.get(new Integer(i10));

								if(inVisibleFaces10==null) {
									inVisibleFaces10=new HashMap<String,String>();
								}

								inVisibleFaces10.put(Integer.toString(Renderer3D.CAR_LEFT),"");
								inVisible.put(new Integer(i10),inVisibleFaces10);

							}

						}
						//////////
						if(j<numy-1){

							int i00=(i+j*numx)+kLayer*numx*numy;
							int i01=(i+(j+1)*numx)+kLayer*numx*numy;

							Block vBlock00 = aBlocks.get(i00);
							Block vBlock01 = aBlocks.get(i01);

							if(vBlock00.getBlock_type()==vBlock01.getBlock_type() && vBlock00.getBlock_type()!=Block.BLOCK_TYPE_GRID
									){

								HashMap<String,String> inVisibleFaces00=inVisible.get(new Integer(i00));

								if(inVisibleFaces00==null) {
									inVisibleFaces00=new HashMap<String,String>();
								}

								inVisibleFaces00.put(Integer.toString(Renderer3D.CAR_FRONT),"");
								inVisible.put(new Integer(i00),inVisibleFaces00);


								HashMap<String,String> inVisibleFaces01=inVisible.get(new Integer(i01));

								if(inVisibleFaces01==null) {
									inVisibleFaces01=new HashMap<String,String>();
								}

								inVisibleFaces01.put(Integer.toString(Renderer3D.CAR_BACK),"");
								inVisible.put(new Integer(i01),inVisibleFaces01);

							}

						}
						///////////
						if(kLayer<numz-1){

							int i00=(i+j*numx)+kLayer*numx*numy;
							int i01=(i+j*numx)+(kLayer+1)*numx*numy;

							Block vBlock00 = aBlocks.get(i00);
							Block vBlock01 = aBlocks.get(i01);

							if(vBlock00.getBlock_type()==vBlock01.getBlock_type() && vBlock00.getBlock_type()!=Block.BLOCK_TYPE_GRID
									){

								HashMap<String,String> inVisibleFaces00=inVisible.get(new Integer(i00));

								if(inVisibleFaces00==null) {
									inVisibleFaces00=new HashMap<String,String>();
								}

								inVisibleFaces00.put(Integer.toString(Renderer3D.CAR_TOP),"");
								inVisible.put(new Integer(i00),inVisibleFaces00);


								HashMap<String,String> inVisibleFaces01=inVisible.get(new Integer(i01));

								if(inVisibleFaces01==null) {
									inVisibleFaces01=new HashMap<String,String>();
								}

								inVisibleFaces01.put(Integer.toString(Renderer3D.CAR_BOTTOM),"");
								inVisible.put(new Integer(i01),inVisibleFaces01);

							}

						}
						////////////

					}
				}
			}

			if(!aBlocks.isEmpty()){

				BlocksMesh bm=new BlocksMesh(numx,numy,numz,side,zside,x0,y0,0);

				int bsz=aBlocks.size();

				for (int i = 0; i <bsz;  i++) {
					Block vBlock = aBlocks.get(i);
					bm.blocks[i].setTexture_index(vBlock.getTexture_index());
					bm.blocks[i].setDecal_index(vBlock.getDecal_index());
					bm.blocks[i].setBlock_type(vBlock.getBlock_type());
					bm.blocks[i].setOldNz(vBlock.getOldNz());

					HashMap inVisibleFaces=inVisible.get(new Integer(i));

					bm.blocks[i].buildCubicMesh(inVisibleFaces);
				}
				return bm;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static void buildBlock(ArrayList<Block> aBlocks, String str) {

		String[] vals = str.split(" ");

		Block bl=new Block();

		int oldNz=Integer.parseInt(vals[1]);

		String texture_indexes=vals[2];

		StringTokenizer stk=new StringTokenizer(texture_indexes,"\\|");

		int index=Integer.parseInt(stk.nextToken());
		bl.setTexture_index(index);

		int decal_index=Integer.parseInt(vals[3]);
		bl.setDecal_index(decal_index);

		bl.setOldNz(oldNz);

		int block_type=Integer.parseInt(vals[4]);
		bl.setBlock_type(block_type);


		if(vals.length==6){
			int block_layer=Integer.parseInt(vals[5]);
			bl.setLayer(block_layer);
		}
		aBlocks.add(bl);
	}

	private static ArrayList<DrawObject> loadObjectsFromFile(BufferedReader br){

		ArrayList<DrawObject> drawObjects=new ArrayList<DrawObject>();

		try {

			boolean read=false;

			String str=null;
			int rows=0;
			while((str=br.readLine())!=null){

				if(str.indexOf("objects")>=0){
					read=!read;
					continue;
				}

				if(!read) {
					continue;
				}

				if(str.indexOf("#")>=0 || str.length()==0) {
					continue;
				}
				DrawObject dro=buildDrawObject(str);
				drawObjects.add(dro);
			}

			return drawObjects;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	private static DrawObject buildDrawObject(String str) {

		StringTokenizer tok=new StringTokenizer(str," ");

		double x=Double.parseDouble(tok.nextToken());
		double y=Double.parseDouble(tok.nextToken());
		double z=Double.parseDouble(tok.nextToken());
		int index=Integer.parseInt(tok.nextToken());
		double rotation_angle=Double.parseDouble(tok.nextToken());

		DrawObject dro=null;

		if(DrawObject.isMonster(index)){
			dro=new Monster();
		}else{
			dro=new DrawObject();
		}

		dro.x=x;
		dro.y=y;
		dro.z=z;
		dro.setIndex(index);
		dro.setRotation_angle(rotation_angle);

		if(dro instanceof Monster){
			((Monster)dro).setStartAngle(rotation_angle);
		}

		return dro;
	}



	private void saveObjects(ArrayList<DrawObject> drawObjects,PrintWriter pr ) {

		try {
			double anglePrecision=1000000;
			for(int i=0;i<drawObjects.size();i++){

				DrawObject dro=drawObjects.get(i);
				String rot=Double.toString(Math.round(dro.getRotation_angle()*anglePrecision)/anglePrecision);

				String str=dro.getX()+" "+dro.getY()+" "+dro.getZ()+" "+dro.getIndex()+" "+rot;
				pr.println(str);
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void saveOther(Scene scene, PrintWriter pr) {
		try {
			pr.println("STARTX="+scene.getStartx());
			pr.println("STARTY="+scene.getStarty());

		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void menuCanceled(MenuEvent arg0) {
	}

	@Override
	public void menuDeselected(MenuEvent arg0) {
		redrawAfterMenu=true;

	}

	@Override
	public void menuSelected(MenuEvent arg0) {
		redrawAfterMenu=false;

	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
	}

	public void saveScene(Scene scene) {

		fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setDialogTitle("Save scene");
		if(currentDirectory!=null) {
			fc.setCurrentDirectory(currentDirectory);
		}
		if(currentFile!=null) {
			fc.setSelectedFile(currentFile);
		}

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			currentDirectory=fc.getCurrentDirectory();
			currentFile=fc.getSelectedFile();
			saveScene(file,scene);
		}
	}

	protected void saveScene(File file, Scene scene) {

		PrintWriter pr;
		try {
			pr = new PrintWriter(new FileOutputStream(file));

			pr.println("<map>");
			saveBlocks(scene.map,pr);
			pr.println("</map>");
			pr.println("<objects>");
			saveObjects(scene.objects,pr);
			pr.println("</objects>");
			pr.println("<other>");
			saveOther(scene,pr);
			pr.println("</other>");
			pr.close();

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public static Scene loadScene(File file) {

		Scene scene=new Scene(null);

		try{

			BufferedReader br=new BufferedReader(new FileReader(file));
			scene.map=loadBlocksFromFile(br);
			br.close();

			br=new BufferedReader(new FileReader(file));
			scene.objects=loadObjectsFromFile(br);
			br.close();

			br=new BufferedReader(new FileReader(file));
			loadSceneDataFromFile(br,scene);
			br.close();

			//checkNormals();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return scene;
	}


	private static void loadSceneDataFromFile(BufferedReader br, Scene scene) {

		try {

			boolean read=false;

			String str=null;
			while((str=br.readLine())!=null){

				if(str.indexOf("other>")>=0){
					read=!read;
					continue;
				}

				if(!read) {
					continue;
				}

				if(str.indexOf("#")>=0 || str.length()==0) {
					continue;
				}

				if(str.startsWith("STARTX=")) {
					scene.startx=Integer.parseInt(str.substring(str.indexOf("=")+1));
				}
				if(str.startsWith("STARTY=")) {
					scene.starty=Integer.parseInt(str.substring(str.indexOf("=")+1));
				}
			}


		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {
	}

	@Override
	public void mouseMoved(MouseEvent e) {
	}


	public Scene cloneScene(Scene scene) {

		Scene scn=new Scene(null);
		scn.objects=cloneObjects(scene);

		if(scene.map!=null) {
			scn.map=scene.map.clone();
		}
		return scn;
	}

	public ArrayList<DrawObject> cloneObjects(Scene scene) {

		ArrayList<DrawObject> objs=new ArrayList<DrawObject>();

		for(int i=0;i<scene.objects.size();i++){
			DrawObject dro=scene.objects.get(i);
			objs.add((DrawObject) dro.clone());
		}

		return objs;
	}
	
}