package micromouse;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractListModel;
import javax.swing.DefaultListModel;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.ListModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static micromouse.Direction.WE;
import static micromouse.Direction.NS;
import static micromouse.Direction.EW;
import static micromouse.Direction.SN;


class PathList extends JList{

    
    public void setGraph(Graph graph){
        
        DefaultListModel model = new DefaultListModel();
        for(Path path:graph){
            model.addElement(path);
        }
        
        setModel(model);
    }
}

class EdgeListener extends MouseAdapter {

    Browser browser;

    public EdgeListener(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void mousePressed(MouseEvent e) {

        Edge edge = browser.edgeAt(e.getPoint());
        if (edge != null) {
            browser.edgeClick(edge);
        }

    }

}

class NodeListener extends MouseAdapter {

    Browser browser;

    Node start, stop;

    public NodeListener(Browser browser) {
        this.browser = browser;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        stop = null;
        start = browser.nodeAt(e.getPoint());
        if (start != null) {
            browser.nodeClick(start);
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        stop = browser.nodeAt(e.getPoint());
        if (start != null && stop != null) {
            browser.wall(start, stop);
            start = null;
            stop = null;
        }
    }

}

public class Browser extends JPanel implements ChangeListener {

    public static int EDGE_SIZE = 30;

    public static int NODE_SIZE = 2;

    Maze maze;

    Mouse mouse;
    
    PathList pathList = new PathList();

    public void drawGraph(Graphics g) {
        if (mouse == null) {
            return;
        }

        Graph graph = mouse.graph;
        
        for (Path path : graph) {
            g.setColor(path.selected?Color.RED:Color.ORANGE);
            if (!path.isEmpty()) {
                Point start = new Point(path.get(0));
                Point p = new Point(start.x*EDGE_SIZE+EDGE_SIZE/2,start.y*EDGE_SIZE+EDGE_SIZE/2);
                for (Point m : path) {
                    Point pNext = new Point(m.x*EDGE_SIZE+EDGE_SIZE/2,m.y*EDGE_SIZE+EDGE_SIZE/2);
                    g.drawLine(p.x, p.y, pNext.x, pNext.y);
                    p = pNext;
                }
                g.fillOval(start.x*EDGE_SIZE+EDGE_SIZE/2-4, start.y*EDGE_SIZE+EDGE_SIZE/2-4, 8,8);
            }
        }
    }

    public Point roomPosition(Room room) {
        return new Point(room.col, room.row);
    }

    public Point roomCenter(Room room) {
        Point position = roomPosition(room);
        return new Point(position.x * EDGE_SIZE + EDGE_SIZE / 2, position.y * EDGE_SIZE + Browser.EDGE_SIZE / 2);
    }

    public Node nodeAt(Point p) {
        for (int col = 0; col <= maze.width; col++) {
            for (int row = 0; row <= maze.height; row++) {
                Rectangle bound = new Rectangle(col * EDGE_SIZE - 2, row * EDGE_SIZE - 2, 5, 5);
                if (bound.contains(p)) {
                    return maze.nodeAt(col, row);
                }
            }
        }
        return null;
    }

    public void wall(Node node1, Node node2) {
        maze.wall(node1, node2);
        repaint();
    }

    public Edge edgeAt(Point p) {
        int w;
        int h;
        int x;
        int y;
        Rectangle r;

        for (Edge edge : maze.edges) {
            int x1 = edge.node1.x * EDGE_SIZE;
            int y1 = edge.node1.y * EDGE_SIZE;
            int x2 = edge.node2.x * EDGE_SIZE;
            int y2 = edge.node2.y * EDGE_SIZE;

            // horizontal
            if (y1 == y2) {
                h = 5;
                w = Math.abs(x2 - x1);
                y = y1;
                x = Math.min(x1, x2);
                r = new Rectangle(x + h / 2, y - h / 2, w - h, h);
                if (r.contains(p)) {
                    return edge;
                }

            }
            // vertical
            if (x1 == x2) {
                h = Math.abs(y2 - y1);
                w = 5;
                x = x1;
                y = Math.min(y1, y2);
                r = new Rectangle(x, y + w / 2, w, h - w);
                if (r.contains(p)) {
                    return edge;
                }
            }
        }
        return null;
    }

