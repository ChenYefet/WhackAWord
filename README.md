# Whack-A-Word
 An educational game that helps the user learn how to say certain food items in English. It works as follows:
 
 A vocabulary card with a flashcard image of a food item pops up from one of a series of holes in the ground. The user hears the name of the food item then must tap it, after which they receive some positive feedback – an animated tick with a sound effect, and the card changing colour multiple times in quick succession before it is hidden within the hole it had emerged from. If they don't tap it, it retreats into its hole and pops up again in a random one. After each round of three successful taps, the number of cards that pop up simultaneously increases by one. Each card that pops up must have a different image to the other cards that pop up at the same time so that the user always chooses between different food items, and the user should always be tasked to tap a different food card to all previous food cards that they have correctly tapped during the game so that the game is more challenging and fun. At the end of the third round, the user wins and hears, "Well done."

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
The <span style ="font-family: Courier New">`WhackAWordActivity`</span> class extends <span style ="font-family: Courier New">`AppCompatActivity`</span>, which is the superclass for activities that use the <span style ="font-family: Courier New">`appcompat`</span> library. When <span style ="font-family: Courier New">`WhackAWordActivity`</span> is created, the <span style ="font-family: Courier New">`onCreate`</span> method is called and sets the user interface to the one defined in <span style ="font-family: Courier New">`activity_whack_a_word.xml`</span>. It also fixes the screen orientation to landscape mode, animates the sky, and calls the <span style ="font-family: Courier New">`playWhackAWord`</span> method that plays the game recursively each time a card is correctly tapped.

The layout file <span style ="font-family: Courier New">`activity_whack_a_word.xml`</span> contains only one card per hole, so to make it seem as though different cards can pop up from the same hole at different times, the image of each card that pops up is determined dynamically before it appears. The Selector class is used to keep track of which food item is assigned to which food card.

### The <span style ="font-family: Courier New">`Card`</span> class
The abstract class <span style ="font-family: Courier New">`Card`</span> serves as a template for creating concrete subclasses of cards, such as the <span style ="font-family: Courier New">`FoodCard`</span> class. It contains constant instance variables for the card's ID and the ID of its imageView, as objects of the <span style ="font-family: Courier New">`Card`</span> class are modelled as having an imageView (where an image can be set).

### The <span style ="font-family: Courier New">`FoodCard`</span> class
The <span style ="font-family: Courier New">`FoodCard`</span> class is a subclass of <span style ="font-family: Courier New">`Card`</span> with a <span style ="font-family: Courier New">`FoodItem`</span> instance variable, as objects of the <span style ="font-family: Courier New">`FoodCard`</span> class are modelled as having a food item (or literally, the image of a food item).

### The <span style ="font-family: Courier New">`VocabularyItem`</span> class
The abstract class <span style ="font-family: Courier New">`VocabularyItem`</span> serves as a template for creating concrete subclasses of vocabulary items, such as the <span style ="font-family: Courier New">`FoodItem`</span> class. It contains constant instance variables for its name and audio ID, as well as an instance variable for its definition, which is not used in this game but may be useful for other applications that use the <span style ="font-family: Courier New">`VocabularyItem`</span> class.

### The <span style ="font-family: Courier New">`FoodItem`</span> class
The <span style ="font-family: Courier New">`FoodItem`</span> class is a subclass of <span style ="font-family: Courier New">`VocabularyItem`</span> with a constant instance variable for the ID of its image, as objects of the <span style ="font-family: Courier New">`FoodItem`</span> class are modelled as having an image.

### The <span style ="font-family: Courier New">`Collections`</span> class
The <span style ="font-family: Courier New">`Collections`</span> class is responsible for managing collections, containing class variables for the available food items, the available food cards, the food items that have been correctly tapped, a map of food items that are set for display to the food cards upon which they are set to be displayed, and more.

### The <span style ="font-family: Courier New">`LevelProperties`</span> class
The <span style ="font-family: Courier New">`LevelProperties`</span> class is responsible for managing the properties of levels, such as the current level, the number of cards to display, and the count of successful taps. It contains constant class variables for the last level of the game, the required number of successful taps per level, and the number of cards to display for each of the levels.

### The <span style ="font-family: Courier New">`ScreenProperties`</span> class
The <span style ="font-family: Courier New">`ScreenProperties`</span> class is responsible for managing the properties of the screen. It contains a class variable for whether the screen is small, which is necessary for some animations.

### The <span style ="font-family: Courier New">`Selector`</span> class
The <span style ="font-family: Courier New">`Selector`</span> class is responsible for selecting which food items belong to which food cards during gameplay. It ensures that the chosen food items align with the game's rules as described above.

### The <span style ="font-family: Courier New">`AudioManager`</span> class
The <span style ="font-family: Courier New">`AudioManager`</span> class is responsible for managing audio playback. It contains a class variable for the currently playing media player, which is necessary to control the playback of audio across different method calls; and a class variable for the audio queue, which is used to store audio IDs and allow for the management of audio files in a 'first-in, first-out' (FIFO) manner so that they are played in a sequential order (rather than concurrently).

