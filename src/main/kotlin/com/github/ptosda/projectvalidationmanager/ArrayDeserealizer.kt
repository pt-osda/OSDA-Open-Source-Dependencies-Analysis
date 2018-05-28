package com.github.ptosda.projectvalidationmanager

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.JsonNode

class ArrayDeserealizer : JsonDeserializer<ArrayList<String>>() {

    override fun deserialize(jsonParser: JsonParser?, ctxt: DeserializationContext?): ArrayList<String> {
        val objCodec = jsonParser?.codec

        val node = objCodec?.readTree<JsonNode>(jsonParser)

        val list = ArrayList<String>()
        node?.asIterable()?.forEach{
            list.add(it.asText())
        }

        return list
    }

}