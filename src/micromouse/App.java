/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

/**
 *
 * @author viljinsky
 */
class App implements ChangeListener {

    Maze maze = new Maze(16, 16);
    Browser browser = new Browser(maze);
    FileManager fileManager = new FileManager(browser);
    CommandBar commandBar = new CommandBar(browser);
    StatusBar statusBar = new StatusBar();
  //  PathList pathList = new PathList();

    @Override
    public void stateChanged(ChangeEvent e) {
        Mouse mouse = browser.mouse;
        if (mouse != null) {
            statusBar.setStatusText(mouse.state());
        }
    }

    public void execute() {
        maze.addChangeListener(browser);
        maze.addChangeListener(this);
        JFrame frame = new JFrame("MicroMouse v1.0");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        Container c = frame.getContentPane();
        
        JPanel wrapper = new JPanel(new FlowLayout());
        wrapper.add(browser);
        
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,new JScrollPane(wrapper),new JScrollPane(browser.pathList));
        splitPane.setResizeWeight(1.0);
//        splitPane.setDividerLocation(100);
        c.add(splitPane);
        c.add(commandBar, BorderLayout.PAGE_START);
        c.add(statusBar, BorderLayout.PAGE_END);
        statusBar.setStatusText("maze" + maze.width + " X " + maze.height);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileManager.menu());
        frame.setJMenuBar(menuBar);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        
        browser.pathList.addListSelectionListener(e->{
            if(!e.getValueIsAdjusting()){
            JList list = (JList)e.getSource();
            Object obj = list.getSelectedValue();
            if (obj!=null){
                Path path = (Path)obj;
                browser.mouse.graph.setSelected(path);
                browser.repaint();
//                System.out.println(obj.toString());
            }
            }
        });
        try {
            maze.read(getClass().getResourceAsStream("/micromouse/plan"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
