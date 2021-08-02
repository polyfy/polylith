
output=$(pwd)/output

cd ../examples/local-dep-old-format
ws=$(pwd)

echo $output

echo "1/5 info"
poly info fake-sha:aaaaa > $output/local-dep-old-format/info.txt
echo "2/5 libs"
poly libs > $output/local-dep-old-format/libs.txt
echo "3/5 deps"
poly deps > $output/local-dep-old-format/deps.txt
echo "4/5 ws"
poly ws out:$output/local-dep-old-format/ws.edn replace:$HOME:USER-HOME:$ws:WS-HOME color-mode:none
echo "5/5 test"
poly test :dev > $output/local-dep-old-format/test.txt
