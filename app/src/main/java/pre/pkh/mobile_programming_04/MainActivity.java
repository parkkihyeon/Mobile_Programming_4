package pre.pkh.mobile_programming_04;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    // Database 관련 객체들
    SQLiteDatabase db;
    String dbName = "idList.db"; // name of Database;
    String tableName = "idListTable"; // name of Table;
    int dbMode = Context.MODE_PRIVATE;

    // layout object
    EditText mEtName;
    EditText mEtNumber;
    Button mBtInsert;
    Button mBtRead;
    Button mBtDel ;
    Button mBtUpdate ;
    Button mBtSortintg ;

    ListView list ;
    ArrayAdapter<String> musicAdapter;
    ArrayList<String> nameList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // create databases
        db = openOrCreateDatabase(dbName,dbMode,null);
        // create table;
        createTable();

        mEtName = (EditText) findViewById(R.id.et_text);
        mEtNumber = (EditText) findViewById(R.id.et_number) ;

        mBtInsert = (Button) findViewById(R.id.bt_insert);
        mBtRead = (Button) findViewById(R.id.bt_read);
        mBtDel = (Button) findViewById(R.id.bt_del);
        mBtUpdate = (Button) findViewById(R.id.bt_update);
        mBtSortintg = (Button) findViewById(R.id.sort) ;

        ListView mList = (ListView) findViewById(R.id.list_view);

        mBtInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = mEtName.getText().toString();
                insertData(name);
            }
        });

        mBtRead = (Button) findViewById(R.id.bt_read);
        mBtRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameList.clear(); // 원본 데이터를 수정하기 전에 삭제한다. 해줘야 하는거임.
                selectAll();
                musicAdapter.notifyDataSetChanged(); // 보여주는거임. Listview 전체를!
            }
        });

        mBtDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String index = mEtNumber.getText().toString();
                int Index = Integer.parseInt(index);
                removeData(Index);
            }
        });

        mBtSortintg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nameList.clear();
                SortingDescData() ;
                musicAdapter.notifyDataSetChanged();

            }
        });

        mBtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String index = mEtNumber.getText().toString();
                String name = mEtName.getText().toString();
                int Index = Integer.parseInt(index);
                updateData(Index,name) ;

            }
        });

        // Create listview
        nameList = new ArrayList<String>();
        musicAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, nameList);// 네임리스트에 있는 것을...
        mList.setAdapter(musicAdapter);

    }
//    // Database 생성 및 열기
//    public void createDatabase(String dbName, int dbMode) {
//        db = openOrCreateDatabase(dbName, dbMode, null);
//    }

    // Table 생성
    public void createTable() {
        try {
            String sql = "create table " + tableName + "(id integer primary key autoincrement, " + "name text not null)";
            db.execSQL(sql);
        } catch (android.database.sqlite.SQLiteException e) {
            Log.d("Lab sqlite", "error: " + e);
        }
    }

    // Table 삭제
    public void removeTable() {
        String sql = "drop table " + tableName;
        db.execSQL(sql);
    }

    // Data 추가
    public void insertData(String name) {
        String sql = "insert into " + tableName + " values(NULL, '" + name + "');";
        db.execSQL(sql);
    }

    // Data 업데이트
    public void updateData(int index, String name) {
        String sql = "update " + tableName + " set name = '" + name + "' where id = " + index + ";";
        db.execSQL(sql);
    }

    // Data 삭제
    public void removeData(int index) {
        String sql = "delete from " + tableName + " where id = " + index + ";";
        db.execSQL(sql);
    }

    // Data 읽기(꺼내오기)
    public void selectData(int index) {
        String sql = "select * from " + tableName + " where id = " + index + ";";
        Cursor result = db.rawQuery(sql, null);
        // select를 하면 데이터의 집합을 얻어옴. 존재하는 데이터. 셀렉된 데이터를 읽어옴  마이스커서처럼 차례대로 하나하나 옮김.

        // result(Cursor 객체)가 비어 있으면 false 리턴
        if (result.moveToFirst()) {
            int id = result.getInt(0);
            String name = result.getString(1);
//            Toast.makeText(this, "index= " + id + " name=" + name, Toast.LENGTH_LONG).show();

            Log.d("lab_sqlite", "\"index= \" + id + \" name=\" + name ");
        }
        result.close();
    }

    public void SortingDescData() {
        String sql = "select * from " + tableName + " order by " + "id desc;";
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
            Log.d("lab_sqlite", "index= " + id + " name=" + name);

            nameList.add(name);
            results.moveToNext();
        }
        results.close();
    }

    // 모든 Data 읽기
    public void selectAll() {
        String sql = "select * from " + tableName + ";"; // 오도바이 디크 , 디비 셀렉트 정렬 쳐서
        Cursor results = db.rawQuery(sql, null);

        results.moveToFirst();

        while (!results.isAfterLast()) {
            int id = results.getInt(0);
            String name = results.getString(1);
            Log.d("lab_sqlite", "index= " + id + " name=" + name);

            nameList.add(name);

            results.moveToNext();
        }
        results.close();
    }

}

//  셀렉트는 쿼리라고 부르기 때문에 로우쿼리를 사용한다. 필드 데이터를 리턴한다고 해서... 셀렉트는 쿼리라고 부른다.
