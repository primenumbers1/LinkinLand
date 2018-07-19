package primenumbers.editors.maze;

public class MazeCell implements Cloneable {

	public static final int EMPTY_CELL=0;
	public static final int WALL_CELL=1;

	private int value=WALL_CELL;
	private boolean visited=false;
	private int xIndex=-1;
	private int yIndex=-1;

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}

	public boolean isVisited() {
		return visited;
	}

	public void setVisited(boolean visited) {
		this.visited = visited;
	}

	public int getxIndex() {
		return xIndex;
	}

	public void setxIndex(int xIndex) {
		this.xIndex = xIndex;
	}

	public int getyIndex() {
		return yIndex;
	}

	public void setyIndex(int yIndex) {
		this.yIndex = yIndex;
	}

	@Override
	public String toString() {
		return xIndex+","+yIndex+"="+value;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		MazeCell mc=new MazeCell();
		mc.setValue(value);
		mc.setxIndex(xIndex);
		mc.setyIndex(yIndex);
		mc.setVisited(visited);
		return mc;
	}
	
}