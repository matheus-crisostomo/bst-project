package bst.theme;

import java.awt.*;

/**
 * Theme — Design tokens (Vercel Dark + Glassmorphism)
 */
public final class Theme {

    private Theme() {}

    public static final Color BG_DARK    = new Color(5, 5, 8); // Very dark indigo
    public static final Color BG_PANEL   = new Color(15, 15, 20); // Darker panel
    public static final Color BG_SURFACE = new Color(25, 25, 32); // Slightly lighter
    public static final Color BG_GLASS   = new Color(255, 255, 255, 12); // Brighter glass
    public static final Color BG_GLASS2  = new Color(255, 255, 255, 18); // Brighter glass

    public static final Color BORDER       = new Color(255, 255, 255, 20);
    public static final Color BORDER_LIGHT = new Color(255, 255, 255, 40);

    public static final Color ACCENT_BLUE   = new Color(100, 200, 255);
    public static final Color ACCENT_ORANGE = new Color(255, 130,  80);
    public static final Color ACCENT_PURPLE = new Color(180, 130, 255);
    public static final Color ACCENT_GREEN  = new Color( 80, 240, 160);
    public static final Color ACCENT_CYAN   = new Color( 40, 220, 255);

    public static final Color TEXT_BRIGHT = new Color(245, 245, 250);
    public static final Color TEXT_MUTED  = new Color(130, 130, 140);
    public static final Color TEXT_DIM    = new Color( 80,  80,  90);

    public static final Color NODE_FILL = new Color(18, 18, 18);
    public static final Color EDGE      = new Color(50, 50, 50);

    public static final int NODE_RADIUS = 22;
    public static final int H_GAP       = 60;
    public static final int V_GAP       = 78;
    public static final int CANVAS_PAD  = 48;

    public static Font ui(int style, int size) {
        // Tenta Inter, depois Segoe UI, depois SansSerif como fallback
        for (String name : new String[]{"Inter", "Segoe UI", "SF Pro Display", "Helvetica Neue"}) {
            Font f = new Font(name, style, size);
            if (!f.getFamily().equals("Dialog")) return f;
        }
        return new Font("SansSerif", style, size);
    }

    public static Font mono(int style, int size) {
        for (String name : new String[]{"JetBrains Mono", "Cascadia Code", "Consolas", "Menlo"}) {
            Font f = new Font(name, style, size);
            if (!f.getFamily().equals("Dialog")) return f;
        }
        return new Font("Monospaced", style, size);
    }
}
