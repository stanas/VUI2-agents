package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;

public class ReactionAgent extends AbstractAgent {

    /**
     * Attributes definition
     */
    private String movementHistory;
    String lastTurn;
    boolean cycle;
    Integer steps;
    Integer longerForward;

    /**
     * Constructor
     */
    public ReactionAgent() {
        movementHistory = "";
        lastTurn = "RIGHT";
        cycle = false;
        steps = 0;
        longerForward = 0;
    }

    @Override
    public Action doAction(boolean wall, boolean dirty, boolean dock) {

        steps++;

        printInputValues(wall, dirty, dock);
        printText("Last turn: " + lastTurn);

        if (checkMovementHistoryLength(1) && checkLastActionTaken(1, "D")) {
            printText("Reset");
            movementHistory = "";
            lastTurn = "RIGHT";
            cycle = false;
        }

        // DIRT FIND
        if (dirty) {
            // doActionOperations("C", "CLEAN");
            printText("Action: CLEAN");
            return Action.CLEAN;
        }/* else if (dock && checkMovementHistoryLength(3)) { // ON DOCK BUT NOT ENOUGH MOVEMENTS
            printText("Action: TURN OF, DOCK!");
            doActionOperations("D");
            return Action.TURN_OFF;
        } */else if (!wall && cycle) {
            printText("ACTION: CYCLE");
            doActionOperations("F");
            return Action.FORWARD;
        } else if (wall && cycle) {
            cycle = false;
        }

        // Pokud nelze dokocit FFF, podivej se na posledni otocku a otoc se opacne
   /*     if (checkMovementHistoryLength(4) && checkLastActionTaken(4, "FFLF"))  {
            printText("ACTION: RFFFL");
            doActionOperations("R", "RIGHT");

            return Action.TURN_RIGHT;
        }*/
        // -- MOVE ONE FORWARD
        if (!wall && checkMovementHistoryLength(5) && checkLastActionTaken(5, "LFFFR")) {
            printText("ACTION: RFFF");
            doActionOperations("F");
            return Action.FORWARD;
        }

        // -- MOVE ONE FORWARD
        if (!wall && checkMovementHistoryLength(5) && checkLastActionTaken(5, "RFFFL")) {
            printText("ACTION: RFFFL");
            doActionOperations("F");
            return Action.FORWARD;
        } else if (checkMovementHistoryLength(5) && checkLastActionTaken(5, "RFFFL")) {
            printText("ACTION: !RFFFL");
            doActionOperations("R");
            cycle = true;
            return Action.TURN_RIGHT;
        }

        // -- MOVE ONE FORWARD
        if (!wall && checkMovementHistoryLength(5) && checkLastActionTaken(5, "RRFFF")) {
            printText("ACTION: RFFF");
            doActionOperations("F");
            return Action.FORWARD;// pro fungování nahradit otočkou
        }

        // START UP RIGHT SEQUENCE
        // -- TURN RIGHT
        if (checkMovementHistoryLength(4) && checkLastActionTaken(4, "FFFL")) {
            printText("ACTION: FFFL");
            doActionOperations("R");
            return Action.TURN_RIGHT;
        }

        // -- MOVE ONE FORWARD
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "RFFF")) {
            printText("ACTION: RFFF");
            doActionOperations("F");
            return Action.FORWARD;
        }

        // -- TURN SECOND RIGHT UP
        if (checkMovementHistoryLength(4) && checkLastActionTaken(4, "FRFF")) {
            printText("ACTION: RFFF");
            doActionOperations("R");
            return Action.TURN_RIGHT;
        }

        // -- FORWARD UP
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "RFRF")) {
            printText("ACTION: RFFF");
            doActionOperations("F");
            return Action.FORWARD;
        }

        // START DOWN LEFT SEQUENCE FROM RIGHT UP SEQUENCE
        // -- TURN FIRST LEFT
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "FFFR")) {
            printText("ACTION: FFFR");
            doActionOperations("L");
            return Action.TURN_LEFT;
        }

        // -- MOVE ONE FORWARD
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "LFFF")) {
            printText("ACTION: LFFF");
            doActionOperations("F");
            return Action.FORWARD;
        }

        // -- TURN SECOND LEFT DOWN
        if (checkMovementHistoryLength(4) && checkLastActionTaken(4, "FLFF")) {
            printText("ACTION: FLFF");
            doActionOperations("L");
            return Action.TURN_LEFT;
        }

        // -- FORWARD DOWN
        if (!wall && checkMovementHistoryLength(4) && checkLastActionTaken(4, "LFLF")) {
            printText("ACTION: LFLF");
            doActionOperations("F");
            return Action.FORWARD;
        }

