#!/bin/bash

# Build the core
mvn -pl spring-finqube-core -DskipTests clean package
if [ $? -ne 0 ]; then
    echo "Failed to build spring-finqube-core"
    exit 1
fi

# Build the autoconfigure
mvn -pl spring-finqube-autoconfigure -DskipTests clean package
if [ $? -ne 0 ]; then
    echo "Failed to build spring-finqube-autoconfigure"
    exit 1
fi


# Build the starter
mvn -pl spring-finqube-starter -DskipTests clean package
if [ $? -ne 0 ]; then
    echo "Failed to build spring-finqube-starter"
    exit 1
fi

# Build the examples
mvn -pl spring-finqube-examples -DskipTests clean package
if [ $? -ne 0 ]; then
    echo "Failed to build spring-finqube-examples"
    exit 1
fi

# Build the admin dashboard
mvn -pl spring-finqube-admin-gwt -DskipTests -Pgwt clean package
if [ $? -ne 0 ]; then
    echo "Failed to build spring-finqube-admin-gwt"
    exit 1
fi

# Run the admin dashboard
mvn -pl spring-finqube-admin-gwt -DskipTests spring-boot:run
if [ $? -ne 0 ]; then
    echo "Failed to run spring-finqube-admin-gwt"
    exit 1
fi

echo "Open your browser: http://localhost:8080 to access the admin dashboard."