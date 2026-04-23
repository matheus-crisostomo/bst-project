---
inclusion: always
---

# Contribuição e Git — BST Visualizer

## Compilar e Executar

```bash
# Linux/macOS
./compile.sh && ./run.sh

# Windows
compile.bat && run.bat
```

Requisito: Java 17+. Sem dependências externas.

## Commits

Formato: `tipo: descrição curta em português`

| Tipo     | Quando usar                                      |
|----------|--------------------------------------------------|
| `feat`   | Nova funcionalidade                              |
| `fix`    | Correção de bug ou lógica incorreta              |
| `refactor` | Mudança de código sem alterar comportamento    |
| `style`  | Formatação, espaçamento, sem mudança de lógica   |
| `docs`   | README, comentários, Javadoc                     |
| `chore`  | Configuração, scripts, .gitignore                |

Exemplos:
```
feat: adicionar suporte a árvore Red-Black
fix: corrigir isFull retornando true para árvore vazia
refactor: extrair lógica de balanceamento para método privado
chore: adicionar .gitignore para arquivos compilados
```

Para commits com mais contexto, use corpo separado por linha em branco:
```
fix: corrigir definição de isDegenerate

- Árvore vazia agora retorna false
- Árvore com 1 nó agora retorna false
- Extraído isDegenerateRec como helper privado
```

## O que NÃO commitar

O `.gitignore` já exclui:
- `out/` — bytecode compilado
- `*.class` — arquivos de classe avulsos

Não adicionar ao repositório: arquivos `.txt` de árvores exportadas, arquivos de IDE (`.idea/`, `.vscode/`).

## Checklist antes de commitar

- [ ] Código compila sem erros (`./compile.sh`)
- [ ] Nenhuma cor ou fonte hardcoded fora de `Theme`
- [ ] Novos métodos públicos têm Javadoc
- [ ] Dependências respeitam o fluxo `ui → controller → model`
- [ ] Nenhum `System.out.println` de debug esquecido
