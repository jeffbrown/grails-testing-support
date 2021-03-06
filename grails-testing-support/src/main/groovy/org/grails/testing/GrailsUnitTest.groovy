/*
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.grails.testing

import grails.core.GrailsApplication
import groovy.transform.CompileStatic
import org.grails.testing.runtime.TestRuntime
import org.grails.testing.runtime.TestRuntimeFactory
import org.junit.Rule
import org.spockframework.runtime.model.FieldMetadata
import org.springframework.context.ConfigurableApplicationContext
import org.springframework.context.MessageSource

@CompileStatic
trait GrailsUnitTest {

    private TestRuntime currentRuntime;

    @Rule
    @FieldMetadata(
            line = -1,
            name = "freshRuntimeRule",
            ordinal = 0
    )
    public TestRuntimeRule freshRuntimeRule = new TestRuntimeRule(testInstance: this)

    /**
     *
     * @return grailsApplication.mainContext
     */
    ConfigurableApplicationContext getApplicationContext() {
        (ConfigurableApplicationContext) grailsApplication.mainContext
    }

    /**
     *
     * @return The GrailsApplication instance
     */
    GrailsApplication getGrailsApplication() {
        (GrailsApplication) runtime.getValue("grailsApplication", [testInstance: this])
    }

    public TestRuntime getRuntime() {
        if (currentRuntime == null) {
            currentRuntime = TestRuntimeFactory.getRuntimeForTestClass(this.class);
        }
        if (currentRuntime == null) {
            throw new IllegalStateException("Current TestRuntime instance is null.");
        } else if (currentRuntime.isClosed()) {
            throw new IllegalStateException("Current TestRuntime instance is closed.");
        }
        return currentRuntime;
    }

    public void setRuntime(TestRuntime runtime) {
        this.currentRuntime = runtime;
    }

    void defineBeans(boolean immediateDelivery = true, Closure<?> closure) {
        runtime.publishEvent("defineBeans", [closure: closure], [immediateDelivery: immediateDelivery])
    }

    /**
     *
     * @return the MessageSource bean from the applicatin context
     */
    MessageSource getMessageSource() {
        applicationContext.getBean("messageSource", MessageSource)
    }
}
