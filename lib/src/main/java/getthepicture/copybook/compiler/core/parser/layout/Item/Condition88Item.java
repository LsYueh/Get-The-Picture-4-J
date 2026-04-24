package getthepicture.copybook.compiler.core.parser.layout.Item;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import getthepicture.cobol.core.AreaT;
import getthepicture.copybook.compiler.core.parser.layout.core.AbstractDataItem;

public final class Condition88Item extends AbstractDataItem {
    /**
     * 88 VALUE 列表，可以是一個或多個
     */
    private final List<Object> values;
    /**
     * 88 VALUE ... THROUGH 的結尾值
     */
    private final Object throughValue;

    public Condition88Item(AreaT area, String name) {
        this(area, name, null, null);
    }

    public Condition88Item(AreaT area, String name, List<Object> values, Object throughValue) {
        super(area, 88, name);
        this.values       = (values != null) ? new ArrayList<>(values) : new ArrayList<>();
        this.throughValue = throughValue;
    }

    public List<Object> getValues() {
        return Collections.unmodifiableList(values);
    }

    public Object getThroughValue() {
        return throughValue;
    }

    // ----------------------------
    // Dump
    // ----------------------------
    @Override
    public void dump(PrintWriter writer, int indent) {
        if (values.isEmpty() && throughValue == null) {
            writer.println(indent(indent) + "88 " + getName());
        } else {
            String valuePart = values.stream()
                    .map(v -> v != null ? v.toString() : "NULL")
                    .collect(Collectors.joining(" "));
            if (throughValue != null)
                valuePart += " through " + throughValue;
            writer.println(indent(indent) + "88 " + getName() + " >> Value(s) in " + valuePart);
        }
    }
}
