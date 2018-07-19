package primenumbers.scene;

import java.io.File;

import primenumbers.BlocksMesh;
import primenumbers.editors.Editor;

public class SceneMigrator extends Editor {

	public static void main(String[] args) {
		SceneMigrator sm=new SceneMigrator();
		sm.migrate("hospital");
	}


	private void migrate(String map_name){

		File oldLib=new File("old_lib");
		File[] files = oldLib.listFiles();

		for(int f=0;f<files.length;f++){

			File file=files[f];

			Scene sc = Editor.loadScene(new File("old_lib"+File.separator+""+file.getName()));

			/*if(sc.map!=null){

				int numx = sc.map.getNumx();
				int numy = sc.map.getNumy();
				int numz = findOldNumZ(sc.map);
				sc.map.setNumz(numz);

				Block[] newBlocks=new Block[numx*numy*numz];

				Block[] blocks = sc.map.blocks;

				for (int i = 0; i < numx; i++) {

					for (int j = 0; j < numy; j++) {

						int pos=i+numx*j;

						Block block = blocks[pos];

						for (int k = Block.BLOCK_GROUND_LAYER; k < numz; k++) {

							int newPos=(i+j*numx)+k*numx*numy;



							if(block.getBlock_type()==Block.BLOCK_TYPE_GROUND){

								newBlocks[newPos]=(Block) block.clone();

								if(k>Block.BLOCK_GROUND_LAYER){

									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_EMPTY_WALL);
								}

							}else if(block.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL){

								newBlocks[newPos]=(Block) block.clone();

								//ceiling
								if(k==block.getOldNz()-1){
									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_FULL_WALL);
									newBlocks[newPos].setTexture_index(0);
								}
								else if(k==Block.BLOCK_GROUND_LAYER){
									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_GROUND);
								}


							}else if(block.getBlock_type()==Block.BLOCK_TYPE_FULL_WALL){

								newBlocks[newPos]=(Block) block.clone();
								if(k>=block.getOldNz()) {
									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_EMPTY_WALL);
								}

							}else if(block.getBlock_type()==Block.BLOCK_TYPE_WINDOW){

								newBlocks[newPos]=(Block) block.clone();

								if(k==Block.BLOCK_GROUND_LAYER || (k>=3 &&  k<block.getOldNz())){

									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_FULL_WALL);
								}else if(k>=block.getOldNz())
								{
									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_EMPTY_WALL);
								}

							}else if(block.getBlock_type()==Block.BLOCK_TYPE_DOOR){

								newBlocks[newPos]=(Block) block.clone();

								if(k==Block.BLOCK_GROUND_LAYER){
									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_GROUND);
									newBlocks[newPos].setTexture_index(0);
								}
								else if(k>=4 && k<block.getOldNz()){

									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_FULL_WALL);

								}else if(k>=block.getOldNz()){

									newBlocks[newPos].setBlock_type(Block.BLOCK_TYPE_EMPTY_WALL);
								}

							}

							newBlocks[newPos].setLayer(k);
						}




					}



				}

				sc.map.blocks=newBlocks;

			}*/

			currentFile=new File("lib"+File.separator+""+file.getName());
			saveScene(currentFile,sc);
			System.out.println("Migrated:"+file.getName());
		}

		System.out.println("MIGRATION DONE!");
	}


	private int findOldNumZ(BlocksMesh map) {

		Block[] blocks = map.blocks;
		int numx = map.getNumx();
		int numy = map.getNumy();

		int maxZ=0;

		for (int i = 0; i < numx; i++) {

			for (int j = 0; j < numy; j++) {
				int pos=i+numx*j;

				Block block = blocks[pos];
				if(block.getOldNz()>maxZ) {
					maxZ=block.getOldNz();
				}
			}
		}

		return maxZ;
	}
	
}