    public void edgeClick(Edge edge) {
        maze.edges.remove(edge);
        repaint();
        System.out.println("edge click " + edge);
    }

    public void nodeClick(Node node) {
        System.out.println("node click " + node);
    }

    Rectangle roomRactangle(Room room) {
        Rectangle rect = new Rectangle();
        rect.width = EDGE_SIZE;
        rect.height = EDGE_SIZE;
        rect.x = room.col * EDGE_SIZE;
        rect.y = room.row * EDGE_SIZE;
        return rect;
    }

    private void drawStart(Graphics g) {
        Rectangle r = roomRactangle(maze.start);
        g.setColor(Color.YELLOW);
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    private void drawFinish(Graphics g) {
        g.setColor(Color.PINK);
        for (Room room : maze) {
            if (room.contain(maze.finish)) {
                Rectangle r = roomRactangle(room);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    @Override
    public void paint(Graphics g) {
        if (maze == null) {
            super.paint(g);
            return;
        }
        g.setColor(Color.WHITE);
        Rectangle rr = g.getClipBounds();
        g.fillRect(rr.x, rr.y, rr.width, rr.height);

        if (maze.isEmpty()) {
            return;
        }

        drawStart(g);
        drawFinish(g);

        // рамка
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, maze.width * EDGE_SIZE, maze.height * EDGE_SIZE);

        // рёбра
        g.setColor(Color.GRAY);
        for (Edge edge : maze.edges) {
            Node n1 = edge.node1;
            Node n2 = edge.node2;
            g.drawLine(n1.x * EDGE_SIZE, n1.y * EDGE_SIZE, n2.x * EDGE_SIZE, n2.y * EDGE_SIZE);
        }
        // узлы
        for (int x = 0; x <= maze.width; x++) {
            for (int y = 0; y <= maze.height; y++) {
                Node node = maze.nodeAt(x, y);
                Rectangle t = new Rectangle(node.x * EDGE_SIZE - NODE_SIZE, node.y * EDGE_SIZE - NODE_SIZE, NODE_SIZE * 2, NODE_SIZE * 2);
                g.setColor(Color.WHITE);
                g.fillRect(t.x, t.y, t.width, t.height);
                g.setColor(Color.GRAY);
                g.drawRect(t.x, t.y, t.width, t.height);
            }
        }

        drawGraph(g);

//        Mouse mouse = field.mouse;
        if (mouse != null) {
            // мышь
            
            Point p = mouse.position;
            Point center = new Point(p.x*EDGE_SIZE+EDGE_SIZE/2,p.y*EDGE_SIZE+EDGE_SIZE/2);
            
//            Room room = mouse.room;
            g.setColor(Color.red);
//            Point center = roomCenter(room);
            g.fillRect(center.x - 2, center.y - 2, 5, 5);
            switch (mouse.direction) {
                case SN:
                    g.drawLine(center.x, center.y, center.x, center.y + 10);
                    break;
                case WE:
                    g.drawLine(center.x, center.y, center.x - 10, center.y);
                    break;
                case EW:
                    g.drawLine(center.x, center.y, center.x + 10, center.y);
                    break;
                case NS:
                    g.drawLine(center.x, center.y, center.x, center.y - 10);
                    break;
            }
        }
    }

    public void showInFrame() {
        JFrame frame = new JFrame("MicroMouse v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(this);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    public void showMessage(String message) {
        JOptionPane.showMessageDialog(getParent(), message);
    }

    public void setMaze(Maze maze) {
        this.maze = maze;
        mouse = new Mouse(maze);
        maze.addChangeListener(this);
        setPreferredSize(new Dimension(maze.width * EDGE_SIZE, maze.height * EDGE_SIZE));
    }

    public Browser() {
        this(new Maze(16, 16));
    }

    public Browser(Maze maze) {
        addMouseListener(new NodeListener(this));
        addMouseListener(new EdgeListener(this));
        this.maze = maze;
        mouse = new Mouse(maze);
        setPreferredSize(new Dimension(maze.width * EDGE_SIZE+1, maze.height * EDGE_SIZE+1));
    }

    public static void main(String[] args) {
        new App().execute();
    }
}

