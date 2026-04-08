package bst.model;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   BSTNode — Nó da Árvore Binária de Busca
 *   Representa um único nó da estrutura de dados
 * ╚══════════════════════════════════════════════════════╝
 *
 * Responsabilidade única: armazenar o valor do nó,
 * referências aos filhos e posição de layout para o renderer.
 */
public class BSTNode {

    /** Valor armazenado neste nó. */
    public int val;

    /** Filho esquerdo (valores menores). */
    public BSTNode left;

    /** Filho direito (valores maiores). */
    public BSTNode right;

    /**
     * Coluna na grade de layout (atribuída pelo TreeRenderer
     * via travessia in-order — não faz parte da lógica BST).
     */
    public int gridX;

    /**
     * Linha na grade de layout — equivale à profundidade do nó.
     */
    public int gridY;

    /**
     * Cria um novo nó com o valor informado.
     * @param val valor a armazenar
     */
    public BSTNode(int val) {
        this.val = val;
    }

    @Override
    public String toString() {
        return "BSTNode{val=" + val + "}";
    }
}
