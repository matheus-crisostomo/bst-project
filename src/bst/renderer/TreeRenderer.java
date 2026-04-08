package bst.renderer;

import bst.model.BSTNode;
import bst.theme.Theme;

import java.awt.*;
import java.awt.geom.*;
import java.util.ArrayList;
import java.util.List;

public class TreeRenderer {

    private int highlightedValue  = Integer.MIN_VALUE;
    private int lastInsertedValue = Integer.MIN_VALUE;

    public void setHighlightedValue(int val)  { this.highlightedValue  = val; }
    public void setLastInsertedValue(int val) { this.lastInsertedValue = val; }
    public void clearHighlight()              { this.highlightedValue  = Integer.MIN_VALUE; }
    public void clearLastInserted()           { this.lastInsertedValue = Integer.MIN_VALUE; }

    // ── Layout ───────────────────────────────────────────────────────────────

    public void assignPositions(BSTNode root) {
        int[] counter = {0};
        assignRec(root, 0, counter);
    }

    private void assignRec(BSTNode node, int depth, int[] counter) {
        if (node == null) return;
        assignRec(node.left,  depth + 1, counter);
        node.gridX = counter[0]++;
        node.gridY = depth;
        assignRec(node.right, depth + 1, counter);
    }

    public Dimension calcCanvasSize(BSTNode root, int minW, int minH) {
        if (root == null) return new Dimension(minW, minH);
        List<BSTNode> all = collectAll(root);
        int minX = all.stream().mapToInt(n -> n.gridX).min().orElse(0);
        int maxX = all.stream().mapToInt(n -> n.gridX).max().orElse(0);
        int maxY = all.stream().mapToInt(n -> n.gridY).max().orElse(0);
        int w = Math.max((maxX - minX + 1) * Theme.H_GAP + Theme.CANVAS_PAD * 2, minW);
        int h = Math.max((maxY + 1)        * Theme.V_GAP + Theme.CANVAS_PAD * 2, minH);
        return new Dimension(w, h);
    }

    // ── Render ───────────────────────────────────────────────────────────────

    public void render(Graphics2D g2, BSTNode root, int canvasW, int canvasH) {
        applyHints(g2);
        drawBackground(g2, canvasW, canvasH);

        if (root == null) {
            drawEmpty(g2, canvasW, canvasH);
            return;
        }

        int[] off = calcOffsets(root, canvasW);
        drawEdges(g2, root, off[0], Theme.CANVAS_PAD);
        drawNodes(g2, root, root, off[0], Theme.CANVAS_PAD);
    }

    // ── Hit test ─────────────────────────────────────────────────────────────

    public int getNodeAt(BSTNode root, int mx, int my, int canvasW) {
        if (root == null) return Integer.MIN_VALUE;
        int[] off = calcOffsets(root, canvasW);
        for (BSTNode n : collectAll(root)) {
            int cx = px(n, off[0]);
            int cy = py(n, Theme.CANVAS_PAD);
            if (Point.distance(mx, my, cx, cy) <= Theme.NODE_RADIUS + 4) return n.val;
        }
        return Integer.MIN_VALUE;
    }

    // ── Privados ─────────────────────────────────────────────────────────────

    private void applyHints(Graphics2D g2) {
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void drawBackground(Graphics2D g2, int w, int h) {
        // grade sutil
        g2.setColor(new Color(255, 255, 255, 3));
        g2.setStroke(new BasicStroke(1f));
        for (int x = 0; x < w; x += 44) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 44) g2.drawLine(0, y, w, y);
    }

