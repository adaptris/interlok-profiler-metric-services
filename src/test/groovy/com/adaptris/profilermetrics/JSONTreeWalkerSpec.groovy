package com.adaptris.profilermetrics

import groovy.json.JsonSlurper
import spock.lang.Specification

class JSONTreeWalkerSpec extends Specification {
    def sut = new JSONTreeWalker()

    def "get min service details from parsed service"() {
        setup:
        def myService = "{\n" +
                "\"messageCount\": 1,\n" +
                "\"avgMsTaken\": 3,\n" +
                "\"serviceClass\":\n" +
                "\"LogMessageService\",\n" +
                "\"uniqueId\":\n" +
                "\"log-initial-request-log\"}"
        def jsonSlurper = new JsonSlurper()
        def parsedJson = jsonSlurper.parseText(myService)

        when:
        def serviceDetails = sut.processServiceDetails("adapter1", "channel1", "workflow1", parsedJson)

        then:
        parsedJson != null
        serviceDetails != null
        serviceDetails.size() == 7
        serviceDetails.adapter == "adapter1"
        serviceDetails.channel == "channel1"
        serviceDetails.workflow == "workflow1"
        serviceDetails.uniqueId == "log-initial-request-log"
        serviceDetails.serviceClass == "LogMessageService"
        serviceDetails.timeTaken == 3
        serviceDetails.msgCount == 1
    }

    def "get min service details from null service"() {
        when:
        def serviceDetails1 = sut.processServiceDetails(null, null, null, null)
        def serviceDetails2 = sut.processServiceDetails("adapter", "channel", "workflow", null)
        then:
        serviceDetails1 == null
        serviceDetails2 == null
    }

}