/*
        if (checkMovementHistoryLength(4) && checkLastActionTaken(3) == "FFFF"){
            doActionOperations("L", "LEFT");
            return Action.TURN_LEFT;
        }
*/

        if (checkMovementHistoryLength(3) && checkLastActionTaken(3, "RLR")) {
            printText("CYCLE");

            lastTurn = "RIGHT";
            doActionOperations("R");
            return Action.TURN_RIGHT;
        }

        if (checkMovementHistoryLength(3) && checkLastActionTaken(3, "LRL")) {
            printText("CYCLE");

            doActionOperations("L");
            lastTurn = "LEFT";
            return Action.TURN_LEFT;
        }

        if (wall) {
            printText("NORMAL WALL");
            switch (lastTurn) {
                case "LEFT": {
                    doActionOperations("R");
                    lastTurn = "RIGHT";
                    return Action.TURN_RIGHT;
                }
                case "RIGHT": {
                    lastTurn = "LEFT";
                    doActionOperations("L");
                    return Action.TURN_LEFT;
                }
                default: {
                    lastTurn = "LEFT";
                    doActionOperations("L");
                    return Action.TURN_LEFT;
                }
            }
        }

        /*if (!wall && checkMovementHistoryLength(3) && checkLastActionTaken(3, "FFF") && longerForward <= 2) {
            printText("Longer Forward: " + longerForward);
            longerForward++;
            doActionOperations("F");
            return Action.FORWARD;
        } else */if (checkMovementHistoryLength(3) && checkLastActionTaken(3, "FFF")) {
            //  if (longerForward != 0) longerForward = 0;
            // printText("Longer Forward: " + longerForward);
            lastTurn = "LEFT";
            doActionOperations("L");
            return Action.TURN_LEFT;
        }

        if (!wall) {
            longerForward = 0;
            printText("Action: CLASSIC FORWARD: " + longerForward);
            doActionOperations("F");
            return Action.FORWARD;
        }

        return null;
    }

    /**
     * Check movement history length.
     *
     * @param length Integer Length to check.
     * @return boolean
     */
    private boolean checkMovementHistoryLength(Integer length) {
        return movementHistory.length() >= length;
    }

    /**
     * @param lastPosition
     * @param actionsTaken
     * @return
     */
    private boolean checkLastActionTaken(Integer lastPosition, String actionsTaken) {
        return movementHistory.substring(0, lastPosition).equals(actionsTaken);
    }

    /**
     * Performing accompanying operations for each action.
     *
     * @param newAction Char Acronym for new action.
     */
    private void doActionOperations(String newAction) {
        addNewMovement(newAction);
        printText("HISTORY: " + movementHistory);
        printText("NEW: " + newAction);
    }

    /**
     * Add new movement to movement history.
     *
     * @param newAction Char Acronym for new action.
     */
    private void addNewMovement(String newAction) {
        String updatedMovementHistory = newAction;

        for (int i = 0; i < movementHistory.length(); i++) {
            char c = movementHistory.charAt(i);
            updatedMovementHistory = updatedMovementHistory + c;
        }

        movementHistory = updatedMovementHistory;
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
