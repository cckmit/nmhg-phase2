/**
 * ProcessCreditSubmitWS_ServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package twms.clubcar.integration.is.creditsubmit;

public class ProcessCreditSubmitWS_ServiceLocator extends org.apache.axis.client.Service implements twms.clubcar.integration.is.creditsubmit.ProcessCreditSubmitWS_Service {

    public ProcessCreditSubmitWS_ServiceLocator() {
    }


    public ProcessCreditSubmitWS_ServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public ProcessCreditSubmitWS_ServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for ProcessCreditSubmitWS
    private java.lang.String ProcessCreditSubmitWS_address = "http://blrirap01.in.corp.tavant.com:8086/services/ProcessCreditSubmit-WS";

    public java.lang.String getProcessCreditSubmitWSAddress() {
        return ProcessCreditSubmitWS_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String ProcessCreditSubmitWSWSDDServiceName = "ProcessCreditSubmit-WS";

    public java.lang.String getProcessCreditSubmitWSWSDDServiceName() {
        return ProcessCreditSubmitWSWSDDServiceName;
    }

    public void setProcessCreditSubmitWSWSDDServiceName(java.lang.String name) {
        ProcessCreditSubmitWSWSDDServiceName = name;
    }

    public twms.clubcar.integration.is.creditsubmit.$Proxy207 getProcessCreditSubmitWS() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(ProcessCreditSubmitWS_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getProcessCreditSubmitWS(endpoint);
    }

    public twms.clubcar.integration.is.creditsubmit.$Proxy207 getProcessCreditSubmitWS(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            twms.clubcar.integration.is.creditsubmit.ProcessCreditSubmitWSSoapBindingStub _stub = new twms.clubcar.integration.is.creditsubmit.ProcessCreditSubmitWSSoapBindingStub(portAddress, this);
            _stub.setPortName(getProcessCreditSubmitWSWSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setProcessCreditSubmitWSEndpointAddress(java.lang.String address) {
        ProcessCreditSubmitWS_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (twms.clubcar.integration.is.creditsubmit.$Proxy207.class.isAssignableFrom(serviceEndpointInterface)) {
                twms.clubcar.integration.is.creditsubmit.ProcessCreditSubmitWSSoapBindingStub _stub = new twms.clubcar.integration.is.creditsubmit.ProcessCreditSubmitWSSoapBindingStub(new java.net.URL(ProcessCreditSubmitWS_address), this);
                _stub.setPortName(getProcessCreditSubmitWSWSDDServiceName());
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
        if ("ProcessCreditSubmit-WS".equals(inputPortName)) {
            return getProcessCreditSubmitWS();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://external.server.integration.twms.tavant", "ProcessCreditSubmit-WS");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://external.server.integration.twms.tavant", "ProcessCreditSubmit-WS"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("ProcessCreditSubmitWS".equals(portName)) {
            setProcessCreditSubmitWSEndpointAddress(address);
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
