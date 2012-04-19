package game;

import java.io.*;
import java.net.URL;
import javax.sound.sampled.*;

public enum Audio {

    JUMP("resources/audio/sfx/Jumping.wav"),
    WOOHOO("resources/audio/sfx/Jumping2.wav"),
    BACKGROUNDMUSIC("resources/audio/music/SuaveBobertMusic.wav");

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
        values();
    }
    
    public void loop(){
        if (!clip.isActive()){
            System.out.println("HEYYYYYYYO");
            
    }
    }
}
