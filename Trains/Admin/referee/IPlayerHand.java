package referee;

import java.util.Map;

public interface IPlayerHand<T extends Enum<T>> {
  void addCardsToHand(T cardType, int amount) throws IllegalArgumentException;

  int getNumCardsOfType(T cardType);

  int getTotalNumCards();

  void removeCardsFromHand(T cardType, int amount) throws IllegalArgumentException, IllegalStateException;

  Map<T, Integer> getHand();
}
