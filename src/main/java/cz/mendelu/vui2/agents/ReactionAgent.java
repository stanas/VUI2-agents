package cz.mendelu.vui2.agents;

import cz.mendelu.vui2.agents.greenfoot.AbstractAgent;
import cz.mendelu.vui2.agents.statetables.ReactionAgentStateTable;

import java.util.ArrayList;
import java.util.Iterator;

public class ReactionAgent extends AbstractAgent {

    @Override
    public Action doAction(boolean canMove, boolean dirty, boolean dock) {

        System.out.println("------------------------------------------------");
        System.out.println(canMove);
        System.out.println(dirty);
        System.out.println(dock);
        ArrayList<ReactionAgentStateTable> stateTable = new ArrayList<>();
        stateTable.add(new ReactionAgentStateTable(false, false, false, Action.FORWARD, ""));
        stateTable.add(new ReactionAgentStateTable(false, false, true, Action.TURN_OFF, ""));
        stateTable.add(new ReactionAgentStateTable(false, true, false, Action.CLEAN, ""));
        stateTable.add(new ReactionAgentStateTable(false, true, true, Action.TURN_OFF, ""));
        stateTable.add(new ReactionAgentStateTable(true, false, false, Action.TURN_RIGHT, ""));
        stateTable.add(new ReactionAgentStateTable(true, false, true, Action.TURN_OFF, ""));
        stateTable.add(new ReactionAgentStateTable(true, true, false, Action.CLEAN,""));
        stateTable.add(new ReactionAgentStateTable(true, true, true, Action.CLEAN,""));

        Iterator iterator = stateTable.iterator();

        while (iterator.hasNext()) {
            ReactionAgentStateTable state = (ReactionAgentStateTable) iterator.next();
            if(state.canMove == canMove && state.dirty == dirty && state.dock == dock){
                System.out.println(state.action);
                return state.action;
            }

        }
        return null;
    }
}
