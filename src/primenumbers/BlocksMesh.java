package primenumbers;

import primenumbers.scene.Block;

public class BlocksMesh {

	private int numx=0;
	private int numy=0;
	private int numz=0;
	private double x0=0;
	private double y0=0;
	private double z0=0;

	private int side=0;
	private int zside=0;

	public Block[] blocks=null;



	public BlocksMesh(int numx, int numy,int numz, int side,int zside, double x0, double y0, double z0) {

		this.numx = numx;
		this.numy = numy;
		this.numz = numz;
		this.side = side;
		this.zside = zside;
		this.x0 = x0;
		this.y0 = y0;
		this.z0 = z0;

		blocks=new Block[numx*numy*numz];

		for (int i = 0; i <numx; i++) {
			for (int j = 0; j < numy; j++) {
				for (int kLayer = 0; kLayer< numz; kLayer++) {

					double x=i*side;
					double y=j*side;
					double z=kLayer*zside;
					double dx=side;
					double dy=side;
					double dz=zside;

					int bType=Block.BLOCK_TYPE_EMPTY_WALL;
					if(kLayer==Block.BLOCK_GROUND_LAYER) {
						bType=Block.BLOCK_TYPE_GROUND;
					}

					Block bl=new Block(bType,x,y,z,dx,dy,dz,kLayer);
					blocks[(i+j*numx)+kLayer*numx*numy]=bl;
				}
			}
		}
	}


	public BlocksMesh() {
		// TODO Auto-generated constructor stub
	}

	public int getSide() {
		return side;
	}
	public void setSide(int side) {
		this.side = side;
	}
	public int getNumx() {
		return numx;
	}
	public void setNumx(int numx) {
		this.numx = numx;
	}
	public int getNumy() {
		return numy;
	}
	public void setNumy(int numy) {
		this.numy = numy;
	}
	public double getX0() {
		return x0;
	}
	public void setX0(double x0) {
		this.x0 = x0;
	}
	public double getY0() {
		return y0;
	}
	public void setY0(double y0) {
		this.y0 = y0;
	}

	public int getNumz() {
		return numz;
	}


	public void setNumz(int numz) {
		this.numz = numz;
	}


	public int getZside() {
		return zside;
	}


	public void setZside(int zside) {
		this.zside = zside;
	}


	public double getZ0() {
		return z0;
	}


	public void setZ0(double z0) {
		this.z0 = z0;
	}

	@Override
	public BlocksMesh clone() {

		BlocksMesh pm=new BlocksMesh();
		pm.blocks=new Block[this.blocks.length];

		for (int i = 0; i < blocks.length; i++) {
			pm.blocks[i]=(Block) blocks[i].clone();
		}


		pm.setNumx(getNumx());
		pm.setNumy(getNumy());
		pm.setSide(getSide());
		pm.setX0(getX0());
		pm.setY0(getY0());
		pm.setZ0(getZ0());
		pm.setNumz(getNumz());
		pm.setZside(getZside());

		return pm;
	}
	
}