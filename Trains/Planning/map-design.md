# Trains: Game map.TrainMap Data Definition

## map.TrainMap data definition

>The Valid operations on a map are:
>- Get information about the board and the state of the game
>   - Get all Cities
>   - Get all Rail Paths
>   - Get names of all cities
>   - Get a collection of all pairs of Cities connected by some series of Rail Paths

_Note: Validation logic, such as determining if a rail is already occupied, if a given set of cards can be used to occupy a particular Rail Path, etc. should not be enforced in the map.TrainMap itself. This is the job of a separate referee component to determine which operations on a map.TrainMap are valid in the context of a particular game._

To fulfill this Interface, we will represent the map.TrainMap with the following data:

>A map.TrainMap is a set of Destinations, a set of Rail Paths describing connections between those 
>destinations, and an Occupied Rail Mapping.

A map.TrainMap represents the central part of the game board, containing city destinations and connections 
between them. Also, keeps track of connections that are acquired by players throughout the game.


>A Destination is a String name

A Destination represents one city on the game board. The String name is the name of that destination
that the players are given when they are told to connect two destinations, and there should not be any duplicate Destinations (also implied by the use of a set).


>A Rail Path has:
>- An unordered orderedPair of different Destinations
>- An int Length (one of 3,4,5)
>- An enum Color

A Rail Path represents one connection between two Destinations on the game board. That requires a 
number (given by Length) of train cards of a particular Color to occupy.


>A Color is an enumeration of:
>- red
>- blue
>- green
>- white

A Color is one of the four valid colors for Rail Paths and train cards.


>An Occupied Rail Mapping is a mapping from Rail Path to int PlayerID of the occupying player.

The Occupied Rail Mapping contains an entry for every Rail Path in the map.TrainMap, including unoccupied 
ones. Unoccupied Rail paths are mapped to the PlayerID 0. PlayerIDs for occupied paths should be positive integers.
