package micromouse;

import java.awt.Point;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

enum Direction {
    
    WE, NS, EW, SN;
    
    public Direction left(){
        switch (this) {
            case WE:
                return SN;
            case NS:
                return WE;
            case EW:
                return  NS;
            case SN:
                return EW;
            default:
                return this;
        }
    }
    
    public Direction right(){
        switch (this) {
            case WE:
                return NS;                
            case NS:
                return EW;
            case EW:
                return SN;
            case SN:
                return WE;
             default:
                return this;
        }
        
    }
    
};

class Node {

    int x;
    
    int y;

    public Node(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return "Node{" + "x=" + x + "; y=" + y + '}';
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Node) {
            Node n = (Node) obj;
            return x == n.x && y == n.y;
        }
        return false;
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

class Room {

    int col;
    int row;
    public Edge upper;
    public Edge right;
    public Edge left;
    public Edge down;

    public boolean contain(Node node) {
        return upper.node1.equals(node) || upper.node2.equals(node) || down.node1.equals(node) || down.node2.equals(node);
    }

    public Room(Field field, int col, int row) {
        this.col = col;
        this.row = row;
        upper = new Edge(field.nodeAt(col, row), field.nodeAt(col + 1, row));
        right = new Edge(field.nodeAt(col + 1, row), field.nodeAt(col + 1, row + 1));
        left = new Edge(field.nodeAt(col, row), field.nodeAt(col, row + 1));
        down = new Edge(field.nodeAt(col, row + 1), field.nodeAt(col + 1, row + 1));
    }

    public Point center() {
        return new Point(col * Browser.EDGE_SIZE + Browser.EDGE_SIZE / 2, row * Browser.EDGE_SIZE + Browser.EDGE_SIZE / 2);
    }

    @Override
    public String toString() {
        return "Room{col=" + col + "; row=" + row + "}";
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (obj instanceof Room) {
            Room r = (Room) obj;
            return center().equals(r.center());
        }
        return false;
    }

}

/**
 *
 * @author viljinsky
 */
class Field extends ArrayList<Room> {

    int width;

    int height;

    ArrayList<Node> nodes = new ArrayList<>();
    
    ArrayList<Edge> edges = new ArrayList<>();

    public Room start;

    public Node finish;
    
    public boolean isFinish(Room room){
        return room.contain(finish);
    }

    public int roomCount() {
        return width * height;
    }

    private ArrayList<ChangeListener> listeners = new ArrayList<>();

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    public Field() {
        defaultPlan();
        start = room(0, 0);
        finish = nodeAt(width / 2, width / 2);
    }

