# Modifications to code

1. 'winNotification' and 'tournamentResult' were not being called on the player by the referee and manager
   at the end of a game / tournament, respectively. 
   Git commit: https://github.ccs.neu.edu/CS4500-F21/san-juan/commit/fab8995a31dc94a90feb011182e15e78b444d0e8

3. Added a couple of lines to CommunicationUtils' "tryplayerinteraction" to properly shut down the exercutor service.
   This wasn't working previously.
   Git commit: https://github.ccs.neu.edu/CS4500-F21/san-juan/commit/77d1c773d911e2155f06770720a489c2f29c14ed

4. Refactored ITournamentPlayer and IRefereePlayer to be contained in a single interface, IPlayer.
   Nothing in the implementation of a player for a tournament changed.
   Git commit: https://github.ccs.neu.edu/CS4500-F21/san-juan/commit/12f98845b508f652ed3d075df968f6f730662a42

5. Changing our "GameEndReport" to store all the players with scores in descending order,
   instead of ascending. (Added a line to reverse the list)
   Git commit: https://github.ccs.neu.edu/CS4500-F21/san-juan/commit/48d8bfc31801fcaf0edd69210885629c47d6ee25