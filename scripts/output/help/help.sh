#!/usr/bin/env bash

echo "--- help ---"
poly help :all color-mode:none > 01-help.txt

echo "--- check ---"
poly help check color-mode:none > 02-check.txt

echo "--- create ---"
poly help create color-mode:none > 03-create.txt

echo "--- create component ---"
poly help create component color-mode:none > 04-create-component.txt

echo "--- create base ---"
poly help create base color-mode:none > 05-create-base.txt

echo "--- create project ---"
poly help create project color-mode:none > 06-create-project.txt

echo "--- create workspace ---"
poly help create workspace color-mode:none > 07-create-workspace.txt

echo "--- deps ---"
poly help deps color-mode:none > 08-deps.txt

echo "--- deps :brick ---"
poly help deps :brick color-mode:none > 09-deps-brick.txt

echo "--- deps :project ---"
poly help deps :project color-mode:none > 10-deps-project.txt

echo "--- deps :workspace ---"
poly help deps :workspace color-mode:none > 11-deps-workspace.txt

echo "--- deps :project :brick ---"
poly help deps :project :brick color-mode:none > 12-deps-project-brick.txt

echo "--- diff ---"
poly help diff color-mode:none > 13-diff.txt

echo "--- info ---"
poly help info color-mode:none > 14-info.txt

echo "--- libs ---"
poly help libs color-mode:none > 15-libs.txt

echo "--- migrate ---"
poly help migrate color-mode:none > 16-migrate.txt

echo "--- prompt ---"
poly help prompt color-mode:none > 17-prompt.txt

echo "--- test ---"
poly help test color-mode:none > 18-test.txt

echo "--- ws ---"
poly help ws color-mode:none > 19-ws.txt
