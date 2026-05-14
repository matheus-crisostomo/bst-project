package bst.observer;

import bst.model.RotationType;

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

    /**
     * Chamado quando uma rotação AVL é executada durante o balanceamento.
     * Implementação padrão vazia para observers que não precisam reagir.
     *
     * @param type tipo da rotação realizada
     * @param pivotVal valor do nó pivô da rotação
     */
    default void onRotation(RotationType type, int pivotVal) {}
}
