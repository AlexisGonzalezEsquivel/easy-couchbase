package alexis.com.arqui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.util.ArrayUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import alexis.com.easy_couchbase.CRUD;

public class MainActivity extends AppCompatActivity {

    CRUD crud;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button buttonCreateStudent = (Button) findViewById(R.id.buttonCreateStudent);
        buttonCreateStudent.setOnClickListener(new OnClickListenerCreateStudent());

        crud = new CRUD(getApplicationContext());

        try {
            readRecords();
        } catch (CouchbaseLiteException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
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

    public void readRecords() throws CouchbaseLiteException, IOException, ClassNotFoundException {

        LinearLayout linearLayoutRecords = (LinearLayout) findViewById(R.id.linearLayoutRecords);
        linearLayoutRecords.removeAllViews();

        List<Object> students = crud.list("Student",Student.class);

        Log.d("ALEXIS",students.size()+"");
        /*se encima con el botón*/
        students.add(0,new Student("",""));

        if (students.size() > 0) {

            for (Object obj : students) {

                Student s = (Student) obj;
                Log.d("ALEXIS",s.toString());
                String id = s.getId();
                String studentFirstname = s.getName();
                String studentEmail = s.getEmail();

                String textViewContents = studentFirstname + " - " + studentEmail;

                TextView textViewStudentItem= new TextView(this);
                textViewStudentItem.setPadding(0, 10, 0, 10);
                textViewStudentItem.setText(textViewContents);
                textViewStudentItem.setTag(id);
                linearLayoutRecords.addView(textViewStudentItem);
                textViewStudentItem.setOnLongClickListener(
                        new OnLongClickListenerStudentRecord(
                                getApplicationContext()));
            }

        }

        else {
            TextView locationItem = new TextView(this);
            locationItem.setPadding(8, 8, 8, 8);
            linearLayoutRecords.addView(locationItem);
        }

    }
}
