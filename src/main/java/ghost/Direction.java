package ghost;

public enum Direction {

    UP {
        public Direction opp() { return DOWN; }
        public int[] update(int x, int y, int delta) {
            return new int[] {x, y - delta};
        }
    },
    DOWN {
        public Direction opp() { return UP; }
        public int[] update(int x, int y, int delta) {
            return new int[] {x, y + delta};
        }
    },
    LEFT {
        public Direction opp() { return RIGHT; }
        public int[] update(int x, int y, int delta) {
            return new int[] {x - delta, y};
        }
    },
    RIGHT {
        public Direction opp() { return LEFT; }
        public int[] update(int x, int y, int delta) {
            return new int[] {x + delta, y};
        }
    },
    STOP {
        public Direction opp() { return STOP; }
        public int[] update(int x, int y, int delta) {
            return new int[] {x, y};
        }
    };


    public abstract Direction opp();


    public abstract int[] update(int x, int y, int delta);
}
