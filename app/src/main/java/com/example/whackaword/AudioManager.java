package com.example.whackaword;

import android.media.MediaPlayer;

import java.util.Queue;

/**
 * The AudioManager class is responsible for managing audio playback in the Whack-A-Word game
 *
 * It contains two class variables:
 *
 * mediaPlayerForSequentialAudio, which maintains a reference to
 * the currently playing MediaPlayer that deals with the playback of audio in sequence
 *
 * audioQueue, which is used to store audio IDs
 * and allow for the management of audio files
 * in a 'first-in, first-out' (FIFO) manner
 * so that they are played in a sequential order (rather than concurrently)
 */
public class AudioManager
{
    private static MediaPlayer mediaPlayerForSequentialAudio;
    public static Queue<Integer> audioQueue;

    /**
     * Plays an audio file
     * concurrently with other audio playback
     */
    public static void playAudioConcurrently(WhackAWordActivity aWhackAWordActivity, int audioID)
    {
        MediaPlayer mediaPlayerForConcurrentAudio = MediaPlayer.create(aWhackAWordActivity, audioID);
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
        AudioManager.audioQueue.add(audioID);
        AudioManager.implementSequentialPlayback(aWhackAWordActivity);
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
     * - and hides all cards upon audio completion if the user has won
     *
     * Displays an animated tick if the tick sound is being played
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

        if (!AudioManager.audioQueue.isEmpty())
        {
            int audioID = AudioManager.audioQueue.poll();
            AudioManager.mediaPlayerForSequentialAudio = MediaPlayer.create(aWhackAWordActivity, audioID);
            AudioManager.mediaPlayerForSequentialAudio.setOnCompletionListener(mp ->
            {
                AudioManager.mediaPlayerForSequentialAudio.release();
                // Releases the media player to free up resources

                AudioManager.mediaPlayerForSequentialAudio = null;
                // Removes the reference to the MediaPlayer

                AudioManager.implementSequentialPlayback(aWhackAWordActivity);
                AnimationManager.hideCardsIfUserHasWonAndAudioHasCompleted(aWhackAWordActivity);
            });

            AudioManager.mediaPlayerForSequentialAudio.start();
            AnimationManager.displayAnimatedTickDuringTickAudio(aWhackAWordActivity, audioID);
        }

    }

}