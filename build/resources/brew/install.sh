#!/usr/bin/env bash

prefix="$1"

# jar needed by scripts
mkdir -p "$prefix/libexec"
cp ./*.jar "$prefix/libexec"

# scripts
${HOMEBREW_RUBY_PATH} -pi.bak -e "gsub(/PREFIX/, '$prefix')" {{PROJECT}}
mkdir -p "$prefix/bin"
cp {{PROJECT}} "$prefix/bin"
