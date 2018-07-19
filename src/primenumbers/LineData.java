package primenumbers;

import java.util.ArrayList;

import primenumbers.scene.Block;

public class LineData implements Cloneable {

	private ArrayList<LineDataVertex> lineDatas=new ArrayList<LineDataVertex>();
	private boolean isSelected=false;

	private double shadowCosin=1;

	private String data=null;
	private String data2=null;

	private int texture_index=0;
	private int decal_index=Block.NO_DECAL_INDEX;
	private String hexColor=GREEN_HEX;

	private static final String GREEN_HEX="00FF00";

	public int size(){
		return lineDatas.size();
	}

	void addIndex(int n){

		LineDataVertex ldv=new LineDataVertex();
		ldv.setVertex_index(n);

		lineDatas.add(ldv);
	}

	public void resetIndexes(){
		lineDatas.clear();
	}


	public void addIndex(int n, int tn, double ptx,double pty) {

		LineDataVertex ldv=new LineDataVertex();
		ldv.setVertex_index(n);
		ldv.setVertex_texture_index(tn);
		ldv.setVertex_texture_x(ptx);
		ldv.setVertex_texture_y(pty);

		lineDatas.add(ldv);
	}

	public DPoint2D getVertexTexturePoint(int i){

		LineDataVertex ldv=getItem(i);
		return new DPoint2D(ldv.getVertex_texture_x(),ldv.getVertex_texture_y());
	}

	public int getIndex(int i){

		LineDataVertex ldv=getItem(i);
		return ldv.getVertex_index();
	}

	public int getVertex_texture_index(int i){

		LineDataVertex ldv=getItem(i);
		return ldv.getVertex_texture_index();
	}

	private LineDataVertex getItem(int i){

		LineDataVertex ldv=lineDatas.get(i);
		return ldv;
	}

	@Override
	public String toString() {

		return decomposeLineData(this);
	}

	public String getHexColor() {
		return hexColor;
	}

	public void setHexColor(String hexColor) {
		this.hexColor = hexColor;
	}

	public int getTexture_index() {
		return texture_index;
	}

	public void setTexture_index(int texture_index) {
		this.texture_index = texture_index;
	}
	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}


	public String getData2() {
		return data2;
	}

	public void setData2(String data2) {
		this.data2 = data2;
	}


	public LineData(){}

	public boolean isSelected() {
		return isSelected;
	}

	public void setSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	@Override
	public LineData clone() {

		LineData ldnew=new LineData();

		for(int i=0;i<size();i++){

			DPoint2D pt=getVertexTexturePoint(i);

			ldnew.addIndex(getIndex(i),getVertex_texture_index(i),pt.x,pt.y);
		}
		ldnew.texture_index=texture_index;
		ldnew.shadowCosin=shadowCosin;
		ldnew.data=data;
		ldnew.data2=data2;
		ldnew.decal_index=decal_index;
		ldnew.hexColor=hexColor;

		return ldnew;
	}

	private String decomposeLineData(LineData ld) {

		String str="";

		if(data!=null) {
			str+="["+data+"]";
		}

		int size=ld.size();

		for(int j=0;j<size;j++){

			if(j>0) {
				str+=",";
			}

			LineDataVertex ldv=ld.getItem(j);
			str+=ldv.getVertex_index()+"/"+ldv.getVertex_texture_index();
		}

		return str;
	}

	public double getShadowCosin() {
		return shadowCosin;
	}

	public void setShadowCosin(double shadowCosin) {
		this.shadowCosin = shadowCosin;
	}

	public int getDecal_index() {
		return decal_index;
	}

	public void setDecal_index(int decal_index) {
		this.decal_index = decal_index;
	}
	
}