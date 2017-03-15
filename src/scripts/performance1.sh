#!/bin/sh
source="https://477-27.csse.rose-hulman.edu:8004/file.txt"
local="localhost:8004/file.txt"
count=0
avg=0
num=20
printf "running 20 GET requests...\n"
for ((i=1; i <= $num; ++i))
do
	before=$(date +%s%N)/1000000
	response=$(curl -s -X GET localhost:8004/default/file.txt)
	echo $response
	after=$(date +%s%N)/1000000
	result=$((after - before))
	printf "time taken: $result ms\n"
	avg=$((avg + result))
#	if [ "$response" = "200OK" ]
#	then 
#	    count=$((count+1))
#	else
#	    count=$((count-1))
#	    echo "failed"
#	fi
done
avg=$((avg / num))
printf "done.\n"
echo "avgerage time: $avg ms"