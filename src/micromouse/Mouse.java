package micromouse;

import java.util.ArrayList;

public class Mouse {

    Graph graph;

    Maze maze;

    Room room;

    public void change() {
        if (maze != null) {
            maze.change();
        }
    }

    ArrayList<Room> trace = new ArrayList<>();

    Direction direction = Direction.WE;

    public void reset() {
        graph = new Graph();
        room = maze.start;
        direction = Direction.WE;
        trace.clear();
        graph = new Graph();
        graph.add(room);
    }

    private boolean flag = true;

    private final long delay = 10;

    public boolean step() throws Exception {

        // if first stap
        if (graph == null) {
            graph = new Graph(room);
        }

        Room tmp;

        tmp = maze.next(room, direction);
        if (tmp != null){
            if (!trace.contains(tmp)){
                trace.add(room);
                room = tmp;

                if (maze.isFinish(room)) {
                    status = "goal";
                }

                graph.add(direction);
                return true;
            } else {
                graph.add(direction);
            }
        }

        for (Direction d : Direction.values()) {
            tmp = maze.next(room, d);
            if(tmp!=null){
                if (!trace.contains(tmp)){
                    direction = d;
                    return true;
                }
            }
        }

        if (trace.size()>1) {
            for (int n = trace.size() - 1; n >= 0; n--) {
                tmp = trace.get(n);
                for (Direction d : Direction.values()) {
                    Room next = maze.next(tmp, d);
                    if (next != null  && !next.equals(room)) {
                        if(!trace.contains(next)){ 
                            trace.add(room);
                            room = tmp;
                            direction = d;
                            graph.add(room);
                            return true;
                        } else {
                        }
                    }
                }
            }
        }
        
        if (!trace.isEmpty()) {
            trace.add(room);
        room = trace.get(0);
        direction = Direction.WE;
    }
        status = "no there room";
return false;

    }

    public void start() {

        graph = new Graph();
        graph.add(room);

        flag = true;
        new Thread() {
            @Override
public void run() {
                try {
                    while (flag) {
                        if (!step()) {
                            break;
                        }
                        change();
                        long t = System.currentTimeMillis();
                        do {
                        } while (t + delay > System.currentTimeMillis());
                    }
                    graph.print();
                    change();
                } catch (Exception e) {
                    System.out.println("" + e.getMessage());
                    flag = false;
                }
            }

        }.start();
    }

    public void pause() {
        flag = !flag;
        if (flag) {
            start();
        }
    }

    String status = "unknow";

    public String getStatus() {
        return status;
    }

    public Mouse(Maze maze) {
        this.maze = maze;
        this.room = maze.room(0, 0);
    }

}
