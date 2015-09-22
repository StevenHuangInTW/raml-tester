/*
 * Copyright (C) 2014 Stefan Niederhauser (nidin@gmx.ch)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package guru.nidi.ramltester;

import jdepend.framework.DependencyDefiner;
import jdepend.framework.JDepend;
import jdepend.framework.JavaPackage;
import jdepend.framework.PackageFilter;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

import static jdepend.framework.DependencyMatchers.*;
import static org.junit.Assert.assertThat;

/**
 *
 */
public class DependencyTest {
    private static JDepend depend;

    @BeforeClass
    public static void init() throws IOException {
        depend = new JDepend(PackageFilter.all().including("guru.nidi.ramltester").excludingRest());
        depend.addDirectory("target/classes");
        depend.analyze();
    }

    @Test
    public void dependencies() {
        class GuruNidiRamltester implements DependencyDefiner {
            JavaPackage self, core, httpcomponents, restassured, junit, validator, model, servlet, spring, jaxrs, util;

            public void dependUpon() {
                self.dependsUpon(model, core, servlet, httpcomponents, restassured, spring, jaxrs, validator);
                core.dependsUpon(model, util);
                util.dependsUpon(model);
                servlet.dependsUpon(model, util, core);
                httpcomponents.dependsUpon(model, util, core);
                restassured.dependsUpon(model, core);
                junit.dependsUpon(util, core);
                validator.dependsUpon(util, core);
                spring.dependsUpon(model, util, core);
                jaxrs.dependsUpon(model, util, core);
            }
        }
        assertThat(depend, matchesPackages(new GuruNidiRamltester()));
    }

    @Test
    public void noCircularDependencies() {
        assertThat(depend, hasNoCycles());
    }

    @Test
    public void maxDistance() {
        System.out.println(distances(depend, "guru.nidi.ramltester"));
        assertThat(depend, hasMaxDistance("guru.nidi.ramltester", .9));
    }

}
