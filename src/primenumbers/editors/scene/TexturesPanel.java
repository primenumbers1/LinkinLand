package primenumbers.editors.scene;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.Comparator;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.Border;
import javax.swing.border.EtchedBorder;

public class TexturesPanel extends JDialog implements ActionListener, MouseListener {

	private JButton cancel=null;

	private JPanel center;
	private JLabel[] textureLabels;

	private int TEXTURE_SIDE_X=100;
	private int TEXTURE_SIDE_Y=100;

	private int DELTAY=25;
	private int DELTAX=25;
	private int BORDER=90;

	private int X_SIZE=800;
	private int Y_SIZE=500;

	private int columns=5;
	private int selectedIndex=-1;

	private OrderedTexture[] orderedTextures;


	public TexturesPanel(BufferedImage[] textures,String[] descriptions,int TEXTURE_SIDE_X,int TEXTURE_SIDE_Y,boolean isToOrderbyName){

		this.TEXTURE_SIDE_X=TEXTURE_SIDE_X;
		this.TEXTURE_SIDE_Y=TEXTURE_SIDE_Y;

		setTitle("Choose texture");
		setLayout(null);



		int HEIGHT=BORDER+(DELTAY+TEXTURE_SIDE_Y)*((int)Math.ceil(textures.length*1.0/columns));
		int WIDTH=BORDER+(TEXTURE_SIDE_X+DELTAX)*columns;
		setSize(20+X_SIZE,Y_SIZE+50);

		setModal(true);
		center=new JPanel(null);
		center.setPreferredSize(new Dimension(WIDTH,HEIGHT));
		JScrollPane scrp=new JScrollPane(center);
		scrp.setBounds(0,0,X_SIZE,Y_SIZE);

		add(scrp);
		int r=10;

		textureLabels=new JLabel[textures.length];
		orderedTextures=new OrderedTexture[textures.length];

		if(isToOrderbyName){

			for (int i = 0; i < orderedTextures.length; i++) {
				orderedTextures[i]=new OrderedTexture(textures[i], descriptions[i],i);
			}
			Arrays.sort(orderedTextures, new TextureDescriptionComparator());

		}else{

			for (int i = 0; i < orderedTextures.length; i++) {
				orderedTextures[i]=new OrderedTexture(textures[i], "",i);
			}
		}


		for(int i=0;i<orderedTextures.length;i++){

			textureLabels[i]=new JLabel();
			textureLabels[i].setBounds(BORDER/2+(DELTAX+TEXTURE_SIDE_X)*(i%columns),r,TEXTURE_SIDE_X,TEXTURE_SIDE_Y);
			Border border=BorderFactory.createEtchedBorder(EtchedBorder.RAISED);
			textureLabels[i].setBorder(border);
			BufferedImage icon=new BufferedImage(TEXTURE_SIDE_X,TEXTURE_SIDE_Y,BufferedImage.TYPE_INT_RGB);
			icon.getGraphics().drawImage(orderedTextures[i].getTexture(),0,0,textureLabels[i].getWidth(),textureLabels[i].getHeight(),null);
			ImageIcon ii=new ImageIcon(icon);

			textureLabels[i].setIcon(ii);

			center.add(textureLabels[i]);

			if(descriptions!=null) {
				textureLabels[i].setToolTipText(orderedTextures[i].getDescription());
			}

			textureLabels[i].addMouseListener(this);

			if(i%columns==columns-1) {
				r+=DELTAY+TEXTURE_SIDE_Y;
			}
		}
		if((textures.length)%columns!=0) {
			r+=TEXTURE_SIDE_Y;
		}

		r+=30;

		cancel=new JButton("Cancel");
		cancel.setBounds(100,r,80,20);
		center.add(cancel);
		cancel.addActionListener(this);
		//System.out.println(cancel.getBounds());

		setVisible(true);

	}



	public int getSelectedIndex() {

		return selectedIndex;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		Object obj = e.getSource();
		if(obj==cancel){
			selectedIndex=-1;
			dispose();
		}
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {

	}

	@Override
	public void mouseExited(MouseEvent arg0) {

	}

	@Override
	public void mousePressed(MouseEvent arg0) {

		Object obj = arg0.getSource();

		for(int i=0;i<textureLabels.length;i++){

			if(obj==textureLabels[i]){
				selectedIndex=orderedTextures[i].getIndex();
				dispose();
			}
		}

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	class OrderedTexture{

		BufferedImage texture;
		String description;
		int index;

		public OrderedTexture(BufferedImage texture, String description,int index) {

			super();
			this.texture = texture;
			this.index = index;
			if(description!=null) {
				this.description = description;
			} else {
				this.description = "";
			}
		}

		public BufferedImage getTexture() {
			return texture;
		}
		public void setTexture(BufferedImage texture) {
			this.texture = texture;
		}
		public String getDescription() {
			return description;
		}
		public void setDescription(String description) {

			if(description!=null) {
				this.description = description;
			} else {
				this.description = "";
			}
		}

		public int getIndex() {
			return index;
		}

		public void setIndex(int index) {
			this.index = index;
		}
	}

	class TextureDescriptionComparator implements Comparator{


		@Override
		public int compare(Object o1, Object o2) {
			OrderedTexture ot1=(OrderedTexture) o1;
			OrderedTexture ot2=(OrderedTexture) o2;

			if(ot1.getDescription().isEmpty() && !ot2.getDescription().isEmpty() ) {
				return -1;
			} else if(!ot1.getDescription().isEmpty() && ot2.getDescription().isEmpty() ) {
				return +1;
			} else {
				return ot1.getDescription().compareTo(ot2.getDescription());
			}
		}

	}
	
}