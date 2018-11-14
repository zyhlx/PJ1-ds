import com.sun.tools.attach.VirtualMachine;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;

import java.io.*;

public class UI extends Application {
    boolean isRedTree = true;
    static RBTree tree = new RBTree();
    static BplusTree Btree = new BplusTree();
    String[] ops = {"INSERT", "DELETE"};

    public void start(Stage primaryStage) {
        try {

            TextArea textShow = new TextArea();//展示


            Node nullNode = new Node(null, null, null, null, null, true);
            tree.setmRoot(nullNode);

            FileInputStream file = new FileInputStream("D:/大二/数据结构与算法导论/数据结构project1/sample files/1_initial.txt");
            InputStreamReader reader = new InputStreamReader(file, "GBK");
            BufferedReader buffer1 = new BufferedReader(reader);
            String s = buffer1.readLine();
            int time = 0;
            while ((s = buffer1.readLine()) != null) {
                s = s.toLowerCase();
                String s1 = buffer1.readLine();
                Node z = new Node(s, s1, nullNode, nullNode, nullNode, false);
                if (tree.getmRoot().getKey() == null) {
                    tree.setmRoot(z);
                    z.setBlack(true);
                } else {
                    tree.RSInsert(tree, z);
                }

                //              time++;


            }
           // StringBuffer buffer = new StringBuffer();
          //  System.out.print(tree.preOrder(tree.getmRoot(), 0, 0, buffer));

            buffer1.close();


            FileInputStream file1 = new FileInputStream("D:/大二/数据结构与算法导论/数据结构project1/sample files/1_initial.txt");
            InputStreamReader reader2 = new InputStreamReader(file1, "GBK");
            BufferedReader buffer2 = new BufferedReader(reader2);
            String s2 = buffer2.readLine();
            while ((s2 = buffer2.readLine()) != null) {
                s2 = s2.toLowerCase();
                String s3 = buffer2.readLine();
                Btree.insertOrUpdate(s2, s3);
                time++;
                if (time == 100 && time <= 500) {
//                    System.out.println(tree.getmRoot().getKey());
                    time = 0;
                }
            }
            buffer2.close();

            //最底下一层
            FlowPane background = new FlowPane();
            background.setPadding(new Insets(10, 10, 10, 10));
            background.setHgap(5);

            //左边右边两块
            VBox leftVbox = new VBox(10);
            VBox rightVbox = new VBox(10);
            leftVbox.setPadding(new Insets(20, 10, 20, 10));
            rightVbox.setPadding(new Insets(20, 10, 20, 10));
            //左边开始布局
            //左上
            //标题
            Label labelLeft = new Label("MANAGEMENT");
//            labelLeft.setStyle("-fx-background-color: #f3f3f3;-fx-font-weight: bold");
            leftVbox.getChildren().add(labelLeft);
            leftVbox.setMargin(labelLeft, new Insets(-30, 0, 0, 10));

            //边框
            leftVbox.setStyle("-fx-border-color: #c1e4e9;");
            //     rightVbox.setStyle("-fx-border-color: #c1e4e9;");

            //browser submit
            GridPane leftGridePane = new GridPane();
            leftGridePane.setStyle("-fx-border-color: #c0c6c9;");
            leftGridePane.setAlignment(Pos.CENTER);
            leftGridePane.setHgap(10);
            leftGridePane.setVgap(20);
            leftGridePane.setPadding(new Insets(25, 25, 25, 25));


            TextField textBrowser = new TextField();

            Button buttonBrowser = new Button("Browser");
            buttonBrowser.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent arg0) {
                    FileChooser fileChooser = new FileChooser();
                    fileChooser.setTitle("Browser");

                    fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));

