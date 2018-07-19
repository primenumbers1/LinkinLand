package primenumbers.main;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.CountDownLatch;

import javax.sound.sampled.Clip;

import primenumbers.CubicMesh;
import primenumbers.DPoint2D;
import primenumbers.DPoint3D;
import primenumbers.DrawObject;
import primenumbers.Engine;
import primenumbers.LineData;
import primenumbers.Monster;
import primenumbers.Point3D;
import primenumbers.Polygon3D;
import primenumbers.PolygonMesh;
import primenumbers.Texture;
import primenumbers.ZBuffer;
import primenumbers.main.gameover.GameStatistics;
import primenumbers.main.loader.GameLoader;
import primenumbers.scene.Block;
import primenumbers.scene.Scene;
import primenumbers.scene.Weapon;
import primenumbers.sound.GameSounds;

public class Game extends Renderer3D {

	private static final int PLAYER_POLYGON_SIDE=125;

	private static final int WALL_BOTTOM_FACE_INDEX = 0;

	private Scene scene=null;

	private int LONGITUDINAL_SPEED=0;
	private int LATERAL_SPEED=0;

	private int MAX_LIFE=100;
	private int life=MAX_LIFE;

	private Weapon[] weapons=null;
	private int selected_weapon=0;

	private Clip footSteps=null;

	boolean crouch;

	private double tanp=Math.tan(Math.PI/3.0);
	private double i_2pi=1.0/(Math.PI*2.0);

	private int animation_cycle=0;
	private int ANIMATION_FRAME_RATE=4;

	public String map_name=GameLoader.DEFAULT_MAP;

	private MainFrame mainFrame=null;

	private boolean CHEAT_FREEZE=false;
	private boolean CHEAT_GOD=false;
	private boolean CHEAT_GHOST=false;

	private GameStatistics gameStatistics=null;

	private double BARYCENTRIC_HEIGHT=300;

	private double currentZ=0;
	private double zSpeed=0;

	public static final int LEVEL_EASY=0;
	public static final int LEVEL_NORMAL=1;
	public static final int LEVEL_HARD=2;

	public Game(){}

	public Game(MainFrame mainFrame, String[] args) {

		this.mainFrame=mainFrame;
		gameStatistics=new GameStatistics();
		applyCheats(args);

		HEIGHT=mainFrame.HEIGHT;
		WIDTH=mainFrame.WIDTH;
		this.map_name=mainFrame.getMapName();

		roadZbuffer=new ZBuffer(WIDTH*HEIGHT);
		roadZbuffer2=new ZBuffer(WIDTH*HEIGHT);

		YFOCUS=HEIGHT/2;
		XFOCUS=WIDTH/2;

		scene=new Scene(this);
		scene.init();
		scene.deleteEmptyBlocks();

		weapons=Weapon.loadWeapons();
		footSteps=GameSounds.getClip(GameSounds.SOUNDS_DIRECTORY+""+File.separator+"footsteps.wav");

		observerPoint=new DPoint3D(0,0,0);

		lightPoint=new LightSource(
				new DPoint3D(0,0,0),
				new DPoint3D(0,1,0)
				);

		calculateShadowCosines();
		changeWeapon(0);

		POSX=scene.getStartx();
		POSY=scene.getStarty();

		MOVZ=YFOCUS;

		buildNewZBuffers();
	}

	private void calculateShadowCosines() {

		int sz=scene.objects.size();

		for(int i=0;i<sz;i++){

			DrawObject dro=scene.objects.get(i);
			PolygonMesh[] meshes = dro.getMeshes();

			for (int j = 0; j < meshes.length; j++) {

				if(meshes[j]!=null) {
					calculateShadowCosines((CubicMesh)meshes[j]);
				}
			}
		}

		for (int i = 0; i < weapons.length; i++) {

			if(weapons[i].getWeaponMesh()!=null) {
				calculateShadowCosines(weapons[i].getWeaponMesh());
			}
		}
	}

	void draw(BufferedImage buf) {
		drawScene();
		drawWeapon();
		buildScreen(buf);
	}

