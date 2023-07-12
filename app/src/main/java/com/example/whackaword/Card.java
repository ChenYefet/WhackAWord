package com.example.whackaword;

import androidx.annotation.NonNull;

/**
 * The abstract class Card serves as a template for creating
 * concrete subclasses of cards, such as the FoodCard class
 */
public abstract class Card
{
    private final int ID;
    private final int imageViewID;

    /**
     * Constructor for objects of the abstract class Card
     */
    public Card(int anID, int anImageViewID)
    {
        this.ID = anID;
        this.imageViewID = anImageViewID;
    }

    /**
     * Getter for the ID of the card
     */
    public int getID()
    {
        return this.ID;
    }

    /**
     * Getter for the ID of the imageView of the card
     */
    public int getImageViewID()
    {
        return this.imageViewID;
    }

    /**
     * Returns a string representation of the card
     */
    @NonNull
    @Override
    public String toString()
    {
        return "An instance of class " + this.getClass().getName() +
                " representing a card with ID " + this.getID() +
                "and image view ID " + this.getImageViewID();
    }
}
