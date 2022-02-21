package map;

import map.City;
import map.Destination;
import map.ICity;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class TestDestination {

  @Test
  public void TestConstruction() {
    Assertions.assertThrows(IllegalArgumentException.class, () -> {
      new Destination(new City("same",0,0), new City("same",1,1));
    });

    Assertions.assertThrows(NullPointerException.class, () -> {
      new Destination(null, new City("same",1,1));
    });

    Assertions.assertThrows(NullPointerException.class, () -> {
      new Destination(new City("same",1,1), null);
    });
  }

  @Test
  public void TestCompare() {
    ICity a = new City("a",0,0);
    ICity b = new City("b",0,0);
    ICity c = new City("c",0,0);
    Destination ab = new Destination(a, b);
    Destination ba = new Destination(b, a);
    Destination ac = new Destination(a,c);
    Destination bc = new Destination(b, c);
    Assertions.assertEquals(0, ab.compareTo(ba));

    // Compare by earliest element
    Assertions.assertEquals(b.getName().compareTo(a.getName()), bc.compareTo(ac));
    Assertions.assertEquals(-1 * b.getName().compareTo(a.getName()), ac.compareTo(bc));

    //Compare by latest element
    Assertions.assertEquals(b.getName().compareTo(c.getName()), ab.compareTo(ac));
    Assertions.assertEquals(-1 * b.getName().compareTo(c.getName()), ac.compareTo(ab));
  }
}
