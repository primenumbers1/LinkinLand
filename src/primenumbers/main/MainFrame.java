package primenumbers.main;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import primenumbers.Engine;
import primenumbers.main.gameover.GameOver;
import primenumbers.main.gameover.GameStatistics;
import primenumbers.main.loader.GameLoader;

public class MainFrame extends JFrame implements KeyListener, MouseListener, MouseMotionListener, MouseWheelListener {

	public static final int WIDTH=1000;
	public static final int HEIGHT=620;
	private int BOTTOM_BORDER=100;

	private String VERSION="LinkinLand 3.0.10 - Infinity";

	private transient Graphics2D graph=null;

	private JPanel center=null;

	private transient BufferedImage buf=null;
	private transient Game game;
	private transient Engine engine;

	public int sight_x=0;
	public int sight_y=0;

	private double _4pi=4.0*Math.PI;

	private int WALKING_SPEED=40;
	private JPanel bottom;
	private JLabel loaded_ammo;
	private JLabel ammo;
	private JLabel weapon;
	private JLabel life;
	private double diffX;
	private double diffY;

	private int oldMouseX=0;
	private int oldMouseY=0;
	private transient Robot robot;

	private String mapName=GameLoader.DEFAULT_MAP;
	private GameLoader gameLoader=null;


	public static void main(String[] args) {
		GameLoader gl=new GameLoader();
		MainFrame mf=new MainFrame(gl,args);
	}

	private MainFrame(GameLoader gameLoader,String[] args){

		this.gameLoader=gameLoader;
		this.mapName=gameLoader.getMap();

		setTitle(VERSION);
		setSize(WIDTH,HEIGHT+BOTTOM_BORDER);
		setLocation(100,10);
		setLayout(null);

		center=new JPanel();
		center.setBounds(0,0,WIDTH,HEIGHT);
		add(center);

		init(args);

		buildBottom();

		addKeyListener(this);
		center.addMouseListener(this);
		center.addMouseMotionListener(this);
		center.addMouseWheelListener(this);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		//no cursor?
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");
		getContentPane().setCursor(blankCursor);

		oldMouseX=WIDTH/2;
		oldMouseY=HEIGHT/2;

		setVisible(true);
		requestFocus();

		if(!engine.isAlive()) {
			engine.start();
		}
	}

	private void buildBottom() {

		bottom=new JPanel(null);
		bottom.setBounds(0,HEIGHT,WIDTH,BOTTOM_BORDER);
		add(bottom);

		Font bigFont=new Font("ARIAL",Font.PLAIN,16);

		JLabel jlb=new JLabel("LIFE");
		jlb.setBounds(300,10,70,30);
		jlb.setFont(bigFont);
		bottom.add(jlb);

		life = new JLabel();
		showLife();
		life.setBounds(370,10,100,30);
		life.setFont(bigFont);
		bottom.add(life);

		jlb=new JLabel("AMMO");
		jlb.setBounds(570,10,70,30);
		jlb.setFont(bigFont);
		bottom.add(jlb);

		loaded_ammo=new JLabel();
		loaded_ammo.setText(addBlanks(game.getWeapon().LOADED_AMMO,3));
		loaded_ammo.setBounds(640,10,40,30);
		loaded_ammo.setFont(bigFont);
		bottom.add(loaded_ammo);

		jlb=new JLabel("/");
		jlb.setBounds(680,10,20,30);
		jlb.setFont(bigFont);
		bottom.add(jlb);

		ammo=new JLabel();
		ammo.setText(addBlanks(game.getWeapon().AMMO,4));
		ammo.setBounds(700,10,100,30);
		ammo.setFont(bigFont);
		bottom.add(ammo);

		weapon=new JLabel();
		weapon.setText(game.getWeapon().getName());
		weapon.setBounds(800,10,100,30);
		weapon.setFont(bigFont);
		bottom.add(weapon);
	}

	void showLife() {
		life.setText(addBlanks(game.getLIFE(),4));
	}

