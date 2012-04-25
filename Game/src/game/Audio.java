package game;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class Audio {

    
    
//    JUMP("resources/audio/sfx/Jumping.wav"),
//    WOOHOO("resources/audio/sfx/Jumping2.wav"),
//    BACKGROUNDMUSIC("resources/audio/music/SuaveBobertMusic.wav");

    public static enum Volume {

        MUTE, LOW, MEDIUM, HIGH
    }
    
    public static Volume volume = Volume.HIGH;
    private Clip clip;

    Audio(String soundFileName) {
        try {

            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFileName));
            clip = AudioSystem.getClip();
            clip.open(audioInputStream);

        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void play() {

        if (volume != Volume.MUTE) {
            if (clip.isRunning()) {
                clip.stop();
            }

            clip.setFramePosition(0);
            clip.start();
        }
    }

    static void init() {
//        values();
    }

    public void loop() {
        
        clip.loop(Clip.LOOP_CONTINUOUSLY);
        
    }
    
    public void stop() {
        clip.stop();
    }
}
