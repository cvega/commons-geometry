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
package org.apache.commons.geometry.spherical.oned;

import java.util.List;

import org.apache.commons.geometry.core.Region;
import org.apache.commons.geometry.core.RegionLocation;
import org.apache.commons.geometry.core.partitioning.Split;
import org.apache.commons.geometry.core.partitioning.SplitLocation;
import org.apache.commons.geometry.core.precision.DoublePrecisionContext;
import org.apache.commons.geometry.core.precision.EpsilonDoublePrecisionContext;
import org.apache.commons.geometry.euclidean.twod.Vector2D;
import org.apache.commons.numbers.angle.PlaneAngleRadians;
import org.junit.Assert;
import org.junit.Test;

public class RegionBSPTree1STest {

    private static final double TEST_EPS = 1e-10;

    private static final DoublePrecisionContext TEST_PRECISION =
            new EpsilonDoublePrecisionContext(TEST_EPS);

    private static final Transform1S HALF_PI_PLUS_AZ = Transform1S.createRotation(PlaneAngleRadians.PI_OVER_TWO);

    private static final Transform1S PI_MINUS_AZ = Transform1S.createNegation().rotate(PlaneAngleRadians.PI);

    @Test
    public void testConstructor_default() {
        // act
        RegionBSPTree1S tree = new RegionBSPTree1S();

        // assert
        Assert.assertFalse(tree.isFull());
        Assert.assertTrue(tree.isEmpty());

        Assert.assertEquals(0, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertNull(tree.getBarycenter());
    }

    @Test
    public void testConstructor_true() {
        // act
        RegionBSPTree1S tree = new RegionBSPTree1S(true);

        // assert
        Assert.assertTrue(tree.isFull());
        Assert.assertFalse(tree.isEmpty());

        Assert.assertEquals(PlaneAngleRadians.TWO_PI, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertNull(tree.getBarycenter());
    }

    @Test
    public void testConstructor_false() {
        // act
        RegionBSPTree1S tree = new RegionBSPTree1S(false);

        // assert
        Assert.assertFalse(tree.isFull());
        Assert.assertTrue(tree.isEmpty());

        Assert.assertEquals(0, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertNull(tree.getBarycenter());
    }

    @Test
    public void testFull() {
        // act
        RegionBSPTree1S tree = RegionBSPTree1S.full();

        // assert
        Assert.assertTrue(tree.isFull());
        Assert.assertFalse(tree.isEmpty());

        Assert.assertEquals(PlaneAngleRadians.TWO_PI, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertNull(tree.getBarycenter());
    }

    @Test
    public void testEmpty() {
        // act
        RegionBSPTree1S tree = RegionBSPTree1S.empty();

        // assert
        Assert.assertFalse(tree.isFull());
        Assert.assertTrue(tree.isEmpty());

        Assert.assertEquals(0, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertNull(tree.getBarycenter());
    }

    @Test
    public void testCopy() {
        // arrange
        RegionBSPTree1S orig = RegionBSPTree1S.fromInterval(AngularInterval.of(0, PlaneAngleRadians.PI, TEST_PRECISION));

        // act
        RegionBSPTree1S copy = orig.copy();

        // assert
        Assert.assertNotSame(orig, copy);

        orig.setEmpty();

        checkSingleInterval(copy, 0, PlaneAngleRadians.PI);
    }

    @Test
    public void testFromInterval_full() {
        // act
        RegionBSPTree1S tree = RegionBSPTree1S.fromInterval(AngularInterval.full());

        // assert
        Assert.assertTrue(tree.isFull());
    }

    @Test
    public void testFromInterval_nonFull() {
        for (double theta = 0.0; theta <= PlaneAngleRadians.TWO_PI; theta += 0.2) {
            // arrange
            double min = theta;
            double max = theta + PlaneAngleRadians.PI_OVER_TWO;

            // act
            RegionBSPTree1S tree = RegionBSPTree1S.fromInterval(AngularInterval.of(min, max, TEST_PRECISION));

            checkSingleInterval(tree, min, max);

            Assert.assertEquals(PlaneAngleRadians.PI_OVER_TWO, tree.getSize(), TEST_EPS);
            Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
            Assert.assertEquals(PlaneAngleRadians.normalizeBetweenZeroAndTwoPi(theta + (0.25 * PlaneAngleRadians.PI)),
                    tree.getBarycenter().getNormalizedAzimuth(), TEST_EPS);
        }
    }

    @Test
    public void testClassify_full() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.full();

        // act/assert
        for (double az = -PlaneAngleRadians.TWO_PI; az <= 2 * PlaneAngleRadians.TWO_PI; az += 0.2) {
            checkClassify(tree, RegionLocation.INSIDE, az);
        }
    }

    @Test
    public void testClassify_empty() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();

        // act/assert
        for (double az = -PlaneAngleRadians.TWO_PI; az <= 2 * PlaneAngleRadians.TWO_PI; az += 0.2) {
            checkClassify(tree, RegionLocation.OUTSIDE, az);
        }
    }

    @Test
    public void testClassify() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.fromInterval(
                AngularInterval.of(-PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));

        // act/assert
        checkClassify(tree, RegionLocation.BOUNDARY,
                -PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO,
                -PlaneAngleRadians.PI_OVER_TWO - PlaneAngleRadians.TWO_PI, PlaneAngleRadians.PI_OVER_TWO + PlaneAngleRadians.TWO_PI);
        checkClassify(tree, RegionLocation.INSIDE,
                0.0, 0.5, -0.5,
                PlaneAngleRadians.TWO_PI, 0.5 + PlaneAngleRadians.TWO_PI, -0.5 - PlaneAngleRadians.TWO_PI);
        checkClassify(tree, RegionLocation.OUTSIDE,
                PlaneAngleRadians.PI, PlaneAngleRadians.PI + 0.5, PlaneAngleRadians.PI - 0.5,
                PlaneAngleRadians.PI + PlaneAngleRadians.TWO_PI, PlaneAngleRadians.PI + 0.5 + PlaneAngleRadians.TWO_PI,
                PlaneAngleRadians.PI - 0.5 + PlaneAngleRadians.TWO_PI);
    }

    @Test
    public void testToIntervals_full() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.full();

        // act
        List<AngularInterval> intervals = tree.toIntervals();

        // assert
        Assert.assertEquals(1, intervals.size());

        AngularInterval interval = intervals.get(0);
        Assert.assertTrue(interval.isFull());
    }

    @Test
    public void testToIntervals_empty() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();

        // act
        List<AngularInterval> intervals = tree.toIntervals();

        // assert
        Assert.assertEquals(0, intervals.size());
    }