                    fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("txt", "*.txt"));

                    File file = fileChooser.showOpenDialog(primaryStage);
                    if (file != null) {
                        textBrowser.setText(file.getPath());
                    }

                }

            });

            Button buttonSubmit = new Button("Submit");


            buttonSubmit.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent arg0) {
                    if (textBrowser.getText() != null) {
                        try {
                            FileInputStream file = new FileInputStream(textBrowser.getText());
                            InputStreamReader reader = new InputStreamReader(file, "GBK");
                            BufferedReader buffer1 = new BufferedReader(reader);
                            String op = buffer1.readLine();
                            StringBuffer bufferCheck = new StringBuffer();

                            if (isRedTree) {
                                int time = 0;
                                String name;
                                while ((name = buffer1.readLine()) != null) {
                                    name = name.toLowerCase();
                                    if (op.equals(ops[0])) {
                                        String s1 = buffer1.readLine();
                                        Node z = new Node(name, s1, nullNode, nullNode, nullNode, false);
                                        tree.RSInsert(tree, z);

                                    } else if (op.equals(ops[1])) {
                                        name = name.toLowerCase();
//                                        if (name.equals("coherent")){
//                                            tree.search(tree.getmRoot(), name);
//                                        }
                                        if (tree.search(tree.getmRoot(), name).getKey() != null) {
                                            tree.RBDelete(tree, tree.search(tree.getmRoot(), name));
                                        }else {
                                          //  System.out.println(name+"不存在");
                                        }
                                    } else {
                                        bufferCheck.append(tree.search(tree.getmRoot(), name).getKey() + "\n");
                                    }

                                    time++;
                                    if (time == 100 && time <= 500) {
//                                               StringBuffer buffer=new StringBuffer();
//                                               textShow.setText(tree.preOrder(tree.getmRoot(),0,0,buffer));
                                        time = 0;
                                    }
                                }
//                                StringBuffer buffer=new StringBuffer();

                                bufferCheck.append("红黑树操作完成\n");
                                textShow.setText(bufferCheck.toString());

                            } else {
                                int time = 0;
                                String name;
                                while ((name = buffer1.readLine()) != null) {
                                    name = name.toLowerCase();
                                    if (op.equals(ops[0])) {
                                        String s1 = buffer1.readLine();
                                        Btree.insertOrUpdate(name, s1);
                                    } else if (op.equals(ops[1])) {
                                        Btree.bTreeDelete(name);
                                    } else {
                                        bufferCheck.append(Btree.bTreeSearch(name) + "\n");
                                    }

                                    time++;
                                    if (time == 100 && time <= 500) {
                                        //       StringBuffer buffer=new StringBuffer();
                                        //       textShow.setText(tree.preOrder(tree.getmRoot(),0,0,buffer));
                                        time = 0;
                                    }
                                }


                                buffer1.close();
                                bufferCheck.append("B+树操作完成\n");
                            }
                            textShow.setText(bufferCheck.toString());

                        } catch (IOException e) {
                            textShow.setText("路径或文件有误");
                        }

                    }

                }
            });


            //自适应
            textBrowser.prefWidthProperty().bind(leftGridePane.widthProperty().divide(1.25));
            buttonBrowser.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            buttonSubmit.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            leftGridePane.prefWidthProperty().bind(background.widthProperty());

            leftGridePane.add(textBrowser, 0, 0, 2, 1);
            leftGridePane.add(buttonBrowser, 0, 1);
            leftGridePane.add(buttonSubmit, 1, 1);

            ColumnConstraints column1 = new ColumnConstraints();
            column1.setPercentWidth(50);
            ColumnConstraints column2 = new ColumnConstraints();
            column2.setPercentWidth(50);
            leftGridePane.getColumnConstraints().addAll(column1, column2);

            GridPane.setHalignment(buttonBrowser, HPos.CENTER);
            GridPane.setHalignment(buttonSubmit, HPos.CENTER);


            //左下
            GridPane leftdownGridePane = new GridPane();
            Label english = new Label("English:");
            Label chinese = new Label("Chinese:");
            TextField textEnglish = new TextField();
            TextField textChinese = new TextField();
