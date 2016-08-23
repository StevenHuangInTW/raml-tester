package guru.nidi.ramltester.model;

import org.raml.v2.api.model.v10.security.SecurityScheme;

import java.util.ArrayList;
import java.util.List;

/**
 *
 */
public class SecScheme10 implements UnifiedSecScheme {
    private SecurityScheme scheme;

    public SecScheme10(SecurityScheme scheme) {
        this.scheme = scheme;
    }

    static List<UnifiedSecScheme> of(List<SecurityScheme> schemes) {
        final List<UnifiedSecScheme> res = new ArrayList<>();
        for (final SecurityScheme s : schemes) {
            res.add(new SecScheme10(s));
        }
        return res;
    }

    @Override
    public String name() {
        return scheme.name();
    }

    @Override
    public String type() {
        return scheme.type();
    }

    @Override
    public String description() {
        return scheme.description().value();
    }

    @Override
    public UnifiedSecSchemePart describedBy() {
        return new SecSchemePart10(scheme.describedBy());
    }

    @Override
    public UnifiedSecSchemeSettings settings() {
        return new SecSchemeSettings10(scheme.settings());
    }
}
