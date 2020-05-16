<!---
 Licensed to the Apache Software Foundation (ASF) under one or more
 contributor license agreements.  See the NOTICE file distributed with
 this work for additional information regarding copyright ownership.
 The ASF licenses this file to You under the Apache License, Version 2.0
 (the "License"); you may not use this file except in compliance with
 the License.  You may obtain a copy of the License at

      http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
-->
<!---
 +======================================================================+
 |****                                                              ****|
 |****      THIS FILE IS GENERATED BY THE COMMONS BUILD PLUGIN      ****|
 |****                    DO NOT EDIT DIRECTLY                      ****|
 |****                                                              ****|
 +======================================================================+
 | TEMPLATE FILE: readme-md-template.md                                 |
 | commons-build-plugin/trunk/src/main/resources/commons-xdoc-templates |
 +======================================================================+
 |                                                                      |
 | 1) Re-generate using: mvn commons:readme-md                          |
 |                                                                      |
 | 2) Set the following properties in the component's pom:              |
 |    - commons.componentid (required, alphabetic, lower case)          |
 |    - commons.release.version (required)                              |
 |                                                                      |
 | 3) Example Properties                                                |
 |                                                                      |
 |  <properties>                                                        |
 |    <commons.componentid>math</commons.componentid>                   |
 |    <commons.release.version>1.2</commons.release.version>            |
 |  </properties>                                                       |
 |                                                                      |
 +======================================================================+
--->
Apache Commons Geometry - TEST
===================

[![Build Status](https://travis-ci.org/apache/commons-geometry.svg?branch=master)](https://travis-ci.org/apache/commons-geometry)
[![Coverage Status](https://coveralls.io/repos/github/apache/commons-geometry/badge.svg?branch=master)](https://coveralls.io/github/apache/commons-geometry?branch=master)
[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=commons-geometry&metric=alert_status)](https://sonarcloud.io/dashboard?id=commons-geometry)
<!---
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-core/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-core/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-enclosing/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-enclosing/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-euclidean/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-euclidean/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-hull/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-hull/)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-spherical/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.apache.commons/commons-geometry-spherical/)
--->
[![License](http://img.shields.io/:license-apache-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0.html)

The Apache Commons Geometry project provides geometric types and utilities.

Documentation
-------------

More information can be found on the [Apache Commons Geometry homepage](https://commons.apache.org/proper/commons-geometry).
The [JavaDoc](https://commons.apache.org/proper/commons-geometry/javadocs/api-release) can be browsed.
Questions related to the usage of Apache Commons Geometry should be posted to the [user mailing list][ml].

Where can I get the latest release?
-----------------------------------
You can download source and binaries from our [download page](https://commons.apache.org/proper/commons-geometry/download_geometry.cgi).

Alternatively you can pull it from the central Maven repositories:

```xml
<dependency>
  <groupId>org.apache.commons</groupId>
  <artifactId>commons-geometry</artifactId>
  <version>1.0</version>
</dependency>
```

Contributing
------------

We accept Pull Requests via GitHub. The [developer mailing list][ml] is the main channel of communication for contributors.
There are some guidelines which will make applying PRs easier for us:
+ No tabs! Please use spaces for indentation.
+ Respect the code style.
+ Create minimal diffs - disable on save actions like reformat source code or organize imports. If you feel the source code should be reformatted create a separate PR for this change.
+ Provide JUnit tests for your changes and make sure your changes don't break any existing tests by running ```mvn clean test```.

If you plan to contribute on a regular basis, please consider filing a [contributor license agreement](https://www.apache.org/licenses/#clas).
You can learn more about contributing via GitHub in our [contribution guidelines](CONTRIBUTING.md).

License
-------
This code is under the [Apache Licence v2](https://www.apache.org/licenses/LICENSE-2.0).

See the `NOTICE.txt` file for required notices and attributions.

Donations
---------
You like Apache Commons Geometry? Then [donate back to the ASF](https://www.apache.org/foundation/contributing.html) to support the development.

Additional Resources
--------------------

+ [Apache Commons Homepage](https://commons.apache.org/)
+ [Apache Issue Tracker (JIRA)](https://issues.apache.org/jira/browse/GEOMETRY)
+ [Apache Commons Twitter Account](https://twitter.com/ApacheCommons)
+ `#apache-commons` IRC channel on `irc.freenode.org`

[ml]:https://commons.apache.org/mail-lists.html
