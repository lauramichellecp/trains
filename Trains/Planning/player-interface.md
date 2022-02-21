# Player Interface Planning:

### Phases in a game of Trains
1. The Referee determines the player's initial cards and trains (before any communication between referee and player via this interface)
2. One-by-one, the Referee gives out destinations by calling chooseDestinations() on the players. This gives the players a representation of the initial game state and expects the players to choose the specified amount of destinations (two) and return them.
3. One-by-one, the Referee will call takeTurn() on each player, in the correct turn order. This gives the player the most up-to-date version of the PlayerGameState and expects an action the player wishes to take.
    - Each player will only be given an update on the state of the game at the start of their turn 
    through this takeTurn() call.
    - If a player is removed from the game, the Referee will handle skipping their turn.
4. Once the game has ended, the Referee will determine the winner, and then call receiveEndState()
on each Player to inform them of the winner and the state of the game as it ended.
    - The Referee will end communication with the player via this interface after this call to receiveEndState()

### Interface that a Player should implement:
```
/**
* The Player should select numToChoose destinations from the Set destinationOptions, and return them
* to the referee. 
* The Referee will call this method during setup of the game after the initial setup of the map, and
* after cards and trains have been determined for each player.
* The Player will be given the initial state of the game, PlayerGameState initialPlayerGameState, 
* which contains the cards in the player's starting hand, the game map, and all other information 
* that should be available to the player.
*/
Set<Destination> chooseDestinations(Set<Destination> destinationOptions, int numToChoose, 
PlayerGameState initialPlayerGameState);

/**
* The Player should decide what action it will take on its turn by returning a valid strategy.TurnAction. 
* The Referee will call this method when it is this Player's turn, and provide the current state of 
* the game that is visible to this player, currentPlayerGameState.
*/
strategy.TurnAction takeTurn(PlayerGameState currentPlayerGameState);

/**
* The Referee will call this method after the end of the game to inform this Player that the game 
* has ended, the state of the board at the end of the game, and who won.
* The Set of PlayerGameStates endStatesForAllPlayers will contain the PlayerGameStates of all 
* players in the game at the moment the game ended, so that this player can see all selected 
* destinations, all hands, and get all information necessary to determine why the winner won.
*/
void receiveEndState(Set<PlayerGameState> endStatesForAllPlayers, String winnerID);

/**
* The Referee will call this method to notify a player that the player has been removed from the 
* game, for any reason. The player will receive no further communication from the referee.
* No information about the game or reason is given, nor is a response expected. This method will be
* called if the player attempts to cheat or misbehave.
*/
void removedFromGame();
```


### Data Definition
A strategy.TurnAction represents the player's desired action for a single turn, and contains the following:
- An enum strategy.Action
- An ```Optional<IRailConnection>```
    - must be empty if action is DRAW_CARDS
    - must contain a value if strategy.Action is ACQUIRE_CONNECTION
- Contains getters for the strategy.Action and IRailConnection that return defensive copies of the data

strategy.TurnAction objects are only created using the following Factory Pattern:
- strategy.TurnAction will have the methods:
```
    static strategy.TurnAction createDrawCards()
    static strategy.TurnAction createAcquireConnection(IRailConnection railConnection)
```
- strategy.TurnAction will have a private constructor
```
    private strategy.TurnAction(strategy.Action action, Optional<IRailConnection> railConnection)
```


An strategy.Action is one of:
```
- DRAW_CARDS
- ACQUIRE_CONNECTION
```