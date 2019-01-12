package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;

import java.util.ArrayList;

public class GoalAgent extends AbstractAgent {

    /**
     * Attributes for all phases
     */
    private String movementHistory;

    /**
     * Attributes for forward phase
     */
    private String lastTurn;
    private boolean cycle;
    private Integer steps;
    private Integer longerForward;
    private Integer actualLongerForward;

    /**
     * Attributes for backward phase
     */
    private boolean backToDock;
    private Integer maxTurningStep;
    private Integer firstTurning;

    /**
     * Attributes for creating world map and direct determination
     */
    private ArrayList<WorldPosition> world;
    private Integer x;
    private Integer y;
    private Direction direction;

    /**
     * Constructor
     */
    public GoalAgent() {
        this.movementHistory = "";

        this.lastTurn = "RIGHT";
        this.cycle = false;
        this.steps = 0;
        this.longerForward = 0;
        this.actualLongerForward = 0;

        this.backToDock = false;
        this.maxTurningStep = 0;
        this.firstTurning = 0;

        this.x = 0;
        this.y = 0;
        this.direction = Direction.NORTH;
        this.world = new ArrayList<WorldPosition>();
        this.world.add(new WorldPosition(0, 0, "DOCK"));
    }

    /**
     * Main method for choosing action, determining which phase is actual, if forward, or backward.
     *
     * @param wall  boolean Wall check
     * @param dirty boolean Dirty check
     * @param dock  boolean Dock check
     * @return Action Chosen action
     */
    @Override
    public Action doAction(boolean wall, boolean dirty, boolean dock) {
        //For testing purposes.
        this.checkReset();

        this.checkPathToDock();

        // Initial max turning point.
        if (this.maxTurningStep == 0) this.initMaxTurningStep();

        // Check if it's time to turn around.
        if (this.maxTurningStep >= (timeToSimulation - this.steps)) this.backToDock = true;

        // Back to dock - else continue forward
        if (this.backToDock) {
            if (dock) return Action.TURN_OFF;
            String action = this.movementHistory.substring(0, 1);
            return this.doActionBackward(action);
        } else {
            return this.doActionForward(wall, dirty, dock);
        }
    }

