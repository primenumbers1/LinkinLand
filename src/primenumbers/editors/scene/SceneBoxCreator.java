package primenumbers.editors.scene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import primenumbers.BlocksMesh;
import primenumbers.editors.IntegerTextField;
import primenumbers.scene.Block;

class SceneBoxCreator extends JDialog implements ActionListener {

	private static final int TEXTURE_INDEX_0 = 0;
	private IntegerTextField box_num_x;
	private IntegerTextField box_num_y;

	private int WIDTH=280;
	private int BOTTOM_HEIGHT=100;
	private int HEIGHT=100;
	private JButton generate;
	private JButton delete;
	private JPanel box_panel;

	private boolean saved=false;
	private boolean is_expand_mode;
	transient private BlocksMesh map;
	private IntegerTextField box_num_z;

	public SceneBoxCreator(BlocksMesh map){

		if(map!=null) {
			is_expand_mode=true;
		}

		this.map=map;

		setSize(WIDTH,HEIGHT+BOTTOM_HEIGHT);
		setLayout(null);
		setModal(true);
		setTitle("Create box");

		int r=10;

		box_panel=getBoxPanel();
		box_panel.setBounds(0, 0, WIDTH, HEIGHT);
		add(box_panel);

		r+=HEIGHT;


		if(is_expand_mode) {
			generate=new JButton("Expand box");
		} else {
			generate=new JButton("Create box");
		}

		generate.setBounds(10,r,130,20);
		generate.addActionListener(this);

		add(generate);
		delete=new JButton("Cancel");
		delete.setBounds(150,r,100,20);
		delete.addActionListener(this);
		add(delete);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		//debug
		setData();

		setVisible(true);
	}


	private void setData() {

		if(!is_expand_mode){
			box_num_x.setText(10);
			box_num_y.setText(10);
			box_num_z.setText(Block.zBlocks);
		}else{
			box_num_x.setText(map.getNumx());
			box_num_y.setText(map.getNumy());
			box_num_z.setText(map.getNumz());
		}
	}


	private JPanel getBoxPanel() {

		JPanel box=new JPanel();
		box.setLayout(null);

		int r=10;
		int column=100;

		JLabel jlb=new JLabel("Num X points");
		jlb.setBounds(5, r, 100, 20);
		box.add(jlb);
		box_num_x=new IntegerTextField();
		box_num_x.setBounds(column, r, 100, 20);
		box.add(box_num_x);

		if(is_expand_mode){
			jlb=new JLabel(">"+map.getNumx());
			jlb.setBounds(column+110, r, 100, 20);
			box.add(jlb);
		}

		r+=30;
		jlb=new JLabel("Num Y points");
		jlb.setBounds(5, r, 100, 20);
		box.add(jlb);
		box_num_y=new IntegerTextField();
		box_num_y.setBounds(column, r, 100, 20);
		box.add(box_num_y);

		if(is_expand_mode){
			jlb=new JLabel(">"+map.getNumy());
			jlb.setBounds(column+110, r, 100, 20);
			box.add(jlb);
		}

		r+=30;
		jlb=new JLabel("Num Z points");
		jlb.setBounds(5, r, 100, 20);
		box.add(jlb);
		box_num_z=new IntegerTextField();
		box_num_z.setBounds(column, r, 100, 20);
		box.add(box_num_z);

		if(is_expand_mode){

			jlb=new JLabel(">"+map.getNumz());
			jlb.setBounds(column+110, r, 100, 20);
			box.add(jlb);
		}

		return box;
	}


	@Override
	public void actionPerformed(ActionEvent e) {

		Object o=e.getSource();

		if(o==generate){
			saved=true;
			dispose();
		}
		else if(o==delete){
			saved=false;
			dispose();
		}
	}

	public BlocksMesh getBox(){

		if(is_expand_mode) {
			return expandTemplate();
		} else {
			return  generateTemplate();
		}
	}

