package primenumbers.editors.scene;

import java.awt.CardLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JToggleButton;
import javax.swing.RepaintManager;
import javax.swing.TransferHandler;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

import primenumbers.BlocksMesh;
import primenumbers.CubicMesh;
import primenumbers.DPoint3D;
import primenumbers.DrawObject;
import primenumbers.Polygon3D;
import primenumbers.editors.ComboElement;
import primenumbers.editors.DoubleTextField;
import primenumbers.editors.Editor;
import primenumbers.editors.EditorData;
import primenumbers.editors.IntegerTextField;
import primenumbers.editors.scene.panels.EditorPanel;
import primenumbers.editors.scene.panels.SceneEditorIsoPanel;
import primenumbers.editors.scene.panels.SceneEditorTopPanel;
import primenumbers.main.HelpPanel;
import primenumbers.scene.Block;
import primenumbers.scene.Scene;

public class SceneEditor extends Editor implements ItemListener {

	private static final String IMAGES_FILE_PATH = "lib"+File.separator+"images"+File.separator;
	public static final int OTHER_IMAGES_INDEX_GRID = 2;
	public static final int OTHER_IMAGES_INDEX_DOOR = 1;
	public static final int OTHER_IMAGES_INDEX_WINDOW = 0;

	private Scene scene=null;

	private transient BufferedImage buf=null;

	private static final int ISO_VIEW=1;
	private static final int TOP_VIEW=0;

	private int VIEW_TYPE=TOP_VIEW;

	private static final String CELL_MODE="CELL_MODE";
	private static final String DECAL_MODE="DECALS_MODE";
	private static final String HEIGHT_MODE="HEIGHT_MODE";
	private static final String WALL_MODE="WALL_MODE";
	private static final String OBJECT_MODE="OBJECT_MODE";
	private String mode=CELL_MODE;

	private static final int BORDER_LEFT=250;
	private static final int CENTER_HEIGHT=600;
	private static final int CENTER_WIDTH=900;
	private static final int BORDER_BOTTOM=100;
	private static final int BORDER_RIGHT=50;
	private EditorPanel panelIso;
	private EditorPanel panelTop;
	private JPanel bottom;
	private JPanel left;
	private JMenuBar jmb;
	private JMenu jmEdit;
	private JMenu jmOther;
	private JMenuItem jmtBuildBox;
	private JMenu jmFile;
	private JMenuItem jmtLoadScene;
	private JMenuItem jmtSaveScene;
	private JMenu jmView;
	private JMenuItem jmt_3d_view;
	private JMenuItem jmt_top_view;
	private JMenu jmHelp;
	private JMenuItem jmtExpandBox;
	private JMenuItem jmtFasterMotion;
	private JMenuItem jmtSlowerMotion;
	private JMenuItem jmMassModify;
	private JMenuItem jmtHelp;
	private JCheckBoxMenuItem jmChkBlueScale;
	private JMenuItem jmtDuplicateWalls;
	private JMenuItem jmtUndo;

	private JButton change_cell;
	private JButton changeObject;
	private JButton delete_object;

	private Graphics2D graphics;

	private JLabel cellTextureLabel;

	private JComboBox<ComboElement> chooseCellTexture;
	private JComboBox<ComboElement> chooseWallTexture;
	private JButton chooseCellPanelTexture;

	private JButton chooseWallPanelTexture;
	private JRadioButton radioWallTypeFull;
	private JLabel wallTextureLabel;

	private JComboBox<ComboElement> chooseObjectTexture;
	private JButton chooseObjectPanelTexture;
	private JLabel objectTextureLabel;

	public JCheckBox checkMultiselection;

	public static BufferedImage[] worldImages;
	public static BufferedImage[] wallImages;
	public static BufferedImage[] otherTextures;
	public static BufferedImage[] worldDecals;
	public static BufferedImage[] worldDecalsImages;

	private static BufferedImage[] objectImages;
	private static CubicMesh[] objectMeshes;
	private static String[] objectDescriptions;
	private static BufferedImage[] otherImages;

	private JButton deselectAll;

	private Rectangle currentRect;
	private boolean isDrawCurrentRect=false;
	private ArrayDeque<ArrayList<DrawObject>> oldObjects=new ArrayDeque<ArrayList<DrawObject>>();

	private DoubleTextField objMove;
	private JButton moveObjUp;
	private JButton moveObjDown;
	private JButton moveObjLeft;
	private JButton moveObjRight;
	private JButton moveObjTop;
	private JButton moveObjBottom;
	private JButton changeWall;

	private DoubleTextField rotation_angle;

	private JRadioButton radioWallTypeWindow;
	private JRadioButton radioWallTypeDoor;
	private JRadioButton radioCellTypeCeiling;
	private JRadioButton radioCellTypeGround;
	private JRadioButton radioWallTypeEmpty;
	private JRadioButton radioWallTypeGrid;

	private JLabel cellCoordinates;

	private DecimalFormat dfc;

	private IntegerTextField startX;
	private IntegerTextField startY;

	private JComboBox<ComboElement> blockHeight;

	private String header="<html><body>";
	private String footer="</body></html>";

	public JCheckBox checkDrawBlockHeight;

	private JToggleButton toogle_cell;
	private JToggleButton toogle_decal;
	private JToggleButton toogle_height;
	private JToggleButton toogle_wall;
	private JToggleButton toogle_object;

	private JPanel right_tools;
	private JPanel right_tool_options;
	private JPanel right_common_options;

	private JCheckBox checkHideObjects;
	private JButton change_height;
	private JComboBox<ComboElement> showLayer;

	private JButton btn_pi_angle;
	private JButton btn_pi_angle_2;
	private JComboBox<ComboElement> chooseDecalTexture;
	private JButton chooseDecalPanelTexture;
	private JLabel decalTextureLabel;
	private JButton changeDecal;
	private JButton removeDecal;
	private int DECAL_BACKGROUND_RGB=new Color(150,150,150).getRGB();
	private JMenuItem pile_objects_jmt;;

	public static final int ALL_LAYERS=-1;

	public SceneEditor(){

		setTitle("Scene Editor");
		setSize(BORDER_LEFT+CENTER_WIDTH+BORDER_RIGHT,CENTER_HEIGHT+BORDER_BOTTOM);
		setLayout(null);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		panelIso=getPanel3D();
		panelIso.setTransferHandler(new FileTransferhandler());
		panelTop=getPanelTop();
		panelTop.setTransferHandler(new FileTransferhandler());
		add(panelTop);

		currentDirectory=new File("lib");

		buildBottomPanel();
		buildLeftPanel();

		addKeyListener(this);
		addMouseWheelListener(this);

		addMenuBar();

		init();

		RepaintManager.setCurrentManager(
				new RepaintManager(){
					@Override
					public void paintDirtyRegions() {


						super.paintDirtyRegions();
						firePropertyChange("paintDirtyRegions",false,true);
						//if(redrawAfterMenu ) {displayAll();redrawAfterMenu=false;}
					}

				}
				);
		addPropertyChangeListener(this);

		DecimalFormatSymbols dfs=new DecimalFormatSymbols(Locale.UK);
		dfc = new DecimalFormat("###.###");
		dfc.setDecimalFormatSymbols(dfs);

		setVisible(true);

	}

	public static void main(String[] args) {
		SceneEditor se=new SceneEditor();
	}

	private void buildBottomPanel() {

		bottom=new JPanel(null);
		bottom.setBounds(BORDER_LEFT,CENTER_HEIGHT,CENTER_WIDTH,BORDER_BOTTOM);

		int c=10;

		JLabel jLabel = new JLabel("Start X:");
		jLabel.setBounds(c,10,50,20);
		bottom.add(jLabel);

		c+=50;

		startX=new IntegerTextField();
		startX.setBounds(c,10,100,20);
		startX.addActionListener(this);
		startX.addKeyListener(this);
		bottom.add(startX);

		c+=110;

		jLabel = new JLabel("Start Y:");
		jLabel.setBounds(c,10,50,20);
		bottom.add(jLabel);

		c+=50;

		startY=new IntegerTextField();
		startY.setBounds(c,10,100,20);
		startY.addActionListener(this);
		startY.addKeyListener(this);
		bottom.add(startY);

		c+=250;

		cellCoordinates=new JLabel("xxxx");
		cellCoordinates.setBounds(c,10,100,20);
		bottom.add(cellCoordinates);

		add(bottom);

	}

