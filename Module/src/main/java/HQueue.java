public interface HQueue {
    /**
     * returns a fresh (empty) queue
     */
    HuffmanTree newQueue();

    /**
     * return true if the queue is empty
     */
    boolean isEmpty();

    /**
     * return a count of the number of elements in the queue
     */
    int count();

    /**
     * enqueue a true
     */
    void enqueue(HuffmanTree tree);

    /**
     * dequeue and return the highest-priority tree
     */
    HuffmanTree dequeue();

}
