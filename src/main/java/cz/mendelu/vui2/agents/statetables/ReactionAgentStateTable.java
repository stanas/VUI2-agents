package cz.mendelu.vui2.agents.statetables;

import cz.mendelu.vui2.agents.Action;

public class ReactionAgentStateTable {
    public boolean wall;
    public boolean dirty;
    public boolean dock;
    public Action action;
    public String buffer;
    public String actionCode;

    public ReactionAgentStateTable(boolean wall, boolean dirty, boolean dock, Action action, String buffer, String actionCode){
        this.wall = wall;
        this.dirty = dirty;
        this.dock = dock;
        this.action = action;
        this.buffer = buffer;
        this.actionCode = actionCode;
    }
}
