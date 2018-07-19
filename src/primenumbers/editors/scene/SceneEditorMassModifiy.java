package primenumbers.editors.scene;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;

import primenumbers.DrawObject;
import primenumbers.editors.DoubleTextField;

public class SceneEditorMassModifiy extends JDialog implements ActionListener{

	private int WIDTH=300;
	private int HEIGHT=260;

	private JPanel center;

	private JButton update;

	private JButton cancel;

	private Object returnValue=null;

	private static int MODE_NO_MODE=-1;

	private int mode=MODE_NO_MODE;
	private JRadioButton alignMaxX;
	private JRadioButton alignMinX;
	private JRadioButton alignMaxY;
	private JRadioButton alignMinY;
	private JRadioButton commonAngle;
	private DoubleTextField commonAngleValue;

	private static int MODE_MAX_X=0;
	private static int MODE_MIN_X=1;
	private static int MODE_MAX_Y=2;
	private static int MODE_MIN_Y=3;
	private static int MODE_ANGLE=4;

	private String header="<html><body>";
	private String footer="</body></html>";
	private double rotation_angle=0;
	private JButton pi_2_angle;
	private JButton pi_angle;

	public SceneEditorMassModifiy(){

		mode=MODE_NO_MODE;

		returnValue=null;

		setTitle("Mass modify");
		setLayout(null);

		setSize(WIDTH,HEIGHT);
		setModal(true);

		center=new JPanel(null);
		center.setBounds(0,0,WIDTH,HEIGHT);
		add(center);

		int r=10;
		int col1=120;
		int col2=225;

		JLabel lbl=new JLabel("Allignement");
		lbl.setBounds(10,r,100,20);
		center.add(lbl);

		r+=30;
		alignMaxX=new JRadioButton("Max x");
		alignMaxX.setBounds(10,r,100,20);
		center.add(alignMaxX);

		commonAngle=new JRadioButton("Angle");
		commonAngle.setBounds(col1,r,100,20);
		center.add(commonAngle);


		r+=30;
		alignMinX=new JRadioButton("Min x");
		alignMinX.setBounds(10,r,100,20);
		center.add(alignMinX);

		commonAngleValue=new DoubleTextField();
		commonAngleValue.setBounds(col1,r,100,20);
		center.add(commonAngleValue);

		pi_angle=new JButton(header+"&pi;"+footer);
		pi_angle.setBounds(col2,r,50,20);
		pi_angle.addActionListener(this);
		center.add(pi_angle);

		pi_2_angle=new JButton(header+"&pi;/2"+footer);
		pi_2_angle.setBounds(col2,r+30,50,20);
		pi_2_angle.addActionListener(this);
		center.add(pi_2_angle);

		r+=30;
		alignMaxY=new JRadioButton("Max y");
		alignMaxY.setBounds(10,r,100,20);
		center.add(alignMaxY);

		r+=30;
		alignMinY=new JRadioButton("Min y");
		alignMinY.setBounds(10,r,100,20);
		center.add(alignMinY);

		ButtonGroup bg=new ButtonGroup();
		bg.add(alignMaxX);
		bg.add(alignMinX);
		bg.add(alignMaxY);
		bg.add(alignMinY);
		bg.add(commonAngle);

		r+=30;

		update=new JButton("Update");
		update.setBounds(10,r,80,20);
		center.add(update);
		update.addActionListener(this);

		cancel=new JButton("Cancel");
		cancel.setBounds(100,r,80,20);
		center.add(cancel);
		cancel.addActionListener(this);

		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {

		Object obj = arg0.getSource();

		if(obj==update){

			update();
			dispose();

		}
		else if(obj==cancel){
			returnValue=null;
			dispose();
		}else if(obj==pi_2_angle){
			commonAngleValue.setText(Math.PI*0.5);
		}else if(obj==pi_angle){
			commonAngleValue.setText(Math.PI);
		}
	}

	private void update() {

		returnValue=this;

		if(alignMaxX.isSelected()) {
			mode=MODE_MAX_X;
		} else if(alignMinX.isSelected()) {
			mode=MODE_MIN_X;
		} else if(alignMaxY.isSelected()) {
			mode=MODE_MAX_Y;
		} else if(alignMinY.isSelected()) {
			mode=MODE_MIN_Y;
		} else if(commonAngle.isSelected()){
			mode=MODE_ANGLE;
			rotation_angle=commonAngleValue.getvalue();
		}
		else {
			returnValue=null;
		}
	}

	public Object getReturnValue() {
		return returnValue;
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
	}

	public static void massModifyObjects(SceneEditorMassModifiy ret, ArrayList<DrawObject> drawObjects) {

		if(drawObjects==null) {
			return;
		}

		int sz=drawObjects.size();

		double minX=0;
		double maxX=0;
		double minY=0;
		double maxY=0;

		int counter=0;

		for (int i = 0; i <sz ; i++) {

			DrawObject dro=drawObjects.get(i);

			if(dro.isSelected()){

				if(counter==0){
					minX=dro.getX();
					maxX=dro.getX();
					minY=dro.getY();
					maxY=dro.getY();
				}else{

					if(dro.getX()<minX){
						minX=dro.getX();
					}

					if(dro.getY()<minY){
						minY=dro.getY();
					}

					if(dro.getX()>maxX){
						maxX=dro.getX();
					}

					if(dro.getY()>maxY){
						maxY=dro.getY();
					}
				}
				counter++;
			}
		}

		for (int i = 0; i <sz ; i++) {

			DrawObject dro=drawObjects.get(i);

			if(dro.isSelected()){

				double oldX=dro.getX();
				double oldY=dro.getY();

				if(ret.getMode()==MODE_MAX_X){
					dro.setX(maxX);
				}else if(ret.getMode()==MODE_MIN_X){
					dro.setX(minX);
				}else if(ret.getMode()==MODE_MAX_Y){
					dro.setY(maxY);
				}else if(ret.getMode()==MODE_MIN_Y){
					dro.setY(minY);
				}else if(ret.getMode()==MODE_ANGLE){
					dro.setRotation_angle(ret.getRotation_angle());
				}

				SceneEditor.setObjectMesh(dro);
				dro.setDimensionFromCubicMesh();
				dro.buildPolygons();
				//dro.getMesh().translate(dro.getX()-oldX,dro.getY()-oldY, 0);
			}
		}

	}



	public double getRotation_angle() {
		return rotation_angle;
	}

	public void setRotation_angle(double rotation_angle) {
		this.rotation_angle = rotation_angle;
	}
	
}