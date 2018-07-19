package primenumbers.main.loader;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import primenumbers.main.Game;

public class GameLoader extends JDialog implements ActionListener {

	private int WIDTH=600;
	private int LEFT_TOP_BORDER=10;
	private int RIGHT_TOP_BORDER=60;

	private static final int LOC_X=200;
	private static final int LOC_Y=200;

	private JPanel center;
	private String header="<html><body>";
	private String footer="</body></html>";

	private static final Color BACKGROUND_COLOR=new Color(255,255,255);

	private JButton[] mapRadios=null;

	private int selectedIndex=0;
	private JTextField playerName;
	private JRadioButton chooseLevelEasy;
	private JRadioButton chooseLevelNormal;
	private JRadioButton chooseLevelHard;
	private JPanel centerLeft;
	private JPanel centerRight;

	public static final String DEFAULT_MAP="default";

	public GameLoader(){

		selectedIndex=0;

		setTitle("Game loader");
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		List<String> mapNames=loadMapNames();
		int addedHeight=40*mapNames.size();
		int HEIGHT=LEFT_TOP_BORDER+addedHeight;

		setLayout(null);
		setSize(WIDTH,HEIGHT);
		setLocation(LOC_X,LOC_Y);
		setModal(true);

		center=new JPanel();
		center.setLayout(null);
		center.setBounds(0,0,WIDTH,HEIGHT);
		center.setBackground(BACKGROUND_COLOR);
		add(center);

		mapRadios=new JButton[mapNames.size()];

		int r=10;

		JLabel label=new JLabel();
		label.setBounds(30,r,170,50);
		label.setText(getLoadGameText());
		center.add(label);

		centerLeft=new JPanel();
		centerLeft.setLayout(null);
		centerLeft.setBounds(0,0,WIDTH/2,HEIGHT);
		centerLeft.setBackground(BACKGROUND_COLOR);
		center.add(centerLeft);

		centerRight=new JPanel();
		centerRight.setLayout(null);
		centerRight.setBounds(WIDTH/2,RIGHT_TOP_BORDER,WIDTH/2,400);
		centerRight.setBackground(BACKGROUND_COLOR);
		center.add(centerRight);

		r+=60;

		for (int i = 0; i < mapNames.size(); i++) {

			String mName = mapNames.get(i);

			mapRadios[i]=new JButton("X");
			mapRadios[i].setBounds(10,r,50,20);
			if(i==0) {
				mapRadios[i].setSelected(true);
			}
			mapRadios[i].setActionCommand(mName);
			mapRadios[i].addActionListener(this);
			centerLeft.add(mapRadios[i]);

			label=new JLabel();
			label.setBounds(70,r,100,20);
			label.setText(mName);
			centerLeft.add(label);

			r+=30;
		}

		r=10;

		label=new JLabel();
		label.setBounds(10,r,70,20);
		label.setText("Your name:");
		centerRight.add(label);
		playerName=new JTextField(20);
		playerName.setBounds(90,r,150,20);
		centerRight.add(playerName);

		r+=30;

		buildLevelPanel(r,centerRight);

		setVisible(true);
	}

	private void buildLevelPanel(int r, JPanel center2) {

		JLabel label=new JLabel();
		label.setBounds(10,r,100,20);
		label.setText("Choose level:");
		center2.add(label);

		chooseLevelEasy=new JRadioButton("Easy");
		chooseLevelEasy.setBounds(120,r,100,20);
		chooseLevelEasy.setSelected(true);
		chooseLevelEasy.setBackground(BACKGROUND_COLOR);
		center2.add(chooseLevelEasy);

		r+=30;

		chooseLevelNormal=new JRadioButton("Normal");
		chooseLevelNormal.setBounds(120,r,100,20);
		chooseLevelNormal.setBackground(BACKGROUND_COLOR);
		center2.add(chooseLevelNormal);

		r+=30;

		chooseLevelHard=new JRadioButton("hard");
		chooseLevelHard.setBounds(120,r,150,20);
		chooseLevelHard.setBackground(BACKGROUND_COLOR);
		center2.add(chooseLevelHard);

		ButtonGroup bg=new ButtonGroup();
		bg.add(chooseLevelEasy);
		bg.add(chooseLevelNormal);
		bg.add(chooseLevelHard);
	}

	private List<String> loadMapNames() {

		ArrayList<String> ret=new ArrayList<String>();
		ret.add(DEFAULT_MAP);

		File directoryImg=new File("lib");
		File[] files=directoryImg.listFiles();

		for (int i = 0; i < files.length; i++) {
			String fName=files[i].getName();

			if(fName.startsWith("scene_") && !fName.endsWith(DEFAULT_MAP)){
				String mName=fName.substring(fName.indexOf("_")+1);
				ret.add(mName);
			}
		}

		return ret;
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		Object obj = arg0.getSource();

		for (int i = 0; i < mapRadios.length; i++) {
			if(obj==mapRadios[i]){
				selectedIndex=i;
				go();
			}
		}
	}

	private void go() {
		dispose();
	}

	public String getMap(){

		if(selectedIndex>=0) {
			return mapRadios[selectedIndex].getActionCommand();
		}
		return DEFAULT_MAP;
	}

	public String getPlayerName(){
		return playerName.getText();
	}

	private String getLoadGameText() {

		Random rn = new Random();
		int index=rn.nextInt(quotes.length);
		String msg=quotes[index];

		msg=header+
				"<font color='#FF0000'>"+msg+"</font>"+
				footer;
		return msg;
	}

	public int getGameLevel() {
		if(chooseLevelEasy.isSelected()) {
			return Game.LEVEL_EASY;
		}else if(chooseLevelNormal.isSelected()){
			return Game.LEVEL_NORMAL;
		}
		else if(chooseLevelHard.isSelected()){
			return Game.LEVEL_HARD;
		}
		return Game.LEVEL_EASY;
	}

	private String[] quotes={
			"Choose how you want to die...",
			"Next stop to hell!"
	};
	
}