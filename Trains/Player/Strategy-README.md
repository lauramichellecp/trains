###interface IStrategy
Interface that all strategies must adhere to. A strategy must be able to:
- Choose Set<Destinations> given a set of destination options and information about the beginning of the game
- Decide on a TurnAction given information about the current state of the game for a particular player.

#####TurnAction
Small package of information indicating the type of turn requested and details about it if applicable.
It either specifies:
- DRAW_CARDS (no additional info)
- ACQUIRE_CONNECTION (with the desired connection)

###abstract class AStrategy implements IStrategy
Abstract class that handles common behavior such as:
- Choosing the 2 most preferable destinations from an ordered list
- On a turn, determining if there are any affordable connections, and automatically drawing cards if not.

This abstract class provides 3 abstract protected hooks that subclasses (different concrete strategies) are to implement:
- GetPreferredDestinations (order given set of destinations by some preference ordering)
- GetPreferredConnection (given non-empty set of connections, return the one most preferred to acquire)
- ChooseDrawCards (return whether to choose to draw cards, even if a connection can be acquired)

###class Hold10 extends AStrategy
Concrete class implements abstract protected hooks to govern behavior.
###class BuyNow extends AStrategy
Concrete class implements abstract protected hooks to govern behavior.
