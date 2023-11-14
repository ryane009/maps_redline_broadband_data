package edu.brown.cs.student;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testng.Assert;

public class TestFuzz {

  private int numTrials;

  /**
   * Gets a random integer
   * @param bottom the inclusive lower bound
   * @param top the exclusive top bound
   * @return the random integer in the interval [Bottom, Top)
   */
  private Integer getRandomNumber(Integer bottom, Integer top) {
    final ThreadLocalRandom r = ThreadLocalRandom.current();
    return r.nextInt(bottom, top);
  }

  /**
   * reverses a list of Integers
   * @param inList the list to be reversed
   * @return inList but reversed
   */
  private ArrayList<Integer> listReverse (ArrayList<Integer> inList) {
    ArrayList<Integer> returnList = new ArrayList<>();
    for (Integer i : inList) {
      returnList.add(0,i);
    }
    return returnList;
  }

  /**
   * returns a Integer List of random length containing random Integers from -1000 to 1000
   * @return
   */
  private ArrayList<Integer> buildRandomList() {
    ArrayList<Integer> returnList = new ArrayList<>();
    int listLength = getRandomNumber(0, 1000);
    while (returnList.size() < listLength) {
      returnList.add(getRandomNumber(-1000, 1000));
    }
    return returnList;

  }

  /**
   * Initialize numTrials
   */
  @BeforeEach
  public void setup() {
    this.numTrials = 100;
  }

  /**
   * Generates a random list and tests reverse on that list numTrials times
   */
  @Test
  public void testReverse() {
    ArrayList<Integer> testList;
    ArrayList<Integer> reverseList;
    for (int i = 0; i < this.numTrials; i++) {
      testList = this.buildRandomList();
      System.out.println(testList);
      reverseList = this.listReverse(testList);
      for (int j = 0; j < testList.size(); j++) {
        Assert.assertEquals(testList.get(j), reverseList.get(reverseList.size() - j - 1));
      }
    }
  }
}
