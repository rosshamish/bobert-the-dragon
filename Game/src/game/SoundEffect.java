package game;

import java.io.File;
import java.io.IOException;
import javax.sound.sampled.*;

public class SoundEffect {
    
    public static Volume volume = Volume.MEDIUM;
    private Clip clip;
    public static String resourcesPath = "resources/audio/sfx/";

    SoundEffect(String soundFileName) {
        try {
            String soundFileFullPath = resourcesPath + soundFileName;
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(soundFileFullPath));
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
    
    public void stop(Volume _vol) {
        clip.stop();
    }
    
    private void setVolume(Volume _volume) {
        volume = _volume;
        FloatControl vol = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        vol.setValue(volume.getGain());
    }
}
