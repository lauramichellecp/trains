# Player-Manager Interface

```
/**
 * Informs the player that their signup with the tournament has been recognized, and they are 
 * currently on the waitlist with initialPositionOnList - 1 players in front of them.
 *
 * The Tournament Manager will call this method on each player immediately after it receives the 
 * player's request to sign up. Even if there is open space in the tournament, all players start on 
 * the waitlist.
 * 
 * The player will not be informed if their position on the waitlist changes, only if they reach the
 * front and are admitted to the tournament.
 */
void informOnWaitlist(int initialPositionOnList);

/**
 * Informs the player whether or not they have been admitted to a tournament that will begin 
 * imminently. If the boolean in is True, the player is in the tournament, otherwise the player is 
 * not in this tournament and is also no longer on the waitlist to get into the tournament.
 * 
 * The player will return a boolean indicating whether they accept entering the tournament
 * 
 * The Tournament Manager will call this method on players from the front of the waitlist, and admit
 * them to the tournament. When the player receives this method if inTournament is True and it 
 * responds with the correct Acknowledgement, it can expect to be contacted by a referee to begin 
 * playing a game imminently. When the player receives this method and inTournament is False or the 
 * Tournament Manager does not receive the Acknowledgement, the player can expect no further 
 * communication from the Tournament Manager.
 */
Acknowledgement informInTournament(boolean inTournament)

/**
 * Informs the player about their final standing in the tournament.
 * 
 * The Tournament Manager will call this method with the boolean False immediately after this player 
 * has been eliminated from the tournament.
 * The Tournament Manager will call this method with the boolean True on the winner(s) of the 
 * tournament after the end of the final game.
 */
void informTournamentResult(boolean win)


Acknowledgement is an Enum with 1 value: I_EXIST
```

# Player-Manager Protocol

After a player has performed the signup with the communication layer of our system, they will be 
given to the Tournament Manager one-by-one.

The Tournament Manager will then call informOnWaitlist() on each player immediately when it receives
the player. The purpose of this step is to inform the player that the Tournament Manager has 
recognized their existence. There is an indeterminate amount of time between this step and the next.

When the Tournament manager knows for sure whether the player is accepted into the tournament and is
ready for this player to play in the tournament, it will call informInTournament() on the player. 
The argument to informInTournament() will tell the player whether they have been let in to the 
tournament or kicked out. The player must respond with the correct Acknowledgement if they have been
let into the tournament to confirm that have not disappeared in teh time since registration on the
waitlist.

While the tournament is in progress, the player can expect to receive calls from the corresponding 
referee, but the details of that are out of scope for this protocol.

The Tournament Manager will call informTournamentResult() on a player with boolean False immediately
after this player has been eliminated from the tournament.
This will happen immediately after a game in which the player misbehaves, but will also happen when
the player is eliminated from the tournament for losing (Not necessarily losing a game, but losing 
the tournament).

The Tournament Manager will call informTournamentResult() with the boolean True on the winner(s) of 
the tournament after the end of the final game.

After the player receives informTournamentResult() with either argument, there is no further 
communication between the tournament manager and player.

# Manager Specification

The Tournament Manager will manage 1000s of players and organize many games to run a tournament and 
create a ranking of the entering players. When the Tournament Manager is created, it will be given a
minimum and maximum bound of players to place in each game, and a function from a list of that 
number of players to a referee of the game that the players in the tournament will be playing.

These players must implement the above interface and protocol, and additionally be compatible with 
the player-referee interface and protocol defined for the game that this tournament will be playing.

All Tournament managers will be agnostic to the game that they are running. They will create 
game-specific referees using the List<Player> -> Referee function that it is given as input.

Specific tournament manager implementations will decide how many players to place in each game, but
must follow the upper and lower bounds that it is given. Specific implementations will also decide
the tournament structure and mechanism for determining the winners and losers.

During the course of a tournament, the Tournament Manager will receive game reports from referees.
```
A Game Report is:
    - List<PlayerScore> ranking
    - Set<Player> removedPlayers
    
A PlayerScore is:
    - Player thisPlayer
    - int score
    
The list of PlayerScores is ordered by the score in the PlayerScore and represents the ranking of 
the players in the game.
The Set of Players is the set of players that misbehaved during the game, and were removed.
```
The Tournament Manager is responsible for ranking players in the tournament from these GameReports
and implementations will determine how/when they will collect statistics from these GameReports and
whether/how to send to tournament Observers.

At the end of a tournament, the Tournament Manager will return a ranking of the players who were not
eliminated for misbehaving, and a set of players that misbehaved.