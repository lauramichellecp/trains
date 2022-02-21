package referee;

import game_state.RailCard;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TrainsPlayerHand implements IPlayerHand<RailCard> {

  Map<RailCard, Integer> playerHand; // All card types are represented as keys (possibly with 0 cards)

  public TrainsPlayerHand(List<RailCard> initialHand) {
    Objects.requireNonNull(initialHand);
    this.playerHand = new HashMap<>();
    for (RailCard card : RailCard.values()) {
      this.playerHand.put(card, 0);
    }
    for (RailCard card : initialHand) {
      this.addCardsToHand(card, 1);
    }
  }

  public TrainsPlayerHand(Map<RailCard, Integer> initialHand) throws IllegalArgumentException {
    Objects.requireNonNull(initialHand);
    this.playerHand = new HashMap<>();
    for (RailCard card : RailCard.values()) {
      int numInitialCards = initialHand.getOrDefault(card, 0);
      if (numInitialCards < 0) {
        throw new IllegalArgumentException("Cannot have negative cards.");
      }
      this.playerHand.put(card, numInitialCards);
    }
  }


  @Override
  public void addCardsToHand(RailCard cardType, int amount) throws IllegalArgumentException {
    if (amount <= 0) {
      throw new IllegalArgumentException("Must add positive number of cards.");
    }
    this.playerHand.put(cardType, this.playerHand.get(cardType) + amount);
  }

  @Override
  public int getNumCardsOfType(RailCard cardType) {
    return this.playerHand.get(cardType);
  }

  @Override
  public int getTotalNumCards() {
    return this.playerHand.values().stream().mapToInt(Integer::intValue).sum();
  }

  @Override
  public void removeCardsFromHand(RailCard cardType, int amount)
      throws IllegalArgumentException, IllegalStateException {
    if (amount <= 0) {
      throw new IllegalArgumentException("Must remove positive number of cards.");
    }
    int currentNumCards = this.playerHand.get(cardType);
    if (amount > currentNumCards) {
      throw new IllegalStateException("Cannot remove more cards than available.");
    }
    this.playerHand.put(cardType, currentNumCards - amount);
  }

  @Override
  public Map<RailCard, Integer> getHand() {
    return new HashMap<>(this.playerHand);
  }
}
