package bst.theme;

import java.awt.*;

/**
 * Theme — Design tokens (Vercel Dark + Glassmorphism)
 */
public final class Theme {

    private Theme() {}

    // ── Backgrounds ──────────────────────────────────────────────────────────
    public static final Color BG_DARK    = new Color(0,   0,   0);
    public static final Color BG_PANEL   = new Color(10,  10,  10);
    public static final Color BG_SURFACE = new Color(18,  18,  18);
    public static final Color BG_GLASS   = new Color(255, 255, 255, 6);
    public static final Color BG_GLASS2  = new Color(255, 255, 255, 10);

    // ── Bordas ───────────────────────────────────────────────────────────────
    public static final Color BORDER       = new Color(255, 255, 255, 18);
    public static final Color BORDER_LIGHT = new Color(255, 255, 255, 30);

    // ── Acentos ──────────────────────────────────────────────────────────────
    public static final Color ACCENT_BLUE   = new Color(255, 255, 255);
    public static final Color ACCENT_ORANGE = new Color(255, 100,  50);
    public static final Color ACCENT_PURPLE = new Color(155,  92, 255);
    public static final Color ACCENT_GREEN  = new Color( 80, 220, 140);
    public static final Color ACCENT_CYAN   = new Color(100, 200, 255);

    // ── Texto ────────────────────────────────────────────────────────────────
    public static final Color TEXT_BRIGHT = new Color(237, 237, 237);
    public static final Color TEXT_MUTED  = new Color(100, 100, 100);
    public static final Color TEXT_DIM    = new Color( 55,  55,  55);

    // ── Árvore ───────────────────────────────────────────────────────────────
    public static final Color NODE_FILL = new Color(18, 18, 18);
    public static final Color EDGE      = new Color(50, 50, 50);

    // ── Dimensões dos Nós ────────────────────────────────────────────────────
    public static final int NODE_RADIUS = 22;
    public static final int H_GAP       = 60;
    public static final int V_GAP       = 78;
    public static final int CANVAS_PAD  = 48;

    // ── Fontes ───────────────────────────────────────────────────────────────
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
