package bst.renderer;

import bst.model.BSTNode;
import bst.theme.Theme;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeRenderer {

    private int highlightedValue  = Integer.MIN_VALUE;
    private int lastInsertedValue = Integer.MIN_VALUE;
    private boolean enableRBColors = false;


    private int     rotationPivotValue = Integer.MIN_VALUE;
    private String  rotationLabel      = null;
    private float   rotationProgress   = 0f;
    private boolean rotationClockwise  = true;


    /** Posições anteriores dos nós (valor → [pixelX, pixelY]) antes da rotação. */
    private Map<Integer, int[]> previousPositions = new HashMap<>();
    /** Progresso da interpolação de posição (0.0 = posição antiga, 1.0 = posição nova). */
    private float transitionProgress = 1f;
    /** Último offset X usado no render (para snapshot de posições). */
    private int lastOffsetX = 0;

    public void setHighlightedValue(int val)  { this.highlightedValue  = val; }
    public void setLastInsertedValue(int val) { this.lastInsertedValue = val; }
    public void setEnableRBColors(boolean enable) { this.enableRBColors = enable; }
    public void clearHighlight()              { this.highlightedValue  = Integer.MIN_VALUE; }
    public void clearLastInserted()           { this.lastInsertedValue = Integer.MIN_VALUE; }


     * Inicia a animação de rotação no nó pivô.
     */
    public void startRotationAnimation(int pivotVal, String label, boolean clockwise) {
        this.rotationPivotValue = pivotVal;
        this.rotationLabel = label;
        this.rotationClockwise = clockwise;
        this.rotationProgress = 0f;
    }

    public void setRotationProgress(float progress) {
        this.rotationProgress = Math.min(1f, Math.max(0f, progress));
    }

    public void clearRotationAnimation() {
        this.rotationPivotValue = Integer.MIN_VALUE;
        this.rotationLabel = null;
        this.rotationProgress = 0f;
    }

    public boolean isRotationAnimating() {
        return rotationPivotValue != Integer.MIN_VALUE;
    }


     * Captura as posições atuais de todos os nós antes de uma rotação.
     * Deve ser chamado ANTES da árvore ser modificada.
     */
    public void snapshotPositions(BSTNode root, int canvasW) {
        previousPositions.clear();
        if (root == null) return;
        assignPositions(root);
        int[] off = calcOffsets(root, canvasW);
        for (BSTNode n : collectAll(root)) {
            previousPositions.put(n.val, new int[]{px(n, off[0]), py(n, Theme.CANVAS_PAD)});
        }
        lastOffsetX = off[0];
    }

    /**
     * Inicia a transição animada das posições antigas para as novas.
     */
    public void startTransition() {
        this.transitionProgress = 0f;
    }

    /**
     * Atualiza o progresso da transição (0.0 a 1.0).
     */
    public void setTransitionProgress(float progress) {
        this.transitionProgress = Math.min(1f, Math.max(0f, progress));
    }

    /**
     * Limpa o estado de transição.
     */
    public void clearTransition() {
        this.transitionProgress = 1f;
        this.previousPositions.clear();
    }

    /** Verifica se há transição de posição ativa. */
    public boolean isTransitioning() {
        return transitionProgress < 1f && !previousPositions.isEmpty();
    }


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


        applyHints(g2);
        drawBackground(g2, canvasW, canvasH);

        if (root == null) {
            drawEmpty(g2, canvasW, canvasH);
            return;
        }

        int[] off = calcOffsets(root, canvasW);
        int ox = off[0];
        int oy = Theme.CANVAS_PAD;

        if (isTransitioning()) {
            drawEdgesInterpolated(g2, root, ox, oy);
            drawNodesInterpolated(g2, root, root, ox, oy);
        } else {
            drawEdges(g2, root, ox, oy);
            drawNodes(g2, root, root, ox, oy);
        }

        drawRotationOverlay(g2, root, ox, oy);
    }


        if (root == null) return Integer.MIN_VALUE;
        int[] off = calcOffsets(root, canvasW);
        for (BSTNode n : collectAll(root)) {
            int cx = px(n, off[0]);
            int cy = py(n, Theme.CANVAS_PAD);
            if (Point.distance(mx, my, cx, cy) <= Theme.NODE_RADIUS + 4) return n.val;
        }
        return Integer.MIN_VALUE;
    }


        int targetX = px(node, ox);
        int targetY = py(node, oy);

        if (!isTransitioning()) return new int[]{targetX, targetY};

        int[] prev = previousPositions.get(node.val);
        if (prev == null) return new int[]{targetX, targetY};

        float t = transitionProgress;
        int interpX = prev[0] + (int) ((targetX - prev[0]) * t);
        int interpY = prev[1] + (int) ((targetY - prev[1]) * t);
        return new int[]{interpX, interpY};
    }

    private void drawEdgesInterpolated(Graphics2D g2, BSTNode node, int ox, int oy) {
        if (node == null) return;
        int[] pos = interpolatedPos(node, ox, oy);
        if (node.left != null) {
            int[] childPos = interpolatedPos(node.left, ox, oy);
            drawEdge(g2, pos[0], pos[1], childPos[0], childPos[1]);
            drawEdgesInterpolated(g2, node.left, ox, oy);
        }
        if (node.right != null) {
            int[] childPos = interpolatedPos(node.right, ox, oy);
            drawEdge(g2, pos[0], pos[1], childPos[0], childPos[1]);
            drawEdgesInterpolated(g2, node.right, ox, oy);
        }
    }

    private void drawNodesInterpolated(Graphics2D g2, BSTNode node, BSTNode root, int ox, int oy) {
        if (node == null) return;
        drawNodesInterpolated(g2, node.left, root, ox, oy);
        drawNodesInterpolated(g2, node.right, root, ox, oy);
        int[] pos = interpolatedPos(node, ox, oy);
        drawNodeAt(g2, node, node == root, pos[0], pos[1]);
    }


        if (rotationPivotValue == Integer.MIN_VALUE || rotationProgress <= 0f) return;

        BSTNode pivot = findNode(root, rotationPivotValue);
        if (pivot == null) return;

        int[] pos = interpolatedPos(pivot, ox, oy);
        int cx = pos[0];
        int cy = pos[1];
        int arcRadius = Theme.NODE_RADIUS + 14;

        float alpha = rotationProgress < 0.5f
                ? rotationProgress * 2f
                : 2f * (1f - rotationProgress);
        int baseAlpha = (int) (alpha * 200);

        // Arco animado ao redor do pivô
        float sweep = rotationProgress * 270f;
        int startAngle = 90;
        if (!rotationClockwise) sweep = -sweep;

        // Glow externo
        for (int i = 3; i >= 1; i--) {
            int glowAlpha = (int) (alpha * 40 / i);
            g2.setColor(new Color(100, 200, 255, glowAlpha));
            g2.setStroke(new BasicStroke(i * 2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            int r = arcRadius + i * 4;
            g2.drawArc(cx - r, cy - r, r * 2, r * 2, startAngle, (int) sweep);
        }

        // Arco principal
        g2.setColor(new Color(100, 200, 255, baseAlpha));
        g2.setStroke(new BasicStroke(2.5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        g2.drawArc(cx - arcRadius, cy - arcRadius, arcRadius * 2, arcRadius * 2,
                startAngle, (int) sweep);

        // Seta na ponta do arco
        if (rotationProgress > 0.1f) {
            double endAngleRad = Math.toRadians(startAngle + sweep);
            int ax = cx + (int) (arcRadius * Math.cos(endAngleRad));
            int ay = cy - (int) (arcRadius * Math.sin(endAngleRad));

            double arrowAngle = rotationClockwise
                    ? endAngleRad - Math.PI / 2
                    : endAngleRad + Math.PI / 2;
            int arrowLen = 8;
            int ax1 = ax + (int) (arrowLen * Math.cos(arrowAngle - 0.4));
            int ay1 = ay - (int) (arrowLen * Math.sin(arrowAngle - 0.4));
            int ax2 = ax + (int) (arrowLen * Math.cos(arrowAngle + 0.4));
            int ay2 = ay - (int) (arrowLen * Math.sin(arrowAngle + 0.4));

            g2.setStroke(new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.drawLine(ax, ay, ax1, ay1);
            g2.drawLine(ax, ay, ax2, ay2);
        }

        // Label da rotação acima do pivô
        if (rotationLabel != null && rotationProgress > 0.15f) {
            int labelAlpha = (int) (alpha * 255);
            g2.setFont(Theme.ui(Font.BOLD, 11));
            FontMetrics fm = g2.getFontMetrics();
            String text = rotationLabel;
            int tw = fm.stringWidth(text);
            int tx = cx - tw / 2;
            int ty = cy - arcRadius - 12;

            g2.setColor(new Color(100, 200, 255, (int) (alpha * 30)));
            g2.fillRoundRect(tx - 6, ty - fm.getAscent() - 2, tw + 12, fm.getHeight() + 4, 6, 6);
            g2.setColor(new Color(100, 200, 255, (int) (alpha * 80)));
            g2.drawRoundRect(tx - 6, ty - fm.getAscent() - 2, tw + 12, fm.getHeight() + 4, 6, 6);

            g2.setColor(new Color(100, 200, 255, labelAlpha));
            g2.drawString(text, tx, ty);
        }
    }

    private BSTNode findNode(BSTNode node, int val) {
        if (node == null) return null;
        if (node.val == val) return node;
        BSTNode left = findNode(node.left, val);
        return left != null ? left : findNode(node.right, val);
    }


        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,      RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,         RenderingHints.VALUE_RENDER_QUALITY);
    }

    private void drawBackground(Graphics2D g2, int w, int h) {
        g2.setColor(new Color(255, 255, 255, 3));
        g2.setStroke(new BasicStroke(1f));
        for (int x = 0; x < w; x += 44) g2.drawLine(x, 0, x, h);
        for (int y = 0; y < h; y += 44) g2.drawLine(0, y, w, y);
    }

    private void drawEmpty(Graphics2D g2, int w, int h) {
        int cx = w / 2, cy = h / 2;

        g2.setColor(new Color(255, 255, 255, 12));
        g2.setStroke(new BasicStroke(1f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND,
                1f, new float[]{5f, 5f}, 0f));
        g2.drawOval(cx - 36, cy - 50, 72, 72);

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
        drawNodeAt(g2, node, node == root, px(node, ox), py(node, oy));
    }

    private void drawNodeAt(Graphics2D g2, BSTNode node, boolean isRoot, int cx, int cy) {
        final int R = Theme.NODE_RADIUS;

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

        // Define a cor base do nó (vermelho ou preto para RB Tree, padrão para as outras)
        Color fillColor;
        Color borderColor;
        if (enableRBColors) {
            if (node.color) { // RED
                fillColor = isNew ? new Color(255, 60, 60, 45) : isHl ? new Color(255, 60, 60, 35) : new Color(255, 60, 60, 20);
                borderColor = new Color(255, 80, 80, (isNew || isHl) ? 140 : isRoot ? 100 : 70);
            } else { // BLACK
                fillColor = isNew ? new Color(20, 20, 20, 255) : isHl ? new Color(30, 30, 30, 255) : new Color(15, 15, 15, 255);
                borderColor = new Color(80, 80, 80, (isNew || isHl) ? 180 : isRoot ? 140 : 100);
            }
        } else {
            fillColor = isNew ? new Color(255, 255, 255, 22) : isHl ? new Color(255, 255, 255, 16) : new Color(255, 255, 255, 8);
            borderColor = new Color(255, 255, 255, (isNew || isHl) ? 90 : isRoot ? 60 : 30);
        }

        // preenchimento glass
        g2.setColor(fillColor);
        g2.fillOval(cx - R, cy - R, R * 2, R * 2);

        // borda
        float sw = (isNew || isHl || isRoot) ? 1.8f : 1.2f;
        g2.setStroke(new BasicStroke(sw));
        g2.setColor(borderColor);
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
