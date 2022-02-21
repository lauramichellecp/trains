package manager;

public interface ITournamentManager {

  /**
   * Runs the tournament from start to finish and outputs the result (the winning players and
   * misbehaving players).
   *
   * @return The winning players and the misbehaving players, packaged as a TournamentResult.
   */
  TournamentResult runTournament();
}
