package micromouse;

import java.awt.Point;
import java.util.ArrayList;

public class Mouse {

    Graph graph;
    Path path;
    Direction direction;
    Point position;
    Maze sensor;
    private ArrayList<Point> stack = new ArrayList<>();


    public Mouse(Maze sensor) {

        this.sensor = sensor;
        position = new Point(0,0);
        direction = Direction.WE;
        graph = new Graph();
        path = new Path(graph, position);
        graph.add(path);

    }

    public void reset() {
        graph.clear();
        position = new Point(0,0);
        direction = Direction.WE;
        path = new Path(graph, position);
        graph.add(path);
        stack = new ArrayList<>();

    }
    
    public boolean step() {

        Point next;
        
        int count = 0;
        
        for (Direction d : Direction.values()) {
            next = sensor.nextPoint(position, d);
            if (next != null && !path.contains(next)) {
                count++;
            }
        }
        
        if (count > 1) {
            stack.add(0,position);
            path = new Path(graph, position);
            graph.add(path);
        }

        next = sensor.nextPoint(position, direction);
        if (next != null && !graph.contains(next)) {
            position = next;
            path.add(next);
            stateText = "step";
            return true;
        }
        
        for (Direction d : Direction.values()) {
            next = sensor.nextPoint(position, d);
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
                next = sensor.nextPoint(position, d);
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
        sensor.room(position.x, position.y);
        return stateText;
    }

}
