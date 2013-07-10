/*
 * JBoss, Home of Professional Open Source
 * Copyright 2013 Red Hat Inc. and/or its affiliates and other contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a
 * full listing of individual contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jboss.arquillian.quickstart.cxf.service.rs;

import org.jboss.arquillian.quickstart.cxf.application.StockApplication;
import org.jboss.arquillian.quickstart.cxf.model.Stock;
import org.jboss.arquillian.quickstart.cxf.service.StockService;
import org.jboss.shrinkwrap.api.Archive;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.DependencyResolvers;
import org.jboss.shrinkwrap.resolver.api.maven.MavenDependencyResolver;

import java.io.File;

/**
 * An utility class that creates the test deployments.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
final class Deployments {

    /**
     * Creates test deployment.
     *
     * @return the test deployment
     */
    public static Archive createDeployment() {
        File[] libs = loadLibraries();

        return ShrinkWrap.create(WebArchive.class)
                .addClasses(StockApplication.class, Stock.class, StockService.class, StockServiceResource.class)
                .addAsWebInfResource("WEB-INF/web.xml")
                .addAsWebResource("restclient.jsp")
                .addAsWebResource("js/jquery-1.8.2.min.js", "js/jquery-1.8.2.min.js")
                .addAsLibraries(libs);
    }

    /**
     * Loads all required dependencies needed to run the application in the application server.
     *
     * @return the loaded dependencies
     */
    private static File[] loadLibraries() {
        return DependencyResolvers.use(MavenDependencyResolver.class)
                .loadMetadataFromPom("pom.xml")
                .artifacts("org.apache.cxf:cxf-rt-frontend-jaxrs")
                .artifacts("org.apache.cxf:cxf-rt-rs-extension-providers")
                .artifacts("org.codehaus.jettison:jettison")
                .resolveAsFiles();
    }
}
