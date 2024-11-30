package kr.jbnu.se.std;

import javax.sound.sampled.*;
import java.io.File;
import java.util.logging.Logger;


public class AudioManager {
    private Clip backgroundClip;
    private static final Logger logger = Logger.getLogger(AudioManager.class.getName());

    public void playBackgroundMusic(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            backgroundClip = AudioSystem.getClip();
            backgroundClip.open(audioStream);
            backgroundClip.loop(Clip.LOOP_CONTINUOUSLY);
        } catch (Exception e) {
            logger.warning("배경음악 에러.");
        }
    }

    public void playSoundEffect(String filePath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filePath));
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
        } catch (Exception e) {
            logger.warning("효과음 에러.");
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundClip != null && backgroundClip.isRunning()) {
            backgroundClip.stop();
        }
    }
}
