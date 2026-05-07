/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package stylecard;

/**
 *
 * @author Asus
 */
import javax.swing.*;
import java.awt.*;

public class PanelCard extends JPanel {

    private int radius = 19;
    private int yOffset = 0;

    public PanelCard() {
        setOpaque(false);
            addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                yOffset = -5;
                repaint();
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                yOffset = 0;
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // shadow
        g2.setColor(new Color(0, 0, 0, 30));
        g2.fillRoundRect(5, 5 + yOffset, getWidth()-5, getHeight()-5, radius, radius);

        // card
        g2.setColor(Color.WHITE);
        g2.fillRoundRect(0, 0 + yOffset, getWidth()-5, getHeight()-5, radius, radius);
    }
}