package absyn;

public class NameTy extends Absyn {
    public int type;

    final public static int INT = 0;
    final public static int VOID = 1;

    final public static String types[] = { "INT", "VOID" };

    public NameTy(int row, int col, int type) {
        this.row = row;
        this.col = col;
        this.type = type;
    }

    public void accept(AbsynVisitor visitor, int level) {
        visitor.visit(this, level);
    }
}
