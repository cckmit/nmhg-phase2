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
package tavant.twms.jbpm.bsh;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.log4j.Logger;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.graph.action.Script;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

public class BeanScript extends Script {

    private static final Logger logger = Logger.getLogger(BeanScript.class);

    //true indicates script variable, false transient variable
    Map<String, Boolean> scriptVariables = new HashMap<String, Boolean>();

    public BeanScript(String script) {
        super();
        setExpression(script);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Map createInputMap(ExecutionContext executionContext) {
        Map inputMap = super.createInputMap(executionContext);
        // Add transient variable in addition to actual variables
        if(logger.isDebugEnabled())
        {
            logger.debug("Found context variables [" + inputMap.keySet() + "]");
        }
        for(Object key : inputMap.keySet()) {
            this.scriptVariables.put(key.toString(), true);
        }
        Map transientVariablesMap = executionContext.getContextInstance().getTransientVariables();
        if(transientVariablesMap != null) {
            for (Object variable : transientVariablesMap.keySet()) {
                String variableName = variable.toString();
                if(logger.isDebugEnabled())
                {
                    logger.debug("Adding transient variable name [" + variableName + "]");
                }
                inputMap.put(variableName, executionContext.getContextInstance().getTransientVariable(
                        variableName));
                this.scriptVariables.put(variableName, false);
            }
        }
        return inputMap;
    }

    @Override
    public void execute(ExecutionContext executionContext) throws Exception {
        Map inputMap = createInputMap(executionContext);
        if(logger.isDebugEnabled())
        {
            logger.debug("Created input map");
        }
        Set outputNames = getVariablesWithWriteAccess();
        if(logger.isDebugEnabled())
        {
            logger.debug("Output variables are " + outputNames + "]");
        }
        Map outputMap = eval(inputMap, outputNames);
        if(logger.isDebugEnabled())
        {
            logger.debug("Output map " + outputMap + "]");
        }
        setTransientAndContextVariables(outputMap, executionContext);
    }

    Set<String> getVariablesWithWriteAccess() {
        //A reasonable assumption that only transient variables
        //will be overwritten.
        //TODO trying to write all variables like task etc throws
        //serialization exception
        Set<String> writeVariables = new HashSet<String>();
        for(String variable: this.scriptVariables.keySet()) {
            if(!this.scriptVariables.get(variable)) {
                writeVariables.add(variable);
            }
        }
        return writeVariables;
    }

    void setTransientAndContextVariables(Map outputMap, ExecutionContext executionContext) {
        if ((outputMap != null) && (!outputMap.isEmpty()) && (executionContext != null)) {
            ContextInstance contextInstance = executionContext.getContextInstance();
            Token token = executionContext.getToken();

            Iterator iter = outputMap.keySet().iterator();
            while (iter.hasNext()) {
                String mappedName = (String) iter.next();
                String variableName = mappedName;
                if(this.scriptVariables.get(variableName)) {
                    contextInstance.setVariable(variableName, outputMap.get(mappedName), token);
                } else {
                    contextInstance.setTransientVariable(variableName, outputMap.get(mappedName));
                }
            }
        }
    }





}
