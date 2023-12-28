package com.example.whackaword;

import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;

/**
 * The AudioManager class is responsible for managing audio playback in the Whack-A-Word game
 *
 * It contains a constant class variable for the background music volume,
 * as well as two other class variables:
 *
 * mediaPlayerForSequentialAudio, which maintains a reference to
 * the currently playing MediaPlayer that deals with
 * the playback of audio in sequence
 *
 * mediaPlayerForBackgroundMusic, which maintains a reference to
 * the currently playing MediaPlayer that deals with
 * the playback of background music
 */
public class AudioManager
{
    private static final float BACKGROUND_MUSIC_VOLUME = 0.3f;
    private static MediaPlayer mediaPlayerForSequentialAudio;
    private static MediaPlayer mediaPlayerForBackgroundMusic;

    /**
     * Plays background music
     */
    public static void playBackgroundMusic(Context aContext)
    {
        AudioManager.mediaPlayerForBackgroundMusic = MediaPlayer.create(aContext, R.raw.background_music);
        mediaPlayerForBackgroundMusic.setLooping(true);
        mediaPlayerForBackgroundMusic.setVolume(BACKGROUND_MUSIC_VOLUME,BACKGROUND_MUSIC_VOLUME);
        mediaPlayerForBackgroundMusic.start();
    }

    /**
     * Adds an audio file to the audio queue
     * and implements sequential playback
     * with an audio completion listener
     * to determine what happens upon completion of the audio playback
     */
    public static void playAudioSequentially(WhackAWordActivity aWhackAWordActivity, int audioID, AudioCompletionListener audioCompletionListener)
    {
        Collections.audioQueue.add(audioID);
        AudioManager.implementSequentialPlayback(aWhackAWordActivity, audioCompletionListener);
    }

    /**
     * Implements the playAudioSequentially method
     * while audioCompletionListener holds a null reference
     */
    public static void playAudioSequentially(WhackAWordActivity aWhackAWordActivity, int audioID)
    {
        AudioManager.playAudioSequentially(aWhackAWordActivity, audioID, null);
    }

    /**
     * Helper method that plays in sequence all the audio files whose IDs are in the audio queue:
     *
     * Plays the audio file whose ID is at the front of the queue
     * and sets a completion listener to ensure that,
     * upon completion of the audio,
     * the method:
     *
     * - releases the media player and makes it null
     *   in order to avoid resource leaks and prepare it for the next audio playback
     *
     * - recursively calls itself
     *   to both play the next audio file in the queue and call itself recursively,
     *   ensuring that all audio files are played in sequence
     *
     * - and completes any task that was passed in via the audioCompletionListener parameter
     *
     * If there is audio currently playing, does nothing.
     * This does not cause a problem of skipped audio files
     * since this method would be set to be called again recursively
     * upon completion of the audio
     */
    private static void implementSequentialPlayback(WhackAWordActivity aWhackAWordActivity, AudioCompletionListener audioCompletionListener)
    {
        if (AudioManager.mediaPlayerForSequentialAudio != null && AudioManager.mediaPlayerForSequentialAudio.isPlaying())
        {
            return;
        }

        if (!Collections.audioQueue.isEmpty())
        {
            int audioID = Collections.audioQueue.poll();
            AudioManager.mediaPlayerForSequentialAudio = MediaPlayer.create(aWhackAWordActivity, audioID);

            AudioManager.mediaPlayerForSequentialAudio.setOnCompletionListener(mp ->
            {
                AudioManager.mediaPlayerForSequentialAudio.release();
                // Releases the media player to free up resources

                AudioManager.mediaPlayerForSequentialAudio = null;
                // Removes the reference to the MediaPlayer

                AudioManager.implementSequentialPlayback(aWhackAWordActivity, audioCompletionListener);

                if (audioCompletionListener != null)
                {
                    audioCompletionListener.onAudioCompletion();
                }

            });

            AudioManager.startSequentialPlayback(audioID);
        }

    }

    /**
     * Helper method that starts playing an audio file
     * using mediaPlayerForSequentialAudio.
     * Creates a Runnable for audio playback
     * and schedules it with a delay if the first card is about to pop up.
     * If the audio ID is that of a food item,
     * adjusts the background music during playback
     */
    private static void startSequentialPlayback(int audioID)
    {
        Runnable audioPlaybackRunnable = () ->
        {
            AudioManager.mediaPlayerForSequentialAudio.start();

            if (Collections.foodItemAudioIDs.contains(audioID))
            {
                AudioManager.adjustBackgroundMusicVolume();
            }
            // Lowers the volume of the background music during audio playback of a correct food item

        };

        int audioDelay = AnimationManager.firstCardIsAboutToPopUp ? 2000 : 800;
        // The delay before playing the audio of the first correct food item
        // is two seconds (2000 milliseconds),
        // otherwise it is four fifths of a second (800 milliseconds)

        new Handler().postDelayed(audioPlaybackRunnable, audioDelay);
    }

    /**
     * Lowers background music after a delay of three tenths of a second (300 milliseconds),
     * and sets it back to normal
     * after a further delay of half a second (500 milliseconds)
     */
    private static void adjustBackgroundMusicVolume()
    {
        float lowVolumeLevel = 0.1f;
        int delayForVolumeDecrease = 300;
        int durationOfVolumeDecrease = 500;
        int delayForVolumeIncrease = delayForVolumeDecrease + durationOfVolumeDecrease;

        new Handler().postDelayed(() -> AudioManager.mediaPlayerForBackgroundMusic.setVolume(lowVolumeLevel, lowVolumeLevel), delayForVolumeDecrease);
        new Handler().postDelayed(() -> AudioManager.mediaPlayerForBackgroundMusic.setVolume(BACKGROUND_MUSIC_VOLUME, BACKGROUND_MUSIC_VOLUME), delayForVolumeIncrease);
    }

}