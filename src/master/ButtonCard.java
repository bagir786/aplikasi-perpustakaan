/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package master;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.JButton;

/**
 *
 * @author Asus
 */
public class ButtonCard extends JButton {

    private int radius = 20;

    public ButtonCard() {
        setContentAreaFilled(false);
        setFocusPainted(false);
        setBorderPainted(false);
        setOpaque(false);
        setForeground(Color.BLACK);
        setFont(new Font("Segoe UI", Font.BOLD, 14));
        
        addMouseListener(new java.awt.event.MouseAdapter() {
        public void mouseEntered(java.awt.event.MouseEvent evt) {
        setCursor(new Cursor(Cursor.HAND_CURSOR));
    }
        public void mouseExited(java.awt.event.MouseEvent evt) {
        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
}
});
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow
        g2.setColor(new Color(0,0,0,30));
        g2.fillRoundRect(5, 5, getWidth()-5, getHeight()-5, radius, radius);

        // background
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0, getWidth()-5, getHeight()-5, radius, radius);

        super.paintComponent(g);
    }
}