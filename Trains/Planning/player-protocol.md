#Overview
The player implements an interface containing methods that the referee will call on the player. The referee always initiates communication, and sometimes requires a response from the player. All interactions are functional in the sense that the players need not modify any game state directly, but will be given game state information and return a decision each time any decision is required.

Main Phases Summary:
- Setup before any referee-player interaction
- Destination Selection
- Playing the game (i.e., taking turns)
- End of game

Main Method Summary:
- chooseDestinations()
- takeTurn()
- receiveEndState()
- removedFromGame()

For more information about the phases and method details (i.e., signatures), see ```player-interface.md```.

###Phase-method relationship detail

__Pre-communication setup:__ By the time referee-player interaction occurs, the referee will have the initial setup of the game, such as the board, the deck, starting player hands of rail cards, and starting number of rails.

__Destination Selection:__ The first point of communication will occur when the players select their destinations via chooseDestinations(), which is called only once on each player, just before the first player takes their first turn. This method will be called on players one-at-a-time in turn order.

__Taking Turns in the game:__ For actual gameplay, the takeTurn() method is called on a player to determine the player's action for the turn. It is first called on the first turn of the first player, and is called on players in turn order until the game is over. This will give the player their view of the most up-to-date state of the game and the player will respond with their choice of action on that turn. The takeTurn() method is called once per turn, and expects only one strategy.TurnAction as a result, so this is a single call-and-response, and no further communication between the referee and the player will occur during the turn.

__End of game:__ Finally, once the referee has determined the game is over, it will broadcast the end state of the game to the players by calling receiveEndState() on the players. This is the only time receiveEndState() is called, and after it, no further communication between the referee and the players will occur.

__Removal of player:__ At any time before receiveEndState() has been called on a player, the referee may notify a player that they have been removed from the game (regardless of reason, but will certainly occur if the player attempts to cheat or misbehave). The referee will call removedFromGame() on the target player to inform the player this has occurred, and the player will receive no further communication from the referee.

For a visual representation of the player-referee protocol, see ```2PlayerGame.jpg``` and ```InvalidTurn.jpg```.