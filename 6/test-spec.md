1 - Tests that at exactly 10 cards with affordable connections, Hold10 chooses more cards

2 - Tests comparison of connections where every connection is affordable. Specifically, compared to the proper connection to choose, it introduces:
 - A cheaper connection with earlier second city but later first city
 - A cheaper connection with same first city but later second city
 - A connection of earlier color but more expensive
 - Connections of the same cost but later color
 
3 - Tests picking a non-preferred connection because the most preferred is affordable but already occupied  
 
4 - Tests where must draw cards because there is no affordable connection (by cards in hand) available  

5 - Tests must draw cards due to insufficient rails