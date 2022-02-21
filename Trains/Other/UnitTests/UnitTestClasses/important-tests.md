# Important tests to write for milestone 6

- Referee
    - only draw cards mocked players
        - game ends by deck empty and all players choose draw
    - one player that attempts to acquire a connection without resources and one only draw cards
        - game ends by all remaining players draw and deck is empty and the cheaty player is eliminated
    - two players that attempts to acquire a connection without resources
    - valid game of BuyNows
        - game ends and there is a ranking
    - referee test with a player that throws an exception
    - construction tests on referee
    - check cards received by the player
        - validate cards received in mock player
    - add check that a player doesnt start a turn with fewer than two rails

 
- Player
    - load player from class file and call several methods on it
    - load Hold10 or BuyNow not from file and check correct responses
