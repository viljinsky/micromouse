/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package micromouse;

import java.awt.FlowLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 *
 * @author viljinsky
 */
public class StatusBar extends JPanel {
    
    JLabel label = new JLabel("StatusBar");

    public StatusBar() {
        setLayout(new FlowLayout(FlowLayout.LEFT));
        add(label);
    }

    public void setStatusText(String statusText) {
        label.setText(statusText);
    }
    
}
