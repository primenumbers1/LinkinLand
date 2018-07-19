package primenumbers.main;

import java.util.Comparator;

import primenumbers.Point3D;
import primenumbers.scene.Block;

public class BlockComparator implements Comparator<Block> {

	double playerPosX=0;
	double playerPosY=0;

	public BlockComparator(double playerPosX, double playerPosY) {
		this.playerPosX = playerPosX;
		this.playerPosY = playerPosY;
	}

	@Override
	public int compare(Block o1, Block o2) {

		if(o1==null && o2!=null) {
			return 1;
		} else if(o1!=null && o2==null) {
			return -1;
		} else if(o1==null && o2==null) {
			return 0;
		}

		double d1=Point3D.squareDistance(playerPosX, playerPosY, 0, o1.x, o1.y, o1.z);
		double d2=Point3D.squareDistance(playerPosX, playerPosY, 0, o2.x, o2.y, o2.z);

		if(d1>d2) {
			return 1;
		} else if(d1<d2) {
			return -1;
		} else {
			return 0;
		}
	}

}