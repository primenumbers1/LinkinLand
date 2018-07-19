package primenumbers.editors.maze;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.RepaintManager;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class MazeFrame extends JFrame implements ActionListener, MenuListener, KeyListener, MouseWheelListener {
	
	private JPanel center;
	private Graphics2D graphics2D;

	int WIDTH=800;
	int HEIGHT=500;
	int BOTTOM_BORDER=100;
	int LEFT_BORDER=100;

	private Maze maze=null;
	private BufferedImage buf=null;
	private JMenuItem jmiGenerateMaze;
	private JMenuItem jmiSaveMaze;
	private JMenuItem jmiLoadMaze;
	public boolean redrawAfterMenu=false;
	private JFileChooser fc;
	private File currentDirectory;
	private File currentFile;

	private int x0=0;
	private int y0=0;
	private int dxMaze=10;
	private int dyMaze=10;
	private JMenuItem jmiSaveImage;

	public static void main(String[] args) {
		MazeFrame mz=new MazeFrame();
	}

	public MazeFrame(){

		setTitle("Maze frame");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(null);
		setSize(WIDTH+LEFT_BORDER,HEIGHT+BOTTOM_BORDER);

		center=new JPanel();
		center.setBounds(0,0,WIDTH,HEIGHT);
		add(center);
		addKeyListener(this);
		addMouseWheelListener(this);
		buildMenuBar();

		RepaintManager.setCurrentManager(new RepaintManager() {
			@Override
			public void paintDirtyRegions() {

				super.paintDirtyRegions();
				if (redrawAfterMenu) {
					draw();
					redrawAfterMenu = false;
				}
			}

		});

		setVisible(true);

		initialize();
	}

	private void buildMenuBar() {
		JMenuBar jmb=new JMenuBar();

		JMenu mazeMenu=new JMenu("Maze");
		mazeMenu.addActionListener(this);
		mazeMenu.addMenuListener(this);
		jmb.add(mazeMenu);

		jmiGenerateMaze=new JMenuItem("Generate");
		jmiGenerateMaze.addActionListener(this);
		mazeMenu.add(jmiGenerateMaze);

		mazeMenu.addSeparator();

		jmiSaveMaze=new JMenuItem("Save");
		jmiSaveMaze.addActionListener(this);
		mazeMenu.add(jmiSaveMaze);

		jmiLoadMaze=new JMenuItem("Load");
		jmiLoadMaze.addActionListener(this);
		mazeMenu.add(jmiLoadMaze);

		JMenu imageMenu=new JMenu("Image");
		imageMenu.addActionListener(this);
		imageMenu.addMenuListener(this);
		jmb.add(imageMenu);

		jmiSaveImage=new JMenuItem("Save");
		jmiSaveImage.addActionListener(this);
		imageMenu.add(jmiSaveImage);

		setJMenuBar(jmb);
	}

	private void initialize() {

		graphics2D=(Graphics2D) center.getGraphics();
		buf=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		draw();
	}

	private void draw() {

		if(graphics2D==null) {
			graphics2D=(Graphics2D) center.getGraphics();
		}

		Graphics2D bufGraphics=(Graphics2D)buf.getGraphics();
		draw(bufGraphics,WIDTH,HEIGHT,x0,y0);
		graphics2D.drawImage(buf,0,0,WIDTH,HEIGHT,null);

	}

	private void draw(Graphics2D bufGraphics,int iWidth, int iHeight,int iX0, int iY0) {

		bufGraphics.setColor(Color.BLACK);
		bufGraphics.fillRect(0, 0, iWidth, iHeight);

		if(maze==null) {
			return;
		}

		MazeCell[][] cells = maze.getCells();

		for (int i = 0; i < maze.getnX(); i++) {
			for (int j = 0; j < maze.getnY(); j++) {
				int val=cells[i][j].getValue();

				if(val==MazeCell.EMPTY_CELL){
					bufGraphics.setColor(Color.WHITE);
					drawCell(i,j,bufGraphics,iHeight,iX0,iY0);
				}else if(val==MazeCell.WALL_CELL){
					bufGraphics.setColor(Color.BLACK);
					drawCell(i,j,bufGraphics,iHeight,iX0,iY0);
				}
			}
		}
	}

	private void drawCell(int i, int j, Graphics2D bufGraphics, int iHeight,int iX0, int iY0) {


		int x=dxMaze*i+iX0;
		int y=iHeight-(dyMaze*j+iY0);
		bufGraphics.fillRect(x, y-dyMaze, dxMaze, dyMaze);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		Object obj = arg0.getSource();
		if(obj==jmiGenerateMaze){
			generateMaze();
		} else if(obj==jmiSaveMaze){
			saveMaze();
		} else if(obj==jmiLoadMaze){
			loadMaze();
			draw();
		} else if(obj==jmiSaveImage){
			saveImage();
		}

	}

	private void saveImage() {
		fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setDialogTitle("Save maze");
		if(currentDirectory!=null) {
			fc.setCurrentDirectory(currentDirectory);
		}
		if(currentFile!=null) {
			fc.setSelectedFile(currentFile);
		}

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			int imgDx=dxMaze*maze.getnX();
			int imgDY=dyMaze*(maze.getnY());
			BufferedImage bufImage=new BufferedImage(imgDx,imgDY,BufferedImage.TYPE_INT_RGB);
			try {
				draw((Graphics2D) bufImage.getGraphics(),imgDx,imgDY,0,0);
				ImageIO.write(bufImage, "gif", file);

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void loadMaze() {
		fc = new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setDialogTitle("Load maze");
		if(currentDirectory!=null) {
			fc.setCurrentDirectory(currentDirectory);
		}
		if(currentFile!=null) {
			fc.setSelectedFile(currentFile);
		}

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			currentDirectory=fc.getCurrentDirectory();
			currentFile=fc.getSelectedFile();

			try {
				maze=Maze.loadMaze(file);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void saveMaze() {
		fc = new JFileChooser();
		fc.setDialogType(JFileChooser.SAVE_DIALOG);
		fc.setDialogTitle("Save maze");
		if(currentDirectory!=null) {
			fc.setCurrentDirectory(currentDirectory);
		}
		if(currentFile!=null) {
			fc.setSelectedFile(currentFile);
		}

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			File file = fc.getSelectedFile();
			currentDirectory=fc.getCurrentDirectory();
			currentFile=fc.getSelectedFile();

			Maze.saveMaze(maze,file);
		}
	}

	private void generateMaze() {

		MazeGenerationDialogue dialog=new MazeGenerationDialogue();
		if(!dialog.isSaved()) {
			return;
		}

		int nX=dialog.getNxSize();
		int nY=dialog.getNySize();

		MazeGenerator mazeGenerator=new MazeGenerator();
		this.maze = mazeGenerator.generateMaze(nX, nY);
		draw();
	}

	@Override
	public void menuCanceled(MenuEvent arg0) {
		// TODO Auto-generated method stub

	}
	@Override
	public void menuDeselected(MenuEvent arg0) {
		redrawAfterMenu=true;

	}
	@Override
	public void menuSelected(MenuEvent arg0) {
		redrawAfterMenu=false;

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent arg0) {

		int code = arg0.getKeyCode();

		if(code==KeyEvent.VK_UP){
			translate(0,-1);
			draw();
		}
		else if(code==KeyEvent.VK_DOWN){
			translate(0,+1);
			draw();
		}
		else if(code==KeyEvent.VK_LEFT){
			translate(1,0);
			draw();
		}
		else if(code==KeyEvent.VK_RIGHT){
			translate(-1,0);
			draw();
		}

	}

	private void translate(int i, int j) {

		x0+=dxMaze*i;
		y0+=dyMaze*j;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
		int rotation = arg0.getWheelRotation();
		if(rotation>0){
			translate(0,1);
		}else{
			translate(0,-1);
		}
		draw();

	}
}