	private JPanel buildObjectMovePanel(int i, int r) {

		JPanel move=new JPanel();
		move.setBounds(i,r,100,100);
		move.setLayout(null);

		Border border = BorderFactory.createEtchedBorder();
		move.setBorder(border);

		objMove=new DoubleTextField();
		objMove.setBounds(30,40,40,20);
		objMove.setToolTipText("Position increment");
		move.add(objMove);
		objMove.addKeyListener(this);

		moveObjUp=new JButton(new ImageIcon(IMAGES_FILE_PATH+"trianglen.jpg"));
		moveObjUp.setBounds(40,10,20,20);
		moveObjUp.addActionListener(this);
		moveObjUp.setFocusable(false);
		move.add(moveObjUp);

		moveObjDown=new JButton(new ImageIcon(IMAGES_FILE_PATH+"triangles.jpg"));
		moveObjDown.setBounds(40,70,20,20);
		moveObjDown.addActionListener(this);
		moveObjDown.setFocusable(false);
		move.add(moveObjDown);

		moveObjLeft=new JButton(new ImageIcon(IMAGES_FILE_PATH+"triangleo.jpg"));
		moveObjLeft.setBounds(5,40,20,20);
		moveObjLeft.addActionListener(this);
		moveObjLeft.setFocusable(false);
		move.add(moveObjLeft);

		moveObjRight=new JButton(new ImageIcon(IMAGES_FILE_PATH+"trianglee.jpg"));
		moveObjRight.setBounds(75,40,20,20);
		moveObjRight.addActionListener(this);
		moveObjRight.setFocusable(false);
		move.add(moveObjRight);

		moveObjTop=new JButton(new ImageIcon(IMAGES_FILE_PATH+"up.jpg"));
		moveObjTop.setBounds(5,70,20,20);
		moveObjTop.addActionListener(this);
		moveObjTop.setFocusable(false);
		move.add(moveObjTop);

		moveObjBottom=new JButton(new ImageIcon(IMAGES_FILE_PATH+"down.jpg"));
		moveObjBottom.setBounds(75,70,20,20);
		moveObjBottom.addActionListener(this);
		moveObjBottom.setFocusable(false);
		move.add(moveObjBottom);

		return move;

	}

	private EditorPanel getPanel3D() {
		SceneEditorIsoPanel panel=new SceneEditorIsoPanel(this,CENTER_WIDTH,CENTER_HEIGHT);
		panel.setBounds(BORDER_LEFT,0,CENTER_WIDTH,CENTER_HEIGHT);
		panel.addKeyListener(this);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		return panel;
	}

	private EditorPanel getPanelTop() {
		SceneEditorTopPanel panel=new SceneEditorTopPanel(this,CENTER_WIDTH,CENTER_HEIGHT);
		panel.setBounds(BORDER_LEFT,0,CENTER_WIDTH,CENTER_HEIGHT);
		panel.addKeyListener(this);
		panel.addMouseListener(this);
		panel.addMouseMotionListener(this);
		return panel;
	}


