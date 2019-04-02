package com.adaptris.profilermetrics

import com.adaptris.annotation.AdapterComponent
import com.adaptris.core.AdaptrisMessage
import com.adaptris.core.CoreException
import com.adaptris.core.ServiceException
import com.adaptris.core.util.Args
import com.adaptris.core.util.ExceptionHelper
import com.thoughtworks.xstream.annotations.XStreamAlias
import org.hibernate.validator.constraints.NotBlank

import javax.validation.Valid

/**
 * Interlok service to convert a metrics message into ElasticSearch bulk import json message.
 */
@XStreamAlias("metrics-to-elasticsearch-bulk-service")
@AdapterComponent
class MetricsToElasticsearchBulkService extends AbstractMetricsService {

    @Valid
    @NotBlank
    private String indexName
    @Valid
    @NotBlank
    private String typeName


    @Override
    protected void initService() throws CoreException {
        try {
            super.initService();
            Args.notNull(indexName, "indexName")
            Args.notNull(typeName, "typeName")
        }
        catch (Exception e) {
            throw ExceptionHelper.wrapCoreException(e);
        }
    }

    @Override
    void doService(AdaptrisMessage adaptrisMessage) throws ServiceException {
        def payload = adaptrisMessage.getStringPayload()
        def generatedJSON = jsonParser.convertMetricsToElasticsearchBulkJSON(indexName, typeName, payload)
        adaptrisMessage.setStringPayload(generatedJSON)
    }

    String getIndexName() {
        return indexName
    }

    void setIndexName(String indexName) {
        this.indexName = indexName
    }

    String getTypeName() {
        return typeName
    }

    void setTypeName(String typeName) {
        this.typeName = typeName
    }
}
