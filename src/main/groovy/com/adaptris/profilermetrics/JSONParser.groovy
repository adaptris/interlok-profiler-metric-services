package com.adaptris.profilermetrics

import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.util.logging.Log

@Log
class JSONParser {
    def CSV_DELIMETER  = ", "
    def LINE_DELIMETER = "\n"

    def jsonSlurper
    def jsonTreeWalker


    JSONParser() {
        this(null, null)
    }

    JSONParser(List<String> includePatterns, List<String> excludePatterns) {
        jsonSlurper = new JsonSlurper()
        jsonTreeWalker = new JSONTreeWalker(includePatterns, excludePatterns)
    }

    // get datestamp value
    def getDatestampValue(Map jsonMap) {
        jsonMap.datetimestamp
    }

    // find all services nodes
    def findAllServices(Map jsonMap) {
        jsonTreeWalker.processAdapters(jsonMap)
    }

    // format out the data
    private def formatJsonToCSV(def jsonFileMap) {
        def datestampValue = getDatestampValue(jsonFileMap)
        def serviceDetails = findAllServices(jsonFileMap)
        def outputLines = []
        serviceDetails.each({ it ->
            outputLines << ([datestampValue, it.adapter, it.channel, it.workflow, it.uniqueId, it.serviceClass, it.timeTaken, it.msgCount].join(CSV_DELIMETER))
        })
        outputLines.join(LINE_DELIMETER) + LINE_DELIMETER
    }

    private def formatOutSingleJsonLine(def datestampValue, def singleMetricsItem) {
        return [datestamp: datestampValue,
         adapter: singleMetricsItem.adapter,
         channel: singleMetricsItem.channel,
         workflow: singleMetricsItem.workflow,
         uniqueId: singleMetricsItem.uniqueId,
         timeTaken: singleMetricsItem.timeTaken,
         msgCount: singleMetricsItem.msgCount]
    }

    // format out the data
    private def formatJsonToJson(def jsonFileMap) {
        def datestampValue = getDatestampValue(jsonFileMap)
        def serviceDetails = findAllServices(jsonFileMap)
        def outputLines = []
        serviceDetails.each({ it ->
            outputLines << formatOutSingleJsonLine(datestampValue, it)
        })
        JsonOutput.toJson(outputLines)
    }

    private def formatJsonToElasticsearchBulkFormat(def indexName, def typeName, def jsonFileMap) {
        def datestampValue = getDatestampValue(jsonFileMap)
        def serviceDetails = findAllServices(jsonFileMap)
        def outputLines = []
        serviceDetails.each({ it ->
            outputLines << [index: [_index: indexName, _type: typeName]]
            outputLines << formatOutSingleJsonLine(datestampValue, it)
        })
        // format out each line appended with a new line
        outputLines.collect({it -> JsonOutput.toJson(it) }).join("\n")
    }

    def convertMetricsToCSV(def rawJsonString) {
        def jsonMap = parseStringToJsonMap(rawJsonString)
        formatJsonToCSV(jsonMap)
    }

    def convertMetricsToFlatJSON(def rawJsonString) {
        def jsonMap = parseStringToJsonMap(rawJsonString)
        formatJsonToJson(jsonMap)
    }

    def convertMetricsToElasticsearchBulkJSON(def indexName, def typeName, def rawJsonString) {
        def jsonMap = parseStringToJsonMap(rawJsonString)
        formatJsonToElasticsearchBulkFormat(indexName, typeName, jsonMap)
    }

    // parse a json file
    def parseJsonFile(def filename) {
        def resource = this.getClass().getResource(filename)
        def jsonFile = new File(resource.toURI())
        parseFileToJsonMap(jsonFile)
    }

    private def parseStringToJsonMap(def jsonText) {
        jsonSlurper.parseText(jsonText)
    }

    private def parseFileToJsonMap(def jsonFile) {
        jsonSlurper.parse(jsonFile)
    }
}
