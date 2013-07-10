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
package org.jboss.arquillian.extension.rest.warp.impl.client;

import org.jboss.arquillian.container.test.spi.RemoteLoadableExtension;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.spi.LoadableExtension;
import org.jboss.arquillian.core.spi.ServiceLoader;
import org.jboss.arquillian.extension.rest.warp.api.RestContext;
import org.jboss.arquillian.extension.rest.warp.impl.container.WarpRestRemoteExtension;
import org.jboss.arquillian.extension.rest.warp.impl.provider.RestContextProvider;
import org.jboss.arquillian.extension.rest.warp.spi.WarpRestInterceptorEnricher;
import org.jboss.arquillian.warp.spi.WarpDeploymentEnrichmentExtension;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.JavaArchive;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import java.util.Collection;

/**
 * Warp Rest extension. This class is responsible for bootstrapping the core functionality,
 * package all required classes and enrich the test deployment.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class WarpRestExtension implements LoadableExtension, WarpDeploymentEnrichmentExtension {

    /**
     * {@link ServiceLoader} instance.
     */
    @Inject
    private Instance<ServiceLoader> serviceLoaderInstance;

    /**
     * {@inheritDoc}
     */
    @Override
    public void register(ExtensionBuilder builder) {

        builder.service(WarpDeploymentEnrichmentExtension.class, this.getClass());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JavaArchive getEnrichmentLibrary() {

        JavaArchive archive = ShrinkWrap.create(JavaArchive.class, "warp-rest-extension.jar");

        // adds the api classes
        archive.addPackage(RestContext.class.getPackage());

        // adds the spi classes
        archive.addPackage(WarpRestInterceptorEnricher.class.getPackage());

        // adds the implementation classes
        archive.addPackage(WarpRestRemoteExtension.class.getPackage());
        archive.addPackage(RestContextProvider.class.getPackage());

        // registers the extension
        archive.addAsServiceProvider(RemoteLoadableExtension.class, WarpRestRemoteExtension.class);

        return archive;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void enrichWebArchive(WebArchive webArchive) {

        Collection<WarpRestInterceptorEnricher> enrichers =
                serviceLoaderInstance.get().all(WarpRestInterceptorEnricher.class);

        // for each registered enricher
        for (WarpRestInterceptorEnricher enricher : enrichers) {

            // enriches the deployment archive
            enricher.enrichWebArchive(webArchive);
        }
    }
}
