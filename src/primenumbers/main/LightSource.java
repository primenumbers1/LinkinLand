package primenumbers.main;

import primenumbers.DPoint3D;

class LightSource {

	private DPoint3D position;

	private DPoint3D xAxis=null;
	private DPoint3D yAxis=null;
	private DPoint3D zAxis=null;

	//light point axes

	private double cos[][]=null;


	LightSource(DPoint3D position, DPoint3D yAxis) {
		super();
		this.position = position;
		this.yAxis = yAxis;
		cos=new double[3][3];

		calculateLightAxes();
	}
	public DPoint3D getPosition() {
		return position;
	}
	public void setPosition(DPoint3D position) {
		this.position = position;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {

		LightSource ls=new LightSource();

		ls.position=position.clone();

		ls.xAxis=xAxis.clone();
		ls.yAxis=yAxis.clone();
		ls.zAxis=zAxis.clone();

		return ls;

	}

	public LightSource() {
		super();
	}
	private void calculateLightAxes(){


		xAxis=new DPoint3D(yAxis.y,-yAxis.x,0).calculateVersor();
		zAxis=new DPoint3D(-yAxis.z*yAxis.x,
				-yAxis.z*yAxis.y,
				yAxis.y*yAxis.y+yAxis.x*yAxis.x).calculateVersor();

		/*System.out.println("x:"+xAxis);
		System.out.println("y:"+yAxis);
		System.out.println("z:"+zAxis);*/

		cos[0][0]=xAxis.x;
		cos[0][1]=xAxis.y;
		cos[0][2]=xAxis.z;

		cos[1][0]=yAxis.x;
		cos[1][1]=yAxis.y;
		cos[1][2]=yAxis.z;

		cos[2][0]=zAxis.x;
		cos[2][1]=zAxis.y;
		cos[2][2]=zAxis.z;

	}
	
}