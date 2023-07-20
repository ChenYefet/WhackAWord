# Whack-A-Word
 An educational game that helps the user learn how to say certain food items in English. It works as follows:
 
 A vocabulary card with a flashcard image of a food item pops up from one of a series of holes in the ground. The user hears the name of the food item then must tap it, after which they hear it again and receive some positive feedback – a tick with a sound effect. If they don't tap it, it retreats into its hole and pops up again in a random one. After each round of three successful taps, the number of cards that pop up simultaneously increases by one. Each card that pops up must have a different image to the other cards that pop up at the same time so that the user always chooses between different food items, and the user should always be tasked to tap a different food card to all previous food cards that they have correctly tapped during the game so that the game is more challenging and fun. At the end of the third round, the user wins.

## Table of contents
* [Languages](#languages)
* [Libraries](#libraries)
* [Description of code](#description-of-code)
* [Illustrations](#illustrations)
* [Requirements and setup](#requirements-and-setup)
* [Known bugs](#known-bugs)
* [Find a bug?](#find-a-bug)
* [Credits](#credits)
* [License](#license)

## Languages
Java 8

## Libraries
* <span style ="font-family: Courier New">`androidx.appcompat:appcompat:1.6.1` </span>(for backwards compatibility of various user interface elements and widgets with older versions of Android)
* <span style ="font-family: Courier New">`com.google.android.material:material:1.9.0` </span>(for certain pre-built user interface components and styles)
* <span style ="font-family: Courier New">`androidx.constraintlayout:constraintlayout:2.1.4` </span>(for defining constraints between user interface elements and creating responsive user interfaces that adapt to different screen sizes)
* <span style ="font-family: Courier New">`junit:junit:4.13.2` </span>(for unit testing)
* <span style ="font-family: Courier New">`androidx.test.ext:junit:1.1.5` </span>(for instrumented testing)
* <span style ="font-family: Courier New">`androidx.test.espresso:espresso-core:3.5.1` </span>(for UI testing)

## Description of code
### The <span style ="font-family: Courier New">`WhackAWordActivity`</span> class
The <span style ="font-family: Courier New">`WhackAWordActivity`</span> class extends <span style ="font-family: Courier New">`AppCompatActivity`</span>, which is the superclass for activities that use the <span style ="font-family: Courier New">`appcompat`</span> library. When <span style ="font-family: Courier New">`WhackAWordActivity`</span> is created, the <span style ="font-family: Courier New">`onCreate`</span> method is called and sets the user interface to the one defined in <span style ="font-family: Courier New">`activity_whack_a_word.xml`</span>. It also fixes the screen orientation to landscape mode, initialises instance and class variables and calls the <span style ="font-family: Courier New">`whackAWord`</span> method that plays the game recursively each time a card is correctly tapped.

The layout file <span style ="font-family: Courier New">`activity_whack_a_word`</span> contains only one card per hole, so to make it seem as though different cards can pop up from the same hole at different times, the image of each card that pops up is determined dynamically before it appears. Therefore, it is necessary to always keep track of which food items are assigned to which food cards.

### The <span style ="font-family: Courier New">`Card`</span> class
The abstract class <span style ="font-family: Courier New">`Card`</span> serves as a template for creating concrete subclasses of cards, such as the <span style ="font-family: Courier New">`FoodCard`</span> class. It defines constant instance variables for the card's ID and the ID of its imageView, as objects of the <span style ="font-family: Courier New">`Card`</span> class are modelled as having an imageView (where an image can be set).

### The <span style ="font-family: Courier New">`FoodCard`</span> class
The <span style ="font-family: Courier New">`FoodCard`</span> class is a subclass of <span style ="font-family: Courier New">`Card`</span> with a <span style ="font-family: Courier New">`FoodItem`</span> instance variable, as objects of the <span style ="font-family: Courier New">`FoodCard`</span> class are modelled as having a food item (or literally, the image of a food item).

### The <span style ="font-family: Courier New">`VocabularyItem`</span> class
The abstract class <span style ="font-family: Courier New">`VocabularyItem`</span> serves as a template for creating concrete subclasses of vocabulary items, such as the <span style ="font-family: Courier New">`FoodItem`</span> class. It defines constant instance variables for its name and audio ID, as well as an instance variable for its definition, which is not used in this game but may be useful for other applications that use the <span style ="font-family: Courier New">`VocabularyItem`</span> class.

### The <span style ="font-family: Courier New">`FoodItem`</span> class
The <span style ="font-family: Courier New">`FoodItem`</span> class is a subclass of <span style ="font-family: Courier New">`VocabularyItem`</span> with a constant instance variable for the ID of its image, as objects of the <span style ="font-family: Courier New">`FoodItem`</span> class are modelled as having an image.

## Illustrations
![Whack-A-Word game with one card displayed](https://github.com/ChenYefet/WhackAWord/assets/122983411/258f54f5-7d1c-4297-8174-d6098098a4f3)
![Whack-A-Word game with three cards and a tick displayed](https://github.com/ChenYefet/WhackAWord/assets/122983411/b947e418-e498-4533-8920-7036c6f9b681)

## Requirements and setup
Before using this app, there are some prerequisites that must be met. This section provides information about the requirements that need to be fulfilled in order to use this app.
### Operating System Requirements
This app can be run on any operating system that supports Java. This includes Windows, macOS, and Linux.
### Hardware Requirements
This app does not have any specific hardware requirements. It can run on any system that supports Java.
### Software Requirements
* Java Development Kit (JDK) version 8 or later installed on your computer. You can download the latest version of the JDK from the official Oracle website [here](https://www.oracle.com/java/technologies/downloads/).
* An Integrated Development Environment (IDE) such as Android Studio installed on your computer. Note that IDEs have their own requirements. You can find requirements for the Android Studio IDE [here](https://developer.android.com/studio/install?gclid=CjwKCAjwjMiiBhA4EiwAZe6jQ6JIVXEkxXTH3jSBnS3iT6wq3o8irNlSSfIroMs2__YxISpgDZlfvBoCiSYQAvD_BwE&gclsrc=aw.ds).

### Setting up the project
* Clone this repository to your local machine.
* Open the project in your preferred Java IDE (such as Android Studio).
* Run the program and play Whack-A-Word!

## Known bugs
* The cards are not animated.

## Find a bug?
If you found an issue or would like to submit an improvement to this project, please submit an issue using the 'Issues' tab above. If you would like to submit a pull request, please reference the issue you created.

## Credits
This app is my attempt to implement the following mockups and instructions, given to me by the [onebillion](https://onebillion.org/) organisation after I sent them a job application. I used [Inkscape](https://inkscape.org/) to create the SVG file that I needed in order to make each card appear to be coming out of their hole.

### Mockups
![mockup-1-card](https://github.com/ChenYefet/Calculator/assets/122983411/79f55e30-e33f-4c52-9196-2f62529d35f1)
![mockup-2-card](https://github.com/ChenYefet/Calculator/assets/122983411/93e04545-ae19-45a8-9422-d82174d08641)
![mockup-3-card](https://github.com/ChenYefet/Calculator/assets/122983411/44182f31-a6c9-4399-9e31-21b1d1219ef8)

### Instructions
"Whack-a-word


Create a simple game for young children to learn vocabulary, that can be used on an Android tablet.

Overview
A series of holes in the ground are present. To start, a vocabulary card pops up from one of the holes, with a flashcard image. The child hears the word on the card then must tap it, after which they hear the word again then receive some positive feedback - a tick with sound effect. If they don't tap it, it retreats into its hole and pops up again in a random one. After three successful taps, the child starts to get more vocabulary moles simultaneously - two, then three. They must choose the correct one, or all retreat into their holes.

Notes\
\- Mockups are provided for you to work from .\
\- Use the provided image and sound assets.\
\- You can use whichever development environment and language you wish.\
\- Deliver an APK, source code and instructions to build.\
\- Email a link to jobs@onebillion.org"

## License
This app is licensed under the [MIT License](https://github.com/ChenYefet/WhackAWord/blob/master/LICENSE).
