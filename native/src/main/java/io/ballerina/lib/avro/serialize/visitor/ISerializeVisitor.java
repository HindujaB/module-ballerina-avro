/*
 * Copyright (c) 2024, WSO2 LLC. (http://www.wso2.com)
 *
 * WSO2 LLC. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package io.ballerina.lib.avro.serialize.visitor;

import io.ballerina.lib.avro.serialize.ArraySerializer;
import io.ballerina.lib.avro.serialize.EnumSerializer;
import io.ballerina.lib.avro.serialize.FixedSerializer;
import io.ballerina.lib.avro.serialize.PrimitiveSerializer;
import io.ballerina.lib.avro.serialize.RecordSerializer;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BMap;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;

public interface ISerializeVisitor {

    GenericRecord visit(RecordSerializer recordSerializer, BMap<?, ?> data) throws Exception;
    GenericData.Array<Object> visit(ArraySerializer arraySerializer, BArray data);
    Object visit(EnumSerializer enumSerializer, Object data);
    GenericData.Fixed visit(FixedSerializer fixedSerializer, Object data);
    Object visit(PrimitiveSerializer primitiveSerializer, Object data) throws Exception;
}
