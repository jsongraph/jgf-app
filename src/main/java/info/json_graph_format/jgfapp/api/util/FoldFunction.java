package info.json_graph_format.jgfapp.api.util;

public interface FoldFunction<T, U> {

    public U initial();
    public U fold(T item, U accumulated);
}
