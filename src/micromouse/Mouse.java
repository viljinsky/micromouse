package micromouse;

import java.awt.Point;
import java.util.ArrayList;

public class Mouse {

    Graph graph;
    Path path;
    Direction direction;
    Point position;
    Maze maze;
    private ArrayList<Point> stack = new ArrayList<>();


    public Mouse(Maze maze) {

        this.maze = maze;
        position = new Point(maze.start.col, maze.start.row);
        direction = Direction.WE;
        graph = new Graph();
        path = new Path(graph, position);
        graph.add(path);

    }

    public void reset() {
        position = new Point(maze.start.col, maze.start.row);
        direction = Direction.WE;
        graph.clear();
        path = new Path(graph, new Point(0, 0));
        graph.add(path);
        stack = new ArrayList<>();

    }
    
    public boolean step() {

        Point next;
        
        int count = 0;
        
        for (Direction d : Direction.values()) {
            next = maze.nextPoint(position, d);
            if (next != null && !path.contains(next)) {
                count++;
            }
        }
        
        if (count > 1) {
            stack.add(0,position);
            path = new Path(graph, position);
            graph.add(path);
        }

        next = maze.nextPoint(position, direction);
        if (next != null && !graph.contains(next)) {
            position = next;
            path.add(next);
            stateText = "step";
            return true;
        }
        
        for (Direction d : Direction.values()) {
            next = maze.nextPoint(position, d);
            if (next != null && !graph.contains(next)) {
                direction = d;
                stateText = "resolve";
                return true;
            }
            if(next!=null && !path.contains(next)){
                path.add(next);
            }
        }
        
        while (!stack.isEmpty()){
            position = stack.remove(0);
            for(Direction d:Direction.values()){
                next = maze.nextPoint(position, d);
                if (next !=null && !graph.contains(next)){
                    direction = d;
                    return true;
                }
            }            
           
        }
        
        System.out.println("path not found");
        stateText = "path not found";

        return false;
    }

    String stateText = "mouse";

    public String state() {
        maze.room(position.x, position.y);
        return stateText;
    }

}
