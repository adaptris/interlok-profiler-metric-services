package com.adaptris.profilermetrics

import groovy.util.logging.Log

import java.util.regex.Matcher
import java.util.regex.Pattern

@Log
class JSONTreeWalker {
    def SERVICES_KEY  = "services"
    def CHANNELS_KEY  = "channels"
    def ADAPTERS_KEY  = "adapters"
    def WORKFLOWS_KEY = "workflows"

    private List<Pattern> includeChannels
    private List<Pattern> excludeChannels


    public JSONTreeWalker() {
        this(null, null)
    }

    public JSONTreeWalker(List<String> includePatterns, List<String> excludePatterns) {
        includeChannels = createPatterns(includePatterns)
        excludeChannels = createPatterns(excludePatterns)
    }

    private List<Pattern> createPatterns(List<String> definedFilterStrings) {
        if (definedFilterStrings != null) {
            return definedFilterStrings.collect { filterString -> Pattern.compile(filterString) }
        }
        return null
    }

    def isMatch(def currentPattern, def channelName) {
        Matcher m = currentPattern.matcher(channelName)
        return m.matches()
    }

    def isListDefined(def patternList) {
        return (patternList != null && patternList.size() > 0)
    }

    // Determine if the given channel name should be filter out ie skipped for processing
    def filterChannel(def channelName) {
        def filterOutChannel = false
        if (isListDefined(includeChannels)) {
            filterOutChannel = !includeChannels.any{item -> isMatch(item, channelName)}
        }
        if (isListDefined(excludeChannels)) {
            filterOutChannel = filterOutChannel || excludeChannels.any{item -> isMatch(item, channelName)}
        }
        return filterOutChannel
    }

    // Process all Adapters within the metrics structure and return the service details
    def processAdapters(Map topLevelAdapterMetricsMap) {
        if (topLevelAdapterMetricsMap == null || topLevelAdapterMetricsMap.size() < 1) return null
        def results = []
        def adaptersLevel = findDeep(topLevelAdapterMetricsMap, ADAPTERS_KEY)
        // iterate through adapters
        if (adaptersLevel instanceof Map) {
            adaptersLevel.each { k, v ->
                results.addAll(processChannels(k, v))
            }
        }
        return results
    }

    // Process channels within a single adapter
    def processChannels(def adapterName, Map topLevelAdapterMetricsMap) {
        log.info("Now processing channels for adapter: ${adapterName}" )
        if (topLevelAdapterMetricsMap == null || topLevelAdapterMetricsMap.size() < 1) return null
        def results = []
        def channelsLevel = findDeep(topLevelAdapterMetricsMap, CHANNELS_KEY)
        // iterate through adapters
        if (channelsLevel instanceof Map) {
            channelsLevel.each { k, v ->
                if (!filterChannel(k)) {
                    results.addAll(processWorkflows(adapterName, k, v))
                }
                else {
                    log.info ("Channel ${k} is being filtered out from being processed")
                }
            }
        }
        return results
    }

    // Process workflows within a channel
    private def processWorkflows(def adapterName, def channelName, Map channelMetricsMap) {
        log.info("Now processing workflows for channel: ${channelName}" )
        def results = []
        def workflowLevel = findDeep(channelMetricsMap, WORKFLOWS_KEY)
        if (workflowLevel != null && workflowLevel instanceof Map && !workflowLevel.isEmpty()) {
            workflowLevel.each { k, v ->
                results.addAll(processServices(adapterName, channelName, k, v))
            }
        }
        return results
    }

    // Process services within a workflow
    private def processServices(def adapterName, def channelName, def workflowName, Map channelMetricsMap) {
        log.info("Now processing services for workflow: ${workflowName}")
        def results = []
        def servicesLevel = findDeep(channelMetricsMap, SERVICES_KEY)
        if (servicesLevel != null && servicesLevel instanceof Map && !servicesLevel.isEmpty()) {
            servicesLevel.each { k, v ->
                results << processServiceDetails(adapterName, channelName, workflowName, v)
                // recursive call
                results.addAll(processServices(adapterName, channelName, workflowName, v))
            }
        }
        return results
    }

    // for each service extract key details & convert time to millisecs
    private def processServiceDetails(def adapterName, def channelName, def workflowName, def jsonObject) {
        if (jsonObject == null) return null
        log.info("Processing details for service name: ${jsonObject.uniqueId}")
        def outputMap = [
                adapter: adapterName,
                channel: channelName,
                workflow: workflowName,
                uniqueId: jsonObject.uniqueId?: null,
                serviceClass: jsonObject.serviceClass?: null,
                timeTaken: jsonObject.avgMsTaken != null? jsonObject.avgMsTaken : 0,
                msgCount: jsonObject.messageCount?: 0
        ]
        return outputMap
    }

    // A recursive func to find a given key in a nested Map
    private def findDeep(Map m, String key) {
        if (m.containsKey(key)) return m[key]
        m.findResult { k, v -> v instanceof Map ? findDeep(v, key) : null }
    }
}
