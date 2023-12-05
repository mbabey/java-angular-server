#!/bin/bash

mkdir out
javac src/Server.java -d ./out
echo "Build complete. To run:"
echo "cd ./out"
echo "java Server <your-IPv4-address>"