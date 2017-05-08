package alexis.com.arqui;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.couchbase.lite.CouchbaseLiteException;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import alexis.com.easy_couchbase.CRUD;
import alexis.com.easy_couchbase.DatabaseManager;

import static org.junit.Assert.*;

/**
 * Instrumentation test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {

    /*
*Se verifica que la base de datos sea creada
*Y que a pesar de que en los resources este como Doggos
*El framework debe pasarlo a lower case para aceptar el nombre
*Por lo tanto se espera que la base de datos creada tenga el nombre
*esperado
*/
    @Test
    public void baseDatos() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        DatabaseManager dbm = DatabaseManager.getInstance(appContext);
        assertEquals("students", dbm.getDatabase().getName());
    }

    @Test
    public void create() throws Exception {
        Context appContext = InstrumentationRegistry.getTargetContext();
        CRUD crud = new CRUD(appContext);
        assertNotEquals(null,crud.create(new Student("Alexis","Esq"),"Student"));
    }

    @Test
    public void find() throws InvocationTargetException, IllegalAccessException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        CRUD crud = new CRUD(appContext);
        Student sr = new Student("Alexis2","Esq2");
        String s = crud.create(sr,"Student");
        Student st = (Student) crud.find(s,"Student",Student.class);
        assertEquals(true,sr.getId().equals(st.getId()));
    }

    @Test
    public void delete() throws InvocationTargetException, IllegalAccessException, CouchbaseLiteException {
        Context appContext = InstrumentationRegistry.getTargetContext();
        CRUD crud = new CRUD(appContext);
        Student sr = new Student("Alexis3","Esq3");
        String s = crud.create(sr,"Student");
        assertEquals(true,crud.delete(s));
        assertEquals(false,crud.delete("sdahqw7823eh23ebhj"));
    }

    @Test
    public void list() throws InvocationTargetException, IllegalAccessException, CouchbaseLiteException, IOException, ClassNotFoundException {

        Context appContext = InstrumentationRegistry.getTargetContext();

        CRUD crud = new CRUD(appContext);

        crud.create(new Student("Alexis4","Esq4"),"Student");
        crud.create(new Student("Alexis5","Esq5"),"Student");

        /*
        * por los ya creados y eliminados
        */
        assertEquals(4,crud.list("Student",Student.class).size());
    }

}
