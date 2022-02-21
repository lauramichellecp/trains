package utils;

import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/** Utility class for communication with a player */
public class CommunicationUtils {
  /**
   * A single point of functionality for calling a player's functions/methods an action on a given
   * player. Returns Optional.empty() if there's an exception or timeout. This method has no
   * side-effects on anything about this referee.
   *
   * @param action the action to attempt on a player
   * @param player the given player
   * @param <T>    the type of response for the interaction
   * @return An optional containing the player's response if present or indicating player ought to
   * be removed if empty.
   */
  public static <T, U> Optional<U> tryPlayerInteraction(Function<T, U> action, T player, int timeOutInSeconds) {
    ExecutorService executor = Executors.newCachedThreadPool();
    Callable<U> task = () -> action.apply(player);
    Future<U> future = executor.submit(task);
    try {
      U result = future.get(timeOutInSeconds, TimeUnit.SECONDS);
      executor.shutdownNow();
      return Optional.of(result);
    } catch (Exception e) {
      future.cancel(true);
      executor.shutdownNow();
      return Optional.empty();
    }
  }
}

