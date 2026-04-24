package getthepicture.copybook.compiler.core.parser.layout.Item;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.parser.layout.core.AbstractDataItem;
import getthepicture.copybook.compiler.core.parser.layout.core.DataItem;
import getthepicture.picture.core.meta.PictureMeta;

public final class ElementaryDataItem extends AbstractDataItem {
    private final PictureMeta pic;
    private final boolean isFiller;
    private final String value;

    private final List<Condition88Item> conditions = new ArrayList<>();

    public ElementaryDataItem(AreaT area, int level, String name, PictureMeta pic) {
        this(area, level, name, pic, null, null, false, null);
    }

    public ElementaryDataItem(AreaT area, int level, String name, PictureMeta pic,
                               Integer occurs, String value, boolean isFiller, String comment) {
        super(area, level, name, occurs, comment);
        this.pic      = Objects.requireNonNull(pic, "pic must not be null");
        this.isFiller = isFiller;
        this.value    = value;
    }

    public PictureMeta  getPic()      { return pic;      }
    public boolean      isFiller()    { return isFiller; }
    public String       getValue()    { return value;    }

    // ----------------------------
    // Level 88 Condition-name
    // ----------------------------
    public List<Condition88Item> getConditions() {
        return Collections.unmodifiableList(conditions);
    }

    @Override
    public List<DataItem> getChildren() {
        return Collections.unmodifiableList(conditions);
    }

    public void addCondition(Condition88Item condition) {
        conditions.add(condition);
    }

    // ----------------------------
    // Dump
    // ----------------------------
    @Override
    public void dump(PrintWriter w, int indent) {
        w.printf("%s%02d %s%s >>", indent(indent), getLevel(), getName(), formatComment());
        if (pic != null)
            w.printf(" PIC: %s", pic);
        if (getOccurs() != null && getOccurs() > 1)
            w.printf(" OCCURS: %d", getOccurs());
        if (value != null)
            w.printf(" VALUE: \"%s\"", value);
        w.println();
        for (Condition88Item c : conditions)
            c.dump(w, indent + 1);
    }
}
