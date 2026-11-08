#!/usr/bin/env bash
set -e

PORT="${PORT:-8080}"
echo "=== Starting SkillSwap on port ${PORT} ==="
exec java -cp "bin:lib/*" com.skillswap.Main

