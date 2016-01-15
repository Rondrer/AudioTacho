package eu.reusing.audiotacho;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

import eu.reusing.audiotacho.dataprocessor.DataProcessor;
import eu.reusing.audiotacho.dataprocessor.SpeedDataConsumer;

public class MainActivity extends AppCompatActivity implements SpeedDataConsumer {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        final Button button = (Button) findViewById(R.id.button);
        final SpeedDataConsumer consumer = this;
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DataProcessor processor = new DataProcessor(consumer);
                (new Thread(processor)).start();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void updateSpeedData(final double speed) {
        final TextView textView = (TextView) findViewById(R.id.textView);

        textView.post(new Runnable() {
            @Override
            public void run() {
                textView.setText(String.format("%1$,.2f", speed));
            }
        });
        System.out.println(speed);
    }
}
