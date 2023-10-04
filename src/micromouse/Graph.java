package micromouse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.StringJoiner;

class Node extends Point{

    public Node(int x, int y) {
        super(x,y);
    }

    public Node(Point position) {
        super(position);
    }
        
    @Override
    public String toString() {
        return "Node{" + "x=" + x + "; y=" + y + '}';
    }
}

class Edge {

    Node node1;

    Node node2;

    public Edge(Node node1, Node node2) {
        this.node1 = node1;
        this.node2 = node2;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Edge) {
            Edge e = (Edge) obj;

            return (e.node1.equals(node1) && e.node2.equals(node2))
                    || (e.node1.equals(node2) && e.node2.equals(node1));
        }
        return false;
    }

    @Override
    public String toString() {
        return "Edge{(" + node1.x + " " + node1.y + ");(" + node2.x + " " + node2.y + ")}";
    }

}


class Path extends ArrayList<Node> {

    Graph graph;
    
    Point position;
    
    boolean selected;
    
    public Path(Graph graph,Point start) {
        this.graph = graph;
        position = new Point(start);
        add(new Node(position));
    }

    public boolean add(Point position) {
        return super.add(new Node(position)); 
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
        add(new Node(position));
        
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",","path{","}");
        for(Point p:this){
            joiner.add(p.x+" "+p.y);
        }
        return joiner.toString();
    }
    
    
    
    public Point end(){
        return get(size()-1);
    }
    
    public Point start(){
        return get(0);
    }
}

/**
 *
 * @author viljinsky
 */
public class Graph extends ArrayList<Path> {

    private Path path;
    
    public void setSelected(Path path){
        for(Path t:this){
            t.selected = t.equals(path);
        }
    }

    public Graph() {
    }

    public void add(Direction direction) {
        path.add(direction);
    }
    
    public void print() {
        for (Path p : this) {
            System.out.println(p.toString());
        }
    }

    public boolean contains(Point p){
        for(Path t:this){
            if (t.contains(p)){
                return true;
            }
        }
        return false;
    }

    // поиск пути у котрого посленяя точка равна p
    public Path find(Point p){
        for(Path path:this){
            if (p.equals(path.end())){
                return path;
            }
        }
        return null;
    }
    
}
