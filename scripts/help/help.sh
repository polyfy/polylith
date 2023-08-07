#!/usr/bin/env bash

echo "--- help ---"
poly help :all color-mode:none :fake-poly > ../output/help/help.txt

echo "--- check ---"
poly help check color-mode:none > ../output/help/check.txt

echo "--- create ---"
poly help create color-mode:none > ../output/help/create.txt

echo "--- create component ---"
poly help create component color-mode:none > ../output/help/create-component.txt

echo "--- create base ---"
poly help create base color-mode:none > ../output/help/create-base.txt

echo "--- create project ---"
poly help create project color-mode:none > ../output/help/create-project.txt

echo "--- create workspace ---"
poly help create workspace color-mode:none > ../output/help/create-workspace.txt

echo "--- deps ---"
poly help deps color-mode:none :fake-poly > ../output/help/deps.txt

echo "--- deps :brick ---"
poly help deps :brick color-mode:none > ../output/help/deps-brick.txt

echo "--- deps :project ---"
poly help deps :project color-mode:none > ../output/help/deps-project.txt

echo "--- deps :workspace ---"
poly help deps :workspace color-mode:none > ../output/help/deps-workspace.txt

echo "--- deps :project :brick ---"
poly help deps :project :brick color-mode:none > ../output/help/deps-project-brick.txt

echo "--- diff ---"
poly help diff color-mode:none > ../output/help/diff.txt

echo "--- doc ---"
poly help doc color-mode:none > ../output/help/doc.txt

echo "--- info ---"
poly help info color-mode:none :fake-poly > ../output/help/info.txt

echo "--- tap ---"
poly help tap color-mode:none > ../output/help/tap.txt

echo "--- libs ---"
poly help libs color-mode:none :fake-poly > ../output/help/libs.txt

echo "--- migrate ---"
poly help migrate color-mode:none > ../output/help/migrate.txt

echo "--- overview ---"
poly help overview color-mode:none > ../output/help/overview.txt

echo "--- shell ---"
poly help shell color-mode:none > ../output/help/shell.txt

echo "--- switch-ws ---"
poly help switch-ws color-mode:none > ../output/help/switch-ws.txt

echo "--- test ---"
poly help test color-mode:none > ../output/help/test.txt

echo "--- ws ---"
poly help ws color-mode:none > ../output/help/ws.txt
