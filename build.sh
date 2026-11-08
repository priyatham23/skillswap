#!/usr/bin/env bash
set -e

echo "=== SkillSwap Render Build Step ==="
mkdir -p bin
mkdir -p web/uploads/avatars
mkdir -p data

find src -name "*.java" > sources.txt
echo "Compiling $(wc -l < sources.txt) Java source files..."
javac -cp "lib/*" -d bin @sources.txt

echo "=== Build finished successfully ==="

