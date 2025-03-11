#!/bin/env sh

# Collects a list of git authors, one per line.

git log --format='%an' \
  | sort \
  | uniq \
  | grep --invert \
    -e "^domi41$" \
    -e "^William$"
