// Copyright (c) 2024 WSO2 LLC. (http://www.wso2.com).
//
// WSO2 LLC. licenses this file to you under the Apache License,
// Version 2.0 (the "License"); you may not use this file except
// in compliance with the License.
// You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

import ballerina/test;
import ballerina/io;

@test:Config{
    groups: ["record", "bytes"]
}
public isolated function testRecordsWithBytes() returns error? {
    string schema = string `
        {
            "namespace": "example.avro",
            "type": "record",
            "name": "Student",
            "fields": [
                {"name": "name", "type": "string"},
                {"name": "favorite_color", "type": "bytes"}
            ]
        }`;

    Student1 student = {
        name: "Liam",
        favorite_color: "yellow".toBytes()
    };

    Schema avro = check new(schema);
    byte[] serializedValue = check avro.toAvro(student);
    Student1 deserializedValue = check avro.fromAvro(serializedValue);
    test:assertEquals(deserializedValue, student);
}

@test:Config {
    groups: ["array", "byte"]
}
public isolated function testArraysWithBytes() returns error? {
    string schema = string `
        {
            "type": "array",
            "name" : "floatArray", 
            "namespace": "data", 
            "items": "bytes"
        }`;

    byte[][] numbers = ["22.4".toBytes(), "556.84350".toBytes(), "78.0327".toBytes()];

    Schema avro = check new (schema);
    byte[] serializedValue = check avro.toAvro(numbers);
    byte[][] deserializedValue = check avro.fromAvro(serializedValue);
    test:assertEquals(deserializedValue, numbers);
}

@test:Config {
    groups: ["primitive", "bytes"]
}
public isolated function testBytes() returns error? {
    string schema = string `
        {
            "type": "bytes",
            "name" : "byteValue", 
            "namespace": "data"
        }`;

    byte[] value = "5".toBytes();

    Schema avro = check new (schema);
    byte[] serializedValue = check avro.toAvro(value);
    byte[] deserializedValue = check avro.fromAvro(serializedValue);
    test:assertEquals(deserializedValue, value);
}

@test:Config {
    groups: ["record", "map", "bytes"]
}
public isolated function testNestedRecordsWithBytes() returns error? {
    string jsonFileName = string `tests/resources/schema_bytes.json`;
    string schema = (check io:fileReadJson(jsonFileName)).toString();

    Lecturer4 lecturer4 = {
        name: {
            "John": 1, 
            "Sam": 2, 
            "Liam": 3
        },
        byteData: "s".toBytes().cloneReadOnly(),
        instructor: {
            byteData: "ddd".toBytes().cloneReadOnly()
        }
    };

    Schema avro = check new (schema);
    byte[] serialize = check avro.toAvro(lecturer4);
    Lecturer4 deserializedValue = check avro.fromAvro(serialize);
    // deserialize.instructor.student.name = "Sam";
    test:assertEquals(deserializedValue, lecturer4);
}
