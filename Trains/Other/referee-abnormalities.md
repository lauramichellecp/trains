During a method call on a player:
- No response or overly long response time
    - Thrown by method in our IPlayer implementation
    - Caught by referee, referee removes player from the game
- Invalid response (based on expected return type of method)
    - If response is not of valid type (not well-formed), then IPlayer implementation throws some exception
    - If response is well-formed but invalid, referee will verify validity
    - In either case, this behavior is caught by referee and player is removed
- Other unanticipated exception must not break everything
    - Exceptions that could happen with method calls to IPlayer
    - Maybe try-catch calls to game state in case we have a bug but still want to try to keep playing
Outside of a method call on a player:
    - It is possible for a player to send messages or attempt to communicate when it is not asked for. In this case, these messages are ignored. We only remove players for issues that occur during an action on their behalf (destination selection or taking turn)
What to do upon abnormal behavior:
- Remove IPlayer from active players
- Remove active IPlayerData from RefereeGameState (new method in interface)
- Make sure both referee and referee game state are in sync w.r.t. turns

