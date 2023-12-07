package com.example.whackaword;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * An object of the WhackAWordActivity class represents a game called Whack-A-Word
 * with the following description:
 *
 * A vocabulary card with a flashcard image of a food item
 * pops up from one of a series of holes in the ground.
 * The user hears the name of the food item then must tap it,
 * after which they receive some positive feedback –
 * an animated tick with a sound effect
 * and the card changing colour multiple times in quick succession
 * before it is hidden within the hole it had emerged from.
 * If they don't tap it,
 * it retreats into its hole and pops up again in a random one.
 * After each round of three successful taps,
 * the number of cards that pop up simultaneously
 * increases by one.
 * Each card that pops up must have a different image
 * to the other cards that pop up at the same time
 * so that the user always chooses between different food items,
 * and the user should always be tasked to tap a different food card
 * to all previous food cards that they have correctly tapped
 * during the game
 * so that the game is more challenging and fun.
 * At the end of the third round, the user wins
 */
public class WhackAWordActivity extends AppCompatActivity
{
    /**
     * This is the method that gets called when the activity is created.
     * It sets up the initial state of the game,
     * including its layout and variables,
     * animates the sky,
     * and calls the playWhackAWord method that starts the game
     *
     * savedInstanceState either contains the activity's previously saved state
     * or is null if the activity has never existed before.
     * It could include the contents of user interface widgets,
     * the values of instance variables,
     * or any other relevant data that the user would need
     * the next time the activity is created,
     * for example after a user rotates the device from portrait to landscape mode
     * (in which case the Activity object would be destroyed and recreated),
     * although the screen orientation is set to landscape via the AndroidManifest file,
     * ensuring that this does not happen
     * and that the game is always played in landscape mode
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // Passes savedInstanceState to AppCompatActivity.
        // This is called before the rest of the onCreate() method
        // to ensure proper setup and initialization of the activity,
        // e.g. toolbar, theme, backward compatibility via the AndroidX library

        this.setContentView(R.layout.activity_whack_a_word);
        // Sets up the layout of the activity

        ScreenProperties.setSmallScreenTo(ScreenProperties.isScreenSmall(this));
        Collections.initialiseCollections();
        LevelProperties.initialiseLevelProperties();
        AnimationManager.initialiseAnimationProperties();
        Selector.thereAreNewFoodItems = true;

        AnimationManager.animateSky(this);
        AudioManager.playBackgroundMusic(this);
        this.playWhackAWord();
    }

    /**
     * Plays Whack-A-Word:
     *
     * Causes food cards to pop up,
     * plays the correct audio,
     * and sets click listeners for the food cards
     */
    public void playWhackAWord()
    {
        Selector.selectFoodCardsForDisplay(LevelProperties.numberOfCardsToDisplay);
        AnimationManager.cardsPopUp(this, Collections.mapOfFoodItemsToTheirFoodCards);

        if (Selector.thereAreNewFoodItems)
        {
            Selector.setCorrectFoodItemFromThoseThatAreOnDisplay();
            AudioManager.playAudioSequentially(this, Selector.correctFoodItem.getAudioID());
        }

        FoodCard correctFoodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(Selector.correctFoodItem);

        TapManager.setClickListeners(this, correctFoodCard, Selector.correctFoodItem);
    }

    /**
     * Initialises the properties of the next level if the user has reached it
     * and plays the 'well done' audio if the user has won
     *
     * Hides cards and plays Whack-A-Word again
     * if the user hasn't won
     */
    public static void continuePlaying(WhackAWordActivity aWhackAWordActivity)
    {
        if (LevelProperties.userHasReachedTheNextLevel())
        {
            LevelProperties.setNextLevelProperties();
        }

        if (LevelProperties.userWins())
        {
            AudioManager.playAudioSequentially(aWhackAWordActivity, R.raw.well_done);
        }
        else
        {
            AnimationManager.hideCards(aWhackAWordActivity);
            Collections.availableFoodItems = new ArrayList<>(Collections.foodItems);
            Collections.mapOfFoodItemsToTheirFoodCards = new HashMap<>();
            Selector.thereAreNewFoodItems = true;
            aWhackAWordActivity.playWhackAWord();
        }

    }

    /**
     * Causes cards to hide and pop up again
     */
    public static void tryAgain(WhackAWordActivity aWhackAWordActivity)
    {
        AnimationManager.hideCards(aWhackAWordActivity);

        Collections.mapOfFoodItemsToTheirFoodCards.replaceAll(((foodItem, hiddenFoodCard) -> null));
        // Keeps all FoodItem keys in the map while setting all their FoodCard values to null
        // since those food items need to be displayed again
        // on food cards which are not yet determined

        Selector.thereAreNewFoodItems = false;

        aWhackAWordActivity.playWhackAWord();
    }

}