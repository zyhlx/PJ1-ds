public class RBTree {
    //根节点.
    private Node mRoot;
    //还应该有个来记录数量的值,每插入一个就应该数据加1.
    private int mCount;



    public int getSize() {
        return mCount;
    }

    //判空
    public boolean isEmpty() {
        return mCount == 0;
    }

    public Node search(Node x,String key) {//查找指定节点
        if (x.getRightChild()!=null&&x.getLeftChild()!=null){
            if (x.getLeftChild().getKey()!=null&&x.getRightChild().getKey()!=null){
                if (x.getRightChild().getKey().compareTo(x.getKey())<0){
                    System.out.println(x.getKey()+""+x.getLeftChild().getKey()+x.getRightChild().getKey());
                }
            }
        }

        if (x.getKey()==null || key.equals(x.getKey()) ) {
            return x;
        }

        if (key.compareTo(x.getKey()) < 0) {
            return search(x.getLeftChild(), key);
        } else {
            return search(x.getRightChild(), key);
        }
    }


    public  String SearchFromTo(String keyStart,String keyEnd){
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer = inorderFromToSearch(getmRoot(),keyStart,keyEnd,stringBuffer);
        return stringBuffer.toString();
    }



    public static StringBuffer inorderFromToSearch(Node x,String keyStart,String keyEnd,StringBuffer stringBuffer){
        if (x.getLeftChild().getKey()!=null&&x.getKey().compareTo(keyStart)>0){
            inorderFromToSearch(x.getLeftChild(),keyStart,keyEnd,stringBuffer);
        }
        if (x.getValue()!=null&&x.getKey().compareTo(keyStart)>=0&&x.getKey().compareTo(keyEnd)<=0){
            stringBuffer.append(x.getKey()+" "+x.getValue()+"\n");
        }
        if (x.getRightChild().getKey()!=null&&x.getKey().compareTo(keyEnd)<=0){
            inorderFromToSearch(x.getRightChild(),keyStart,keyEnd,stringBuffer);
        }

        return stringBuffer;
    }


    public static void RSInsert(RBTree tree,Node z){
        if (tree.search(tree.getmRoot(),z.getKey()).getKey()!=null){

        }else {
            Node y =  new Node(null,null,null,null,null,true);
            Node x = tree.getmRoot();
            while(x.getKey()!=null){
                y=x;
                if(z.getKey().compareTo(x.getKey())<0){
                    x=x.getLeftChild();
                }else {
                    x=x.getRightChild();
                }
            }
            z.setmParent(y);
            if(y.getKey()==null){
                tree.setmRoot(z);
            }else if (z.getKey().compareTo(y.getKey())<0){
                y.setLeftChild(z);
            }else {
                y.setRightChild(z);
            }
            z.setLeftChild(new Node(null,null,z,null,null,true));
            z.setRightChild(new Node(null,null,z,null,null,true));
            z.setBlack(false);
            RBInsertFixUp(tree,z);
        }

    }

    public static void RBInsertFixUp(RBTree tree,Node z){
        while(!z.getmParent().isBlack()){//父亲也是红的
            if(z.getmParent()==z.getmParent().getmParent().getLeftChild()){//如果z的父亲是祖父的左节点
                Node y = z.getmParent().getmParent().getRightChild();//uncle是右节点
                if (!y.isBlack()){//uncle是红的
                    z.getmParent().setBlack(true);//uncle 父亲设为黑
                    y.setBlack(true);
                    z.getmParent().getmParent().setBlack(false);//祖父涂红
                    z=z.getmParent().getmParent();//z变祖父循环判断祖父变红有没有影响
                }else {
                    if (z==z.getmParent().getRightChild()){//uncle是黑的之后看左右节点 是右节点的话
                        z=z.getmParent();
                        leftRotation(tree,z);//旋转成左节点 输进去的是父节点
                    }
                    z.getmParent().setBlack(true);
                    z.getmParent().getmParent().setBlack(false);
                    rightRotation(tree, z.getmParent().getmParent());
                }

            }else {
                Node y = z.getmParent().getmParent().getLeftChild();//uncle是左节点
                if (!y.isBlack()){//uncle是红的
                    z.getmParent().setBlack(true);//uncle 父亲设为黑
                    y.setBlack(true);
                    z.getmParent().getmParent().setBlack(false);//祖父涂红
                    z=z.getmParent().getmParent();//z变祖父循环判断祖父变红有没有影响
                }else {
                    if (z==z.getmParent().getLeftChild()){//uncle是黑的之后看左右节点 是右节点的话
                        z=z.getmParent();
                        rightRotation(tree,z);//旋转成左节点 输进去的是父节点
                    }
                    z.getmParent().setBlack(true);
                    z.getmParent().getmParent().setBlack(false);
                    leftRotation(tree, z.getmParent().getmParent());
                }
            }
        }
        tree.getmRoot().setBlack(true);
    }


    public static Node RBDelete(RBTree tree,Node z){//没有判断Z是否存在树上
        Node y ,x;
        if(z.getLeftChild().getKey()==null||z.getRightChild().getKey()==null){
            y=z;//是不是单儿子
        }else {
            if (!tree.predecessor(z).isBlack()){
                y=tree.predecessor(z);
            }else {
                y=tree.successor(z);//不是找后继
            }

        }
        if (y.getLeftChild().getKey()!=null){//y是左儿子吗
            x = y.getLeftChild();
        }else {
            x = y.getRightChild();
        }

        x.setmParent(y.getmParent());//将y的儿子接上y的父节点 如果y是后继这边做的是在处理后继的身后事对y本身的删除其实只要改个value
        if (y.getmParent().getKey()==null){//让y的父节点接上新的儿子
            tree.setmRoot(x);
        }else if (y==y.getmParent().getLeftChild()){
            y.getmParent().setLeftChild(x);
        }else {
            y.getmParent().setRightChild(x);
        }
        if(y!=z){//后继删除特殊要求
            z.setmKey(y.getKey());
            z.setValue(y.getValue());
        }
        if (y.isBlack()){
            RBDeleteFixUp(tree, x,x.getmParent());//x是被删除元素的儿子进去的
        }


        return y;
    }
    public static void RBDeleteFixUp(RBTree tree,Node x,Node par){
        Node w;
        while(x!=tree.getmRoot()&&x.isBlack()){
            if(x==par.getLeftChild()){//如果x是左节点

                w=par.getRightChild();//兄弟是右节点
                if(!w.isBlack()){//看看兄弟是不是黑的 不是
                    w.setBlack(true);//兄弟变成黑
                    par.setBlack(false);//父节点原黑变红
                    leftRotation(tree,par);//旋转 注旋转没有上色 我父亲改变了
                    w=par.getRightChild();//我有了新的黑的兄弟
                }
                if(w.getLeftChild().isBlack()&&w.getRightChild().isBlack()){//开始判断黑的兄弟的左右儿子了 情况一是都是黑 上交黑色
                    w.setBlack(false);
                    x=par;//上交黑色 循环了
                }
                else{//左右有一个红的
                    if(w.getRightChild().isBlack()){//右边是黑的吗
                        w.getLeftChild().setBlack(true);//是那就是左边是红的 把左边的变黑
                        w.setBlack(false);//自己变右边的红
                        rightRotation(tree,w);//旋转
                        w=par.getRightChild();//现在新的兄弟是原来红的现在黑色的左侄子还有一个变成红色的右侄子了
                    }
                    w.setBlack(par.isBlack());//变颜色
                    par.setBlack(true);
                    w.getRightChild().setBlack(true);
                    leftRotation(tree,par);
                    x=tree.getmRoot();
                }
            }else {
                w=par.getLeftChild();//兄弟是左节点
                if(!w.isBlack()){//看看兄弟是不是黑的 不是
                    w.setBlack(true);//兄弟变成黑
                    par.setBlack(false);//父节点原黑变红
                    rightRotation(tree,x.getmParent());//旋转 注旋转没有上色 我父亲改变了
                    w=par.getLeftChild();//我有了新的黑的兄弟
                }
                if(w.getLeftChild().isBlack()&&w.getRightChild().isBlack()){//开始判断黑的兄弟的左右儿子了 情况一是都是黑 上交黑色
                    w.setBlack(false);
                    x=par;//上交黑色 循环了
                }
                else{//左右有一个红的
                    if(w.getLeftChild().isBlack()){//左边是黑的吗
                        w.getRightChild().setBlack(true);//是那就是右边是红的 把右边的变黑
                        w.setBlack(false);//自己变右边的红
                        leftRotation(tree,w);//旋转
                        w=par.getLeftChild();//现在新的兄弟是原来红的现在黑色的右侄子还有一个变成红色的左侄子了
                    }
                    w.setBlack(par.isBlack());//变颜色
                    par.setBlack(true);
                    w.getLeftChild().setBlack(true);
                    rightRotation(tree,par);
                    x=tree.getmRoot();
                }
            }
        }
        x.setBlack(true);
    }

    public static void leftRotation(RBTree tree,Node x){
        Node y = x.getRightChild();//取x的右节点
        x.setRightChild(y.getLeftChild());//x的右节点变成原来右节点的左子节点
        y.getLeftChild().setmParent(x);//把y的左子节点的父节点变成x
        y.setmParent(x.getmParent());//y的父节点变成x的父节点
        if(x.getmParent().getKey()==null){//是否是根节点
            tree.setmRoot(y);
        }else if (x==x.getmParent().getLeftChild()){//原左子节点
            x.getmParent().setLeftChild(y);
        }else {
            x.getmParent().setRightChild(y);
        }
        y.setLeftChild(x);
        x.setmParent(y);

    }
    public static void rightRotation(RBTree tree,Node x){
        Node y = x.getLeftChild();//取x的左节点
        x.setLeftChild(y.getRightChild());//x的左节点变成原来左节点的右子节点
        y.getRightChild().setmParent(x);//把y的右子节点的父节点变成x
        y.setmParent(x.getmParent());//y的父节点变成x的父节点
        if(x.getmParent().getKey()==null){//是否是根节点
            tree.setmRoot(y);
        }else if (x==x.getmParent().getLeftChild()){//x原来是原左子节点
            x.getmParent().setLeftChild(y);//现在y也是左节点
        }else {
            x.getmParent().setRightChild(y);//反之右节点
        }
        y.setRightChild(x);
        x.setmParent(y);
    }



    public String preOrder(Node rootNode,int level,int child,StringBuffer buffer) {//先序遍历树
        if(rootNode.getKey()!=null){
            buffer.append("level="+level+";child="+child+"; "+rootNode.getKey()+":"+rootNode.getValue()+"\t"+rootNode.isBlack()+"\n");
            level++;
            preOrder(rootNode.getLeftChild(),level,0,buffer);
            preOrder(rootNode.getRightChild(),level,1,buffer);
        }
//        else {
//
//            buffer.append("level="+level+";child="+child+"; "+rootNode.getKey()+"\t"+"\n");
//        }
        return buffer.toString();
    }

    public Node  successor(Node rootNode){
        if(rootNode.getRightChild().getKey()!=null){
            return maximum(rootNode.getRightChild());
        }
        Node successor = rootNode.getmParent();
        while (successor.getKey()!=null&&rootNode==successor.getRightChild()){
            rootNode=successor;
            successor=rootNode.getmParent();
        }
        return successor;
    }


    public Node predecessor(Node rootNode){
        if(rootNode.getLeftChild().getKey()!=null){
            return minimum(rootNode.getLeftChild());
        }
        Node successor = rootNode.getmParent();
        while (successor.getKey()!=null&&rootNode==successor.getLeftChild()){
            rootNode=successor;
            successor=rootNode.getmParent();
        }
        return successor;
    }

    public Node minimum(Node x){
        while (x.getLeftChild().getKey()!=null){
            x=x.getLeftChild();
        }
        return x;
    }

    public Node maximum(Node x){
        while (x.getRightChild().getKey()!=null){
            x=x.getRightChild();
        }
        return x;
    }



    public Node getmRoot() {
        return mRoot;
    }

    public void setmRoot(Node mRoot) {
        this.mRoot = mRoot;
        mRoot.setBlack(true);
    }



//    public void preorderPrint(Node rootNode){
//        if(!rootNode.getKey().equals("null")){
//            System.out.print(rootNode.getKey()+"\t");
//            preOrder(rootNode.getLeftChild());
//            preOrder(rootNode.getRightChild());
//        }else {
//            System.out.print(rootNode.getKey()+"\t");
//        }
//
//    }

}
