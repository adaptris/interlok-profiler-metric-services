package com.adaptris.profilermetrics

import com.adaptris.annotation.AdapterComponent
import com.adaptris.core.AdaptrisMessage
import com.adaptris.core.ServiceException
import com.thoughtworks.xstream.annotations.XStreamAlias

/**
 * Interlok service to convert a metrics message to a flattened csv message that can be appended to a csv file.
 */
@XStreamAlias("metrics-to-csv-service")
@AdapterComponent
class MetricsToCSVService extends AbstractMetricsService {

    public MetricsToCSVService() {
        super();
    }

    @Override
    void doService(AdaptrisMessage adaptrisMessage) throws ServiceException {
        def payload = adaptrisMessage.getStringPayload()
        def generatedJSON = jsonParser.convertMetricsToCSV(payload)
        adaptrisMessage.setStringPayload(generatedJSON)
    }
}
