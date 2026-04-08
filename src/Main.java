import bst.ui.BSTVisualizer;

import javax.swing.*;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   Main — Ponto de Entrada do Sistema
 *   Árvore Binária de Busca — Visualizador Gráfico
 * ╚══════════════════════════════════════════════════════╝
 *
 * Como compilar e executar:
 *
 *   Linux / macOS:
 *     chmod +x compile.sh run.sh
 *     ./compile.sh && ./run.sh
 *
 *   Windows:
 *     compile.bat
 *     run.bat
 *
 *   Manualmente:
 *     javac -d out $(find src -name "*.java")  [Linux/macOS]
 *     java  -cp out Main
 */
public class Main {

    public static void main(String[] args) {
        // Define o look & feel do sistema operacional para melhor integração
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback para o L&F padrão do Swing
        }

        // Toda criação de UI deve ocorrer na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(BSTVisualizer::new);
    }
}
