package bst.ui.panels;

import bst.model.RotationType;
import bst.observer.BSTObserver;
import bst.theme.Theme;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class HistoryPanel extends JPanel implements BSTObserver {

    private static final int MAX_ENTRIES = 200;

    private final List<String> history = new ArrayList<>();
    private final JPanel entriesPanel;
    private final JScrollPane scrollPane;
    private JLabel countLabel;

    // Contexto temporário para agrupar rotações com a operação que as causou
    private String pendingOperation = null;
    private final List<String> pendingRotations = new ArrayList<>();

    public HistoryPanel() {
        setOpaque(false);
        setBorder(new EmptyBorder(10, 16, 10, 16));
        setPreferredSize(new Dimension(0, 160));
        setLayout(new BorderLayout());

        JPanel container = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_PANEL);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 16, 16);
                g2.setColor(new Color(255, 255, 255, 10)); // inner light
                g2.drawRoundRect(1, 1, getWidth() - 3, getHeight() - 3, 16, 16);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 16, 16);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        container.setOpaque(false);

        container.add(buildHeader(), BorderLayout.NORTH);

        entriesPanel = new JPanel();
        entriesPanel.setLayout(new BoxLayout(entriesPanel, BoxLayout.Y_AXIS));
        entriesPanel.setOpaque(false);

        scrollPane = new JScrollPane(entriesPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUI(new bst.theme.ModernScrollBarUI());
        scrollPane.getVerticalScrollBar().setUnitIncrement(10);
        scrollPane.getVerticalScrollBar().setOpaque(false);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        container.add(scrollPane, BorderLayout.CENTER);

        add(container, BorderLayout.CENTER);

        showPlaceholder();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(8, 16, 6, 16));

        JLabel title = new JLabel("Histórico");
        title.setFont(Theme.ui(Font.PLAIN, 11));
        title.setForeground(Theme.TEXT_DIM);

        countLabel = new JLabel("0 operações");
        countLabel.setFont(Theme.ui(Font.PLAIN, 10));
        countLabel.setForeground(Theme.TEXT_DIM);

        JButton clearBtn = new JButton("Limpar") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (getModel().isRollover()) {
                    g2.setColor(new Color(255, 255, 255, 10));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 5, 5);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        clearBtn.setFont(Theme.ui(Font.PLAIN, 10));
        clearBtn.setForeground(Theme.TEXT_DIM);
        clearBtn.setContentAreaFilled(false);
        clearBtn.setBorderPainted(false);
        clearBtn.setFocusPainted(false);
        clearBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        clearBtn.setMargin(new Insets(2, 6, 2, 6));
        clearBtn.addActionListener(e -> clearHistory());

        JPanel right = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        right.setOpaque(false);
        right.add(countLabel);
        right.add(clearBtn);

        header.add(title, BorderLayout.WEST);
        header.add(right, BorderLayout.EAST);
        return header;
    }


    @Override
    public void onNodeInserted(int val) {
        pendingOperation = "INSERT:" + val;
    }

    @Override
    public void onNodeRemoved(int val) {
        pendingOperation = "REMOVE:" + val;
    }

    @Override
    public void onRotation(RotationType type, int pivotVal) {
        pendingRotations.add(type.getShortName() + " no nó " + pivotVal);
    }

    @Override
    public void onTreeChanged() {
        SwingUtilities.invokeLater(this::flushPending);
    }


    private void flushPending() {
        if (pendingOperation == null) {
            // Operação sem insert/remove (clear, mirror, load)
            pendingRotations.clear();
            return;
        }

        String[] parts = pendingOperation.split(":");
        String op = parts[0];
        String val = parts[1];

        StringBuilder entry = new StringBuilder();
        if ("INSERT".equals(op)) {
            entry.append("+ Inserido ").append(val);
        } else {
            entry.append("− Removido ").append(val);
        }

        if (!pendingRotations.isEmpty()) {
            entry.append("  ⟳ ");
            entry.append(String.join(", ", pendingRotations));
        }

        addEntry(entry.toString());

        pendingOperation = null;
        pendingRotations.clear();
    }

    private void addEntry(String text) {
        if (history.size() >= MAX_ENTRIES) {
            history.remove(0);
        }
        history.add(text);

        // Rebuild visual
        rebuildEntries();
    }

    private void rebuildEntries() {
        entriesPanel.removeAll();

        // Mostra do mais recente para o mais antigo
        for (int i = history.size() - 1; i >= 0; i--) {
            String entry = history.get(i);
            JLabel lbl = createEntryLabel(entry);
            entriesPanel.add(lbl);
        }

        countLabel.setText(history.size() + " operaç" + (history.size() == 1 ? "ão" : "ões"));

        entriesPanel.revalidate();
        entriesPanel.repaint();

        // Scroll para o topo (mais recente)
        SwingUtilities.invokeLater(() -> scrollPane.getVerticalScrollBar().setValue(0));
    }

    private JLabel createEntryLabel(String text) {
        JLabel lbl = new JLabel(text);
        lbl.setFont(Theme.mono(Font.PLAIN, 11));
        lbl.setBorder(new EmptyBorder(3, 16, 3, 16));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        lbl.setMaximumSize(new Dimension(Integer.MAX_VALUE, 22));

        // Colorir baseado no tipo
        if (text.startsWith("+")) {
            lbl.setForeground(Theme.ACCENT_GREEN);
        } else if (text.startsWith("−")) {
            lbl.setForeground(Theme.ACCENT_ORANGE);
        } else {
            lbl.setForeground(Theme.TEXT_MUTED);
        }

        // Rotações em destaque
        if (text.contains("⟳")) {
            lbl.setToolTipText(extractRotationTooltip(text));
        }

        return lbl;
    }

    private String extractRotationTooltip(String text) {
        int idx = text.indexOf("⟳");
        if (idx >= 0) {
            return "Rotações aplicadas: " + text.substring(idx + 2).trim();
        }
        return null;
    }

    private void showPlaceholder() {
        entriesPanel.removeAll();
        JLabel placeholder = new JLabel("Operações aparecerão aqui");
        placeholder.setFont(Theme.ui(Font.PLAIN, 11));
        placeholder.setForeground(Theme.TEXT_DIM);
        placeholder.setBorder(new EmptyBorder(10, 16, 10, 16));
        placeholder.setAlignmentX(Component.LEFT_ALIGNMENT);
        entriesPanel.add(placeholder);
        entriesPanel.revalidate();
        entriesPanel.repaint();
    }

    /**
     * Limpa todo o histórico de operações.
     */
    public void clearHistory() {
        history.clear();
        countLabel.setText("0 operações");
        showPlaceholder();
    }
}
