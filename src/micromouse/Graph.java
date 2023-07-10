/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.StringJoiner;

class Move extends Point{

    Path path;

    public Move(Path path) {
        this.path = path;       
    }

    public Move(Path path,Point position) {
        super(position);
        this.path = path;
    }

    @Override
    public String toString() {
        return x+" "+y;
    }
    
    
    

}

class Path extends ArrayList<Move> {

    Graph graph;
    Point position;
    
    

    public Path(Graph graph,Point start) {
        this.graph = graph;
        this.position = start;
        add(new Move(this,start));
    }
    
    public void add(Direction d){
        switch(d){
            case WE:
                position.x+=1;break;
            case NS:
                position.y+=1;break;
            case EW:
                position.x-=1;break;
            case SN:
                position.y-=1;break;
        }
        Move move = new Move(this,position);
        add(move);
        
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",","graph{","}\n");
        for(Move m:this){
            joiner.add(m.toString());
        }
        
        return joiner.toString();
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
        path = new Path(this, room.position());
        add(path);
    }

    public void add(Room room) {
        path = new Path(this, room.position());
        super.add(path);
    }

    public void add(Direction direction) {
        path.add(direction);
    }
    
    public void add(Room room,Direction direction){
        Path tmp = new Path(this, room.position());
        tmp.add(direction);
        add(tmp);
    }

    public void print() {
        for (Path p : this) {
            System.out.print(p.toString());
        }
    }

}
