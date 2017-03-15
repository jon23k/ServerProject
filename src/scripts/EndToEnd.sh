#!/bin/sh
numPassed=0
#printf "running POST request\n"
response=$(curl -s -H 'filename: file.txt' -H 'Content-Type: text/plain' -X POST -d "post data" localhost:8004/)
#echo "$response"
if [ "$response" = "post data" ]
then 
    numPassed=$((numPassed+1))
#else
    #echo "failed\n"
fi

#printf "running GET request\n"
response=$(curl -s -X GET localhost:8004/file.txt)
#echo "$response"
if [ "$response" = "post data" ]
then 
    numPassed=$((numPassed+1))
#else
    #echo "failed\n"
fi

#printf "running PUT request\n"
response=$(curl -s -H 'filename: file.txt' -H 'Content-Type: text/plain' -X PUT -d "put data" localhost:8004/)
#echo "$response"
if [ "$response" = "put data" ]
then 
    numPassed=$((numPassed+1))
#else
    #echo "failed\n"
fi

#printf "running GET request\n"
response=$(curl -s -X GET localhost:8004/file.txt)
#echo "$response"
if [ "$response" = "put data" ]
then 
    numPassed=$((numPassed+1))
#else
    #echo "failed\n"
fi

#printf "running HEAD request\n"
response=$(curl -s -X HEAD -i localhost:8004/file.txt)
#echo "$response"
if [ "$response" = "post data" ]
then 
    numPassed=$((numPassed+1))
#else
    #echo "failed\n"
fi

#printf "running DELETE request\n"
response=$(curl -s -X DELETE localhost:8004/file.txt)
#echo "$response"
if [ "$response" = "" ]
then 
    numPassed=$((numPassed+1))
#else
    #echo "failed\n"
fi

#printf "running GET request\n"
response=$(curl -s -X GET localhost:8004/file.txt)
#echo "$response"
if [ "$response" = "" ]
then 
    numPassed=$((numPassed+1))
#else
    #echo "failed\n"
fi

if [ "$numPassed" = 7 ]
then
    echo Success
else
    echo Failed
fi
