package game;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;
import rosslib.RossLib;

public enum Audio {

    NARRATION1(RossLib.getResource("resources/audio/Narration/NarrationIntro.wav").getPath()),
    BACKGROUNDMUSIC(RossLib.getResource("resources/audio/music/SuaveBobertMusic.wav").getPath());
    
    public static Volume volume = Volume.MEDIUM;
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

    public void play(Volume _vol) {
        this.setVolume(_vol);
        if (volume != Volume.MUTE) {
            if (clip.isRunning()) {
                clip.stop();
            }

            clip.setFramePosition(0);
            clip.start();
        }
    }

    public void loop(Volume _vol) {
        this.setVolume(_vol);
        if (volume != Volume.MUTE) {
            clip.loop(Clip.LOOP_CONTINUOUSLY);
        }
    }
    
    public void stop() {
        clip.stop();
    }
    
    private void setVolume(Volume _volume) {
        volume = _volume;
        FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        vol.setValue(volume.getGain());
    }
}
