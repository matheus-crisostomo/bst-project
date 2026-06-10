package bst.theme;

import javax.swing.*;
import javax.swing.plaf.basic.BasicScrollBarUI;
import java.awt.*;

public class ModernScrollBarUI extends BasicScrollBarUI {
    private final int THUMB_SIZE = 8;
    
    @Override
    protected void configureScrollBarColors() {
        this.thumbColor = new Color(255, 255, 255, 40);
        this.thumbDarkShadowColor = new Color(0, 0, 0, 0);
        this.thumbHighlightColor = new Color(0, 0, 0, 0);
        this.thumbLightShadowColor = new Color(0, 0, 0, 0);
        this.trackColor = new Color(0, 0, 0, 0);
        this.trackHighlightColor = new Color(0, 0, 0, 0);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        return createZeroButton();
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        return createZeroButton();
    }

    private JButton createZeroButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(0, 0));
        button.setMinimumSize(new Dimension(0, 0));
        button.setMaximumSize(new Dimension(0, 0));
        return button;
    }

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        // Transparent track
    }

    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if (thumbBounds.isEmpty() || !scrollbar.isEnabled()) return;
        
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        Color color = isDragging ? new Color(255, 255, 255, 80)
                   : isThumbRollover() ? new Color(255, 255, 255, 60)
                   : thumbColor;
        
        g2.setColor(color);
        int w = scrollbar.getOrientation() == JScrollBar.VERTICAL ? THUMB_SIZE : thumbBounds.width;
        int h = scrollbar.getOrientation() == JScrollBar.VERTICAL ? thumbBounds.height : THUMB_SIZE;
        int x = scrollbar.getOrientation() == JScrollBar.VERTICAL ? thumbBounds.x + (thumbBounds.width - w) / 2 : thumbBounds.x;
        int y = scrollbar.getOrientation() == JScrollBar.VERTICAL ? thumbBounds.y : thumbBounds.y + (thumbBounds.height - h) / 2;
        
        g2.fillRoundRect(x, y, w, h, THUMB_SIZE, THUMB_SIZE);
        g2.dispose();
    }
}
