package io.ballerina.lib.avro.deserialize.visitor;

import io.ballerina.lib.avro.deserialize.ArrayDeserializer;
import io.ballerina.lib.avro.deserialize.MapDeserializer;
import io.ballerina.lib.avro.deserialize.RecordDeserializer;
import io.ballerina.lib.avro.deserialize.StringDeserializer;
import io.ballerina.runtime.api.creators.ValueCreator;
import io.ballerina.runtime.api.types.RecordType;
import io.ballerina.runtime.api.types.Type;
import io.ballerina.runtime.api.values.BMap;
import io.ballerina.runtime.api.values.BString;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;

import java.nio.ByteBuffer;

import static io.ballerina.lib.avro.deserialize.visitor.DeserializeVisitor.extractMapType;
import static io.ballerina.lib.avro.deserialize.visitor.DeserializeVisitor.extractRecordType;
import static io.ballerina.lib.avro.deserialize.visitor.UnionRecordUtils.visitUnionRecords;
import static io.ballerina.runtime.api.utils.StringUtils.fromString;

public class RecordUtils {

    public static void processMapField(BMap<BString, Object> avroRecord,
                                 Schema.Field field, Object fieldData) throws Exception {
        Type mapType = extractMapType(avroRecord.getType());
        MapDeserializer mapDeserializer = new MapDeserializer(field.schema(), mapType);
        Object fieldValue = mapDeserializer.visit(new DeserializeVisitor(), fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processArrayField(BMap<BString, Object> avroRecord,
                                   Schema.Field field, Object fieldData) throws Exception {
        ArrayDeserializer arrayDes = new ArrayDeserializer(field.schema(), avroRecord.getType());
        Object fieldValue = arrayDes.visit(new DeserializeVisitor(), (GenericData.Array<Object>) fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processBytesField(BMap<BString, Object> avroRecord, Schema.Field field, Object fieldData) {
        ByteBuffer byteBuffer = (ByteBuffer) fieldData;
        Object fieldValue = ValueCreator.createArrayValue(byteBuffer.array());
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processRecordField(BMap<BString, Object> avroRecord,
                                    Schema.Field field, Object fieldData) throws Exception {
        Type recType = extractRecordType((RecordType) avroRecord.getType());
        RecordDeserializer recordDes = new RecordDeserializer(field.schema(), recType);
        Object fieldValue = recordDes.visit(new DeserializeVisitor(), fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processStringField(BMap<BString, Object> avroRecord,
                                    Schema.Field field, Object fieldData) {
        StringDeserializer stringDes = new StringDeserializer();
        Object fieldValue = stringDes.visit(new DeserializeVisitor(), fieldData);
        avroRecord.put(fromString(field.name()), fieldValue);
    }

    public static void processUnionField(Type type, BMap<BString, Object> avroRecord,
                                   Schema.Field field, Object fieldData) throws Exception {
        visitUnionRecords(type, avroRecord, field, fieldData);
    }
}
