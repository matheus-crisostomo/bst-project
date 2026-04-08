package bst.observer;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   BSTObserver — Interface do padrão Observer
 *   Define o contrato de notificação para mudanças na BST
 * ╚══════════════════════════════════════════════════════╝
 *
 * Padrão: Observer (GoF)
 * Qualquer componente que precise reagir a mudanças na
 * árvore deve implementar esta interface e se registrar
 * via BST.addObserver(BSTObserver).
 */
public interface BSTObserver {

    /**
     * Chamado sempre que a estrutura da árvore muda
     * (inserção, remoção ou limpeza).
     */
    void onTreeChanged();

    /**
     * Chamado após uma inserção bem-sucedida.
     * @param val valor do nó inserido
     */
    void onNodeInserted(int val);

    /**
     * Chamado após uma remoção bem-sucedida.
     * @param val valor do nó removido
     */
    void onNodeRemoved(int val);
}
