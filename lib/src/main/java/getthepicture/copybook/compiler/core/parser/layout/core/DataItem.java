package getthepicture.copybook.compiler.core.parser.layout.core;

import java.io.PrintWriter;
import java.util.List;

import getthepicture.cobol.core.AreaT;

/**
 * IBM Enterprise COBOL for z/OS:
 * <a href="https://www.ibm.com/docs/en/cobol-zos/6.5.0?topic=constants-using-data-items-group-items">
 * Using data items and group items</a>
 */
public interface DataItem {
    AreaT getArea();
    int getLevel();
    String getName();
    Integer getOccurs();   // null if not present
    String getComment();   // null if not present
    List<DataItem> getChildren();

    // ----------------------------
    // Dump
    // ----------------------------

    void dump(PrintWriter writer, int indent);

    default void dump(PrintWriter writer) {
        dump(writer, 0);
    }
}