	private void buildLeftPanel() {

		int upper_right_height=180;
		int middle_right_height=90;
		int lower_right_height=500;

		left=new JPanel(null);
		left.setBounds(0,0,BORDER_LEFT,CENTER_HEIGHT+BORDER_BOTTOM);

		right_tools=new JPanel(null);
		right_tools.setBorder(BorderFactory.createTitledBorder("Choose tool"));


		right_tools.setBounds(5,0,150,upper_right_height);
		left.add(right_tools);

		right_common_options=new JPanel(null);
		right_common_options.setBounds(0,upper_right_height,BORDER_LEFT,middle_right_height);
		left.add(right_common_options);

		right_tool_options=new JPanel(new CardLayout());
		right_tool_options.setBounds(0,middle_right_height+upper_right_height,BORDER_LEFT,lower_right_height);
		left.add(right_tool_options);

		UIManager.put("ToggleButton.select", Color.WHITE);

		int r=30;

		toogle_cell=new JToggleButton("Cell");
		toogle_cell.setSelected(true);
		toogle_cell.setActionCommand(CELL_MODE);
		toogle_cell.addActionListener(this);
		toogle_cell.addKeyListener(this);
		toogle_cell.setBounds(10,r,100,20);

		r+=30;

		toogle_decal=new JToggleButton("Decal");
		toogle_decal.setActionCommand(DECAL_MODE);
		toogle_decal.addActionListener(this);
		toogle_decal.addKeyListener(this);
		toogle_decal.setBounds(10,r,100,20);

		r+=30;

		toogle_height=new JToggleButton("Height");
		toogle_height.setActionCommand(HEIGHT_MODE);
		toogle_height.addActionListener(this);
		toogle_height.addKeyListener(this);
		toogle_height.setBounds(10,r,100,20);

		r+=30;

		toogle_wall=new JToggleButton("Wall");
		toogle_wall.setActionCommand(WALL_MODE);
		toogle_wall.addActionListener(this);
		toogle_wall.addKeyListener(this);
		toogle_wall.setBounds(10,r,100,20);

		r+=30;

		toogle_object=new JToggleButton("Object");
		toogle_object.setActionCommand(OBJECT_MODE);
		toogle_object.addActionListener(this);
		toogle_object.addKeyListener(this);
		toogle_object.setBounds(10,r,100,20);

		ButtonGroup bgb=new ButtonGroup();
		bgb.add(toogle_cell);
		bgb.add(toogle_decal);
		bgb.add(toogle_wall);
		bgb.add(toogle_object);

		right_tools.add(toogle_cell);
		right_tools.add(toogle_decal);
		right_tools.add(toogle_wall);
		right_tools.add(toogle_object);

		/////////////// COMMON SECTION

		r=5;

		JLabel label=new JLabel("Layer:");
		label.setBounds(10,r,50,20);
		right_common_options.add(label);

		showLayer=new JComboBox<ComboElement>();
		setLayersComboData(showLayer,0);
		showLayer.setBounds(60,r,100,20);
		showLayer.addKeyListener(this);
		showLayer.setFocusable(false);
		showLayer.addItemListener(

				new ItemListener() {
					@Override
					public void itemStateChanged(ItemEvent arg0) {

						deselectAll();
						draw();
					}

				}
				);
		right_common_options.add(showLayer);

		r+=30;

		checkMultiselection=new JCheckBox(header+"M<u>u</u>ltiselect"+footer);
		checkMultiselection.setBounds(10,r,100,20);
		checkMultiselection.addKeyListener(this);
		right_common_options.add(checkMultiselection);


		deselectAll=new JButton(header+"D<u>e</u>select all"+footer);
		deselectAll.addActionListener(this);
		deselectAll.addKeyListener(this);
		deselectAll.setBounds(110,r,100,20);
		right_common_options.add(deselectAll);

		r+=30;

		checkHideObjects=new JCheckBox(header+"Hide objects"+footer);
		checkHideObjects.setBounds(10,r,100,20);
		checkHideObjects.addKeyListener(this);
		checkHideObjects.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				draw();
			}
		});

		right_common_options.add(checkHideObjects);

		///////////////

		JPanel cell_panel = buildCellPanel();
		right_tool_options.add(cell_panel,CELL_MODE);

		JPanel decals_panel = buildDecalPanel();
		right_tool_options.add(decals_panel,DECAL_MODE);

		JPanel height_panel=buildHeightPanel();
		right_tool_options.add(height_panel,HEIGHT_MODE);

		JPanel wall_panel=buildWallPanel();
		right_tool_options.add(wall_panel,WALL_MODE);

		JPanel object_panel=buildObjectPanel();
		right_tool_options.add(object_panel,OBJECT_MODE);

		add(left);
	}

	private void setLayersComboData(JComboBox<ComboElement> showLayer, int nLayers) {

		showLayer.removeAllItems();
		showLayer.addItem(new ComboElement(Integer.toString(SceneEditor.ALL_LAYERS),"All"));

		for (int i = 0; i < nLayers; i++) {
			showLayer.addItem(new ComboElement(Integer.toString(i),Integer.toString(i)));
		}
	}

	private JPanel buildHeightPanel() {

		int r=10;

		JPanel height_panel=new JPanel(null);

		JLabel label=new JLabel("Height:");
		label.setBounds(10,r,60,20);
		height_panel.add(label);

		blockHeight=new JComboBox<ComboElement>();
		blockHeight.setBounds(70,r,50,20);
		blockHeight.addActionListener(this);
		blockHeight.addKeyListener(this);
		for(int i=0;i<16;i++){
			String ii=Integer.toString(i+1);
			blockHeight.addItem(new ComboElement(ii,ii));
		}
		blockHeight.setSelectedIndex(Block.zBlocks-1);

		blockHeight.addItemListener(this);
		height_panel.add(blockHeight);

		change_height=new JButton("Change");
		change_height.setBounds(130,r,80,20);
		change_height.addKeyListener(this);
		change_height.addActionListener(this);
		height_panel.add(change_height);

		r+=30;

		checkDrawBlockHeight=new JCheckBox("Show height");
		checkDrawBlockHeight.setBounds(10,r,140,20);
		checkDrawBlockHeight.addKeyListener(this);
		checkDrawBlockHeight.addActionListener(
				new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						draw();

					}
				}

				);
		height_panel.add(checkDrawBlockHeight);

		return height_panel;

	}

	private JPanel buildObjectPanel() {

		JPanel object_panel=new JPanel(null);

		int r=20;

		chooseObjectTexture=new JComboBox<ComboElement>();
		chooseObjectTexture.addItem(new ComboElement("",""));
		chooseObjectTexture.addItemListener(this);
		/*chooseTexture.setBounds(35,r,50,20);*/
		chooseObjectTexture.addKeyListener(this);

		chooseObjectPanelTexture=new JButton(header+"<u>M</u>esh"+footer);
		chooseObjectPanelTexture.setBounds(10,r,100,20);
		chooseObjectPanelTexture.addActionListener(this);
		chooseObjectPanelTexture.addKeyListener(this);
		object_panel.add(chooseObjectPanelTexture);

		r+=20;

		objectTextureLabel=new JLabel();
		objectTextureLabel.setFocusable(false);
		objectTextureLabel.setBounds(10,r,100,100);
		Border bordero=BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		objectTextureLabel.setBorder(bordero);
		object_panel.add(objectTextureLabel);

		r+=110;

		JLabel jlb=new JLabel("Rotation angle:");
		jlb.setBounds(10,r,90,20);
		object_panel.add(jlb);
		rotation_angle=new DoubleTextField(8);
		rotation_angle.setBounds(105,r,80,20);
		rotation_angle.addKeyListener(this);
		object_panel.add(rotation_angle);

		btn_pi_angle=new JButton(header+"&pi;"+footer);
		btn_pi_angle.setBounds(190,r,50,20);
		btn_pi_angle.addActionListener(this);
		btn_pi_angle.addKeyListener(this);
		object_panel.add(btn_pi_angle);

		r+=30;

		changeObject=new JButton(header+"Change <u>o</u>bject"+footer);
		changeObject.setBounds(10,r,140,20);
		changeObject.addActionListener(this);
		changeObject.addKeyListener(this);
		object_panel.add(changeObject);

		btn_pi_angle_2=new JButton(header+"&pi;/2"+footer);
		btn_pi_angle_2.setBounds(190,r,50,20);
		btn_pi_angle_2.addActionListener(this);
		btn_pi_angle_2.addKeyListener(this);
		object_panel.add(btn_pi_angle_2);

		r+=30;

		delete_object=new JButton(header+"<u>D</u>elete object"+footer);
		delete_object.setBounds(10,r,140,20);
		delete_object.addActionListener(this);
		delete_object.addKeyListener(this);
		object_panel.add(delete_object);

		r+=30;

		object_panel.add(buildObjectMovePanel(10,r));

		return object_panel;
	}

	private JPanel buildWallPanel() {

		JPanel wallPanel=new JPanel(null);

		int r=10;

		chooseWallPanelTexture=new JButton(header+"W<u>a</u>ll"+footer);
		chooseWallPanelTexture.setBounds(10,r,100,20);
		chooseWallPanelTexture.addActionListener(this);
		chooseWallPanelTexture.addKeyListener(this);
		wallPanel.add(chooseWallPanelTexture);

		r+=30;

		changeWall=new JButton(header+"C<u>h</u>. wall"+footer);
		changeWall.setBounds(120,r,100,20);
		changeWall.addActionListener(this);
		changeWall.addKeyListener(this);
		wallPanel.add(changeWall);

		chooseWallTexture=new JComboBox<ComboElement>();
		chooseWallTexture.addItem(new ComboElement("",""));
		/*chooseTexture.setBounds(35,r,50,20);*/
		chooseWallTexture.addKeyListener(this);

		wallTextureLabel=new JLabel();
		wallTextureLabel.setFocusable(false);
		wallTextureLabel.setBounds(10,r,100,100);
		Border border = BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		wallTextureLabel.setBorder(border);
		wallPanel.add(wallTextureLabel);

		r+=30;

		ButtonGroup bg=new ButtonGroup();

		radioWallTypeFull=new JRadioButton("Full wall");
		radioWallTypeFull.setBounds(120,r,100,20);
		radioWallTypeFull.addKeyListener(this);
		radioWallTypeFull.addItemListener(this);
		wallPanel.add(radioWallTypeFull);
		bg.add(radioWallTypeFull);

		r+=30;

		radioWallTypeEmpty=new JRadioButton("Empty wall");
		radioWallTypeEmpty.setBounds(120,r,100,20);
		radioWallTypeEmpty.addKeyListener(this);
		radioWallTypeEmpty.addItemListener(this);
		wallPanel.add(radioWallTypeEmpty);
		bg.add(radioWallTypeEmpty);

		r+=30;

		radioWallTypeWindow=new JRadioButton("Window");
		radioWallTypeWindow.setBounds(120,r,100,20);
		radioWallTypeWindow.addKeyListener(this);
		radioWallTypeWindow.addItemListener(this);
		wallPanel.add(radioWallTypeWindow);
		bg.add(radioWallTypeWindow);

		r+=30;

		radioWallTypeDoor=new JRadioButton("Door");
		radioWallTypeDoor.setBounds(120,r,100,20);
		radioWallTypeDoor.addKeyListener(this);
		radioWallTypeDoor.addItemListener(this);
		wallPanel.add(radioWallTypeDoor);
		bg.add(radioWallTypeDoor);


		r+=30;

		radioWallTypeGrid=new JRadioButton("Grid");
		radioWallTypeGrid.setBounds(120,r,100,20);
		radioWallTypeGrid.addKeyListener(this);
		radioWallTypeGrid.addItemListener(this);
		wallPanel.add(radioWallTypeGrid);
		bg.add(radioWallTypeGrid);

		r+=10;

		return wallPanel;
	}

	private JPanel buildCellPanel() {
		JPanel cellPanel = new JPanel(null);

		int r=10;

		chooseCellTexture=new JComboBox<ComboElement>();
		chooseCellTexture.addItem(new ComboElement("",""));
		chooseCellTexture.addItemListener(this);
		/*chooseTexture.setBounds(35,r,50,20);*/
		chooseCellTexture.addKeyListener(this);


		chooseCellPanelTexture=new JButton(header+"<u>T</u>exture"+footer);
		chooseCellPanelTexture.setBounds(10,r,100,20);
		chooseCellPanelTexture.addActionListener(this);
		chooseCellPanelTexture.addKeyListener(this);
		cellPanel.add(chooseCellPanelTexture);

		r+=30;

		cellTextureLabel=new JLabel();
		cellTextureLabel.setFocusable(false);
		cellTextureLabel.setBounds(10,r,100,100);
		Border border=BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		cellTextureLabel.setBorder(border);
		cellPanel.add(cellTextureLabel);


		change_cell=new JButton(header+"<u>C</u>hange cell"+footer);
		change_cell.setBounds(120,r,100,20);
		change_cell.addActionListener(this);
		change_cell.addKeyListener(this);
		cellPanel.add(change_cell);

		r+=30;

		ButtonGroup bg0=new ButtonGroup();

		radioCellTypeCeiling=new JRadioButton("With ceiling");
		radioCellTypeCeiling.setBounds(120,r,100,20);
		radioCellTypeCeiling.addKeyListener(this);
		radioCellTypeCeiling.addItemListener(this);
		cellPanel.add(radioCellTypeCeiling);

		bg0.add(radioCellTypeCeiling);


		radioCellTypeGround=new JRadioButton("Ground");
		radioCellTypeGround.setBounds(120,r+30,100,20);
		radioCellTypeGround.addKeyListener(this);
		radioCellTypeGround.addItemListener(this);
		cellPanel.add(radioCellTypeGround);

		bg0.add(radioCellTypeGround);

		return cellPanel;
	}

	private JPanel buildDecalPanel() {
		JPanel decalPanel = new JPanel(null);

		int r=10;

		chooseDecalTexture=new JComboBox<ComboElement>();
		chooseDecalTexture.addItem(new ComboElement("",""));
		chooseDecalTexture.addItemListener(this);
		chooseDecalTexture.addKeyListener(this);


		chooseDecalPanelTexture=new JButton(header+"<u>D</u>ecal"+footer);
		chooseDecalPanelTexture.setBounds(10,r,100,20);
		chooseDecalPanelTexture.addActionListener(this);
		chooseDecalPanelTexture.addKeyListener(this);
		decalPanel.add(chooseDecalPanelTexture);

		r+=30;

		decalTextureLabel=new JLabel();
		decalTextureLabel.setFocusable(false);
		decalTextureLabel.setBounds(10,r,100,100);
		Border border=BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
		decalTextureLabel.setBorder(border);
		decalPanel.add(decalTextureLabel);

		changeDecal=new JButton(header+"Ch. decal"+footer);
		changeDecal.setBounds(130,r,100,20);
		changeDecal.addActionListener(this);
		changeDecal.addKeyListener(this);
		decalPanel.add(changeDecal);

		r+=30;

		removeDecal=new JButton(header+"Remove"+footer);
		removeDecal.setBounds(130,r,100,20);
		removeDecal.addActionListener(this);
		removeDecal.addKeyListener(this);
		decalPanel.add(removeDecal);

		r+=30;

		return decalPanel;
	}


	private void init() {

		buf=new BufferedImage(CENTER_WIDTH,CENTER_HEIGHT,BufferedImage.TYPE_INT_RGB);
		scene=new Scene(null);
		try {
			loadImages();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadImages() throws IOException {

		File directoryImg=new File("lib");
		File[] files=directoryImg.listFiles();
		int numFiles=files.length;

		worldImages=EditorData.loadWorldImages(files);
		for(int i=0;i<worldImages.length;i++){
			chooseCellTexture.addItem(new ComboElement(Integer.toString(i),Integer.toString(i)));
		}

		worldDecals=EditorData.loadWorldDecalImages(files);
		worldDecalsImages=new BufferedImage[worldDecals.length];
		for(int i=0;i<worldDecals.length;i++){
			chooseDecalTexture.addItem(new ComboElement(Integer.toString(i),Integer.toString(i)));

			int w=worldDecals[i].getWidth();
			int h=worldDecals[i].getHeight();
			worldDecalsImages[i]=new BufferedImage(w,h , BufferedImage.TYPE_INT_RGB);
			fillDecal(worldDecalsImages[i]);
			worldDecalsImages[i].getGraphics().drawImage(worldDecals[i], 0, 0, null);

		}

		wallImages=EditorData.loadWallmages(files);
		for(int i=0;i<wallImages.length;i++){
			chooseWallTexture.addItem(new ComboElement(Integer.toString(i),Integer.toString(i)));
		}

		ArrayList<File> vObjects=new ArrayList<File>();
		for (int i = 0; i < numFiles; i++) {
			File file=new File("lib"+File.separator+"object3D_"+i+"_0");
			if(file.exists()) {
				vObjects.add(file);
			}
		}

		objectImages=new BufferedImage[vObjects.size()];
		objectMeshes=new CubicMesh[vObjects.size()];
		objectDescriptions=new String[vObjects.size()];
		for(int i=0;i<vObjects.size();i++){
			chooseObjectTexture.addItem(new ComboElement(Integer.toString(i),Integer.toString(i)));
			objectImages[i]=ImageIO.read(new File("lib"+File.separator+"object_"+i+".jpg"));
			if(DrawObject.IS_3D){
				objectMeshes[i]=CubicMesh.loadMeshFromFile(new File("lib"+File.separator+"object3D_"+i+"_0"));
				objectDescriptions[i]=objectMeshes[i].getDescription();
			}
		}

		ArrayList<File> vOtherImages=new ArrayList<File>();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().startsWith("other_image_")
					){
				vOtherImages.add(files[i]);
			}
		}
		otherImages=new BufferedImage[vOtherImages.size()];
		for(int i=0;i<vOtherImages.size();i++){
			otherImages[i]=ImageIO.read(new File("lib"+File.separator+"other_image_"+i+".png"));
		}

		ArrayList<File> vOtherTextures=new ArrayList<File>();
		for(int i=0;i<files.length;i++){
			if(files[i].getName().startsWith("other_texture_")
					){
				vOtherTextures.add(files[i]);
			}
		}
		otherTextures=new BufferedImage[vOtherTextures.size()];
		for(int i=0;i<vOtherTextures.size();i++){
			otherTextures[i]=ImageIO.read(new File("lib"+File.separator+"other_texture_"+i+".png"));
		}

	}


	private void fillDecal(BufferedImage bufferedImage) {

		int w=bufferedImage.getWidth();
		int h=bufferedImage.getHeight();
		for(int i=0;i<w;i++) {
			for(int j=0;j<h;j++){
				bufferedImage.setRGB(i, j,DECAL_BACKGROUND_RGB);
			}
		}
	}

	@Override
	public void paint(Graphics g) {
		super.paint(g);
		draw();
	}

	private void draw() {

		int layer=getSelectedLayer();

		Graphics2D graph = (Graphics2D) buf.getGraphics();
		EditorPanel ep=getCenter();

		draw(getCenter(),layer,graph);

		if(graphics==null) {
			graphics=(Graphics2D) ep.getGraphics();
		}

		graphics.drawImage(buf,0,0,null);
	}

	public EditorPanel getCenter(){

		if(VIEW_TYPE==ISO_VIEW) {
			return panelIso;
		} else {
			return panelTop;
		}
	}

	public int getSelectedLayer(){

		if(showLayer.getSelectedIndex()<0) {
			return ALL_LAYERS;
		}

		ComboElement ce= (ComboElement) showLayer.getSelectedItem();

		int layer=Integer.parseInt(ce.getCode());
		return layer;
	}

	private void draw( EditorPanel editorPanel,int layer,Graphics2D graph) {

		graph.setColor(Color.BLACK);
		graph.fillRect(0,0,CENTER_WIDTH,CENTER_HEIGHT);

		editorPanel.setHide_objects(checkHideObjects.isSelected());
		editorPanel.drawScene(scene,layer,graph);

		drawCurrentRect(graph);
	}

	private void addMenuBar() {

		jmb=new JMenuBar();

		jmFile=new JMenu("File");
		jmFile.addMenuListener(this);
		jmb.add(jmFile);

		jmtLoadScene=new JMenuItem("Load scene");
		jmtLoadScene.addActionListener(this);
		jmFile.add(jmtLoadScene);

		jmtSaveScene=new JMenuItem("Save scene");
		jmtSaveScene.addActionListener(this);
		jmFile.add(jmtSaveScene);

		jmView=new JMenu("View");
		jmView.addMenuListener(this);
		jmb.add(jmView);

		jmt_3d_view=new JMenuItem("3D view");
		jmt_3d_view.addActionListener(this);
		jmView.add(jmt_3d_view);

		jmt_top_view=new JMenuItem("Top view");
		jmt_top_view.addActionListener(this);
		jmView.add(jmt_top_view);

		jmView.addSeparator();

		jmtFasterMotion=new JMenuItem("+ motion");
		jmtFasterMotion.addActionListener(this);
		jmView.add(jmtFasterMotion);

		jmtSlowerMotion=new JMenuItem("- motion");
		jmtSlowerMotion.addActionListener(this);
		jmView.add(jmtSlowerMotion);

		jmEdit=new JMenu("Edit");
		jmEdit.addMenuListener(this);
		jmb.add(jmEdit);

		jmtUndo=new JMenuItem("Undo");
		jmtUndo.addActionListener(this);
		jmtUndo.setEnabled(false);
		jmEdit.add(jmtUndo);

		jmChkBlueScale=new JCheckBoxMenuItem("Blue scale");
		jmChkBlueScale.addActionListener(this);
		jmEdit.add(jmChkBlueScale);

		jmOther=new JMenu("Other");
		jmOther.addMenuListener(this);
		jmb.add(jmOther);

		jmtBuildBox=new JMenuItem("Build box");
		jmtBuildBox.addActionListener(this);
		jmOther.add(jmtBuildBox);

		jmtExpandBox=new JMenuItem("Expand scene");
		jmtExpandBox.addActionListener(this);
		jmOther.add(jmtExpandBox);

		jmtDuplicateWalls=new JMenuItem("Duplicate walls");
		jmtDuplicateWalls.addActionListener(this);
		jmOther.add(jmtDuplicateWalls);

		jmMassModify=new JMenuItem("Mass modify");
		jmMassModify.addActionListener(this);
		jmOther.add(jmMassModify);

		jmOther.addSeparator();

		pile_objects_jmt=new JMenuItem("Pile object");
		pile_objects_jmt.addActionListener(this);
		jmOther.add(pile_objects_jmt);

		jmHelp=new JMenu("Help");
		jmHelp.addMenuListener(this);
		jmb.add(jmHelp);
		jmtHelp=new JMenuItem("Help");
		jmtHelp.addActionListener(this);
		jmHelp.add(jmtHelp);

		setJMenuBar(jmb);
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();

		if(toogle_cell==obj || toogle_height==obj || toogle_wall==obj || toogle_object==obj  || toogle_decal==obj){
			CardLayout cl = (CardLayout)(right_tool_options.getLayout());

			mode=((JToggleButton) obj).getActionCommand();
			cl.show(right_tool_options,mode);

		}
		else if(obj==jmtBuildBox){
			buildBox();
		}
		if(obj==jmtExpandBox){
			expandBox();
		}
		else if(obj==jmt_top_view){
			changeView(TOP_VIEW);
		}
		else if(obj==jmt_3d_view){
			changeView(ISO_VIEW);
		}
		else if(obj==change_cell){
			changeCell();
		}
		else if(obj==changeWall){
			changeWall();
		}
		else if(obj==change_height){
			changeHeight();
		}
		else if(obj==changeObject){
			changeObject();
		}
		else if(obj==changeDecal){
			changeDecal();
		}
		else if(obj==delete_object){
			deleteObjects();
		}
		else if(obj==btn_pi_angle){
			rotation_angle.setText(Math.PI);
		}
		else if(obj==btn_pi_angle_2){
			rotation_angle.setText(Math.PI*0.5);
		}
		else if(obj==jmtLoadScene){

			Scene sc = loadScene();
			if(sc!=null){
				scene=sc;
				setSceneData();
			}
			draw();
		}
		else if(obj==jmtSaveScene){
			saveScene(scene);
		}
		else if(obj==chooseCellPanelTexture){
			chooseCellPanelTexture();
		}
		else if(obj==chooseWallPanelTexture){
			chooseWallPanelTexture();
		}
		else if(obj==chooseObjectPanelTexture){
			chooseObjectPanelTexture();
		}
		else if(obj==chooseDecalPanelTexture){
			chooseDecalPanelTexture();
		}
		else if(obj==removeDecal){
			removeDecal();
		}
		else if(obj==jmtUndo){
			undo();
		}
		else if(obj==deselectAll){
			deselectAll();
		}
		else if(obj==moveObjUp){
			moveSelectedObject(0,1,0);
		}
		else if(obj==moveObjDown){
			moveSelectedObject(0,-1,0);
		}
		else if(obj==moveObjLeft){
			moveSelectedObject(-1,0,0);
		}
		else if(obj==moveObjRight){
			moveSelectedObject(+1,0,0);
		}
		else if(obj==moveObjTop){
			moveSelectedObject(0,0,1);
		}
		else if(obj==moveObjBottom){
			moveSelectedObject(0,0,-1);
		}
		else if( obj==pile_objects_jmt && OBJECT_MODE.equals(mode)){
			pileSameSelectedObjects();
		}
		else if(obj==jmtFasterMotion){
			changeMotionIncrement(+1);
		}else if(obj==jmtSlowerMotion){
			changeMotionIncrement(-1);
		}else if(obj==jmChkBlueScale){
			getCenter().emptyGrayScale();
			draw();
		}else if(obj==jmMassModify){
			massModify();
		}
		else if(obj==jmtDuplicateWalls){
			duplicateWalls();
		}
		else if(obj==jmtHelp){
			help();
		}

	}

	private void chooseObjectPanelTexture() {

		TexturesPanel tp=new TexturesPanel(objectImages,objectDescriptions,100,100,true);

		int indx=tp.getSelectedIndex();
		if(indx!=-1){
			chooseObjectTexture.setSelectedIndex(indx+1);
		}
	}

	private void chooseWallPanelTexture() {

		TexturesPanel tp=new TexturesPanel(wallImages,null,100,100,false);

		int indx=tp.getSelectedIndex();
		if(indx!=-1){
			chooseWallTexture.setSelectedIndex(indx+1);
			setWallTexturePanel(indx);
		}
	}

	private void chooseCellPanelTexture() {

		TexturesPanel tp=new TexturesPanel(worldImages,null,100,100,false);

		int indx=tp.getSelectedIndex();
		if(indx!=-1) {
			chooseCellTexture.setSelectedIndex(indx+1);
		}
	}

	private void chooseDecalPanelTexture() {

		TexturesPanel tp=new TexturesPanel(worldDecalsImages,null,100,100,false);

		int indx=tp.getSelectedIndex();
		if(indx!=-1) {
			chooseDecalTexture.setSelectedIndex(indx+1);
		}

	}

	private void moveSelectedObject(int dx, int dy, int dz) {

		prepareUndo();

		String sqty=objMove.getText();

		if(sqty==null || "".equals(sqty)) {
			return;
		}

		double qty=Double.parseDouble(sqty);

		for(int o=0;o<scene.objects.size();o++){

			DrawObject dro=scene.objects.get(o);

			if(!dro.isSelected()) {
				continue;
			}

			dro.setX(dro.getX()+dx*qty);
			dro.setY(dro.getY()+dy*qty);
			dro.setZ(dro.getZ()+dz*qty);

			dro.buildPolygons();
			dro.getMesh().translate(dx*qty, dy*qty, dz*qty);
			setObjectPanelData(dro);
		}
		draw();
	}

	private Scene loadScene() {

		fc=new JFileChooser();
		fc.setDialogType(JFileChooser.OPEN_DIALOG);
		fc.setDialogTitle("Load scene");
		if(currentDirectory!=null) {
			fc.setCurrentDirectory(currentDirectory);
		}
		if(currentFile!=null) {
			fc.setSelectedFile(currentFile);
		}

		int returnVal = fc.showOpenDialog(null);

		if (returnVal == JFileChooser.APPROVE_OPTION) {
			currentDirectory=fc.getCurrentDirectory();
			currentFile=fc.getSelectedFile();

			File file = fc.getSelectedFile();
			Scene scene=loadScene(file);
			return scene;
		}
		return null;
	}


	private void setSceneData() {
		startX.setText(scene.getStartx());
		startY.setText(scene.getStarty());
		int nLayers = scene.map.getNumz();
		setLayersComboData(showLayer,nLayers);
	}


	private void getSceneData() {
		scene.setStartx(startX.getvalue());
		scene.setStarty(startY.getvalue());
		//scene.setStarty(startZ.getvalue());
	}

	private void deleteObjects() {

		ArrayList<DrawObject> filtered=new ArrayList<DrawObject>();

		for (int i = 0; i < scene.objects.size(); i++) {

			DrawObject dro=scene.objects.get(i);

			if(dro.isSelected()){
				continue;
			}
			filtered.add(dro);
		}
		scene.objects=filtered;

		draw();
	}

	private void changeView(int type) {

		remove(getCenter());

		if(type==ISO_VIEW){

			add(panelIso);
		} else {
			add(panelTop);
		}

		VIEW_TYPE=type;
		draw();
	}

	private void changeObject() {

		if(chooseObjectTexture.getSelectedIndex()<=0) {
			return;
		}

		prepareUndo();

		for (int i = 0; i < scene.objects.size(); i++) {

			DrawObject dro=scene.objects.get(i);

			if(dro.isSelected()){

				dro.setRotation_angle(rotation_angle.getvalue());

				dro.setIndex(chooseObjectTexture.getSelectedIndex()-1);
				//dro.setMesh(objectMeshes[dro.getIndex()],dro.active_mesh);
				updateObject(dro);
			}
		}

		draw();
	}

	private void addObject(int x,int y,int z) {

		DrawObject dro=new DrawObject();
		dro.setX(x);
		dro.setY(y);
		dro.setZ(z);

		dro.setHexColor("0000FF");

		if(chooseObjectTexture.getSelectedIndex()>0) {
			dro.setIndex(chooseObjectTexture.getSelectedIndex()-1);
		}

		dro.setRotation_angle(rotation_angle.getvalue());
		setObjectMesh(dro);
		dro.setDimensionFromCubicMesh();
		dro.buildPolygons();

		scene.addObject(dro);

		draw();
	}


	private void changeCell() {

		if(scene==null || scene.map==null) {
			return;
		}

		Block[] blocks = scene.map.blocks;

		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];
			if(bl.isSelected()){
				changeCell(bl);
			}
		}
		draw();
	}

	private void changeDecal() {
		if(scene==null || scene.map==null) {
			return;
		}
		Block[] blocks = scene.map.blocks;

		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];
			if(bl.isSelected()){
				changeDecal(bl);
			}
		}
		draw();
	}



	private void removeDecal() {
		if(scene==null || scene.map==null) {
			return;
		}
		Block[] blocks = scene.map.blocks;

		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];
			if(bl.isSelected()){
				bl.setDecal_index(Block.NO_DECAL_INDEX);
				chooseDecalTexture.setSelectedIndex(Block.NO_DECAL_INDEX);
				bl.buildCubicMesh();
			}
		}
		draw();
	}


	private void changeHeight() {

		if(scene==null || scene.map==null) {
			return;
		}

		prepareUndo();

		Block[] blocks = scene.map.blocks;

		ComboElement vp=(ComboElement) blockHeight.getSelectedItem();

		if(vp==null) {
			return;
		}

		int nz=Integer.parseInt(vp.getValue());

		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];
			if(bl.isSelected()){
				bl.buildCubicMesh();
			}
		}

		draw();
	}

	private void changeCell(Block bl) {

		if(chooseCellTexture.getSelectedIndex()<=0) {
			return;
		}

		if(radioCellTypeCeiling.isSelected()){
			bl.setBlock_type(Block.BLOCK_TYPE_GROUND);
		}
		else if(radioCellTypeGround.isSelected()) {
			bl.setBlock_type(Block.BLOCK_TYPE_GROUND);
		} else {
			bl.setBlock_type(Block.BLOCK_TYPE_GROUND);
		}
		bl.setTexture_index(chooseCellTexture.getSelectedIndex()-1);
		bl.buildCubicMesh();
	}


	private void changeDecal(Block bl) {
		if(chooseDecalTexture.getSelectedIndex()<=0) {
			return;
		}

		bl.setDecal_index(chooseDecalTexture.getSelectedIndex()-1);
		bl.buildCubicMesh();
	}

	private void changeWall() {

		if(scene==null || scene.map==null) {
			return;
		}

		Block[] blocks = scene.map.blocks;

		for (int i = 0; i < blocks.length; i++) {
			Block bl = blocks[i];
			if(bl.isSelected()){
				changeWall(bl);
			}
		}
		draw();
	}

	private void changeWall(Block bl) {

		if(chooseWallTexture.getSelectedIndex()<=0) {
			return;
		}

		if(radioWallTypeFull.isSelected()){
			bl.setBlock_type(Block.BLOCK_TYPE_FULL_WALL);
		}
		else if(radioWallTypeEmpty.isSelected()) {
			bl.setBlock_type(Block.BLOCK_TYPE_EMPTY_WALL);
		} else if(radioWallTypeWindow.isSelected()) {
			bl.setBlock_type(Block.BLOCK_TYPE_WINDOW);
		} else if(radioWallTypeDoor.isSelected()) {
			bl.setBlock_type(Block.BLOCK_TYPE_DOOR);
		} else if(radioWallTypeGrid.isSelected()) {
			bl.setBlock_type(Block.BLOCK_TYPE_GRID);
		}
		bl.setTexture_index(chooseWallTexture.getSelectedIndex()-1);
		bl.buildCubicMesh();
	}

	private void buildBox() {

		SceneBoxCreator sbc=new SceneBoxCreator(null);

		BlocksMesh box = sbc.getBox();
		if(box!=null){
			scene.map=box;
			setSceneData();
		}

		draw();
	}

	private void expandBox() {

		SceneBoxCreator sbc=new SceneBoxCreator(scene.map);

		BlocksMesh box = sbc.getBox();
		if(box!=null){
			scene.map=box;
			setSceneData();
		}
		draw();
	}


	private void duplicateWalls() {
		int layer=getSelectedLayer();

		if(layer==ALL_LAYERS) {
			return;
		}

		prepareUndo();
		scene.map=SceneBoxCreator.duplicateWalls(scene.map,layer);
		setSceneData();
		draw();
		JOptionPane.showMessageDialog(this,"Layer "+layer+" duplicated in layer "+(layer+1)+"!","Done",JOptionPane.OK_OPTION);
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		int code = arg0.getKeyCode();

		EditorPanel ep=getCenter();

		if(code==KeyEvent.VK_UP){
			ep.translate(0,-1);
			draw();
		}
		else if(code==KeyEvent.VK_DOWN){
			ep.translate(0,+1);
			draw();
		}
		else if(code==KeyEvent.VK_LEFT){
			ep.translate(1,0);
			draw();
		}
		else if(code==KeyEvent.VK_RIGHT){
			ep.translate(-1,0);
			draw();
		}
		else if(code==KeyEvent.VK_F1){
			ep.zoomIn();
			draw();
		}
		else if(code==KeyEvent.VK_F2){
			ep.zoomOut();
			draw();
		}
		else if(code==KeyEvent.VK_A)
		{
			chooseWallPanelTexture();
		}
		else if(code==KeyEvent.VK_C)
		{
			changeCell();
		}
		else if(code==KeyEvent.VK_D)
		{
			deleteObjects();
		}
		else if(code==KeyEvent.VK_E)
		{
			deselectAll();
			draw();
		}
		else if(code==KeyEvent.VK_H)
		{
			changeWall();
		}
		else if(code==KeyEvent.VK_M)
		{
			chooseObjectPanelTexture();
		}
		else if(code==KeyEvent.VK_O)
		{
			changeObject();
		}
		else if(code==KeyEvent.VK_Q )
		{
			ep.rotate(+0.1);
			draw();
		}
		else if(code==KeyEvent.VK_T)
		{
			chooseCellPanelTexture();
		}
		else if(code==KeyEvent.VK_U)
		{
			checkMultiselection.setSelected(!checkMultiselection.isSelected());
		}
		else if(code==KeyEvent.VK_W )
		{
			ep.rotate(-0.1);
			draw();
		}
	}
	@Override
	public void mouseClicked(MouseEvent arg0) {

		EditorPanel ep=getCenter();
		int layer=getSelectedLayer();

		if(CELL_MODE.equals(mode)){

			if(arg0.getButton()==MouseEvent.BUTTON1){
				selectPolygon(arg0.getX(),arg0.getY());
			}else if(arg0.getButton()==MouseEvent.BUTTON3){
				deselectAll();
				changePolygon(arg0.getX(),arg0.getY());
			}

			deselectAllObjects();
		}
		else if(WALL_MODE.equals(mode)){

			if(arg0.getButton()==MouseEvent.BUTTON1){
				selectPolygon(arg0.getX(),arg0.getY());
			}else if(arg0.getButton()==MouseEvent.BUTTON3){
				deselectAll();
				changePolygon(arg0.getX(),arg0.getY());
			}

			deselectAllObjects();
		}
		else if(HEIGHT_MODE.equals(mode)){

			if(arg0.getButton()==MouseEvent.BUTTON1){
				selectPolygon(arg0.getX(),arg0.getY());
			}else if(arg0.getButton()==MouseEvent.BUTTON3){
				deselectAll();
				changePolygonHeight(arg0.getX(),arg0.getY());
			}

			deselectAllObjects();
		}
		else if(OBJECT_MODE.equals(mode)){

			if(arg0.getButton()==MouseEvent.BUTTON1){
				selectObject(arg0.getX(),arg0.getY());
			}
			else if(arg0.getButton()==MouseEvent.BUTTON3){

				deselectAll();

				if(ep instanceof SceneEditorTopPanel){

					int x=ep.calcAssInverseX(arg0.getX(),arg0.getY(),0);
					int y=ep.calcAssInverseY(arg0.getX(),arg0.getY(),0);
					int z=(int) getMinZDrawingObjects(scene.map,layer,null);

					addObject(x,y,z);

				}else if(ep instanceof SceneEditorIsoPanel){
					putObjectInCell(arg0.getX(),arg0.getY());
				}
			}
			deselectAllPolygons();
		}else if(DECAL_MODE.equals(mode)){

			if(arg0.getButton()==MouseEvent.BUTTON1){
				selectPolygon(arg0.getX(),arg0.getY());
			}else if(arg0.getButton()==MouseEvent.BUTTON3){
				deselectAll();
				changePolygon(arg0.getX(),arg0.getY());
			}

			deselectAllObjects();
		}
		draw();
	}


	private void putObjectInCell(int x, int y) {

		if(scene==null || scene.map==null || scene.map.blocks==null) {
			return;
		}

		EditorPanel ep=getCenter();


		ArrayList<Block> polygons=ep.getClickedPolygons(x,y,scene.map.blocks);

		if(!polygons.isEmpty()){

			Block bl=polygons.get(0);

			int z=(int) getMinZDrawingObjects(scene.map,getSelectedLayer(),bl);

			if(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL ||
					bl.getBlock_type()==Block.BLOCK_TYPE_GROUND
					){
				addObject((int)bl.getX(),(int)bl.getY(),z);
			}
		}
	}

	private void changePolygon(int x, int y) {

		if(scene==null || scene.map==null) {
			return;
		}

		EditorPanel ep=getCenter();

		ArrayList<Block> cBlocks=ep.getClickedPolygons(x,y,scene.map.blocks);

		for (int i = 0; i < cBlocks.size(); i++) {

			Block bl =cBlocks.get(i);

			if(CELL_MODE.equals(mode)){
				changeCell(bl);
				bl.buildCubicMesh();
			}
			else if(WALL_MODE.equals(mode)){
				changeWall(bl);
				bl.buildCubicMesh();
			}
			else if(DECAL_MODE.equals(mode)){
				changeDecal(bl);
				bl.buildCubicMesh();
			}
		}
	}

	private void changePolygonHeight(int x, int y) {

		if(scene==null || scene.map==null) {
			return;
		}

		EditorPanel ep=getCenter();

		ComboElement vp=(ComboElement) blockHeight.getSelectedItem();

		if(vp==null) {
			return;
		}

		int nz=Integer.parseInt(vp.getValue());

		for (int i = 0; i < scene.map.blocks.length; i++) {

			Block bl =scene.map.blocks[i];

			boolean clicked=ep.isPolygonClicked(x,y,bl);

			if(clicked){
				bl.buildCubicMesh();
			}
		}
	}

	private void deselectAll() {

		if(scene.map==null) {
			return;
		}

		deselectAllPolygons();
		deselectAllObjects();

		draw();
	}

	private void deselectAllObjects() {

		for (int i = 0; i < scene.objects.size(); i++) {

			DrawObject dro=scene.objects.get(i);
			dro.setSelected(false);
		}
	}


	private void deselectAllPolygons() {

		if(scene.map==null || scene.map.blocks==null) {
			return;
		}

		Block[] blocks = scene.map.blocks;

		for (int i = 0; i < blocks.length; i++) {

			Block bl = blocks[i];
			bl.setSelected(false);
			bl.setSelectedZ(-1);
		}
	}

	private void selectObject(int x, int y) {

		EditorPanel ep=getCenter();
		ep.selectObjects(x,y,scene.objects);
	}

	public void setObjectPanelData(DrawObject dro) {

		rotation_angle.setText(dro.getRotation_angle());
		chooseObjectTexture.setSelectedIndex(dro.getIndex()+1);
	}

	private void selectPolygon(int x, int y) {

		if(scene==null || scene.map==null) {
			return;
		}

		EditorPanel ep=getCenter();
		ep.selectPolygons( x,y,scene.map.blocks);
	}

	public void setCellPanelData(Block bl) {

		if(CELL_MODE.equals(mode) || WALL_MODE.equals(mode) || DECAL_MODE.equals(mode)){

			if(bl.getBlock_type()==Block.BLOCK_TYPE_GROUND  ){

				chooseCellTexture.setSelectedIndex(bl.getTexture_index()+1);
				radioCellTypeGround.setSelected(bl.getBlock_type()==Block.BLOCK_TYPE_GROUND);
				if(bl.getDecal_index()!=Block.NO_DECAL_INDEX) {
					chooseDecalTexture.setSelectedIndex(bl.getDecal_index()+1);
				}else{
					chooseDecalTexture.setSelectedIndex(-1);
				}
			}
			else  if(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL  ||
					bl.getBlock_type()==Block.BLOCK_TYPE_FULL_WALL ||
					bl.getBlock_type()==Block.BLOCK_TYPE_WINDOW ||
					bl.getBlock_type()==Block.BLOCK_TYPE_DOOR ||
					bl.getBlock_type()==Block.BLOCK_TYPE_GRID
					){

				radioWallTypeFull.setSelected(bl.getBlock_type()==Block.BLOCK_TYPE_FULL_WALL);
				radioWallTypeEmpty.setSelected(bl.getBlock_type()==Block.BLOCK_TYPE_EMPTY_WALL);
				radioWallTypeWindow.setSelected(bl.getBlock_type()==Block.BLOCK_TYPE_WINDOW);
				radioWallTypeDoor.setSelected(bl.getBlock_type()==Block.BLOCK_TYPE_DOOR);
				radioWallTypeGrid.setSelected(bl.getBlock_type()==Block.BLOCK_TYPE_GRID);

				setWallTexturePanel(bl.getTexture_index());
			}


		} else if(HEIGHT_MODE.equals(mode)){
			setBlockHeight(bl);
		}

		String txt= dfc.format(bl.getX())+","+dfc.format(bl.getY());
		cellCoordinates.setText(txt);
	}


	private void setBlockHeight(Block bl) {

		DefaultComboBoxModel dcbm= (DefaultComboBoxModel) blockHeight.getModel();

		for(int i=0;i<dcbm.getSize();i++){

			ComboElement vp=(ComboElement) dcbm.getElementAt(i);

			/*if(vp.getCode().equals(""+bl.getNz()))
			{

			  blockHeight.setSelectedIndex(i);
			  break;
			}	*/
		}
	}

	public int getBlockHeight(){

		ComboElement vp= (ComboElement) blockHeight.getSelectedItem();

		if(vp==null) {
			return Block.zBlocks;
		}

		String val=vp.getCode();
		return Integer.parseInt(val);
	}

	private void setWallTexturePanel(int block_index) {

		chooseWallTexture.setSelectedIndex(block_index+1);

		ComboElement val=(ComboElement) chooseWallTexture.getSelectedItem();
		if(!"".equals(val.getCode())){

			int num=Integer.parseInt(val.getCode());

			BufferedImage icon=new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);
			icon.getGraphics().drawImage(wallImages[num],0,0,wallTextureLabel.getWidth(),wallTextureLabel.getHeight(),null);

			if(radioWallTypeWindow.isSelected() || radioWallTypeDoor.isSelected() || radioWallTypeGrid.isSelected()){

				int other_index=0;

				if(radioWallTypeWindow.isSelected()) {
					other_index=OTHER_IMAGES_INDEX_WINDOW;
				} else if(radioWallTypeDoor.isSelected()) {
					other_index=OTHER_IMAGES_INDEX_DOOR;
				}
				else if(radioWallTypeGrid.isSelected()) {
					other_index=OTHER_IMAGES_INDEX_GRID;
				}

				icon.getGraphics().drawImage(otherImages[other_index],0,0,wallTextureLabel.getWidth(),wallTextureLabel.getHeight(),null);
			}
			ImageIcon ii=new ImageIcon(icon);
			wallTextureLabel.setIcon(ii);
		} else {
			wallTextureLabel.setIcon(null);
		}
	}
	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {

		EditorPanel ep=getCenter();
		int move=arg0.getUnitsToScroll();

		if(move>0) {
			ep.translateAxes(0,1);
		} else {
			ep.translateAxes(0,-1);
		}
		draw();
	}
	@Override
	public void propertyChange(PropertyChangeEvent arg0) {

		if("paintDirtyRegions".equals(arg0.getPropertyName()) && redrawAfterMenu)
		{
			draw();
			redrawAfterMenu=false;
		}
		else if("roadUpdate".equals(arg0.getPropertyName()))
		{
			draw();
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {

		isDrawCurrentRect=true;
		updateSize(e);
		draw();
	}

	private void drawCurrentRect(Graphics2D bufGraphics) {

		if(!isDrawCurrentRect) {
			return;
		}
		int x0=Math.min(currentRect.x,currentRect.x+currentRect.width);
		int x1=Math.max(currentRect.x,currentRect.x+currentRect.width);
		int y0=Math.min(currentRect.y,currentRect.y+currentRect.height);
		int y1=Math.max(currentRect.y,currentRect.y+currentRect.height);

		bufGraphics.setColor(Color.WHITE);
		bufGraphics.drawRect(x0,y0,x1-x0,y1-y0);
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		int x = arg0.getX();
		int y = arg0.getY();
		currentRect = new Rectangle(x, y, 0, 0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {

		isDrawCurrentRect=false;
		updateSize(arg0);
		if(CELL_MODE.equals(mode) || WALL_MODE.equals(mode) || DECAL_MODE.equals(mode) ) {
			selectPointsWithRectangle();
		}else if(OBJECT_MODE.equals(mode)){
			selectObjectsWithRectangle(scene.objects);
		}
		draw();
	}

	private void selectPointsWithRectangle() {

		int x0=Math.min(currentRect.x,currentRect.x+currentRect.width);
		int x1=Math.max(currentRect.x,currentRect.x+currentRect.width);
		int y0=Math.min(currentRect.y,currentRect.y+currentRect.height);
		int y1=Math.max(currentRect.y,currentRect.y+currentRect.height);

		Rectangle selRectangle=new Rectangle(x0,y0,x1-x0,y1-y0);

		int layer=getSelectedLayer();

		if(scene.map!=null){

			EditorPanel ep=getCenter();

			Block[] blocks = scene.map.blocks;

			for (int i = 0; i < blocks.length; i++) {

				Block bl = blocks[i];

				if(bl.getLayer()!=layer) {
					continue;
				}

				Polygon3D pol = ep.builProjectedPolygon(bl.getInvertedLowerBase(),bl.getMesh().points);

				if(selRectangle.intersects(pol.getBounds())) {
					bl.setSelected(true);
				}
				else if(!checkMultiselection.isSelected()) {
					bl.setSelected(false);
				}
			}
		}
	}

	public boolean selectObjectsWithRectangle(ArrayList<DrawObject> drawObjects) {

		if(drawObjects==null) {
			return false;
		}

		int x0=Math.min(currentRect.x,currentRect.x+currentRect.width);
		int x1=Math.max(currentRect.x,currentRect.x+currentRect.width);
		int y0=Math.min(currentRect.y,currentRect.y+currentRect.height);
		int y1=Math.max(currentRect.y,currentRect.y+currentRect.height);

		EditorPanel ep=getCenter();
		//select point from road
		boolean found=false;
		int sz=drawObjects.size();

		for(int j=0;j<sz;j++){


			DrawObject dro=drawObjects.get(j);

			int xo=ep.calcAssX(dro.getX(),dro.getY(),dro.getZ());
			int yo=ep.calcAssY(dro.getX(),dro.getY(),dro.getZ());

			if(xo>=x0 && xo<=x1 && yo>=y0 && yo<=y1  ){

				dro.setSelected(true);
				found=true;

			}
			else if(!checkMultiselection.isSelected()) {
				dro.setSelected(false);
			}


		}

		return found;
	}

	public static double getMinZDrawingObjects(BlocksMesh map, int layer, Block bl) {

		double minZ=0;

		if(layer==SceneEditor.ALL_LAYERS){
			minZ=map.getZside()*map.getNumz()*map.getZside();
		}else{
			minZ=map.getZside()*layer;
		}

		if(bl!=null && bl.getBlock_type()==Block.BLOCK_TYPE_FULL_WALL) {
			minZ+=map.getZside();
		}

		return minZ;
	}



	private void updateSize(MouseEvent e) {
		int x = e.getX();
		int y = e.getY();
		currentRect.setSize(x - currentRect.x,
				y - currentRect.y);
	}

	private void help() {
		HelpPanel hp=new HelpPanel(300,200,this.getX()+100,this.getY(),HelpPanel.SCENE_EDITOR_HELP_TEXT,this);
	}

	@Override
	public void saveScene(Scene scene) {

		getSceneData();
		super.saveScene(scene);
	}

	public static Scene loadScene(File file) {

		Scene sc= Editor.loadScene(file);

		if(sc==null) {
			return null;
		}

		for (int i = 0; i < sc.objects.size(); i++) {

			DrawObject dro=sc.objects.get(i);
			setObjectMesh(dro);

			/*dro.setMesh(objectMeshes[dro.getIndex()],dro.active_mesh);*/
			dro.setDimensionFromCubicMesh();
			dro.buildPolygons();
		}
		return sc;
	}

	public static void setObjectMesh(DrawObject dro) {

		DPoint3D center=null;

		CubicMesh cm=objectMeshes[dro.getIndex()].clone();

		DPoint3D point = cm.point000;
		double dx=-point.x+dro.x;
		double dy=-point.y+dro.y;
		double dz=-point.z+dro.z;

		cm.translate(dx,dy,dz);

		center=cm.findCentroid();

		if(dro.getRotation_angle()!=0) {
			cm.rotate(center.x,center.y,Math.cos(dro.getRotation_angle()),Math.sin(dro.getRotation_angle()));
		}
		dro.setMesh(cm,0);
	}
	@Override
	public void itemStateChanged(ItemEvent arg0) {

		Object o=arg0.getSource();
		if(o==chooseCellTexture){

			ComboElement val=(ComboElement) chooseCellTexture.getSelectedItem();
			if(!"".equals(val.getCode())){

				int num=Integer.parseInt(val.getCode());

				BufferedImage icon=new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);
				icon.getGraphics().drawImage(worldImages[num],0,0,cellTextureLabel.getWidth(),cellTextureLabel.getHeight(),null);
				ImageIcon ii=new ImageIcon(icon);
				cellTextureLabel.setIcon(ii);
			} else {
				cellTextureLabel.setIcon(null);
			}
		}
		else if(o==chooseObjectTexture){

			ComboElement val=(ComboElement) chooseObjectTexture.getSelectedItem();
			if(!"".equals(val.getCode())){

				int num=Integer.parseInt(val.getCode());

				BufferedImage icon=new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);
				icon.getGraphics().drawImage(objectImages[num],0,0,objectTextureLabel.getWidth(),objectTextureLabel.getHeight(),null);
				ImageIcon ii=new ImageIcon(icon);
				objectTextureLabel.setIcon(ii);
			} else {
				objectTextureLabel.setIcon(null);
			}

		}else if(o==radioWallTypeFull || o==radioWallTypeWindow || o==radioWallTypeGrid){

			int textIndex=chooseWallTexture.getSelectedIndex()-1;
			setWallTexturePanel(textIndex);
		}else if(o==chooseDecalTexture){
			ComboElement val=(ComboElement) chooseDecalTexture.getSelectedItem();
			if(val!=null && !"".equals(val.getCode())){

				int num=Integer.parseInt(val.getCode());

				BufferedImage icon=new BufferedImage(100,100,BufferedImage.TYPE_3BYTE_BGR);
				icon.getGraphics().drawImage(worldDecalsImages[num],0,0,decalTextureLabel.getWidth(),decalTextureLabel.getHeight(),null);
				ImageIcon ii=new ImageIcon(icon);
				decalTextureLabel.setIcon(ii);
			} else {
				decalTextureLabel.setIcon(null);
			}
		}
	}

	private void changeMotionIncrement(int i) {
		getCenter().changeMotionIncrement(i);
	}

	private void prepareUndo() {

		if(scene==null || scene.map==null) {
			return;
		}

		jmtUndo.setEnabled(true);

		if(oldObjects.size()==MAX_STACK_SIZE){
			oldObjects.removeLast();
		}
		oldObjects.push(cloneObjects(scene));
	}


	private void undo(){

		if(!oldObjects.isEmpty()){
			scene.objects=oldObjects.pop();
			draw();
		}
		if(oldObjects.isEmpty()){
			jmtUndo.setEnabled(false);
		}
	}

	public boolean isUseBlueScale() {
		return jmChkBlueScale.isSelected();
	}

	private void massModify() {

		SceneEditorMassModifiy remm=new SceneEditorMassModifiy();
		Object ret = remm.getReturnValue();

		if(ret!=null){

			prepareUndo();

			if(mode.equals(OBJECT_MODE)){
				SceneEditorMassModifiy.massModifyObjects((SceneEditorMassModifiy)ret,scene.objects);
			}

			deselectAllObjects();
			draw();
		}
	}

	private void pileSameSelectedObjects() {

		if(scene==null || scene.objects==null) {
			return;
		}

		int oSize=scene.objects.size();

		prepareUndo();

		HashMap<String, String> doubleSelection=new HashMap<String, String>();

		ArrayList<DrawObject> piledObjects=new ArrayList<DrawObject>();

		for (int i = 0; i < oSize; i++) {

			DrawObject dro = scene.objects.get(i);

			if(dro.isSelected()){

				int pSize=piledObjects.size();
				boolean found=false;
				for (int j = 0; j < pSize; j++) {
					DrawObject piledDro = piledObjects.get(j);
					if(piledDro.getX()==dro.getX() && piledDro.getY()==dro.getY() ){
						found=true;
						if(piledDro.getZ()<dro.getZ()){
							piledObjects.set(j,dro);
							break;
						}
					}
				}
				if(!found){
					piledObjects.add(dro);
				}
				dro.setSelected(false);
			}
		}

		int pSize=piledObjects.size();
		for (int i = 0; i < pSize; i++) {

			DrawObject dro = piledObjects.get(i);

			DrawObject clonedObject = (DrawObject) dro.clone();

			double dz0=dro.getDz();
			clonedObject.setX(dro.getX());
			clonedObject.setY(dro.getY());
			clonedObject.setZ(dro.getZ()+dz0);

			updateObject(clonedObject);
			scene.addObject(clonedObject);

		}

		deselectAllObjects();
		draw();

	}


	private void updateObject(DrawObject clonedObject) {
		setObjectMesh(clonedObject);
		clonedObject.setDimensionFromCubicMesh();
		clonedObject.buildPolygons();
	}


	private class FileTransferhandler extends TransferHandler{

		@Override
		public boolean canImport(JComponent comp, DataFlavor[] transferFlavors) {

			for(int i=0;i<transferFlavors.length;i++){

				if (!transferFlavors[i].equals(DataFlavor.javaFileListFlavor)) {
					return false;
				}
			}
			return true;
		}
		@Override
		public boolean importData(JComponent comp, Transferable t) {

			try {
				List list=(List) t.getTransferData(DataFlavor.javaFileListFlavor);
				Iterator itera = list.iterator();
				while(itera.hasNext()){

					Object o=itera.next();
					if(!(o instanceof File)) {
						continue;
					}
					File file=(File) o;
					currentDirectory=file.getParentFile();
					currentFile=file;

					Scene sc = loadScene(file);
					if(sc!=null) {
						scene=sc;
					}
					setSceneData();
					draw();
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
}