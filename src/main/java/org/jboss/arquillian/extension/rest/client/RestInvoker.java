/*
 * JBoss, Home of Professional Open Source
 * Copyright 2011 Red Hat Inc. and/or its affiliates and other contributors
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
package org.jboss.arquillian.extension.rest.client;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Providers;

import org.jboss.arquillian.container.spi.client.protocol.metadata.HTTPContext;
import org.jboss.arquillian.container.spi.client.protocol.metadata.ProtocolMetaData;
import org.jboss.arquillian.container.spi.client.protocol.metadata.Servlet;
import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.InstanceProducer;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.core.api.annotation.Observes;
import org.jboss.arquillian.test.spi.annotation.TestScoped;
import org.jboss.arquillian.test.spi.event.suite.Before;
import org.jboss.resteasy.client.ClientExecutor;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.core.ClientInvoker;
import org.jboss.resteasy.client.core.ClientInvokerInterceptorFactory;
import org.jboss.resteasy.client.core.extractors.DefaultEntityExtractorFactory;
import org.jboss.resteasy.client.core.extractors.EntityExtractorFactory;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.jboss.resteasy.util.IsHttpMethod;

/**
 * RestInvoker
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class RestInvoker
{
   @Inject
   private Instance<ProtocolMetaData> metaDataInst;

   // Share the Response in the Arquillian scopes so it can be injected by the RestEnricher
   @Inject @TestScoped
   private InstanceProducer<Response> responseProducer;

   public void call(@Observes Before event)
   {
      Set<String> httpMethods = IsHttpMethod.getHttpMethods(event.getTestMethod());
      if (httpMethods != null && httpMethods.size() == 1)
      {
         Response response = doRestCall(event.getTestMethod(), httpMethods);
         responseProducer.set(response);
      }
   }

   /**
    * Get the deployment URL, create the RestEasy Client Proxy Invoker for the given TestMethod and return the result.
    *
    * @param testMethod
    * @param httpMethods
    * @return
    */
   private Response doRestCall(Method testMethod, Set<String> httpMethods)
   {
      URI baseUri = getBaseURL();
      ClientExecutor executor = ClientRequest.getDefaultExecutor();
      ResteasyProviderFactory providerFactory = ResteasyProviderFactory.getInstance();
      EntityExtractorFactory extractorFactory = new DefaultEntityExtractorFactory();

      ArqClientInvoker invoker = new ArqClientInvoker(baseUri, testMethod.getDeclaringClass(), testMethod, providerFactory, executor, extractorFactory);
      invoker.getAttributes().putAll(new HashMap<String, Object>());

      ClientInvokerInterceptorFactory.applyDefaultInterceptors(invoker, providerFactory, testMethod.getDeclaringClass(), testMethod);
      invoker.setHttpMethod(httpMethods.iterator().next());

      return invoker.request(new Object[]{null});
   }

   // Currently no way to share @ArquillianResource URL (URLResourceProvider) logic internally, copied logic
   private URI getBaseURL()
   {
      HTTPContext context = metaDataInst.get().getContext(HTTPContext.class);
      if(allInSameContext(context.getServlets()))
      {
         return context.getServlets().get(0).getBaseURI();
      }
      throw new IllegalStateException("No baseURL found in HTTPContext");
   }

   private boolean allInSameContext(List<Servlet> servlets)
   {
      Set<String> context = new HashSet<String>();
      for (Servlet servlet : servlets)
      {
         context.add(servlet.getContextRoot());
      }
      return context.size() == 1;
   }

   // Need to extend ClientInvoker to call createRequest
   private static class ArqClientInvoker extends ClientInvoker
   {
      public ArqClientInvoker(URI baseUri, Class<?> declaring, Method method, ResteasyProviderFactory providerFactory, ClientExecutor executor, EntityExtractorFactory extractorFactory)
      {
         super(baseUri, declaring, method, providerFactory, executor, extractorFactory);
      }

      // As oppose to invoke(Object[]) which will return the Response Entity, we expose the raw Response object 
      public Response request(Object[] args)
      {
         boolean isProvidersSet = ResteasyProviderFactory.getContextData(Providers.class) != null;
         if (!isProvidersSet) ResteasyProviderFactory.pushContext(Providers.class, providerFactory);

         try
         {
            if (uri == null) throw new RuntimeException("You have not set a base URI for the client proxy");

            ClientRequest request = createRequest(args);

            try
            {
               return request.httpMethod(httpMethod);
            }
            catch (Exception e)
            {
               throw new RuntimeException(e);
            }
         }
         finally
         {
            if (!isProvidersSet) ResteasyProviderFactory.popContextData(Providers.class);
         }
      }
   }
}