    @Test
    public void testToIntervals_singleCut() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();

        for (double theta = 0; theta <= PlaneAngleRadians.TWO_PI; theta += 0.2) {
            // act/assert
            tree.setEmpty();
            tree.getRoot().cut(CutAngles.createPositiveFacing(theta, TEST_PRECISION));

            checkSingleInterval(tree, 0, theta);

            tree.setEmpty();
            tree.getRoot().cut(CutAngles.createNegativeFacing(theta, TEST_PRECISION));

            checkSingleInterval(tree, theta, PlaneAngleRadians.TWO_PI);
        }
    }

    @Test
    public void testToIntervals_wrapAround_joinedIntervalsOnPositiveSide() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(0.25 * PlaneAngleRadians.PI, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));
        tree.add(AngularInterval.of(1.5 * PlaneAngleRadians.PI, 0.25 * PlaneAngleRadians.PI, TEST_PRECISION));

        // act
        List<AngularInterval> intervals = tree.toIntervals();

        // assert
        Assert.assertEquals(1, intervals.size());

        checkInterval(intervals.get(0), 1.5 * PlaneAngleRadians.PI, PlaneAngleRadians.PI_OVER_TWO);
    }

    @Test
    public void testToIntervals_wrapAround_joinedIntervalsOnNegativeSide() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(1.75 * PlaneAngleRadians.PI, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));
        tree.add(AngularInterval.of(1.5 * PlaneAngleRadians.PI, 1.75 * PlaneAngleRadians.PI, TEST_PRECISION));

        // act
        List<AngularInterval> intervals = tree.toIntervals();

        // assert
        Assert.assertEquals(1, intervals.size());

        checkInterval(intervals.get(0), 1.5 * PlaneAngleRadians.PI, PlaneAngleRadians.PI_OVER_TWO);
    }

    @Test
    public void testToIntervals_multipleIntervals() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(-PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI - 0.5, PlaneAngleRadians.PI, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI, PlaneAngleRadians.PI + 0.5, TEST_PRECISION));

        // act
        List<AngularInterval> intervals = tree.toIntervals();

        // assert
        Assert.assertEquals(2, intervals.size());

        checkInterval(intervals.get(0), PlaneAngleRadians.PI - 0.5, PlaneAngleRadians.PI + 0.5);
        checkInterval(intervals.get(1), -PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO);
    }

    @Test
    public void testToIntervals_multipleIntervals_complement() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(-PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI - 0.5, PlaneAngleRadians.PI, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI, PlaneAngleRadians.PI + 0.5, TEST_PRECISION));

        tree.complement();

        // act
        List<AngularInterval> intervals = tree.toIntervals();

        // assert
        Assert.assertEquals(2, intervals.size());

        checkInterval(intervals.get(0), PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI - 0.5);
        checkInterval(intervals.get(1), PlaneAngleRadians.PI + 0.5, -PlaneAngleRadians.PI_OVER_TWO);
    }

    @Test
    public void testSplit_empty() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();

        // act/assert
        Assert.assertEquals(SplitLocation.NEITHER,
                tree.split(CutAngles.createPositiveFacing(0, TEST_PRECISION)).getLocation());
        Assert.assertEquals(SplitLocation.NEITHER,
                tree.split(CutAngles.createNegativeFacing(PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)).getLocation());
        Assert.assertEquals(SplitLocation.NEITHER,
                tree.split(CutAngles.createPositiveFacing(PlaneAngleRadians.PI, TEST_PRECISION)).getLocation());
        Assert.assertEquals(SplitLocation.NEITHER,
                tree.split(CutAngles.createNegativeFacing(-PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)).getLocation());
        Assert.assertEquals(SplitLocation.NEITHER,
                tree.split(CutAngles.createPositiveFacing(PlaneAngleRadians.TWO_PI, TEST_PRECISION)).getLocation());
    }

    @Test
    public void testSplit_full() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.full();

        // act/assert
        checkSimpleSplit(
            tree.split(CutAngles.createPositiveFacing(1e-6, TEST_PRECISION)),
            AngularInterval.of(0, 1e-6, TEST_PRECISION),
            AngularInterval.of(1e-6, PlaneAngleRadians.TWO_PI, TEST_PRECISION)
        );
        checkSimpleSplit(
            tree.split(CutAngles.createNegativeFacing(PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)),
            AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.TWO_PI, TEST_PRECISION),
            AngularInterval.of(0, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)
        );
        checkSimpleSplit(
            tree.split(CutAngles.createPositiveFacing(PlaneAngleRadians.PI, TEST_PRECISION)),
            AngularInterval.of(0, PlaneAngleRadians.PI, TEST_PRECISION),
            AngularInterval.of(PlaneAngleRadians.PI, PlaneAngleRadians.TWO_PI, TEST_PRECISION)
        );
        checkSimpleSplit(
            tree.split(CutAngles.createNegativeFacing(-PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)),
            AngularInterval.of(-PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.TWO_PI, TEST_PRECISION),
            AngularInterval.of(0, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)
        );
        checkSimpleSplit(
            tree.split(CutAngles.createPositiveFacing(PlaneAngleRadians.TWO_PI - 1e-6, TEST_PRECISION)),
            AngularInterval.of(0, PlaneAngleRadians.TWO_PI - 1e-6, TEST_PRECISION),
            AngularInterval.of(PlaneAngleRadians.TWO_PI - 1e-6, PlaneAngleRadians.TWO_PI, TEST_PRECISION)
        );
    }

    @Test
    public void testSplit_full_cutEquivalentToZero() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.full();

        AngularInterval twoPi = AngularInterval.of(0, PlaneAngleRadians.TWO_PI, TEST_PRECISION);

        // act/assert
        checkSimpleSplit(
            tree.split(CutAngles.createPositiveFacing(0, TEST_PRECISION)),
            null,
            twoPi
        );
        checkSimpleSplit(
            tree.split(CutAngles.createNegativeFacing(0, TEST_PRECISION)),
            twoPi,
            null
        );

        checkSimpleSplit(
            tree.split(CutAngles.createPositiveFacing(PlaneAngleRadians.TWO_PI - 1e-18, TEST_PRECISION)),
            null,
            twoPi
        );
        checkSimpleSplit(
            tree.split(CutAngles.createNegativeFacing(PlaneAngleRadians.TWO_PI - 1e-18, TEST_PRECISION)),
            twoPi,
            null
        );
    }

    @Test
    public void testSplit_singleInterval() {
        // arrange
        AngularInterval interval = AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION);
        RegionBSPTree1S tree = interval.toTree();

        // act
        checkSimpleSplit(
            tree.split(CutAngles.createNegativeFacing(0, TEST_PRECISION)),
            interval,
            null
        );
        checkSimpleSplit(
            tree.split(CutAngles.createNegativeFacing(-PlaneAngleRadians.TWO_PI, TEST_PRECISION)),
            interval,
            null
        );

        checkSimpleSplit(
            tree.split(CutAngles.createPositiveFacing(PlaneAngleRadians.TWO_PI + PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION)),
            null,
            interval
        );
        checkSimpleSplit(
            tree.split(CutAngles.createPositiveFacing(1.5 * PlaneAngleRadians.PI, TEST_PRECISION)),
            interval,
            null
        );

        checkSimpleSplit(
            tree.split(CutAngles.createNegativeFacing(PlaneAngleRadians.PI, TEST_PRECISION)),
            AngularInterval.of(PlaneAngleRadians.PI, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION),
            AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI, TEST_PRECISION)
        );
    }

    @Test
    public void testSplit_singleIntervalSplitIntoTwoIntervalsOnSameSide() {
        // arrange
        RegionBSPTree1S tree = AngularInterval.of(-PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION).toTree();

        CutAngle cut = CutAngles.createPositiveFacing(0, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.split(cut);

        // assert
        Assert.assertEquals(SplitLocation.PLUS, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        Assert.assertNull(minus);

        RegionBSPTree1S plus = split.getPlus();
        List<AngularInterval> plusIntervals = plus.toIntervals();
        Assert.assertEquals(1, plusIntervals.size());
        checkInterval(plusIntervals.get(0), -PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO);
    }

    @Test
    public void testSplit_multipleRegions() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(PlaneAngleRadians.TWO_PI - 1, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI, -PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));

        CutAngle cut = CutAngles.createNegativeFacing(1, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.split(cut);

        // assert
        Assert.assertEquals(SplitLocation.BOTH, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        List<AngularInterval> minusIntervals = minus.toIntervals();
        Assert.assertEquals(3, minusIntervals.size());
        checkInterval(minusIntervals.get(0), 1, PlaneAngleRadians.PI_OVER_TWO);
        checkInterval(minusIntervals.get(1), PlaneAngleRadians.PI, -PlaneAngleRadians.PI_OVER_TWO);
        checkInterval(minusIntervals.get(2), PlaneAngleRadians.TWO_PI - 1, 0);

        RegionBSPTree1S plus = split.getPlus();
        List<AngularInterval> plusIntervals = plus.toIntervals();
        Assert.assertEquals(1, plusIntervals.size());
        checkInterval(plusIntervals.get(0), 0, 1);
    }

    @Test
    public void testSplitDiameter_full() {
        // arrange
        RegionBSPTree1S full = RegionBSPTree1S.full();
        CutAngle splitter = CutAngles.createPositiveFacing(PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = full.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.BOTH, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        List<AngularInterval> minusIntervals = minus.toIntervals();
        Assert.assertEquals(1, minusIntervals.size());
        checkInterval(minusIntervals.get(0), 1.5 * PlaneAngleRadians.PI, 2.5 * PlaneAngleRadians.PI);

        RegionBSPTree1S plus = split.getPlus();
        List<AngularInterval> plusIntervals = plus.toIntervals();
        Assert.assertEquals(1, plusIntervals.size());
        checkInterval(plusIntervals.get(0), PlaneAngleRadians.PI_OVER_TWO, 1.5 * PlaneAngleRadians.PI);
    }

    @Test
    public void testSplitDiameter_empty() {
        // arrange
        RegionBSPTree1S empty = RegionBSPTree1S.empty();
        CutAngle splitter = CutAngles.createPositiveFacing(PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = empty.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.NEITHER, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        Assert.assertNull(minus);

        RegionBSPTree1S plus = split.getPlus();
        Assert.assertNull(plus);
    }

    @Test
    public void testSplitDiameter_minus_zeroOnMinusSide() {
        // arrange
        RegionBSPTree1S tree = AngularInterval.of(0, 1, TEST_PRECISION).toTree();
        CutAngle splitter = CutAngles.createPositiveFacing(1, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.MINUS, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        List<AngularInterval> minusIntervals = minus.toIntervals();
        Assert.assertEquals(1, minusIntervals.size());
        checkInterval(minusIntervals.get(0), 0, 1);

        RegionBSPTree1S plus = split.getPlus();
        Assert.assertNull(plus);
    }

    @Test
    public void testSplitDiameter_minus_zeroOnPlusSide() {
        // arrange
        RegionBSPTree1S tree = AngularInterval.of(1, 2, TEST_PRECISION).toTree();
        CutAngle splitter = CutAngles.createNegativeFacing(0, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.MINUS, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        List<AngularInterval> minusIntervals = minus.toIntervals();
        Assert.assertEquals(1, minusIntervals.size());
        checkInterval(minusIntervals.get(0), 1, 2);

        RegionBSPTree1S plus = split.getPlus();
        Assert.assertNull(plus);
    }

    @Test
    public void testSplitDiameter_plus_zeroOnMinusSide() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(1, 1.1, TEST_PRECISION));
        tree.add(AngularInterval.of(2, 2.1, TEST_PRECISION));

        CutAngle splitter = CutAngles.createPositiveFacing(1, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.PLUS, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        Assert.assertNull(minus);

        RegionBSPTree1S plus = split.getPlus();
        List<AngularInterval> plusIntervals = plus.toIntervals();
        Assert.assertEquals(2, plusIntervals.size());
        checkInterval(plusIntervals.get(0), 1, 1.1);
        checkInterval(plusIntervals.get(1), 2, 2.1);
    }

    @Test
    public void testSplitDiameter_plus_zeroOnPlusSide() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(1, 1.1, TEST_PRECISION));
        tree.add(AngularInterval.of(2, 2.1, TEST_PRECISION));

        CutAngle splitter = CutAngles.createNegativeFacing(PlaneAngleRadians.PI - 1, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.PLUS, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        Assert.assertNull(minus);

        RegionBSPTree1S plus = split.getPlus();
        List<AngularInterval> plusIntervals = plus.toIntervals();
        Assert.assertEquals(2, plusIntervals.size());
        checkInterval(plusIntervals.get(0), 1, 1.1);
        checkInterval(plusIntervals.get(1), 2, 2.1);
    }

    @Test
    public void testSplitDiameter_both_zeroOnMinusSide() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(1, 1.1, TEST_PRECISION));
        tree.add(AngularInterval.of(2, 3, TEST_PRECISION));

        CutAngle splitter = CutAngles.createPositiveFacing(2.5, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.BOTH, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        List<AngularInterval> plusIntervals = minus.toIntervals();
        Assert.assertEquals(2, plusIntervals.size());
        checkInterval(plusIntervals.get(0), 1, 1.1);
        checkInterval(plusIntervals.get(1), 2, 2.5);

        RegionBSPTree1S plus = split.getPlus();
        List<AngularInterval> minusIntervals = plus.toIntervals();
        Assert.assertEquals(1, minusIntervals.size());
        checkInterval(minusIntervals.get(0), 2.5, 3);
    }

    @Test
    public void testSplitDiameter_both_zeroOnPlusSide() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(1, 1.1, TEST_PRECISION));
        tree.add(AngularInterval.of(2, 3, TEST_PRECISION));

        CutAngle splitter = CutAngles.createNegativeFacing(2.5, TEST_PRECISION);

        // act
        Split<RegionBSPTree1S> split = tree.splitDiameter(splitter);

        // assert
        Assert.assertEquals(SplitLocation.BOTH, split.getLocation());

        RegionBSPTree1S minus = split.getMinus();
        List<AngularInterval> minusIntervals = minus.toIntervals();
        Assert.assertEquals(1, minusIntervals.size());
        checkInterval(minusIntervals.get(0), 2.5, 3);

        RegionBSPTree1S plus = split.getPlus();
        List<AngularInterval> plusIntervals = plus.toIntervals();
        Assert.assertEquals(2, plusIntervals.size());
        checkInterval(plusIntervals.get(0), 1, 1.1);
        checkInterval(plusIntervals.get(1), 2, 2.5);
    }

    @Test
    public void testRegionProperties_singleInterval_wrapsZero() {
        // arrange
        RegionBSPTree1S tree = AngularInterval.of(-PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI,
                TEST_PRECISION).toTree();

        // act/assert
        Assert.assertEquals(1.5 * PlaneAngleRadians.PI, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertEquals(0.25 * PlaneAngleRadians.PI, tree.getBarycenter().getAzimuth(), TEST_EPS);
    }

    @Test
    public void testRegionProperties_singleInterval_doesNotWrap() {
        // arrange
        RegionBSPTree1S tree = AngularInterval.of(PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.TWO_PI,
                TEST_PRECISION).toTree();

        // act/assert
        Assert.assertEquals(1.5 * PlaneAngleRadians.PI, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertEquals(1.25 * PlaneAngleRadians.PI, tree.getBarycenter().getAzimuth(), TEST_EPS);
    }

    @Test
    public void testRegionProperties_multipleIntervals_sameSize() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(0, 0.1, TEST_PRECISION));
        tree.add(AngularInterval.of(0.2, 0.3, TEST_PRECISION));

        // act/assert
        Assert.assertEquals(0.2, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertEquals(0.15, tree.getBarycenter().getAzimuth(), TEST_EPS);
    }

    @Test
    public void testRegionProperties_multipleIntervals_differentSizes() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(0, 0.2, TEST_PRECISION));
        tree.add(AngularInterval.of(0.3, 0.7, TEST_PRECISION));

        // act/assert
        Assert.assertEquals(0.6, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);

        Vector2D barycenterVector = Point1S.of(0.1).getVector().withNorm(0.2)
                .add(Point1S.of(0.5).getVector().withNorm(0.4));
        Assert.assertEquals(Point1S.from(barycenterVector).getAzimuth(), tree.getBarycenter().getAzimuth(), TEST_EPS);
    }

    @Test
    public void testRegionProperties_equalAndOppositeIntervals() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(-1, 1, TEST_PRECISION));
        tree.add(AngularInterval.of(Math.PI - 1, Math.PI + 1, TEST_PRECISION));

        // act/assert
        Assert.assertEquals(4, tree.getSize(), TEST_EPS);
        Assert.assertEquals(0, tree.getBoundarySize(), TEST_EPS);
        Assert.assertNull(tree.getBarycenter()); // no unique barycenter exists
    }

    @Test
    public void testTransform_fullAndEmpty() {
        // arrange
        RegionBSPTree1S full = RegionBSPTree1S.full();
        RegionBSPTree1S empty = RegionBSPTree1S.empty();

        // act
        full.transform(PI_MINUS_AZ);
        empty.transform(HALF_PI_PLUS_AZ);

        // assert
        Assert.assertTrue(full.isFull());
        Assert.assertFalse(full.isEmpty());

        Assert.assertFalse(empty.isFull());
        Assert.assertTrue(empty.isEmpty());
    }

    @Test
    public void testTransform_halfPiPlusAz() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(-1, 1, TEST_PRECISION));
        tree.add(AngularInterval.of(2, 3, TEST_PRECISION));

        // act
        tree.transform(HALF_PI_PLUS_AZ);

        // assert
        Assert.assertEquals(3, tree.getSize(), TEST_EPS);

        List<AngularInterval> intervals = tree.toIntervals();

        Assert.assertEquals(2, intervals.size());
        checkInterval(intervals.get(0), PlaneAngleRadians.PI_OVER_TWO - 1, PlaneAngleRadians.PI_OVER_TWO + 1);
        checkInterval(intervals.get(1), PlaneAngleRadians.PI_OVER_TWO + 2, PlaneAngleRadians.PI_OVER_TWO + 3);
    }

    @Test
    public void testTransform_piMinusAz() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(-1, 1, TEST_PRECISION));
        tree.add(AngularInterval.of(2, 3, TEST_PRECISION));

        // act
        tree.transform(PI_MINUS_AZ);

        // assert
        Assert.assertEquals(3, tree.getSize(), TEST_EPS);

        List<AngularInterval> intervals = tree.toIntervals();

        Assert.assertEquals(2, intervals.size());
        checkInterval(intervals.get(0), PlaneAngleRadians.PI - 3, PlaneAngleRadians.PI - 2);
        checkInterval(intervals.get(1), PlaneAngleRadians.PI - 1, PlaneAngleRadians.PI + 1);
    }

    @Test
    public void testProject_fullAndEmpty() {
        // arrange
        RegionBSPTree1S full = RegionBSPTree1S.full();
        RegionBSPTree1S empty = RegionBSPTree1S.empty();

        // act/assert
        Assert.assertNull(full.project(Point1S.ZERO));
        Assert.assertNull(full.project(Point1S.PI));

        Assert.assertNull(empty.project(Point1S.ZERO));
        Assert.assertNull(empty.project(Point1S.PI));
    }

    @Test
    public void testProject_withIntervals() {
        // arrange
        RegionBSPTree1S tree = RegionBSPTree1S.empty();
        tree.add(AngularInterval.of(-PlaneAngleRadians.PI_OVER_TWO, PlaneAngleRadians.PI_OVER_TWO, TEST_PRECISION));
        tree.add(AngularInterval.of(PlaneAngleRadians.PI - 1, PlaneAngleRadians.PI + 1, TEST_PRECISION));

        // act/assert
        Assert.assertEquals(-PlaneAngleRadians.PI_OVER_TWO,
                tree.project(Point1S.of(-PlaneAngleRadians.PI_OVER_TWO - 0.1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(-PlaneAngleRadians.PI_OVER_TWO,
                tree.project(Point1S.of(-PlaneAngleRadians.PI_OVER_TWO)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(-PlaneAngleRadians.PI_OVER_TWO,
                tree.project(Point1S.of(-PlaneAngleRadians.PI_OVER_TWO + 0.1)).getAzimuth(), TEST_EPS);

        Assert.assertEquals(-PlaneAngleRadians.PI_OVER_TWO, tree.project(Point1S.of(-0.1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(PlaneAngleRadians.PI_OVER_TWO, tree.project(Point1S.ZERO).getAzimuth(), TEST_EPS);
        Assert.assertEquals(PlaneAngleRadians.PI_OVER_TWO, tree.project(Point1S.of(0.1)).getAzimuth(), TEST_EPS);

        Assert.assertEquals(PlaneAngleRadians.PI - 1,
                tree.project(Point1S.of(PlaneAngleRadians.PI - 0.5)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(PlaneAngleRadians.PI + 1,
                tree.project(Point1S.of(PlaneAngleRadians.PI + 0.5)).getAzimuth(), TEST_EPS);
    }

    @Test
    public void testProject_equidistant() {
        // arrange
        RegionBSPTree1S tree = AngularInterval.of(1, 2, TEST_PRECISION).toTree();
        RegionBSPTree1S treeComplement = tree.copy();
        treeComplement.complement();

        // act/assert
        Assert.assertEquals(1, tree.project(Point1S.of(1.5)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(1, treeComplement.project(Point1S.of(1.5)).getAzimuth(), TEST_EPS);
    }

    @Test
    public void testProject_intervalAroundZero_closerOnMinSide() {
        // arrange
        double start = -1;
        double end = 0.5;
        RegionBSPTree1S tree = AngularInterval.of(start, end, TEST_PRECISION).toTree();

        // act/assert
        Assert.assertEquals(end, tree.project(Point1S.of(-1.5 * PlaneAngleRadians.PI)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-PlaneAngleRadians.PI)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-0.5 * PlaneAngleRadians.PI)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-0.5)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(-0.25)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(-0.1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.ZERO).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(0.1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(0.25)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(0.5)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(0.75)).getAzimuth(), TEST_EPS);
    }

    @Test
    public void testProject_intervalAroundZero_closerOnMaxSide() {
        // arrange
        double start = -0.5;
        double end = 1;
        RegionBSPTree1S tree = AngularInterval.of(start, end, TEST_PRECISION).toTree();

        // act/assert
        Assert.assertEquals(end, tree.project(Point1S.of(-1.5 * PlaneAngleRadians.PI)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(-PlaneAngleRadians.PI)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-0.5 * PlaneAngleRadians.PI)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-0.5)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-0.25)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(-0.1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.ZERO).getAzimuth(), TEST_EPS);
        Assert.assertEquals(start, tree.project(Point1S.of(0.1)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(0.25)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(0.5)).getAzimuth(), TEST_EPS);
        Assert.assertEquals(end, tree.project(Point1S.of(0.75)).getAzimuth(), TEST_EPS);
    }

    private static void checkSimpleSplit(Split<RegionBSPTree1S> split, AngularInterval minusInterval,
            AngularInterval plusInterval) {

        RegionBSPTree1S minus = split.getMinus();
        if (minusInterval != null) {
            Assert.assertNotNull("Expected minus region to not be null", minus);
            checkSingleInterval(minus, minusInterval.getMin(), minusInterval.getMax());
        } else {
            Assert.assertNull("Expected minus region to be null", minus);
        }

        RegionBSPTree1S plus = split.getPlus();
        if (plusInterval != null) {
            Assert.assertNotNull("Expected plus region to not be null", plus);
            checkSingleInterval(plus, plusInterval.getMin(), plusInterval.getMax());
        } else {
            Assert.assertNull("Expected plus region to be null", plus);
        }
    }

    private static void checkSingleInterval(RegionBSPTree1S tree, double min, double max) {
        List<AngularInterval> intervals = tree.toIntervals();

        Assert.assertEquals("Expected a single interval in the tree", 1, intervals.size());

        checkInterval(intervals.get(0), min, max);
    }

    private static void checkInterval(AngularInterval interval, double min, double max) {
        double normalizedMin = PlaneAngleRadians.normalizeBetweenZeroAndTwoPi(min);
        double normalizedMax = PlaneAngleRadians.normalizeBetweenZeroAndTwoPi(max);

        if (TEST_PRECISION.eq(normalizedMin, normalizedMax)) {
            Assert.assertTrue(interval.isFull());
        } else {
            Assert.assertEquals(normalizedMin,
                    interval.getMinBoundary().getPoint().getNormalizedAzimuth(), TEST_EPS);
            Assert.assertEquals(normalizedMax,
                    interval.getMaxBoundary().getPoint().getNormalizedAzimuth(), TEST_EPS);
        }
    }

    private static void checkClassify(Region<Point1S> region, RegionLocation loc, double... pts) {
        for (double pt : pts) {
            Assert.assertEquals("Unexpected location for point " + pt, loc, region.classify(Point1S.of(pt)));
        }
    }
}
