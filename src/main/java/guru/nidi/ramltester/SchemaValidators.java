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

import guru.nidi.raml.loader.model.RamlLoader;
import guru.nidi.ramltester.core.SchemaValidator;
import guru.nidi.ramltester.validator.JavaXmlSchemaValidator;
import guru.nidi.ramltester.validator.RestassuredSchemaValidator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 *
 */
public final class SchemaValidators {
    private final List<SchemaValidator> validators;

    private SchemaValidators(List<SchemaValidator> validators) {
        this.validators = validators;
    }

    public static SchemaValidators empty() {
        return new SchemaValidators(Collections.<SchemaValidator>emptyList());
    }

    public static SchemaValidators standard() {
        return new SchemaValidators(Arrays.asList(new RestassuredSchemaValidator(), new JavaXmlSchemaValidator()));
    }

    public SchemaValidators addSchemaValidator(SchemaValidator schemaValidator) {
        final ArrayList<SchemaValidator> newValidators = new ArrayList<>(validators);
        newValidators.add(schemaValidator);
        return new SchemaValidators(newValidators);
    }

    public SchemaValidators withResourceLoader(RamlLoader resourceLoader) {
        final ArrayList<SchemaValidator> newValidators = new ArrayList<>();
        for (final SchemaValidator validator : validators) {
            newValidators.add(validator.withResourceLoader(resourceLoader));
        }
        return new SchemaValidators(newValidators);
    }

    public List<SchemaValidator> getValidators() {
        return validators;
    }
}
