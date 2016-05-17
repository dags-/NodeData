package me.dags.data.node;

/**
 * @author dags <dags@dags.me>
 */
public interface NodeTypeAdapter<T>
{
    Node toNode(T t);

    T fromNode(Node node);
}
