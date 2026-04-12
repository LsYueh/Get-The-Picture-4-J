package getthepicture.picture.codec.semantic;

import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;
import getthepicture.picture.core.clause.items.PicClauseSemantic;
import getthepicture.picture.core.clause.items.PicClauseUsage;

// TODO: 看看 Date/Time/Timestamp 要不要支援 COMP-3
public class Rules {

    private static final Map<PicClauseSemantic, Constraint> RULES;

    static {
        Map<PicClauseSemantic, Constraint> map = new EnumMap<>(PicClauseSemantic.class);

        map.put(PicClauseSemantic.GREGORIAN_DATE, new Constraint(
            null,
            new PicClauseUsage[]{ PicClauseUsage.DISPLAY },
            8, 0, false
        ));
        map.put(PicClauseSemantic.MINGUO_DATE, new Constraint(
            null,
            new PicClauseUsage[]{ PicClauseUsage.DISPLAY },
            7, 0, false
        ));
        map.put(PicClauseSemantic.TIME6, new Constraint(
            new PicClauseBaseClass[]{ PicClauseBaseClass.NUMERIC, PicClauseBaseClass.ALPHANUMERIC },
            new PicClauseUsage[]{ PicClauseUsage.DISPLAY },
            6, 0, false
        ));
        map.put(PicClauseSemantic.TIME9, new Constraint(
            new PicClauseBaseClass[]{ PicClauseBaseClass.NUMERIC, PicClauseBaseClass.ALPHANUMERIC },
            new PicClauseUsage[]{ PicClauseUsage.DISPLAY },
            9, 0, false
        ));
        map.put(PicClauseSemantic.TIMESTAMP14, new Constraint(
            new PicClauseBaseClass[]{ PicClauseBaseClass.NUMERIC, PicClauseBaseClass.ALPHANUMERIC },
            new PicClauseUsage[]{ PicClauseUsage.DISPLAY },
            14, 0, false
        ));
        map.put(PicClauseSemantic.BOOLEAN, new Constraint(
            null,
            new PicClauseUsage[]{ PicClauseUsage.DISPLAY },
            1, 0, false
        ));

        RULES = Collections.unmodifiableMap(map);
    }

    public static Constraint getConstraint(PicClauseSemantic semantic) {
        Constraint rule = RULES.get(semantic);
        if (rule == null)
            throw new IllegalStateException("No rule defined for " + semantic);
        return rule;
    }
}
