package micromouse;

import java.util.ArrayList;
import java.util.StringJoiner;

class Move {

    Direction direction;

    Integer count;

    public Move(Direction direction, Integer count) {
        this.direction = direction;
        this.count = count;
    }

    @Override
    public String toString() {
        return direction.name();
    }

}

class Path extends ArrayList<Move> {

    Room room;

    public Path(Room room) {
        this.room = room;
    }

    public String toString() {
        StringJoiner s = new StringJoiner("-", room.toString(), "\n");
        for (Move m : this) {
            s.add(m.toString());
        }
        return s.toString();
    }
}

public class Mouse {

    Path path = new Path(null);

    ArrayList graph;

    Maze maze;

    Room room;

    ArrayList<Room> trace = new ArrayList<>();

    Direction direction = Direction.WE;

    public void reset() {
        room = maze.start;
        direction = Direction.WE;
        trace.clear();
        maze.change();
    }

    private boolean flag = true;

    private final long delay = 10;

    public void resolve() {
        for (Object obj : graph) {
            Path p = (Path) obj;
            System.out.println(p.toString());

        }
    }

    public void step() throws Exception {

        Room tmp;

        tmp = maze.next(room, direction);
        if (tmp != null) {
            if (!trace.contains(tmp)) {
                trace.add(room);
                room = tmp;
                maze.change();

                if (maze.isFinish(room)) {
                    System.out.println("GOALL");
                }
                return;
            }
        }

        for (Direction d : Direction.values()) {
            tmp = maze.next(room, d);
            if (tmp != null && !trace.contains(tmp)) {
                direction = d;
                maze.change();
                return;
            }
        }

        if (!trace.isEmpty()) {
            for (int n = trace.size() - 1; n >= 0; n--) {
                tmp = trace.get(n);
                for (Direction d : Direction.values()) {
                    Room t1 = maze.next(tmp, d);
                    if (t1 != null && !trace.contains(t1) && !t1.equals(room)) {
                        trace.add(room);
                        room = tmp;
                        direction = d;
                        maze.change();
                        return;
                    }
                }
            }
        }
        if(!trace.isEmpty()){
            trace.add(room);
            room = trace.get(0);
            maze.change();
        }
        throw new RuntimeException("no there room");

    }

    public void start() {

        path = new Path(room);
        graph = new ArrayList();

        flag = true;
        new Thread() {
            @Override
            public void run() {
                try {
                    while (flag) {
                        step();
                        long t = System.currentTimeMillis();
                        do {
                        } while (t + delay > System.currentTimeMillis());
                    }
                } catch (Exception e) {
                    System.out.println("" + e.getMessage());
                    flag = false;
                }
            }

        }.start();
    }

    public void pause() {
        flag = false;
    }

    public Mouse(Maze maze) {
        this.maze = maze;
        this.room = maze.room(0, 0);
    }

}
