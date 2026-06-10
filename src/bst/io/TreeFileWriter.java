package bst.io;

import bst.model.BST;
import bst.model.AVLTree;
import bst.model.RedBlackTree;
import bst.model.BSTAnalyzer;
import bst.model.BSTNode;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TreeFileWriter {

    private final BSTAnalyzer analyzer;

    public TreeFileWriter(BSTAnalyzer analyzer) {
        this.analyzer = analyzer;
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  SERIALIZAÇÃO — PARÊNTESES ANINHADOS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Converte a árvore para a notação de Parênteses Aninhados.
     * Representação recursiva: (valor subÁrvoreEsq subÁrvoreDir)
     *
     * @param root raiz da árvore (pode ser null)
     * @return string no formato de parênteses aninhados
     */
    public String toNestedParentheses(BSTNode root) {
        if (root == null) return "()";
        return "("
                + root.val
                + " " + toNestedParentheses(root.left)
                + " " + toNestedParentheses(root.right)
                + ")";
    }

    /**
     * Versão indentada (multi-linha) para maior legibilidade.
     * Cada nível de profundidade recebe indentação adicional.
     */
    public String toNestedParenthesesPretty(BSTNode root) {
        StringBuilder sb = new StringBuilder();
        buildPretty(root, sb, 0);
        return sb.toString();
    }

    private void buildPretty(BSTNode node, StringBuilder sb, int indent) {
        String pad = "  ".repeat(indent);
        if (node == null) {
            sb.append(pad).append("()");
            return;
        }
        sb.append(pad).append("(").append(node.val).append("\n");
        buildPretty(node.left,  sb, indent + 1);
        sb.append("\n");
        buildPretty(node.right, sb, indent + 1);
        sb.append("\n").append(pad).append(")");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  GERAÇÃO DO CONTEÚDO DO ARQUIVO
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Gera o conteúdo completo do arquivo TXT.
     *
     * @param bst a árvore a ser exportada
     * @return string completa pronta para salvar em arquivo
     */
    public String generateContent(BST bst) {
        BSTNode root = bst.root;
        StringBuilder sb = new StringBuilder();

        String sep  = "═".repeat(52);
        String sep2 = "─".repeat(52);

        String treeTypeStr = "Árvore Binária de Busca (Padrão)";
        if (bst instanceof AVLTree) treeTypeStr = "Árvore AVL";
        else if (bst instanceof RedBlackTree) treeTypeStr = "Árvore Rubro-Negra";

        sb.append("╔").append("═".repeat(50)).append("╗\n");
        sb.append(String.format("║   %-46s ║%n", treeTypeStr.toUpperCase()));
        sb.append("║   Formato: Parênteses Aninhados                 ║\n");
        sb.append("╚").append("═".repeat(50)).append("╝\n");
        sb.append("  Gerado em: ")
          .append(LocalDateTime.now().format(
                  DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")))
          .append("\n\n");

        if (root == null) {
            sb.append("  ⚠  A árvore está vazia — nenhum dado para exportar.\n");
            return sb.toString();
        }

        sb.append(sep).append("\n");
        sb.append("  HISTÓRICO DE OPERAÇÕES E ROTAÇÕES\n");
        sb.append(sep2).append("\n");
        List<BST.TreeOperation> ops = bst.getOperationHistory();
        if (ops.isEmpty()) {
            sb.append("  (Nenhuma operação rastreada)\n\n");
        } else {
            for (int i = 0; i < ops.size(); i++) {
                BST.TreeOperation op = ops.get(i);
                sb.append(String.format("  %d. %s(%d)", i + 1, op.type, op.val));
                if (op.rotations.isEmpty()) {
                    sb.append(" -> Nenhuma rotação\n");
                } else {
                    sb.append(" -> Rotações: ").append(String.join(", ", op.rotations)).append("\n");
                }
            }
            sb.append("\n");
        }

        sb.append(sep).append("\n");
        sb.append("  1. PARÊNTESES ANINHADOS (Compacto)\n");
        sb.append(sep2).append("\n");
        sb.append("  ").append(toNestedParentheses(root)).append("\n\n");

        sb.append(sep).append("\n");
        sb.append("  2. PARÊNTESES ANINHADOS (Indentado)\n");
        sb.append(sep2).append("\n");
        String pretty = toNestedParenthesesPretty(root);
        for (String line : pretty.split("\n")) {
            sb.append("  ").append(line).append("\n");
        }
        sb.append("\n");

        sb.append(sep).append("\n");
        sb.append("  3. ESTATÍSTICAS\n");
        sb.append(sep2).append("\n");
        sb.append(stat("Total de Nós",      analyzer.countNodes(root)));
        sb.append(stat("Nós Folha",         analyzer.countLeaves(root)));
        sb.append(stat("Nós Internos",      analyzer.countInternalNodes(root)));
        sb.append(stat("Raiz",              root.val));
        sb.append(stat("Altura da Árvore",  analyzer.getTreeHeight(root)));
        sb.append(stat("Profundidade Máx.", analyzer.getTreeMaxDepth(root)));
        sb.append(stat("Total de Níveis",   analyzer.getTreeLevels(root)));
        sb.append("  Tipo(s)           :  ")
          .append(String.join(", ", analyzer.getTreeTypes(root)))
          .append("\n\n");

        sb.append(sep).append("\n");
        sb.append("  4. TIPOS DA ÁRVORE\n");
        sb.append(sep2).append("\n");
        sb.append(typeRow("Cheia (Full/Strict)",    analyzer.isFull(root)));
        sb.append(typeRow("Completa (Complete)",    analyzer.isComplete(root)));
        sb.append(typeRow("Perfeita (Perfect)",     analyzer.isPerfect(root)));
        sb.append(typeRow("Balanceada (Balanced)",  analyzer.isBalanced(root)));
        sb.append(typeRow("Degenerada (Degenerate)",analyzer.isDegenerate(root)));
        sb.append("\n");

        sb.append(sep).append("\n");
        sb.append("  5. PERCURSOS\n");
        sb.append(sep2).append("\n");
        sb.append("  In-Order    (Esq→Raiz→Dir):  ")
          .append(joinArrow(bst.inorder())).append("\n");
        sb.append("  Pre-Order   (Raiz→Esq→Dir):  ")
          .append(joinArrow(bst.preorder())).append("\n");
        sb.append("  Post-Order  (Esq→Dir→Raiz):  ")
          .append(joinArrow(bst.postorder())).append("\n");
        sb.append("  Level-Order (Por Nível BFS):  ")
          .append(joinArrow(bst.levelorder())).append("\n\n");

        sb.append(sep).append("\n");
        sb.append("  6. CAMINHOS RAIZ → FOLHA\n");
        sb.append(sep2).append("\n");
        List<List<Integer>> paths = analyzer.getAllRootToLeafPaths(root);
        for (int i = 0; i < paths.size(); i++) {
            sb.append("  [").append(i + 1).append("] ").append(joinArrow(paths.get(i))).append("\n");
        }
        sb.append("\n");

        sb.append(sep).append("\n");
        sb.append("  7. DETALHES DE CADA NÓ (In-Order)\n");
        sb.append(sep2).append("\n");
        sb.append(String.format("  %-8s %-8s %-12s %-8s %-6s%n",
                "VALOR", "NÍVEL", "PROFUNDIDADE", "ALTURA", "FOLHA?"));
        sb.append("  ").append("─".repeat(46)).append("\n");
        for (int val : bst.inorder()) {
            int level = analyzer.getNodeLevel(root, val);
            int depth = analyzer.getNodeDepth(root, val);
            int hNode = analyzer.getNodeHeight(root, val);
            BSTNode n = analyzer.findNode(root, val);
            boolean isLeaf = n != null && n.left == null && n.right == null;
            sb.append(String.format("  %-8d %-8d %-12d %-8d %-6s%n",
                    val, level, depth, hNode, isLeaf ? "Sim" : "Não"));
        }
        sb.append("\n");

        sb.append(sep).append("\n");
        sb.append("  8. COMPARAÇÃO COM OUTRAS ESTRUTURAS\n");
        sb.append("  Aplicando a mesma sequência de inserções/remoções\n");
        sb.append(sep2).append("\n");

        if (bst.getClass() != BST.class) {
            BST bstNormal = new BST();
            applyOperations(bstNormal, ops);
            sb.append("\n  [ ÁRVORE BST PADRÃO ]\n");
            sb.append("  Parênteses: ").append(toNestedParentheses(bstNormal.root)).append("\n");
            sb.append("  In-Order  : ").append(joinArrow(bstNormal.inorder())).append("\n");
        }

        if (!(bst instanceof AVLTree)) {
            AVLTree avl = new AVLTree();
            applyOperations(avl, ops);
            sb.append("\n  [ ÁRVORE AVL ]\n");
            sb.append("  Parênteses: ").append(toNestedParentheses(avl.root)).append("\n");
            sb.append("  In-Order  : ").append(joinArrow(avl.inorder())).append("\n");
        }

        if (!(bst instanceof RedBlackTree)) {
            RedBlackTree rb = new RedBlackTree();
            applyOperations(rb, ops);
            sb.append("\n  [ ÁRVORE RUBRO-NEGRA ]\n");
            sb.append("  Parênteses: ").append(toNestedParentheses(rb.root)).append("\n");
            sb.append("  In-Order  : ").append(joinArrow(rb.inorder())).append("\n");
        }
        sb.append("\n");

        return sb.toString();
    }

    private void applyOperations(BST target, List<BST.TreeOperation> ops) {
        for (BST.TreeOperation op : ops) {
            if ("INSERIR".equals(op.type)) {
                target.insert(op.val);
            } else if ("REMOVER".equals(op.type)) {
                target.remove(op.val);
            }
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  GRAVAÇÃO EM ARQUIVO
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Salva o conteúdo no arquivo especificado (UTF-8).
     *
     * @param content  conteúdo gerado por {@link #generateContent(BST)}
     * @param file     arquivo de destino
     * @throws IOException se ocorrer erro de I/O
     */
    public void saveToFile(String content, File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(file), "UTF-8"))) {
            writer.write(content);
        }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  HELPERS PRIVADOS
    // ═════════════════════════════════════════════════════════════════════════

    private String stat(String label, int value) {
        return String.format("  %-20s:  %d%n", label, value);
    }

    private String typeRow(String name, boolean value) {
        return String.format("  %s %-30s%n", value ? "✓" : "✗", name);
    }

    private String joinArrow(List<Integer> list) {
        if (list.isEmpty()) return "(vazio)";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(" → ");
            sb.append(list.get(i));
        }
        return sb.toString();
    }
}
