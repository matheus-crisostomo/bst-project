# BST Visualizer — Árvore Binária de Busca

Visualizador gráfico de Árvore Binária de Busca em **Java + Swing**, com interface dark no estilo Vercel + Glassmorphism e arquitetura orientada a objetos.

---

## Estrutura do Projeto

```
bst-project/
├── src/
│   ├── Main.java
│   └── bst/
│       ├── theme/
│       │   └── Theme.java                 ← Tokens de design (cores, fontes)
│       ├── observer/
│       │   └── BSTObserver.java           ← Interface do padrão Observer
│       ├── model/
│       │   ├── BSTNode.java               ← Nó da árvore
│       │   ├── BST.java                   ← Estrutura BST + notificação
│       │   └── BSTAnalyzer.java           ← Métricas e análises
│       ├── controller/
│       │   └── BSTController.java         ← Mediador UI ↔ Modelo
│       ├── io/
│       │   ├── TreeFileWriter.java        ← Serialização e gravação TXT
│       │   └── TreeFileReader.java        ← Leitura e reconstrução da árvore
│       ├── renderer/
│       │   └── TreeRenderer.java          ← Lógica de desenho Graphics2D
│       └── ui/
│           ├── BSTVisualizer.java         ← JFrame principal
│           └── panels/
│               ├── HeaderPanel.java       ← Cabeçalho + estatísticas
│               ├── ControlPanel.java      ← Botões e campo de entrada
│               ├── TreePanel.java         ← Canvas de renderização
│               ├── InfoPanel.java         ← Painel lateral de análise
│               └── TraversalPanel.java    ← Rodapé com resultado de percurso
├── compile.sh / compile.bat
├── run.sh     / run.bat
└── README.md
```

---

## Como Executar

**Requisito:** Java 17+

### Linux / macOS
```bash
chmod +x compile.sh run.sh
./compile.sh && ./run.sh
```

### Windows
```bat
compile.bat
run.bat
```

### Manualmente
```bash
javac -d out $(find src -name "*.java")
java -cp out Main
```

---

## Funcionalidades

| Função | Descrição |
|---|---|
| Inserir | Insere um valor seguindo as regras da BST, sem duplicatas |
| Remover | Remove com tratamento dos 3 casos (folha, 1 filho, 2 filhos) |
| Limpar | Apaga toda a árvore |
| Em-Ordem | Esq → Raiz → Dir (resultado sempre ordenado) |
| Pré-Ordem | Raiz → Esq → Dir |
| Pós-Ordem | Esq → Dir → Raiz |
| Por Nível | BFS — nível por nível |
| Clique no nó | Seleciona o nó e exibe detalhes no painel lateral |
| Salvar | Exporta a árvore em TXT no formato Parênteses Aninhados |
| Carregar | Reconstrói a árvore a partir de um TXT salvo |

---

## Painel de Análise (lateral direito)

Atualizado em tempo real conforme a árvore muda.

| Seção | Conteúdo |
|---|---|
| Propriedades | Total de nós, folhas, internos, raiz, altura, profundidade máxima, níveis |
| Tipos da Árvore | Cheia, Completa, Perfeita, Balanceada, Degenerada |
| Nó Selecionado | Nível, profundidade, altura, filhos, caminho da raiz |
| Caminhos Raiz → Folha | Todos os caminhos listados |

---

## Definições Implementadas

| Conceito | Definição |
|---|---|
| Nível do nó | Raiz = 1. Fórmula: profundidade + 1 |
| Profundidade do nó | Número de arestas até a raiz. Raiz = 0 |
| Altura do nó | Número de arestas até a folha mais distante. Folha = 0 |
| Altura da árvore | Altura da raiz. Árvore vazia = -1, só raiz = 0 |
| Árvore Cheia | Todo nó tem 0 ou 2 filhos |
| Árvore Completa | Todos os níveis preenchidos exceto o último (esq → dir) |
| Árvore Perfeita | Todos os nós internos com 2 filhos e folhas no mesmo nível |
| Árvore Balanceada | \|h(esq) − h(dir)\| ≤ 1 para todo nó |
| Árvore Degenerada | Cada nó tem no máximo 1 filho |

---

## Formato de Arquivo (Parênteses Aninhados)

```
(50 (30 (20 () ()) (40 () ())) (70 (60 () ()) (80 () ())))
```

O arquivo TXT gerado pelo **Salvar** inclui além da expressão: estatísticas completas, tipos da árvore, os 4 percursos e todos os caminhos raiz → folha.

---

## Arquitetura e Padrões de Projeto

| Padrão | Onde é aplicado |
|---|---|
| Observer | `BST` notifica todos os painéis automaticamente a cada mudança |
| MVC | `BST` (Model) · `BSTVisualizer` (View) · `BSTController` (Controller) |
| Strategy | `TreeRenderer` separa o algoritmo de desenho dos componentes Swing |
| Mediator | `BSTController` medeia UI → Modelo com resultados tipados |
| Facade | `BSTVisualizer` orquestra todos os painéis e serviços |
