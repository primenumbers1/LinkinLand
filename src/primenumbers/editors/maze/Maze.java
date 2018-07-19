package primenumbers.editors.maze;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.StringTokenizer;

public class Maze {

    private static final String CELLS_TAG = "CELLS";
    private static final String SIZE_TAG = "SIZE";
    private int nX=0;
    private int nY=0;


    private MazeCell[][] cells=null;

    public Maze(int nX, int nY) {
        super();
        this.nX = nX;
        this.nY = nY;
        cells=new MazeCell[nX][nY];

        for (int i = 0; i < nX; i++) {
            for (int j = 0; j < nY; j++) {
                cells[i][j]=new MazeCell();
                cells[i][j].setValue(MazeCell.WALL_CELL);
                cells[i][j].setxIndex(i);
                cells[i][j].setyIndex(j);
            }
        }
    }

    public int getnX() {
        return nX;
    }

    public void setnX(int nX) {
        this.nX = nX;
    }

    public MazeCell[][] getCells() {
        return cells;
    }

    public int getnY() {
        return nY;
    }

    public void setnY(int nY) {
        this.nY = nY;
    }

    public static Maze loadMaze(File file) throws IOException {

        Maze maze=null;
        BufferedReader br=new BufferedReader(new FileReader(file));
        String line=null;
        while((line=br.readLine())!=null){
            if(line.startsWith(SIZE_TAG)){
                String val=line.substring(line.indexOf("=")+1);
                StringTokenizer stk=new StringTokenizer(val, " ");
                int nX=Integer.parseInt(stk.nextToken());
                int nY=Integer.parseInt(stk.nextToken());
                maze=new Maze(nX, nY);
            }else if(line.startsWith(CELLS_TAG)){
                String val=line.substring(line.indexOf("=")+1);
                StringTokenizer stk=new StringTokenizer(val, " ");
                MazeCell[][] cells = maze.getCells();
                for (int i = 0; i < maze.getnX(); i++) {
                    for (int j = 0; j < maze.getnY(); j++) {
                        cells[i][j].setValue(Integer.parseInt(stk.nextToken()));
                    }
                }
            }
        }
        br.close();
        return maze;
    }

    public static void saveMaze(Maze maze, File file) {

        PrintWriter pw=null;

        try{

            pw=new PrintWriter(file);

            pw.println(SIZE_TAG+"="+maze.getnX()+" "+maze.getnY());
            MazeCell[][] cells = maze.getCells();

            pw.print(CELLS_TAG+"=");
            StringBuilder sb= new StringBuilder();
            for (int i = 0; i < maze.getnX(); i++) {

                for (int j = 0; j < maze.getnY(); j++) {
                    int val=cells[i][j].getValue();

                    if(val==MazeCell.EMPTY_CELL){
                        sb.append(MazeCell.EMPTY_CELL);
                    }else if(val==MazeCell.WALL_CELL){
                        sb.append(MazeCell.WALL_CELL);
                    }
                    sb.append(" ");
                }

            }
            pw.println(sb.toString());
        }catch(Exception e){
            e.printStackTrace();
        }finally {
            if(pw!=null) {
                pw.close();
            }
        }
    }
    
}