    public Field(int width, int height) {
        this.width = width;
        this.height = height;
        for (int x = 0; x <= width; x++) {
            for (int y = 0; y <= height; y++) {
                nodes.add(new Node(x, y));
            }
        }
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                add(new Room(this, col, row));
            }
        }
        start = room(0, 0);
        finish = nodeAt(width / 2, width / 2);
    }

    private void defaultPlan() {
        width = 0;
        height = 0;
        StringBuilder sb = new StringBuilder();
        try (
                InputStream str = getClass().getResourceAsStream("/micromouse/plan"); InputStreamReader reader = new InputStreamReader(str, "utf-8");) {
            char[] buf = new char[1000];
            int count;

            while ((count = reader.read(buf)) > 0) {
                sb.append(buf, 0, count);
            }

        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
        String[] lines = sb.toString().split("\n");
        for (String s : lines) {
            if (s.trim().isEmpty()) {
                continue;
            }
            String[] values = s.split("=");
            switch (values[0].trim()) {
                case "width":
                    width = Integer.valueOf(values[1].trim());
                    break;
                case "height":
                    height = Integer.valueOf(values[1].trim());
            }
        }

        for (int i = 0; i <= width; i++) {
            for (int j = 0; j <= height; j++) {
                nodes.add(new Node(i, j));
            }
        }

        for (String s : lines) {
            if (s.trim().isEmpty()) {
                continue;
            }
            String[] values = s.split("=");
            if (values[0].trim().equals("edge")) {
                String[] p = values[1].split(",");
                addEdge(Integer.valueOf(p[0].trim()), Integer.valueOf(p[1].trim()), Integer.valueOf(p[2].trim()), Integer.valueOf(p[3].trim()));
            }
        }

        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                add(new Room(this, col, row));
            }
        }
    }

    public Node nodeAt(int x, int y) {

        for (Node node : nodes) {
            if (node.x == x && node.y == y) {
                return node;
            }
        }
        return null;
    }

    public void addEdge(int x1, int y1, int x2, int y2) {
        Node n1 = nodeAt(x1, y1);
        Node n2 = nodeAt(x2, y2);
        if (n1 == null || n2 == null) {
            throw new RuntimeException("incorrect args ");
        }
        edges.add(new Edge(n1, n2));
    }

    public void change() {
        for (ChangeListener listener : listeners) {
            listener.stateChanged(new ChangeEvent(this));
        }
    }

    public Room room(int col, int row) {
        if (col < 0 || row < 0) {
            return null;
        }
        if (col >= width || row >= width) {
            return null;
        }
        return new Room(this, col, row);
    }

    public Room nextRoom(Room room, Direction direction) {
        if (isOpen(room, direction)) {
            switch (direction) {
                case EW:
                    return room(room.col - 1, room.row);
                case WE:
                    return room(room.col + 1, room.row);
                case SN:
                    return room(room.col, room.row - 1);
                case NS:
                    return room(room.col, room.row + 1);
            }
        }
        return null;
    }

    public boolean isOpen(Room room, Direction direction) {
        switch (direction) {
            case SN:
                return !edges.contains(room.upper);
            case WE:
                return !edges.contains(room.right);
            case EW:
                return !edges.contains(room.left);
            case NS:
                return !edges.contains(room.down);
        }
        return true;
    }

}

public class Mouse {

    Field field;

    Room room;

    ArrayList<Room> trace = new ArrayList<>();

    Direction direction = Direction.WE;

    private int position = -1;

    public void reset() {
        room = field.start;
        direction = Direction.WE;
        trace.clear();
        position = -1;
        field.change();
    }

    private boolean flag = true;
    
    private int count = 0;
    
    private final int maxCount = 4;

    private final long delay = 10;

    public void left() {
        direction = direction.left();
        field.change();
    }

    public void right() {
        direction = direction.right();
        field.change();
    }

    public void forvard() throws Exception{

        Room tmp = field.nextRoom(room, direction);
        if (tmp != null) {
            if (!trace.contains(room)) {
                trace.add(room);
                position = trace.indexOf(room);
            }
            this.room = tmp;
            field.change();
            if (field.isFinish(tmp)){
                throw new Exception("!!!! GOOL !!!!");
            }
        }

    }

    public void start() {

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

    private Room find(Room room) {
        Room tmp;
        for (Direction d : Direction.values()) {
            tmp = field.nextRoom(room, d);
            if (tmp != null
                    && field.isOpen(tmp, d)
                    && !trace.contains(tmp)) {
                return tmp;
            }
        }
        return null;
    }

    public void step() throws Exception{

        Room tmp;
        tmp = field.nextRoom(room, direction.left());
        if (tmp != null && !trace.contains(tmp)) {
            left();
            forvard();
            count = 0;
            return;
        }

        tmp = field.nextRoom(room, direction);
        if (tmp == null) {
            left();
        } else {
            if (trace.contains(tmp)) {
                left();
            } else {
                forvard();
                count = 0;
            }
        }
        if (++count > maxCount) {
            System.err.println("count overlow");
            trace.add(room);
            for (int n = trace.size() - 1; n >= 0; n--) {
                Room f = find(trace.get(n));
                if (f != null) {
                    this.room = trace.get(n);
                    break;
                }
            }
        }

    }

    public void back() {
        if (position >= 0) {
            if (!trace.contains(room)) {
                trace.add(room);
            }
            Room tmp = trace.get(position--);
            room = tmp;
            field.change();

        }
    }

    public Mouse(Field field) {
        this.field = field;
        this.room = field.room(0, 0);
//        field.mouse = this;
    }

}