	private void drawWeapon() {

		CubicMesh cm=weapons[selected_weapon].getWeaponMesh();
		Texture texture=weapons[selected_weapon].getWeaponTexture();

		double rotateX = weapons[selected_weapon].getRotateX();

		double wy=POSY+SCREEN_DISTANCE*0.5-weapons[selected_weapon].getMoveY();
		double wx=POSX+Weapon.RIGHT_HAND;
		double wz=MOVZ-HEIGHT*0.5;

		CubicMesh weaponMesh = cm.clone();
		weaponMesh.translate(wx,wy,wz);
		if(rotateX!=0){
			weaponMesh.rotateX(wy, wz, Math.cos(rotateX), Math.sin(rotateX));
		}

		weaponMesh.rotate(POSX,POSY,MOVZ,viewDirectionCos,viewDirectionSin,sightLatDirectionCos,sightLatDirectionSin);
		decomposeCubicMesh(weaponMesh,Color.GRAY,texture,null,roadZbuffer);
	}


	private void decomposeParallel() {

		try {
			CountDownLatch startSignal = new CountDownLatch(1);
			CountDownLatch doneSignal = new CountDownLatch(2);

			Arrays.sort(scene.map.blocks,new BlockComparator(POSX,POSY));
			Collections.sort(scene.objects,new ObjectsComparator(POSX,POSY));

			new Thread(new SceneThread(roadZbuffer,0,startSignal, doneSignal)).start();
			new Thread(new SceneThread(roadZbuffer2,1,startSignal, doneSignal)).start();

			startSignal.countDown();
			doneSignal.await();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private void drawScene() {

		if(scene.map==null) {
			return ;
		}

		drawSky();

		decomposeParallel();
	}


	/**
	 *  alfa = scale factor:
	 *  2PI:(tetam*2.0)=dw:(alfa*WIDTH)
	 *  i:WIDTH=teta:(tetam*2.0) 0<i<WIDTH, i screen x point
	 *  i_set:dw=teta:2PI i_set background x point
	 *
	 *  texture and screen with y axis downward oriented:
	 *
	 *  j=0 -> j_set= dh- ((HEIGHT-YFOCUS)*alfa)
	 *  j=(HEIGHT-YFOCUS) -> j_set= dh
	 */
	private void drawSky() {

		double YBASE=YFOCUS-SCREEN_DISTANCE*Math.tan(sightLatDirection);

		int dw=Scene.background.getWidth();
		int dh=Scene.background.getHeight();

		double tetam=Math.atan(WIDTH/(2.0*SCREEN_DISTANCE));

		//actual value: 0,738
		double alfa=tetam*dw/(Math.PI*WIDTH);

		int deltah=(int) (dh-(HEIGHT-YBASE)*alfa);//(int) (dh*(1-alfa)+YFOCUS*alfa);

		double eta=tetam*2.0/WIDTH;


		for(int i=0;i<WIDTH;i++){

			double teta=i*eta-viewDirection;
			//double teta=(tetam-Math.atan((WIDTH/2.0-i)*i_d))-viewDirection-rearAngle;

			if(teta<0) {
				teta=teta+2*Math.PI;
			}
			if(teta>2*Math.PI) {
				teta=teta-2*Math.PI;
			}

			int i_set=(int) (dw*teta*i_2pi);

			for(int j=0;j<(HEIGHT-YBASE);j++){

				int tot=i+j*WIDTH;
				int j_set=(int) (alfa*j+deltah);

				int rgb=Texture.getRGB(Scene.background,null,i_set,j_set);
				if(tot<roadZbuffer.getSize()) {
					roadZbuffer.setRgbColor(rgb,tot);
				}
			}
		}
	}

	void up() {

		int NEW_POSX=(int) (POSX-LONGITUDINAL_SPEED*viewDirectionSin+LATERAL_SPEED*viewDirectionCos);
		int NEW_POSY=(int) (POSY+LONGITUDINAL_SPEED*viewDirectionCos+LATERAL_SPEED*viewDirectionSin);

		if(checkIsWayFree(NEW_POSX,NEW_POSY)){

			POSX=NEW_POSX;
			POSY=NEW_POSY;
		}

		double transZ=getZLevel(POSX,POSY,currentZ+BARYCENTRIC_HEIGHT);
		currentZ+=5.0*Engine.dt*0.01*zSpeed;

		if(transZ<currentZ){
			zSpeed+=-9.8*Engine.dt*0.01;
		}else{
			currentZ=transZ;
			zSpeed=0;
		}
		MOVZ=YFOCUS+(int)currentZ;

		boolean animate=animation_cycle>0 && animation_cycle%ANIMATION_FRAME_RATE==0;

		updateObjects(animate);

		if(animate) {
			animation_cycle=0;
		}
		animation_cycle++;

		checkGameStatus();
	}


	private void checkGameStatus() {

		if(life<=0) {
			mainFrame.gameOver(gameStatistics);
		}
	}

	/**
	 * 	  update objects if animated
	 * @param animate
	 *
	 */
	private void updateObjects(boolean animate) {

		if(isCHEAT_FREEZE()) {
			return;
		}

		int szo=scene.objects.size();

		for (int i = 0; i < szo; i++) {

			DrawObject dro=scene.objects.get(i);
			dro.update(this,animate);
		}
	}

	private boolean checkIsWayFree(int nEW_POSX, int nEW_POSY) {

		if(scene.map==null) {
			return true;
		}

		Block[] blocks = scene.map.blocks;
		Polygon3D player=buildPlayerPolygon();

		for (int i = 0; i < blocks.length && !isCHEAT_GHOST(); i++) {

			Block bl = blocks[i];

			if(bl==null) {
				continue;
			}

			if(currentZ+BARYCENTRIC_HEIGHT>bl.getMaxZ(currentZ+BARYCENTRIC_HEIGHT)) {
				continue;
			} else if(currentZ+BARYCENTRIC_HEIGHT<bl.getMinZ(currentZ+BARYCENTRIC_HEIGHT)) {
				continue;
			}

			if(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL ||
					bl.getBlock_type()==Block.BLOCK_TYPE_GROUND ||
					bl.getBlock_type()==Block.BLOCK_TYPE_DOOR
					) {
				continue;
			}

			Polygon3D base = PolygonMesh.getBodyPolygon(bl.getMesh().points,bl.getInvertedLowerBase(),null);
			buildTransformedPolygon(base,nEW_POSX,nEW_POSY);

			DPoint3D intersection=getIntersection(player,base);

			if(intersection!=null) {
				return false;
			}
		}

		int szo=scene.objects.size();
		for (int i = 0; i < szo; i++) {
			DrawObject dro=scene.objects.get(i);
			if(!dro.isVisible()) {
				continue;
			}

			if(currentZ>dro.getMaxZ(currentZ)) {
				continue;
			}

			Polygon3D base = dro.getBorder().clone();
			buildTransformedPolygon(base,nEW_POSX,nEW_POSY);
			DPoint3D intersection=getIntersection(player,base);

			if((DrawObject.isMonster(dro.getIndex()))
					&& !isCHEAT_GOD()) {
				double distance=DPoint3D.calculateNorm(getPlayerDistanceFromObject(nEW_POSX,nEW_POSY,dro));
				if(distance<Monster.REACH) {
					hitPlayer();
				}
			}else if(intersection!=null){

				if(dro.getIndex()==DrawObject.AMMO_INDEX) {
					dro.setVisible(false);
					fillAmmo();
					return true;
				}else if(dro.getIndex()==DrawObject.FIRST_AID_KIT_INDEX) {
					dro.setVisible(false);
					fillLife();
					return true;
				}
				return false;
			}
		}

		return true;
	}



	static DPoint3D getPlayerDistanceFromObject(int POSX, int POSY, DrawObject dro) {
		CubicMesh cm0=(CubicMesh) dro.getMeshes()[0];
		DPoint3D center=cm0.findCentroid();
		DPoint3D distance=new DPoint3D(POSX-center.x ,POSY-center.y,0);
		return distance;
	}

	private void hitPlayer() {
		life--;
		mainFrame.showLife();
	}


	private void fillLife() {
		life+=MAX_LIFE;
		mainFrame.showLife();

	}

	private Polygon3D buildPlayerPolygon() {

		Polygon3D base=new Polygon3D();

		base.addPoint(-PLAYER_POLYGON_SIDE,-PLAYER_POLYGON_SIDE);
		base.addPoint(PLAYER_POLYGON_SIDE,-PLAYER_POLYGON_SIDE);
		base.addPoint(PLAYER_POLYGON_SIDE,PLAYER_POLYGON_SIDE);
		base.addPoint(-PLAYER_POLYGON_SIDE,PLAYER_POLYGON_SIDE);

		return base;
	}


	/***
	 *
	 * calculate the intersections between two polygons border
	 *
	 *
	 * @param playerPolygon
	 * @param objectBorder
	 * @return
	 */
	private DPoint3D getIntersection(Polygon playerPolygon, Polygon objectBorder) {

		//variables for calculus
		DPoint2D poly0=new DPoint2D();
		DPoint2D poly1=new DPoint2D();
		DPoint2D carP0=new DPoint2D();
		DPoint2D carP1=new DPoint2D();

		for (int i = 0; i < playerPolygon.npoints; i++) {

			poly0.x=playerPolygon.xpoints[i];
			poly0.y=playerPolygon.ypoints[i];

			poly1.x=playerPolygon.xpoints[(i+1)%playerPolygon.npoints];
			poly1.y=playerPolygon.ypoints[(i+1)%playerPolygon.npoints];


			for (int j = 0; j < objectBorder.npoints; j++) {

				carP0.x=objectBorder.xpoints[j];
				carP0.y=objectBorder.ypoints[j];

				carP1.x=objectBorder.xpoints[(j+1)%objectBorder.npoints];
				carP1.y=objectBorder.ypoints[(j+1)%objectBorder.npoints];


				DPoint3D pInter=calculateLineIntersection(poly0,poly1,carP0,carP1);

				if(pInter!=null) {
					return pInter;
				}
			}
		}
		return null;
	}

	private static DPoint3D calculateLineIntersection(DPoint2D p1,DPoint2D p2,DPoint2D p3,DPoint2D p4) {


		double det=(p4.y-p3.y)*(p2.x-p1.x)-(p4.x-p3.x)*(p2.y-p1.y);
		double ua=(p4.x-p3.x)*(p1.y-p3.y)-(p4.y-p3.y)*(p1.x-p3.x);
		double ub=(p2.x-p1.x)*(p1.y-p3.y)-(p2.y-p1.y)*(p1.x-p3.x);

		if(det==0) {
			return null;
		}

		double i_de=1.0/det;

		double xx=p1.x+ua*i_de*(p2.x-p1.x);
		double yy=p1.y+ua*i_de*(p2.y-p1.y);
		DPoint3D intersection=new DPoint3D(xx,yy,0);

		if (intersection.x < Math.min(p3.x,p4.x) || intersection.x  > Math.max(p3.x,p4.x)) {
			return null;
		}

		if (intersection.y < Math.min(p3.y,p4.y) || intersection.y  > Math.max(p3.y,p4.y)) {
			return null;
		}

		if (intersection.x < Math.min(p1.x,p2.x) || intersection.x  > Math.max(p1.x,p2.x)) {
			return null;
		}

		if (intersection.y < Math.min(p1.y,p2.y) || intersection.y  > Math.max(p1.y,p2.y)) {
			return null;
		}

		return intersection;
	}
	/**
	 *
	 * You should select the nearest hit object...
	 *
	 * **/
	private void checkShoot(int x,int y){


		gameStatistics.addShot();

		DrawObject nearestHit=null;
		double nearDistance=-1;

		for (int j = 0; j < scene.objects.size(); j++) {

			DrawObject dro=scene.objects.get(j);

			if(!dro.isVisible()) {
				continue;
			}

			CubicMesh cm=(CubicMesh) dro.getMesh();

			int polSize=cm.polygonData.size();
			for(int i=0;i<polSize;i++){

				LineData ld=cm.polygonData.get(i);

				Polygon3D pol3D=PolygonMesh.getBodyPolygon(cm.points,ld,null);
				buildTransformedPolygon(pol3D);

				pol3D=Polygon3D.clipPolygon3DInY(pol3D,(int) (SCREEN_DISTANCE*0.25));

				if(pol3D==null) {
					continue;
				}

				Polygon poly = builProjectedPolygon(pol3D);

				if(poly.contains(x,y)){

					double distance=Polygon3D.findCentroid(pol3D).y;

					if(nearestHit==null || distance<nearDistance) {
						nearestHit=dro;
						nearDistance=distance;
					}
					break;
				}
			}
		}
		if(nearestHit!=null &&
				(weapons[selected_weapon].getRange()==Weapon.INFINITE_RANGE ||
				nearDistance<=weapons[selected_weapon].getRange())
				) {
			gameStatistics.checkKilled(nearestHit);
		}

	}

	private Polygon builProjectedPolygon(Polygon3D p3d) {

		int size=p3d.npoints;

		int[] xx=new int[size];
		int[] yy=new int[size];

		for(int i=0;i<size;i++){

			double x=p3d.xpoints[i];
			double y=p3d.ypoints[i];
			double z=p3d.zpoints[i];

			xx[i]=(int) calculPerspX(x,y,z);
			yy[i]=(int) calculPerspY(x,y,z);
		}

		Polygon pol=new Polygon(xx,yy,size);
		return pol;
	}

	/**
	 * Check if block is visible
	 * */
	protected boolean isPointsOnScreen(Point3D[] tPoints) {

		double maxX=0;
		double maxY=0;
		double minX=0;
		double minY=0;
		for (int i = 0; i < tPoints.length; i++) {

			Point3D p = tPoints[i];
			double x0=(int)calculPerspX(p.x,p.y,p.z);
			double y0=(int)calculPerspY(p.x,p.y,p.z);

			if(i==0){
				maxX=x0;
				maxY=y0;
				minX=x0;
				minY=y0;
			}else{
				maxX=Math.max(x0,maxX);
				maxY=Math.max(y0,maxY);
				minX=Math.min(x0,minX);
				minY=Math.min(y0,minY);
			}
		}

		if(maxX<0 || minX>WIDTH || maxY<0 || minY>HEIGHT) {
			return false;
		}
		return true;
	}


	public int getLONGITUDINAL_SPEED() {
		return LONGITUDINAL_SPEED;
	}

	public void setLONGITUDINAL_SPEED(int lONGITUDINAL_SPEED) {
		LONGITUDINAL_SPEED = lONGITUDINAL_SPEED;
	}

	public int getLATERAL_SPEED() {
		return LATERAL_SPEED;
	}

	public void setLATERAL_SPEED(int lATERAL_SPEED) {
		LATERAL_SPEED = lATERAL_SPEED;
	}

	private boolean keepFire=false;
	void fire(MainFrame mainFrame) {

		FireThread ft=new FireThread(mainFrame);
		ft.start();
	}


	public void stopFire() {
		keepFire=false;
	}

	void cock() {
		if(!weapons[selected_weapon].isCocked()) {
			weapons[selected_weapon].cock();
		}
	}

	public boolean isCocked() {

		return weapons[selected_weapon].isCocked();
	}


	private void fillAmmo() {
		for (int i = 0; i < weapons.length; i++) {
			weapons[selected_weapon].fillAmmo();
		}
		mainFrame.updateTextPanels();
	}

	void reload(){
		weapons[selected_weapon].reload();
	}


	public Weapon getWeapon(){
		return weapons[selected_weapon];
	}

	void changeWeapon(int i) {

		weapons[selected_weapon].end();
		if(i<weapons.length) {
			selected_weapon=i;
			weapons[selected_weapon].start();
		}
	}

	void nextWeapon(int diff) {
		if(selected_weapon+diff>=0) {
			changeWeapon(selected_weapon+diff);
		}
	}

	void footSteps(boolean moving) {

		if(moving && !footSteps.isRunning()) {
			GameSounds.makeSoundLoop(footSteps,Clip.LOOP_CONTINUOUSLY);
		} else if(!moving) {
			GameSounds.stop(footSteps);
		}
	}



	private void applyCheats(String[] args) {

		if(args==null) {
			return;
		}

		for (int i = 0; i < args.length; i++) {
			if("freeze".equals(args[i])) {
				CHEAT_FREEZE=true;
			}else if("god".equals(args[i])) {
				CHEAT_GOD=true;
			}else if("ghost".equals(args[i])) {
				CHEAT_GHOST=true;
			}


		}
	}

	public GameStatistics getGameStatistics() {
		return gameStatistics;
	}


	private double getZLevel(int PX,int PY, double zBarycenter){

		double z=0;

		if(scene.map==null || isCHEAT_GHOST()) {
			return z;
		}

		Block[] blocks = scene.map.blocks;

		for (int i = 0; i < blocks.length ; i++) {

			Block bl = blocks[i];

			if(bl==null){
				continue;
			}

			if(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL ||
					bl.getBlock_type()==Block.BLOCK_TYPE_DOOR) {
				continue;
			}

			Polygon3D base = PolygonMesh.getBodyPolygon(bl.getMesh().points,bl.getInvertedLowerBase(),null);
			buildTransformedPolygon(base,PX,PY);

			if(Polygon3D.isIntersect(0,0,base.getBounds()) && base.contains(0,0)){
				if(currentZ+BARYCENTRIC_HEIGHT<bl.getMinZ(currentZ+BARYCENTRIC_HEIGHT)) {
					continue;
				}
				z=bl.getMaxZ(zBarycenter);
				//System.out.println(i+">>>z="+z+" currz="+(currentZ+BARYCENTRIC_HEIGHT)+", minz="+bl.getMinZ(currentZ+BARYCENTRIC_HEIGHT));
			}
		}
		return z;
	}

	public int getLIFE() {
		return life;
	}

	double getzSpeed() {
		return zSpeed;
	}

	void setzSpeed(double zSpeed) {
		this.zSpeed = zSpeed;
	}

	public MainFrame getMainFrame() {
		return mainFrame;
	}

	public boolean isCHEAT_GOD() {
		return CHEAT_GOD;
	}

	public boolean isCHEAT_GHOST() {
		return CHEAT_GHOST;
	}

	public boolean isCHEAT_FREEZE() {
		return CHEAT_FREEZE;
	}

	private void decomposeParallel(int index,ZBuffer ziBuffer) {

		Block[] blocks = scene.map.blocks;

		for (int i =0; i < blocks.length; i++) {

			if(i%2!=index) {
				continue;
			}

			Block bl = blocks[i];

			if(bl==null) {
				continue;
			}

			ArrayList<LineData> lines=bl.getPolygons();
			int blsz=  lines.size();

			Point3D[] tPoints=buildTrasformedPoints(bl.getMesh().points);

			if(!isPointsOnScreen(tPoints)) {
				continue;
			}

			for (int j = 0; j < blsz; j++) {

				LineData ld = lines.get(j);
				Polygon3D p3d = PolygonMesh.getBodyPolygon(tPoints,ld,null);
				//buildTransformedPolygon(p3d);
				Texture image=null;
				Texture decal=null;

				if(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL){

				}
				else if(bl.getBlock_type()==Block.BLOCK_TYPE_GROUND){

					if(j==WALL_BOTTOM_FACE_INDEX){
						image=Scene.worldTextures[p3d.getIndex()];
						if(p3d.getIndex1()>=0) {
							decal=Scene.worldDecals[p3d.getIndex1()];
						}
					}else{

						int max_ceiling=Scene.ceilingTextures.length;
						if(p3d.getIndex()<max_ceiling) {
							image=Scene.ceilingTextures[p3d.getIndex()];
						} else {
							image=Scene.ceilingTextures[0];
						}
					}
				}
				else if(bl.getBlock_type()==Block.BLOCK_TYPE_FULL_WALL ||
						bl.getBlock_type()==Block.BLOCK_TYPE_WINDOW ||
						bl.getBlock_type()==Block.BLOCK_TYPE_DOOR ||
						bl.getBlock_type()==Block.BLOCK_TYPE_GRID){

					image=Scene.wallTextures[p3d.getIndex()];
				}


				//scene.worldTextures[0]
				decomposeClippedPolygonIntoZBuffer(p3d,ZBuffer.fromHexToColor(p3d.getHexColor()),image,decal,ziBuffer);
			}
		}

		//objects
		int szo=scene.objects.size();

		for (int i = 0; i < szo; i++) {

			if(i%2!=index) {
				continue;
			}

			DrawObject dro=scene.objects.get(i);
			if(dro.isVisible()) {
				decomposeCubicMesh((CubicMesh) dro.getMesh(),ZBuffer.fromHexToColor(dro.getHexColor()),
						scene.objectTextures[dro.getIndex()],null,ziBuffer);
			}
		}
	}



	class SceneThread implements Runnable{

		private ZBuffer ziBuffer;
		private int index=0;
		private final CountDownLatch startSignal;
		private final CountDownLatch doneSignal;

		public SceneThread(ZBuffer roadZbuffer, int index,
				CountDownLatch startSignal, CountDownLatch doneSignal) {
			this.ziBuffer=roadZbuffer;
			this.index=index;
			this.startSignal = startSignal;
			this.doneSignal = doneSignal;
		}

		@Override
		public void run() {
			try {
				startSignal.await();
				decomposeParallel(index,ziBuffer);
				doneSignal.countDown();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

		}
	}



	public boolean isDrawSight() {
		return !weapons[selected_weapon].isHitWeapon();
	}

	class FireThread extends Thread{

		MainFrame mainFrame=null;

		public FireThread(MainFrame mainFrame) {
			this.mainFrame = mainFrame;
		}

		@Override
		public void run() {
			keepFire=true;
			while(keepFire){

				if(weapons[selected_weapon].fire(isCHEAT_GOD())) {

					int sight_x = mainFrame.sight_x;
					int sight_y = mainFrame.sight_y;
					checkShoot(sight_x,sight_y);
					mainFrame.updateTextPanels();
				}else{
					break;
				}
				if(weapons[selected_weapon].isAutomaticWeapon()){

					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

				}else{
					break;
				}
			}
			keepFire=false;
		}
	}
	
}