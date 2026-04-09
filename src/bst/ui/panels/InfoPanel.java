package bst.ui.panels;

import bst.model.BST;
import bst.model.BSTAnalyzer;
import bst.model.BSTNode;
import bst.observer.BSTObserver;
import bst.theme.Theme;
import java.awt.*;
import java.util.List;
import javax.swing.*;
import javax.swing.border.*;

public class InfoPanel extends JPanel implements BSTObserver {

    private static final int WIDTH = 310;

    private final BST         bst;
    private final BSTAnalyzer analyzer;
    private int selectedNodeVal = Integer.MIN_VALUE;

    // Properties
    private JLabel lblNodes, lblLeaves, lblInternal, lblRoot;
    private JLabel lblHeight, lblDepth, lblLevels, lblTreeDepth;

    // Types
    private JLabel lblFull, lblComplete, lblPerfect, lblBalanced, lblDegen;

    // Selected node
    private JLabel  lblSelVal, lblSelLevel, lblSelDepth, lblSelHeight;
    private JLabel  lblSelLeft, lblSelRight, lblSelIsLeaf;
    private JTextArea taSelPath;

    // Paths
    private JTextArea taAllPaths;

    public InfoPanel(BST bst, BSTAnalyzer analyzer) {
        this.bst      = bst;
        this.analyzer = analyzer;

        setPreferredSize(new Dimension(WIDTH, 0));
        setBackground(Theme.BG_PANEL);
        setBorder(new MatteBorder(0, 1, 0, 0, Theme.BORDER));
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);

