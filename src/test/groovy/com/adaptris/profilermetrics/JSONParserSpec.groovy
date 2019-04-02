package com.adaptris.profilermetrics

import spock.lang.Specification

class JSONParserSpec extends Specification {
    def sut = new JSONParser()
    public static final String JSON_INPUT_FILE1 = "/sample1.json"
    public static final String JSON_INPUT_FILE2 = "/metrics2019-1.json"

    def "parsing of json file"() {
        when:
            def result = sut.parseJsonFile(JSON_INPUT_FILE1)
        then:
        result != null
        result instanceof Map
        result.datetimestamp == "2018-06-04T17:03:33.773"
    }

    def "parsing of 2019 json file"() {
        when:
        def result = sut.parseJsonFile(JSON_INPUT_FILE2)
        then:
        result != null
        result instanceof Map
        result.datetimestamp == "2019-03-21T15:47:12.445"
    }

    def "obtaining timestamp from parsed json"() {
        when:
        def result = sut.parseJsonFile(JSON_INPUT_FILE1)
        def dstampValue = sut.getDatestampValue(result)
        then:
        dstampValue == "2018-06-04T17:03:33.773"
    }

    def "obtaining timestamp from parsed 2019 json"() {
        when:
        def result = sut.parseJsonFile(JSON_INPUT_FILE2)
        def dstampValue = sut.getDatestampValue(result)
        then:
        dstampValue == "2019-03-21T15:47:12.445"
    }

    def "find all services from json message"() {
        when:
        def result = sut.parseJsonFile(JSON_INPUT_FILE1)
        def servicesFound = sut.findAllServices(result)
        then:
        servicesFound != null
        servicesFound.size() == 12
    }

    def "find all services from 2019 json message"() {
        when:
        def result = sut.parseJsonFile(JSON_INPUT_FILE2)
        def servicesFound = sut.findAllServices(result)
        then:
        servicesFound != null
        servicesFound.size() == 21
    }

    def "find all services given null map"() {
        when:
        def servicesFound = sut.findAllServices(null)
        then:
        servicesFound == null
    }

    def "find all services given empty map"() {
        when:
        def servicesFound = sut.findAllServices([:])
        then:
        servicesFound == null
    }

    def "find all services given small map"() {
        when:
        def servicesFound = sut.findAllServices([name: "hello"])
        then:
        servicesFound == []
    }

    def "find all services given nested services"() {

    }

    def "find all services given include patterns"() {

    }

    def "find all services given excluded patterns"() {
        setup:
        sut = new JSONParser([], ['performance-metrics-channel'])
        when:
        def result = sut.parseJsonFile(JSON_INPUT_FILE2)
        def servicesFound = sut.findAllServices(result)
        then:
        servicesFound != null
        servicesFound.size() == 16
        cleanup:
        sut = new JSONParser()
    }

    def "find all services given both include and exclude patterns"() {

    }

    def "convert the given json file to csv"() {
        setup:
        def expectedOutput = """2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, log-initial-request-log, LogMessageService, 3, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, log-request-converted-to-firco-log, LogMessageService, 1, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, add-metadata-init-service, AddMetadataService, 4, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, 41de27af-e482-415d-bd4f-14d3600991fd, JsonPathService, 92, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, log-confirm-captured-req-params-log, LogMessageService, 2, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, a8543f90-9367-43de-a65e-eaa1bb4b8f3d, PoolingSplitJoinService, 974, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, log-post-pooling-split-join-service-log, LogMessageService, 16, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, metadata-save-merged-engine-results-to-metadata, PayloadToMetadataService, 2, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, ref-send-back-firco-immediate-response, SharedService, 46, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, final-jetty-response-service, JettyResponseService, 19, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, metadata-reload-saved-json-into-payload, MetadataToPayloadService, 1, 1
2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, log-final-request-processed-log, LogMessageService, 21, 1
"""
        when:
        def jsonFileMap = sut.parseJsonFile(JSON_INPUT_FILE1)
        def result = sut.formatJsonToCSV(jsonFileMap)
        then:
        result == expectedOutput
    }

    def "convert the given json file to a flattened json" () {
        setup:
        def expectedOutput = """[{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-initial-request-log","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"add-metadata-init-service","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"pooling-split-join-service","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"pooling-split-main-service-list","timeTaken":24,"msgCount":2},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"determine-which-endpoint-to-call","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"CheckTargetEndpoint","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"default-service-id","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-processing-default-request","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"TradeComplianceEndpoint","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-processing-tradecompliance-request","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"FircoEndpoint","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-processing-firco-request","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-post-conversion-log","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-post-pooling-split-join-service-log","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"final-jetty-response-service","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-final-request-processed-log","timeTaken":0,"msgCount":0},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"log-initial-performance-request-log","timeTaken":0,"msgCount":1},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"eb2ac714-5e8a-49e0-817d-b9b8467e5d80","timeTaken":1,"msgCount":1},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"log-subset-performance-request-log","timeTaken":1,"msgCount":1},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"metrics-json-to-csv-service","timeTaken":1,"msgCount":1},{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"final-metrics-log-service","timeTaken":0,"msgCount":0}]"""
        when:
        def jsonFileMap = sut.parseJsonFile(JSON_INPUT_FILE2)
        def result = sut.formatJsonToJson(jsonFileMap)
        then:
        result == expectedOutput
    }

    def "convert metrics to elastic search bulk format"() {
        setup:
        def expectedOutput = """{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-initial-request-log","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"add-metadata-init-service","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"pooling-split-join-service","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"pooling-split-main-service-list","timeTaken":24,"msgCount":2}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"determine-which-endpoint-to-call","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"CheckTargetEndpoint","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"default-service-id","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-processing-default-request","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"TradeComplianceEndpoint","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-processing-tradecompliance-request","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"FircoEndpoint","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-processing-firco-request","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-post-conversion-log","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-post-pooling-split-join-service-log","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"final-jetty-response-service","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"firco-filter-clink-request-processing-channel","workflow":"standardWorkflow","uniqueId":"log-final-request-processed-log","timeTaken":0,"msgCount":0}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"log-initial-performance-request-log","timeTaken":0,"msgCount":1}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"eb2ac714-5e8a-49e0-817d-b9b8467e5d80","timeTaken":1,"msgCount":1}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"log-subset-performance-request-log","timeTaken":1,"msgCount":1}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"metrics-json-to-csv-service","timeTaken":1,"msgCount":1}
{"index":{"_index":"profilermetrics","_type":"default"}}
{"datestamp":"2019-03-21T15:47:12.445","adapter":"interlok_firco_proxy","channel":"performance-metrics-channel","workflow":"main-metric-workflow","uniqueId":"final-metrics-log-service","timeTaken":0,"msgCount":0}"""

        when:
        def jsonFileMap = sut.parseJsonFile(JSON_INPUT_FILE2)
        def result = sut.formatJsonToElasticsearchBulkFormat("profilermetrics", "default", jsonFileMap)
        then:
        result == expectedOutput
    }
}
