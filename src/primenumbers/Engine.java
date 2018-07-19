package primenumbers;

import primenumbers.main.MainFrame;

public class Engine extends Thread {

	public static final int dt=50;

	private boolean running=true;

	private MainFrame mainFrame=null;

	private long MS_PER_FRAME=50;

	public Engine(MainFrame mainFrame) {


		this.mainFrame=mainFrame;
	}

	private long startTime;
	private long endTime;
	private long frameTimes = 0;
	private short frames = 0;

	@Override
	public void run() {

		startTime = (int) System.currentTimeMillis();
		while(running){

			try {

				endTime =  System.currentTimeMillis();

				mainFrame.up();

				try {
					long delay=endTime + MS_PER_FRAME - System.currentTimeMillis();
					if(delay>0) {
						sleep(delay);
					}
				} catch (InterruptedException e) {
				}

				endTime = (int) System.currentTimeMillis();

				frameTimes = frameTimes + endTime - startTime;
				startTime=endTime;
				frames++;

				if(frameTimes >= 1000)
				{
					System.out.println("FPS:"+Long.toString(frames));
					frames = 0;
					frameTimes = 0;
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


	public boolean isRunning() {
		return running;
	}


	public void setRunning(boolean running) {
		this.running = running;
	}
	
}
