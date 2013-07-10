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
package org.jboss.arquillian.extension.rest.warp.spi;

import javax.ws.rs.core.MultivaluedMap;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implementation of {@link MultivaluedMap}.
 *
 * @author <a href="mailto:jmnarloch@gmail.com">Jakub Narloch</a>
 */
public class MultivaluedMapImpl<K, V> extends HashMap<K, List<V>> implements MultivaluedMap<K, V> {

    /**
     * Creates new instance of {@link MultivaluedMapImpl} class.
     */
    public MultivaluedMapImpl() {
        // empty constructor
    }

    /**
     * Creates new instance of {@link MultivaluedMapImpl} from specfied map
     *
     * @param map the map
     */
    public MultivaluedMapImpl(MultivaluedMap<K, V> map) {

        addAll(map);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void putSingle(K key, V value) {

        List<V> list = new ArrayList<V>();
        list.add(value);
        put(key, list);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(K k, V v) {

        getList(k).add(v);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public V getFirst(K k) {

        List<V> list = get(k);

        return list != null ? list.get(0) : null;
    }

    /**
     * Adds all elements of given map.
     *
     * @param map the map
     */
    private void addAll(MultivaluedMap<K, V> map) {

        for (Map.Entry<K, List<V>> entry : map.entrySet()) {

            getList(entry.getKey()).addAll(entry.getValue());
        }
    }

    /**
     * Retrieves the list associated with given key.
     *
     * @param k the key
     *
     * @return the list associated with given key
     */
    private List<V> getList(K k) {

        List<V> list = get(k);

        if (list == null) {
            list = new ArrayList<V>();
            put(k, list);
        }

        return list;
    }
}