    /**
     * Main method for forward phase.
     *
     * @param wall  boolean Wall check
     * @param dirty boolean Dirty check
     * @param dock  boolean Dock check
     * @return Action Chosen action
     */
    private Action doActionForward(boolean wall, boolean dirty, boolean dock) {

        this.steps++;
        setLongerForward();

        // Record wall.
        if (wall) {
            this.makeWallMarkOnMap("WALL");
        }

        // If it's dirty, it's only what matters!
        if (dirty) {
            this.makeMarkOnMap(this.x, this.y, "CLEAN");
            return Action.CLEAN;
        }

        // While in cycle, and there's no wall, always go forward!
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
     * Main method for backward phase.
     *
     * @param action String Last taken action.
     * @return Action.
     */
    private Action doActionBackward(String action) {

        this.steps++;

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
     * Checks if the dock passes and recalculate maximum turning point.
     */
    private void checkPathToDock() {
        if (this.x == 0 && this.y == 0) {

            this.maxTurningStep = (timeToSimulation - steps) / 2 + 1;
        }

//        printText("TIME TO SIMUL: " + timeToSimulation);
//        printText("TURNING POINT: " + maxTurningStep);
//        printText("REST         : " + (timeToSimulation - steps));
//        printText("STEPS        : " + steps);
    }

    /**
     * Method recalculates the current position and saves it to the map.
     *
     * @param desc String Position description.
     */
    private void makeForwardMarkOnMap(String desc) {
        switch (direction) {
            case EAST:
                this.x += 1;
                makeMarkOnMap(this.x, this.y, desc);
                break;
            case WEST:
                this.x -= 1;
                makeMarkOnMap(this.x, this.y, desc);
                break;
            case NORTH:
                this.y += 1;
                makeMarkOnMap(this.x, this.y, desc);
                break;
            case SOUTH:
                this.y -= 1;
                makeMarkOnMap(this.x, this.y, desc);
                break;
        }
    }

    /**
     * Method saves wall position to the map.
     *
     * @param desc String Position description.
     */
    private void makeWallMarkOnMap(String desc) {
        switch (direction) {
            case EAST:
                makeMarkOnMap(this.x + 1, this.y, desc);
                break;
            case WEST:
                makeMarkOnMap(this.x - 1, this.y, desc);
                break;
            case NORTH:
                makeMarkOnMap(this.x, this.y + 1, desc);
                break;
            case SOUTH:
                makeMarkOnMap(this.x, this.y - 1, desc);
                break;
        }
    }

    /**
     * Method that performs the positioning of the position itself on the map.
     *
     * @param x    Integer Position X.
     * @param y    Integer Position Y.
     * @param desc String Position description.
     */
    private void makeMarkOnMap(Integer x, Integer y, String desc) {

        if (!this.checkPositionExistence(desc))
            world.add(new WorldPosition(x, y, desc));
    }

    /**
     * Direction update.
     *
     * @param action Action Last action taken.
     */
    private void setDirection(Action action) {
        if (action == Action.TURN_LEFT) {
            switch (this.direction) {
                case EAST:
                    this.direction = Direction.NORTH;
                    break;
                case WEST:
                    this.direction = Direction.SOUTH;
                    break;
                case NORTH:
                    this.direction = Direction.WEST;
                    break;
                case SOUTH:
                    this.direction = Direction.EAST;
                    break;
            }
        } else
            switch (this.direction) {
                case EAST:
                    this.direction = Direction.SOUTH;
                    break;
                case WEST:
                    this.direction = Direction.NORTH;
                    break;
                case NORTH:
                    this.direction = Direction.EAST;
                    break;
                case SOUTH:
                    this.direction = Direction.WEST;
                    break;
            }
    }

    /**
     * Method checks if the current position has already record with specific description, if so, doesn't save new position.
     *
     * @param desc String Position description.
     * @return boolean.
     */
    private boolean checkPositionExistence(String desc) {
        boolean positionFound = false;

        for (WorldPosition position : this.world)
            if (position.getY().equals(this.y) && position.getX().equals(this.x) && position.getDesc().equals(desc))
                positionFound = true;

        return positionFound;
    }

    /**
     * Control dump method for current world.
     */
    private void printWorldInformation() {
        for (WorldPosition position : world) {
            printText("X: " + position.getX() + " Y: " + position.getY() + " Desc: " + position.getDesc());
        }
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
        this.addNewMovement(newAction);

        switch (newAction) {
            case "F":
                this.makeForwardMarkOnMap("EMPTY");
                return Action.FORWARD;
            case "R":
                setDirection(Action.TURN_RIGHT);
                return Action.TURN_RIGHT;
            case "L":
                setDirection(Action.TURN_LEFT);
                return Action.TURN_LEFT;
            case "D":
                this.makeMarkOnMap(this.x, this.y, "DOCK");
                return Action.TURN_OFF;
            case "C":
                this.makeMarkOnMap(this.x, this.y, "CLEAN");
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

    private void initMaxTurningStep() {
        this.maxTurningStep = (timeToSimulation / 2) - 3;
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


/**
 *  Class storing each of world positions.
 */
class WorldPosition {

    /**
     * World position attributes.
     */
    private Integer x;
    private Integer y;
    private String desc;

    /**
     * World position constructor
     *
     * @param x Integer Position X
     * @param y Integer Position Y
     * @param desc String Position description.
     */
    public WorldPosition(Integer x, Integer y, String desc) {
        this.x = x;
        this.y = y;
        this.desc = desc;
    }

    /**
     * Return X.
     *
     * @return Integer X.
     */
    public Integer getX() {
        return this.x;
    }

    /**
     * Return Y.
     *
     * @return Integer Y.
     */
    public Integer getY() {
        return this.y;
    }

    /**
     * Return description.
     *
     * @return String Description.
     */
    public String getDesc() {
        return this.desc;
    }
}
