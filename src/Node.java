public class Node {
    //Node的key
    private String mKey;
    //Node对应的value值
    private String mValue;
    private boolean isBlack;
    //左子节点和右子节点，初始状态的时候都为null.
    private Node mLeftChild;
    private Node mRightChild;
    private Node mParent;

    public Node(String key, String value,Node Parent,Node mLeftChild,Node mRightChild,boolean isBlack) {
        mKey = key;
        mValue = value;
        this.mLeftChild =mLeftChild ;
        this.mRightChild = mRightChild;
        this.isBlack = isBlack;
        this.mParent = Parent;
    }

    public String getValue() {
        return mValue;
    }

    public Node getLeftChild() {
        return mLeftChild;
    }

    public Node getRightChild() {
        return mRightChild;
    }

    public boolean isBlack() {
        return isBlack;
    }

    public void setBlack(boolean black) {
        isBlack = black;
    }

    public void setValue(String newValue) {
        mValue = newValue;
    }

    public void setLeftChild(Node left) {
        mLeftChild = left;
    }

    public void setRightChild(Node right) {
        mRightChild = right;
    }

    public void setmKey(String mKey) {
        this.mKey = mKey;
    }

    public String getKey() {
        return mKey;
    }

    public Node getmParent() {
        return mParent;
    }

    public void setmParent(Node mParent) {
        this.mParent = mParent;
    }

    public static Node copyNode(Node x){
        Node y = new Node(x.getKey(),x.getValue(),x.getmParent(),x.getLeftChild(),x.getRightChild(),x.isBlack());

        return y;
    }
}
