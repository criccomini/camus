package com.linkedin.camus.etl.kafka.coders;

import kafka.message.Message;

import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;

import com.linkedin.camus.coders.CamusWrapper;

public class LatestSchemaKafkaAvroMessageDecoder extends KafkaAvroMessageDecoder
{

	@Override
	public CamusWrapper<Record> decode(Message message)
	{
		try
		{
			GenericDatumReader<Record> reader = new GenericDatumReader<Record>();
			
			Schema schema = super.registry.getLatestSchemaByTopic(super.topicName).getSchema();
			
			reader.setSchema(schema);
			
			return new CamusWrapper<Record>(reader.read(
                    null, 
                    decoderFactory.jsonDecoder(
                            schema, 
                            new String(
                                    message.payload().array(), 
                                    Message.payloadOffset(message.magic()),
                                    message.payloadSize()
                            )
                    )
            ));
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
}