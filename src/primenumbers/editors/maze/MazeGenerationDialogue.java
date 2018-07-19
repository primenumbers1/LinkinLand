package primenumbers.editors.maze;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import primenumbers.editors.IntegerTextField;

public class MazeGenerationDialogue extends JDialog implements ActionListener {

	private int WIDTH=280;
	private int BOTTOM_HEIGHT=100;
	private int HEIGHT=100;
	private JButton generate;
	private JButton delete;
	private boolean saved=false;
	private JPanel sizePanel;
	private IntegerTextField sizeNumX;
	private IntegerTextField sizeNumY;

	public MazeGenerationDialogue(){


		setSize(WIDTH,HEIGHT+BOTTOM_HEIGHT);
		setLayout(null);
		setModal(true);
		setTitle("Create box");

		int r=10;

		sizePanel=getSizePanel();
		sizePanel.setBounds(0, 0, WIDTH, HEIGHT);
		add(sizePanel);

		r+=HEIGHT;

		generate=new JButton("Generate");
		generate.setBounds(10,r,130,20);
		generate.addActionListener(this);

		add(generate);
		delete=new JButton("Cancel");
		delete.setBounds(150,r,100,20);
		delete.addActionListener(this);
		add(delete);

		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setVisible(true);
	}

	private JPanel getSizePanel() {

		JPanel sizePanel=new JPanel();
		sizePanel.setLayout(null);

		int r=10;
		int column=100;

		JLabel jlb=new JLabel("Num X points");
		jlb.setBounds(5, r, 100, 20);
		sizePanel.add(jlb);
		sizeNumX=new IntegerTextField();
		sizeNumX.setBounds(column, r, 100, 20);
		sizeNumX.setText(50);
		sizePanel.add(sizeNumX);

		r+=30;
		jlb=new JLabel("Num Y points");
		jlb.setBounds(5, r, 100, 20);
		sizePanel.add(jlb);
		sizeNumY=new IntegerTextField();
		sizeNumY.setBounds(column, r, 100, 20);
		sizeNumY.setText(50);
		sizePanel.add(sizeNumY);

		return sizePanel;
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

	public boolean isSaved() {
		return saved;
	}

	public int getNxSize(){
		return sizeNumX.getvalue();
	}

	public int getNySize(){
		return sizeNumY.getvalue();
	}

	
}