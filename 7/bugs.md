Unit test for equality of an OrderedPair object and an UnorderedPair object failed symetric 
property, since an UnorderedPair was an instance of an OrderedPair but not the other way
around. Bug was fixed by creating an abstract class `APair.java` holding the pair's fields 
and overriding equality for both `OrderedPair.java` and `UnorderedPair.java`

- failing unit test `testEquals()` prior to fix: https://github.ccs.neu.edu/CS4500-F21/massasoit/blob/54e90d7f0f3ad90339a6b116a79a42e72b7a0e97/Trains/Other/UnitTests/UnitTestClasses/TestUnorderedPair.java#L64-L77
- fix: https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/2e795b8368c22c072418072097be95d8aa5a55b1  



In `TrainsReferee.java`, we did not properly remove players who cheated when acquiring a connection. This bug 
occured after in the new single-point of control functionality for calling a player's functions/methods

- failing unit test `testOneInvalidPlayer()` prior to fix: https://github.ccs.neu.edu/CS4500-F21/massasoit/blob/51e204bd5430dfe1fb80081a2e207252745ceffb/Trains/Other/UnitTests/UnitTestClasses/TestTrainsReferee.java#L213-L236   
- fix: https://github.ccs.neu.edu/CS4500-F21/massasoit/commit/a08e7eff913c294b96059a5e2b58af1f03bffbaf  


