package tavant.twms.integration.layer;

import org.springframework.util.Assert;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.WebServiceMessageFactory;
import org.springframework.ws.client.WebServiceIOException;
import org.springframework.ws.client.WebServiceTransformerException;
import org.springframework.ws.client.WebServiceTransportException;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.context.DefaultMessageContext;
import org.springframework.ws.context.MessageContext;
import org.springframework.ws.transport.TransportException;
import org.springframework.ws.transport.WebServiceConnection;
import org.springframework.ws.transport.context.DefaultTransportContext;
import org.springframework.ws.transport.context.TransportContext;
import org.springframework.ws.transport.context.TransportContextHolder;
import org.springframework.ws.transport.support.TransportUtils;

import javax.xml.transform.TransformerException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;

/**
 * Created with IntelliJ IDEA.
 * User: deepak.patel
 * Date: 24/4/13
 * Time: 6:22 PM
 * To change this template use File | Settings | File Templates.
 */
public class CustomWebServiceTemplate extends WebServiceTemplate{

    public CustomWebServiceTemplate(WebServiceMessageFactory messageFactory) {
        super(messageFactory);
    }


    @SuppressWarnings("unchecked")
    @Override
    protected <T> T doSendAndReceive(MessageContext messageContext,
                                     WebServiceConnection connection,
                                     WebServiceMessageCallback requestCallback,
                                     WebServiceMessageExtractor<T> responseExtractor) throws IOException {
        try {
            if (requestCallback != null) {
                requestCallback.doWithMessage(messageContext.getRequest());
            }
            // Apply handleRequest of registered interceptors
            int interceptorIndex = -1;
            if (getInterceptors() != null) {
                for (int i = 0; i < getInterceptors().length; i++) {
                    interceptorIndex = i;
                    if (!getInterceptors()[i].handleRequest(messageContext)) {
                        break;
                    }
                }
            }
            // if an interceptor has set a response, we don't send/receive
            //Hack for no logger situation. need a better solution
            ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
            messageContext.getRequest().writeTo(requestStream);
            if (!messageContext.hasResponse()) {
                sendRequest(connection, messageContext.getRequest());
                if (hasError(connection, messageContext.getRequest())) {
                    return (T)handleError(connection, messageContext.getRequest());
                }
                WebServiceMessage response = connection.receive(getMessageFactory());
                messageContext.setResponse(response);
            }
            logResponse(messageContext);
            if (messageContext.hasResponse()) {
                if (!hasFault(connection, messageContext.getResponse())) {
                    triggerHandleResponse(interceptorIndex, messageContext);
                    return responseExtractor.extractData(messageContext.getResponse());
                }
                else {
                    triggerHandleFault(interceptorIndex, messageContext);
                    return (T)handleFault(connection, messageContext);
                }
            }
            else {
                return null;
            }
        }
        catch (TransformerException ex) {
            throw new WebServiceTransformerException("Transformation error: " + ex.getMessage(), ex);
        }
    }

    private void logResponse(MessageContext messageContext) throws IOException {
        if (messageContext.hasResponse()) {
            if (receivedMessageTracingLogger.isTraceEnabled()) {
                ByteArrayOutputStream requestStream = new ByteArrayOutputStream();
                messageContext.getRequest().writeTo(requestStream);
                ByteArrayOutputStream responseStream = new ByteArrayOutputStream();
                messageContext.getResponse().writeTo(responseStream);
                receivedMessageTracingLogger
                        .trace("Received response [" + responseStream.toString("UTF-8") + "] for request [" +
                                requestStream.toString("UTF-8") + "]");
            }
            else if (receivedMessageTracingLogger.isDebugEnabled()) {
                receivedMessageTracingLogger
                        .debug("Received response [" + messageContext.getResponse() + "] for request [" +
                                messageContext.getRequest() + "]");
            }
        }
        else {
            if (logger.isDebugEnabled()) {
                receivedMessageTracingLogger
                        .debug("Received no response for request [" + messageContext.getRequest() + "]");
            }
        }
    }


    private void triggerHandleResponse(int interceptorIndex, MessageContext messageContext) {
        if (messageContext.hasResponse() && getInterceptors() != null) {
            for (int i = interceptorIndex; i >= 0; i--) {
                if (!getInterceptors()[i].handleResponse(messageContext)) {
                    break;
                }
            }
        }
    }

    //** Sends the request in the given message context over the connection. *//*
    private void sendRequest(WebServiceConnection connection, WebServiceMessage request) throws IOException {
        if (sentMessageTracingLogger.isTraceEnabled()) {
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            request.writeTo(os);
            sentMessageTracingLogger.trace("Sent request [" + os.toString("UTF-8") + "]");
        }
        else if (sentMessageTracingLogger.isDebugEnabled()) {
            sentMessageTracingLogger.debug("Sent request [" + request + "]");
        }
        connection.send(request);
    }

    private void triggerHandleFault(int interceptorIndex, MessageContext messageContext) {
        if (messageContext.hasResponse() && getInterceptors() != null) {
            for (int i = interceptorIndex; i >= 0; i--) {
                if (!getInterceptors()[i].handleFault(messageContext)) {
                    break;
                }
            }
        }
    }

}
