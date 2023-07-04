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
    public static final String FORVARD = "forward";
    public static final String LEFT = "left";
    public static final String RIGHT = "rignt";
    public static final String STEP = "step";
    public static final String BACK = "back";
    public static final String START = "start";
    public static final String PAUSE = "pause";

    private void doCommand(String command) {
        try {
            switch (command) {
                case START:
                    mouse.start();
                    break;
                case PAUSE:
                    mouse.pause();
                    break;
                case STEP:
                    mouse.step();
                    break;
                case RESET:
                    mouse.reset();
                    break;
                case FORVARD:
                    mouse.forvard();
                    break;
                case LEFT:
                    mouse.left();
                    break;
                case BACK:
                    mouse.back();
                    break;
                case RIGHT:
                    mouse.right();
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
        for (String command : new String[]{START, PAUSE, STEP, RESET, null, FORVARD, BACK, LEFT, RIGHT}) {
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

class NodeListener extends MouseAdapter {

    Field field;

    Node start, stop;

    Node nodeAt(Point p) {

        for (int col = 0; col <= field.width; col++) {
            for (int row = 0; row <= field.height; row++) {
                Rectangle bound = new Rectangle(col * Browser.EDGE_SIZE - 2, row * Browser.EDGE_SIZE - 2, 5, 5);
                if (bound.contains(p)) {
                    return field.nodeAt(col, row);
                }
            }
        }
        return null;
    }

    public NodeListener(Browser browser) {
        browser.addMouseListener(this);
        this.field = browser.field;

    }

    @Override
    public void mousePressed(MouseEvent e) {
        stop = null;
        start = nodeAt(e.getPoint());
        if (start != null) {
            System.out.println("start");
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        stop = nodeAt(e.getPoint());
        if (start != null && stop != null) {
            System.out.println("stop");
            field.addEdge(start.x, start.y, stop.x, stop.y);
            field.change();
        }
    }

}

public class Browser extends JPanel implements ChangeListener {

    public static int EDGE_SIZE = 40;

    public static int NODE_SIZE = 2;

    Field field;

    Mouse mouse;

    Rectangle roomRactangle(Room room) {
        Rectangle rect = new Rectangle();
        rect.width = EDGE_SIZE;
        rect.height = EDGE_SIZE;
        rect.x = room.col * EDGE_SIZE;
        rect.y = room.row * EDGE_SIZE;
        return rect;
    }

    private void drawStart(Graphics g) {
        Rectangle r = roomRactangle(field.start);
        g.setColor(Color.YELLOW);
        g.fillRect(r.x, r.y, r.width, r.height);
    }

    private void drawFinish(Graphics g) {
        g.setColor(Color.PINK);
        for (Room room : field) {
            if (room.contain(field.finish)) {
                Rectangle r = roomRactangle(room);
                g.fillRect(r.x, r.y, r.width, r.height);
            }
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        repaint();
    }

    public Browser(Field field) {
        this.field = field;
        mouse = new Mouse(field);
        addMouseListener(new NodeListener(this));
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension((field.width) * EDGE_SIZE, (field.height) * EDGE_SIZE);
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(Color.WHITE);
        Rectangle rr = g.getClipBounds();
        g.fillRect(rr.x, rr.y, rr.width, rr.height);

        if (field.isEmpty()) {
            return;
        }

        drawStart(g);
        drawFinish(g);

        // рамка
        g.setColor(Color.GRAY);
        g.drawRect(0, 0, field.width * EDGE_SIZE, field.height * EDGE_SIZE);

        // рёбра
        g.setColor(Color.GRAY);
        for (Edge edge : field.edges) {
            Node n1 = edge.node1;
            Node n2 = edge.node2;
            g.drawLine(n1.x * EDGE_SIZE, n1.y * EDGE_SIZE, n2.x * EDGE_SIZE, n2.y * EDGE_SIZE);
        }
        // узлы
        for (int x = 0; x <= field.width; x++) {
            for (int y = 0; y <= field.height; y++) {
                Node node = field.nodeAt(x, y);
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

        Field f = (Field) e.getSource();
        Mouse mouse = browser.mouse;
        if (mouse != null) {
            statusBar.setStatusText(mouse.room.toString());
        }
    }

    public void execute() {
        Field field = new Field();
        browser = new Browser(field);
        commandBar.setMouse(browser.mouse);

        field.addChangeListener(browser);
        field.addChangeListener(this);

        JFrame frame = new JFrame("MicroMouse v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = frame.getContentPane();
        c.add(new JScrollPane(browser));
        c.add(commandBar, BorderLayout.PAGE_START);
        c.add(statusBar, BorderLayout.PAGE_END);

        statusBar.setStatusText("field count " + field.roomCount());
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

}
