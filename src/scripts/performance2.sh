#!/bin/bash
source="https://477-27.csse.rose-hulman.edu:8004/file.txt"
local="localhost:8004/file.txt"

count=0

time=10
start=$(date +%s)
printf "running GET requests...\n"
while [ $(date +%s) -lt $((start + time)) ];
do
	response=$(curl -s -X GET localhost:8004/file.txt)
	count=$((count+1))
done
printf "done.\n"
echo "$count requests handled in $time seconds"