	private BlocksMesh generateTemplate(){

		if(!saved) {
			return null;
		}

		String snumx=box_num_x.getText();
		String snumy=box_num_y.getText();
		String snumz=box_num_z.getText();

		if(snumx==null || snumx.length()==0
				|| snumy==null || snumy.length()==0
				|| snumz==null || snumz.length()==0
				) {
			return null;
		}

		int numx=box_num_x.getvalue();
		int numy=box_num_y.getvalue();
		int numz=box_num_z.getvalue();

		return new BlocksMesh(numx,numy,numz,Block.side,Block.zSide,0,0,0);

	}

	private BlocksMesh expandTemplate() {
		if(!saved) {
			return null;
		}

		String snumx=box_num_x.getText();
		String snumy=box_num_y.getText();
		String snumz=box_num_z.getText();

		if(snumx==null || snumx.length()==0
				|| snumy==null || snumy.length()==0
				|| snumz==null || snumz.length()==0
				) {
			return null;
		}

		int numx=box_num_x.getvalue();
		int numy=box_num_y.getvalue();
		int numz=box_num_z.getvalue();

		if(numx<map.getNumx() || numy<map.getNumy()){

			JOptionPane.showMessageDialog(this,"Can't shrink the original map!","Error",JOptionPane.ERROR_MESSAGE);
			saved=false;
			return null;
		}

		BlocksMesh nbm=new BlocksMesh(numx,numy,numz,Block.side,Block.zSide,0,0,0);

		for (int i = 0; i < numx; i++) {

			for (int j = 0; j < numy; j++) {

				for (int kLayer = 0; kLayer < numz; kLayer++) {

					int pos=pos(i,j,kLayer,numx,numy);

					if(i<map.getNumx() && j<map.getNumy() && kLayer<map.getNumz()){

						int oldPos=pos(i,j,kLayer,map.getNumx(),map.getNumy());
						nbm.blocks[pos]=map.blocks[oldPos];
						nbm.blocks[pos].buildCubicMesh();
					}
				}
			}
		}

		return nbm;
	}

	public static BlocksMesh duplicateWalls(BlocksMesh map2, int selectedLayer) {

		int numx=map2.getNumx();
		int numy=map2.getNumy();
		int numz=map2.getNumz();

		BlocksMesh nbm=new BlocksMesh(numx,numy,numz,Block.side,Block.zSide,0,0,0);

		for (int i = 0; i < numx; i++) {

			for (int j = 0; j < numy; j++) {

				for (int kLayer = 0; kLayer < numz; kLayer++) {

					int pos=pos(i,j,kLayer,numx,numy);
					int oldPos=pos(i,j,kLayer,map2.getNumx(),map2.getNumy());
					nbm.blocks[pos]=map2.blocks[oldPos];

					if(kLayer==selectedLayer+1){

						int oldSelectedPos=pos(i,j,kLayer-1,map2.getNumx(),map2.getNumy());
						Block selBlock = map2.blocks[oldSelectedPos];
						int selBlockType=selBlock.getBlock_type();
						int selTextureIndex=selBlock.getTexture_index();
						int selDecalIndex=selBlock.getDecal_index();
						if(selBlockType==Block.BLOCK_TYPE_GROUND){
							nbm.blocks[pos].setBlock_type(Block.BLOCK_TYPE_EMPTY_WALL);
							nbm.blocks[pos].setTexture_index(TEXTURE_INDEX_0);
						} else {
							nbm.blocks[pos].setBlock_type(selBlockType);
							nbm.blocks[pos].setTexture_index(selTextureIndex);
							nbm.blocks[pos].setDecal_index(selDecalIndex);
						}
					}
					nbm.blocks[pos].buildCubicMesh();
				}
			}
		}

		return nbm;
	}


	private static int pos(int i, int j,int kLayer,int numx, int numy) {
		return (i+j*numx)+kLayer*numx*numy;
	}
	
}