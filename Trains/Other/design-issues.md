Notes of potentially major design issues/unresolved questions:
- Destination is a UnorderedPair<ICity>, and currently allows for a destination of the same two cities anywhere this isn't explicitly validated. For example, BasicInitialPlayerData.
- There is a lot of code in RefereeGameState, can any of it be moved into a real PlayerData class/interface?
- The notion of a hand of cards is more complicated than just a HashMap<RailCard, Integer> because of the case with zero cards. This structure is used a lot. Does it deserve its own data definition?
- Our visualization is really not satisfying, but changes to it break our (already broken) harnesses.
- RefereeGameState does a LOT of validation. Can these be delegated elsewhere or can certain constructor inputs be data definitions themselves that imply they are valid (e.g., no negative number of rail cards)

- Should the game state do ANY validation of rules?
    - Where are the rules?
    - are the rules in the rules checker?
    - Self-eval seemed to check that the rules were in the game state

"Should the game state provide enough information for the referee to determine if an action is valid itself, or should the referee ask the game state if an action is valid?"
"Is the rules checker a separate component, or is it intertwined with the object that is performing actions on the game state?"

