/**
 * Tavant_ProcessServiceLocator.java
 *
 * This file was auto-generated from WSDL
 * by the Apache Axis 1.4 Apr 22, 2006 (06:55:48 PDT) WSDL2Java emitter.
 */

package twms.clubcar.integration.wm.extwtydebitsubmit;

public class Tavant_ProcessServiceLocator extends org.apache.axis.client.Service implements twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessService {

    public Tavant_ProcessServiceLocator() {
    }


    public Tavant_ProcessServiceLocator(org.apache.axis.EngineConfiguration config) {
        super(config);
    }

    public Tavant_ProcessServiceLocator(java.lang.String wsdlLoc, javax.xml.namespace.QName sName) throws javax.xml.rpc.ServiceException {
        super(wsdlLoc, sName);
    }

    // Use to get a proxy class for Tavant_ProcessPort0
    private java.lang.String Tavant_ProcessPort0_address = "http://10.80.14.29:5677/soap/rpc";

    public java.lang.String getTavant_ProcessPort0Address() {
        return Tavant_ProcessPort0_address;
    }

    // The WSDD service name defaults to the port name.
    private java.lang.String Tavant_ProcessPort0WSDDServiceName = "Tavant_ProcessPort0";

    public java.lang.String getTavant_ProcessPort0WSDDServiceName() {
        return Tavant_ProcessPort0WSDDServiceName;
    }

    public void setTavant_ProcessPort0WSDDServiceName(java.lang.String name) {
        Tavant_ProcessPort0WSDDServiceName = name;
    }

    public twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessPortType getTavant_ProcessPort0() throws javax.xml.rpc.ServiceException {
       java.net.URL endpoint;
        try {
            endpoint = new java.net.URL(Tavant_ProcessPort0_address);
        }
        catch (java.net.MalformedURLException e) {
            throw new javax.xml.rpc.ServiceException(e);
        }
        return getTavant_ProcessPort0(endpoint);
    }

    public twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessPortType getTavant_ProcessPort0(java.net.URL portAddress) throws javax.xml.rpc.ServiceException {
        try {
            twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessBindingStub _stub = new twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessBindingStub(portAddress, this);
            _stub.setPortName(getTavant_ProcessPort0WSDDServiceName());
            return _stub;
        }
        catch (org.apache.axis.AxisFault e) {
            return null;
        }
    }

    public void setTavant_ProcessPort0EndpointAddress(java.lang.String address) {
        Tavant_ProcessPort0_address = address;
    }

    /**
     * For the given interface, get the stub implementation.
     * If this service has no port for the given interface,
     * then ServiceException is thrown.
     */
    public java.rmi.Remote getPort(Class serviceEndpointInterface) throws javax.xml.rpc.ServiceException {
        try {
            if (twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessPortType.class.isAssignableFrom(serviceEndpointInterface)) {
                twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessBindingStub _stub = new twms.clubcar.integration.wm.extwtydebitsubmit.Tavant_ProcessBindingStub(new java.net.URL(Tavant_ProcessPort0_address), this);
                _stub.setPortName(getTavant_ProcessPort0WSDDServiceName());
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
        if ("Tavant_ProcessPort0".equals(inputPortName)) {
            return getTavant_ProcessPort0();
        }
        else  {
            java.rmi.Remote _stub = getPort(serviceEndpointInterface);
            ((org.apache.axis.client.Stub) _stub).setPortName(portName);
            return _stub;
        }
    }

    public javax.xml.namespace.QName getServiceName() {
        return new javax.xml.namespace.QName("http://www.webmethods.com/", "Tavant_ProcessService");
    }

    private java.util.HashSet ports = null;

    public java.util.Iterator getPorts() {
        if (ports == null) {
            ports = new java.util.HashSet();
            ports.add(new javax.xml.namespace.QName("http://www.webmethods.com/", "Tavant_ProcessPort0"));
        }
        return ports.iterator();
    }

    /**
    * Set the endpoint address for the specified port name.
    */
    public void setEndpointAddress(java.lang.String portName, java.lang.String address) throws javax.xml.rpc.ServiceException {
        
if ("Tavant_ProcessPort0".equals(portName)) {
            setTavant_ProcessPort0EndpointAddress(address);
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
