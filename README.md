# Ballerina Avro Module

[![Build](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-timestamped-master.yml)](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-timestamped-master.yml)
[![codecov](https://codecov.io/gh/ballerina-platform/module-ballerina-avro/branch/master/graph/badge.svg)](https://codecov.io/gh/ballerina-platform/module-ballerina-avro)
[![Trivy](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/trivy-scan.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/trivy-scan.yml)
[![GraalVM Check](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-with-bal-test-graalvm.yml/badge.svg)](https://github.com/ballerina-platform/module-ballerina-avro/actions/workflows/build-with-bal-test-graalvm.yml)
[![GitHub Last Commit](https://img.shields.io/github/last-commit/ballerina-platform/module-ballerina-avro.svg)](https://github.com/ballerina-platform/module-ballerina-avro/commits/master)
[![Github issues](https://img.shields.io/github/issues/ballerina-platform/ballerina-standard-library/module/avro.svg?label=Open%20Issues)](https://github.com/ballerina-platform/ballerina-standard-library/labels/module%2Favro)
[![codecov](https://codecov.io/gh/ballerina-platform/module-ballerina-avro/branch/master/graph/badge.svg)](https://codecov.io/gh/ballerina-platform/module-ballerina-avro)

Avro is an open-source data serialization system that enables efficient binary serialization and deserialization. It allows users to define schemas for structured data, providing better representation and fast serialization/deserialization. Avro's schema evolution capabilities ensure compatibility and flexibility in evolving data systems.

The Ballerina Avro module provides the capability to efficiently serialize and deserialize data using Avro schemas.

### Client

The Client is will take the Avro schema in string format. And will return an error if the schema is not a valid Avro schema. The client can be used to serialize and deserilize data and the data should be in the correct format.

A `Client` can be defined using the string value of an Avro schema as shown below:

```ballerina
avro:Avro avro = check new("avro-schema-string");
```

### APIs associated with Avro

- **toAvro**: Serializes the given data according to the Avro format.
- **fromAvro**: Deserializes the given Avro encoded message to the given data type.

#### `toAvro`

Serializes the given data according to the Avro format.

```ballerina
import ballerina/avro;

public function main() returns error? {
    avro:Avro avro = check new(string `{"type": "int", "name" : "intValue", "namespace": "data" }`);
    int value = 5;
    byte[] serializeData = check avro.toAvro(value);
}
```

#### `fromAvro`

Deserializes the given Avro encoded message to the given data type.

```ballerina
import ballerina/avro;

public function main() returns error? {
    byte[] data = // Avro encoded message ;
    int deserializeData = check avro.fromAvro(data);
}
```
