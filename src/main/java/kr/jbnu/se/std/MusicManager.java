package kr.jbnu.se.std;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;


public class MusicManager {
    private static final Logger logger = Logger.getLogger(MusicManager.class.getName());
    private transient Clip clipbg;
    private String backgroundSound;
    private String activeSound;
    public MusicManager(String backgroundSound){
        this.backgroundSound = backgroundSound;
        this.activeSound = activeSound;
    }

    public void playBackgroundMusic() {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(backgroundSound));
            clipbg = AudioSystem.getClip();
            clipbg.open(audioStream);
            clipbg.loop(Clip.LOOP_CONTINUOUSLY); // 무한 반복
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            logger.log(Level.WARNING, "An error occurred: ", e); //스택트레이스도 함께 기록
        }
    }

    public void stopBackgroundMusic() {
        if (clipbg != null && clipbg.isRunning()) {
            clipbg.stop();
        }
    }
}


