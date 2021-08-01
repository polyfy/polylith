
output=$(pwd)/output

cd ../examples/local-dep
ws=$(pwd)

echo $output

echo "1/6 info"
poly info fake-sha:aaaaa > $output/local-dep/info.txt
echo "2/6 libs"
poly libs > $output/local-dep/libs.txt
echo "3/6 deps"
poly deps > $output/local-dep/deps.txt
echo "4/6 diff"
poly diff since:0aaeb58 > $output/local-dep/diff.txt
echo "5/6 ws"
poly ws out:$output/local-dep/ws.edn replace:$HOME:USER-HOME:$ws:WS-HOME color-mode:none
echo "6/6 test"
poly test :dev > $output/local-dep/test.txt
