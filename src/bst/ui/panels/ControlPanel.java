package bst.ui.panels;

import bst.controller.BSTController;
import bst.controller.BSTController.InsertResult;
import bst.controller.BSTController.RemoveResult;
import bst.theme.Theme;
import java.awt.*;
import java.awt.event.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.swing.*;
import javax.swing.border.*;

public class ControlPanel extends JPanel {

    private final BSTController controller;
    private JTextField inputField;

    private BiConsumer<String, String> toastCallback     = (m, t) -> {};
    private Consumer<String>           traversalCallback = t -> {};
    private Runnable                   saveCallback      = () -> {};
    private Runnable                   loadCallback      = () -> {};

    public ControlPanel(BSTController controller) {
        this.controller = controller;
        setBackground(Theme.BG_PANEL);
        setBorder(new MatteBorder(0, 0, 1, 0, Theme.BORDER));
        setLayout(new FlowLayout(FlowLayout.LEFT, 8, 10));
        buildComponents();
    }

    public void setToastCallback(BiConsumer<String, String> cb)  { this.toastCallback     = cb; }
    public void setTraversalCallback(Consumer<String>       cb)  { this.traversalCallback = cb; }
    public void setSaveCallback(Runnable                    cb)  { this.saveCallback      = cb; }
    public void setLoadCallback(Runnable                    cb)  { this.loadCallback      = cb; }

    public void setInputValue(String val) {
        SwingUtilities.invokeLater(() -> inputField.setText(val));
    }

    private void buildComponents() {
        inputField = buildInputField();

        JButton btnInsert = buildPrimaryBtn("Inserir");
        JButton btnRemove = buildGhostBtn("Remover");
        JButton btnClear  = buildGhostBtn("Limpar");
        JButton btnMirror = buildGhostBtn("Espelhar");

        add(inputField);
        add(btnInsert);
        add(btnRemove);
        add(btnClear);
        add(btnMirror);
        add(buildDivider());

        JLabel fileLabel = new JLabel("Arquivo");
        fileLabel.setFont(Theme.ui(Font.PLAIN, 11));
        fileLabel.setForeground(Theme.TEXT_DIM);
        add(fileLabel);

        JButton btnSave = buildGhostBtn("Salvar");
        JButton btnLoad = buildGhostBtn("Carregar");
        add(btnSave);
        add(btnLoad);
        add(buildDivider());

        JLabel travLabel = new JLabel("Percurso");
        travLabel.setFont(Theme.ui(Font.PLAIN, 11));
        travLabel.setForeground(Theme.TEXT_DIM);
        add(travLabel);

        add(buildTravBtn("Em-Ordem",    "inorder"));
        add(buildTravBtn("Pré-Ordem",   "preorder"));
        add(buildTravBtn("Pós-Ordem",   "postorder"));
        add(buildTravBtn("Por Nível",   "levelorder"));

        btnInsert.addActionListener(e -> doInsert());
        btnRemove.addActionListener(e -> doRemove());
        btnClear.addActionListener(e  -> doClear());
        btnMirror.addActionListener(e -> doMirror());
        btnSave.addActionListener(e   -> saveCallback.run());
        btnLoad.addActionListener(e   -> loadCallback.run());

        inputField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doInsert();
            }
        });
    }

    private void doInsert() {
        InsertResult r = controller.insert(inputField.getText());
        if (r.isSuccess()) {
            inputField.setText(""); inputField.requestFocus();
            toastCallback.accept(r.message, "success");
        } else {
            toastCallback.accept(r.message, "error");
        }
    }

    private void doRemove() {
        RemoveResult r = controller.remove(inputField.getText());
        if (r.isSuccess()) {
            inputField.setText(""); inputField.requestFocus();
            toastCallback.accept(r.message, "info");
        } else {
            toastCallback.accept(r.message, "error");
        }
    }

    private void doClear() {
        controller.clear();
        inputField.setText(""); inputField.requestFocus();
        toastCallback.accept("Árvore limpa", "info");
    }

    private void doMirror() {
        controller.mirror();
        toastCallback.accept("Árvore espelhada", "info");
    }

    // ── Componentes ──────────────────────────────────────────────────────────

    private JTextField buildInputField() {
        JTextField field = new JTextField(7) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_SURFACE);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.setColor(Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        field.setFont(Theme.ui(Font.PLAIN, 13));
        field.setForeground(Theme.TEXT_BRIGHT);
        field.setCaretColor(Theme.TEXT_MUTED);
        field.setBackground(new Color(0, 0, 0, 0));
        field.setOpaque(false);
        field.setBorder(new EmptyBorder(6, 10, 6, 10));
        field.setPreferredSize(new Dimension(110, 34));
        field.setHorizontalAlignment(JTextField.LEFT);
        return field;
    }

    private JButton buildPrimaryBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? new Color(220, 220, 220)
                         : getModel().isRollover() ? new Color(240, 240, 240)
                         : Color.WHITE;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.ui(Font.PLAIN, 13));
        btn.setForeground(Color.BLACK);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 14, 0, 14));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 8, 34));
        return btn;
    }

    private JButton buildGhostBtn(String text) {
        JButton btn = new JButton(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = getModel().isPressed() ? new Color(255, 255, 255, 14)
                         : getModel().isRollover() ? new Color(255, 255, 255, 10)
                         : Theme.BG_GLASS;
                g2.setColor(bg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 7, 7);
                g2.setColor(getModel().isRollover() ? Theme.BORDER_LIGHT : Theme.BORDER);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 7, 7);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        btn.setFont(Theme.ui(Font.PLAIN, 13));
        btn.setForeground(Theme.TEXT_BRIGHT);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        btn.setMargin(new Insets(0, 14, 0, 14));
        btn.setPreferredSize(new Dimension(btn.getPreferredSize().width + 8, 34));
        return btn;
    }

    private JButton buildTravBtn(String label, String type) {
        JButton btn = buildGhostBtn(label);
        btn.setFont(Theme.ui(Font.PLAIN, 12));
        btn.setForeground(Theme.TEXT_MUTED);
        btn.setPreferredSize(new Dimension(100, 30));
        btn.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { btn.setForeground(Theme.TEXT_BRIGHT); }
            @Override public void mouseExited(MouseEvent e)  { btn.setForeground(Theme.TEXT_MUTED);  }
        });
        btn.addActionListener(e -> traversalCallback.accept(type));
        return btn;
    }

    private JSeparator buildDivider() {
        JSeparator sep = new JSeparator(JSeparator.VERTICAL);
        sep.setForeground(Theme.BORDER);
        sep.setPreferredSize(new Dimension(1, 22));
        return sep;
    }
}
