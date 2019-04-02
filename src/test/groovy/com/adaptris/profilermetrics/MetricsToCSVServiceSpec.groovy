package com.adaptris.profilermetrics

import com.adaptris.core.AdaptrisMessage
import com.adaptris.core.DefaultAdaptrisMessageImp
import com.adaptris.core.DefaultMessageFactory
import com.adaptris.util.SimpleIdGenerator
import spock.lang.Specification

class MetricsToCSVServiceSpec extends Specification {
    static MetricsToCSVService sut = new MetricsToCSVService()

    def setupSpec() {
        sut.initService()
        sut.prepare()
    }

    AdaptrisMessage createAdaptrisMessageFromFile() {
        def resource = this.getClass().getResource(JSONParserSpec.JSON_INPUT_FILE1)
        def jsonFile = new File(resource.toURI())

        AdaptrisMessage msg = new DefaultAdaptrisMessageImp(new SimpleIdGenerator(), new DefaultMessageFactory())
        msg.setStringPayload(jsonFile.text)
        return msg
    }

    def "test basic conversion of file to csv"() {
        setup:
        def message = createAdaptrisMessageFromFile()
        when:
        sut.doService(message)
        then:
        message.getStringPayload() == """2018-06-04T17:03:33.773, interlok_firco_proxy, FircoFilterRequestProcessingChannel, poolingWorkflow, log-initial-request-log, LogMessageService, 3, 1
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

    }

    def cleanupSpec() {
        sut.closeService()
    }
}
