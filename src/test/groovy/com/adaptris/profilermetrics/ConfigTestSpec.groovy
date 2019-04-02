package com.adaptris.profilermetrics

import com.adaptris.core.AdapterXStreamMarshallerFactory
import com.adaptris.core.AdaptrisMarshaller
import com.adaptris.core.CoreException
import com.adaptris.core.DefaultMarshaller
import com.adaptris.core.XStreamMarshaller
import com.thoughtworks.xstream.XStream
import spock.lang.Specification

class ConfigTestSpec extends Specification {
    protected AdaptrisMarshaller configMarshaller;
    protected AdaptrisMarshaller defaultMarshaller;

    def "test generation of xml"() {
        setup:
        try {
            defaultMarshaller = DefaultMarshaller.getDefaultMarshaller();
            // by default we want to marshal config w/o ID Referencing mode to remove "ID cruft".
            // So we aren't going to use the DefaultMarshaller *as is*
            configMarshaller = new XpathModeXStream();
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
        when:
            def sut = new MetricsToCSVService()
            sut.setChannelIncludePatterns(["asdasds"])
            sut.setChannelExcludePatterns(["hello", "goodbye"])
//            sut.initService()
            sut.setUniqueId("unique-id")
            ExampleServiceConfig config = new ExampleServiceConfig();
            config.addService(sut);
            def result = configMarshaller.marshal(config);
        then:
            result != null
            result == """<dummy-placeholder-service-element>
  <services>
    <com.adaptris.profilermetrics.MetricsToCSVService>
      <unique-id>unique-id</unique-id>
      <channel-include-patterns>
        <string>asdasds</string>
      </channel-include-patterns>
      <channel-exclude-patterns>
        <string>hello</string>
        <string>goodbye</string>
      </channel-exclude-patterns>
    </com.adaptris.profilermetrics.MetricsToCSVService>
  </services>
</dummy-placeholder-service-element>"""
    }

    private class XpathModeXStream extends XStreamMarshaller {
        public XpathModeXStream() throws CoreException {
            super();
        }

        @Override
        public String marshal(Object obj) throws CoreException {
            XStream xstream = AdapterXStreamMarshallerFactory.getInstance().createXStream();
//      XStream xstream = XStreamBootstrap.createXStream();
//      xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
            String xmlResult = xstream.toXML(obj);
            return xmlResult;
        }

        @Override
        public void marshal(Object obj, Writer writer) throws CoreException {
            try {
                XStream xstream = AdapterXStreamMarshallerFactory.getInstance().createXStream();
//        XStream xstream = XStreamBootstrap.createXStream();
//        xstream.setMode(XStream.XPATH_ABSOLUTE_REFERENCES);
                xstream.toXML(obj, writer);
                writer.flush();
            }
            catch (Exception ex) {
                throw new CoreException(ex);
            }
        }
    }
}
