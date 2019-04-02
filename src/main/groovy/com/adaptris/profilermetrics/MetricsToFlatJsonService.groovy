package com.adaptris.profilermetrics

import com.adaptris.annotation.AdapterComponent
import com.adaptris.core.AdaptrisMessage
import com.adaptris.core.ServiceException
import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * Interlok service to convert a metrics message to a flattened json message suitable for posting into ElasticSearch.
 */
@XStreamAlias("metrics-to-flat-json-service")
@AdapterComponent
class MetricsToFlatJsonService extends AbstractMetricsService {

    @Override
    void doService(AdaptrisMessage adaptrisMessage) throws ServiceException {
        def payload = adaptrisMessage.getStringPayload()
        def generatedJSON = jsonParser.convertMetricsToFlatJSON(payload)
        adaptrisMessage.setStringPayload(generatedJSON)
    }
}
