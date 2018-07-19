package primenumbers.main.gameover;

import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import primenumbers.main.MainFrame;
import primenumbers.main.loader.GameLoader;

public class GameOver extends JDialog implements ActionListener {

	private int WIDTH=400;
	private int HEIGHT=240;
	
	private JPanel center;
	private JButton goButton;
	
	private static Color BACKGROUND_COLOR=new Color(255,255,255);
	
	private String header="<html><body>";
	private String footer="</body></html>";
	private GameLoader gameLoader;

	public GameOver(MainFrame mainFrame, GameStatistics gameStatistics) {

		
		this.gameLoader=mainFrame.getGameLoader();
		
		String title="Game over";
				
		setTitle(title);
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		Point loc = mainFrame.getLocation();

		int LOC_X=loc.x+(mainFrame.WIDTH-WIDTH)/2;
		int LOC_Y=loc.y+(mainFrame.HEIGHT-HEIGHT)/2;

		setLayout(null);
		setSize(WIDTH,HEIGHT);
		setLocation(LOC_X,LOC_Y);
		setModal(true);

		center=new JPanel();
		center.setLayout(null);
		center.setBounds(0,0,WIDTH,HEIGHT);
		center.setBackground(BACKGROUND_COLOR);
		add(center);

		int r=10;		 


		JLabel label=new JLabel();
		label.setBounds(30,r,340,50);
		label.setText(getGameOverText());
		center.add(label);
		
		r+=50;
		
		label=new JLabel();
		label.setBounds(30,r,340,60);
		label.setText(getAccountText(gameStatistics));
		center.add(label);

		r+=100;

		goButton=new JButton("Exit");
		goButton.addActionListener(this);
		goButton.setBounds(180,r,80,20);
		center.add(goButton);
		setVisible(true);
	}




	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		Object obj = arg0.getSource();
		
		if(obj==goButton){
			go();
		}
		
	}
	
	private void go() {
		
		System.exit(0);
		
	}
	
	
	private String getAccountText(GameStatistics gameStatistics) {
		
		   Date now=new Date();
		   long pTime= (long) ((now.getTime()-gameStatistics.getStartTime().getTime())/1000.0);
		   
			
			String pn=gameLoader.getPlayerName();
			
			if(pn==null || pn.equals(""))
				pn="You";
		
		   String msg=pn+" killed:"+gameStatistics.getKilled()+"<br>";
		   msg+=pn+" destroyed:"+gameStatistics.getDestroyed_objects()+" objects"+"<br>";
		   msg+=pn+" shoot:"+gameStatistics.getTotalShots()+" shots"+"<br>";
		   msg+=pn+" played:"+pTime+" seconds"+"<br>";;
			
		   msg=header+
					"<font color='#FF0000'>"+msg+"</font>"+
					footer;
			
			return msg;
	}
	
	
	private String getGameOverText() {
		
		Random rn = new Random();
		int index=rn.nextInt(quotes.length);
		String msg=quotes[index];
		
		msg=header+
				"<font color='#FF0000'>"+msg+"</font>"+
				footer;
		
		return msg;
	}
	
	
	private String[] quotes={
			
			
			"Sorry dude, they got you!",
			"You're dead,<br>this must happen, sooner or later...",
			"Dogs are feasting on your flesh!",
			
	};
	
}