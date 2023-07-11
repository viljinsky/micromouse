package micromouse;

import java.awt.Point;
import java.util.ArrayList;
import java.util.StringJoiner;

class Path extends ArrayList<Point> {

    Graph graph;
    
    Point position;
    
    public Path(Graph graph,Point start) {
        this.graph = graph;
        position = new Point(start);
        add(position);
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
        add(position);
        
    }

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",","graph{","}");
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
