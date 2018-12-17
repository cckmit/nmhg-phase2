/**
 * PriceCheckWS_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package twms.clubcar.integration.is.pricecheck;

public class PriceCheckWS_ServiceLocator extends org.apache.axis.client.Service implements twms.clubcar.integration.is.pricecheck.PriceCheckWS_Service {

    public PriceCheckWS_ServiceLocator() {
    }


    public PriceCheckWS_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public PriceCheckWS_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for PriceCheckWS
    private java.lang.String PriceCheckWS_address = "http://blrirap01.in.corp.tavant.com:8086/services/PriceCheck-WS";

    public java.lang.String getPriceCheckWSAddress() {
        return PriceCheckWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String PriceCheckWSWSDDServiceName = "PriceCheck-WS";

    public java.lang.String getPriceCheckWSWSDDServiceName() {
        return PriceCheckWSWSDDServiceName;
    }

    public void setPriceCheckWSWSDDServiceName(java.lang.String name) {
        PriceCheckWSWSDDServiceName = name;
    }

    public twms.clubcar.integration.is.pricecheck.$Proxy207 getPriceCheckWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(PriceCheckWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getPriceCheckWS(endpoint);
    }

    public twms.clubcar.integration.is.pricecheck.$Proxy207 getPriceCheckWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            twms.clubcar.integration.is.pricecheck.PriceCheckWSSoapBindingStub _stub = new twms.clubcar.integration.is.pricecheck.PriceCheckWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getPriceCheckWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setPriceCheckWSEndpointAddress(java.lang.String address) {
        PriceCheckWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (twms.clubcar.integration.is.pricecheck.$Proxy207.class.isAssignableFrom(serviceEndpointInterface)) {
                twms.clubcar.integration.is.pricecheck.PriceCheckWSSoapBindingStub _stub = new twms.clubcar.integration.is.pricecheck.PriceCheckWSSoapBindingStub(new java.net.URL(PriceCheckWS_address), this);
                _stub.setPortName(getPriceCheckWSWSDDServiceName());
                return _stub;
            }
        }
        catch (java.lang.Throwable t) {
            throw new javax.xml.rpc.ServiceException(t);
        }
        throw new javax.xml.rpc.ServiceException("There is no stub implementation for the interface:  " + (serviceEndpointInterface == null ? "null" : serviceEndpointInterface.getName()));
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(javax.xml.namespace.QName portName, Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        if (portName == null) {
            return getPort(serviceEndpointInterface);
        }
        java.lang.String inputPortName = portName.getLocalPart();
        if ("PriceCheck-WS".equals(inputPortName)) {
            return getPriceCheckWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://external.server.integration.twms.tavant", "PriceCheck-WS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://external.server.integration.twms.tavant", "PriceCheck-WS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("PriceCheckWS".equals(portName)) {
            setPriceCheckWSEndpointAddress(address);
        }
        else 
{ // Unknown Port Name
            throw new javax.xml.rpc.ServiceException(" Cannot set Endpoint Address for Unknown Port" + portName);
        }
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(javax.xml.namespace.QName portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        setEndpointAddress(portName.getLocalPart(), address);
    }

}
