/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse;

import java.util.ArrayList;
import java.util.StringJoiner;

class Move {

    Path path;

    Direction direction;

    Integer count;

    public Move(Path path, Direction direction, Integer count) {
        this.path = path;
        this.direction = direction;
        this.count = count;
    }

    @Override
    public String toString() {
        return direction.name();
    }

}

class Path extends ArrayList<Move> {

    Graph graph;

    Room room;

    public Path(Graph grapth, Room room) {
        this.graph = grapth;
        this.room = room;
    }

    public void add(Direction direction) {
        super.add(new Move(this, direction, 1));
    }

    public String toString() {
        StringJoiner s = new StringJoiner("-", "{ col=" + room.col + " row=" + room.row + " (" + (isEmpty() ? "empty" : size()) + ")}", "\n");
        for (Move m : this) {
            s.add(m.toString());
        }
        return s.toString();
    }
}

/**
 *
 * @author viljinsky
 */
public class Graph extends ArrayList<Path> {

    private Path path;

    public Graph() {
    }

    public Graph(Room room) {
        path = new Path(this, room);
        add(path);
    }

    public void add(Room room) {
        path = new Path(this, room);
        super.add(path);
    }

    public void add(Direction direction) {
        path.add(direction);
    }
    
    public void add(Room room,Direction direction){
        Path tmp = new Path(this, room);
        tmp.add(direction);
        add(tmp);
    }

    public void print() {
        for (Path p : this) {
            System.out.print(p.toString());
        }
    }

}
