package cz.mendelu.vui2.agents.greenfoot;


import greenfoot.World;


public class RobotWorld extends World {

    public static int CELL_SIZE = 24;
    public static byte[][] world;
    public static AbstractAgent agent;
    public static int timeToLive;
    public static int score;

    public static LabelActor simulationLabel;
    public static LabelActor scoreLabel;
    public static TraceActor traceActor;

    public RobotWorld() {
        super(world[0].length, world.length, CELL_SIZE);
        setPaintOrder(LabelActor.class, AgentActor.class, TraceActor.class, DockActor.class, DirtyActor.class, WallActor.class);

        // Test world external settings
        if (agent == null) {
            throw new IllegalStateException("Agent implementation is not set.");
        }
        if (timeToLive == 0) {
            throw new IllegalStateException("Time to simulation is not set.");
        }

        // Set agent information about simulation.
        agent.timeToSimulation = timeToLive;

        // Create info labels
        simulationLabel = new LabelActor("Simulation: ", timeToLive);
        addObject(simulationLabel, 2, 0);

        scoreLabel = new LabelActor("Score: ", 0);
        addObject(scoreLabel, 5, 0);

        // Create world map
        setBackground("images/world-background.png");
        for (int r = 0; r < world.length; r++) {
            for (int c = 0; c < world[r].length; c++) {
                if (world[r][c] == 'X') {
                    addObject(new WallActor(), c, r);
                }
                else if (world[r][c] == '_') {
                    addObject(new AgentActor(agent), c, r);
                    addObject(new DockActor(), c, r);

                    traceActor = new TraceActor(world[0].length, world.length, c, r);
                    addObject(traceActor, world[0].length / 2, world.length / 2);
                }
                else if (world[r][c] != '0') {
                    addObject(new DirtyActor(), c, r);
                }
            }
        }
    }
}