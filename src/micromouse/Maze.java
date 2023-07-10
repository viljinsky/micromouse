package micromouse;

import java.awt.Point;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

enum Direction {

    WE, NS, EW, SN;

    public Direction left() {
        switch (this) {
            case WE:
                return SN;
            case SN:
                return EW;
            case EW:
                return NS;
            case NS:
                return WE;
            default:
                return this;
        }
    }

    public Direction right() {
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
    
    public Direction back(){
        switch (this){
            case WE:
                return EW;
            case EW:
                return WE;                
            case SN:
                return NS;
            case NS:
                return SN;
            default:
                return null;
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
    
    public Point position(){
        return new Point(col, row);
    }

    public Room(Maze maze, int col, int row) {
        this.col = col;
        this.row = row;
        upper = new Edge(maze.nodeAt(col, row), maze.nodeAt(col + 1, row));
        right = new Edge(maze.nodeAt(col + 1, row), maze.nodeAt(col + 1, row + 1));
        left = new Edge(maze.nodeAt(col, row), maze.nodeAt(col, row + 1));
        down = new Edge(maze.nodeAt(col, row + 1), maze.nodeAt(col + 1, row + 1));
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
            return col == r.col && row==r.row ;
        }
        return false;
    }

}

/**
 *
 * @author viljinsky
 */
public class Maze extends ArrayList<Room> {

    int width = -1;
    int height = -1;
    ArrayList<Node> nodes;
    ArrayList<Edge> edges;
    public Room start;
    public Node finish;

    public void read(InputStream str) throws Exception {
        width = -1;
        height = -1;
        StringBuilder sb = new StringBuilder();
        try (InputStreamReader reader = new InputStreamReader(str, "utf-8")) {
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
            int n = s.indexOf("\\");
            s = s.substring(0, n == -1 ? s.length() : n);
            if (s.trim().isEmpty()) {
                continue;
            }
            String[] values = s.split("=");
            if (values[0].trim().equals("size")) {
                String[] s1 = values[1].split(",");
                width = Integer.valueOf(s1[0].trim());
                height = Integer.valueOf(s1[1].trim());
            }
        }
        if (width == -1 || height == -1) {
            width = 8;
            height = 8;
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
                edge(Integer.valueOf(p[0].trim()), Integer.valueOf(p[1].trim()), Integer.valueOf(p[2].trim()), Integer.valueOf(p[3].trim()));
            }
        }
        for (int col = 0; col < width; col++) {
            for (int row = 0; row < height; row++) {
                add(new Room(this, col, row));
            }
        }

    }

    public void write(OutputStream out) throws Exception {

        try (OutputStreamWriter writer = new OutputStreamWriter(out, "utf-8");) {
            writer.write(String.format("size = %d,%d\n", width, height));
            for (Edge edge : edges) {
                writer.write(String.format("edge = %d,%d,%d,%d\n", edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y));
            }
        }
    }

    public boolean isFinish(Room room) {
        return room.contain(finish);
    }

    private ArrayList<ChangeListener> listeners = new ArrayList<>();

    public void addChangeListener(ChangeListener listener) {
        listeners.add(listener);
    }

    File file = new File("maze.ini");

    public Node nodeAt(int x, int y) {
        for (Node node : nodes) {
            if (node.x == x && node.y == y) {
                return node;
            }
        }
        return null;
    }

    public void edge(int x1, int y1, int x2, int y2) {
        Node n1 = nodeAt(x1, y1);
        Node n2 = nodeAt(x2, y2);
        if (n1 == null || n2 == null) {
            throw new RuntimeException("incorrect args ");
        }
        Edge e = new Edge(n1, n2);
        if (!edges.contains(e)) {
            edges.add(new Edge(n1, n2));
        }
    }

    public void wall(Node n1, Node n2) {
        wall(n1.x, n1.y, n2.x, n2.y);
    }

    public void wall(int x1, int y1, int x2, int y2) {

        if (x1 > x2) {
            int tmp = x2;
            x2 = x1;
            x1 = tmp;
        }
        if (y1 > y2) {
            int tmp = y2;
            y2 = y1;
            y1 = tmp;
        }

        for (int i = x1; i < x2; i++) {
            edge(i, y1, i + 1, y1);
        }

        for (int i = y1; i < y2; i++) {
            edge(x2, i, x2, i + 1);
        }
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

    public Room next(Room room, Direction direction) {
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

    public Maze() {
        this(16, 16);
        InputStream in;
        try {
            if (file.exists()) {
                in = new FileInputStream(file);
            } else {
                in = getClass().getResourceAsStream("/micromouse/plan");
            }
            try {
                read(in);
            } finally {
                in.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Maze(int width, int height) {
        this.width = width;
        this.height = height;
        nodes = new ArrayList<>();
        edges=new ArrayList<>();
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

}
