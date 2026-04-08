package bst.controller;

import bst.model.BST;
import bst.observer.BSTObserver;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   BSTController — Controlador da Árvore
 *   Medeia as operações entre a UI e o modelo BST
 * ╚══════════════════════════════════════════════════════╝
 *
 * Padrão: Controller (MVC) + Mediator (GoF)
 * Responsabilidades:
 * - Validar entradas vindas da UI antes de tocar o modelo
 * - Converter strings da UI em inteiros com tratamento de erro
 * - Delegar operações ao BST e retornar resultados tipados
 * - Registrar/remover observadores no BST
 */
public class BSTController {

    private final BST bst;

    public BSTController(BST bst) {
        this.bst = bst;
    }

    // ── Gerenciamento de Observadores ────────────────────────────────────────

    public void addObserver(BSTObserver observer) {
        bst.addObserver(observer);
    }

    public void removeObserver(BSTObserver observer) {
        bst.removeObserver(observer);
    }

    // ── Operações com Resultado Tipado ───────────────────────────────────────

    /**
     * Valida e executa a inserção de um valor.
     *
     * @param rawInput string digitada pelo usuário
     * @return {@link InsertResult} descrevendo o resultado
     */
    public InsertResult insert(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return InsertResult.EMPTY;
        }
        int val;
        try {
            val = Integer.parseInt(rawInput.trim());
        } catch (NumberFormatException e) {
            return InsertResult.INVALID;
        }
        boolean ok = bst.insert(val);
        return ok ? InsertResult.success(val) : InsertResult.duplicate(val);
    }

    /**
     * Valida e executa a remoção de um valor.
     *
     * @param rawInput string digitada pelo usuário
     * @return {@link RemoveResult} descrevendo o resultado
     */
    public RemoveResult remove(String rawInput) {
        if (rawInput == null || rawInput.isBlank()) {
            return RemoveResult.EMPTY;
        }
        int val;
        try {
            val = Integer.parseInt(rawInput.trim());
        } catch (NumberFormatException e) {
            return RemoveResult.INVALID;
        }
        boolean ok = bst.remove(val);
        return ok ? RemoveResult.success(val) : RemoveResult.notFound(val);
    }

    /**
     * Limpa toda a árvore.
     */
    public void clear() {
        bst.clear();
    }

    /** Retorna a referência ao modelo BST (somente leitura recomendada). */
    public BST getBst() {
        return bst;
    }

    // ════════════════════════════════════════════════════════════════════════
    //  Tipos de resultado — encapsulam código + mensagem
    // ════════════════════════════════════════════════════════════════════════

    /** Resultado de uma operação de inserção. */
    public static class InsertResult {
        public enum Code { OK, DUPLICATE, INVALID, EMPTY }

        public final Code   code;
        public final int    val;
        public final String message;

        private InsertResult(Code code, int val, String message) {
            this.code    = code;
            this.val     = val;
            this.message = message;
        }

        public static InsertResult success(int val) {
            return new InsertResult(Code.OK, val, "Nó " + val + " inserido com sucesso");
        }
        public static InsertResult duplicate(int val) {
            return new InsertResult(Code.DUPLICATE, val, "Valor " + val + " já existe na árvore");
        }
        public static final InsertResult INVALID =
                new InsertResult(Code.INVALID, 0, "Valor inválido — use números inteiros");
        public static final InsertResult EMPTY =
                new InsertResult(Code.EMPTY, 0, "Digite um valor numérico");

        public boolean isSuccess() { return code == Code.OK; }
    }

    /** Resultado de uma operação de remoção. */
    public static class RemoveResult {
        public enum Code { OK, NOT_FOUND, INVALID, EMPTY }

        public final Code   code;
        public final int    val;
        public final String message;

        private RemoveResult(Code code, int val, String message) {
            this.code    = code;
            this.val     = val;
            this.message = message;
        }

        public static RemoveResult success(int val) {
            return new RemoveResult(Code.OK, val, "Nó " + val + " removido");
        }
        public static RemoveResult notFound(int val) {
            return new RemoveResult(Code.NOT_FOUND, val, "Valor " + val + " não encontrado na árvore");
        }
        public static final RemoveResult INVALID =
                new RemoveResult(Code.INVALID, 0, "Valor inválido — use números inteiros");
        public static final RemoveResult EMPTY =
                new RemoveResult(Code.EMPTY, 0, "Digite o valor a remover");

        public boolean isSuccess() { return code == Code.OK; }
    }
}