        JPanel content = buildContent();
        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setBackground(Theme.BG_PANEL);
        scroll.getViewport().setBackground(Theme.BG_PANEL);
        scroll.getVerticalScrollBar().setUnitIncrement(14);
        scroll.getVerticalScrollBar().setBackground(Theme.BG_PANEL);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(new CompoundBorder(
            new MatteBorder(0, 0, 1, 0, Theme.BORDER),
            new EmptyBorder(12, 16, 12, 16)
        ));
        JLabel title = new JLabel("Análise");
        title.setFont(Theme.ui(Font.PLAIN, 13));
        title.setForeground(Theme.TEXT_BRIGHT);
        p.add(title, BorderLayout.WEST);
        return p;
    }

    private JPanel buildContent() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(Theme.BG_PANEL);
        p.setBorder(new EmptyBorder(8, 0, 16, 0));

        p.add(buildSection("Propriedades", this::buildPropertiesRows));
        p.add(vGap(2));
        p.add(buildSection("Tipos da Árvore", this::buildTypesRows));
        p.add(vGap(2));
        p.add(buildSection("Nó Selecionado", this::buildNodeRows));
        p.add(vGap(2));
        p.add(buildSection("Caminhos Raiz → Folha", this::buildPathsRows));

        return p;
    }

    // ── Seções ───────────────────────────────────────────────────────────────

    private JPanel buildSection(String title, Runnable rowBuilder) {
        JPanel sec = new JPanel();
        sec.setLayout(new BoxLayout(sec, BoxLayout.Y_AXIS));
        sec.setBackground(Theme.BG_PANEL);
        sec.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // título da seção
        JLabel lbl = new JLabel(title);
        lbl.setFont(Theme.ui(Font.PLAIN, 11));
        lbl.setForeground(Theme.TEXT_DIM);
        lbl.setBorder(new EmptyBorder(10, 16, 6, 16));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        sec.add(lbl);

        // card glass
        JPanel card = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_GLASS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(4, 0, 4, 0));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));

        // injeta as linhas no card atual via campo temporário
        currentCard = card;
        rowBuilder.run();
        currentCard = null;

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.setBorder(new EmptyBorder(0, 12, 0, 12));
        wrapper.setAlignmentX(Component.LEFT_ALIGNMENT);
        wrapper.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        wrapper.add(card, BorderLayout.CENTER);
        sec.add(wrapper);

        return sec;
    }

    private JPanel currentCard;

    private void buildPropertiesRows() {
        lblNodes    = cardRow("Total de Nós",    "—");
        lblLeaves   = cardRow("Nós Folha",       "—");
        lblInternal = cardRow("Nós Internos",    "—");
        lblRoot     = cardRow("Raiz",            "—");
        cardDivider();
        lblHeight    = cardRow("Altura",            "—");
        lblTreeDepth = cardRow("Profundidade",   "—");
        lblDepth     = cardRow("Prof. Máxima",   "—");
        lblLevels    = cardRow("Níveis",         "—");
    }

    private void buildTypesRows() {
        lblFull      = typeRow("Cheia (Full)");
        lblComplete  = typeRow("Completa");
        lblPerfect   = typeRow("Perfeita");
        lblBalanced  = typeRow("Balanceada");
        lblDegen     = typeRow("Degenerada");
    }

    private void buildNodeRows() {
        JLabel hint = new JLabel("Clique em um nó para inspecionar");
        hint.setFont(Theme.ui(Font.PLAIN, 11));
        hint.setForeground(Theme.TEXT_DIM);
        hint.setBorder(new EmptyBorder(4, 14, 4, 14));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentCard.add(hint);

        lblSelVal    = cardRow("Valor",           "—");
        cardDivider();
        lblSelLevel  = cardRow("Nível",           "—");
        lblSelDepth  = cardRow("Profundidade",    "—");
        lblSelHeight = cardRow("Altura",          "—");
        cardDivider();
        lblSelLeft   = cardRow("Filho Esquerdo",  "—");
        lblSelRight  = cardRow("Filho Direito",   "—");
        lblSelIsLeaf = cardRow("É Folha",         "—");
        cardDivider();

        JLabel pathLbl = new JLabel("Caminho da Raiz");
        pathLbl.setFont(Theme.ui(Font.PLAIN, 11));
        pathLbl.setForeground(Theme.TEXT_DIM);
        pathLbl.setBorder(new EmptyBorder(6, 14, 4, 14));
        pathLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentCard.add(pathLbl);

        taSelPath = buildTextArea(3);
        JScrollPane sp = wrapTextArea(taSelPath);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 54));
        currentCard.add(sp);
        currentCard.add(Box.createVerticalStrut(6));
    }

    private void buildPathsRows() {
        taAllPaths = buildTextArea(7);
        JScrollPane sp = wrapTextArea(taAllPaths);
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        currentCard.add(Box.createVerticalStrut(4));
        currentCard.add(sp);
        currentCard.add(Box.createVerticalStrut(6));
    }

    // ── Refresh ───────────────────────────────────────────────────────────────

    public void refresh() {
        BSTNode root = bst.root;
        refreshProperties(root);
        refreshTypes(root);
        refreshSelectedNode();
        refreshPaths(root);
    }

    private void refreshProperties(BSTNode root) {
        if (root == null) {
            setText(lblNodes, "—"); setText(lblLeaves, "—");
            setText(lblInternal, "—"); setText(lblRoot, "—");
            setText(lblHeight, "—"); setText(lblDepth, "—"); setText(lblLevels, "—"); setText(lblTreeDepth, "—");
        } else {
            setText(lblNodes,    String.valueOf(analyzer.countNodes(root)));
            setText(lblLeaves,   String.valueOf(analyzer.countLeaves(root)));
            setText(lblInternal, String.valueOf(analyzer.countInternalNodes(root)));
            setText(lblRoot,     String.valueOf(root.val));
            int h = analyzer.getTreeHeight(root);
            setText(lblHeight,    h + " aresta(s)");
            setText(lblTreeDepth, analyzer.getTreeMaxDepth(root) + " nível(is)");
            setText(lblDepth,     h + " aresta(s)");
            setText(lblLevels,    String.valueOf(analyzer.getTreeLevels(root)));
        }
    }

    private void refreshTypes(BSTNode root) {
        if (root == null) {
            setType(lblFull, false); setType(lblComplete, false);
            setType(lblPerfect, false); setType(lblBalanced, false);
            setType(lblDegen, false);
            return;
        }
        setType(lblFull,     analyzer.isFull(root));
        setType(lblComplete, analyzer.isComplete(root));
        setType(lblPerfect,  analyzer.isPerfect(root));
        setType(lblBalanced, analyzer.isBalanced(root));
        setType(lblDegen,    analyzer.isDegenerate(root) && analyzer.countNodes(root) > 1);
    }

    private void refreshSelectedNode() {
        BSTNode root = bst.root;
        if (root == null || selectedNodeVal == Integer.MIN_VALUE
                || analyzer.findNode(root, selectedNodeVal) == null) {
            setText(lblSelVal, "—"); setText(lblSelLevel, "—");
            setText(lblSelDepth, "—"); setText(lblSelHeight, "—");
            setText(lblSelLeft, "—"); setText(lblSelRight, "—");
            setText(lblSelIsLeaf, "—");
            taSelPath.setText("—");
            return;
        }
        BSTNode node   = analyzer.findNode(root, selectedNodeVal);
        int level  = analyzer.getNodeLevel(root, selectedNodeVal);
        int depth  = analyzer.getNodeDepth(root, selectedNodeVal);
        int height = analyzer.getNodeHeight(root, selectedNodeVal);

        setText(lblSelVal,    String.valueOf(node.val));
        setText(lblSelLevel,  String.valueOf(level));
        setText(lblSelDepth,  String.valueOf(depth));
        setText(lblSelHeight, String.valueOf(height));
        setText(lblSelLeft,   node.left  != null ? String.valueOf(node.left.val)  : "null");
        setText(lblSelRight,  node.right != null ? String.valueOf(node.right.val) : "null");
        setText(lblSelIsLeaf, (node.left == null && node.right == null) ? "Sim" : "Não");

        List<Integer> path = analyzer.getPathToNode(root, selectedNodeVal);
        taSelPath.setText(joinArrow(path));
    }

    private void refreshPaths(BSTNode root) {
        if (root == null) { taAllPaths.setText("—"); return; }
        List<List<Integer>> paths = analyzer.getAllRootToLeafPaths(root);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < paths.size(); i++) {
            if (i > 0) sb.append("\n");
            sb.append(i + 1).append(".  ").append(joinArrow(paths.get(i)));
        }
        taAllPaths.setText(sb.toString());
        taAllPaths.setCaretPosition(0);
    }

    // ── API Pública ───────────────────────────────────────────────────────────

    public void setSelectedNode(int val) {
        this.selectedNodeVal = val;
        SwingUtilities.invokeLater(this::refreshSelectedNode);
    }

    // ── Observer ──────────────────────────────────────────────────────────────

    @Override public void onTreeChanged()       { SwingUtilities.invokeLater(this::refresh); }
    @Override public void onNodeInserted(int v) {}
    @Override public void onNodeRemoved(int v)  {
        if (v == selectedNodeVal) selectedNodeVal = Integer.MIN_VALUE;
        SwingUtilities.invokeLater(this::refresh);
    }

    // ── Helpers de construção ─────────────────────────────────────────────────

    private JLabel cardRow(String key, String value) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(4, 14, 4, 14));

        JLabel k = new JLabel(key);
        k.setFont(Theme.ui(Font.PLAIN, 12));
        k.setForeground(Theme.TEXT_MUTED);
        k.setMinimumSize(new Dimension(0, 0));

        JLabel v = new JLabel(value);
        v.setFont(Theme.ui(Font.PLAIN, 12));
        v.setForeground(Theme.TEXT_BRIGHT);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        v.setMinimumSize(new Dimension(0, 0));

        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        currentCard.add(row);
        return v;
    }

    private JLabel typeRow(String name) {
        JPanel row = new JPanel(new BorderLayout(6, 0));
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(4, 14, 4, 14));

        JLabel k = new JLabel(name);
        k.setFont(Theme.ui(Font.PLAIN, 12));
        k.setForeground(Theme.TEXT_MUTED);
        k.setMinimumSize(new Dimension(0, 0));

        JLabel v = new JLabel("—");
        v.setFont(Theme.ui(Font.PLAIN, 12));
        v.setForeground(Theme.TEXT_DIM);
        v.setHorizontalAlignment(SwingConstants.RIGHT);
        v.setMinimumSize(new Dimension(0, 0));

        row.add(k, BorderLayout.WEST);
        row.add(v, BorderLayout.EAST);
        currentCard.add(row);
        return v;
    }

    private void setType(JLabel lbl, boolean ok) {
        lbl.setText(ok ? "Sim" : "Não");
        lbl.setForeground(ok ? Theme.ACCENT_GREEN : Theme.TEXT_DIM);
    }

    private void cardDivider() {
        JSeparator s = new JSeparator();
        s.setForeground(Theme.BORDER);
        s.setBackground(Theme.BORDER);
        s.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);
        currentCard.add(Box.createVerticalStrut(2));
        currentCard.add(s);
        currentCard.add(Box.createVerticalStrut(2));
    }

    private JTextArea buildTextArea(int rows) {
        JTextArea ta = new JTextArea(rows, 1);
        ta.setFont(Theme.mono(Font.PLAIN, 11));
        ta.setForeground(Theme.TEXT_MUTED);
        ta.setBackground(Theme.BG_SURFACE);
        ta.setCaretColor(Theme.TEXT_MUTED);
        ta.setBorder(new EmptyBorder(8, 10, 8, 10));
        ta.setEditable(false);
        ta.setLineWrap(true);
        ta.setWrapStyleWord(false);
        ta.setMaximumSize(new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE));
        ta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return ta;
    }

    private JScrollPane wrapTextArea(JTextArea ta) {
        JScrollPane sp = new JScrollPane(ta);
        sp.setBorder(new MatteBorder(1, 0, 1, 0, Theme.BORDER));
        sp.setBackground(Theme.BG_SURFACE);
        sp.getViewport().setBackground(Theme.BG_SURFACE);
        return sp;
    }

    private Component vGap(int h) { return Box.createVerticalStrut(h); }

    private void setText(JLabel lbl, String text) { lbl.setText(text); }

    private String joinArrow(List<Integer> list) {
        if (list == null || list.isEmpty()) return "—";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
