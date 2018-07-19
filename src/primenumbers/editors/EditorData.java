package primenumbers.editors;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import primenumbers.Texture;

public class EditorData {

	public static  Texture[] loadWallTextures(File[] files) throws IOException {

		Texture[] localWallTextures=null;
		int numFiles=files.length;

		ArrayList<File> vWallTextures=new ArrayList<File>();
		for (int index = 0; index < numFiles; index++) {
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("wall_texture_"+index+"_0.")){
					vWallTextures.add(files[i]);
				}
			}
		}
		localWallTextures=new Texture[vWallTextures.size()];
		for(int i=0;i<vWallTextures.size();i++){
			File file=vWallTextures.get(i);
			localWallTextures[i]=new Texture(ImageIO.read(file));
			String extension=getExtension(file.getName());
			loadMipMaps(localWallTextures[i],"lib"+File.separator+"wall_texture_"+i,extension);
		}
		return localWallTextures;
	}

	public static Texture[] loadWorldTextures(File[] files) throws IOException {

		Texture[] localWorldTextures=null;

		int numFiles=files.length;
		ArrayList<File> vRoadTextures=new ArrayList<File>();
		for (int index = 0; index < numFiles; index++) {
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("world_texture_"+index+"_0.")){
					vRoadTextures.add(files[i]);
				}
			}
		}
		localWorldTextures=new Texture[vRoadTextures.size()];
		for(int i=0;i<vRoadTextures.size();i++){
			File file=vRoadTextures.get(i);
			localWorldTextures[i]=new Texture(ImageIO.read(file));
			String extension=getExtension(file.getName());
			loadMipMaps(localWorldTextures[i],"lib"+File.separator+"world_texture_"+i,extension);
		}
		return localWorldTextures;
	}


	public static Texture[] loadWorldDecals(File[] files) throws IOException {

		Texture[] localWorldTextures=null;

		int numFiles=files.length;
		ArrayList<File> vRoadTextures=new ArrayList<File>();
		for (int index = 0; index < numFiles; index++) {
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("world_decal_"+index+"_0.")){
					vRoadTextures.add(files[i]);
				}
			}
		}
		localWorldTextures=new Texture[vRoadTextures.size()];
		for(int i=0;i<vRoadTextures.size();i++){
			File file=vRoadTextures.get(i);
			localWorldTextures[i]=new Texture(ImageIO.read(file));
			String extension=getExtension(file.getName());
			loadMipMaps(localWorldTextures[i],"lib"+File.separator+"world_decal_"+i,extension);
		}
		return localWorldTextures;
	}


	private static String getExtension(String fName) {
		int index=fName.lastIndexOf(".");
		return fName.substring(index+1);
	}

	public static BufferedImage[] loadWorldImages(File[] files) throws IOException {

		BufferedImage[] localWorldImages=null;

		int numFiles=files.length;
		ArrayList<File> vRoadTextures=new ArrayList<File>();
		for (int index = 0; index < numFiles; index++) {
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("world_texture_"+index+"_0.")){
					vRoadTextures.add(files[i]);
				}
			}
		}
		localWorldImages=new BufferedImage[vRoadTextures.size()];
		for(int i=0;i<vRoadTextures.size();i++){
			File file=vRoadTextures.get(i);
			localWorldImages[i]=ImageIO.read(file);
		}
		return localWorldImages;
	}

	public static BufferedImage[] loadWorldDecalImages(File[] files) throws IOException {

		BufferedImage[] localWorldDecalImages=null;

		int numFiles=files.length;
		ArrayList<File> vTextures=new ArrayList<File>();
		for (int index = 0; index < numFiles; index++) {
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("world_decal_"+index+"_0.")){
					vTextures.add(files[i]);
				}
			}
		}
		localWorldDecalImages=new BufferedImage[vTextures.size()];
		for(int i=0;i<vTextures.size();i++){
			File file=vTextures.get(i);
			localWorldDecalImages[i]=ImageIO.read(file);
		}
		return localWorldDecalImages;
	}

	public static BufferedImage[] loadWallmages(File[] files) throws IOException {

		BufferedImage[] localWallImages=null;

		int numFiles=files.length;
		ArrayList<File> vRoadTextures=new ArrayList<File>();
		for (int index = 0; index < numFiles; index++) {
			for(int i=0;i<files.length;i++){
				if(files[i].getName().startsWith("wall_texture_"+index+"_0.")){
					vRoadTextures.add(files[i]);
				}
			}
		}
		localWallImages=new BufferedImage[vRoadTextures.size()];
		for(int i=0;i<vRoadTextures.size();i++){
			File file=vRoadTextures.get(i);
			localWallImages[i]=ImageIO.read(file);
		}
		return localWallImages;
	}

	private static void loadMipMaps(Texture texture, String fileBase,String extension) throws IOException {


		File mipFile = new File(fileBase+"_1."+extension);
		if(mipFile.exists()){

			Texture mipTexture = new Texture(ImageIO.read(new File(fileBase+"_1."+extension)));
			texture.setMipTexture1(mipTexture);
		}

		mipFile = new File(fileBase+"_2."+extension);
		if(mipFile.exists()){

			Texture mipTexture = new Texture(ImageIO.read(new File(fileBase+"_2."+extension)));
			texture.setMipTexture2(mipTexture);
		}

		mipFile = new File(fileBase+"_3."+extension);
		if(mipFile.exists()){

			Texture mipTexture = new Texture(ImageIO.read(new File(fileBase+"_3."+extension)));
			texture.setMipTexture3(mipTexture);
		}

	}
	
}