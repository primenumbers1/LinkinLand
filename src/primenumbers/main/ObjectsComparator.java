package primenumbers.main;

import java.util.Comparator;

import primenumbers.DPoint3D;
import primenumbers.DrawObject;
import primenumbers.Polygon3D;

public class ObjectsComparator implements Comparator<DrawObject> {

	double playerPosX=0;
	double playerPosY=0;

	public ObjectsComparator(double playerPosX, double playerPosY) {
		this.playerPosX = playerPosX;
		this.playerPosY = playerPosY;
	}


	@Override
	public int compare(DrawObject o1, DrawObject o2) {
		Polygon3D base1 = o1.getBase();
		Polygon3D base2 = o2.getBase();

		DPoint3D center1 = Polygon3D.findCentroid(base1);
		DPoint3D center2 = Polygon3D.findCentroid(base2);

		double d1=DPoint3D.squareDistance(playerPosX, playerPosY, 0, center1.x, center1.y, 0);
		double d2=DPoint3D.squareDistance(playerPosX, playerPosY, 0, center2.x, center2.y, 0);

		if(d1>d2) {
			return 1;
		} else if(d1<d2) {
			return -1;
		} else {
			return 0;
		}

	}
	
}