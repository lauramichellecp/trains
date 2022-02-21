## Data Representation
[ X ] Refactor TurnAction to not use Optional in field or method result.
- This results in redundant calls to TurnAction.getType() and Optional<IRailConnection>.get()

[ X ] Update UnorderedPair definition to make it a subclass of an OrderedPair, overriding only HashCode and .equals()
- Currently, the meaning of UnorderedPair is quite weird, since it respects order in some ways and doesn't in others
- Naming can also be changed to make this relationship very clear

[ X ] Remove copyPlayerData() method from IPlayerData interface and offer it as a constructor instead
- This is much more secure than simply trusting that the implementations of the interface copy it correctly

[ X ] Bonus: For strategy, have a README or interface module/file contains a diagram for organization of code.


## Functionality for Data

[ X ] In Referee, have a single-point of control functionality for calling a player's functions/methods  

[ X ] Add unit test for BuyNow strategy requesting more cards  

[ X ] Fix TestPlayer tests to have correct file path for class loading  

[ X ] Fix IPlayer API to return destinations NOT chosen
- Changes must propagate to player implementation, unit tests, and referee expecting destinations from the player  

[ X ] Validate number of players given to referee
- Spec requires 2 - 8 players, so referee should enforce this
- Test this validation  
  
[ X ] Fix MapRenderer tests to reflect allowing multiple connections between cities
  
[ ] Fix TestRefereeGameState tests to reflect all refactorings since Milestone 4  
- NOTE: We are in the process of re-designing our Referee to have it contain the functionality currently residing in RefereeGameState, so this item is pending.  

[ ] In Referee, have a ranking function separate from the reporting function
- NOTE: We are currently communicating with the grader for this point, since we believe that our implementation
handles ranking of players by score separately from the calculation of scores. A GameEndReport IS the ranking of players by score (and which players cheated).  

[ X ] In ScoreGraphUtils, optimize the efficiency and clarity of longest path methods
- Currently, the methods are difficult to read and understand where the vast inefficiencies of brute force may be coming from  

[ X ] Fix the outdated documentation in strategy package
- There is missing doc for parameters and return statements for various methods  

[ ] In MapRenderer, refactor methods and change visibility 
- Whatever component would draw occupied rails, etc. needs to know where the connections and cities were drawn  

[ ] In MapRenderer, center text for the name of city
- Currently, it appears in the top-right where it can be difficult to see against connections behind it  

## Feedback on code that was fixed before this milestone
[ X ] Have a function to determine all connections that can still be acquired
- This can be in RefereeGameState, PlayerGameState, or a utility function. It just has to exist somewhere.

[ X ] Have a function to decide whether it is legal to acquire a connection for a player
- This should exist in RefereeGameState, and it would be nice to be an exportable function for players to be able to use





