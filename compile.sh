#!/bin/bash
echo "┌─────────────────────────────────────────┐"
echo "│  Compilando BST Visualizer...           │"
echo "└─────────────────────────────────────────┘"

rm -rf out
mkdir -p out

if javac -encoding UTF-8 -d out $(find src -name "*.java" | sort); then
    echo ""
    echo "✔  Compilação concluída! Execute: ./run.sh"
else
    echo ""
    echo "✖  Erro na compilação."
    exit 1
fi
