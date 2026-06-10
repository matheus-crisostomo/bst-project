package bst.model;

public class RedBlackTree extends BST {

    private static final boolean RED   = true;
    private static final boolean BLACK = false;


    @Override
    public boolean insert(int val) {
        startOperation("INSERIR", val);
        if (contains(root, val)) return false;
        root = insertRB(root, val);
        root.color = BLACK;
        notifyInserted(val);
        notifyChanged();
        return true;
    }

    private BSTNode insertRB(BSTNode h, int val) {
        if (h == null) return new BSTNode(val); // Nós novos nascem RED

        if (val < h.val)      h.left  = insertRB(h.left,  val);
        else if (val > h.val) h.right = insertRB(h.right, val);

        return balance(h);
    }


    @Override
    public boolean remove(int val) {
        startOperation("REMOVER", val);
        if (!contains(root, val)) return false;

        // Se ambos os filhos da raiz são pretos, torna a raiz temporariamente vermelha
        if (root != null && !isRed(root.left) && !isRed(root.right)) {
            root.color = RED;
        }

        root = removeRB(root, val);
        if (root != null) root.color = BLACK;

        notifyRemoved(val);
        notifyChanged();
        return true;
    }

    private BSTNode removeRB(BSTNode h, int val) {
        if (val < h.val) {
            if (!isRed(h.left) && h.left != null && !isRed(h.left.left)) {
                h = moveRedLeft(h);
            }
            h.left = removeRB(h.left, val);
        } else {
            if (isRed(h.left)) {
                h = rotateRight(h);
            }
            if (val == h.val && h.right == null) {
                return null;
            }
            if (!isRed(h.right) && h.right != null && !isRed(h.right.left)) {
                h = moveRedRight(h);
            }
            if (val == h.val) {
                BSTNode x = minNode(h.right);
                h.val = x.val;
                h.right = deleteMin(h.right);
            } else {
                h.right = removeRB(h.right, val);
            }
        }
        return balance(h);
    }

    private BSTNode deleteMin(BSTNode h) {
        if (h.left == null) return null;

        if (!isRed(h.left) && !isRed(h.left.left)) {
            h = moveRedLeft(h);
        }

        h.left = deleteMin(h.left);
        return balance(h);
    }


    private BSTNode balance(BSTNode h) {
        if (isRed(h.right) && !isRed(h.left))      h = rotateLeft(h);
        if (isRed(h.left)  && isRed(h.left.left))  h = rotateRight(h);
        if (isRed(h.left)  && isRed(h.right))      flipColors(h);
        return h;
    }

    private boolean isRed(BSTNode node) {
        if (node == null) return false;
        return node.color == RED;
    }

    private BSTNode rotateLeft(BSTNode h) {
        BSTNode x = h.right;
        h.right = x.left;
        x.left = h;
        x.color = h.color;
        h.color = RED;
        notifyRotation(RotationType.LEFT_LEFT, h.val); // Reutiliza enum LEFT_LEFT para left rotation visual
        return x;
    }

    private BSTNode rotateRight(BSTNode h) {
        BSTNode x = h.left;
        h.left = x.right;
        x.right = h;
        x.color = h.color;
        h.color = RED;
        notifyRotation(RotationType.RIGHT_RIGHT, h.val); // Reutiliza enum RIGHT_RIGHT para right rotation visual
        return x;
    }

    private void flipColors(BSTNode h) {
        h.color = !h.color;
        if (h.left != null)  h.left.color  = !h.left.color;
        if (h.right != null) h.right.color = !h.right.color;
    }

    private BSTNode moveRedLeft(BSTNode h) {
        flipColors(h);
        if (h.right != null && isRed(h.right.left)) {
            h.right = rotateRight(h.right);
            h = rotateLeft(h);
            flipColors(h);
        }
        return h;
    }

    private BSTNode moveRedRight(BSTNode h) {
        flipColors(h);
        if (h.left != null && isRed(h.left.left)) {
            h = rotateRight(h);
            flipColors(h);
        }
        return h;
    }


    /**
     * Espelha a árvore e rebalanceia completamente via reinserção,
     * para manter a propriedade Red-Black (LLRB é estritamente inclinada à esquerda).
     */
    @Override
    public void mirror() {
        java.util.List<Integer> vals = new java.util.ArrayList<>();
        collectMirrorPreorder(root, vals);
        root = null;
        for (int v : vals) insert(v);
        notifyChanged();
    }

    private void collectMirrorPreorder(BSTNode node, java.util.List<Integer> vals) {
        if (node == null) return;
        vals.add(node.val);
        collectMirrorPreorder(node.right, vals);
        collectMirrorPreorder(node.left,  vals);
    }


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
