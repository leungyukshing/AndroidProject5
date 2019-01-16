package com.example.httpapi;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;

import rx.Observable;
import rx.Subscriber;


public class BilibiliActivity extends AppCompatActivity {
    private EditText inputBox;
    private Button button;
    private ImageView test;
    final String api = "https://space.bilibili.com/ajax/top/showTop?mid=";
    final String previewapi = "https://api.bilibili.com/pvideo?aid=";
    RecyclerView recyclerView;
    MyAdapter myAdapter;
    ArrayList<RecylerObj> list;
    Observable imgObservable;
    Observable previewObservable;
    Observable bigImageObservable;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bilibili);

        // 初始化变量
        inputBox = (EditText)findViewById(R.id.inputBox);
        button = (Button)findViewById(R.id.button);
        test = (ImageView)findViewById(R.id.test);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        list = new ArrayList<>();
        myAdapter = new MyAdapter(list);

        // 渲染RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(myAdapter);

        //  Register Event Bus
        EventBus.getDefault().register(this);

        final Observable operationObservable = Observable.create(new Observable.OnSubscribe<RecylerObj>() {
            @Override
            public void call(Subscriber<? super RecylerObj> subscriber) {
               int responseCode = 0;
                RecylerObj recylerObj = null;
                try {
                   // 使用API请求数据
                   String userid = inputBox.getText().toString();
                   URL url = new URL(api + userid);
                   final HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                   responseCode = httpURLConnection.getResponseCode();

                   // 解析Data中的数据
                   InputStream inputStream = httpURLConnection.getInputStream();
                   BufferedReader reader = new BufferedReader((new InputStreamReader(inputStream)));
                   String data;
                   if(responseCode == 200) {
                       String inputLine;
                       StringBuffer resultData = new StringBuffer();
                       while((inputLine = reader.readLine()) != null) {
                           resultData.append(inputLine);
                       }
                       data = resultData.toString();
                       //System.out.println("Data: " + data);

                        // 将字符串数据转为Json后，初始化RecylerObj
                       recylerObj = new Gson().fromJson((String)data, RecylerObj.class);
                       recylerObj.getData().setVisible(true);

                       final RecylerObj obj = recylerObj;

                        // 获取数据后，新建一个Observable用于请求网络图片
                       imgObservable = Observable.create(new Observable.OnSubscribe<Bitmap>() {
                           @Override
                           public void call(Subscriber<? super Bitmap> subscriber) {
                               Bitmap bitmap = null;
                               try {
                                   System.out.println("Create Image Observable");
                                   // 获取图片
                                   URL imgUrl = new URL(obj.getData().getCover());
                                   HttpURLConnection bitmapConn = (HttpURLConnection)imgUrl.openConnection();
                                   if (bitmapConn.getResponseCode() == 200) {
                                       //System.out.println("Get Image!");
                                       InputStream inputStream1 = bitmapConn.getInputStream();
                                       bitmap = BitmapFactory.decodeStream(inputStream1);
                                   }
                               }
                               catch (Exception ex) {
                                   ex.printStackTrace();
                               }
                               subscriber.onNext(bitmap);
                               subscriber.onCompleted();
                           }
                       });
                       // 新建一个Observable用于请求预览图
                       previewObservable = Observable.create(new Observable.OnSubscribe<String[]>() {
                           @Override
                           public void call(Subscriber<? super String[]> subscriber) {
                               String[] indices = null;
                               try {
                                   //System.out.println("Create Preview Observable");
                                   // 获取预览图
                                   URL previewUrl = new URL(previewapi + obj.getData().getAid());
                                   HttpURLConnection previewConn = (HttpURLConnection)previewUrl.openConnection();
                                   if (previewConn.getResponseCode() == 200) {
                                       //System.out.println("Get Preview!");
                                       InputStream inputStream2 = previewConn.getInputStream();
                                       BufferedReader reader2 = new BufferedReader((new InputStreamReader(inputStream2)));
                                       String str;
                                       String inputLine;
                                       StringBuffer resultData = new StringBuffer();
                                       while((inputLine = reader2.readLine()) != null) {
                                           resultData.append(inputLine);
                                       }
                                       str = resultData.toString();

                                       JSONObject jsonObject = new JSONObject(str);
                                       String imageStr = jsonObject.getJSONObject("data").getString("image");
                                       String index = jsonObject.getJSONObject("data").getString("index");
                                       final String[] image = imageStr.substring(2, imageStr.length() - 3).split("\",\"");
                                       index = index.substring(1, index.length()-1);
                                       /*
                                       for (int i = 0; i < image.length; i++) {
                                           System.out.println(image[i]);
                                       }
                                       */
                                       indices = index.split(",");

                                       // 根据image获取大图
                                       bigImageObservable = Observable.create(new Observable.OnSubscribe<ArrayList<Bitmap>>() {
                                           @Override
                                           public void call(Subscriber<? super ArrayList<Bitmap>> subscriber) {
                                               Bitmap bitmap = null;
                                               ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
                                               try {
                                                   // 获取大图
                                                   URL bigImageUrl = new URL(image[0]);
                                                   HttpURLConnection bitmapConn = (HttpURLConnection)bigImageUrl.openConnection();
                                                   if (bitmapConn.getResponseCode() == 200) {
                                                       //System.out.println("Get Big Image!");
                                                       InputStream inputStream3 = bitmapConn.getInputStream();
                                                       bitmap = BitmapFactory.decodeStream(inputStream3);
                                                       bitmapArrayList.add(bitmap);
                                                   }
                                                   if (image.length > 1) {
                                                       URL bigImageUrl1 = new URL(image[1]);
                                                       HttpURLConnection bitmapConn1 = (HttpURLConnection)bigImageUrl1.openConnection();
                                                       if (bitmapConn1.getResponseCode() == 200) {
                                                           InputStream inputStream4 = bitmapConn1.getInputStream();
                                                           bitmap = BitmapFactory.decodeStream(inputStream4);
                                                           bitmapArrayList.add(bitmap);
                                                       }
                                                   }

                                               }
                                               catch (Exception ex) {
                                                   ex.printStackTrace();
                                               }
                                               subscriber.onNext(bitmapArrayList);
                                               subscriber.onCompleted();
                                           }
                                       });
                                   }
                               }
                               catch (Exception ex) {
                                   ex.printStackTrace();
                               }
                               subscriber.onNext(indices);
                               subscriber.onCompleted();
                           }
                       });
                   }
               }
               catch (UnknownHostException ex) {
                   System.out.println("网络无连接错误");
                   EventBus.getDefault().post(new MessageEvent(0));
                   ex.printStackTrace();
               }
               catch (JsonSyntaxException ex) {
                    System.out.println("数据库不存在错误");
                    EventBus.getDefault().post(new MessageEvent(1));
                   ex.printStackTrace();
               }
               catch (Exception ex) {
                    ex.printStackTrace();
               }
               subscriber.onNext(recylerObj);
                subscriber.onCompleted();
            }
        });

        // 为button添加事件监听
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String input = inputBox.getText().toString();
                // 处理空输入情况
                if (input.equals("")) {
                    Toast.makeText(BilibiliActivity.this, "输入的ID不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                int userId = Integer.parseInt(input);
                if (userId <= 0) {
                    Toast.makeText(BilibiliActivity.this, "ID 必须为正整数", Toast.LENGTH_SHORT).show();
                }
                else {
                    // 新建线程，请求返回用户数据
                    new Thread() {
                        @Override
                        public void run() {
                            super.run();
                            operationObservable.subscribe(new Subscriber() {
                                @Override
                                public void onCompleted() { }

                                @Override
                                public void onError(Throwable e) { }

                                @Override
                                public void onNext(Object o) {
                                    final RecylerObj t = (RecylerObj) o;
                                    if (t.isStatus()) {
                                        // 加入当前页面的列表中
                                        list.add(t);
                                        final int index = list.size();
                                        System.out.println("Receive aid: " + list.get(0).getData().getAid());
                                        // 告知主线程更新数据
                                        EventBus.getDefault().post(new MessageEvent(2));

                                        // 新建线程，请求返回该用户的图片
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                // 用于测试时看到进度条
                                                try {
                                                    sleep(1000);
                                                }
                                                catch (Exception ex) {
                                                    ex.printStackTrace();
                                                }
                                                super.run();
                                                //System.out.println("Image Thread");
                                                imgObservable.subscribe(new Subscriber() {
                                                    @Override
                                                    public void onCompleted() { }

                                                    @Override
                                                    public void onError(Throwable e) { }

                                                    @Override
                                                    public void onNext(Object o) {
                                                        // 获取用户图片后，加载图片
                                                        Bitmap bitmap = (Bitmap)o;
                                                        list.get(index-1).getData().setBitmap(bitmap);
                                                        list.get(index-1).getData().setVisible(false);
                                                        // 加载图片后，告知Adapter更新数据
                                                        EventBus.getDefault().post(new MessageEvent(2));
                                                    }
                                                });
                                            }
                                        }.start();
                                        // 新建线程，请求返回该用户的预览信息
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                previewObservable.subscribe(new Subscriber() {
                                                    @Override
                                                    public void onCompleted() { }

                                                    @Override
                                                    public void onError(Throwable e) { }

                                                    @Override
                                                    public void onNext(Object o) {
                                                        final String[] indices = (String[])o;
                                                        //System.out.println("Preview Thread" + indices.length);
                                                        // 新建线程，请求返回该用户的预览图图片
                                                        new Thread() {
                                                            @Override
                                                            public void run() {
                                                                super.run();
                                                                bigImageObservable.subscribe(new Subscriber() {
                                                                    @Override
                                                                    public void onCompleted() { }

                                                                    @Override
                                                                    public void onError(Throwable e) { }

                                                                    @Override
                                                                    public void onNext(Object o) {
                                                                        //System.out.println("BigImage Thread");
                                                                        ArrayList<Bitmap> bitmap = (ArrayList<Bitmap>) o;
                                                                        ArrayList<RecylerObj.PreviewImage> previewImageArrayList = new ArrayList<>();
                                                                        final int w = 160;
                                                                        final int h = 90;
                                                                        int x = 0, y = 0;
                                                                        int t = 0;
                                                                        //System.out.println("Size: " + indices.length);
                                                                        for (int i = 0; i < indices.length; i++) {
                                                                            //System.out.println("Slice Image, x = " + x + ", y = " + y + "Wid = " + bitmap.get(t).getWidth() + "Hei = " + bitmap.get(t).getHeight());
                                                                            if (i != 0 && i % 10 == 0) {
                                                                                x = 0;
                                                                                y += h;
                                                                            }
                                                                            if (x == bitmap.get(t).getWidth() || y == bitmap.get(t).getHeight()) {
                                                                                //System.out.println("T changed!");
                                                                                t++;
                                                                                x = 0;
                                                                                y = 0;
                                                                            }
                                                                            RecylerObj.PreviewImage previewImage = new RecylerObj.PreviewImage();
                                                                            previewImage.setIndex(Integer.valueOf(indices[i]));
                                                                            Bitmap b = Bitmap.createBitmap(bitmap.get(t), x, y, w, h);

                                                                            previewImage.setBitmap(b);
                                                                            previewImageArrayList.add(previewImage);
                                                                            //System.out.println("i = " +Integer.valueOf(indices[i]) +  "Index: " + previewImage.getIndex());
                                                                            x += w;
                                                                        }
                                                                        //System.out.println("Testing!");
                                                                        list.get(index-1).setPreviewImageArrayList(previewImageArrayList);
                                                                        // 加载图片后，告知Adapter更新数据
                                                                        EventBus.getDefault().post(new MessageEvent(2));
                                                                    }
                                                                });
                                                            }
                                                        }.start();
                                                    }
                                                });
                                            }
                                        }.start();
                                    }
                                    else {
                                        EventBus.getDefault().post(new MessageEvent(1));
                                    }
                                }
                            });
                        }
                    }.start();

                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        // 弹出Toast：网络连接失败
        if (event.code == 0) {
            Toast.makeText(BilibiliActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
        }
        // 弹出Toast：数据库中不存在记录
        else if (event.code == 1){
            Toast.makeText(BilibiliActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
        }
        // 更新Adapter
        else if (event.code == 2) {
            myAdapter.notifyDataSetChanged();
        }
        else if (event.code == 3) {
            System.out.println("测试");
            myAdapter.notifyDataSetChanged();
            test.setImageBitmap(list.get(0).getPreviewImageArrayList().get(1).getBitmap());
            System.out.println("Test: " + list.get(0).getPreviewImageArrayList().get(1).getIndex());
        }
        else {
            System.out.println("非法请求");
        }
    }
}
