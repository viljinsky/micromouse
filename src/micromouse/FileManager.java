package micromouse;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.swing.AbstractAction;
import javax.swing.JMenu;

/**
 *
 * @author viljinsky
 */
class FileManager {

    public static final String NEW = "new";
    public static final String CLEAR = "clear";
    public static final String OPEN = "open";
    public static final String SAVE = "save";
    public static final String EXIT = "exit";

    Browser browser;

    public FileManager(Browser browser) {
        this.browser = browser;
    }

    void doCommand(String command) {
        Maze maze = browser.maze;
        try {
            switch (command) {
                case CLEAR:
                    maze.edges.clear();
                    maze.change();
                    break;
                case EXIT:
                    System.exit(0);

                case SAVE:
                
                try (OutputStream out = new FileOutputStream(new File("maze.ini"));) {
                    maze.write(out);
                    System.out.println("OK");
                }

                break;
                case NEW:
                    browser.setMaze(new Maze(16,16));
                    browser.mouse.reset();
                    browser.repaint();
                    browser.revalidate();
                    break;
                    
                case OPEN:
                    browser.showMessage(command);
                    break;
                default:
                    browser.showMessage(command + " not found");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    JMenu menu() {
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_ALT);
        for (String command : new String[]{NEW, OPEN, SAVE, EXIT, CLEAR}) {
            menu.add(new AbstractAction(command) {
                @Override
                public void actionPerformed(ActionEvent e) {
                    doCommand(e.getActionCommand());
                }
            });
        }
        return menu;
    }

}
