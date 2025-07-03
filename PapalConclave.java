import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class PapalConclave {
    // Constants for configuration
    static final int NUM_CARDINALS = 135;
    static final int TWO_THIRDS = 90;
    static final int MIN_SLEEP_TIME = 100;
    static final int MAX_SLEEP_TIME = 300;
    static final int PERSUASION_DIVISOR = 100;
    static final int INFLUENCE_MAX = 101;
    static final int PERSUASION_MAX = 101;
    static final int DIVIDER_LENGTH = 50;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("Papal Conclave Simulation Starting...");
        System.out.println(NUM_CARDINALS + " cardinals are entering the Sistine Chapel");
        System.out.println("Two-thirds majority required: " + TWO_THIRDS + " votes\n");

        Conclave conclave = new Conclave();
        Thread[] threads = new Thread[NUM_CARDINALS];

        // Create and start all cardinal threads
        for (int i = 0; i < NUM_CARDINALS; i++) {
            threads[i] = new Thread(new Cardinal(i, conclave));
            threads[i].start();
        }

        // Wait for election to complete
        conclave.waitForElection();
        int newPope = conclave.getWinner();
        System.out.printf("\nHabemus Papam! Cardinal %d has been elected the new Pope.%n", newPope);
        System.out.println("Long live Pope Cardinal " + newPope + "!");

        // Clean shutdown of all threads
        for (Thread t : threads) {
            t.interrupt();
            t.join();
        }
    }

    /**
     * Represents a cardinal participating in the papal conclave.
     * Each cardinal runs in its own thread and can vote and participate in
     * discussions.
     */
    static class Cardinal implements Runnable {
        private final int id;
        private final int influence;
        private final int persuasibility;
        private int preference;
        private final Conclave conclave;
        private final Random rnd = new Random();

        Cardinal(int id, Conclave conclave) {
            this.id = id;
            this.conclave = conclave;
            this.influence = rnd.nextInt(INFLUENCE_MAX);
            this.persuasibility = rnd.nextInt(PERSUASION_MAX);
            this.preference = id; // Initially prefer themselves
            conclave.registerInfluence(id, this.influence);
        }

        @Override
        public void run() {
            try {
                while (!conclave.isConclaveEnded()) {
                    // Wait for voting phase to begin
                    conclave.waitUntilVoting();
                    if (conclave.isConclaveEnded())
                        break;

                    // Cast vote and check if election is decided
                    int winner = conclave.castVote(preference);
                    if (winner != -1)
                        break;

                    // Wait for discussion phase to begin
                    conclave.waitUntilDiscussion();
                    if (conclave.isConclaveEnded())
                        break;

                    // Participate in discussion
                    speak();
                    conclave.noteDiscussionDone();
                }
            } catch (InterruptedException ie) {
                // Restore interrupt status and exit cleanly
                Thread.currentThread().interrupt();
            }
        }

        /**
         * Simulates a cardinal speaking during the discussion phase.
         * Cardinals may change their preference based on the influence of leading
         * candidates.
         */
        private void speak() throws InterruptedException {
            // Simulate speaking time
            Thread.sleep(MIN_SLEEP_TIME + rnd.nextInt(MAX_SLEEP_TIME - MIN_SLEEP_TIME));

            int[] lastResults = conclave.getLastVoteResults();
            List<Integer> leaders = findLeadingCandidates(lastResults);

            if (leaders.isEmpty())
                return;

            // Randomly select a leader from those with maximum votes
            int leader = leaders.get(rnd.nextInt(leaders.size()));

            // Don't change preference if already supporting the leader
            if (leader == preference)
                return;

            // Calculate persuasion probability based on leader's influence and own
            // persuasibility
            int leaderInfluence = conclave.getInfluenceOf(leader);
            int persuasionProbability = (leaderInfluence * this.persuasibility) / PERSUASION_DIVISOR;

            // Change preference based on persuasion probability
            if (rnd.nextInt(PERSUASION_DIVISOR) < persuasionProbability) {
                preference = leader;
            }
        }

        /**
         * Finds all candidates with the maximum number of votes.
         * 
         * @param results Array of vote counts for each candidate
         * @return List of candidate IDs with maximum votes
         */
        private List<Integer> findLeadingCandidates(int[] results) {
            List<Integer> leaders = new ArrayList<>();
            int maxVotes = -1;

            // Find all cardinals with maximum votes
            for (int i = 0; i < results.length; i++) {
                if (results[i] > maxVotes) {
                    maxVotes = results[i];
                    leaders.clear();
                    leaders.add(i);
                } else if (results[i] == maxVotes) {
                    leaders.add(i);
                }
            }

            return leaders;
        }
    }

    /**
     * Manages the conclave process, coordinating voting and discussion phases.
     * Acts as the central authority for the election process.
     */
    static class Conclave {
        private final int[] votes = new int[NUM_CARDINALS];
        private final int[] lastVoteResults = new int[NUM_CARDINALS];
        private final int[] influence = new int[NUM_CARDINALS];
        private int votesCast = 0;
        private int discussionsDone = 0;
        private int winner = -1;
        private boolean conclaveEnded = false;
        private boolean isVotingPhase = true;
        private int round = 0;

        /**
         * Registers a cardinal's influence level.
         * 
         * @param id   Cardinal ID
         * @param infl Influence level (0-100)
         */
        synchronized void registerInfluence(int id, int infl) {
            influence[id] = infl;
        }

        /**
         * Processes a cardinal's vote and manages the voting phase.
         * 
         * @param candidateId ID of the candidate being voted for
         * @return Winner ID if election is decided, -1 otherwise
         */
        synchronized int castVote(int candidateId) throws InterruptedException {
            if (conclaveEnded)
                return winner;

            votes[candidateId]++;
            votesCast++;

            // Wait for all cardinals to vote
            if (votesCast < NUM_CARDINALS) {
                while (!conclaveEnded && isVotingPhase) {
                    wait();
                }
                return winner;
            }

            // Last voter - process results
            processVoteResults();

            // Check for winner
            if (checkForWinner()) {
                return winner;
            }

            // Prepare next round
            prepareNextRound();
            return -1;
        }

        /**
         * Processes the vote results and prints round information.
         */
        private void processVoteResults() {
            System.arraycopy(votes, 0, lastVoteResults, 0, NUM_CARDINALS);

            round++;
            System.out.println("\n=== Round " + round + " Results ===");

            int maxVotes = 0;
            int leader = -1;
            for (int i = 0; i < NUM_CARDINALS; i++) {
                if (lastVoteResults[i] > maxVotes) {
                    maxVotes = lastVoteResults[i];
                    leader = i;
                }
            }

            System.out.println("----------------------");
            System.out.printf("Leader: Cardinal %d with %d votes%n%n", leader, maxVotes);
        }

        /**
         * Checks if any candidate has reached the two-thirds majority.
         * 
         * @return true if a winner is found, false otherwise
         */
        private boolean checkForWinner() {
            for (int i = 0; i < NUM_CARDINALS; i++) {
                if (lastVoteResults[i] >= TWO_THIRDS) {
                    winner = i;
                    conclaveEnded = true;
                    printDivider();
                    System.out.println("ELECTION DECIDED IN ROUND " + round);
                    System.out.printf("Cardinal %d received %d votes (>= %d required)%n",
                            i, lastVoteResults[i], TWO_THIRDS);
                    printDivider();
                    notifyAll();
                    return true;
                }
            }
            return false;
        }

        /**
         * Prepares the conclave for the next round by resetting vote counters
         * and switching to discussion phase.
         */
        private void prepareNextRound() {
            Arrays.fill(votes, 0);
            votesCast = 0;
            isVotingPhase = false; // Switch to discussion
            notifyAll();
        }

        /**
         * Prints a visual divider for election announcements.
         */
        private void printDivider() {
            for (int i = 0; i < DIVIDER_LENGTH; i++) {
                System.out.print("=");
            }
            System.out.println();
        }

        /**
         * Notifies that a cardinal has completed their discussion.
         * When all cardinals finish, switches back to voting phase.
         */
        synchronized void noteDiscussionDone() {
            if (conclaveEnded || isVotingPhase)
                return;

            discussionsDone++;
            if (discussionsDone == NUM_CARDINALS) {
                discussionsDone = 0;
                isVotingPhase = true; // Switch back to voting
                System.out.println("\nDiscussion phase completed. Starting next voting round...");
                notifyAll();
            }
        }

        /**
         * Makes a cardinal wait until the voting phase begins.
         */
        synchronized void waitUntilVoting() throws InterruptedException {
            while (!conclaveEnded && !isVotingPhase) {
                wait();
            }
        }

        /**
         * Makes a cardinal wait until the discussion phase begins.
         */
        synchronized void waitUntilDiscussion() throws InterruptedException {
            while (isVotingPhase && !conclaveEnded) {
                wait();
            }
        }

        /**
         * Returns a copy of the last vote results.
         * 
         * @return Array of vote counts for each candidate
         */
        synchronized int[] getLastVoteResults() {
            return lastVoteResults.clone();
        }

        /**
         * Returns the influence level of a specific cardinal.
         * 
         * @param id Cardinal ID
         * @return Influence level (0-100)
         */
        synchronized int getInfluenceOf(int id) {
            return influence[id];
        }

        /**
         * Checks if the conclave has ended.
         * 
         * @return true if conclave is ended, false otherwise
         */
        synchronized boolean isConclaveEnded() {
            return conclaveEnded;
        }

        /**
         * Returns the ID of the winning cardinal.
         * 
         * @return Winner ID, or -1 if no winner yet
         */
        synchronized int getWinner() {
            return winner;
        }

        /**
         * Makes the main thread wait until the election is complete.
         */
        synchronized void waitForElection() throws InterruptedException {
            while (!conclaveEnded) {
                wait();
            }
        }
    }
}
