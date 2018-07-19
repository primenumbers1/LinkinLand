package primenumbers.main.gameover;

import java.util.Date;

import primenumbers.DrawObject;
import primenumbers.Monster;

public class GameStatistics {

	private int killed=0;
	private int destroyed_objects=0;

	private Date startTime=null;

	private int totalShots=0;

	public GameStatistics(){

		startTime=new Date();
	}

	public void checkKilled(DrawObject dro) {

		if(dro instanceof Monster) {
			Monster monster = (Monster)dro;
			int monsterLife=monster.hit();
			if(monsterLife<=0){
				killed++;
				dro.setVisible(false);
			}
		} else {
			destroyed_objects++;
			dro.setVisible(false);
		}
	}

	public int getKilled() {
		return killed;
	}

	public void setKilled(int killed) {
		this.killed = killed;
	}

	public int getDestroyed_objects() {
		return destroyed_objects;
	}

	public void setDestroyed_objects(int destroyed_objects) {
		this.destroyed_objects = destroyed_objects;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}

	public int getTotalShots() {
		return totalShots;
	}

	public void setTotalShots(int totalShots) {
		this.totalShots = totalShots;
	}

	public void addShot() {
		totalShots++;
	}
}