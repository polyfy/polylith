
output=$(pwd)/output

cd ../examples/local-dep-old-format

echo $output

echo "1/6 info"
poly info fake-sha:aaaaa > $output/local-dep-old-format/info.txt
echo "2/6 libs"
poly libs > $output/local-dep-old-format/libs.txt
echo "3/6 deps"
poly deps > $output/local-dep-old-format/deps.txt
echo "4/6 diff"
poly diff since:0aaeb58 > $output/local-dep-old-format/diff.txt
echo "5/6 ws"
poly ws out:$output/local-dep-old-format/ws.edn :user-home
echo "6/6 test"
poly test :dev > $output/local-dep-old-format/test.txt
