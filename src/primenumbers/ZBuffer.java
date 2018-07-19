package primenumbers;

import java.awt.Color;

public final class ZBuffer {

	public int[] rgbColor;
	private double[] z;
	private boolean[] empty;
	private int size=0;

	/*public double p_x=0;
		public double p_y=0;
		public double p_z=0;

		public double[] pcf_values=null;*/

	public ZBuffer(int size){

		this.size=size;
		rgbColor=new int[size];
		z=new double[size];
		empty=new boolean[size];
	}


	public int getRgbColor(int index) {
		return rgbColor[index];
	}
	public void setRgbColor(int rgbColor,int index) {
		this.rgbColor[index] = rgbColor;
	}
	public double getZ(int index) {
		return z[index];
	}
	private void setZ(double z, int index) {
		this.z[index] = z;
	}

	public ZBuffer() {
		super();
	}

	public static Color  fromHexToColor(String col){



		int r=Integer.parseInt(col.substring(0,2),16);
		int g=Integer.parseInt(col.substring(2,4),16);
		int b=Integer.parseInt(col.substring(4,6),16);

		Color color=new Color(r,g,b);

		return color;
	}

	private static String addZeros(String hexString) {

		if(hexString.length()==1) {
			return "0"+hexString;
		} else {
			return hexString;
		}
	}
	@Deprecated
	public static int  pickRGBColorFromTexture(
			Texture texture,double px,double py,double pz,
			DPoint3D xDirection,DPoint3D yDirection, DPoint3D origin,int deltaX,int deltaY,
			BarycentricCoordinates bc
			){

		int x=0;
		int y=0;

		if(bc!=null){

			DPoint3D p=bc.getBarycentricCoordinates(px,py,pz);

			DPoint2D p0=bc.pt0;
			DPoint2D p1=bc.pt1;
			DPoint2D p2=bc.pt2;

			x=(int) (p.x*(p0.x)+p.y*p1.x+(1-p.x-p.y)*p2.x);
			y=(int) (p.x*(p0.y)+p.y*p1.y+(1-p.x-p.y)*p2.y);
		}


		int w=texture.getWidth();
		int h=texture.getHeight();

		//border fixed condition
		/*if(x<0) x=0;
			if(x>=w) x=w-1;
			if(y<0) y=0;
			if(y>=h) y=h-1;*/

		//border periodic condition
		if(x<0) {
			x=w-1+x%w;
		}
		if(x>=w) {
			x=x%w;
		}
		if(y<0) {
			y=h-1+y%h;
		}
		if(y>=h) {
			y=y%h;
		}

		int argb= Texture.getRGB(texture,null,x, h-y-1);
		return argb;

	}

	/*public static Point3D  pickTexturePositionPCoordinates(Texture texture,double px,double py,double pz,Point3D xDirection,Point3D yDirection, Point3D origin,int deltaX,int deltaY){



			double x=0;
			double y=0;

			if(origin!=null){


				 x= Point3D.calculateDotProduct(px-origin.x,py-origin.y, pz-origin.z,xDirection)+deltaX;
				 y= Point3D.calculateDotProduct(px-origin.x,py-origin.y, pz-origin.z,yDirection)+deltaY;


			}
			else
			{
				  x= Point3D.calculateDotProduct(px,py,pz,xDirection)+deltaX;
				  y= Point3D.calculateDotProduct(px,py,pz,yDirection)+deltaY;

			}



			int w=texture.getWidth();
			int h=texture.getHeight();

			//border fixed conditions
			//if(x<0) x=0;
			//if(x>=w)x=w-1;
			//if(y<0) y=0;
			//if(y>=h) y=h-1;

			//border periodic condition
			if(x<0) x=w+x%w;
			if(x>=w) x=x%w;
			if(y<0)	y=h+y%h;
			if(y>=h) y=y%h;


			Point3D point=new Point3D();
			point.x=x;
			point.y=h-y-1;//?
			return point;


		}*/

	public void update(double xs,double ys,double zs, int rgbColor,boolean empty,int index) {


		if(isEmpty(index) ||  getZ(index)>ys ){

			setZ(ys,index);
			setRgbColor(rgbColor,index);
			setEmpty(empty,index);

		}

	}

	public boolean isToUpdate(double ys,int index){


		return isEmpty(index) ||  getZ(index)>ys;
	}

	public boolean isEmpty(int index) {

		return empty[index];
	}


	public void set(double xs,double ys,double zs, int rgbColor,boolean empty,int index) {

		setZ(ys,index);
		setRgbColor(rgbColor,index);
		setEmpty(empty,index);
	}


	public void setEmpty(boolean b, int index) {
		empty[index]=b;

	}


	public int getSize() {
		return size;
	}


	public void setSize(int size) {
		this.size = size;
	}
	
}