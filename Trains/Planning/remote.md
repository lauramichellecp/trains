## Remote Interaction Protocol Diagram:
![Logical Interactions 2](https://media.github.ccs.neu.edu/user/6225/files/7d91aec8-a976-42ed-b498-823c4a9ae4d0)

## Remote Interaction Protocol Details:

![remote_proxy_pattern_components](https://media.github.ccs.neu.edu/user/6225/files/5da5622d-2011-466a-b60b-25886ba08fe5)

An Admin component that needs to communicate with a player (manager or referee) will have access to a ProxyPlayer that implements the logical interface between that admin component and the player (the admin component is none the wiser that there is any proxy).

The ProxyPlayer, in order to implement the logical interface expected by the Admin component calling methods on it, will “delegate” all of the work to the player by sending JSON messages via TCP to the ProxyAdmin, whose job is to send JSON messages back to the ProxyPlayer that indicate what the actual player’s response is. The ProxyPlayer then deserializes this response in order to return to the Admin component data of the expected return type in Java.

The ProxyAdmin receives JSON messages from the ProxyPlayer indicating what information is requested from the actual player. The ProxyAdmin will deserialize these messages in order to call the appropriate actual (Java) methods on the Player. Once it receives a Java response from the Player, the ProxyAdmin serializes the response to send to the ProxyPlayer.

The Player implements the logical interface expected by the admin components (manager and referee). It has no idea who is calling these methods (i.e., no idea that it is a ProxyAdmin and not the actual Admin).
With these proxy components, neither the Admin nor the Player need to be changed at all, because the proxy components are merely an implementation detail of the logical interface between these two components.

## JSON Message Format Details:

All JSON messages sent between the ProxyPlayer and ProxyReferee (via TCP) will have the following format.

{
“type”: String messageType,
“data”: JSONElement data
}

The ‘type’ field is a string that indicates what logical method the data corresponds to. This is so that proxy components receiving JSON messages can tell what the message is for (e.g., takeTurn, chooseDestinations). That way, the proxy component can rely upon the data field to be formed in a particular way (specified below) to match the message type. There is a 1-1 correspondence between messageType(s) and methods in the logical interaction between a Referee/Manager and a Player. The same messageType is used for the JSON message that acts as the method call AND for the JSON message that acts as the method return. Using the same messageType for method call AND return will never lead to confusion because each proxy component receiving a JSON message will only receive it from one source in one direction.

The ‘data’ field has a value that can be any JSON, and its form and validity is dependent entirely on the ‘type’ field. It can range from a simple boolean to a complex object representing a train map.

The expected form of the value for the data field for each ‘type’ is specified below. It is specified in terms of the JSON vocabulary developed throughout the Testing Tasks for milestones (and are in **bold**). For example, ‘Map’ means an entire JSON object representing a TrainMap according to https://www.ccs.neu.edu/home/matthias/4500-f21/3.html#%28tech._map%29.


The following specifies the expected format of the value of the ‘data’ field for a given ‘type’ of message. Note that for a given message, the expected structure depends on whether the message is a proxy method call (i.e., from ProxyPlayer to ProxyReferee) or a proxy method return (i.e., from ProxyReferee to ProxyPlayer). The first bullet is the structure of the value of the ‘data’ field (as types) and the second bullet is an interpretation.

* “Tournament_start” 
  * Call Input - boolean 
    * Indicates whether the player is actually playing in this tournament 
  * Return - **Map**
    * The map that the player submits to the tournament 
* “Setup” 
  * Call Input - [**Map**, integer, **Card Collection**]
    * A JSON Array containing a game map, integer number of rails, and initial player hand 
* “Pick” 
  * Call Input - **Destination**[]
    * A JSON Array of destinations that are the player’s destination options (order is irrelevant)
  * Return - **Destination**[]
    * A JSON Array of destinations that the player rejects (order is irrelevant)
* “Play”
  * Call Input - **PlayerState**
    * The JSON Object representing the player’s view of the game state for taking a turn
  * Return - Either the string “more_cards” or an **Acquired**
    * The two possibilities for the player’s turn. Recipients of this message will have to parse carefully to check which possibility the message returned.
* “More”
  * Call Input - **Color**[]
    * JSON Array of the Color of the cards that the player drew as a result of requesting more cards. Order is irrelevant.
* “Win”
  * Call Input - boolean
    * True if player won the game (tie included), false if player lost
* “Tournament_end”
  * Call Input - boolean
    * True if player won the tournament (tie included), false if player lost
