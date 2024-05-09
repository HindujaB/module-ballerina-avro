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

package io.ballerina.lib.avro.deserialize.visitor;

import io.ballerina.lib.avro.deserialize.RecordDeserializer;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.utils.StringUtils;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericEnumSymbol;
import org.apache.avro.generic.GenericFixed;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.util.Utf8;

import java.nio.ByteBuffer;
import java.util.Map;

public class UnionRecordUtils {

    public static void visitUnionRecords(Type type, BMap<BString, Object> ballerinaRecord,
                                         Schema.Field field, Object fieldData) throws Exception {
        for (Schema schemaType : field.schema().getTypes()) {
            if (fieldData == null) {
                ballerinaRecord.put(StringUtils.fromString(field.name()), null);
                break;
            }
            switch (schemaType.getType()) {
                case BYTES:
                    handleBytesField(field, fieldData, ballerinaRecord);
                    break;
                case FIXED:
                    handleFixedField(field, fieldData, ballerinaRecord);
                    break;
                case ARRAY:
                    handleArrayField(field, fieldData, ballerinaRecord, schemaType);
                    break;
                case MAP:
                    handleMapField(field, fieldData, ballerinaRecord);
                    break;
                case RECORD:
                    handleRecordField(type, field, fieldData, ballerinaRecord, schemaType);
                    break;
                case STRING:
                    handleStringField(field, fieldData, ballerinaRecord);
                    break;
                case INT, LONG:
                    handleIntegerField(field, fieldData, ballerinaRecord);
                    break;
                case FLOAT, DOUBLE:
                    handleFloatField(field, fieldData, ballerinaRecord);
                    break;
                case ENUM:
                    handleEnumField(field, fieldData, ballerinaRecord);
                    break;
                default:
                    handleDefaultField(field, fieldData, ballerinaRecord);
            }
        }
    }

    private static void handleDefaultField(Schema.Field field, Object fieldData,
                                           BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof Boolean) {
            ballerinaRecord.put(StringUtils.fromString(field.name()), fieldData);
        }
    }

    private static void handleEnumField(Schema.Field field, Object fieldData, BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof GenericEnumSymbol<?>) {
            ballerinaRecord.put(StringUtils.fromString(field.name()), StringUtils.fromString(fieldData.toString()));
        }
    }

    private static void handleFloatField(Schema.Field field, Object fieldData, BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof Double) {
            ballerinaRecord.put(StringUtils.fromString(field.name()), fieldData);
        } else {
            ballerinaRecord.put(StringUtils.fromString(field.name()), Double.parseDouble(fieldData.toString()));
        }
    }

    private static void handleIntegerField(Schema.Field field, Object fieldData,
                                           BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof Integer || fieldData instanceof Long) {
            ballerinaRecord.put(StringUtils.fromString(field.name()), ((Number) fieldData).longValue());
        }
    }

    private static void handleStringField(Schema.Field field, Object fieldData, BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof Utf8) {
            ballerinaRecord.put(StringUtils.fromString(field.name()), StringUtils.fromString(fieldData.toString()));
        }
    }

    public static void handleRecordField(Type type, Schema.Field field, Object fieldData,
                                         BMap<BString, Object> ballerinaRecord, Schema schemaType) throws Exception {
        if (fieldData instanceof GenericRecord) {
            RecordDeserializer recordDes = new RecordDeserializer(schemaType, type);
            Object fieldValue = recordDes.visit(new DeserializeVisitor(), (GenericRecord) fieldData);
            ballerinaRecord.put(StringUtils.fromString(field.name()), fieldValue);
        }
    }

    private static void handleMapField(Schema.Field field, Object fieldData, BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof Map<?, ?>) {
            BMap<BString, Object> avroMap = ValueCreator.createMapValue();
            Object[] keys = ((Map<String, Object>) fieldData).keySet().toArray();
            for (Object key : keys) {
                avroMap.put(StringUtils.fromString(key.toString()),
                        ((Map<String, Object>) fieldData).get(key));
            }
            ballerinaRecord.put(StringUtils.fromString(field.name()), avroMap);
        }
    }

    private static void handleBytesField(Schema.Field field, Object fieldData, BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof ByteBuffer) {
            BArray byteArray = ValueCreator.createArrayValue(((ByteBuffer) fieldData).array());
            ballerinaRecord.put(StringUtils.fromString(field.name()), byteArray);
        }
    }

    private static void handleFixedField(Schema.Field field, Object fieldData, BMap<BString, Object> ballerinaRecord) {
        if (fieldData instanceof GenericFixed) {
            BArray byteArray = ValueCreator.createArrayValue(((GenericData.Fixed) fieldData).bytes());
            ballerinaRecord.put(StringUtils.fromString(field.name()), byteArray);
        }
    }

    private static void handleArrayField(Schema.Field field, Object fieldData,
                                         BMap<BString, Object> ballerinaRecord, Schema schemaType) {
        if (fieldData instanceof GenericData.Array<?>) {
            Object[] objectArray = ((GenericData.Array<?>) fieldData).toArray();
            if (schemaType.getElementType().getType().equals(Schema.Type.STRING)
                    || schemaType.getElementType().getType().equals(Schema.Type.ENUM)) {
                BString[] stringArray = new BString[objectArray.length];
                BArray ballerinaArray = ValueCreator.createArrayValue(stringArray);
                int i = 0;
                for (Object obj : objectArray) {
                    stringArray[i] = StringUtils.fromString(obj.toString());
                    i++;
                }
                ballerinaRecord.put(StringUtils.fromString(field.name()), ballerinaArray);
            } else {
                ballerinaRecord.put(StringUtils.fromString(field.name()), fieldData);
            }
        }
    }
}
