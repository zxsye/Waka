package ghost;

public enum GhostMode {
    CHASE { public GhostMode opp() { return SCATTER; }},
    SCATTER { public GhostMode opp() { return CHASE; }},
    FRIGHTENED { public GhostMode opp() { return FRIGHTENED; }};
    public abstract GhostMode opp();
}
