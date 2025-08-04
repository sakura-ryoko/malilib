package fi.dy.masa.malilib.gui;

public enum LeftRight
{
    LEFT {
        @Override public int choose(int left, int right) {return left;}
    },
    RIGHT {
        @Override public int choose(int left, int right) {return right;}
    },
    CENTER {
        @Override public int choose(int left, int right) {return (left + right) >> 1;}
    };
    public abstract int choose(int left, int right);
    public int getLeft(int left, int right, int width, int reserved){
        return choose(left + reserved, right - reserved - width);
    }
}
