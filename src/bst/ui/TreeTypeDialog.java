package bst.ui;

import bst.theme.Theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Diálogo de seleção do tipo de árvore exibido na inicialização.
 * Retorna o tipo escolhido via {@link #getSelectedType()}.
 */
public class TreeTypeDialog extends JDialog {

    public enum TreeType { BST, AVL }

    private TreeType selectedType = null;

    public TreeTypeDialog() {
        super((Frame) null, "Selecionar Tipo de Árvore", true);
        setUndecorated(true);
        setBackground(new Color(0, 0, 0, 0));

        JPanel root = buildRoot();
        setContentPane(root);
        pack();
        setLocationRelativeTo(null);
        setResizable(false);
    }

    private JPanel buildRoot() {
        JPanel root = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 14, 14);
                g2.setColor(Theme.BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 14, 14);
                g2.dispose();
            }
        };
        root.setOpaque(false);
        root.setPreferredSize(new Dimension(480, 320));
        root.setBorder(new EmptyBorder(32, 32, 28, 32));

        root.add(buildHeader(),  BorderLayout.NORTH);
        root.add(buildCards(),   BorderLayout.CENTER);
        root.add(buildFooter(),  BorderLayout.SOUTH);

        return root;
    }

    private JPanel buildHeader() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(0, 0, 24, 0));

        JLabel title = new JLabel("Escolha o tipo de árvore");
        title.setFont(Theme.ui(Font.PLAIN, 18));
        title.setForeground(Theme.TEXT_BRIGHT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Selecione a estrutura que deseja visualizar");
        sub.setFont(Theme.ui(Font.PLAIN, 12));
        sub.setForeground(Theme.TEXT_MUTED);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setBorder(new EmptyBorder(4, 0, 0, 0));

        p.add(title);
        p.add(sub);
        return p;
    }

    private JPanel buildCards() {
        JPanel p = new JPanel(new GridLayout(1, 2, 12, 0));
        p.setOpaque(false);

        p.add(buildCard(
            TreeType.BST,
            "BST",
            "Árvore Binária de Busca",
            "Inserção e remoção simples,\nsem rebalanceamento automático.",
            Theme.ACCENT_CYAN
        ));

        p.add(buildCard(
            TreeType.AVL,
            "AVL",
            "Árvore AVL",
            "Balanceamento automático após\ncada inserção e remoção.",
            Theme.ACCENT_GREEN
        ));

        return p;
    }

    private JPanel buildCard(TreeType type, String tag, String name, String desc, Color accent) {
        JPanel card = new JPanel(new BorderLayout()) {
            boolean hovered = false;

            {
                addMouseListener(new MouseAdapter() {
                    @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
                    @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
                    @Override public void mouseClicked(MouseEvent e) { select(type); }
                });
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = hovered ? new Color(255, 255, 255, 12) : Theme.BG_GLASS;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
                g2.setColor(hovered ? Theme.BORDER_LIGHT : Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 10, 10);
                // barra de acento no topo
                g2.setColor(accent);
                g2.fillRoundRect(0, 0, getWidth(), 3, 3, 3);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(20, 18, 20, 18));

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        JLabel tagLbl = new JLabel(tag);
        tagLbl.setFont(Theme.ui(Font.BOLD, 22));
        tagLbl.setForeground(accent);
        tagLbl.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel nameLbl = new JLabel(name);
        nameLbl.setFont(Theme.ui(Font.PLAIN, 13));
        nameLbl.setForeground(Theme.TEXT_BRIGHT);
        nameLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        nameLbl.setBorder(new EmptyBorder(4, 0, 10, 0));

        content.add(tagLbl);
        content.add(nameLbl);

        for (String line : desc.split("\n")) {
            JLabel l = new JLabel("· " + line);
            l.setFont(Theme.ui(Font.PLAIN, 11));
            l.setForeground(Theme.TEXT_MUTED);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            content.add(l);
        }

        card.add(content, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildFooter() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(20, 0, 0, 0));

        JLabel hint = new JLabel("Clique em um card para continuar");
        hint.setFont(Theme.ui(Font.PLAIN, 11));
        hint.setForeground(Theme.TEXT_DIM);
        p.add(hint);
        return p;
    }

    private void select(TreeType type) {
        this.selectedType = type;
        dispose();
    }

    /** Retorna o tipo selecionado, ou {@code null} se o diálogo foi fechado sem escolha. */
    public TreeType getSelectedType() {
        return selectedType;
    }
}
