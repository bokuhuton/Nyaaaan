package com.nyaaaam.nyaaan;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import java.util.Calendar;

import twitter4j.Twitter;
import twitter4j.TwitterException;

public class MainActivity extends AppCompatActivity {

    private EditText mInputText;
    private Twitter mTwitter;
    int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //認証
        if (!TwitterUtils.hasAccessToken(this)) {
            Intent intent = new Intent(this, OAuth.class);
            startActivity(intent);
            finish();
        }


        mTwitter = TwitterUtils.getTwitterInstance(this);
        mInputText = (EditText) findViewById(R.id.editText);


        findViewById(R.id.tweet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tweet();
            }
        });

            }

    private void tweet() {
        AsyncTask<String, Void, Boolean> task = new AsyncTask<String, Void, Boolean>() {

            String str  = mInputText.getText().toString();
            String emp = " ";
            int fl = 0;

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    if(str.isEmpty()) {
                        fl = 1;
                        return false;
                    }
                    else{
                        try {
                            Context ctxt = createPackageContext("com.nyaaaam.nyaaan", 0);
                            File f = new File(ctxt.getFilesDir(),"count.txt");
                            f.createNewFile();

                            FileInputStream fis = new FileInputStream(new File (ctxt.getFilesDir() , "count.txt"));
                            BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                            String num = br.readLine();
                            System.out.println(num);

                            try{
                                i = Integer.parseInt(num);
                            }catch(NumberFormatException e){
                                ;
                            }

                            i++;

                            FileOutputStream fos = openFileOutput("count.txt",MODE_PRIVATE);
                            BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(fos));
                            bw.write(String.valueOf(i));
                            bw.close();
                            fos.close();

                            fis.close();
                            mTwitter.updateStatus("にゃーん！"+ DateFormat.format("(yyyy/MM/dd/ kk:mm:ss)", Calendar.getInstance()).toString() +i+"回目");
                            return true;
                        }catch(IOException | PackageManager.NameNotFoundException e) {
                            e.printStackTrace();
                            return false;
                        }

                    }
                } catch (TwitterException e) {
                    e.printStackTrace();
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    showToast("ツイートが完了しました！");
                } else {
                    if(fl == 1) {
                        showToast("ツイート文が未記入です!");
                    }
                    showToast("ツイートに失敗しました。。。");
                }
            }
        };
        task.execute(mInputText.getText().toString());
    }

    private void showToast(String text) {
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
    }
}
