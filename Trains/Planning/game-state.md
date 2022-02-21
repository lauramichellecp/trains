# Trains: Game State Representation

The referee Data representation of the game state has all the information to represent the game
state, and it has the ability to generate a read-only object for a specified player. This read only
object is the player data representation.

## Total Game State: Referee's game state data representation
This will contain all the information to represent the state of the game from the instant just 
before cards and trains are given to players, until the game has ended and scoring is completed.

### OmniscientGameState Data Definition
An OmniscientGameState has:
- Static data:
    - An ITrainMap to represent the game board
    - A list of PlayerID in turn order
    
- Dynamic data about the overall game state: 
    - A List cards in the deck
    - A Map of occupied IRailConnections to occupying String PlayerID
        - If a RailConnection that is in the game map is not in this Map, then it is not occupied
    - An int representing the number of turns that have elapsed
   
- Dynamic data about each player:
    - A Map of String PlayerID to PlayerData object
    
A PlayerData has:
- A List of Destinations that this player selected
    - This will start as empty when the game state object is created, and will be populated when the player selects their two destinations.
- A map of RailCard to int number of cards of that color in this player's hand
- An int number of rails that this player has left in their bank

### PlayerGameState DataDefinition
A PlayerGameState has:
- A playerID of the player this object is assigned to
- A read-only OmniscientGameState
    - This is enforced in two ways
        1. The PlayerGameState maintains a reference to a read-only interface for the OmniscientGameState
            - each method takes in a playerID when only some players would know the information, and returns only the information that that player would know
        2. The PlayerGameState itself implements a read-only interface that calls the corresponding methods on the underlying OmniscientGameState with the PlayerID of the player this is assigned to.
    
    
## Operations on each representation

### OmniscientGameState Operations
- Modify the current game state
    - Remove a number of cards of a specific color from the hand of the player with a specific PlayerID
    - Add an entry mapping an IRailConnection to a specific PlayerID to the OccupiedRailConnections map
    - Remove a number of cards from the top of the deck, and add them to the hand of the player with a specific PlayerID
    - Move to the next player's turn
        - Use the list of PlayerID in turn order, and the int turn number to determine who goes next
        
An important note is that the game state can no longer by modified after the game is over. The game ends when the number of rails left in a player's bank reaches 2, 1, or 0 at the end of their turn, and after each other player has taken another turn.

- View all game state
    - Get a defensive copy of the list of cards left in the deck
    - Get a defensive copy of the map of occupied connections
    - Get the data representation of the map
    - Get a defensive copy of the list of players in turn order
    - Get a defensive copy of the map of cards in the hand of the player with any PlayerID
    - Get the number of rails left in the bank of the player with any PlayerID
    - Get the destinations that were selected by the player with any PlayerID
    
- Create a PlayerGameState object for a specific PlayerID
    - The Referee will give these objects to the corresponding player

## PlayerGameState Operations
All of the Player's game actions will go through the Referee, so they only need a read-only representation of the game state.
The Referee will handle modifying the game state to carry out each of these actions.

- View all public game state:
    - Get the number of cards left in the deck
    - Get a defensive copy of the map of occupied connections
    - Get the data representation of the map
    - Get a defensive copy of the list of players in turn order
    
- View generic information about other players:
    - Get the number of cards in the hand of the player with any PlayerID
    - Get the number of rails left in the bank of the player with any PlayerID
    
- View hidden information about the player that this PlayerGameState is assigned to:
    - Get a defensive copy of the map of cards in the hand of the player that this PlayerGameState is assigned to
    - Get the destinations that were selected by the player that this PlayerGameState is assigned to

## Notes
These are a few things that we intentionally left out of this data representation:
- handling removed players
    - We are letting the referee make all game changes and change the turn, so the referee will be able to modify the game state to handle skipping the removed player however they want.
- post game scoring
    - We are letting the referee handle all scoring, the OmniscientGameState contains enough information for the referee to calculate scores after the game has ended.
    
- Removed from playerGameState in milestone 4:
    - Get the PlayerID of the player who triggered the end of the game
    - Get whether the game is over
- Removed from refereeGame State in milestone 4:
    - Turn Counter
        - Nothing in the game state tracks whose turn it is, so this last bit of turn functionality 
        should live in teh referee with everything else