package org.apache.rya.api.resolver.impl;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import java.util.Random;

import junit.framework.TestCase;
import org.apache.rya.api.domain.RyaType;
import org.eclipse.rdf4j.model.vocabulary.XMLSchema;

/**
 * Date: 7/20/12
 * Time: 9:43 AM
 */
public class DoubleRyaTypeResolverTest extends TestCase {

    public void testDoubleSerialization() throws Exception {
        Double d = randomDouble();
        RyaType ryaType = new RyaType(XMLSchema.DOUBLE, d.toString());
        byte[] serialize = new DoubleRyaTypeResolver().serialize(ryaType);
        assertEquals(d, Double.parseDouble(new DoubleRyaTypeResolver().deserialize(serialize).getData()));
    }

    private double randomDouble() {
        return new Random(System.currentTimeMillis()).nextDouble();
    }
}
