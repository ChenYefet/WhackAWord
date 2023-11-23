package com.example.whackaword;

import android.content.pm.ActivityInfo;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Set;

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
 *
 * The WhackAWordActivity class contains one instance variable:
 *
 * correctFoodItem, which is the food item
 * whose image is on the card that the user is tasked to tap
 *
 * Note that food cards and food items are modelled as separate from each other,
 * even though the user is meant to understand that they are part of the same card.
 * For example, if a food item is displayed on a card,
 * and subsequently a different food item is displayed on the same card,
 * the user is meant to understand that they are different cards,
 * whereas programmatically they are the same card with different food items,
 * one having replaced the other.
 * Currently, there are five cards (one for each hole) and nine food items,
 * and each card's food item can vary
 * (via the setImageResource method in the DisplayManager class)
 */
public class WhackAWordActivity extends AppCompatActivity
{
    private FoodItem correctFoodItem;

    /**
     * This is the method that gets called when the activity is created.
     * It sets up the initial state of the activity,
     * including its layout and variables,
     * animates the sky,
     * and calls the playWhackAWord method that starts the game.
     *
     * savedInstanceState either contains the activity's previously saved state
     * or is null if the activity has never existed before.
     * It could include the contents of user interface widgets,
     * the values of instance variables,
     * or any other relevant data that the user would need
     * the next time the activity is created,
     * for example after a user rotates the device from portrait to landscape mode
     * (in which case the Activity object would be destroyed and recreated),
     * although the setRequestedOrientation method ensures that this does not happen
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

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        // Sets the screen orientation to landscape mode
        // and causes it to remain so regardless of the device's physical orientation

        ScreenProperties.setSmallScreenTo(ScreenProperties.isScreenSmall(this));
        Collections.initialiseCollections();
        LevelProperties.initialiseLevelProperties();
        AnimationManager.firstCardIsAboutToPopUp = true;

        AnimationManager.animateSky(this);
        AudioManager.playBackgroundMusic(this);
        this.playWhackAWord();
    }

    /**
     * Getter for the correct food item
     */
    public FoodItem getCorrectFoodItem()
    {
        return this.correctFoodItem;
    }

    /**
     * Setter for the correct food item
     */
    public void setCorrectFoodItem(FoodItem aFoodItem)
    {
        this.correctFoodItem = aFoodItem;
    }

    /**
     * Plays Whack-A-Word:
     *
     * Causes food cards to pop up,
     * plays the correct audio,
     * and sets click listeners for correct and incorrect food cards
     */
    public void playWhackAWord()
    {
        Selector.selectFoodCardsForDisplay(LevelProperties.numberOfCardsToDisplay, true);

        AnimationManager.cardsPopUp(this, Collections.mapOfFoodItemsToTheirFoodCards);

        Set<FoodItem> foodItemsOnDisplay = Collections.mapOfFoodItemsToTheirFoodCards.keySet();
        // The food items would be 'on' display (and not just set 'for' display)
        // by the end of the animations that were started in the cardsPopUp method

        for (FoodItem foodItemOnDisplay : foodItemsOnDisplay)
        {

            if (!Collections.correctlyTappedFoodItems.contains(foodItemOnDisplay))
            {
                this.setCorrectFoodItem(foodItemOnDisplay);
                break;
            }

        }
        // Sets the correct food item to one of the food items on display
        // that hasn't yet been correctly tapped

        FoodItem correctFoodItem = this.getCorrectFoodItem();
        int correctFoodItemAudioID = correctFoodItem.getAudioID();
        FoodCard correctFoodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(correctFoodItem);

        AudioManager.playAudioSequentially(this, correctFoodItemAudioID);

        TapManager.setOnClickListenerForFoodCard(this, correctFoodCard, true);

        if (Collections.mapOfFoodItemsToTheirFoodCards.size() > 1)

        // I.e. if there are any incorrect food items that are set for display
        // in addition to the one correct food item ...

        {

            for (FoodItem foodItem : Collections.mapOfFoodItemsToTheirFoodCards.keySet())
            {

                if (foodItem != correctFoodItem)
                {
                    FoodCard incorrectFoodCard = Collections.mapOfFoodItemsToTheirFoodCards.get(foodItem);

                    TapManager.setOnClickListenerForFoodCard(this, incorrectFoodCard, false);
                    // ... set a click listener for each of their corresponding food cards

                }

            }

        }

    }

    /**
     * Conveys positive feedback by
     * playing a tick sound, displaying an animated tick and
     * continuously changing the colour of aFoodCard
     */
    public static void conveyPositiveFeedback(WhackAWordActivity aWhackAWordActivity, FoodCard aFoodCard)
    {
        AudioManager.playAudioSequentially(aWhackAWordActivity, R.raw.correct);
        AnimationManager.displayAnimatedTick(aWhackAWordActivity);
        AnimationManager.continuouslyChangeCardColour(aWhackAWordActivity, aWhackAWordActivity.findViewById(aFoodCard.getID()));
    }

}