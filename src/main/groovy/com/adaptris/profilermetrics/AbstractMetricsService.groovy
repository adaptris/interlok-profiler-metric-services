package com.adaptris.profilermetrics

import com.adaptris.core.CoreException
import com.adaptris.core.ServiceImp
import com.thoughtworks.xstream.annotations.XStreamImplicit

abstract class AbstractMetricsService extends ServiceImp {
    protected JSONParser jsonParser


    @XStreamImplicit(itemFieldName = "channel-includes")
    protected List<String> channelIncludePatterns

    @XStreamImplicit(itemFieldName = "channel-excludes")
    protected List<String> channelExcludePatterns


    public AbstractMetricsService() {
        channelIncludePatterns = new ArrayList<String>()
        channelExcludePatterns = new ArrayList<String>()
    }

    public List<String> getChannelIncludePatterns() {
        return channelIncludePatterns
    }

    public void setChannelIncludePatterns(List<String> channelIncludePatterns) {
        this.channelIncludePatterns = channelIncludePatterns
    }

    public List<String> getChannelExcludePatterns() {
        return channelExcludePatterns
    }

    public void setChannelExcludePatterns(List<String> channelExcludePatterns) {
        this.channelExcludePatterns = channelExcludePatterns
    }


    @Override
    protected void initService() throws CoreException {
        jsonParser = new JSONParser(channelIncludePatterns, channelExcludePatterns)
    }

    @Override
    protected void closeService() {
        jsonParser = null
    }

    @Override
    void prepare() throws CoreException {
    }
}
