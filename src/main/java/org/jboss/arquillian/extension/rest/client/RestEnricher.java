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

import javax.ws.rs.core.Response;

import org.jboss.arquillian.core.api.Instance;
import org.jboss.arquillian.core.api.annotation.Inject;
import org.jboss.arquillian.test.spi.TestEnricher;

/**
 * RestEnricher
 *
 * @author <a href="mailto:aslak@redhat.com">Aslak Knutsen</a>
 * @version $Revision: $
 */
public class RestEnricher implements TestEnricher
{
   @Inject
   private Instance<Response> responseInst;

   @Override
   public void enrich(Object testCase)
   {
   }

   @Override
   public Object[] resolve(Method method)
   {
      Object[] values = new Object[method.getParameterTypes().length];
      if(responseInst.get() != null)
      {
         Class<?>[] parameterTypes = method.getParameterTypes();
         for(int i = 0; i < parameterTypes.length; i++)
         {
            if(Response.class.isAssignableFrom(parameterTypes[i]))
            {
               values[i] = responseInst.get();
            }
         }
      }
      return values;
   }
}
