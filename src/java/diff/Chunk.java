package diff;

import java.util.ArrayList;
import java.util.List;

public class Chunk<T>
{
    final int       anchor;
    final List<T>   contents;

    Chunk(int anchor, List<T> contents)
    {
        this.anchor = anchor;
        this.contents = new ArrayList<T>(contents);
    }

    int size()
    {
        return contents.size();
    }

    void appendContents(StringBuilder builder, String prefix)
    {
        for (T element: contents) {
            builder.append(prefix);
            builder.append(" ");
            builder.append(element.toString());
            builder.append("\n");
        }
    }
}
