package bst.ui.panels;

import bst.model.BST;
import bst.model.RotationType;
import bst.observer.BSTObserver;
import bst.renderer.TreeRenderer;
import bst.theme.Theme;
import java.awt.*;
import java.awt.event.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;

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

    /** Callback para exibir toast com informação da rotação. */
    private BiConsumer<String, String> rotationToastCallback = (m, t) -> {};

    /** Timer da animação de rotação. */
    private Timer rotationAnimTimer;
    private static final int ROTATION_ANIM_DURATION_MS = 900;
    private static final int ROTATION_ANIM_FPS = 40;

    /** Estado pendente de rotação (capturado em onRotation, consumido em onTreeChanged). */
    private RotationType pendingRotationType = null;
    private int pendingRotationPivot = Integer.MIN_VALUE;

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

    /**
     * Define o callback para exibir toast com informação da rotação.
     *
     * @param cb BiConsumer(mensagem, tipo)
     */
    public void setRotationToastCallback(BiConsumer<String, String> cb) {
        this.rotationToastCallback = cb;
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
        SwingUtilities.invokeLater(() -> {
            if (pendingRotationType != null) {
                startRotationAnimations();
            }
            repaint();
        });
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

    @Override
    public void onRotation(RotationType type, int pivotVal) {
        // Captura posições ANTES da rotação modificar a árvore.
        // Se já há uma rotação pendente (cascata), mantém o snapshot original
        // mas atualiza o tipo/pivô para a última rotação da cascata.
        if (pendingRotationType == null) {
            int canvasW = getParent() != null ? getParent().getWidth() : getWidth();
            renderer.snapshotPositions(bst.root, canvasW);
        }
        pendingRotationType = type;
        pendingRotationPivot = pivotVal;
    }

    /**
     * Inicia as animações de rotação (arco + transição de nós) após a árvore mudar.
     * Chamado pelo onTreeChanged quando há uma rotação pendente.
     */
    private void startRotationAnimations() {
        RotationType type = pendingRotationType;
        int pivotVal = pendingRotationPivot;
        pendingRotationType = null;
        pendingRotationPivot = Integer.MIN_VALUE;

        if (type == null) return;

        // Determina direção do arco baseado no tipo de rotação
        boolean clockwise = (type == RotationType.LEFT_LEFT || type == RotationType.RIGHT_LEFT);

        renderer.startRotationAnimation(pivotVal, type.getShortName(), clockwise);
        renderer.startTransition();

        // Exibe toast com descrição da rotação
        rotationToastCallback.accept(type.getDescription() + " no nó " + pivotVal, "info");

        // Animação com timer
        if (rotationAnimTimer != null && rotationAnimTimer.isRunning()) {
            rotationAnimTimer.stop();
            renderer.clearRotationAnimation();
            renderer.clearTransition();
        }

        long startTime = System.currentTimeMillis();
        int interval = 1000 / ROTATION_ANIM_FPS;

        rotationAnimTimer = new Timer(interval, e -> {
            long elapsed = System.currentTimeMillis() - startTime;
            float progress = (float) elapsed / ROTATION_ANIM_DURATION_MS;

            if (progress >= 1f) {
                renderer.clearRotationAnimation();
                renderer.clearTransition();
                ((Timer) e.getSource()).stop();
            } else {
                // Ease-out cubic para suavidade
                float eased = 1f - (1f - progress) * (1f - progress) * (1f - progress);
                renderer.setRotationProgress(eased);
                renderer.setTransitionProgress(eased);
            }
            repaint();
        });
        rotationAnimTimer.setRepeats(true);
        rotationAnimTimer.start();
    }
}
