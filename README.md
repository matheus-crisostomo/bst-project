# 🌲 BST Visualizer — Árvore Binária de Busca

Visualizador gráfico de Árvore Binária de Busca em **Java + Swing** com arquitetura orientada a objetos.

---

## 📁 Estrutura de Arquivos

```
bst-project/
├── src/
│   ├── Main.java                          ← Ponto de entrada
│   └── bst/
│       ├── theme/
│       │   └── Theme.java                 ← Tokens de design (cores, fontes)
│       ├── observer/
│       │   └── BSTObserver.java           ← Interface do padrão Observer
│       ├── model/
│       │   ├── BSTNode.java               ← Nó da árvore (dados)
│       │   └── BST.java                   ← Estrutura BST + notificação
│       ├── controller/
│       │   └── BSTController.java         ← Mediador UI ↔ Modelo
│       ├── renderer/
│       │   └── TreeRenderer.java          ← Toda lógica Graphics2D
│       └── ui/
│           ├── BSTVisualizer.java         ← JFrame principal (orquestrador)
│           └── panels/
│               ├── HeaderPanel.java       ← Cabeçalho + estatísticas
│               ├── ControlPanel.java      ← Botões + campo de entrada
│               ├── TreePanel.java         ← Canvas de renderização
│               └── TraversalPanel.java    ← Rodapé com resultado de percurso
├── compile.sh   / compile.bat
├── run.sh       / run.bat
└── README.md
```

---

## 🏗️ Arquitetura e Padrões de Projeto

| Padrão         | Onde é aplicado                                          |
|----------------|----------------------------------------------------------|
| **Observer**   | `BST` notifica `HeaderPanel`, `TreePanel`, `TraversalPanel` automaticamente |
| **MVC**        | `BST` (Model) · `BSTVisualizer` (View/Controller) · `BSTController` (Controller) |
| **Facade**     | `BSTVisualizer` orquestra todos os componentes           |
| **Strategy**   | `TreeRenderer` separa o algoritmo de desenho dos Swing components |
| **Mediator**   | `BSTController` medeia UI → Modelo com resultados tipados |

### Fluxo de dados

```
Usuário digita valor
       ↓
  ControlPanel
       ↓ chama
  BSTController.insert(rawInput)
       ↓ valida e chama
  BST.insert(val)
       ↓ notifica observers
  ┌────┴──────────────┬─────────────────┐
  ▼                   ▼                 ▼
HeaderPanel      TreePanel       TraversalPanel
(atualiza stats) (redesenha)     (reseta percurso)
```

---

## ▶️ Como Executar

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
# Compilar
javac -d out $(find src -name "*.java")

# Executar
java -cp out Main
```

> **Requisito:** Java 17+ (usa `switch` com `->`)

---

## 🎮 Funcionalidades

| Função         | Descrição                                           |
|----------------|-----------------------------------------------------|
| **Inserir**    | Insere um valor seguindo as regras da BST (sem duplicatas) |
| **Remover**    | Remove com tratamento dos 3 casos (folha, 1 filho, 2 filhos) |
| **Limpar**     | Apaga toda a árvore                                 |
| **In-Order**   | Percurso Esq → Raiz → Dir (resultado sempre ordenado) |
| **Pre-Order**  | Percurso Raiz → Esq → Dir                          |
| **Post-Order** | Percurso Esq → Dir → Raiz                          |
| **Level-Order**| BFS — nível por nível                               |
| **Clique**     | Clique em qualquer nó para selecioná-lo             |

---

## 📐 Regras da BST implementadas

- **Inserção**: valores menores vão para a subárvore esquerda, maiores para a direita
- **Busca por duplicata**: rejeita valores já existentes
- **Remoção com sucessor in-order**: ao remover um nó com dois filhos, substitui pelo menor valor da subárvore direita
- **Layout visual**: posição X calculada via índice in-order (garante sem sobreposição)

---

## 🆕 Funcionalidades Adicionadas

### 💾 Salvar em TXT (Parênteses Aninhados)
Clique em **SALVAR** para exportar a árvore. O arquivo gerado contém:
- Parênteses Aninhados compacto e indentado
- Estatísticas completas
- Tipos da árvore
- Todos os 4 percursos
- Todos os caminhos Raiz→Folha
- Tabela detalhada por nó

**Exemplo de formato:**
```
(50 (30 (20 () ()) (40 () ())) (70 (60 () ()) (80 () ())))
```

### 📊 Painel InfoPanel (lateral direito)
Exibe em tempo real:

| Seção | Conteúdo |
|---|---|
| **Propriedades** | Total de nós, folhas, internos, raiz, altura, profundidade, níveis |
| **Tipos** | Cheia ✓/✗, Completa ✓/✗, Perfeita ✓/✗, Balanceada ✓/✗, Degenerada ✓/✗ |
| **Nó Selecionado** | Nível, profundidade, altura, filhos, caminho da raiz |
| **Caminhos R→F** | Todos os caminhos raiz → folha |

### 📐 Definições implementadas

| Conceito | Definição |
|---|---|
| **Nível do nó** | Distância da raiz (raiz = nível 1) |
| **Profundidade do nó** | Número de arestas até a raiz (raiz = 0) |
| **Altura do nó** | Número de arestas até a folha mais distante (folha = 0) |
| **Altura da árvore** | Altura da raiz |
| **Profundidade máx.** | Maior profundidade entre todas as folhas |
| **Árvore Cheia** | Todo nó tem 0 ou 2 filhos |
| **Árvore Completa** | Todos os níveis preenchidos exceto último (esq→dir) |
| **Árvore Perfeita** | Full + Complete + todas as folhas no mesmo nível |
| **Árvore Balanceada** | \|h(esq) − h(dir)\| ≤ 1 para todo nó |
| **Árvore Degenerada** | Cada nó tem no máximo 1 filho |

### 📁 Novos arquivos
```
src/bst/model/BSTAnalyzer.java   ← Todas as análises e métricas
src/bst/io/TreeFileWriter.java   ← Serialização e gravação em TXT
src/bst/ui/panels/InfoPanel.java ← Painel lateral de informações
```
