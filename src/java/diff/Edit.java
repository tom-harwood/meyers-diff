package diff;
public class Edit<T>
{
    public enum Type { Add, Delete, Change };

    public final Type       type;
    public final Chunk<T>   original;
    public final Chunk<T>   revised;

    Edit(Type type, Chunk<T> original, Chunk<T> revised)
    {
        this.type = type;
        this.original = original;
        this.revised = revised;
    }

    public String toString()
    {
        StringBuilder result = new StringBuilder();

        switch(type) {
        case Add: {
            result.append(String.format("%da%d\n", revised.anchor + 1, revised.anchor + revised.size()));
            revised.appendContents(result, ">");
            break;
        }
        case Delete: {
            result.append(String.format("%dd%d\n", original.anchor+1, original.anchor + original.size()));
            original.appendContents(result, "<");
            break;
        }
        case Change: {
            if (original.size() > 1) {
                result.append(String.format("%d,%dc", original.anchor+1, original.anchor + original.size()));
            } else {
                result.append(String.format("%dc", original.anchor+1));
            }

            if (revised.size() > 1) {
                result.append(String.format("%d,%d\n", revised.anchor+1, revised.anchor + revised.size()));
            } else {
                result.append(String.format("%d\n", revised.anchor+1));
            }
            original.appendContents(result, "<");
            result.append("--\n");
            revised.appendContents(result, ">");
            break;
        }
        }
        return result.toString();
    }
}
