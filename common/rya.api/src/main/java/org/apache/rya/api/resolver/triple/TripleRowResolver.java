package org.apache.rya.api.resolver.triple;

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



import org.apache.rya.api.RdfCloudTripleStoreConstants;
import org.apache.rya.api.domain.RyaStatement;
import org.apache.rya.api.domain.RyaType;
import org.apache.rya.api.domain.RyaIRI;

import java.util.Map;

import static org.apache.rya.api.RdfCloudTripleStoreConstants.TABLE_LAYOUT;

/**
 * Date: 7/17/12
 * Time: 7:33 AM
 */
public interface TripleRowResolver {

    public Map<RdfCloudTripleStoreConstants.TABLE_LAYOUT, TripleRow> serialize(RyaStatement statement) throws TripleRowResolverException;

    public RyaStatement deserialize(TABLE_LAYOUT table_layout, TripleRow tripleRow) throws TripleRowResolverException;

}
