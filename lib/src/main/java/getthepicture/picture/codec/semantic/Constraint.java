package getthepicture.picture.codec.semantic;

import java.util.Arrays;

import getthepicture.picture.core.clause.items.PicClauseBaseClass;
import getthepicture.picture.core.clause.items.PicClauseUsage;
import getthepicture.picture.core.meta.PictureMeta;

/**
 * 語意約束模型
 */
public final class Constraint {

    private final PicClauseBaseClass[] allowedClasses;
    private final PicClauseUsage[]     allowedUsage;
    private final Integer              requiredLength;
    private final Integer              requiredDecimalDigits;
    private final Boolean              mustBeSigned;

    public Constraint(
        PicClauseBaseClass[] allowedClasses,
        PicClauseUsage[]     allowedUsage,
        Integer              requiredLength,
        Integer              requiredDecimalDigits,
        Boolean              mustBeSigned
    ) {
        this.allowedClasses        = allowedClasses != null ? allowedClasses : new PicClauseBaseClass[0];
        this.allowedUsage          = allowedUsage   != null ? allowedUsage   : new PicClauseUsage[0];
        this.requiredLength        = requiredLength;
        this.requiredDecimalDigits = requiredDecimalDigits;
        this.mustBeSigned          = mustBeSigned;
    }

    // -------------------------------------------------------------------------
    // Getters
    // -------------------------------------------------------------------------

    public PicClauseBaseClass[] getAllowedClasses()        { return allowedClasses; }
    public PicClauseUsage[]     getAllowedUsage()          { return allowedUsage; }
    public Integer              getRequiredLength()        { return requiredLength; }
    public Integer              getRequiredDecimalDigits() { return requiredDecimalDigits; }
    public Boolean              getMustBeSigned()          { return mustBeSigned; }

    // -------------------------------------------------------------------------
    // 語意「合法結構」
    // -------------------------------------------------------------------------

    public boolean isStructureValid(PictureMeta pic) {
        if (allowedClasses.length > 0 && !contains(allowedClasses, pic.getBaseClass()))
            return false;
        if (allowedUsage.length > 0 && !contains(allowedUsage, pic.getUsage()))
            return false;
        if (requiredLength != null && pic.getStorageOccupied() != requiredLength)
            return false;
        if (requiredDecimalDigits != null && pic.getDecimalDigits() != requiredDecimalDigits)
            return false;
        if (mustBeSigned != null && pic.isSigned() != mustBeSigned)
            return false;
        return true;
    }

    public void validateOrThrow(PictureMeta pic) {
        String semanticName = pic.getSemantic().toString();

        if (allowedClasses.length > 0 && !contains(allowedClasses, pic.getBaseClass()))
            throw new UnsupportedOperationException(
                semanticName + " does not support BaseClass '" + pic.getBaseClass() + "'.");
        if (allowedUsage.length > 0 && !contains(allowedUsage, pic.getUsage()))
            throw new UnsupportedOperationException(
                semanticName + " does not support usage '" + pic.getUsage() + "'.");
        if (requiredLength != null && pic.getStorageOccupied() != requiredLength)
            throw new UnsupportedOperationException(
                semanticName + " must occupy exactly " + requiredLength + " bytes. Actual: " + pic.getStorageOccupied());
        if (requiredDecimalDigits != null && pic.getDecimalDigits() != requiredDecimalDigits)
            throw new UnsupportedOperationException(
                semanticName + " requires DecimalDigits = " + requiredDecimalDigits + ". Actual: " + pic.getDecimalDigits());
        if (mustBeSigned != null && pic.isSigned() != mustBeSigned)
            throw new UnsupportedOperationException(
                semanticName + " requires Signed = " + mustBeSigned + ".");
    }

    // -------------------------------------------------------------------------
    // Helpers
    // -------------------------------------------------------------------------

    private static <T> boolean contains(T[] array, T target) {
        return Arrays.asList(array).contains(target);
    }
}
