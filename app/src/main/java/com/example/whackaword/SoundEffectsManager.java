package com.example.whackaword;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;
import android.os.Handler;

/**
 * The SoundEffectsManager class is responsible for managing sound effects
 * in the Whack-A-Word game
 */
public class SoundEffectsManager
{
    private static final int MAX_SOUND_POOL_STREAMS = 2;
    private static SoundPool soundPool;
    private static int popUpSoundID;
    private static int hideCardsSoundID;
    private static int tickSoundID;

    /**
     * Initialises the sound pool with audio attributes for game usage,
     * and the sound IDs for each sound effect
     */
    public static void initialiseSoundPool(Context aContext)
    {
        AudioAttributes audioAttributes = new AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_GAME) // The audio is intended for game-related purposes
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION) // The audio is meant for system sounds
                .build();

        SoundEffectsManager.soundPool = new SoundPool.Builder()
                .setMaxStreams(MAX_SOUND_POOL_STREAMS)
                .setAudioAttributes(audioAttributes)
                .build();

        SoundEffectsManager.popUpSoundID = SoundEffectsManager.soundPool.load(aContext, R.raw.cards_pop_up, 1);
        SoundEffectsManager.hideCardsSoundID = SoundEffectsManager.soundPool.load(aContext, R.raw.hide_cards, 1);
        SoundEffectsManager.tickSoundID = SoundEffectsManager.soundPool.load(aContext, R.raw.correct, 1);
    }

    /**
     * Plays the cards' pop-up sound effect
     * after a delay that causes the sound effect to
     * synchronise with the cards' pop-up animation
     */
    public static void playPopUpSoundEffect()
    {
        long delayForPopUpSoundEffect = 1200;
        // A delay of one and a fifth seconds (1200 milliseconds)
        // allows for best synchronisation with the pop-up animation

        SoundEffectsManager.playSoundEffect(SoundEffectsManager.popUpSoundID, delayForPopUpSoundEffect);
    }

    /**
     * Plays the hide cards sound effect
     * after a possible delay that causes the sound effect to
     * synchronise with the hide cards animation.
     * This delay would be necessary when a correct food card is tapped
     * because the animation is delayed when a correct food card is tapped
     */
    public static void playHideCardsSoundEffect()
    {
        long generalDelayForHideCardsSoundEffect = 0;
        // Generally, no audio delay is needed for the hide cards sound effect ...

        long delayForHideCardsSoundEffectWhenACorrectFoodCardIsTapped = generalDelayForHideCardsSoundEffect + AnimationManager.HIDE_CARDS_ANIMATION_DELAY_WHEN_CORRECT_FOOD_CARD_IS_TAPPED;
        // ... but when there is a delay to the animation,
        // that delay has to be accounted for
        // in order to synchronise the sound effect

        long delayForHideCardsSoundEffect = TapManager.correctFoodCardWasJustTapped ? delayForHideCardsSoundEffectWhenACorrectFoodCardIsTapped : generalDelayForHideCardsSoundEffect;

        SoundEffectsManager.playSoundEffect(SoundEffectsManager.hideCardsSoundID, delayForHideCardsSoundEffect);
    }

    /**
     * Plays the tick sound effect
     * after a delay that causes the sound effect to
     * synchronise with an enlarged tick
     */
    public static void playTickSoundEffect()
    {
        int offsetForTickEnlargementDelay = 100;

        long delayForTickSoundEffect = PositiveFeedbackAnimationManager.DURATION_OF_TICK_ENLARGEMENT - offsetForTickEnlargementDelay;
        // This delay allows the tick sound effect
        // to play at the perfect time,
        // just as the tick has just become enlarged

        SoundEffectsManager.playSoundEffect(SoundEffectsManager.tickSoundID, delayForTickSoundEffect);
    }

    /**
     * Plays a sound effect after a delay
     */
    private static void playSoundEffect(int soundID, long delay)
    {
        new Handler().postDelayed(() -> soundPool.play(soundID, 1, 1, 1, 0, 1f), delay);
    }

}
