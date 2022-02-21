TurnAction: should not use Optional in field or method result
- We removed the Optional entirely, and instead store the IRailConnection as 'null' internally and throw exception for attempted access to non-existent IRailConnection  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/54e90d7f0f3ad90339a6b116a79a42e72b7a0e97

UnorderedPair: Update definition to make it and OrderedPair a subclass of an abstract class APair, overriding only HashCode and .equals()
- APair makes it clear that fields put in can be retrieved in the same order as fields taken out  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/2e795b8368c22c072418072097be95d8aa5a55b1

'strategy' package: fix the outdated documentation in strategy package
- We added missing documentation for fields and return statements  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/0d10b35a0c3fa917bd3d7e8f2053e73d15edb98a

ScoreGraphUtils: optimize the efficiency and clarity of longest path methods
- Documented all methods public and private, used list abstractions instead of cumbersome loops, and break up large methods into smaller ones with distinct purpose statements.
- Removed double-counting, documented where brute-force inefficiency occurs  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/92dd073df5beb2439ae411559c7f9db41821d8d9

IPlayerData: remove copyPlayerData() method from IPlayerData interface and offer it as a constructor instead
- This was done directly with new constructor in PlayerData, which also copies fields defensively to ensure only RefereeGameState can modify PlayerData  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/f09cde0c1a528d2ffeba1a2ef0046492751af81e

Strategy Unit Tests: add test for BuyNow having to draw cards
- A test was created where the player cannot afford any connections even with > 10 cards
- The test asserts that both BuyNow() and Hold10() will return a TurnAction specifying DRAW_CARDS  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/60c09b5b05e113ccebfb0aea1e842aaec30d8f0c

Strategy folder README: add a README specifying the code organization for strategies
- This can be found in "massasoit\Trains\Player\Strategy-README.md"  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/5189d284cc2b441c1e0c1d3581203aa2346a6b91#diff-b9b1325d0c5743534c02137610c1e55b44710a58296223b5aa8cb0597c77eb25

MapRenderer Unit Tests: add test to reflect multiple connections and fix broken tests
- The test for the length indicator was updated to reflect the fact that we use a black dot instead of text
- A test was added to test the ordering and position of multiple parallel segments between cities  
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/51e204bd5430dfe1fb80081a2e207252745ceffb

In Referee: have a single-point of control functionality for calling a player's functions/methods
- We implement this in the tryPlayerInteraction method, which is functional and attempts any action on the player that is specified,
 and its return result indicates whether the player must be kicked for cheating.
- We also added a unit test where we created a mock player that takes too long to respond to test that they get kicked out properly   
https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/a08e7eff913c294b96059a5e2b58af1f03bffbaf


