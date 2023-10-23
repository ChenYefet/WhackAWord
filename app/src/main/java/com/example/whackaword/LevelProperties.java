package com.example.whackaword;

/**
 * The LevelProperties class is responsible for managing the properties of levels,
 * such as the current level, the number of cards to display, and the count of successful taps
 */
public class LevelProperties
{
    public static final int LAST_LEVEL = 3;
    public static final int REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL = 3;

    public static final int NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_1 = 1;
    public static final int NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_2 = 2;
    public static final int NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_3 = 3;
    // These last three constants exist despite them equating to their corresponding level
    // since this may change in the future;
    // it is easier to update the code
    // if we decide that level 3 should display four cards instead of three,
    // or that a future level 4 should display five cards instead of four,
    // so it is best for the numberOfCardsToDisplay instance variable
    // to be independent of the current level
    // and be assigned one of these constant class variables instead

    public static int currentLevel;
    public static int numberOfCardsToDisplay;
    public static int countOfSuccessfulTaps;

    /**
     * Initialises the level properties
     */
    public static void initialiseLevelProperties()
    {
        LevelProperties.currentLevel = 1;
        LevelProperties.numberOfCardsToDisplay = NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_1;
        LevelProperties.countOfSuccessfulTaps = 0;
    }

    /**
     * Returns true if the user has won,
     * otherwise returns false
     */
    public static boolean userWins()
    {
        return LevelProperties.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL && LevelProperties.currentLevel == LAST_LEVEL;
    }

    /**
     * Initialises the properties of the next level
     * if the user has reached the next level
     */
    public static void initialiseNextLevelPropertiesIfUserHasReachedTheNextLevel()
    {
        if (LevelProperties.userHasReachedTheNextLevel())
        {
            LevelProperties.initialisesNextLevelProperties();
        }
    }

    /**
     * Helper method that initialises the properties of the next level
     */
    private static void initialisesNextLevelProperties()
    {
        if (LevelProperties.currentLevel < LAST_LEVEL)
        {
            LevelProperties.currentLevel++;
            LevelProperties.countOfSuccessfulTaps = 0;

            if (LevelProperties.currentLevel == 2)
            {
                LevelProperties.numberOfCardsToDisplay = NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_2;
            }
            else // There are currently only three levels
            {
                LevelProperties.numberOfCardsToDisplay = NUMBER_OF_CARDS_TO_DISPLAY_FOR_LEVEL_3;
            }

        }

    }

    /**
     * Helper method that returns true if the user has reached the next level,
     * otherwise returns false
     */
    private static boolean userHasReachedTheNextLevel()
    {
        return LevelProperties.countOfSuccessfulTaps == REQUIRED_NUMBER_OF_SUCCESSFUL_TAPS_PER_LEVEL && LevelProperties.currentLevel < LAST_LEVEL;
    }

}