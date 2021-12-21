package com.dingdo.plugin.game.cdda.data.component

import cn.hutool.core.util.StrUtil
import com.dingdo.common.util.existOrCreate
import com.dingdo.plugin.game.cdda.data.emuns.Type
import com.dingdo.plugin.game.cdda.data.model.common.BaseData
import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import java.io.File

object DataFileLoader {

    private val objectMapper: ObjectMapper = ObjectMapper()
    val dataMap = HashMap<String, DataTypeCollection>()
    val dataIdMap = HashMap<String, BaseData>()

    init {
        objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL)
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
    }

    fun loadData(src: String) {

        val laterInitQueue = ArrayDeque<JsonNode>()

        File(src).walk()
            .filter { it.isFile }
            .forEach { jsonFile ->
                val dataStr = jsonFile.existOrCreate().readText()
                val data = objectMapper.readTree(dataStr)

                data.forEach next@{ rootNode ->
                    if (StrUtil.isNotBlank(rootNode.path("copy-from").asText())) {
                        laterInitQueue.add(rootNode)
                        return@next
                    }
                    saveModel(rootNode) { it.instanceSupplier() }
                }
            }

        initPost(laterInitQueue)
    }


    private fun saveModel(jsonNode: JsonNode, dataSupplier: (Type) -> BaseData) {
        val typeStr = jsonNode.path("type").asText()
        Type.getEnum(typeStr).map {
            val dataModel = dataSupplier.invoke(it).parse(jsonNode)
            val dataTypeCollection = dataMap.getOrPut(typeStr) { DataTypeCollection(it) }

            dataTypeCollection.dataIdMap[dataModel.id] = dataModel
            dataTypeCollection.dataNameMap[dataModel.name] = dataModel
            dataTypeCollection.dataNameMap[dataModel.alias()] = dataModel
            dataIdMap[dataModel.id] = dataModel
        }
    }


    private fun initPost(laterInitQueue: ArrayDeque<JsonNode>) {
        var finished = false
        while (laterInitQueue.isNotEmpty() && !finished) {
            finished = true
            val foreachList = laterInitQueue.toList()
            laterInitQueue.clear()

            foreachList.forEach next@{
                val parentNodeId = it.path("copy-from").asText()
                val parentNode = dataIdMap[parentNodeId]
                if (parentNode == null) {
                    laterInitQueue.add(it)
                    return@next
                }
                finished = false
                saveModel(it) { parentNode.extends() }
            }
        }
    }

    fun searchByName(name: String): BaseData? {
        return dataMap.firstNotNullOfOrNull {
            it.value.dataNameMap[name]
        }
    }
}

class DataTypeCollection constructor(val type: Type) {
    val dataIdMap = HashMap<String, BaseData>()
    val dataNameMap = HashMap<String, BaseData>()
}

fun main() {
    DataFileLoader.loadData("D:\\workspace\\springboot-webjar\\cddaData")
    val dataMap = DataFileLoader.dataMap
    println(DataFileLoader.dataIdMap["maid_dress"])
}
