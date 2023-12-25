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
    private static final float BACKGROUND_MUSIC_VOLUME = 0.4f;

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
     * Plays an audio file
     * concurrently with other audio playback
     */
    public static void playAudioConcurrently(Context aContext, int audioID)
    {
        MediaPlayer mediaPlayerForConcurrentAudio = MediaPlayer.create(aContext, audioID);

        mediaPlayerForConcurrentAudio.setOnCompletionListener(mp ->
        {
            mediaPlayerForConcurrentAudio.release();
            // Releases the media player to free up resources

        });

        mediaPlayerForConcurrentAudio.start();
    }

    /**
     * Adds an audio file to the audio queue
     * and implements sequential playback
     */
    public static void playAudioSequentially(WhackAWordActivity aWhackAWordActivity, int audioID)
    {
        Collections.audioQueue.add(audioID);
        AudioManager.implementSequentialPlayback(aWhackAWordActivity);
    }

    /**
     * Plays the cards pop up sound effect
     * concurrently with other audio playback,
     * after a delay that causes the audio to
     * synchronise with the cards pop up animation
     */
    public static void playPopUpSoundEffect(WhackAWordActivity aWhackAWordActivity)
    {
        long audioDelayForFirstCard = 10;
        // There is a bug. If the delay is 9, the pop-up sound effect plays too fast,
        // while if the delay is 10, it plays too slow. To be fixed.

        long audioDelayForSecondCardOnwards = 800;
        // A delay of four fifths of a second (800 milliseconds)
        // allows for best synchronisation with the 'pop up' animation for the second card onwards

        if (AnimationManager.firstCardIsAboutToPopUp)
        {
            AudioManager.playAudioAfterDelay(aWhackAWordActivity, R.raw.cards_pop_up, audioDelayForFirstCard);
        }
        else
        {
            AudioManager.playAudioAfterDelay(aWhackAWordActivity, R.raw.cards_pop_up, audioDelayForSecondCardOnwards);
        }

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
     * - and hides cards after the 'well done' audio playback if the user has won
     *
     * If there is audio currently playing, does nothing.
     * This does not cause a problem of skipped audio files
     * since this method would be set to be called again recursively
     * upon completion of the audio
     */
    private static void implementSequentialPlayback(WhackAWordActivity aWhackAWordActivity)
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

                AudioManager.implementSequentialPlayback(aWhackAWordActivity);

                if (LevelProperties.userWins() && audioID == R.raw.well_done)
                {
                    AnimationManager.hideCards(aWhackAWordActivity, true);
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

        int audioDelay = AnimationManager.firstCardIsAboutToPopUp ? 2000 : 0;
        // The delay before playing the audio of the first correct food item
        // is two seconds (2000 milliseconds),
        // otherwise there is no delay

        new Handler().postDelayed(audioPlaybackRunnable, audioDelay);
    }

    /**
     * Helper method that plays an audio file after a delay
     */
    private static void playAudioAfterDelay(WhackAWordActivity aWhackAWordActivity, int anAudioID, long delay)
    {
        new Handler().postDelayed(() -> AudioManager.playAudioConcurrently(aWhackAWordActivity, anAudioID), delay);
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