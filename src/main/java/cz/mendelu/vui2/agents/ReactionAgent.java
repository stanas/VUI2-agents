package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;

public class ReactionAgent extends AbstractAgent {

    /**
     * Attributes definition
     */
    /**
     * Stack of all taken actions
     */
    private String movementHistory;

    /**
     * Last turn - LEFT/RIGHT
     */
    private String lastTurn;

    /**
     * Keeps the loop information
     */
    private boolean cycle;

    /**
     * Number of taken actions
     */
    private Integer steps;

    /**
     * Maximum of steps forward
     */
    private Integer longerForward;

    /**
     * Actual number of steps forward
     */
    private Integer actualLongerForward;

    /**
     * Constructor
     */
    public ReactionAgent() {
        this.movementHistory = "";
        this.lastTurn = "RIGHT";

        this.cycle = false;

        this.steps = 0;
        this.longerForward = 0;
        this.actualLongerForward = 0;
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

        this.checkReset();

        this.steps++;
        setLongerForward();

        if (dirty) return Action.CLEAN;

        if (!wall && this.cycle) return doActionOperations("F");

        if (wall && this.cycle) cycle = false;

        // Longer movement forward
        if (!wall && checkMovementHistoryLength(3) && checkLastActionTaken(3, "FFF") && this.actualLongerForward < this.longerForward) {
            this.actualLongerForward++;
            return doActionOperations("F");
        }

        // Cycle detection
        if (checkMovementHistoryLength(3) && checkLastActionTaken(3, "RLF")) {
            return doActionOperations("R");
        }

        // Cycle detection
        if (checkMovementHistoryLength(3) && checkLastActionTaken(3, "LRL")) {
            this.lastTurn = "LEFT";
            return doActionOperations("L");
        }

        // Right sequence - one forward
        if (!wall && checkMovementHistoryLength(5) && checkLastActionTaken(5, "LFFFR")) {
            doActionOperations("F");
            return Action.FORWARD;
        }

        // Right sequence - one forward or turn right and mark new cycle
        if (!wall && checkMovementHistoryLength(5) && checkLastActionTaken(5, "RFFFL")) {
            return doActionOperations("F");
        } else if (checkMovementHistoryLength(5) && checkLastActionTaken(5, "RFFFL")) {
            this.cycle = true;
            return doActionOperations("R");
        }

        // Right sequence - one forward
        if (!wall && checkMovementHistoryLength(5) && checkLastActionTaken(5, "RRFFF")) {
            return doActionOperations("F");
        }

        // Right sequence - turn right
        if (checkMovementHistoryLength(4) && checkLastActionTaken(4, "FFFL")) {
            return doActionOperations("R");
        }

        // Right sequence - one forward
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "RFFF")) {
            return doActionOperations("F");
        } else if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "RFFF")) {
            return doActionOperations("L");
        }

        // Right sequence - second turn right
        if (checkMovementHistoryLength(4) && checkLastActionTaken(4, "FRFF")) {
            return doActionOperations("R");
        }

        // Right sequence - forward up
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "RFRF")) {
            return doActionOperations("F");
        }

        // Left sequence - down left
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "FFFR")) {
            return doActionOperations("L");
        }

        // Left sequence - one forward
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "LFFF")) {
            return doActionOperations("F");
        }

        // Left sequence - second turn left
        if (checkMovementHistoryLength(4) && checkLastActionTaken(4, "FLFF")) {
            return doActionOperations("L");
        }

        // Left sequence - forward down
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "LFLF")) {
            return doActionOperations("F");
        }

        // Cycle
        if (checkMovementHistoryLength(3) && checkLastActionTaken(3, "RLR")) {
            this.lastTurn = "RIGHT";
            return doActionOperations("R");
        }

        // Normal wall detection
        if (wall) {

            switch (this.lastTurn) {
                case "LEFT": {
                    this.lastTurn = "RIGHT";
                    return doActionOperations("R");
                }
                case "RIGHT": {
                    this.lastTurn = "LEFT";
                    return doActionOperations("L");
                }
                default: {
                    this.lastTurn = "LEFT";
                    return doActionOperations("L");
                }
            }
        }

        // Start up sequence for basic 3 forward - else's branch for cycle detection
        if (this.movementHistory.length() == 3 && checkLastActionTaken(3, "FFF")) {
            this.lastTurn = "LEFT";
            return doActionOperations("L");

        } else if (this.lastTurn.equals("LEFT") && checkMovementHistoryLength(3) && checkLastActionTaken(3, "FFF")) {
            this.lastTurn = "RIGHT";
            return doActionOperations("R");

        } else if (this.lastTurn.equals("RIGHT") && checkMovementHistoryLength(3) && checkLastActionTaken(3, "FFF")) {
            this.lastTurn = "LEFT";
            return doActionOperations("L");
        }

        if (!wall) {
            this.actualLongerForward = 0;
            return doActionOperations("F");
        }

        return Action.TURN_OFF;
    }

    /**
     * Check if the attributes reset is needed
     */
    public void checkReset() {
        if (checkMovementHistoryLength(1) && checkLastActionTaken(1, "D")) {
            this.movementHistory = "";
            this.lastTurn = "RIGHT";
            this.cycle = false;

            this.steps = 0;
            this.longerForward = 0;
            this.actualLongerForward = 0;
        }
    }

    /**
     * Sets longer intervals for forward action, breaks basic movement structure from beginning of process.
     */
    private void setLongerForward() {
        if (this.steps > 400) this.longerForward = 3;
        else if (this.steps > 300) this.longerForward = 2;
        else if (this.steps > 200) this.longerForward = 1;
    }


    /**
     * Check movement history length.
     *
     * @param length Integer Length to check.
     * @return boolean
     */
    private boolean checkMovementHistoryLength(Integer length) {
        return this.movementHistory.length() >= length;
    }

    /**
     * @param lastPosition Integer Last/max position in string to be processed.
     * @param actionsTaken String Previous actions to be found.
     * @return boolean Actions founded - true
     */
    private boolean checkLastActionTaken(Integer lastPosition, String actionsTaken) {
        return this.movementHistory.substring(0, lastPosition).equals(actionsTaken);
    }

    /**
     * Performing accompanying operations for each action.
     *
     * @param newAction Char Acronym for new action.
     * @return Action Action to process.
     */
    private Action doActionOperations(String newAction) {
        addNewMovement(newAction);
        printText("HISTORY: " + movementHistory);
        switch (newAction) {
            case "F":
                return Action.FORWARD;
            case "R":
                return Action.TURN_RIGHT;
            case "L":
                return Action.TURN_LEFT;
            case "D":
                return Action.TURN_OFF;
            case "C":
                return Action.CLEAN;
            default:
                return Action.TURN_LEFT;
        }
    }

    /**
     * Add new movement to movement history.
     *
     * @param newAction Char Acronym for new action.
     */
    private void addNewMovement(String newAction) {
        String updatedMovementHistory = newAction;

        for (int i = 0; i < this.movementHistory.length(); i++) {
            char c = this.movementHistory.charAt(i);
            updatedMovementHistory = updatedMovementHistory + c;
        }

        this.movementHistory = updatedMovementHistory;
    }

    public String getMovementHistory() {
        return this.movementHistory;
    }

    public Integer getSteps() {
        return this.steps;
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
