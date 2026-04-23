---
inclusion: always
---

# UI e Tema — BST Visualizer

## Design System

Estilo: Vercel Dark + Glassmorphism. Todos os tokens estão em `bst.theme.Theme`.
Nunca use cores ou fontes hardcoded fora de `Theme`.

## Cores

| Token            | Uso                                      |
|------------------|------------------------------------------|
| `BG_DARK`        | Fundo principal da janela                |
| `BG_PANEL`       | Fundo de painéis (header, control, info) |
| `BG_SURFACE`     | Fundo de inputs e text areas             |
| `BG_GLASS`       | Preenchimento de cards glass             |
| `BG_GLASS2`      | Preenchimento de chips de traversal      |
| `BORDER`         | Bordas padrão                            |
| `BORDER_LIGHT`   | Bordas em hover                          |
| `TEXT_BRIGHT`    | Texto principal                          |
| `TEXT_MUTED`     | Texto secundário / labels                |
| `TEXT_DIM`       | Texto desabilitado / hints               |
| `ACCENT_GREEN`   | Sucesso, AVL, tipos verdadeiros          |
| `ACCENT_ORANGE`  | Erro, alertas                            |
| `ACCENT_CYAN`    | BST, destaques neutros                   |

## Fontes

```java
Theme.ui(Font.PLAIN, 13)   // texto de interface
Theme.mono(Font.PLAIN, 11) // texto monospace (text areas, caminhos)
```

Nunca instancie `new Font(...)` diretamente — use sempre `Theme.ui()` ou `Theme.mono()`.

## Componentes Customizados

Botões e campos sobrescrevem `paintComponent` para o visual glass. Siga o padrão:

```java
@Override protected void paintComponent(Graphics g) {
    Graphics2D g2 = (Graphics2D) g.create();
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
    // desenho customizado
    g2.dispose();
    super.paintComponent(g);
}
```

Sempre chame `g2.dispose()` antes de `super.paintComponent(g)`.

## Bordas e Espaçamento

- Painéis laterais: `MatteBorder` para separadores de 1px
- Conteúdo interno: `EmptyBorder` para padding
- Cards glass: `fillRoundRect` + `drawRoundRect` com raio 10
- Botões: raio 7
- Chips pequenos: raio 5–8

## Feedback ao Usuário

Toda operação com resultado visível usa o sistema de toast do `BSTVisualizer`:

```java
toastCallback.accept("mensagem", "success" | "error" | "info")
```

Nunca use `JOptionPane` para feedback de operações rotineiras — apenas para confirmações destrutivas (sobrescrever arquivo, limpar árvore ao carregar).

## Responsividade

- Tamanho mínimo da janela: 960×580
- `TreePanel` usa `setPreferredSize` dinâmico baseado no tamanho da árvore
- `InfoPanel` tem largura fixa de 310px
- `TraversalPanel` tem altura fixa de 52px
