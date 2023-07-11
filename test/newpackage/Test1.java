/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package newpackage;

import java.awt.BorderLayout;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import javax.swing.JScrollPane;
import micromouse.Browser;
import micromouse.CommandBar;
import micromouse.FileManager;
import micromouse.Maze;
import micromouse.StatusBar;

/**
 *
 * @author viljinsky
 */
public class Test1 extends Browser{
    
    FileManager fileManager = new FileManager(this);
    
    CommandBar commandBar = new CommandBar(this);
    
    StatusBar statusBar = new StatusBar();

    public Test1() {
       super();
       setMaze( new Maze(16,16));
    }
    
    public void execute(){
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(fileManager.menu());
        frame.setJMenuBar(menuBar);
        
        Container container = frame.getContentPane();
        container.add(new JScrollPane(this));
        container.add(commandBar,BorderLayout.PAGE_START);
        container.add(statusBar,BorderLayout.PAGE_END);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    public static void main(String[] args) {
        new Test1().execute();
    }
    
}
