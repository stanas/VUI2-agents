package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;

public class WorldAgent extends AbstractAgent {

    /**
     * Stack of all taken actions.
     */
    private String movementHistory;

    /**
     * Mark if movementHistory was locally saved.
     */
    private boolean movementHistorySaved;

    /**
     * Return back to dock
     */
    private boolean backToDock;

    /**
     * Maximum point when vacuum cleaner need to turn around.
     */
    private Integer maxTurningStep;

    /**
     * Reaction agent.
     */
    private ReactionAgent reactionAgent;

    /**
     * Mark for first turning, which is slightly different from the rest.
     */
    private Integer firstTurning;

    /**
     * Constructor
     */
    public WorldAgent() {
        this.reactionAgent = new ReactionAgent();

        this.movementHistory = "";

        this.movementHistorySaved = false;
        this.backToDock = false;

        this.maxTurningStep = 0;
        this.firstTurning = 0;
    }

    /**
     * Main method choosing action to be taken.
     *
     * @param wall  boolean Wall check
     * @param dirty boolean Dirty check
     * @param dock  boolean Dock check
     * @return Action Chosen action
     */
    @Override
    public Action doAction(boolean wall, boolean dirty, boolean dock) {

        if (this.maxTurningStep == 0) this.initMaxTurningStep();
        reactionAgent.checkReset();

        // Check if it's time to turn around.
        if (this.maxTurningStep <= reactionAgent.getSteps()) this.backToDock = true;

        // Check if movementHistory is locally saved.
        if (!this.movementHistorySaved && this.backToDock) {
            this.movementHistory = reactionAgent.getMovementHistory();
            this.movementHistorySaved = true;
        }

        // Back to dock - else continue
        if (this.backToDock) {
            // When all actions taken, turn off.
            if (this.movementHistory.length() == 0) return Action.TURN_OFF;

            String action = this.movementHistory.substring(0, 1);

            return this.takeAction(action);

        } else {
            return reactionAgent.doAction(wall, dirty, dock);
        }
    }

    /**
     * Init Max Turning Step.
     */
    private void initMaxTurningStep() {
        this.maxTurningStep = (timeToSimulation / 2) - 3;
    }

    /**
     * Backtracking to dock.
     *
     * @param action String First action in stack.
     * @return Action Action
     */
    private Action takeAction(String action) {

        // Beginning turning actions
        if (action.equals("F") && firstTurning < 2) {
            firstTurning++;
            return Action.TURN_LEFT;

        } else if (action.equals("F") && firstTurning == 2) {
            this.movementHistory = this.movementHistory.substring(1);
            return Action.FORWARD;

        } else if (action.equals("R") && firstTurning < 1) {
            firstTurning += 3;
            this.movementHistory = this.movementHistory.substring(1);
            return Action.TURN_RIGHT;

        } else if (action.equals("L") && firstTurning < 1) {
            firstTurning += 3;
            this.movementHistory = this.movementHistory.substring(1);
            return Action.TURN_LEFT;
        }

        this.movementHistory = this.movementHistory.substring(1);

        // Classic actions
        switch (action) {
            case "F":
                return Action.FORWARD;
            case "R":
                return Action.TURN_LEFT;
            case "L":
                return Action.TURN_RIGHT;
            default:
                return Action.TURN_OFF;
        }
    }

    /**
     * Print actual input values.
     *
     * @param wall  boolean Wall in front of the vacuum cleaner.
     * @param dirty boolean Dirt on the floor.
     * @param dock  boolean Vacuum cleaner is in dock.
     */
    private void printInputValues(boolean wall, boolean dirty, boolean dock) {
        System.out.println("------------------------------------------------");
        System.out.println("Wall: " + wall + "-- Dirty: " + dirty + "-- Dock: " + dock);
    }

    /**
     * Print control text.
     *
     * @param text String Text to be printed out.
     */
    private void printText(String text) {
        System.out.println(text);
    }
}
