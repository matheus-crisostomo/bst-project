package bst.ui.panels;

import bst.model.BST;
import bst.observer.BSTObserver;
import bst.theme.Theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.List;

public class TraversalPanel extends JPanel implements BSTObserver {

    private final BST    bst;
    private       JLabel typeLabel;
    private       JPanel outputPanel;

    public TraversalPanel(BST bst) {
        this.bst = bst;
        setBackground(Theme.BG_PANEL);
        setBorder(new MatteBorder(1, 0, 0, 0, Theme.BORDER));
        setPreferredSize(new Dimension(0, 52));
        setLayout(new BorderLayout());
        add(buildInner(), BorderLayout.CENTER);
        showPlaceholder();
    }

    private JPanel buildInner() {
        typeLabel = new JLabel("Traversal");
        typeLabel.setFont(Theme.ui(Font.PLAIN, 11));
        typeLabel.setForeground(Theme.TEXT_DIM);
        typeLabel.setBorder(new EmptyBorder(0, 20, 0, 14));

        outputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        outputPanel.setOpaque(false);

        JScrollPane scroll = new JScrollPane(outputPanel,
                JScrollPane.VERTICAL_SCROLLBAR_NEVER,
                JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);

        JPanel inner = new JPanel(new BorderLayout());
        inner.setOpaque(false);
        inner.setBorder(new EmptyBorder(9, 0, 9, 20));
        inner.add(typeLabel,  BorderLayout.WEST);
        inner.add(scroll,     BorderLayout.CENTER);
        return inner;
    }

    public void showTraversal(String type) {
        if (bst.root == null) return;

        List<Integer> values = switch (type) {
            case "inorder"    -> bst.inorder();
            case "preorder"   -> bst.preorder();
            case "postorder"  -> bst.postorder();
            case "levelorder" -> bst.levelorder();
            default           -> List.of();
        };

        String label = switch (type) {
            case "inorder"    -> "Em-Ordem";
            case "preorder"   -> "Pré-Ordem";
            case "postorder"  -> "Pós-Ordem";
            case "levelorder" -> "Por Nível";
            default           -> "Percurso";
        };

        typeLabel.setText(label);
        typeLabel.setForeground(Theme.TEXT_MUTED);
        renderValues(values);
    }

    private void renderValues(List<Integer> values) {
        outputPanel.removeAll();

        for (int i = 0; i < values.size(); i++) {
            if (i > 0) {
                JLabel arrow = new JLabel("→");
                arrow.setFont(Theme.ui(Font.PLAIN, 11));
                arrow.setForeground(Theme.TEXT_DIM);
                outputPanel.add(arrow);
            }

            JLabel chip = new JLabel(String.valueOf(values.get(i))) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(Theme.BG_GLASS2);
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                    g2.setColor(Theme.BORDER);
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 5, 5);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            chip.setFont(Theme.ui(Font.PLAIN, 12));
            chip.setForeground(Theme.TEXT_BRIGHT);
            chip.setOpaque(false);
            chip.setBorder(new EmptyBorder(3, 8, 3, 8));
            outputPanel.add(chip);
        }

        outputPanel.revalidate();
        outputPanel.repaint();
    }

    private void showPlaceholder() {
        typeLabel.setText("Percurso");
        typeLabel.setForeground(Theme.TEXT_DIM);
        outputPanel.removeAll();

        JLabel placeholder = new JLabel("Selecione um tipo de percurso acima");
        placeholder.setFont(Theme.ui(Font.PLAIN, 11));
        placeholder.setForeground(Theme.TEXT_DIM);
        outputPanel.add(placeholder);

        outputPanel.revalidate();
        outputPanel.repaint();
    }

    @Override public void onTreeChanged()       { SwingUtilities.invokeLater(this::showPlaceholder); }
    @Override public void onNodeInserted(int v) {}
    @Override public void onNodeRemoved(int v)  {}
}
