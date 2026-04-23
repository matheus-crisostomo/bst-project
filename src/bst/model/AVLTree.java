package bst.model;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   AVLTree — Árvore AVL com Balanceamento Automático
 *   Estende BST mantendo a propriedade de altura balanceada
 * ╚══════════════════════════════════════════════════════╝
 *
 * Após cada inserção ou remoção, rotações são aplicadas
 * para garantir |altura(esq) − altura(dir)| ≤ 1 em todo nó.
 */
public class AVLTree extends BST {

    // ── Inserção ─────────────────────────────────────────────────────────────

    @Override
    public boolean insert(int val) {
        if (contains(root, val)) return false;
        root = insertAVL(root, val);
        notifyInserted(val);
        notifyChanged();
        return true;
    }

    private BSTNode insertAVL(BSTNode node, int val) {
        if (node == null) return new BSTNode(val);
        if (val < node.val)      node.left  = insertAVL(node.left,  val);
        else if (val > node.val) node.right = insertAVL(node.right, val);
        else                     return node; // duplicata
        return balance(node);
    }

    // ── Remoção ──────────────────────────────────────────────────────────────

    @Override
    public boolean remove(int val) {
        if (!contains(root, val)) return false;
        root = removeAVL(root, val);
        notifyRemoved(val);
        notifyChanged();
        return true;
    }

    private BSTNode removeAVL(BSTNode node, int val) {
        if (node == null) return null;
        if (val < node.val) {
            node.left  = removeAVL(node.left,  val);
        } else if (val > node.val) {
            node.right = removeAVL(node.right, val);
        } else {
            // nó encontrado
            if (node.left == null)  return node.right;
            if (node.right == null) return node.left;
            // dois filhos: substitui pelo sucessor in-order
            BSTNode succ = minNode(node.right);
            node.val   = succ.val;
            node.right = removeAVL(node.right, succ.val);
        }
        return balance(node);
    }

    // ── Balanceamento ────────────────────────────────────────────────────────

    private BSTNode balance(BSTNode node) {
        int bf = balanceFactor(node);

        // Caso Esquerda-Esquerda
        if (bf > 1 && balanceFactor(node.left) >= 0)
            return rotateRight(node);

        // Caso Esquerda-Direita
        if (bf > 1 && balanceFactor(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }

        // Caso Direita-Direita
        if (bf < -1 && balanceFactor(node.right) <= 0)
            return rotateLeft(node);

        // Caso Direita-Esquerda
        if (bf < -1 && balanceFactor(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    private BSTNode rotateRight(BSTNode y) {
        BSTNode x  = y.left;
        BSTNode t2 = x.right;
        x.right = y;
        y.left  = t2;
        return x;
    }

    private BSTNode rotateLeft(BSTNode x) {
        BSTNode y  = x.right;
        BSTNode t2 = y.left;
        y.left  = x;
        x.right = t2;
        return y;
    }

    private int height(BSTNode n) {
        if (n == null) return -1;
        return 1 + Math.max(height(n.left), height(n.right));
    }

    private int balanceFactor(BSTNode n) {
        return (n == null) ? 0 : height(n.left) - height(n.right);
    }

    // ── Mirror ───────────────────────────────────────────────────────────────

    /**
     * Espelha a árvore e rebalanceia completamente via reinserção,
     * pois o espelhamento simples destrói a propriedade AVL.
     */
    @Override
    public void mirror() {
        java.util.List<Integer> vals = new java.util.ArrayList<>();
        collectMirrorPreorder(root, vals);
        root = null;
        for (int v : vals) root = insertAVL(root, v);
        notifyChanged();
    }

    private void collectMirrorPreorder(BSTNode node, java.util.List<Integer> vals) {
        if (node == null) return;
        vals.add(node.val);
        collectMirrorPreorder(node.right, vals);
        collectMirrorPreorder(node.left,  vals);
    }

    // ── Utilitários ──────────────────────────────────────────────────────────

    private boolean contains(BSTNode node, int val) {
        if (node == null)    return false;
        if (val == node.val) return true;
        return val < node.val ? contains(node.left, val) : contains(node.right, val);
    }

    private BSTNode minNode(BSTNode node) {
        while (node.left != null) node = node.left;
        return node;
    }
}