### The <span style ="font-family: Courier New">`TapManager`</span> class
The <span style ="font-family: Courier New">`TapManager`</span> class is responsible for handling user interactions during gameplay. It allows the user to receive appropriate feedback upon tapping correct or incorrect food cards.

### The <span style ="font-family: Courier New">`DisplayManager`</span> class
The <span style ="font-family: Courier New">`DisplayManager`</span> class is responsible for displaying food items on food cards.

### The <span style ="font-family: Courier New">`AnimationManager`</span> class
The <span style ="font-family: Courier New">`AnimationManager`</span> class is responsible for managing animations that enhance user experience and provide visual feedback.

## Illustrations
### Tablet
<table>
  <tr>
    <td valign="top" width="50%">
      <img src="https://github.com/ChenYefet/WhackAWord/assets/122983411/0cf0da26-d652-4bc5-849e-aefafe86f7fd" alt="Whack-A-Word game played on a tablet, with one card displayed and a daytime sky in the background">
      <p class="caption">Whack-A-Word game played on a tablet, with one card displayed and a daytime sky in the background</p>
    </td>
    <td valign="top" width="50%">
      <img src="https://github.com/ChenYefet/WhackAWord/assets/122983411/8bd53975-91ef-4399-813a-6271277eee9e" alt="Whack-A-Word game played on a tablet, with three cards displayed and about to go back into their holes upon the successful tap of a card. A sunset sky is in the background, having changed from a daytime sky, and the correct feedback is being shown in response to a correctly tapped card (the card is in the process of changing colours and a tick is mid-animation)">
      <p class="caption">Whack-A-Word game played on a tablet, with three cards displayed and about to go back into their holes upon the successful tap of a card. A sunset sky is in the background, having changed from a daytime sky, and the correct feedback is being shown in response to a correctly tapped card (the card is in the process of changing colours and a tick is mid-animation)</p>
    </td>
  </tr>
</table>

### Phone
<table>
  <tr>
    <td valign="top" width="50%">
      <img src="https://github.com/ChenYefet/WhackAWord/assets/122983411/c602045a-cb9e-4443-b3b2-112acff51850" alt="Whack-A-Word game played on a phone, with one card displayed and a northern lights sky in the background, having changed from a sunset sky">
      <p class="caption">Whack-A-Word game played on a phone, with one card displayed and a northern lights sky in the background, having changed from a sunset sky</p>
    </td>
    <td valign="top" width="50%">
      <img src="https://github.com/ChenYefet/WhackAWord/assets/122983411/a7a245de-e4c6-4758-afff-7f5fdc81ade4" alt="Whack-A-Word game played on a phone, with three cards going back into their holes upon the successful tap of a card. A dawn sky in the background, having changed from a northern lights sky, and the correct feedback is being shown in response to a correctly tapped card (the card is in the process of changing colours and a tick is mid-animation)">
      <p class="caption">Whack-A-Word game played on a phone, with three cards going back into their holes upon the successful tap of a card. A dawn sky is in the background, having changed from a northern lights sky, and the correct feedback is being shown in response to a correctly tapped card (the card is in the process of changing colours and a tick is mid-animation)</p>
    </td>
  </tr>
</table>

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
* None at the moment.

## Find a bug?
If you found an issue or would like to submit an improvement to this project, please submit an issue using the 'Issues' tab above. If you would like to submit a pull request, please reference the issue you created.

## Credits
This app is my attempt to implement the following mockups and instructions, given to me by the [onebillion](https://onebillion.org/) organisation after I sent them a job application. In addition to implementing it from scratch, I have made it usable on different types of Android device and implemented additional features not included in the instructions.
I used [Inkscape](https://inkscape.org/) to create the SVG file that I needed in order to make each card appear to be coming out of their hole.
The 'well done' audio file was generated using [TTSMP3](https://ttsmp3.com/), the 'hide cards' audio file was downloaded from [Pixabay](https://pixabay.com/sound-effects/fast-simple-chop-5-6270/), and [CloudConvert](https://cloudconvert.com/) was used to convert them into m4a files.

### Mockups given to me
<table>
  <tr>
    <td><img src="https://github.com/ChenYefet/Calculator/assets/122983411/79f55e30-e33f-4c52-9196-2f62529d35f1" alt="mockup-1-card"></td>
    <td><img src="https://github.com/ChenYefet/Calculator/assets/122983411/93e04545-ae19-45a8-9422-d82174d08641" alt="mockup-2-card"></td>
    <td><img src="https://github.com/ChenYefet/Calculator/assets/122983411/44182f31-a6c9-4399-9e31-21b1d1219ef8" alt="mockup-3-card"></td>
  </tr>
</table>

### Instructions given to me
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
