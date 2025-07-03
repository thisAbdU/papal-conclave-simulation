#!/bin/bash

# Papal Conclave Simulation Build Script

echo "🏛️  Papal Conclave Simulation"
echo "================================"

# Check if Java is installed
if ! command -v javac &> /dev/null; then
    echo "❌ Error: Java compiler (javac) not found!"
    echo "Please install Java 8 or higher."
    exit 1
fi

# Compile the project
echo "📦 Compiling PapalConclave.java..."
javac PapalConclave.java

if [ $? -eq 0 ]; then
    echo "✅ Compilation successful!"
    
    # Check if user wants to run the simulation
    if [ "$1" = "--run" ] || [ "$1" = "-r" ]; then
        echo ""
        echo "🚀 Starting simulation..."
        echo "Press Ctrl+C to stop early"
        echo ""
        java PapalConclave
    else
        echo ""
        echo "💡 To run the simulation, use:"
        echo "   ./build.sh --run"
        echo "   or"
        echo "   java PapalConclave"
    fi
else
    echo "❌ Compilation failed!"
    exit 1
fi 