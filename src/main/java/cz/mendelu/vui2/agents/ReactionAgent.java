package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;
import cz.mendelu.vui2.agents.statetables.ReactionAgentStateTable;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ReactionAgent extends AbstractAgent {

    /**
     * Attributes definition
     */
    private ArrayList stateTable;
    private String movementHistory;
    String lastTurn;
    boolean cycle;

    /**
     * Constructor
     */
    public ReactionAgent() {
        stateTable = defineStates();
        movementHistory = "";
        lastTurn = "RIGHT";
        cycle = false;
    }

    @Override
    public Action doAction(boolean wall, boolean dirty, boolean dock) {
        printInputValues(wall, dirty, dock);
     //   printAction("History:" + movementHistory);
        printAction("Last turn: " + lastTurn);

        if (movementHistory.length() > 1 && movementHistory.substring(0, 1).equals("D")) {
            printAction("Reset");
            movementHistory = "";
            lastTurn = "RIGHT";
            cycle = false;
        }

        // DIRT FIND
        if (dirty) {
            // doActionOperations("C", "CLEAN");
            printAction("Action: CLEAN");
            return Action.CLEAN;
        }

        // ON DOCK BUT NOT ENOUGH MOVEMENTS
        if (dock && movementHistory.length() > 3) {
            printAction("Action: TURN OF, DOCK!");
            doActionOperations("D", "TURN OF");
            return Action.TURN_OFF;
        }

        if(!wall && cycle){
            printAction("ACTION:: CYCLE");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        } else if (wall && cycle){
            cycle = false;
        }

        // Pokud nelze dokocit FFF, podivej se na posledni otocku a otoc se opacne
   /*     if (movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("FFLF"))  {
            printAction("ACTION:: RFFFL");
            doActionOperations("R", "RIGHT");

            return Action.TURN_RIGHT;
        }*/
        // -- MOVE ONE FORWARD
        if (!wall && movementHistory.length() >= 5 && movementHistory.substring(0, 5).equals("LFFFR")) {
            printAction("ACTION:: RFFF");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        }

        // -- MOVE ONE FORWARD
        if (!wall && movementHistory.length() >= 5 && movementHistory.substring(0, 5).equals("RFFFL")) {
            printAction("ACTION:: RFFFL");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        } else  if (movementHistory.length() >= 5 && movementHistory.substring(0, 5).equals("RFFFL"))  {
            printAction("ACTION:: !RFFFL");
            doActionOperations("R", "RIGHT");
            cycle = true;
            return Action.TURN_RIGHT;
        }

        // -- MOVE ONE FORWARD
        if (!wall && movementHistory.length() >= 5 && movementHistory.substring(0, 5).equals("RRFFF")) {
            printAction("ACTION:: RFFF");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;// pro fungování nahradit otočkou
        }

        // START UP RIGHT SEQUENCE
        // -- TURN RIGHT
        if (movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("FFFL")) {
            printAction("ACTION:: FFFL");
            doActionOperations("R", "RIGHT");
            return Action.TURN_RIGHT;
        }

        // -- MOVE ONE FORWARD
        if (!wall && movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("RFFF")) {
            printAction("ACTION:: RFFF");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        }

        // -- TURN SECOND RIGHT UP
        if (movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("FRFF")) {
            printAction("ACTION:: RFFF");
            doActionOperations("R", "RIGHT");
            return Action.TURN_RIGHT;
        }

        // -- FORWARD UP
        if (!wall && movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("RFRF")) {
            printAction("ACTION:: RFFF");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        }

        // START DOWN LEFT SEQUENCE FROM RIGHT UP SEQUENCE
        // -- TURN FIRST LEFT
        if (!wall && movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("FFFR")) {
            printAction("ACTION:: FFFR");
            doActionOperations("L", "LEFT");
            return Action.TURN_LEFT;
        }

        // -- MOVE ONE FORWARD
        if (!wall && movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("LFFF")) {
            printAction("ACTION:: LFFF");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        }

        // -- TURN SECOND LEFT DOWN
        if (movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("FLFF")) {
            printAction("ACTION:: FLFF");
            doActionOperations("L", "LEFT");
            return Action.TURN_LEFT;
        }

        // -- FORWARD DOWN
        if (!wall && movementHistory.length() >= 4 && movementHistory.substring(0, 4).equals("LFLF")) {
            printAction("ACTION:: LFLF");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        }



/*
        if (movementHistory.length() >= 4 && movementHistory.substring(0, 3) == "FFFF"){
            doActionOperations("L", "LEFT");
            return Action.TURN_LEFT;
        }*/
        //  if(movementHistory.length() > 3) printAction(movementHistory.substring(0,3));

        if (movementHistory.length() > 3 && movementHistory.substring(0, 3).equals("RLR")) {
            printAction("CYCLE");

            lastTurn = "RIGHT";
            doActionOperations("R", "RIGHT");
            return Action.TURN_RIGHT;
        }

        if (movementHistory.length() > 3 && movementHistory.substring(0, 3).equals("LRL")) {
            printAction("CYCLE");

            doActionOperations("L", "LEFT");
            lastTurn = "LEFT";
            return Action.TURN_LEFT;
        }

        if (wall) {
            printAction("NORMAL WALL");
            switch (lastTurn) {
                case "LEFT": {
                    doActionOperations("R", "RIGHT");
                    lastTurn = "RIGHT";
                    return Action.TURN_RIGHT;
                }
                case "RIGHT": {
                    lastTurn = "LEFT";
                    doActionOperations("L", "LEFT");
                    return Action.TURN_LEFT;
                }
                default: {
                    lastTurn = "LEFT";
                    doActionOperations("L", "LEFT");
                    return Action.TURN_LEFT;
                }
            }
        }

        if (!wall && movementHistory.length() >= 3 && movementHistory.substring(0, 3).equals("FFF")) {
            printAction("xxF");

            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        }else if (movementHistory.length() >= 3 && movementHistory.substring(0, 3).equals("FFF")) {
            printAction("3xF");
            lastTurn = "LEFT";
            doActionOperations("L", "LEFT");
            return Action.TURN_LEFT;
        }

        if (!wall) {
            printAction("Action: CLASSIC FORWARD ");
            doActionOperations("F", "FORWARD");
            return Action.FORWARD;
        }




       /* if(wall){
            doActionOperations("L", "TURN_RIGHT");
            return Action.TURN_LEFT;
        } else if(wall && !lastTurn){
            lastTurn = true;
            movementHistory = addNewMovement(movementHistory, "R");
            printAction("R");
            return Action.TURN_RIGHT;
        }*/
/*
        switch (movementHistory) {
            case "F": {
                movementHistory = addNewMovement(movementHistory, "L");
                return Action.TURN_LEFT;
            }
            case "FF": {
                movementHistory = addNewMovement(movementHistory, "L");
                return Action.TURN_LEFT;
            }
            case "LF": {
                if (wall) {
                    movementHistory = addNewMovement(movementHistory, "R");
                    return Action.TURN_RIGHT;
                } else {
                    movementHistory = addNewMovement(movementHistory, "F");
                    return Action.FORWARD;
                }
            }
            case "FFF": {
                movementHistory = addNewMovement(movementHistory, "L");
                return Action.TURN_LEFT;
            }
            case "LFF": {
                if (wall) {
                    movementHistory = addNewMovement(movementHistory, "L");
                    return Action.TURN_LEFT;
                } else {
                    movementHistory = addNewMovement(movementHistory, "F");
                    return Action.FORWARD;
                }
            }
            case "FLF": {
                movementHistory = addNewMovement(movementHistory, "L");
                return Action.TURN_LEFT;
            }
            case "LFL": {
                if (wall) {
                    movementHistory = addNewMovement(movementHistory, "R");
                    return Action.TURN_RIGHT;
                } else {
                    movementHistory = addNewMovement(movementHistory, "F");
                    return Action.FORWARD;
                }
            }

        }
*/

     /*   Iterator iterator = stateTable.iterator();
        while (iterator.hasNext()) {
            ReactionAgentStateTable state = (ReactionAgentStateTable) iterator.next();
            if (state.wall == wall && state.dirty == dirty && state.dock == dock) {
                System.out.println(state.action);
                if (movementHistory.length() == 3) {
                    movementHistory = addNewMovement(movementHistory, state.actionCode);
                } else {
                    movementHistory = state.actionCode + movementHistory;
                }

                System.out.println("Movement: " + movementHistory + " - " + movementHistory.length());

                return state.action;

            }

        }
*/

        return null;
    }

    private void doActionOperations(String newAction, String message) {
        movementHistory = addNewMovement(movementHistory, newAction);
        printAction("HISTORY: " + movementHistory);
        printAction(message);
    }

    /**
     * Define basic states
     *
     * @return stateTable ArrayList
     */
    private ArrayList<ReactionAgentStateTable> defineStates() {

        ArrayList<ReactionAgentStateTable> stateTable = new ArrayList<>();

        stateTable.add(new ReactionAgentStateTable(false, false, false, Action.FORWARD, "", "F"));
        stateTable.add(new ReactionAgentStateTable(false, false, true, Action.TURN_RIGHT, "", "R"));
        stateTable.add(new ReactionAgentStateTable(false, true, false, Action.CLEAN, "", "C"));
        stateTable.add(new ReactionAgentStateTable(false, true, true, Action.CLEAN, "", "C"));
        stateTable.add(new ReactionAgentStateTable(true, false, false, Action.TURN_RIGHT, "", "R"));
        stateTable.add(new ReactionAgentStateTable(true, false, true, Action.TURN_LEFT, "", "L"));
        stateTable.add(new ReactionAgentStateTable(true, true, false, Action.CLEAN, "", "C"));
        stateTable.add(new ReactionAgentStateTable(true, true, true, Action.CLEAN, "", "C"));

        return stateTable;
    }

    private Map<String, Action> defineAction() {
        Map<String, Action> actionHashMap = new HashMap<String, Action>();
        actionHashMap.put("F", Action.TURN_LEFT);
        actionHashMap.put("FF", Action.TURN_LEFT);
        actionHashMap.put("FFF", Action.TURN_LEFT);
        actionHashMap.put("FF", Action.TURN_LEFT);
        actionHashMap.put("FFF", Action.TURN_LEFT);
        actionHashMap.put("FFF", Action.TURN_LEFT);
        actionHashMap.put("FFF", Action.TURN_LEFT);
        actionHashMap.put("FFF", Action.TURN_LEFT);
        actionHashMap.put("FFF", Action.TURN_LEFT);


        return actionHashMap;
    }

    /**
     * @param movementHistory
     * @param newAction
     * @return
     */
    private String addNewMovement(String movementHistory, String newAction) {
     /*   printAction("F");
        switch (newAction){
            case "F" : printAction("FORWARD"); break;
            case "L" : printAction("TURN LEFT"); break;
            case "R" : printAction("TURN RIGHT"); break;
            case "C" : printAction("CLEAN"); break;
            case "D" : printAction("DOCK"); break;
        }*/

        // if (movementHistory.length() < 3) return newAction + movementHistory;

        String updatedMovementHistory = newAction;

        for (int i = 0; i < movementHistory.length(); i++) {
            char c = movementHistory.charAt(i);
            updatedMovementHistory = updatedMovementHistory + c;
        }

        return updatedMovementHistory;
    }

    /**
     * Print actual input values.
     *
     * @param wall  boolean
     * @param dirty boolean
     * @param dock  boolean
     */
    private void printInputValues(boolean wall, boolean dirty, boolean dock) {
        System.out.println("------------------------------------------------");
        System.out.println("Wall: " + wall + "-- Dirty: " + dirty + "-- Dock: " + dock);
    }

    private void printAction(String action) {
        System.out.println(action);
    }

}