//            english.setStyle("-fx-background-color: #f3f3f3;-fx-font-weight: bold");
//            chinese.setStyle("-fx-background-color: #f3f3f3;-fx-font-weight: bold");

            Button buttonAdd = new Button("Add");


            buttonAdd.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent arg0) {
                    if (textEnglish.getText() != null && textChinese.getText() != null) {
                        if (isRedTree) {
                            textEnglish.setText(textEnglish.getText().toLowerCase());
                            Node z = new Node(textEnglish.getText().toLowerCase(), textChinese.getText().toLowerCase(), nullNode, nullNode, nullNode, false);
                            tree.RSInsert(tree, z);
                            textShow.setText("add ok");
                        } else {
                            textEnglish.setText(textEnglish.getText().toLowerCase());
                            Btree.insertOrUpdate(textEnglish.getText().toLowerCase(), textChinese.getText().toLowerCase());
                            textShow.setText("add ok");
                        }
                    } else {
                        textShow.setText("请输入完全。");
                    }


                }
            });

            Button buttonDelete = new Button("Delete");


            buttonDelete.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent arg0) {
                    if (textEnglish.getText() != null) {
                        if (isRedTree) {
                            textEnglish.setText(textEnglish.getText().toLowerCase());
                            if (tree.search(tree.getmRoot(), textEnglish.getText().toLowerCase()).getKey() != null) {
                                tree.RBDelete(tree, tree.search(tree.getmRoot(), textEnglish.getText().toLowerCase()));
                            }
//                            if (tree.search(tree.getmRoot(), textEnglish.getText().toLowerCase()).getKey()!=null) {
//                                tree.RBDelete(tree, tree.search(tree.getmRoot(), textEnglish.getText().toLowerCase()));
//                            }

                        } else {
                            textEnglish.setText(textEnglish.getText().toLowerCase());
                            Btree.bTreeDelete(textEnglish.getText().toLowerCase());
                        }
                    } else {
                        textShow.setText("请输入");
                    }


                }
            });


            leftdownGridePane.setStyle("-fx-border-color: #c0c6c9;");
            leftdownGridePane.setAlignment(Pos.CENTER);
            leftdownGridePane.setHgap(10);
            leftdownGridePane.setVgap(20);
            leftdownGridePane.setPadding(new Insets(25, 25, 25, 25));

            // english.prefWidthProperty().bind(leftGridePane.widthProperty().divide(6));
            //chinese.prefWidthProperty().bind(leftGridePane.widthProperty().divide(6));
            textEnglish.prefWidthProperty().bind(leftGridePane.widthProperty().divide(10).multiply(2.7));
            textChinese.prefWidthProperty().bind(leftGridePane.widthProperty().divide(10).multiply(2.7));
            buttonAdd.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            buttonDelete.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            ColumnConstraints column3 = new ColumnConstraints();
            column1.setPercentWidth(50);
            ColumnConstraints column4 = new ColumnConstraints();
            column2.setPercentWidth(50);
            leftdownGridePane.getColumnConstraints().addAll(column3, column4);

            GridPane.setHalignment(buttonAdd, HPos.CENTER);
            GridPane.setHalignment(buttonDelete, HPos.CENTER);


            leftdownGridePane.add(english, 0, 0);
            leftdownGridePane.add(textEnglish, 1, 0);
            leftdownGridePane.add(chinese, 2, 0);
            leftdownGridePane.add(textChinese, 3, 0);
            leftdownGridePane.add(buttonAdd, 0, 1, 2, 1);
            leftdownGridePane.add(buttonDelete, 2, 1, 2, 1);


            //右边开始
            FlowPane flowPane = new FlowPane();
            flowPane.setAlignment(Pos.CENTER);
            flowPane.setHgap(10);
            flowPane.setPadding(new Insets(20, 10, 20, 10));
            RadioButton redBlackTree = new RadioButton("red-black tree");
            RadioButton bPlusTree = new RadioButton("B+ tree");
            ToggleGroup group = new ToggleGroup();
            redBlackTree.setToggleGroup(group);
            bPlusTree.setToggleGroup(group);
            redBlackTree.setSelected(true);
            flowPane.getChildren().addAll(redBlackTree, bPlusTree);
            redBlackTree.setOnAction(event -> {
                isRedTree = true;
            });
            bPlusTree.setOnAction(event -> {
                isRedTree = false;
            });


            //右下
            Label labelRight = new Label("LOOK-UP");
//            labelRight.setStyle("-fx-background-color: #f3f3f3;-fx-font-weight: bold");
            VBox rightBotVox = new VBox(40);
