package primenumbers;

import java.io.File;

import javax.sound.sampled.Clip;

import primenumbers.main.Game;
import primenumbers.main.loader.GameLoader;
import primenumbers.sound.GameSounds;

public class Monster extends DrawObject {

	private static final int SPAWNING_DISTANCE = 5000;
	private static final double ACTIVATION_DISTANCE_EASY=3000.0;
	private static final double ACTIVATION_DISTANCE_NORMAL=5000.0;
	private static final double ACTIVATION_DISTANCE_HARD=10000.0;
	private double activation_distance=ACTIVATION_DISTANCE_EASY;

	private double  startAngle=0;
	private boolean findTarget=false;

	private static final int MAXIMUM_LIFE_EASY=1;
	private static final int MAXIMUM_LIFE_NORMAL=2;
	private static final int MAXIMUM_LIFE_HARD=4;
	private int maximum_life=MAXIMUM_LIFE_EASY;

	private static final double RESURRECTION_PROBABILITY_EASY=0.999;
	private static final double RESURRECTION_PROBABILITY_NORMAL=0.9;
	private static final double RESURRECTION_PROBABILITY_HARD=0.8;
	public static final double REACH = 100;
	private double resurrection_probability=RESURRECTION_PROBABILITY_EASY;

	private int life=maximum_life;

	private ScreamingThread screaming=null;
	private Clip screamingClip=null;
	boolean isScreamingAllowed=true;

	public Monster(){
		//DO NOTHING
	}

	public void setMonsterData(GameLoader gameLoader) {
		int gameLevel=gameLoader.getGameLevel();
		if(gameLevel==Game.LEVEL_EASY){
			maximum_life=MAXIMUM_LIFE_EASY;
			life=maximum_life;
			activation_distance=ACTIVATION_DISTANCE_EASY;
			resurrection_probability=RESURRECTION_PROBABILITY_EASY;
		}else if(gameLevel==Game.LEVEL_NORMAL){
			maximum_life=MAXIMUM_LIFE_NORMAL;
			life=maximum_life;
			activation_distance=ACTIVATION_DISTANCE_NORMAL;
			resurrection_probability=RESURRECTION_PROBABILITY_NORMAL;
		}
		else if(gameLevel==Game.LEVEL_HARD){
			maximum_life=MAXIMUM_LIFE_HARD;
			life=maximum_life;
			activation_distance=ACTIVATION_DISTANCE_HARD;
			resurrection_probability=RESURRECTION_PROBABILITY_HARD;
		}
		if(isScreamingAllowed) {
			screamingClip=GameSounds.getClip(GameSounds.SOUNDS_DIRECTORY+""+File.separator+"groaning_"+getIndex()+".wav");
		}
	}

	@Override
	public void update(Game game, boolean animate) {

		if(!isVisible()){
			updateScreaming(false);
			resurrection();
			return;
		}

		updatePosition(game);

		if(animate) {
			updateActiveMesh();
		}
	}


	private void updateScreaming(boolean isScreaming) {

		if(!isScreamingAllowed){
			return;
		}

		if(isScreaming){
			if(screaming==null || !screaming.isAlive()){
				screaming=new ScreamingThread(screamingClip);
				screaming.setScreaming(true);
				screaming.start();
			}
		}else{
			if(screaming!=null) {
				screaming.setScreaming(false);
			}
		}
	}


	private void updatePosition(Game game) {

		///calculate movement data
		double speed=10.0;

		CubicMesh cm0=(CubicMesh) getMeshes()[0];
		DPoint3D center=cm0.findCentroid();

		DPoint3D distance=new DPoint3D(game.POSX-center.x ,game.POSY-center.y,0);
		DPoint3D objOrientation=new DPoint3D(Math.cos(Math.PI*0.5+getRotation_angle()),Math.sin(Math.PI*0.5+getRotation_angle()),0);

		if(DPoint3D.calculateNorm(distance)<activation_distance) {
			findTarget=true;
			updateScreaming(true);
		}else{
			updateScreaming(false);
		}
		double len=DPoint3D.calculateNorm(distance);
		distance=distance.calculateVersor();

		double max_angle=0.05;
		double diff=0;

		if(findTarget){

			if(len>1){
				DPoint3D  dotProduct=DPoint3D.calculateCrossProduct(objOrientation, distance);
				/*System.out.println(rotation_angle);
				System.out.println(objOrientation);
				System.out.println(distance);
				System.out.println(dotProduct);
				System.out.println("-------------");*/
				if(Math.abs(dotProduct.z)>0.1) {
					diff=Math.signum(dotProduct.z)*max_angle;
				}
			}
			else{
				diff=0;
			}
		}
		setRotation_angle(getRotation_angle()+diff);

		for (int i = 0; i < MESHES_SIZE; i++) {

			CubicMesh cm=(CubicMesh) getMeshes()[i];
			if(cm!=null){
				cm.rotate(center.x,center.y,Math.cos(diff),Math.sin(diff));
				cm.translate(speed*distance.x,speed*distance.y,0);
				if(i==0) {
					calculateBorder();
				}
			}
		}
	}

	private void resurrection() {

		double val=Math.random();

		if(val>resurrection_probability){

			setVisible(true);
			life=maximum_life;
			System.out.println("resurrection!");

			double r=SPAWNING_DISTANCE;
			double angle=Math.PI*Math.random();

			double rx=r*Math.cos(angle);
			double ry=r*Math.sin(angle);

			for (int i = 0; i < MESHES_SIZE; i++) {

				CubicMesh cm=(CubicMesh) getMeshes()[i];
				if(cm!=null){
					cm.translate(rx,ry,0);
				}
			}
		}
	}

	public double getStartAngle() {
		return startAngle;
	}

	public void setStartAngle(double startAngle) {
		this.startAngle = startAngle;
	}

	public int getLife() {
		return life;
	}

	public void setLife(int life) {
		this.life = life;
	}

	public int hit(){
		life--;
		return life;
	}

	public class ScreamingThread extends Thread{

		Clip scream=null;
		boolean isScreaming=false;
		static final int waitTime=5*1000;
		int delay=0;

		public ScreamingThread(Clip scream) {
			super();
			this.scream = scream;
			delay=(int) (waitTime*Math.random());
		}


		@Override
		public void run() {

			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			while(isScreaming){
				GameSounds.makeSoundLoop(scream,0);
				try {
					Thread.sleep(waitTime);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

		public boolean isScreaming() {
			return isScreaming;
		}

		public void setScreaming(boolean isScreaming) {
			this.isScreaming = isScreaming;
		}


	}
	
}