    private void drawEmpty(Graphics2D g2, int w, int h) {
        int cx = w / 2, cy = h / 2;

        // círculo tracejado
        g2.setColor(new Color(255, 255, 255, 12));
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1f, new float[]{5f, 5f}, 0f));
        g2.drawOval(cx - 36, cy - 50, 72, 72);

        // ícone
        g2.setStroke(new BasicStroke(1.5f));
        g2.setColor(new Color(255, 255, 255, 20));
        g2.drawLine(cx, cy - 16, cx - 18, cy + 10);
        g2.drawLine(cx, cy - 16, cx + 18, cy + 10);
        g2.fillOval(cx - 5,  cy - 22, 10, 10);
        g2.fillOval(cx - 23, cy + 4,  10, 10);
        g2.fillOval(cx + 13, cy + 4,  10, 10);

        g2.setFont(Theme.ui(Font.PLAIN, 12));
        g2.setColor(new Color(255, 255, 255, 20));
        String msg = "Insira um valor para começar";
        FontMetrics fm = g2.getFontMetrics();
        g2.drawString(msg, cx - fm.stringWidth(msg) / 2, cy + 52);
    }

    private void drawEdges(Graphics2D g2, BSTNode node, int ox, int oy) {
        if (node == null) return;
        if (node.left  != null) { drawEdge(g2, px(node,ox), py(node,oy), px(node.left,ox),  py(node.left,oy));  drawEdges(g2, node.left,  ox, oy); }
        if (node.right != null) { drawEdge(g2, px(node,ox), py(node,oy), px(node.right,ox), py(node.right,oy)); drawEdges(g2, node.right, ox, oy); }
    }

    private void drawEdge(Graphics2D g2, int x1, int y1, int x2, int y2) {
        g2.setStroke(new BasicStroke(1.2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.setColor(new Color(255, 255, 255, 14));
        g2.drawLine(x1, y1, x2, y2);
    }

    private void drawNodes(Graphics2D g2, BSTNode node, BSTNode root, int ox, int oy) {
        if (node == null) return;
        drawNodes(g2, node.left,  root, ox, oy);
        drawNodes(g2, node.right, root, ox, oy);
        drawNode(g2, node, node == root, ox, oy);
    }

    private void drawNode(Graphics2D g2, BSTNode node, boolean isRoot, int ox, int oy) {
        final int R  = Theme.NODE_RADIUS;
        int cx = px(node, ox);
        int cy = py(node, oy);

        boolean isNew = node.val == lastInsertedValue;
        boolean isHl  = node.val == highlightedValue;

        // glow externo para selecionado/novo
        if (isNew || isHl) {
            for (int i = 3; i >= 1; i--) {
                int alpha = isNew ? 35 / i : 25 / i;
                g2.setColor(new Color(255, 255, 255, alpha));
                g2.setStroke(new BasicStroke(i * 2.5f));
                int off = R + i * 5;
                g2.drawOval(cx - off, cy - off, off * 2, off * 2);
            }
        }

        // sombra
        g2.setColor(new Color(0, 0, 0, 60));
        g2.fillOval(cx - R + 2, cy - R + 2, R * 2, R * 2);

        // preenchimento glass
        if (isNew) {
            g2.setColor(new Color(255, 255, 255, 22));
        } else if (isHl) {
            g2.setColor(new Color(255, 255, 255, 16));
        } else {
            g2.setColor(new Color(255, 255, 255, 8));
        }
        g2.fillOval(cx - R, cy - R, R * 2, R * 2);

        // borda
        float sw = (isNew || isHl || isRoot) ? 1.8f : 1.2f;
        int   ba = (isNew || isHl) ? 90 : isRoot ? 60 : 30;
        g2.setStroke(new BasicStroke(sw));
        g2.setColor(new Color(255, 255, 255, ba));
        g2.drawOval(cx - R, cy - R, R * 2, R * 2);

        // reflexo interno
        g2.setColor(new Color(255, 255, 255, 10));
        g2.fillArc(cx - R + 5, cy - R + 5, R - 4, R - 4, 30, 110);

        // texto
        String text = String.valueOf(node.val);
        int fontSize = text.length() > 3 ? 10 : 12;
        g2.setFont(Theme.ui(Font.PLAIN, fontSize));
        FontMetrics fm = g2.getFontMetrics();
        int tx = cx - fm.stringWidth(text) / 2;
        int ty = cy + fm.getAscent() / 2 - 1;

        // sombra do texto
        g2.setColor(new Color(0, 0, 0, 80));
        g2.drawString(text, tx + 1, ty + 1);

        // texto principal
        int textAlpha = (isNew || isHl) ? 255 : 200;
        g2.setColor(new Color(237, 237, 237, textAlpha));
        g2.drawString(text, tx, ty);

        // badge ROOT
        if (isRoot) {
            g2.setFont(Theme.ui(Font.PLAIN, 9));
            String badge = "root";
            int bw = g2.getFontMetrics().stringWidth(badge) + 10;
            int bx = cx - bw / 2;
            int by = cy - R - 18;
            g2.setColor(new Color(255, 255, 255, 8));
            g2.fillRoundRect(bx, by, bw, 13, 4, 4);
            g2.setColor(new Color(255, 255, 255, 25));
            g2.drawRoundRect(bx, by, bw, 13, 4, 4);
            g2.setColor(new Color(255, 255, 255, 60));
            g2.drawString(badge, bx + 5, by + 10);
        }
    }

    private int[] calcOffsets(BSTNode root, int canvasW) {
        List<BSTNode> all = collectAll(root);
        int minX = all.stream().mapToInt(n -> n.gridX).min().orElse(0);
        int maxX = all.stream().mapToInt(n -> n.gridX).max().orElse(0);
        int cols = maxX - minX + 1;
        int ox = Theme.CANVAS_PAD + (canvasW - cols * Theme.H_GAP) / 2 - minX * Theme.H_GAP;
        return new int[]{ox};
    }

    private int px(BSTNode n, int ox) { return n.gridX * Theme.H_GAP + ox; }
    private int py(BSTNode n, int oy) { return n.gridY * Theme.V_GAP + oy; }

    private List<BSTNode> collectAll(BSTNode node) {
        List<BSTNode> list = new ArrayList<>();
        collectRec(node, list);
        return list;
    }

    private void collectRec(BSTNode node, List<BSTNode> list) {
        if (node == null) return;
        list.add(node);
        collectRec(node.left,  list);
        collectRec(node.right, list);
    }
}
