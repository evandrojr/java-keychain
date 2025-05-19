#!/bin/bash
# Script para recriar e dar push na mesma tag repetidamente
# Uso: ./push-tag.sh v1.0.0

tag="$1"
if [ -z "$tag" ]; then
  echo "Uso: $0 <tag>"
  exit 1
fi

echo "Removendo tag $tag do remoto..."
git push --delete origin "$tag"

echo "Removendo tag $tag localmente..."
git tag -d "$tag"

echo "Criando tag $tag novamente..."
git tag "$tag"

echo "Enviando tag $tag para o remoto..."
git push origin "$tag"

echo "Pronto! Tag $tag reenviada."
