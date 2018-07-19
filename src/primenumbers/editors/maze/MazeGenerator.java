package primenumbers.editors.maze;

import java.util.ArrayList;

public class MazeGenerator {

	public Maze generateMaze(int nX, int nY){

		Maze maze=new Maze(nX, nY);
		createPrimMazePath(maze);
		return maze;
	}

	/**
	 *  Here an Algorithm I found on stackoverflow.com,
	 *  which produces very good mazes
	 *
        1A Grid consists of a 2 dimensional array of cells.
        2A Cell has 2 states: Blocked or Passage.
        3Start with a Grid full of Cells in state Blocked.
        4Pick a random Cell, set it to state Passage and Compute its frontier cells.
        A frontier cell of a Cell is a cell with distance 2 in state Blocked and
        within the grid.
        5While the list of frontier cells is not empty:
            1Pick a random frontier cell from the list of frontier cells.
            2Let neighbors(frontierCell) = All cells in distance 2 in state Passage.
            Pick a random neighbor and connect the frontier cell with the neighbor by
            setting the cell in-between to state Passage.
            Compute the frontier cells of the chosen frontier cell and add
            them to the frontier list. Remove the chosen frontier cell from the
            list of frontier cells.
	 *
	 * @param maze
	 */
	private void createPrimMazePath(Maze maze) {

		MazeCell[][] cells = maze.getCells();
		for (int i = 0; i < maze.getnX(); i++) {
			for (int j = 0; j < maze.getnY(); j++) {
				cells[i][j].setValue(MazeCell.WALL_CELL);
			}
		}
		cells[0][0].setValue(MazeCell.EMPTY_CELL);
		ArrayList<MazeCell> frontCellsList=new ArrayList<MazeCell>();
		addFrontierCells(cells[0][0], maze,frontCellsList,MazeCell.WALL_CELL,2);

		while(frontCellsList.size()>0){

			int n=getNextWallIndex(frontCellsList.size());
			MazeCell mCell=frontCellsList.get(n);

			ArrayList<MazeCell> neighCells=new ArrayList<MazeCell>();
			addFrontierCells(mCell, maze,neighCells,MazeCell.EMPTY_CELL,2);

			if(neighCells.size()>0){
				MazeCell ncell=neighCells.get(getNextWallIndex(neighCells.size()));
				joinCells(mCell,ncell,maze);
				addFrontierCells(mCell, maze,frontCellsList,MazeCell.WALL_CELL,2);
			}
			frontCellsList.remove(mCell);
		}
	}

	private void joinCells(MazeCell mCell, MazeCell unvisitedMCell, Maze maze) {

		int i0=mCell.getxIndex();
		int j0=mCell.getyIndex();
		int i1=unvisitedMCell.getxIndex();
		int j1=unvisitedMCell.getyIndex();

		int x0=Math.min(i0, i1);
		int x1=Math.max(i0, i1);
		int y0=Math.min(j0, j1);
		int y1=Math.max(j0, j1);

		MazeCell[][] cells = maze.getCells();

		if(y0==y1){
			for (int i = x0; i <= x1; i++) {
				cells[i][y0].setValue(MazeCell.EMPTY_CELL);
				cells[i][y0].setVisited(true);
			}
		}else if(x0==x1){
			for (int j = y0; j <= y1; j++) {
				cells[x0][j].setValue(MazeCell.EMPTY_CELL);
				cells[x0][j].setVisited(true);
			}
		}
	}

	/**Non uniform Wall distribution **/
	private int getNextWallIndex(int size) {

		double r=Math.random();
		//r=-r*r+2*r;
		int n=(int) (size*r);
		return n;
	}

	private void addFrontierCells(MazeCell mazeCell,Maze maze,ArrayList<MazeCell> wallsList,int state,int span){

		int i=mazeCell.getxIndex();
		int j=mazeCell.getyIndex();

		MazeCell cell0=pickCell(i+span,j,maze,state);
		if(cell0!=null && !wallsList.contains(cell0)) {
			wallsList.add(cell0);
		}

		cell0=pickCell(i-span,j,maze,state);
		if(cell0!=null && !wallsList.contains(cell0)) {
			wallsList.add(cell0);
		}

		cell0=pickCell(i,j-span,maze,state);
		if(cell0!=null && !wallsList.contains(cell0)) {
			wallsList.add(cell0);
		}

		cell0=pickCell(i,j+span,maze,state);
		if(cell0!=null && !wallsList.contains(cell0)) {
			wallsList.add(cell0);
		}
	}

	private MazeCell pickCell(int i, int j, Maze maze,int state) {

		MazeCell ret=null;

		if(i>=0 && j>=0 && i<maze.getnX() && j<maze.getnY()){
			ret=maze.getCells()[i][j];
			if(ret.getValue()!=state) {
				return null;
			}
		}
		return ret;
	}
	
}