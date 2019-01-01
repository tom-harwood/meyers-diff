import java.util.*;

public class MyersDiff<T>
{
    final List<T>   a;
    final List<T>   b;

    final int M;
    final int N;
    final int MAX;
    final int SIZE;
    final int MIDDLE;
    final Path[]V;

    public MyersDiff(List<T> a, List<T> b)
    {
        if (a == null)
            throw new IllegalArgumentException("missing first sequence");

        if (b == null)
            throw new IllegalArgumentException("missing second sequence");

        this.a = a;
        this.b = b;
        this.N = a.size();
        this.M = b.size();
        this.MAX = M+N+1;
        this.SIZE = 1 + 2 * MAX;
        this.MIDDLE = SIZE / 2;
        this.V = new Path[SIZE];
    }

    public List<Edit<T>> editScript()
    {
        List<Edit<T>> result = new ArrayList<Edit<T>>();
        Path path = buildPath().getDiff();

        while (path != null && path.lastPath != null && path.lastPath.y >= 0) {

            int x = path.x;
            int y = path.y;

            path = path.lastPath;
            assert path != null;

            Chunk<T> original = new Chunk<T>(path.x, a.subList(path.x, x));
            Chunk<T> revised = new Chunk<T>(path.y, b.subList(path.y, y));

            if (original.size() == 0 && revised.size() > 0) {
                result.add(new Edit<T>(Edit.Type.Add, original, revised));

            } else if (original.size() > 0 && revised.size() == 0) {
                result.add(new Edit<T>(Edit.Type.Delete, original, revised));

            } else if (original.size() > 0 && revised.size() > 0) {
                result.add(new Edit<T>(Edit.Type.Change, original, revised));
            }

            path = path.getDiff();
        }

        Collections.reverse(result);
        return result;
    }

    private Path buildPath()
    {
        Main.vprintf("V[1] = dummy edge\n");
        setV(1, new Path(Path.Type.Dummy));

        for (int D = 0; D <= MAX; D++) {
            for (int k = -D; k <= D; k += 2) {
                Path lastPath = null;
                int x;

                if (k == -D || (k != D && getV(k-1).x < getV(k+1).x)) {
                    Main.vprintf(
                        "D = %d, k = %d, last path is V[%d] b/c %s\n", D, k, k+1,
                        (k == -D?
                            "k == -D":
                            String.format("k != D && V[k-1].x < V[k+1].x (%d < %d)", getV(k-1).x, getV(k+1).x)
                        )
                    );
                    lastPath = getV(k+1);
                    x = lastPath.x;
                } else {
                    Main.vprintf("D = %d, k = %d, last path is V[%d]\n", D, k, k-1);
                    lastPath = getV(k-1);
                    x = lastPath.x + 1;
                }

                int y = x - k;
                Path node = new Path(x, y, lastPath, Path.Type.Diff);
                int snakeLength = 0;

                while (x < N && y < M && a.get(x).equals(b.get(y))) {
                    x++;
                    y++;
                    snakeLength++;
                }

                if (snakeLength > 0) {
                    Main.vprintf(">>> snake length %d at (%d,%d)\n", snakeLength, x, y);
                    node = new Path(x, y, node, Path.Type.Snake);
                }

                Main.vprintf("V[%d] = %s\n", k, node);
                setV(k, node);

                if (x >= N && y >= M) {
                    Main.vprintf("Result is %s\n", getV(k));
                    return getV(k);
                }
            }
        }

        throw new IllegalStateException("No SES less than MAX?");
    }

    private Path getV(int i)
    {
        return V[MIDDLE + i];
    }

    private void setV(int i, Path p)
    {
        V[MIDDLE + i] = p;
    }


    private static class Path
    {
        enum Type { Dummy, Diff, Snake };

        final int x;
        final int y;
        final Path lastPath;
        final Path.Type type;

        Path(int x, int y, Path lastPath, Path.Type type)
        {
            this.x = x;
            this.y = y;
            this.lastPath = type == Type.Diff? lastPath.findSnake(): lastPath;
            //this.lastPath = lastPath;
            this.type = type;
        }

        Path(Path.Type type)
        {
            this(0, -1, null, type);
            assert type == Type.Dummy;
        }

        Path findSnake()
        {
            if (this.type == Type.Dummy) {
                return this;

            } else if (this.type == Type.Diff && this.lastPath.type != Type.Dummy) {
                return this.lastPath.findSnake();

            } else {
                return this;
            }
        }

        Path getDiff()
        {
            Path result = (type == Type.Snake) ? lastPath: this;

            if (result == null) {
                throw new IllegalStateException("snake has no previous diff");
            }

            if (result.type == Type.Snake) {
                throw new IllegalStateException("found snake when looking for diff");
            }

            return result;
        }

        public String toString()
        {
            return String.format("{%s (%d,%d)}", type, x, y);
        }
    }
}
