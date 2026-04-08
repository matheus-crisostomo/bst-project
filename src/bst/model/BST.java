package bst.model;

import bst.observer.BSTObserver;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   BST — Árvore Binária de Busca
 *   Estrutura de dados principal do sistema
 * ╚══════════════════════════════════════════════════════╝
 *
 * Padrão: Observable (Observer GoF)
 * Contém toda a lógica da BST: inserção, remoção,
 * percursos e estatísticas. Notifica observadores
 * registrados em cada operação mutável.
 */
public class BST {

    /** Raiz da árvore. */
    public BSTNode root;

    /** Lista de observadores registrados. */
    private final List<BSTObserver> observers = new ArrayList<>();

    // ── Gerenciamento de Observadores ────────────────────────────────────────

    public void addObserver(BSTObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BSTObserver observer) {
        observers.remove(observer);
    }

    private void notifyChanged()         { observers.forEach(BSTObserver::onTreeChanged);         }
    private void notifyInserted(int val) { observers.forEach(o -> o.onNodeInserted(val));         }
    private void notifyRemoved(int val)  { observers.forEach(o -> o.onNodeRemoved(val));          }

    // ── Operações Mutáveis ───────────────────────────────────────────────────

    /**
     * Insere um valor na BST seguindo as regras de ordenação.
     * Duplicatas são rejeitadas.
     *
     * @param val valor a inserir
     * @return {@code true} se inserido, {@code false} se duplicado
     */
    public boolean insert(int val) {
        BSTNode node = new BSTNode(val);

        if (root == null) {
            root = node;
            notifyInserted(val);
            notifyChanged();
            return true;
        }

        BSTNode cur = root;
        while (true) {
            if (val == cur.val) return false;        // duplicata

            if (val < cur.val) {
                if (cur.left == null) { cur.left  = node; break; }
                cur = cur.left;
            } else {
                if (cur.right == null) { cur.right = node; break; }
                cur = cur.right;
            }
        }

        notifyInserted(val);
        notifyChanged();
        return true;
    }

    /**
     * Remove um nó da BST tratando os três casos:
     * <ul>
     *   <li>Nó folha — remoção direta</li>
     *   <li>Um filho — substituição pelo filho</li>
     *   <li>Dois filhos — substituição pelo sucessor in-order</li>
     * </ul>
     *
     * @param val valor a remover
     * @return {@code true} se removido, {@code false} se não encontrado
     */
    public boolean remove(int val) {
        BSTNode parent = null;
        BSTNode cur    = root;
        boolean isLeft = false;

        // Busca o nó
        while (cur != null && cur.val != val) {
            parent = cur;
            if (val < cur.val) { isLeft = true;  cur = cur.left;  }
            else               { isLeft = false; cur = cur.right; }
        }
        if (cur == null) return false; // não encontrado

        // Caso 1: Nó folha
        if (cur.left == null && cur.right == null) {
            if (parent == null)  root         = null;
            else if (isLeft)     parent.left  = null;
            else                 parent.right = null;

        // Caso 2: Um filho
        } else if (cur.left == null || cur.right == null) {
            BSTNode child = (cur.left != null) ? cur.left : cur.right;
            if (parent == null)  root         = child;
            else if (isLeft)     parent.left  = child;
            else                 parent.right = child;

        // Caso 3: Dois filhos — sucessor in-order (menor da subárvore direita)
        } else {
            BSTNode succParent = cur;
            BSTNode succ       = cur.right;
            while (succ.left != null) {
                succParent = succ;
                succ       = succ.left;
            }
            cur.val = succ.val;
            if (succParent == cur) succParent.right = succ.right;
            else                   succParent.left  = succ.right;
        }

        notifyRemoved(val);
        notifyChanged();
        return true;
    }

    /**
     * Remove todos os nós da árvore.
     */
    public void clear() {
        root = null;
        notifyChanged();
    }

    // ── Estatísticas ─────────────────────────────────────────────────────────

    /** Retorna o número total de nós. */
    public int size() { return size(root); }

    /**
     * Altura da árvore = número de ARESTAS no caminho mais longo raiz → folha.
     * Árvore vazia = -1  |  Somente raiz = 0  |  2 níveis = 1 ...
     */
    public int height() { return height(root); }

    private int size(BSTNode n) {
        return (n == null) ? 0 : 1 + size(n.left) + size(n.right);
    }

    private int height(BSTNode n) {
        return (n == null) ? -1 : 1 + Math.max(height(n.left), height(n.right));
    }

    // ── Percursos ────────────────────────────────────────────────────────────

    /** In-Order: Esquerda → Raiz → Direita (resultado ordenado). */
    public List<Integer> inorder() {
        List<Integer> result = new ArrayList<>();
        inorder(root, result);
        return result;
    }

    /** Pre-Order: Raiz → Esquerda → Direita. */
    public List<Integer> preorder() {
        List<Integer> result = new ArrayList<>();
        preorder(root, result);
        return result;
    }

    /** Post-Order: Esquerda → Direita → Raiz. */
    public List<Integer> postorder() {
        List<Integer> result = new ArrayList<>();
        postorder(root, result);
        return result;
    }

    /** Level-Order (BFS): nível por nível, da esquerda para a direita. */
    public List<Integer> levelorder() {
        List<Integer> result = new ArrayList<>();
        if (root == null) return result;
        Queue<BSTNode> queue = new LinkedList<>();
        queue.add(root);
        while (!queue.isEmpty()) {
            BSTNode n = queue.poll();
            result.add(n.val);
            if (n.left  != null) queue.add(n.left);
            if (n.right != null) queue.add(n.right);
        }
        return result;
    }

    private void inorder(BSTNode n, List<Integer> r) {
        if (n == null) return;
        inorder(n.left, r);
        r.add(n.val);
        inorder(n.right, r);
    }

    private void preorder(BSTNode n, List<Integer> r) {
        if (n == null) return;
        r.add(n.val);
        preorder(n.left, r);
        preorder(n.right, r);
    }

    private void postorder(BSTNode n, List<Integer> r) {
        if (n == null) return;
        postorder(n.left, r);
        postorder(n.right, r);
        r.add(n.val);
    }
}
