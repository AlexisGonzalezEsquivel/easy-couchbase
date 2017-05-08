package alexis.com.easy_couchbase;

import android.content.Context;
import android.util.Log;

import com.couchbase.lite.CouchbaseLiteException;
import com.couchbase.lite.Database;
import com.couchbase.lite.Manager;
import com.couchbase.lite.android.AndroidContext;

import java.io.IOException;

/**
 * Created by alexis on 08/05/17.
 */

public class DatabaseManager {

    private final String TAG = "CouchbaseEvents";
    private Manager manager;
    private Database db;

    private static DatabaseManager instance = null;

    /**
     * Constructor
     * @param androidContext contexto de la aplicacion de android
     * @throws IOException
     */
    private DatabaseManager(Context androidContext) throws IOException {
        int s = androidContext.getResources().getIdentifier("dbName", "string", androidContext.getPackageName());
        String dbName = String.valueOf(androidContext.getResources().getString(s).trim()).toLowerCase();
        try {
            manager = new Manager(new AndroidContext(androidContext), Manager.DEFAULT_OPTIONS);
        } catch (IOException e) {
            return;
        }

        if (!Manager.isValidDatabaseName(dbName)) {
            Log.e(TAG, "ERROR IN DATABASE NAME");
            return;
        }

        try {
            db = manager.getDatabase(dbName);
            Log.d(TAG, "Database created");
        } catch (CouchbaseLiteException e) {
            Log.e(TAG, "Cannot get database");
            return;
        }
    }

    /**
     * Regresa una instancia de la clase
     * @param applicationContext Contexto de la aplicacion de android
     * @return
     */
    public static DatabaseManager getInstance(Context applicationContext){
        if(instance == null) {
            try {
                instance = new DatabaseManager(applicationContext);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     *
     * @return Regresa la base de datos
     */
    public Database getDatabase(){
        return db;
    }

    /**
     * Vacia la base de datos y genera una nueva con el mismo nombre
     * @throws CouchbaseLiteException
     */
    public void cleanDatabase() throws CouchbaseLiteException {
        String dbname = this.db.getName();
        this.db.delete();
        this.db = this.manager.getDatabase(dbname);
    }


}
