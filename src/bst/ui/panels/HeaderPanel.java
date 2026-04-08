package bst.ui.panels;

import bst.model.BST;
import bst.observer.BSTObserver;
import bst.theme.Theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;

public class HeaderPanel extends JPanel implements BSTObserver {

    private final BST    bst;
    private       JLabel statNodesVal;
    private       JLabel statHeightVal;
    private       JLabel statRootVal;

    public HeaderPanel(BST bst) {
        this.bst = bst;
        setBackground(Theme.BG_PANEL);
        setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        setPreferredSize(new Dimension(0, 60));
        setLayout(new BorderLayout());
        add(buildLogo(),  BorderLayout.WEST);
        add(buildStats(), BorderLayout.EAST);
        refreshStats();
    }

    private JPanel buildLogo() {
        JPanel logo = new JPanel(new FlowLayout(FlowLayout.LEFT, 14, 0));
        logo.setOpaque(false);
        logo.setBorder(new EmptyBorder(0, 24, 0, 0));
        logo.add(buildLogoIcon());
        logo.add(buildLogoText());
        return logo;
    }

    private JLabel buildLogoIcon() {
        JLabel icon = new JLabel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setStroke(new BasicStroke(1.5f));

                // raiz
                g2.setColor(new Color(255, 255, 255, 30));
                g2.fillOval(13, 2, 14, 14);
                g2.setColor(new Color(255, 255, 255, 80));
                g2.drawOval(13, 2, 14, 14);

                // filhos
                g2.setColor(new Color(255, 255, 255, 18));
                g2.fillOval(2, 22, 12, 12);
                g2.fillOval(26, 22, 12, 12);
                g2.setColor(new Color(255, 255, 255, 50));
                g2.drawOval(2, 22, 12, 12);
                g2.drawOval(26, 22, 12, 12);

                // arestas
                g2.setColor(new Color(255, 255, 255, 30));
                g2.drawLine(17, 16, 8,  22);
                g2.drawLine(23, 16, 32, 22);
            }
        };
        icon.setPreferredSize(new Dimension(42, 38));
        return icon;
    }

    private JPanel buildLogoText() {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        p.setOpaque(false);

        JLabel bst = new JLabel("BST");
        bst.setFont(Theme.ui(Font.BOLD, 15));
        bst.setForeground(Theme.TEXT_BRIGHT);

        JLabel viz = new JLabel("Visualizador");
        viz.setFont(Theme.ui(Font.PLAIN, 15));
        viz.setForeground(Theme.TEXT_MUTED);

        p.add(bst);
        p.add(viz);
        return p;
    }

    private JPanel buildStats() {
        JPanel stats = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        stats.setOpaque(false);
        stats.setBorder(new EmptyBorder(0, 0, 0, 24));

        statNodesVal  = addStatChip(stats, "0",  "nós");
        statHeightVal = addStatChip(stats, "—",  "altura");
        statRootVal   = addStatChip(stats, "—",  "raiz");
        return stats;
    }

    private JLabel addStatChip(JPanel parent, String value, String label) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_GLASS);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setOpaque(false);
        chip.setBorder(new EmptyBorder(6, 12, 6, 12));

        JLabel valLbl = new JLabel(value);
        valLbl.setFont(Theme.ui(Font.BOLD, 13));
        valLbl.setForeground(Theme.TEXT_BRIGHT);

        JLabel lblLbl = new JLabel(label);
        lblLbl.setFont(Theme.ui(Font.PLAIN, 12));
        lblLbl.setForeground(Theme.TEXT_MUTED);

        chip.add(valLbl);
        chip.add(lblLbl);
        parent.add(chip);
        return valLbl;
    }

    private void refreshStats() {
        statNodesVal.setText(String.valueOf(bst.size()));
        statHeightVal.setText(bst.root == null ? "—" : String.valueOf(bst.height()));
        statRootVal.setText(bst.root == null ? "—" : String.valueOf(bst.root.val));
    }

    @Override public void onTreeChanged()       { SwingUtilities.invokeLater(this::refreshStats); }
    @Override public void onNodeInserted(int v) {}
    @Override public void onNodeRemoved(int v)  {}
}
