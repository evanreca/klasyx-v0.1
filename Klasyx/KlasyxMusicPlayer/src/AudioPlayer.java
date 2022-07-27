// AudioPlayer.java
// Programmer: Evan Reca

// This class handles the playback of user-selected audio files in KlasyxMusicPlayer.java and provides methods
// to manipulate the song being played

// Audio Imports
import java.io.File;
import javax.sound.sampled.*;
import java.io.IOException;

public class AudioPlayer {

    // to store current position in the audio file
    Long currentFrame;
    Clip clip;
    public String status = "paused";
    public boolean repeatStatus;

    AudioInputStream audioInputStream;
    static String filePath;

    // constructor to initialize streams and clip
    public AudioPlayer()
            throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {

        // create AudioInputStream object
        filePath = "C:\\Users\\evanr\\OneDrive - Brookdale Community College\\Fall 2020\\" +
                "Advanced Software Project\\Klasyx Music Player\\" +
                "KlasyxMusicPlayer\\Introduction_and_Rondo_Capriccioso.wav";

        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());

        // create clip reference
        clip = AudioSystem.getClip();

        // open audioInputStream to the clip
        clip.open(audioInputStream);

        // initializes loop status - repeat is set to ON by default
        clip.loop(-1);
    }

    public String getStatus() {
        return this.status;
    }

    public String setStatus(String status) {
        this.status = status;
        return status;
    }

    public void setRepeatOn() {
        this.clip.loop(-1);
        repeatStatus = true;
    }

    public void setRepeatOff() {
        this.clip.loop(0);
        repeatStatus = false;
    }

    public boolean getRepeatStatus() {
        return repeatStatus;
    }

    // returns the length of the song in seconds
    public Long getSongLength() {
        return clip.getMicrosecondLength();
    }

    // Method to play the audio
    public void play() {
        clip.start();
        this.status = "playing";
    }

    // Method to pause the audio
    public void pause() {
        if (this.status.equals("paused")) {
//          System.out.println("audio is already paused");
            return;
        }
        this.currentFrame = this.clip.getMicrosecondPosition();
        clip.stop();
        this.status = "paused";
    }

    // Method to resume the audio
    public void resumeAudio() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        if (status.equals("playing")) {
//          System.out.println("Audio is already being played");
            return;
        }
        clip.setMicrosecondPosition(currentFrame);
        this.play();
    }

    // Method to restart the audio
    public void restart() throws IOException, LineUnavailableException,
            UnsupportedAudioFileException {
        currentFrame = 0L;
        clip.setMicrosecondPosition(0);
        this.play();
    }

    // Method to stop the audio
    public void stop() throws UnsupportedAudioFileException,
            IOException, LineUnavailableException {
        currentFrame = 0L;
        clip.stop();
        clip.close();
    }

    // Method to jump over a specific part
    public void jump(long t) throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {

        if (t > 0 && t < clip.getMicrosecondLength()) {
            this.currentFrame = t;
            clip.setMicrosecondPosition(t);
        }
    }

    public void testRepeat() throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {
        clip.setMicrosecondPosition(clip.getMicrosecondLength() - 7_500_000);
    }

    public boolean getActiveStatus() {
        return clip.isActive();
    }

    public long getMicrosecondPosition() {
        return this.clip.getMicrosecondPosition();
    }

    // Method to reset audio stream
    public void resetAudioStream() throws UnsupportedAudioFileException, IOException,
            LineUnavailableException {

        audioInputStream = AudioSystem.getAudioInputStream(new File(filePath).getAbsoluteFile());
        clip.open(audioInputStream);
        clip.loop(Clip.LOOP_CONTINUOUSLY);
    }
}
