#!/bin/bash

# Change to the src directory
cd src

# Compile all Java files with the MySQL connector in the classpath
javac -cp "../lib/mysql-connector-j-9.3.0.jar:." *.java

# Run the Driver class with the MySQL connector in the classpath
java -cp "../lib/mysql-connector-j-9.3.0.jar:." Driver