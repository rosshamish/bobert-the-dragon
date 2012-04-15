package game;

import javax.swing.JFrame;
import java.io.*;
import javax.sound.sampled.*;

/**
 * @author Ross-Desktop
 */
public class Main {
    
    static final int B_WINDOW_HEIGHT = 700;
    static final int B_WINDOW_BAR_HEIGHT = 24;
    static final int B_WINDOW_CANVAS_HEIGHT = B_WINDOW_HEIGHT - B_WINDOW_BAR_HEIGHT;
    static final int B_WINDOW_WIDTH = 1200;
    static BobertFrame bFrame;
    
    public static void main(String[] args) {
        bFrame = new BobertFrame();
        bFrame.setTitle("Bobert the Dragon ... (c) 2012 Bobert-the-Dragon Productions");
        bFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bFrame.setSize(B_WINDOW_WIDTH, B_WINDOW_HEIGHT);
        bFrame.setResizable(false);
        bFrame.setLocationRelativeTo(null);
        bFrame.setVisible(true);
        
        sound = new File("SuaveBobertMusic.wav");
        new Thread(play).start();
    }
    
    static File sound;
    static boolean muted = false;
    static float volume = 100.0f; // volume goes from 0 to 100
    static float pan = 0.0f; // plays from left and right speakers equally. Dunno why we would need this, but why the fuck not?
    
    static double seconds = 0.0d; // sets the delay until music starts playing
    
    static boolean loopedForever = true; // sets it to loop forever if this is true
    
    static int loopTimes = 0; //sets how many times you want sound to loop (dont need to have loopedForever set to true)
    static int loopsDone = 0; // just counts the times the sound has looped so it knows when to stop
    
    final static Runnable play = new Runnable() // this Thread/Runnable is for playing the sound
    {
        public void run()
        {
            try 
            {
                // check if the audio file is a wav file
                if (sound.getName().toLowerCase().contains(".wav"))
                {
                    AudioInputStream stream = AudioSystem.getAudioInputStream(sound);
                    
                    AudioFormat format = stream.getFormat();
                    
                    if (format.getEncoding() != AudioFormat.Encoding.PCM_SIGNED) 
                    {
                        format = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED,
                            format.getSampleRate(),
                            format.getSampleSizeInBits(),
                            format.getChannels(),
                            format.getFrameSize() * 2,
                            format.getFrameRate(),
                            true);
                        
                        stream = AudioSystem.getAudioInputStream(format, stream);
                    }
                    
                    SourceDataLine.Info info = new DataLine.Info(SourceDataLine.class,
                            stream.getFormat(),
                            (int) (stream.getFrameLength() * format.getFrameSize()));
                    
                    SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
                    
                    line.open(stream.getFormat());
                    line.start();
                    
                    // Set Volume
                    FloatControl volumeControl = (FloatControl) line.getControl(FloatControl.Type.MASTER_GAIN);
                    volumeControl.setValue((float) (Math.log(volume / 100.0f) / Math.log(10.0f) * 20.0f));
                    
                    //Mute
                    BooleanControl muteControl = (BooleanControl) line.getControl(BooleanControl.Type.MUTE);
                    muteControl.setValue(muted);
                    
                    //Panning
                    FloatControl panControl = (FloatControl) line.getControl(FloatControl.Type.PAN);
                    panControl.setValue(pan);
                    
                    long lastUpdate = System.currentTimeMillis();
                    double sinceLastUpdate = (System.currentTimeMillis() - lastUpdate) / 1000.0d;
                    
                    //Wait the amount of seconds set before continuing
                    while (sinceLastUpdate < seconds)
                    {
                        sinceLastUpdate = (System.currentTimeMillis() - lastUpdate) / 1000.0d;
                    }
                    System.out.println("Playing!");
                    
                    int numRead = 0;
                    byte[] buf = new byte [line.getBufferSize()];
                    
                    while ((numRead = stream.read(buf, 0, buf.length)) >= 0)
                    {
                        int offset = 0;
                        
                        while (offset < numRead)
                        {
                        offset += line.write(buf, offset, numRead - offset);    
                        }
                    }
                    
                    line.drain();
                    line.stop();
                    
                    if (loopedForever)
                    {
                    new Thread(play).start();    
                    }
                    else if (loopsDone < loopTimes)
                    {
                        loopsDone++;
                        new Thread(play).start();
                    }
                    
                }
            }
            catch (Exception ex) { ex.printStackTrace(); }
        }
    };
}
