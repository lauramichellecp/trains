package utils;

import game_state.RailCard;
import map.RailColor;

/**
 * Utility class for RailCard and RailColors, and conversions between them
 */
public class RailCardUtils {

  /**
   * Determines the rail color corresponding to the given card.
   */
  public static RailColor railColorFromCard(RailCard card) {
    return RailColor.valueOf(card.name());
  }

  /**
   * Determines the rail card corresponding to the given rail color.
   */
  public static RailCard railCardFromColor(RailColor color) {
    return RailCard.valueOf(color.name());
  }

  /**
   * Determines the rail color from the given string in any case (lower, upper, etc.).
   */
  public static RailColor railColorFromLowercaseColor(String lowercaseColor) {
    return RailColor.valueOf(lowercaseColor.toUpperCase());
  }

  /**
   * Determines the rail card from the given string in any case (lower, upper, etc.).
   */
  public static RailCard railCardFromLowercaseCard(String lowercaseCard) {
    return RailCard.valueOf(lowercaseCard.toUpperCase());
  }
}
