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
import eu.reusing.audiotacho.utils.FifoBuffer;

public class MainActivity extends AppCompatActivity implements SpeedDataConsumer {

    private FifoBuffer speed3Buffer = new FifoBuffer(3);
    private FifoBuffer speed5Buffer = new FifoBuffer(5);


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
        speed3Buffer.addDataPoint(speed);
        speed5Buffer.addDataPoint(speed);
        final TextView speedText = (TextView) findViewById(R.id.speed);
        final TextView speed3Text = (TextView) findViewById(R.id.speed3);
        final TextView speed5Text = (TextView) findViewById(R.id.speed5);
        final String speedStr = String.format("%1$,.2f", speed);
        final String speed3Str = String.format("%1$,.2f", speed3Buffer.getAverage());
        final String speed5Str = String.format("%1$,.2f", speed5Buffer.getAverage());
        speedText.post(new Runnable() {
            @Override
            public void run() {
                speedText.setText(speedStr);
                speed3Text.setText(speed3Str);
                speed5Text.setText(speed5Str);
            }
        });
        System.out.println(speed);
    }
}
