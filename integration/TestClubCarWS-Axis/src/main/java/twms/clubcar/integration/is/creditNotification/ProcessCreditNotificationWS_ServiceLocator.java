/**
 * ProcessCreditNotificationWS_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package twms.clubcar.integration.is.creditNotification;

public class ProcessCreditNotificationWS_ServiceLocator extends org.apache.axis.client.Service implements twms.clubcar.integration.is.creditNotification.ProcessCreditNotificationWS_Service {

    public ProcessCreditNotificationWS_ServiceLocator() {
    }


    public ProcessCreditNotificationWS_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProcessCreditNotificationWS_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ProcessCreditNotificationWS
    private java.lang.String ProcessCreditNotificationWS_address = "http://192.168.44.34:8086/services/ProcessCreditNotification-WS";

    public java.lang.String getProcessCreditNotificationWSAddress() {
        return ProcessCreditNotificationWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ProcessCreditNotificationWSWSDDServiceName = "ProcessCreditNotification-WS";

    public java.lang.String getProcessCreditNotificationWSWSDDServiceName() {
        return ProcessCreditNotificationWSWSDDServiceName;
    }

    public void setProcessCreditNotificationWSWSDDServiceName(java.lang.String name) {
        ProcessCreditNotificationWSWSDDServiceName = name;
    }

    public twms.clubcar.integration.is.creditNotification.$Proxy205 getProcessCreditNotificationWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ProcessCreditNotificationWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getProcessCreditNotificationWS(endpoint);
    }

    public twms.clubcar.integration.is.creditNotification.$Proxy205 getProcessCreditNotificationWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            twms.clubcar.integration.is.creditNotification.ProcessCreditNotificationWSSoapBindingStub _stub = new twms.clubcar.integration.is.creditNotification.ProcessCreditNotificationWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getProcessCreditNotificationWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setProcessCreditNotificationWSEndpointAddress(java.lang.String address) {
        ProcessCreditNotificationWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (twms.clubcar.integration.is.creditNotification.$Proxy205.class.isAssignableFrom(serviceEndpointInterface)) {
                twms.clubcar.integration.is.creditNotification.ProcessCreditNotificationWSSoapBindingStub _stub = new twms.clubcar.integration.is.creditNotification.ProcessCreditNotificationWSSoapBindingStub(new java.net.URL(ProcessCreditNotificationWS_address), this);
                _stub.setPortName(getProcessCreditNotificationWSWSDDServiceName());
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
        if ("ProcessCreditNotification-WS".equals(inputPortName)) {
            return getProcessCreditNotificationWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://external.server.integration.twms.tavant", "ProcessCreditNotification-WS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://external.server.integration.twms.tavant", "ProcessCreditNotification-WS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ProcessCreditNotificationWS".equals(portName)) {
            setProcessCreditNotificationWSEndpointAddress(address);
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
