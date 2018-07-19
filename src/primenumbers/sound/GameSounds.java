package primenumbers.sound;

import java.io.File;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.SourceDataLine;

public class GameSounds {

	public static final String SOUNDS_DIRECTORY="lib"+File.separator+"sounds"+File.separator+"";


	public static Clip getClip(String file){

		File shotSoundFile=new File(file);
		AudioInputStream ais;
		try {
			ais = AudioSystem.getAudioInputStream(shotSoundFile);
			Clip sound = AudioSystem.getClip();
			sound.open(ais);
			return sound;

		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;

	}

	public static void makeSound(Clip sound) {


		if (sound.isRunning()) {
			sound.stop();
		}

		sound.setFramePosition(0);
		sound.start();


	}

	public static void makeSoundLoop(Clip sound,int loop) {

		if (sound.isRunning()) {
			sound.stop();
		}

		sound.setFramePosition(0);
		sound.loop(loop);

	}

	public static void stop(Clip sound) {

		if (sound.isRunning()) {
			sound.stop();
		}

	}



	public static AudioInputStream getAudioInputStream(String file){

		AudioInputStream audioInputStream =null;

		try {

			File soundFile = new File("gameover.wav");
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);


		} catch (Exception e) {
			e.printStackTrace();
		}


		return audioInputStream;
	}

	public static void makeSound(AudioInputStream audioInputStream) {

		int BUFFER_SIZE = 64*1024;  // 64 KB

		try {

			AudioFormat audioFormat = audioInputStream.getFormat();
			DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
			SourceDataLine soundLine = (SourceDataLine) AudioSystem.getLine(info);
			soundLine.open(audioFormat);

			soundLine.start();
			int nBytesRead = 0;
			byte[] sampledData = new byte[BUFFER_SIZE];
			while (nBytesRead != -1) {
				nBytesRead = audioInputStream.read(sampledData, 0, sampledData.length);
				if (nBytesRead >= 0) {
					// Writes audio data to the mixer via this source data line.
					soundLine.write(sampledData, 0, nBytesRead);
				}
			}
			soundLine.drain();
			soundLine.close();


		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	
}