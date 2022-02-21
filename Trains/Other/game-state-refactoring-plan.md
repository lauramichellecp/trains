A RefereeGameState has the following fields:
- List<PlayerData> playerDataInTurnOrder //constant, never changes even if players are kicked
- HashSet<PlayerID> kickedPlayers // tracks IDs in playerDataInTurnOrder of removed players
// in the future, could be a map from Integer -> ReasonForRemoval or other information necessary
- ITrainMap map
- List<RailCard> deckOfCards
- int indexOfCurrentPlayer

Constructor(List<PlayerData> playerData, List<RailCard> deck, ITrainMap map)

A note on PlayerID:
The playerID is used to identify players uniquely only for the Referee and RefereeGameState.
It's primary benefit is to be able to reference a player that is not active and determine uniquely
which player is active, for the purposes of the referee's rule enforcement 
(i.e., removing players who play out of turn)


Methods:
- getActionChecker()
- acquireConnectionsForActivePlayer(IRailConnection desiredConnection)
- drawCardsForPlayer(int amount)
- getActivePlayerState()
- advanceTurn()
- getActivePlayerID()
- removePlayer(PlayerID)

A playerID is an int representing index in initial turn order.

A PlayerData represents the data on a player for referee/referee game state has the following methods:
- getID()
- getCards() (mutable reference to HandOfCards)
- getNumRails()
- setNumRails()
- getDestinations() (defensive copy)
- getOwnedConnections()
- copyData() (defensively copies all fields to pass to player interface)

A PlayerGameState represents data given to a player (i.e., the player view of the current state of the game) 
and has the following fields:
- Collection of cards
- Number of rails
- Destinations? (no)
- Set<IRailConnection> ownedConnections
- List<OtherPlayerInfo> otherPlayerInfo
    - Does this show kicked players?
    - How is it in turn order relative to this player?
Methods other than getters:    
- Set<IRailConnection> calculateAvailableConnections(Map)


A OtherPlayerInfo has:
- Set<IRailConnection> ownedConnections


============================== OLD ================================

Note: this design can also still keep Strings for playerID - the only difference is that playerTurnOrder becomes more important

Benefits:
- Construction of RefereeGameState is simpler (has fewer fields)
- Validation of RefereeGameState construction is simpler since there is no redundant information b/w turn order and initial data mappings
- No need for separate InitialPlayerData interface and implementation
- Constructor and accessors for PlayerGameState become more manageable

Notes:
- Getters and modifiers on RefereeGameState become condensed into GetPlayerData(playerID), but this has significant implications, some good and some bad. RefereeGameState would return modifiable references. The justification for this is that the referee is modifying these things anyway, so it simply shifts a little more work onto the referee. It could also be possible to have convenience methods on the RefereeGameState that do work like "acquireConnection()". It will, however, make it difficult to ensure that the state is valid after an action.
- Decision is to collapse RefereeGameState getters into a single defensive copy of PlayerData