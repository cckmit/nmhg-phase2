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
package tavant.twms.process;

import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly=true)
public interface ProcessDefinitionService {

    @Transactional(readOnly=false)
    public abstract void deploy(ProcessDefinition process);

    @Transactional(readOnly=false)
    public abstract void undeploy(final String processName);

    public abstract ProcessDefinition find(final String processName);

    @SuppressWarnings("unchecked")
    public List<ProcessDefinition> findAll();

}
