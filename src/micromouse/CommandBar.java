/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse;

import java.awt.FlowLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.StringJoiner;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

/**
 *
 * @author viljinsky
 */
public class CommandBar extends JPanel {

    Browser browser;

    public static final String RESET = "reset";
    public static final String STEP = "step";
    public static final String START = "start";
    public static final String STOP = "stop";
    boolean pause;

    private void doCommand(String command) {

        Mouse mouse = browser.mouse;
        try {
            switch (command) {
                case RESET:
                    mouse.reset();
                    browser.repaint();
                    break;

                case START:
                    new Thread() {
                        @Override
                        public void run() {
                            pause = false;
                            try {
                                while (mouse.step()) {

                                    browser.repaint();
                                    if (pause) {
                                        break;
                                    }
                                    long t = System.currentTimeMillis();
                                    while (System.currentTimeMillis() < t + 10) {
                                    }
                                }

                                mouse.graph.print();
                                browser.mouse.graph = mouse.graph;
                                browser.repaint();
                            } catch (Exception e) {
                                System.err.println(e.getMessage());
                            }
                        }
                    }.start();
                    break;
                case STOP:
                    pause = true;
                    break;
                case STEP:
                    if (mouse.step()) {
                        browser.mouse.graph = mouse.graph;
                        browser.repaint();
                    }
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

    public CommandBar(Browser browser) {
        super(new FlowLayout(FlowLayout.LEFT));
        this.browser = browser;
//        this.mouse = browser.mouse;
        for (String command : new String[]{START, STOP, STEP, RESET}) {
            if (command == null) {
                add(new JLabel(" "));
            } else {
                add(createButton(command));
            }
        }
    }

//    public void setMouse(Mouse mouse) {
//        this.mouse = mouse;
//    }
}
