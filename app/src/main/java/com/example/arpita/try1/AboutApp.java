package com.example.arpita.try1;

import android.graphics.Color;
import android.os.SystemClock;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class AboutApp extends AppCompatActivity {


    ArrayList<QuesAns.Faq> listq;
    ListView qlistView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about_app);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);

        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        listq= QuesAns.getQ();
        qlistView=(ListView)findViewById(R.id.li);

        QuestionListAdapter quesAdapter;
        quesAdapter = new QuestionListAdapter(listq);
        qlistView.setAdapter(quesAdapter);
    }


    private class QuestionListAdapter extends BaseAdapter {

        class QViewHolder{
            TextView question;
            TextView answer;


        }


        private ArrayList<QuesAns.Faq> mQuestions;

        public QuestionListAdapter(ArrayList<QuesAns.Faq> mQuestions) {

            this.mQuestions = mQuestions;
        }

        //no. of items to be sent in a array
        @Override
        public int getCount() {
            return mQuestions.size();
        }

        //gives position
        @Override
        public QuesAns.Faq getItem(int position)
        {
            return mQuestions.get(position);
        }


        //for making and fetching id of every item in the list
        @Override
        public long getItemId(int position)
        {
            return 0;
        }

        //send item of every view
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater li=getLayoutInflater();
            QViewHolder QViewHolder;

            if(convertView == null) {

                convertView = li.inflate(R.layout.item_list, null);
                QViewHolder=new QViewHolder();
                QViewHolder.question=(TextView)convertView.findViewById(R.id.place_name_tv);
                QViewHolder.answer=(TextView) convertView.findViewById(R.id.address_tv);
                convertView.setTag(QViewHolder);
            }else{

                QViewHolder=(QViewHolder)convertView.getTag();
            }


            QViewHolder.question.setText(getItem(position).ques);
            QViewHolder.answer.setText(getItem(position).ans);




            return convertView;
        }
    }


}
