package bst.model;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   BSTAnalyzer — Analisador Completo da BST
 *   Métricas, tipos, percursos e caminhos
 * ╚══════════════════════════════════════════════════════╝
 *
 * ┌──────────────────────────────────────────────────────┐
 *  TABELA DE REFERÊNCIA DE MÉTRICAS (DEFINIÇÕES CORRETAS)
 * ├─────────────────┬────────────────────────────────────┤
 *  NÍVEL do nó      │ Raiz = 1. Filho da raiz = 2.       │
 *                   │ Fórmula: profundidade + 1          │
 * ├─────────────────┼────────────────────────────────────┤
 *  PROFUNDIDADE nó  │ Raiz = 0. Conta ARESTAS até raiz.  │
 *                   │ Fórmula: nível - 1                 │
 * ├─────────────────┼────────────────────────────────────┤
 *  ALTURA do nó     │ Folha = 0. Conta ARESTAS até folha │
 *                   │ mais distante.                     │
 * ├─────────────────┼────────────────────────────────────┤
 *  ALTURA da árvore │ = altura da raiz.                  │
 *                   │ Árvore vazia = -1. Raiz só = 0.    │
 * ├─────────────────┼────────────────────────────────────┤
 *  TOTAL DE NÍVEIS  │ = altura da árvore + 1             │
 *                   │ Árvore vazia = 0. Raiz só = 1.     │
 * └─────────────────┴────────────────────────────────────┘
 */
public class BSTAnalyzer {

    // ═════════════════════════════════════════════════════════════════════════
    //  MÉTRICAS DO NÓ
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Nível do nó = profundidade + 1.
     * Raiz = 1. Filho direto da raiz = 2. Etc.
     *
     * @return nível (≥ 1) se encontrado; -1 se não encontrado
     */
    public int getNodeLevel(BSTNode root, int val) {
        int depth = getNodeDepth(root, val);
        return (depth == -1) ? -1 : depth + 1;
    }

    /**
     * Profundidade do nó = número de ARESTAS da raiz até o nó.
     * Raiz = 0. Filho da raiz = 1. Etc.
     *
     * @return profundidade (≥ 0) se encontrado; -1 se não encontrado
     */
    public int getNodeDepth(BSTNode root, int val) {
        return findDepth(root, val, 0);
    }

    /**
     * Altura do nó = número de ARESTAS no caminho mais longo do nó até uma folha.
     * Folha = 0. Pai de uma folha = 1. Etc.
     *
     * @return altura (≥ 0) se encontrado; -1 se não encontrado
     */
    public int getNodeHeight(BSTNode root, int val) {
        BSTNode node = findNode(root, val);
        return (node == null) ? -1 : computeHeight(node);
    }

