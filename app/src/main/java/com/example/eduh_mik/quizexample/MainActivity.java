package com.example.eduh_mik.quizexample;

import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.eduh_mik.quizexample.droidtermsprovider.src.main.java.com.udacity.example.droidtermsprovider.DroidTermsExampleContract;


/**
 * Gets the data from the ContentProvider and shows a series of flash cards.
 */

public class MainActivity extends AppCompatActivity {

    // The current state of the app
    private Cursor mData;

    private int mCurrentState;

    private int mDefCol, mWordCol;

    private TextView mWordTextView, mDefinitionTextView;


    private Button mButton;

    // This state is when the word definition is hidden and clicking the button will therefore
    // show the definition
    private final int STATE_HIDDEN = 0;

    // This state is when the word definition is shown and clicking the button will therefore
    // advance the app to the next word
    private final int STATE_SHOWN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the views
        mWordTextView = (TextView)findViewById(R.id.text_view_word);
        mDefinitionTextView = (TextView)findViewById(R.id.text_view_definition);

        mButton = (Button) findViewById(R.id.button_next);

        new WordFetchTask().execute();
    }

    /**
     * This is called from the layout when the button is clicked and switches between the
     * two app states.
     * @param view The view that was clicked
     */
    public void onButtonClick(View view) {

        // Either show the definition of the current word, or if the definition is currently
        // showing, move to the next word.
        switch (mCurrentState) {
            case STATE_HIDDEN:
                showDefinition();
                break;
            case STATE_SHOWN:
                nextWord();
                break;
        }
    }

    public void nextWord() {

        if(mData != null){
            if(!mData.moveToNext()){
                mData.moveToFirst();
            }

            mDefinitionTextView.setVisibility(View.INVISIBLE);
            mButton.setText(getString(R.string.show_definition));

            mWordTextView.setText(mData.getString(mWordCol));
            mDefinitionTextView.setText(mData.getString(mDefCol));


            mCurrentState = STATE_HIDDEN;


        }







    }

    public void showDefinition() {

        if(mData != null){

            mDefinitionTextView.setVisibility(View.VISIBLE);
            // Change button text
            mButton.setText(getString(R.string.next_word));
            mCurrentState = STATE_SHOWN;
        }

    }
    @Override
    protected void onDestroy(){
        super.onDestroy();

        mData.close();
    }

    public class WordFetchTask extends AsyncTask<Void, Void, Cursor>{
        @Override
        protected Cursor doInBackground(Void... params){
            ContentResolver resolver = getContentResolver();

            Cursor cursor = resolver.query(DroidTermsExampleContract.CONTENT_URI,
                    null, null, null, null);

            return cursor;
        }
        @Override
        protected void onPostExecute(Cursor cursor){
            super.onPostExecute(cursor);

            mData = cursor;

            mDefCol = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_DEFINITION);
            mWordCol = mData.getColumnIndex(DroidTermsExampleContract.COLUMN_WORD);

            nextWord();
        }

    }

}
