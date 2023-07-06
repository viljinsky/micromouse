package micromouse;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import static micromouse.Direction.WE;
import static micromouse.Direction.NS;
import static micromouse.Direction.EW;
import static micromouse.Direction.SN;

class CommandBar extends JPanel {

    Mouse mouse;

    public static final String RESET = "reset";
    public static final String STEP = "step";
    public static final String START = "start";
    public static final String PAUSE = "pause";

    private void doCommand(String command) {
        try {
            switch (command) {
                case RESET:
                    mouse.reset();
                    break;
                case START:
                    mouse.start();
                    break;
                case PAUSE:
                    mouse.pause();
                    break;
                case STEP:
                    if (!mouse.step()){
                        for(Object l:mouse.graph){
                            Path p = (Path)l;
                            System.out.println(p);
                        }
                        JOptionPane.showMessageDialog(getParent(), mouse.getStatus());
                        
                    };
                    break;
                default:
                    throw new Exception("command \"" + command + "\" unsupported yet");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(getParent(), e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JButton createButton(String command) {
        JButton button = new JButton(new AbstractAction(command) {
            @Override
            public void actionPerformed(ActionEvent e) {
                doCommand(e.getActionCommand());
            }
        });
        return button;
    }

    public CommandBar() {
        super(new FlowLayout(FlowLayout.LEFT));
        for (String command : new String[]{START, PAUSE, STEP, RESET}) {
            if (command == null) {
                add(new JLabel(" "));
            } else {
                add(createButton(command));
            }
        }
    }

    public void setMouse(Mouse mouse) {
        this.mouse = mouse;
    }

}

class StatusBar extends JPanel {

    JLabel label = new JLabel("StatusBar");

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(label);
    }

    public void setStatusText(String statusText) {
        label.setText(statusText);
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
        if(edge!=null){
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
        if (start!=null){
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

    public static int EDGE_SIZE = 40;

    public static int NODE_SIZE = 2;

    Maze maze;

    Mouse mouse;
    
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
    
    public void wall(Node node1,Node node2){
        maze.wall(node1, node2);
        repaint();
    }
        
    public Edge edgeAt(Point p){
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
                r = new Rectangle(x+h/2, y-h/2, w-h, h);
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
                r = new Rectangle(x, y+w/2, w, h-w);
                if (r.contains(p)) {
                    return edge;
                }
            }
        }
        return null;
    }

    public void edgeClick(Edge edge){
        maze.edges.remove(edge);
        repaint();
        System.out.println("edge click "+edge);
    }
    
    public void nodeClick(Node node){
        System.out.println("node click "+node);
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

//        Mouse mouse = field.mouse;
        if (mouse != null) {
            // мышь
            Room room = mouse.room;
            g.setColor(Color.red);
            Point center = room.center();
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

            // путь
            g.setColor(Color.BLUE);
            for (Room r : mouse.trace) {
                Point p = r.center();
                p.x -= 2;
                p.y -= 2;
                g.drawLine(p.x, p.y + 5, p.x + 5, p.y);
                g.drawLine(p.x, p.y, p.x + 5, p.y + 5);

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

    public Browser(Maze maze) {
        this.maze = maze;        
        mouse = new Mouse(maze);
        setPreferredSize(new Dimension(maze.width * EDGE_SIZE, maze.height * EDGE_SIZE));
        addMouseListener(new NodeListener(this));
        addMouseListener(new EdgeListener(this));
    }

    public static void main(String[] args) {
        new App().execute();
    }
}

/**
 *
 * @author viljinsky
 */
class App implements ChangeListener {

    Browser browser;
    
    StatusBar statusBar = new StatusBar();
    
    CommandBar commandBar = new CommandBar();

    @Override
    public void stateChanged(ChangeEvent e) {

        Mouse mouse = browser.mouse;
        if (mouse != null) {
            statusBar.setStatusText(mouse.room.toString());
        }
    }

    public void execute() {

        Maze maze = new Maze();

//        Field field = new Field(16,16);
//        field.wall(0,1,3,1);
//        field.wall(4,1,16,1);
//        field.wall(15,1,15,15);
//        field.wall(15,15,1,15);
//        field.wall(1,15,1,1);
        browser = new Browser(maze);
        FileManager fileManager = new FileManager(browser);
        commandBar.setMouse(browser.mouse);

        maze.addChangeListener(browser);
        maze.addChangeListener(this);

        JFrame frame = new JFrame("MicroMouse v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = frame.getContentPane();
        c.add(new JScrollPane(browser));
        c.add(commandBar, BorderLayout.PAGE_START);
        c.add(statusBar, BorderLayout.PAGE_END);

        statusBar.setStatusText("maze" + maze.width+" X "+maze.height);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileManager.menu());
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
