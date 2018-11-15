package cz.mendelu.vui2.agents.statetables;

import cz.mendelu.vui2.agents.Action;

public class ReactionAgentStateTable {
    public boolean canMove;
    public boolean dirty;
    public boolean dock;
    public Action action;
    public String buffer;

    public ReactionAgentStateTable(boolean canMove, boolean dirty, boolean dock, Action action, String buffer){
        this.canMove = canMove;
        this.dirty = dirty;
        this.dock = dock;
        this.action = action;
        this.buffer = buffer;
    }
}
