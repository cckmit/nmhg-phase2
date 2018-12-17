/*
 *   Copyright (c)2006 Tavant Technologies
 *   All Rights Reserved.
 *
 *   This software is furnished under a license and may be used and copied
 *   only  in  accordance  with  the  terms  of such  license and with the
 *   inclusion of the above copyright notice. This software or  any  other
 *   copies thereof may not be provided or otherwise made available to any
 *   other person. No title to and ownership of  the  software  is  hereby
 *   transferred.
 *
 *   The information in this software is subject to change without  notice
 *   and  should  not be  construed as a commitment  by Tavant Technologies.
 */
package tavant.twms.domain.claim.payment;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;

import bsh.EvalError;
import bsh.Interpreter;

import com.domainlanguage.money.Money;

class CalculationContext {
    private Map<AmountName, Double> amounts = new HashMap<AmountName, Double>();
    private Interpreter interpreter;
    static Logger logger = LogManager.getLogger(CalculationContext.class);
    
    static String functionDefinitions;
    
    public CalculationContext() {
        super();
        if (functionDefinitions==null) {
            ClassPathResource resource = new ClassPathResource("mathFunctions.bsh", getClass());            
            try {
                InputStream inputStream = resource.getInputStream();
                InputStreamReader reader = new InputStreamReader(inputStream);
                StringWriter stringWriter = new StringWriter();
                FileCopyUtils.copy(reader, stringWriter);
                functionDefinitions = stringWriter.getBuffer().toString();
            } catch (IOException e) {
                logger.error("Failed to load functions from resource "+resource.getFilename(), e);
            }
        }
        
        //Make functions available for the calculation statements.
        interpreter = new Interpreter();
        try {
            interpreter.eval(functionDefinitions);
        } catch (EvalError e) {
            logger.error("error",e);
        }
    }

    double executeStatement(String calculationStatement) throws PaymentCalculationException {
        try {
            String normalizedStatement = calculationStatement.replaceAll("\\s", "").toLowerCase();
            return (Double)interpreter.eval(normalizedStatement);
        } catch (EvalError e) {
            throw new PaymentCalculationException(e);
        }
    }

    void addAmount(Money amount, String name) throws PaymentCalculationException {
        addAmount(amount.breachEncapsulationOfAmount().doubleValue(), name);
    }    
    
    void addAmount(BigDecimal amount, String name) throws PaymentCalculationException {
        addAmount(amount.doubleValue(), name);
    }    
    
    void addAmount(double amount, String name) throws PaymentCalculationException {
        AmountName newName = new AmountName(name);
        addAmount(amount, newName);
    }

    public boolean isAmountDefined(String name) {
        return amounts.containsKey(new AmountName(name));
    }

    public double getAmount(String name) {
        AmountName amountName = new AmountName(name);
        return amounts.get(amountName);
    }

    /**
     * @param amount
     * @param newName
     * @throws PaymentCalculationException
     */
    private void addAmount(double amount, AmountName newName) throws PaymentCalculationException {
        try {
            String variableName = newName.getModelTerm();
            interpreter.set(variableName, amount);
            amounts.put(newName, amount);
        } catch (EvalError e) {
            throw new PaymentCalculationException(e);
        }
    }
    
    @Override
    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("functionDefinitions").append('=').append(functionDefinitions).append("\n");
        buf.append("Amounts -->");
        for( Map.Entry<AmountName,Double> amount : amounts.entrySet()) {
            buf.append('\n');
            buf.append("("+amount.getKey().getBusinessTerm()+","+amount.getKey().getModelTerm()+") = "+amount.getValue());
        }
        return buf.toString();
    }    
}
