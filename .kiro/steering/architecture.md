---
inclusion: always
---

# Arquitetura — BST Visualizer

## Padrões de Projeto Adotados

| Padrão    | Onde é aplicado                                                        |
|-----------|------------------------------------------------------------------------|
| Observer  | `BST` notifica painéis via `BSTObserver` a cada mutação               |
| MVC       | `BST` (Model) · `BSTVisualizer` (View) · `BSTController` (Controller) |
| Strategy  | `TreeRenderer` separa algoritmo de desenho dos componentes Swing       |
| Mediator  | `BSTController` medeia UI → Modelo com resultados tipados              |
| Facade    | `BSTVisualizer` orquestra painéis e serviços                           |

## Regras de Dependência

```
ui  →  controller  →  model
ui  →  renderer
ui  →  theme
io  →  model
```

- `model` não conhece `ui`, `controller` nem `io`
- `renderer` não conhece `controller` nem `io`
- `theme` não tem dependências internas
- Dependências só fluem para baixo — nunca para cima

## Modelo (model/)

- `BSTNode` — estrutura de dados pura, sem lógica
- `BST` — operações mutáveis + notificação de observers; não faz análise
- `AVLTree extends BST` — sobrescreve `insert`, `remove` e `mirror` com lógica AVL
- `BSTAnalyzer` — serviço stateless de análise; recebe `BSTNode root` como parâmetro, nunca guarda estado

## Controller (controller/)

- `BSTController` — valida entrada, converte tipos, delega ao modelo
- Retorna tipos de resultado (`InsertResult`, `RemoveResult`) em vez de lançar exceções para a UI
- Nunca acessa componentes Swing diretamente

## UI (ui/ e ui/panels/)

- `BSTVisualizer` — JFrame principal; monta painéis, registra observers, gerencia callbacks
- Cada painel tem responsabilidade única e implementa `BSTObserver` quando precisa reagir a mudanças
- Comunicação entre painéis via callbacks (`Runnable`, `Consumer`, `BiConsumer`) — sem referências cruzadas diretas

## IO (io/)

- `TreeFileWriter` — stateless; recebe `BST` e `BSTAnalyzer`, gera conteúdo e salva
- `TreeFileReader` — stateless; lê arquivo, faz parse e chama `bst.insert()` em pré-ordem
- Formato: Parênteses Aninhados — `(val (esq) (dir))`, árvore vazia = `()`

## Adicionando Novos Tipos de Árvore

1. Criar classe em `bst/model/` estendendo `BST`
2. Adicionar entrada no enum `TreeTypeDialog.TreeType`
3. Instanciar no `BSTVisualizer` conforme o tipo selecionado
4. Nenhuma outra classe precisa ser alterada
