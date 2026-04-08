package bst.ui.panels;

import bst.model.BST;
import bst.observer.BSTObserver;
import bst.renderer.TreeRenderer;
import bst.theme.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   TreePanel — Canvas de Renderização da Árvore
 *   Componente Swing que exibe a BST graficamente
 * ╚══════════════════════════════════════════════════════╝
 *
 * Padrão: Observer (BSTObserver) — redesenha automaticamente
 * quando a árvore muda. Delega toda lógica de desenho
 * ao TreeRenderer (Separation of Concerns).
 */
public class TreePanel extends JPanel implements BSTObserver {

    private final BST          bst;
    private final TreeRenderer renderer;

    /** Callback chamado quando o usuário clica em um nó. */
    private Consumer<Integer> onNodeSelected = val -> {};

    public TreePanel(BST bst, TreeRenderer renderer) {
        this.bst      = bst;
        this.renderer = renderer;

        setBackground(Theme.BG_DARK);
        addMouseListener(buildMouseListener());
    }

    // ── Setter de Callback ───────────────────────────────────────────────────

    /**
     * Define o callback acionado quando o usuário clica num nó.
     * Útil para preencher o campo de input no ControlPanel.
     *
     * @param cb Consumer que recebe o valor do nó clicado
     */
    public void setOnNodeSelected(Consumer<Integer> cb) {
        this.onNodeSelected = cb;
    }

    // ── Renderização ─────────────────────────────────────────────────────────

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;

        // Atribui posições antes de renderizar
        renderer.assignPositions(bst.root);

        // Ajusta o tamanho preferido com base na árvore atual
        Dimension needed = renderer.calcCanvasSize(
                bst.root,
                getParent() != null ? getParent().getWidth()  : 600,
                getParent() != null ? getParent().getHeight() : 400
        );
        if (!getPreferredSize().equals(needed)) {
            setPreferredSize(needed);
            revalidate();
        }

        renderer.render(g2, bst.root, getWidth(), getHeight());
    }

    // ── Interação com Mouse ──────────────────────────────────────────────────

    private MouseAdapter buildMouseListener() {
        return new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (bst.root == null) return;
                int val = renderer.getNodeAt(bst.root, e.getX(), e.getY(), getWidth());
                if (val != Integer.MIN_VALUE) {
                    renderer.setHighlightedValue(val);
                    onNodeSelected.accept(val);
                    repaint();
                }
            }
        };
    }

    // ── BSTObserver ──────────────────────────────────────────────────────────

    @Override
    public void onTreeChanged() {
        SwingUtilities.invokeLater(this::repaint);
    }

    @Override
    public void onNodeInserted(int val) {
        renderer.setLastInsertedValue(val);
        // Remove o destaque de "novo nó" após 700ms
        Timer timer = new Timer(700, e -> {
            renderer.clearLastInserted();
            repaint();
        });
        timer.setRepeats(false);
        timer.start();
    }

    @Override
    public void onNodeRemoved(int val) {
        renderer.clearHighlight();
    }
}