	void updateTextPanels() {
		ammo.setText(addBlanks(game.getWeapon().AMMO,4));
		loaded_ammo.setText(addBlanks(game.getWeapon().LOADED_AMMO,3));
		weapon.setText(game.getWeapon().getName());
	}

	private String addBlanks(int ammo,int len) {
		String total="";
		for (int i = 0; i < len; i++) {
			total+=" ";
		}
		total+=ammo;
		return total.substring(total.length()-len,total.length());
	}

	private void init(String[] args) {

		buf=new BufferedImage(WIDTH,HEIGHT,BufferedImage.TYPE_INT_RGB);
		buf.setAccelerationPriority(1.0F);
		game=new Game(this,args);
		engine = new Engine(this);

		sight_x=WIDTH/2;
		sight_y=HEIGHT/2;

		try {
			robot = new Robot();
			robot.mouseMove(sight_x,sight_y);

		} catch (AWTException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		//draw();
	}

	private void draw() {
		if(graph==null) {
			graph=(Graphics2D) center.getGraphics();
		}

		draw(buf);
		if(game.isDrawSight()){
			drawSight(buf);
		}
		graph.drawImage(buf,0,0,this);
	}


	private void drawSight(BufferedImage buf) {

		Graphics graph = buf.getGraphics();
		graph.setColor(Color.black);

		sight_x=game.XFOCUS;
		sight_y=(HEIGHT-game.YFOCUS);

		graph.fillRect(sight_x-10,sight_y-1,20,3);
		graph.fillRect(sight_x-1,sight_y-10,3,20);
	}

	private void draw(BufferedImage buf) {
		game.draw(buf);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {

		int code = arg0.getKeyCode();

		if(code==KeyEvent.VK_UP || code==KeyEvent.VK_W){
			game.setLONGITUDINAL_SPEED(WALKING_SPEED);
			game.footSteps(true);
		}
		else if(code==KeyEvent.VK_DOWN || code==KeyEvent.VK_S){
			game.setLONGITUDINAL_SPEED(-WALKING_SPEED);
			game.footSteps(true);
		}
		else if(code==KeyEvent.VK_LEFT || code==KeyEvent.VK_A){
			game.setLATERAL_SPEED(-WALKING_SPEED);
			game.footSteps(true);
		}
		else if(code==KeyEvent.VK_RIGHT || code==KeyEvent.VK_D){
			game.setLATERAL_SPEED(WALKING_SPEED);
			game.footSteps(true);
		}
		else if(code==KeyEvent.VK_Z){
			game.setVIEW_TYPE(Game.REAR_VIEW);
		}
		else if(code==KeyEvent.VK_F){
			fire();
		}
		else if(code==KeyEvent.VK_R){
			reload();
		}
		else if(code==KeyEvent.VK_1){
			changeWeapon(0);
		}
		else if(code==KeyEvent.VK_2){
			changeWeapon(1);
		}
		else if(code==KeyEvent.VK_3){
			changeWeapon(2);
		}
		else if(code==KeyEvent.VK_ESCAPE){
			exitGame();
		}
		else if(code==KeyEvent.VK_C){
			crouch(true);
		}
		else if(code==KeyEvent.VK_SPACE){
			jump(true);
		}
	}

	private void exitGame() {
		gameOver(game.getGameStatistics());
	}

	@Override
	public void keyReleased(KeyEvent arg0) {

		int code = arg0.getKeyCode();

		if(code==KeyEvent.VK_UP|| code==KeyEvent.VK_W){
			game.setLONGITUDINAL_SPEED(0);
			game.footSteps(false);
		}
		else if(code==KeyEvent.VK_DOWN || code==KeyEvent.VK_S){
			game.setLONGITUDINAL_SPEED(0);
			game.footSteps(false);
		}
		else if(code==KeyEvent.VK_LEFT || code==KeyEvent.VK_A){
			game.setLATERAL_SPEED(0);
			game.footSteps(false);
		}
		else if(code==KeyEvent.VK_RIGHT || code==KeyEvent.VK_D){
			game.setLATERAL_SPEED(0);
			game.footSteps(false);
		}
		else if(code==KeyEvent.VK_Z){
			game.setVIEW_TYPE(Game.FRONT_VIEW);
		}
		else if(code==KeyEvent.VK_C){
			crouch(false);
		}
	}


	@Override
	public void keyTyped(KeyEvent arg0) {
	}

	public void up() {

		boolean isCocked=isCocked();

		game.up();

		game.setViewDirection(game.getViewDirection()+diffX);
		diffX=0;
		game.setSightLatDirection(game.getSightLatDirection()+diffY);
		diffY=0;

		if(game.crouch){
			game.MOVZ=game.HEIGHT/2-100;
		}

		draw();

		if(!game.crouch && game.MOVZ<game.HEIGHT/2){
			game.MOVZ=game.HEIGHT/2;
		}

		if(!isCocked) {
			cock();
		}

	}

	private void crouch(boolean action) {
		game.crouch=action;
	}

	private void jump(boolean action) {
		if(Double.doubleToRawLongBits(game.getzSpeed()) ==0) {
			game.setzSpeed(30);
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		oldMouseX=e.getX();
		oldMouseY=e.getY();
	}

	@Override
	public void mouseExited(MouseEvent e) {

		int newMouseX=e.getX();
		int newMouseY=e.getY();

		//System.out.println(">>>>"+newMouseX+" "+newMouseY);

		if(newMouseY<0){
			newMouseY=0+HEIGHT/2;
			//System.out.println("-newMouseY="+newMouseY);
		}
		else if(newMouseY>0+HEIGHT){
			newMouseY=0+HEIGHT/2;
			//System.out.println("+newMouseY="+newMouseY);
		}

		if(newMouseX<0){
			newMouseX=0+WIDTH/2;
			//System.out.println("-newMouseX="+newMouseX);
		}
		else if(newMouseX>0+WIDTH){
			newMouseX=0+WIDTH/2;
			//System.out.println("+newMouseX="+newMouseX);
		}

		robot.mouseMove(newMouseX,newMouseY);
		//System.out.println("<<<<<<"+newMouseX+" "+newMouseY);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {

		int button=arg0.getButton();

		if(button==MouseEvent.BUTTON1) {
			fire();
		} else if(button==MouseEvent.BUTTON3) {
			reload();
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		int button=arg0.getButton();
		if(button==MouseEvent.BUTTON1) {
			stopFire();
		}
	}


	@Override
	public void mouseDragged(MouseEvent e) {
		moveMouse(e);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		moveMouse(e);
	}

	private void moveMouse(MouseEvent e) {

		int newMouseX=e.getX();
		int deltaX=newMouseX-oldMouseX;

		int newMouseY=e.getY();
		int deltaY=newMouseY-oldMouseY;

		oldMouseX=newMouseX;
		oldMouseY=newMouseY;

		diffX-=deltaX*_4pi/WIDTH;
		diffY-=(deltaY)*1.0/HEIGHT;

	}

	private void fire() {

		game.fire(this);

	}


	private void stopFire() {
		game.stopFire();
	}

	private void cock() {
		game.cock();
	}

	private boolean isCocked() {
		return game.isCocked();
	}

	private void reload() {

		game.reload();
		updateTextPanels();
	}

	private void changeWeapon(int i) {

		game.changeWeapon(i);
		updateTextPanels();
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {

		int diff=-1;
		if(arg0.getUnitsToScroll()<0) {
			diff=+1;
		}

		game.nextWeapon(diff);
		updateTextPanels();
	}

	void gameOver(GameStatistics gameStatistics) {

		engine.setRunning(false);
		new GameOver(this,gameStatistics);
	}

	public GameLoader getGameLoader() {
		return gameLoader;
	}

	public String getMapName() {
		return mapName;
	}
	
}