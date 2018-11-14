import java.util.List;
import java.util.Map;

public class BplusTree {


    /** 根节点 */
    private BNode mRoot;

    //还应该有个来记录数量的值,每插入一个就应该数据加1.
    private int mCount;

    /** 阶数，M值 */
    protected int order=5;

    /** 叶子节点的链表头*/
    protected BNode head;

    public BNode getHead() {
        return head;
    }

    public void setHead(BNode head) {
        this.head = head;
    }

    public BNode getmRoot() {
        return mRoot;
    }


    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }


    public void setmRoot(BNode mRoot) {
        this.mRoot = mRoot;
    }


    public void setmCount(int mCount) {
        this.mCount = mCount;
    }

    public int getmCount() {
        return mCount;
    }

    public void bTreeCreate(){

    }

    public void insertOrUpdate(String key,String value) {
        if (getmRoot()==null){
            BNode node = new BNode(null,true,true,key);
            node.insertOrUpdate(key,value,this);
        }else {
            getmRoot().insertOrUpdate(key, value,this);//更新从root节点开始因为更新先要search
        }

    }

    public String bTreeSearch(String key){
        BNode root = getmRoot();
//            head=head.;
        return root.search(key);
    }


    public void bTreeDelete(String key){
        if (getmRoot()!=null){
            getmRoot().remove(key,this);
        }

    }


    public String bTreeSearchFromT0(String keyStart,String keyEnd){
        StringBuffer s=new StringBuffer();
        BNode head = getHead();
        while (head!=null&&head.getLeafEntries().get(head.getLeafEntries().size()-1).getKey().compareTo(keyStart)<0){
                head=head.getNext();
        }
        while (head!=null&&head.getLeafEntries().get(0).getKey().compareTo(keyEnd)<=0){
            for (Map.Entry<String,String> entry:head.getLeafEntries()){
                if (entry.getKey().compareTo(keyStart)>=0&&entry.getKey().compareTo(keyEnd)<=0){
                        s.append(entry.getKey()+" "+entry.getValue()+"\n");

                }
            }
            head=head.getNext();
        }


//        while (head!=null){
//            for (Map.Entry<String,String> entry:head.getLeafEntries()){
//
//                    s.append(entry.getKey()+" "+entry.getValue()+"\n");
//
//            }
//            head=head.getNext();
//        }
        return s.toString();



    }


}
