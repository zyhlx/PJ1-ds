import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class Another{
//    public static void main(String[] args){
//        Node nullNode = new Node("null","null",null,null,null,true);
//        Node x = new Node("1","1",nullNode, new Node("null","null",null,null,null,true), new Node("null","null",null,null,null,true),true);
//        Node y = new Node("2","2",x, new Node("null","null",null,null,null,true), new Node("null","null",null,null,null,true),true);
//        x.getRightChild().setmParent(x);
//        x.getLeftChild().setmParent(x);
//        y.getLeftChild().setmParent(y);
//        y.getRightChild().setmParent(y);
//        x.setRightChild(y);
//        Node z = x.getRightChild();
//        Node zz;
//        if (!y.getLeftChild().getKey().equals("null")){//y是左儿子吗
//            zz = y.getLeftChild();
//        }else {
//            zz = y.getRightChild();
//        }
////       System.out.print(z.getValue());
////        System.out.print(zz.getmParent().getValue()+zz.getKey());
//        RBTree tree = new RBTree();
//        tree.setmRoot(x);
//        tree.preOrder(x);
//    }



    public static void main(String[] args) throws IOException {
        BplusTree tree = new BplusTree();
//        tree.insertOrUpdate("love","爱");
//        tree.insertOrUpdate("cry","哭");
//        tree.insertOrUpdate("sing","唱");
//        tree.insertOrUpdate("laugh","笑");
//        tree.insertOrUpdate("like","喜欢");
//        tree.insertOrUpdate("this","这个");
//        tree.insertOrUpdate("what","什么");
//        tree.insertOrUpdate("log","登陆");
//        tree.insertOrUpdate("think","想");
//        tree.insertOrUpdate("link","链接");
//        tree.insertOrUpdate("left","左边");
//        tree.insertOrUpdate("right","右边");
//        tree.insertOrUpdate("up","上面");
//        tree.insertOrUpdate("down","下面");
//        tree.insertOrUpdate("happy","开心");
//        tree.insertOrUpdate("sad","难过");
//        tree.insertOrUpdate("have","有");
//        tree.insertOrUpdate("let","让");
//        System.out.print(tree.bTreeSearch("cry"));
//        RBTree tree = new RBTree();
//        Node nullNode = new Node("null","null",null,null,null,true);
//        tree.setmRoot(nullNode);

        int time = 0;
        FileInputStream fis = new FileInputStream("D:/大二/数据结构与算法导论/数据结构project1/sample files/1_initial.txt");

        InputStreamReader reader = new InputStreamReader(fis,"GBK");

        BufferedReader br = new BufferedReader(reader);

        String s = br.readLine();//insert

        int time1 = 0;

        long startTime = 0;

        long endTime = 0;

        //   System.out.println("用1_initial.txt插入每100个数据所用时间(ns):");

        while ((s = br.readLine()) != null){//
            String s2 = br.readLine();
                tree.insertOrUpdate(s,s2);

//                Node z = new Node(s,s2,nullNode, new Node("null","null",null,null,null,true), new Node("null","null",null,null,null,true),false);
//
//                tree.RSInsert(tree,z);
            time++;
            if(time == 100){
               System.out.print(tree.getmRoot().getEntries().get(0)+tree.getmRoot().getEntries().get(1));
         //       tree.preOrder(tree.getmRoot(),0,0);
             //  System.out.println(tree.getmRoot().getValue()+tree.getmRoot().isBlack());
              //  break;
            //    time = 0;
          }
        }
        br.close();
  //      System.out.println(tree.getmRoot().getValue()+tree.getmRoot().isBlack());
        System.out.println(time);

//        FileInputStream fis2 = new FileInputStream("D:/大二/数据结构与算法导论/数据结构project1/sample files/3_insert.txt");
//
//        InputStreamReader reader2 = new InputStreamReader(fis2,"GBK");
//
//        BufferedReader br2 = new BufferedReader(reader2);
//
//        String s4 = br2.readLine();//insert
//
//
//        while ((s4 = br2.readLine()) != null){//
//
//
//
//            String s5 = br2.readLine();
//
//           // tree.insertOrUpdate(s4,s5);
//
//          //  Node nullNode = new Node("null","null",null,null,null,true);
//            Node z = new Node(s4,s5,nullNode, new Node("null","null",null,null,null,true), new Node("null","null",null,null,null,true),false);
//
//            tree.RSInsert(tree,z);
//
//            time++;
//
//
//            if(time == 100 ){
//
//                System.out.print(tree.getmRoot().getValue());
//
//                //    System.out.print(tree.getmRoot().getEntries().get(0)+tree.getmRoot().getEntries().get(1));
//              //  break;
//
//            }
//
//        }
//        br2.close();



//        FileInputStream fis3 = new FileInputStream("D:/大二/数据结构与算法导论/数据结构project1/sample files/2_delete.txt");
//
//        InputStreamReader reader3 = new InputStreamReader(fis3,"GBK");
//
//        BufferedReader br3 = new BufferedReader(reader3);
//
//        String s7 = br3.readLine();//insert
//
//        //   System.out.println("用1_initial.txt插入每100个数据所用时间(ns):");
//
//        while ((s7 = br3.readLine()) != null){//
//            String s8 = br3.readLine();
//            // tree.insertOrUpdate(s,s2);
//
//            Node z = new Node(s7,s8,nullNode, new Node("null","null",null,null,null,true), new Node("null","null",null,null,null,true),false);
//
//            tree.RSInsert(tree,z);
//
//
//            time++;
//
//       //     if(time == 100 ){
//                // System.out.print(tree.getmRoot().getEntries().get(0)+tree.getmRoot().getEntries().get(1));
//
//        //        System.out.println(tree.getmRoot().getValue()+tree.getmRoot().isBlack());
//
//             //   break;
//        //         time = 0;
//
//      //      }
//
//        }
//
//
//        br3.close();
//        System.out.println(tree.getmRoot().getValue()+tree.getmRoot().isBlack());
//
//







    }
}