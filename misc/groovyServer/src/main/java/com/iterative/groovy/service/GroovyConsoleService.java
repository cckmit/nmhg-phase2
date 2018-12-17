/*
 * Copyright 2007 Bruce Fancher
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.iterative.groovy.service;

import org.apache.log4j.Logger;

import groovy.ui.Console;

/**
 * 
 * @author Bruce Fancher
 * 
 */
public class GroovyConsoleService extends GroovyService {

	private static final Logger logger = Logger.getLogger(GroovyConsoleService.class);
	
    private Thread thread;

    public GroovyConsoleService() {
        super();
    }

    public void launch() {
    	logger.info("Launching Groovy Console Service");
        thread = new Thread() {
            @Override
            public void run() {
            	logger.info("Attempting to run console thread");
                try {
                    new Console(createBinding()).run();
                }
                catch (Exception e) {
                    logger.error("Exception occured while launching thread", e);
                }
            }
        };

        thread.setDaemon(true);
        thread.start();
    }
}
