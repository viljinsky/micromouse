/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse;

import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
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
        Field field = browser.field;
        switch (command) {
            case CLEAR:
                field.edges.clear();
                field.change();
                break;
            case EXIT:
                System.exit(0);
            case SAVE:
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append(String.format("size = %d,%d\n", field.width, field.height));
                for (Edge edge : field.edges) {
                    stringBuilder.append(String.format("edge = %d,%d,%d,%d\n", edge.node1.x, edge.node1.y, edge.node2.x, edge.node2.y));
                }
                System.out.println("" + stringBuilder.toString());
                break;
            case NEW:
            case OPEN:
                browser.showMessage(command);
                break;
            default:
                browser.showMessage(command + " not found");
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
