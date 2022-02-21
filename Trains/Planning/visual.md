To: Codemanistan Coding Team  
From: William Cutler and Ronan Loughlin  
CC: CS 4500, Software Development, Professors and TAs  
Date: October 6, 2021  

# Map Visualization Plan

### Reusable Component
The main body of the program should be contained in a rendering method that takes in the following:
 - Graphics
 - Integers for canvas width and height (in pixels)
 - map.ITrainMap
 
 ### Rendering details
 This rendering should show every city and every rail connection that connects these cities. Cities should be shown as a dot in the proper relative position with the name displayed next to it. Rail connections should be a line of the specified color between the cities broken up into a number of equal-length segments equal to map.IRailConnection.getLength().

### The MapPanel
There should be a class called MapPanel that extends JPanel and calls this method inside its paintComponent(). This class should determine an appropriate width and height for the canvas, and pass these values to the rendering method. The rendering method will get the map.ITrainMap from the constructor to this class. See Trains/Common/map.ITrainMap.java for the interface specifying how to access the appropriate information from an map.ITrainMap.

### Displaying the MapPanel
There should be a method in another class that takes in the map.ITrainMap information, instantiates this MapPanel, and makes it visible.

### Notes
The separation of these three parts is important - we mostly care about part 1 (reusable component) because we plan to layer more things on top of it in the various displays (i.e., occupied paths, extra info for player views, observers, etc.). The subsequent parts are just as proof-of-concept to display the current data representations.
