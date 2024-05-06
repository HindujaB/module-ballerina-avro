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

package io.ballerina.lib.avro;

import io.ballerina.lib.avro.deserialize.DeserializeFactory;
import io.ballerina.lib.avro.deserialize.Deserializer;
import io.ballerina.lib.avro.serialize.MessageFactory;
import io.ballerina.lib.avro.serialize.Serializer;
import io.ballerina.lib.avro.visitor.DeserializeVisitor;
import io.ballerina.lib.avro.visitor.SerializeVisitor;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.values.BArray;
import io.ballerina.runtime.api.values.BObject;
import io.ballerina.runtime.api.values.BString;
import io.ballerina.runtime.api.values.BTypedesc;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;

import java.io.ByteArrayOutputStream;
import java.util.Objects;

import static io.ballerina.lib.avro.Utils.AVRO_SCHEMA;
import static io.ballerina.lib.avro.Utils.DESERIALIZATION_ERROR;
import static io.ballerina.lib.avro.Utils.SERIALIZATION_ERROR;
import static io.ballerina.lib.avro.Utils.createError;

public final class Avro {

    private Avro() {}

    public static void generateSchema(BObject schemaObject, BString schema) {
        Schema.Parser parser = new Schema.Parser();
        Schema nativeSchema = parser.parse(schema.getValue());
        schemaObject.addNativeData(AVRO_SCHEMA, nativeSchema);
    }

    public static Object toAvro(BObject schemaObject, Object data) {
        Schema schema = (Schema) schemaObject.getNativeData(AVRO_SCHEMA);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            SerializeVisitor serializeVisitor = new SerializeVisitor();
            Serializer serializer = MessageFactory.createMessage(schema);
            Object avroData = Objects.requireNonNull(serializer).generateMessage(serializeVisitor, data);
            DatumWriter<Object> writer = new GenericDatumWriter<>(schema);
            BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(outputStream, null);
            writer.write(avroData, encoder);
            encoder.flush();
            byte[] bytes = outputStream.toByteArray();
            return ValueCreator.createArrayValue(bytes);
        } catch (Exception e) {
            return Utils.createError(SERIALIZATION_ERROR, e);
        }
    }

    public static Object fromAvro(BObject schemaObject, BArray payload, BTypedesc typeParam) {
        Schema schema = (Schema) schemaObject.getNativeData(AVRO_SCHEMA);
        byte[] avroBytes = payload.getByteArray();
        if (Schema.Type.FIXED.equals(schema.getType())) {
            return ValueUtils.convert(ValueCreator.createArrayValue(avroBytes), typeParam.getDescribingType());
        }
        JsonNode deserializedJsonString;
        try {
            AvroMapper mapper = new AvroMapper();
            deserializedJsonString = mapper.readerFor(Object.class).with(new AvroSchema(schema)).readTree(avroBytes);
        } catch (IOException e) {
            return Utils.createError(DESERIALIZATION_ERROR, e);
        }
        Object jsonObject = JsonUtils.parse(deserializedJsonString.toPrettyString());
        return ValueUtils.convert(jsonObject, typeParam.getDescribingType());
    }

    private static Object generateJsonObject(Object data, Schema schema,
                                             ObjectMapper objectMapper) throws JsonProcessingException {
        if (Schema.Type.NULL.equals(schema.getType()) || Schema.Type.FIXED.equals(schema.getType())) {
            return data;
        } else if (Schema.Type.STRING.equals(schema.getType()) || Schema.Type.ENUM.equals(schema.getType())) {
            return objectMapper.readValue("\"" + data + "\"", Object.class);
        }
        Object jsonString = JsonUtils.parse(StringUtils.getJsonString(data));
        return objectMapper.readValue(jsonString.toString(), Object.class);
    }
}
