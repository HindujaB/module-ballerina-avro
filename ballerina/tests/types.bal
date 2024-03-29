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

public type Student record {
    string name;
    string subject;
};

public type Person record {
    string name;
    int age;
};

public type Course record {
    string? name;
    int? credits;
};

public type Instructor record {
    string? name;
    Student student;
};

public type Lecturer record {
    string? name;
    Instructor instructor;
};

public type Color record {
    string? name;
    string[] colors;
};

public type ByteRecord record {
    string name;
    byte bytez;
};

public type FixedRec record {
    byte[] fixed_field;
    string other_field;
};

enum Numbers {
    ONE,
    TWO,
    THREE,
    FOUR
};