    /**
     * Busca recursiva da profundidade. Navega pela BST comparando valores.
     */
    private int findDepth(BSTNode node, int val, int currentDepth) {
        if (node == null)    return -1;
        if (node.val == val) return currentDepth;
        if (val < node.val)  return findDepth(node.left,  val, currentDepth + 1);
        return                      findDepth(node.right, val, currentDepth + 1);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  MÉTRICAS DA ÁRVORE
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Altura da árvore = número de ARESTAS no caminho mais longo raiz → folha.
     * Árvore vazia = -1  |  Somente raiz = 0  |  Raiz + 1 filho = 1 ...
     */
    public int getTreeHeight(BSTNode root) {
        return computeHeight(root);
    }

    /**
     * Profundidade máxima = maior profundidade entre todas as folhas.
     * Numericamente igual à altura da árvore (ambas contam arestas).
     * Árvore vazia = -1  |  Somente raiz = 0
     */
    public int getTreeMaxDepth(BSTNode root) {
        return computeHeight(root);
    }

    /**
     * Total de níveis da árvore.
     * Fórmula: altura + 1
     * Árvore vazia = 0  |  Somente raiz = 1  |  2 níveis = 2 ...
     */
    public int getTreeLevels(BSTNode root) {
        return (root == null) ? 0 : computeHeight(root) + 1;
    }

    /** Total de nós na árvore. */
    public int countNodes(BSTNode root) {
        if (root == null) return 0;
        return 1 + countNodes(root.left) + countNodes(root.right);
    }

    /** Total de nós folha (sem filhos). */
    public int countLeaves(BSTNode root) {
        if (root == null) return 0;
        if (root.left == null && root.right == null) return 1;
        return countLeaves(root.left) + countLeaves(root.right);
    }

    /** Total de nós internos (com pelo menos 1 filho). */
    public int countInternalNodes(BSTNode root) {
        return countNodes(root) - countLeaves(root);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  TIPOS DA ÁRVORE
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Árvore CHEIA (Full / Strictly Binary):
     * Todo nó tem exatamente 0 ou 2 filhos.
     */
    public boolean isFull(BSTNode root) {
        if (root == null) return true;
        boolean isLeaf  = root.left == null && root.right == null;
        boolean hasBoth = root.left != null && root.right != null;
        if (!isLeaf && !hasBoth) return false;
        return isFull(root.left) && isFull(root.right);
    }

    /**
     * Árvore COMPLETA (Complete Binary Tree):
     * Todos os níveis preenchidos exceto possivelmente o último,
     * que deve ser preenchido da esquerda para a direita.
     */
    public boolean isComplete(BSTNode root) {
        if (root == null) return true;
        int n = countNodes(root);
        return checkComplete(root, 0, n);
    }

    private boolean checkComplete(BSTNode node, int index, int n) {
        if (node == null) return true;
        if (index >= n)   return false;
        return checkComplete(node.left,  2 * index + 1, n)
            && checkComplete(node.right, 2 * index + 2, n);
    }

    /**
     * Árvore PERFEITA (Perfect Binary Tree):
     * Todos os nós internos têm 2 filhos e todas as folhas
     * estão no mesmo nível.
     */
    public boolean isPerfect(BSTNode root) {
        if (root == null) return true;
        int h = computeHeight(root);
        return checkPerfect(root, 0, h);
    }

    private boolean checkPerfect(BSTNode node, int depth, int h) {
        if (node == null) return true;
        if (node.left == null && node.right == null) return depth == h;
        if (node.left == null || node.right == null) return false;
        return checkPerfect(node.left,  depth + 1, h)
            && checkPerfect(node.right, depth + 1, h);
    }

    /**
     * Árvore BALANCEADA (Height-Balanced):
     * Para todo nó, |altura(esq) − altura(dir)| ≤ 1.
     */
    public boolean isBalanced(BSTNode root) {
        return checkBalanced(root) != Integer.MIN_VALUE;
    }

    private int checkBalanced(BSTNode node) {
        if (node == null) return -1;
        int lh = checkBalanced(node.left);
        if (lh == Integer.MIN_VALUE) return Integer.MIN_VALUE;
        int rh = checkBalanced(node.right);
        if (rh == Integer.MIN_VALUE) return Integer.MIN_VALUE;
        if (Math.abs(lh - rh) > 1)  return Integer.MIN_VALUE;
        return 1 + Math.max(lh, rh);
    }

    /**
     * Árvore DEGENERADA (Degenerate / Pathological):
     * Cada nó tem no máximo 1 filho — degenerada em lista encadeada.
     */
    public boolean isDegenerate(BSTNode root) {
        if (root == null) return true;
        if (root.left != null && root.right != null) return false;
        BSTNode child = root.left != null ? root.left : root.right;
        return isDegenerate(child);
    }

    /** Retorna lista de tipos aplicáveis. Uma árvore pode satisfazer múltiplos. */
    public List<String> getTreeTypes(BSTNode root) {
        if (root == null) return List.of("Vazia");
        List<String> types = new ArrayList<>();
        if (isPerfect(root))                                        types.add("Perfeita");
        if (isComplete(root))                                       types.add("Completa");
        if (isFull(root))                                           types.add("Cheia (Full)");
        if (isBalanced(root))                                       types.add("Balanceada");
        if (isDegenerate(root) && countNodes(root) > 1)             types.add("Degenerada");
        if (types.isEmpty())                                        types.add("BST Padrão");
        return Collections.unmodifiableList(types);
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  CAMINHOS
    // ═════════════════════════════════════════════════════════════════════════

    /** Retorna todos os caminhos raiz → folha. */
    public List<List<Integer>> getAllRootToLeafPaths(BSTNode root) {
        List<List<Integer>> result = new ArrayList<>();
        if (root != null) collectPaths(root, new ArrayList<>(), result);
        return result;
    }

    private void collectPaths(BSTNode node, List<Integer> current, List<List<Integer>> result) {
        current.add(node.val);
        if (node.left == null && node.right == null) {
            result.add(new ArrayList<>(current));
        } else {
            if (node.left  != null) collectPaths(node.left,  current, result);
            if (node.right != null) collectPaths(node.right, current, result);
        }
        current.remove(current.size() - 1);
    }

    /** Retorna o caminho da raiz até um nó específico. Lista vazia se não encontrado. */
    public List<Integer> getPathToNode(BSTNode root, int val) {
        List<Integer> path = new ArrayList<>();
        findPath(root, val, path);
        return path;
    }

    private boolean findPath(BSTNode node, int val, List<Integer> path) {
        if (node == null) return false;
        path.add(node.val);
        if (node.val == val) return true;
        boolean found = (val < node.val)
                ? findPath(node.left,  val, path)
                : findPath(node.right, val, path);
        if (!found) path.remove(path.size() - 1);
        return found;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILITÁRIOS
    // ═════════════════════════════════════════════════════════════════════════

    /** Busca um nó pelo valor. Retorna null se não encontrado. */
    public BSTNode findNode(BSTNode root, int val) {
        if (root == null)    return null;
        if (root.val == val) return root;
        if (val < root.val)  return findNode(root.left,  val);
        return               findNode(root.right, val);
    }

    /**
     * Altura de um nó contando ARESTAS até a folha mais distante.
     * null = -1  |  folha = 0  |  pai de folha = 1 ...
     */
    private int computeHeight(BSTNode node) {
        if (node == null) return -1;
        return 1 + Math.max(computeHeight(node.left), computeHeight(node.right));
    }
}
