---
inclusion: always
---

# Padrões de Código — BST Visualizer

## Linguagem e Versão
- Java 17+
- Sem frameworks externos — apenas Java SE + Swing

## Estrutura de Pacotes
```
src/
└── bst/
    ├── controller/   ← Mediação UI ↔ Modelo
    ├── io/           ← Leitura e escrita de arquivos
    ├── model/        ← Estruturas de dados e lógica
    ├── observer/     ← Interfaces do padrão Observer
    ├── renderer/     ← Lógica de desenho (Graphics2D)
    ├── theme/        ← Tokens de design (cores, fontes)
    └── ui/
        └── panels/   ← Componentes Swing individuais
```

## Nomenclatura
- Classes: `PascalCase` — ex: `BSTAnalyzer`, `TreePanel`
- Métodos e variáveis: `camelCase` — ex: `insertAVL`, `treePanel`
- Constantes: `UPPER_SNAKE_CASE` — ex: `NODE_RADIUS`, `BG_DARK`
- Pacotes: `lowercase` — ex: `bst.model`, `bst.ui.panels`

## Cabeçalho de Classe
Todo arquivo deve ter o bloco de cabeçalho no topo da classe:
```java
/**
 * ╔══════════════════════════════════════════════════════╗
 *   NomeClasse — Descrição curta
 *   Descrição complementar se necessário
 * ╚══════════════════════════════════════════════════════╝
 *
 * Padrão: NomeDoPatrão (se aplicável)
 * Responsabilidade: descrição da responsabilidade única
 */
```

## Separadores de Seção
Use separadores visuais para agrupar métodos relacionados:
```java
// ── Nome da Seção ────────────────────────────────────────
```

## Organização dos Membros (ordem obrigatória)
1. Constantes e campos estáticos
2. Campos de instância
3. Construtores
4. Métodos públicos
5. Métodos protegidos
6. Métodos privados
7. Classes internas / enums

## Formatação
- Indentação: 4 espaços (sem tabs)
- Chaves na mesma linha: `if (x) {`
- Máximo 120 caracteres por linha
- Linha em branco entre métodos
- Sem trailing whitespace

## Comentários
- Javadoc em todos os métodos públicos e protegidos
- Comentários inline apenas para lógica não óbvia
- Português para comentários de domínio, inglês para termos técnicos de código
