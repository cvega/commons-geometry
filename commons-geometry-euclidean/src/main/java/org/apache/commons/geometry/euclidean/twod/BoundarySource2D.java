/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.commons.geometry.euclidean.twod;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.geometry.core.partitioning.BoundarySource;

/** Extension of the {@link BoundarySource} interface for Euclidean 2D
 * space.
 */
public interface BoundarySource2D extends BoundarySource<Segment> {

    /** Construct a new BSP tree from the boundaries contained in this
     * instance.
     * @return a new BSP tree constructed from the boundaries in this
     *      instance
     * @see RegionBSPTree2D#from(BoundarySource)
     */
    default RegionBSPTree2D toTree() {
        return RegionBSPTree2D.from(this);
    }

    /** Return a {@link BoundarySource2D} instance containing the given segments.
     * @param boundaries segments to include in the boundary source
     * @return a boundary source containing the given boundaries
     */
    static BoundarySource2D from(final Segment... boundaries) {
        return from(Arrays.asList(boundaries));
    }

    /** Return a {@link BoundarySource2D} instance containing the given segments. The given
     * collection is used directly as the source of the segments; no copy is made.
     * @param boundaries segments to include in the boundary source
     * @return a boundary source containing the given boundaries
     */
    static BoundarySource2D from(final Collection<Segment> boundaries) {
        return boundaries::stream;
    }
}