package bst.io;

import bst.model.BST;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.*;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   TreeFileReader — Leitor de Arquivo TXT
 *   Reconstrói a BST a partir do formato Parênteses Aninhados
 * ╚══════════════════════════════════════════════════════╝
 *
 * Padrão: Service (sem estado)
 *
 * O arquivo pode ser qualquer TXT que contenha uma linha com
 * o formato de Parênteses Aninhados compacto, por exemplo:
 *
 *   (50 (30 (20 () ()) (40 () ())) (70 (60 () ()) (80 () ())))
 *
 * O leitor:
 *   1. Varre o arquivo linha por linha
 *   2. Detecta a primeira linha que corresponde ao padrão
 *   3. Faz o parse recursivo da expressão
 *   4. Insere os valores na BST preservando a estrutura original
 *      (inserção por pré-ordem para recriar a mesma árvore)
 *
 * Erros de parse lançam {@link ParseException} com mensagem descritiva.
 */
public class TreeFileReader {

    // ═════════════════════════════════════════════════════════════════════════
    //  EXCEÇÃO PERSONALIZADA
    // ═════════════════════════════════════════════════════════════════════════

    /** Exceção lançada quando o arquivo não pode ser interpretado. */
    public static class ParseException extends Exception {
        public ParseException(String msg) { super(msg); }
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  LEITURA DO ARQUIVO
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Lê um arquivo TXT e reconstrói a BST.
     * Substitui completamente o conteúdo da BST fornecida.
     *
     * @param file arquivo de origem (gerado pela aplicação ou qualquer TXT válido)
     * @param bst  árvore que será substituída
     * @throws IOException    se ocorrer erro de leitura
     * @throws ParseException se nenhuma expressão válida for encontrada
     */
    public void loadFromFile(File file, BST bst) throws IOException, ParseException {
        String expression = findExpression(file);
        bst.clear();
        parse(expression, bst);
    }

    /**
     * Varre o arquivo e retorna a primeira linha que contém
     * uma expressão de parênteses aninhados válida.
     */
    private String findExpression(File file) throws IOException, ParseException {
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                String trimmed = line.trim();
                // Procura linha que inicia com '(' e tem pelo menos um número
                if (trimmed.startsWith("(") && trimmed.contains(")")) {
                    // Ignora linhas que são claramente só "()" vazio sem conteúdo útil
                    if (!trimmed.equals("()")) {
                        // Verifica que contém ao menos um dígito (um valor de nó)
                        if (trimmed.matches(".*\\d.*")) {
                            return trimmed;
                        }
                    }
                }
            }
        }
        throw new ParseException(
                "Nenhuma expressão de Parênteses Aninhados encontrada no arquivo.\n"
                + "O arquivo deve conter uma linha no formato:\n"
                + "  (valor (subárvoreEsq) (subárvoreDir))");
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  PARSER DE PARÊNTESES ANINHADOS
    // ═════════════════════════════════════════════════════════════════════════

    /**
     * Ponto de entrada do parser.
     * Extrai os valores em pré-ordem e os insere na BST.
     * A inserção em pré-ordem recria exatamente a mesma estrutura
     * que foi serializada.
     *
     * @param expr expressão no formato "(val (esq) (dir))" ou "()"
     * @param bst  árvore destino
     */
    private void parse(String expr, BST bst) throws ParseException {
        int[] pos = {0};
        skipSpaces(expr, pos);
        parseNode(expr, pos, bst);

        // Verifica se há conteúdo inesperado após o final
        skipSpaces(expr, pos);
        if (pos[0] < expr.length()) {
            throw new ParseException(
                    "Conteúdo inesperado após o fechamento da expressão na posição " + pos[0]);
        }
    }

    /**
     * Parser recursivo de um nó.
     * Gramática:
     *   node  ::= '(' ')'                         // nó vazio
     *           | '(' INT node node ')'           // nó com valor e dois filhos
     */
    private void parseNode(String expr, int[] pos, BST bst) throws ParseException {
        expect(expr, pos, '(');
        skipSpaces(expr, pos);

        if (pos[0] >= expr.length()) {
            throw new ParseException("Expressão incompleta — parêntese aberto sem fechar.");
        }

        // Nó vazio: "()"
        if (expr.charAt(pos[0]) == ')') {
            pos[0]++; // consome ')'
            return;
        }

        // Lê o valor inteiro
        int val = readInt(expr, pos);

        // Insere na BST ANTES de processar filhos (pré-ordem = recria mesma estrutura)
        bst.insert(val);

        skipSpaces(expr, pos);

        // Filho esquerdo
        parseNode(expr, pos, bst);
        skipSpaces(expr, pos);

        // Filho direito
        parseNode(expr, pos, bst);
        skipSpaces(expr, pos);

        expect(expr, pos, ')');
    }

    // ═════════════════════════════════════════════════════════════════════════
    //  UTILITÁRIOS DO PARSER
    // ═════════════════════════════════════════════════════════════════════════

    /** Avança enquanto houver espaços em branco. */
    private void skipSpaces(String expr, int[] pos) {
        while (pos[0] < expr.length() && Character.isWhitespace(expr.charAt(pos[0]))) {
            pos[0]++;
        }
    }

    /** Verifica e consome o caractere esperado, ou lança ParseException. */
    private void expect(String expr, int[] pos, char expected) throws ParseException {
        skipSpaces(expr, pos);
        if (pos[0] >= expr.length()) {
            throw new ParseException(
                    "Fim inesperado da expressão — esperava '" + expected + "'.");
        }
        if (expr.charAt(pos[0]) != expected) {
            throw new ParseException(String.format(
                    "Caractere inesperado '%c' na posição %d — esperava '%c'.",
                    expr.charAt(pos[0]), pos[0], expected));
        }
        pos[0]++;
    }

    /**
     * Lê um inteiro (com sinal opcional) da posição atual.
     * @throws ParseException se não houver dígito na posição
     */
    private int readInt(String expr, int[] pos) throws ParseException {
        int start = pos[0];
        // Sinal negativo opcional
        if (pos[0] < expr.length() && expr.charAt(pos[0]) == '-') {
            pos[0]++;
        }
        if (pos[0] >= expr.length() || !Character.isDigit(expr.charAt(pos[0]))) {
            throw new ParseException(
                    "Esperava um número inteiro na posição " + start + ".");
        }
        while (pos[0] < expr.length() && Character.isDigit(expr.charAt(pos[0]))) {
            pos[0]++;
        }
        String token = expr.substring(start, pos[0]);
        try {
            return Integer.parseInt(token);
        } catch (NumberFormatException e) {
            throw new ParseException("Número fora do range inteiro: " + token);
        }
    }
}
