import bst.ui.BSTVisualizer;
import bst.ui.TreeTypeDialog;
import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        // Define o look & feel do sistema operacional para melhor integração
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Fallback para o L&F padrão do Swing
        }

        // Toda criação de UI deve ocorrer na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            TreeTypeDialog dialog = new TreeTypeDialog();
            dialog.setVisible(true);

            TreeTypeDialog.TreeType type = dialog.getSelectedType();
            if (type == null) System.exit(0); // fechou sem escolher

            new BSTVisualizer(type);
        });
    }
}
