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
package tavant.twms.jbpm.nodes.fork;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.dom4j.Element;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.jpdl.xml.JpdlXmlReader;


/**
 * @author vineeth.varghese
 * @date Nov 10, 2006
 */
public class ForkNode extends Node {

    private static final Logger logger = Logger.getLogger(ForkNode.class);

    private final List<RepeatableTransition> repeatableTransitions = new ArrayList<RepeatableTransition>();

    public ForkNode() {
        super();
    }

    public ForkNode(String name) {
        super(name);
    }

    @Override
    @SuppressWarnings("unchecked")
    public void read(Element forkElement, JpdlXmlReader jpdlReader) {
        super.read(forkElement, jpdlReader);
        this.name = forkElement.attributeValue("name");
        List<Element> transitionElements = forkElement.elements("transition");
        for (Element transitionElement : transitionElements) {
            /*
             * Only transitions with the "repeat" attribute specified with
             * true will be spawned by the ***RepetitionCriteria*** :) other transition
             * will behave just as it would have in a normal jbpm fork node.
             */
            if (Boolean.parseBoolean(transitionElement.attributeValue("repeat"))) {
                RepeatableTransition repeatableTransition = new RepeatableTransition();
                repeatableTransition.read(transitionElement);
                this.repeatableTransitions.add(repeatableTransition);
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public void execute(ExecutionContext executionContext) {
        // on a fork node you are supposed to execute *all* transitions
        Map<String, RepeatableTransition> transitionMap = new HashMap<String, RepeatableTransition>();
        for(RepeatableTransition repeatableTransition : this.repeatableTransitions) {
            transitionMap.put(repeatableTransition.getTransitionName(), repeatableTransition);
        }

        List<Transition> transitionList = getLeavingTransitions();
        List<ForkedPath> forkPathsToTake = new ArrayList<ForkedPath>();
        for (Transition transition : transitionList) {
            if (transitionMap.containsKey(transition.getName())) {
                if(logger.isDebugEnabled()) {
                    logger.debug("Attempting to take multiple transition path [" + transition + "]");
                }
                RepeatableTransition repeatableTransition = transitionMap.get(transition.getName());
                forkPathsToTake.addAll(repeatableTransition.getForkedPaths(executionContext));
            } else {
                if(logger.isDebugEnabled()) {
                    logger.debug("Attempting to take single transition path [" + transition + "]");
                }
                forkPathsToTake.add(new ForkedPath(transition.getName(),
                        ExecutionContextUtil.createChildExecutionContext(executionContext,
                                transition.getName())));
            }
        }
        for(ForkedPath forkPath : forkPathsToTake) {
            if(logger.isDebugEnabled()) {
                logger.debug("Fork path taken is [" + forkPath + "]");
            }
            forkPath.takeTransition(this);
        }
        if(logger.isDebugEnabled()) {
            logger.debug("After forking child tokens are [" + executionContext.getToken().getChildren() + "]");
        }
    }
}
