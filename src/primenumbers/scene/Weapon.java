package primenumbers.scene;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.Clip;

import primenumbers.CubicMesh;
import primenumbers.Monster;
import primenumbers.Texture;
import primenumbers.sound.GameSounds;

public class Weapon {

	private static final String WEAPONS_DIRECTORY="lib"+File.separator+"weapons"+File.separator+"";

	private int MAX_AMMO=0;
	public int AMMO=0;
	public int LOADED_AMMO=0;
	private int MAX_LOADED_AMMO=0;

	private Clip fire_audio=null;
	private String name="";

	private boolean cocked=true;

	private int moveY=0;
	private int recoil=0;
	private double rotateX=0;
	private double swing_angle;
	private double range;

	private boolean isHitWeapon=false;
	private boolean isAutomaticWeapon=false;
	private boolean isSoundLoop=false;

	private static Clip reload_audio=null;

	private CubicMesh weaponMesh=null;
	private Texture weaponTexture=null;

	private static final double ZERO_ANGLE=0.0;
	public static double INFINITE_RANGE=-1;

	public static final int RIGHT_HAND=200;

	static{
		reload_audio=GameSounds.getClip(GameSounds.SOUNDS_DIRECTORY+""+File.separator+"reload.wav");

	}

	public static Weapon[] loadWeapons(){

		Weapon[] weapons={

				new Weapon(100,100,15,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"gun.wav",
						"weapon_0.mesh","weapon_texture_0.jpg",20,ZERO_ANGLE,INFINITE_RANGE,true,false,"Gun"),
				new Weapon(50,100,8,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"shotgun.wav",
						"weapon_1.mesh","weapon_texture_1.jpg",20,ZERO_ANGLE,INFINITE_RANGE,false,false,"Shotgun"),
				new Weapon(60,200,6,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"revolver.wav",
						"weapon_2.mesh","weapon_texture_2.jpg",20,ZERO_ANGLE,INFINITE_RANGE,false,false,"Revolver"),
				new Weapon(0,0,0,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"slash.wav",
						"weapon_3.mesh","weapon_texture_3.jpg",-50,-Math.PI*0.25,Monster.REACH*4,false,false,"Ax"),
				new Weapon(0,0,0,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"slash.wav",
						"weapon_4.mesh","weapon_texture_4.jpg",-50,-Math.PI*0.25,Monster.REACH*4,false,false,"Baseball bat"),
				new Weapon(100,100,100,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"gun.wav",
						"weapon_5.mesh","weapon_texture_5.jpg",20,ZERO_ANGLE,INFINITE_RANGE,true,false,"Machine Gun"),
				new Weapon(0,0,0,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"saw_cut.wav",
						"weapon_6.mesh","weapon_texture_6.jpg",-50,0.0,Monster.REACH*4,false,true,"Chainsaw"),
				new Weapon(0,0,0,GameSounds.SOUNDS_DIRECTORY+""+File.separator+"slash.wav",
						"weapon_7.mesh","weapon_texture_7.jpg",-50,-Math.PI*0.25,Monster.REACH*4,false,false,"Sword"),


		};
		return weapons;


	}

	private Weapon(int MAX_AMMO,
			int AMMO,
			int MAX_LOADED_AMMO,
			String audio_file,
			String mesh_file,
			String texture_file,
			int recoil,
			double swing_angle,
			double range,
			boolean isAutomaticWeapon,
			boolean isSoundLoop,
			String name) {

		this.MAX_AMMO = MAX_AMMO;
		this.AMMO = AMMO;
		this.MAX_LOADED_AMMO = MAX_LOADED_AMMO;
		this.fire_audio = GameSounds.getClip(audio_file);
		this.name = name;
		this.recoil = recoil;
		this.swing_angle = swing_angle;
		this.range=range;
		this.isAutomaticWeapon=isAutomaticWeapon;
		this.isSoundLoop=isSoundLoop;

		if(MAX_AMMO<=0) {
			isHitWeapon=true;
		}

		try {
			if(mesh_file!=null){
				File wFile=new File(WEAPONS_DIRECTORY+mesh_file);
				weaponMesh=CubicMesh.loadMeshFromFile(wFile);
			}

			if(texture_file!=null){
				File tFile=new File(WEAPONS_DIRECTORY+texture_file);
				weaponTexture=new Texture(ImageIO.read(tFile));
			}
			cock();

		} catch (IOException e) {
			e.printStackTrace();
		}

		reload();
	}

	public void cock() {
		moveY=0;
		rotateX=ZERO_ANGLE;
		cocked=true;
	}

	private void uncock() {
		moveY=recoil;
		rotateX=swing_angle;
		cocked=false;
	}


	public boolean fire(boolean isGod){

		if(LOADED_AMMO>0 || isHitWeapon){

			if(!isGod || !isHitWeapon) {
				LOADED_AMMO--;
			}

			if(!isSoundLoop) {
				GameSounds.makeSound(fire_audio);
			}
			uncock();
			return true;
		}
		return false;
	}

	public void fillAmmo() {
		AMMO+=MAX_AMMO;
	}

	public boolean reload(){

		if(AMMO>0){

			int need=MAX_LOADED_AMMO-LOADED_AMMO;

			if(need>0 && need>AMMO){

				LOADED_AMMO+=AMMO;
				AMMO=0;
				GameSounds.makeSound(reload_audio);

				return true;
			}
			else if(need>0){

				AMMO-=need;
				LOADED_AMMO+=need;

				GameSounds.makeSound(reload_audio);
				return true;
			}
		}
		return false;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getMoveY() {
		return moveY;
	}

	public void setMoveY(int moveY) {
		this.moveY = moveY;
	}

	public boolean isCocked() {
		return cocked;
	}

	public void setCocked(boolean cocked) {
		this.cocked = cocked;
	}

	public Texture getWeaponTexture() {
		return weaponTexture;
	}

	public CubicMesh getWeaponMesh() {
		return weaponMesh;
	}

	public double getRotateX() {
		return rotateX;
	}

	public boolean isHitWeapon() {
		return isHitWeapon;
	}

	public double getRange() {
		return range;
	}

	public boolean isAutomaticWeapon() {
		return isAutomaticWeapon;
	}

	public boolean isSoundLoop() {
		return isSoundLoop;
	}

	public void start() {
		if(isSoundLoop()) {
			GameSounds.makeSoundLoop(fire_audio,Clip.LOOP_CONTINUOUSLY);
		}

	}

	public void end() {
		if(isSoundLoop()) {
			GameSounds.stop(fire_audio);
		}

	}
	
}