//            rightGride.setHgap(10);
//            rightGride.setVgap(20);
            rightBotVox.setPadding(new Insets(20, 25, 25, 25));
            rightBotVox.getChildren().add(labelRight);
            rightBotVox.setMargin(labelRight, new Insets(-30, 0, 0, -5));

            //边框
            rightBotVox.setStyle("-fx-border-color: #c1e4e9;");
            //  rightVbox.setStyle("-fx-border-color: #c1e4e9;");


            TextField translate = new TextField();
            TextField selectFrom = new TextField();
            TextField selectTo = new TextField();
            Button buttonTranslate = new Button("Translate");
            buttonTranslate.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent arg0) {
                    if (translate.getText() != null) {
                        translate.setText(translate.getText().toLowerCase());
                        if (isRedTree) {
                            textShow.setText(tree.search(tree.getmRoot(), translate.getText().toLowerCase()).getValue());
                        } else {
                            textShow.setText(Btree.bTreeSearch(translate.getText().toLowerCase()));
                        }
                    }


                }
            });

            Label select = new Label("Select from");
            Label to = new Label("to");
            Button buttonSubmitSearch = new Button("Submit");

            buttonSubmitSearch.setOnAction(new EventHandler<ActionEvent>() {
                public void handle(ActionEvent arg0) {
                    if (selectFrom.getText() != null && selectTo.getText() != null) {
                        selectFrom.setText(selectFrom.getText().toLowerCase());
                        selectTo.setText(selectTo.getText().toLowerCase());
                        if (isRedTree) {
                            textShow.setText(tree.SearchFromTo(selectFrom.getText().toLowerCase(), selectTo.getText().toLowerCase()));
                        } else {
                            textShow.setText(Btree.bTreeSearchFromT0(selectFrom.getText().toLowerCase(), selectTo.getText().toLowerCase()));
                        }
                    }


                }
            });

            buttonTranslate.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
            buttonSubmitSearch.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

            HBox hBox1 = new HBox(2);
            hBox1.getChildren().addAll(translate, buttonTranslate);

            HBox hBox2 = new HBox(2);
            hBox2.getChildren().addAll(select, selectFrom, to, selectTo, buttonSubmitSearch);


            textShow.setPrefColumnCount(10);
            textShow.setPrefRowCount(10);
            textShow.setEditable(false);
            //  textShow.setDisable(true);

            rightBotVox.getChildren().addAll(hBox1, hBox2, textShow);


//            rightGride.add(translate,0,0);
//            rightGride.add(buttonTranslate,1,0);
//            rightGride.add(select,0,1);
//            rightGride.add(selectFrom,1,1);
//            rightGride.add(to,2,1);
//            rightGride.add(selectTo,3,1);
//            rightGride.add(buttonSubmitSearch,4,1);


            rightVbox.getChildren().addAll(flowPane, rightBotVox);


            leftGridePane.prefHeightProperty().bind(leftVbox.heightProperty().divide(2).subtract(20));
            leftdownGridePane.prefHeightProperty().bind(leftVbox.heightProperty().divide(2).subtract(20));
            leftVbox.prefWidthProperty().bind(background.widthProperty().divide(2).subtract(13));
            rightVbox.prefWidthProperty().bind(background.widthProperty().divide(2).subtract(13));
            leftVbox.prefHeightProperty().bind(background.heightProperty().subtract(20));
            rightVbox.prefHeightProperty().bind(background.heightProperty().subtract(20));

            rightBotVox.prefHeightProperty().bind(rightVbox.heightProperty());
            textShow.prefHeightProperty().bind(rightBotVox.heightProperty().divide(1.5));

            hBox1.prefWidthProperty().bind(rightVbox.prefWidthProperty());
            translate.prefWidthProperty().bind(hBox1.prefWidthProperty().divide(4).multiply(3));
            buttonTranslate.prefWidthProperty().bind(hBox1.prefWidthProperty().divide(4).multiply(1));
            hBox2.prefWidthProperty().bind(rightVbox.prefWidthProperty());
            selectFrom.prefWidthProperty().bind(hBox2.prefWidthProperty().divide(4));
            selectTo.prefWidthProperty().bind(hBox2.prefWidthProperty().divide(4));
//            select.prefWidthProperty().bind(hBox2.prefWidthProperty().divide(8));
//            to.prefWidthProperty().bind(hBox2.prefWidthProperty().divide(16));
//            buttonSubmitSearch.prefWidthProperty().bind(hBox2.prefWidthProperty().subtract(selectTo.prefWidthProperty()).subtract(selectFrom.prefWidthProperty()).subtract(select.prefWidthProperty()).subtract(to.prefWidthProperty()));
//            buttonSubmitSearch.prefWidthProperty().bind(translate.maxWidthProperty());

            leftVbox.getChildren().add(leftGridePane);
            leftVbox.getChildren().add(leftdownGridePane);

            background.getChildren().addAll(leftVbox, rightVbox);
            Scene scene = new Scene(background, 1000, 400);
            scene.getStylesheets().add("All.css");

            primaryStage.setTitle("English-Chinese Dictionary");
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}


