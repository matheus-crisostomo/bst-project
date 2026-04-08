package bst.ui;

import bst.controller.BSTController;
import bst.io.TreeFileReader;
import bst.io.TreeFileWriter;
import bst.model.BST;
import bst.model.BSTAnalyzer;
import bst.renderer.TreeRenderer;
import bst.theme.Theme;
import bst.ui.panels.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class BSTVisualizer extends JFrame {

    private final BST            bst;
    private final BSTController  controller;
    private final BSTAnalyzer    analyzer;
    private final TreeRenderer   renderer;
    private final TreeFileWriter fileWriter;
    private final TreeFileReader fileReader;

    private HeaderPanel    headerPanel;
    private ControlPanel   controlPanel;
    private TreePanel      treePanel;
    private InfoPanel      infoPanel;
    private TraversalPanel traversalPanel;

    private JLabel toastLabel;
    private Timer  toastTimer;

    public BSTVisualizer() {
        super("BST Visualizer");

        bst        = new BST();
        controller = new BSTController(bst);
        analyzer   = new BSTAnalyzer();
        renderer   = new TreeRenderer();
        fileWriter = new TreeFileWriter(analyzer);
        fileReader = new TreeFileReader();

        configureFrame();
        buildPanels();
        wirePanels();
        registerObservers();

        setVisible(true);
    }

    private void configureFrame() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1300, 800);
        setMinimumSize(new Dimension(960, 580));
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.setBackground(Theme.BG_DARK);
        setContentPane(root);
    }

    private void buildPanels() {
        headerPanel    = new HeaderPanel(bst);
        controlPanel   = new ControlPanel(controller);
        treePanel      = new TreePanel(bst, renderer);
        infoPanel      = new InfoPanel(bst, analyzer);
        traversalPanel = new TraversalPanel(bst);

        JScrollPane canvasScroll = new JScrollPane(treePanel);
        canvasScroll.setBorder(null);
        canvasScroll.setBackground(Theme.BG_DARK);
        canvasScroll.getViewport().setBackground(Theme.BG_DARK);

        JPanel center = new JPanel(new BorderLayout());
        center.setBackground(Theme.BG_DARK);
        center.add(controlPanel, BorderLayout.NORTH);
        center.add(canvasScroll, BorderLayout.CENTER);

        JPanel root = (JPanel) getContentPane();
        root.add(headerPanel,    BorderLayout.NORTH);
        root.add(center,         BorderLayout.CENTER);
        root.add(infoPanel,      BorderLayout.EAST);
        root.add(traversalPanel, BorderLayout.SOUTH);

        buildToast();
    }

    private void wirePanels() {
        controlPanel.setToastCallback(this::showToast);

        controlPanel.setTraversalCallback(type -> {
            if (bst.root == null) { showToast("Árvore está vazia", "error"); return; }
            traversalPanel.showTraversal(type);
        });

        controlPanel.setSaveCallback(this::doSave);
        controlPanel.setLoadCallback(this::doLoad);

        treePanel.setOnNodeSelected(val -> {
            controlPanel.setInputValue(String.valueOf(val));
            infoPanel.setSelectedNode(val);
        });
    }

    private void registerObservers() {
        controller.addObserver(headerPanel);
        controller.addObserver(treePanel);
        controller.addObserver(traversalPanel);
        controller.addObserver(infoPanel);
    }

    // ── Save / Load ───────────────────────────────────────────────────────────

    private void doSave() {
        if (bst.root == null) { showToast("Árvore vazia — nada para salvar", "error"); return; }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Salvar Árvore");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo de texto (*.txt)", "txt"));
        chooser.setSelectedFile(new File("arvore_bst.txt"));

        if (chooser.showSaveDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.getName().toLowerCase().endsWith(".txt"))
            file = new File(file.getAbsolutePath() + ".txt");

        if (file.exists()) {
            int c = JOptionPane.showConfirmDialog(this,
                    "Arquivo já existe. Substituir?", "Confirmar", JOptionPane.YES_NO_OPTION);
            if (c != JOptionPane.YES_OPTION) return;
        }

        try {
            fileWriter.saveToFile(fileWriter.generateContent(bst), file);
            showToast("Salvo: " + file.getName(), "success");
        } catch (IOException ex) {
            showToast("Erro ao salvar: " + ex.getMessage(), "error");
        }
    }

    private void doLoad() {
        if (bst.root != null) {
            int c = JOptionPane.showConfirmDialog(this,
                    "Isso substituirá a árvore atual. Continuar?",
                    "Confirmar", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
            if (c != JOptionPane.YES_OPTION) return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Carregar Árvore");
        chooser.setFileFilter(new FileNameExtensionFilter("Arquivo de texto (*.txt)", "txt"));

        if (chooser.showOpenDialog(this) != JFileChooser.APPROVE_OPTION) return;

        File file = chooser.getSelectedFile();
        if (!file.exists()) { showToast("Arquivo não encontrado", "error"); return; }

        try {
            fileReader.loadFromFile(file, bst);
            headerPanel.onTreeChanged();
            treePanel.onTreeChanged();
            traversalPanel.onTreeChanged();
            infoPanel.onTreeChanged();
            showToast(bst.size() + " nós carregados de " + file.getName(), "success");
        } catch (TreeFileReader.ParseException ex) {
            showToast("Formato de arquivo inválido", "error");
        } catch (IOException ex) {
            showToast("Erro de leitura: " + ex.getMessage(), "error");
        }
    }

    // ── Toast ─────────────────────────────────────────────────────────────────

    private void buildToast() {
        toastLabel = new JLabel("", SwingConstants.CENTER) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.setColor(Theme.BORDER_LIGHT);
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        toastLabel.setFont(Theme.ui(Font.PLAIN, 12));
        toastLabel.setOpaque(false);
        toastLabel.setVisible(false);

        toastTimer = new Timer(2600, e -> toastLabel.setVisible(false));
        toastTimer.setRepeats(false);
        getLayeredPane().add(toastLabel, JLayeredPane.POPUP_LAYER);
    }

    private void showToast(String message, String type) {
        Color fg = switch (type) {
            case "success" -> Theme.ACCENT_GREEN;
            case "error"   -> Theme.ACCENT_ORANGE;
            default        -> Theme.TEXT_MUTED;
        };
        Color bg = new Color(18, 18, 18, 230);

        toastLabel.setText(message);
        toastLabel.setForeground(fg);
        toastLabel.setBackground(bg);

        int w = 320, h = 34;
        toastLabel.setBounds(getLayeredPane().getWidth() - w - 16, 68, w, h);
        toastLabel.setVisible(true);
        toastTimer.restart();
    }

    @Override public void doLayout() {
        super.doLayout();
        if (toastLabel != null && toastLabel.isVisible()) {
            int w = 320, h = 34;
            toastLabel.setBounds(getLayeredPane().getWidth() - w - 16, 68, w, h);
        }
    }
}
