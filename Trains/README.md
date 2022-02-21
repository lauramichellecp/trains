# CS4500 Software Development Project

### Admin
The Admin directory contains the referee game state source code.

### Common
The Common directory is the code sources root. It contains all the source code for the Trains
game and surrounding infrastructure.

### Editor
The Editor directory contains the map visualizer code that is contained in the Common directory.

### META-INF
This directory contains the manifest file tht is used to build our JAR. It must be placed at the top level to fix an issue of "no manifest attribute found" when running JAR.

### Other
The Other directory contains our unit harnesses, and example files for our harnesses.

### Planning
The Planning directory contains an analysis of the Trains project plan, and a data definition for 
Trains game board. 

### Player
The Player directory contains the player game state, the Player API, and the stategy classes. 

### Remote 
The Remote directory contains the networking / remote interactions between the admin components and the remote players, includes a proxy admin and a proxy player.

```plan-analysis.md``` Includes 5 questions that we felt were unclear from the project description.

```map-design.md``` Includes the data definition for the Trains game board.

```vidual.md``` Includes a specification for how to write a visualization for the Trains game board.

```game-state.md``` Includes a specification for the data representation and operations on the game state for a game of Trains.

```player-interface.md``` Includes a specification of the API to be implemented by players for the referee-player interface. Also spells out the phases of the game with respect to this interface.

