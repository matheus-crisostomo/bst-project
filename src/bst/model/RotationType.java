package bst.model;

/**
 * ╔══════════════════════════════════════════════════════╗
 *   RotationType — Tipos de Rotação AVL
 *   Enum que identifica as rotações realizadas no balanceamento
 * ╚══════════════════════════════════════════════════════╝
 *
 * Responsabilidade: representar os quatro casos de rotação AVL
 */
public enum RotationType {

    LEFT_LEFT("Rotação Simples à Direita", "RSD"),
    LEFT_RIGHT("Rotação Dupla Esquerda-Direita", "RDD"),
    RIGHT_RIGHT("Rotação Simples à Esquerda", "RSE"),
    RIGHT_LEFT("Rotação Dupla Direita-Esquerda", "RDE");

    private final String description;
    private final String shortName;

    RotationType(String description, String shortName) {
        this.description = description;
        this.shortName = shortName;
    }

    /** Descrição completa da rotação. */
    public String getDescription() {
        return description;
    }

    /** Nome curto (RSD, RDD, RSE, RDE). */
    public String getShortName() {
        return shortName;
    }
}
