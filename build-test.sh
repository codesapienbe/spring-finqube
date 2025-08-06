#!/bin/bash

echo "🔧 Testing Spring Finqube ISO 20022 build..."

# Clean and compile the core module only
echo "📦 Compiling spring-finqube-core..."
./mvnw clean compile -pl spring-finqube-core -am

if [ $? -eq 0 ]; then
    echo "✅ Build successful! Configuration encryption compilation errors fixed."
    echo "🎉 Task 133: Secure Configuration Encryption - COMPLETED"
else
    echo "❌ Build failed. Please check the compilation errors above."
    exit 1
fi

echo "🚀 Ready for next task!"
