package com.example.whackaword;

import android.media.MediaPlayer;

import java.util.Queue;

/**
 * The AudioManager class is responsible for managing audio playback in the Whack-A-Word game
 *
 * It contains two class variables:
 *
 * mediaPlayer, which maintains a reference to the currently playing MediaPlayer.
 * This is necessary to control the playback of audio
 * across different method calls
 *
 * audioQueue, which is used to store audio IDs
 * and allow for the management of audio files
 * in a 'first-in, first-out' (FIFO) manner
 * so that they are played in a sequential order (rather than concurrently)
 */
public class AudioManager
{
    private static MediaPlayer mediaPlayer;
    public static Queue<Integer> audioQueue;

    /**
     * Adds an audio file to the audio queue
     * and initiates sequential playback
     */
    public static void playAudio(WhackAWordActivity aWhackAWordActivity, int audioID)
    {
        AudioManager.audioQueue.add(audioID);
        AudioManager.playAudioInSequence(aWhackAWordActivity);
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
    private static void playAudioInSequence(WhackAWordActivity aWhackAWordActivity)
    {
        if (AudioManager.mediaPlayer != null && AudioManager.mediaPlayer.isPlaying())
        {
            return;
        }

        if (!AudioManager.audioQueue.isEmpty())
        {
            int audioID = AudioManager.audioQueue.poll();
            AudioManager.mediaPlayer = MediaPlayer.create(aWhackAWordActivity, audioID);
            AudioManager.mediaPlayer.setOnCompletionListener(mp ->
            {
                AudioManager.mediaPlayer.release();
                // Releases the media player to free up resources

                AudioManager.mediaPlayer = null;
                // Removes the reference to the MediaPlayer

                AudioManager.playAudioInSequence(aWhackAWordActivity);
                AnimationManager.hideCardsIfUserHasWonAndAudioHasCompleted(aWhackAWordActivity);
            });

            AudioManager.mediaPlayer.start();
            AnimationManager.displayAnimatedTickDuringTickAudio(aWhackAWordActivity, audioID);
        }

    }

}