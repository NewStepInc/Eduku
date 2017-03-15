package eduku.org.views.Popular;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.CharacterPickerDialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.Locale;

import eduku.org.R;
import eduku.org.utils.UiUtil;
import eduku.org.views.Individual.IndividualActivity;
import eduku.org.views.Main.MainListAdapter;

public class PopularActivity extends Activity {
    private boolean m_bSearch;
    PopularListAdapter m_listAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popular);
        ImageView btnBack = (ImageView) findViewById(R.id.btn_back);
        UiUtil.applyImageButtonEffect(btnBack, new Runnable() {
            public void run() {
                finish();
            }
        });

        m_bSearch = getIntent().getExtras().getBoolean("search");
        if (m_bSearch == false)
            findViewById(R.id.txtSearch).setVisibility(View.GONE);
        else
            findViewById(R.id.txtSearch).setVisibility(View.VISIBLE);
        m_listAdapter = new PopularListAdapter(this, m_bSearch);
        ((ListView)findViewById(R.id.popular_list)).setAdapter(m_listAdapter);

        final EditText txtSearch = (EditText)findViewById(R.id.txtSearch);
        txtSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = txtSearch.getText().toString().toLowerCase(Locale.getDefault());
                m_listAdapter.Filter(text);
            }
        });
        ((ListView)findViewById(R.id.popular_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(PopularActivity.this, IndividualActivity.class);
                IndividualActivity.m_Ripple = m_listAdapter.mRippleArray.get(position).object;
                IndividualActivity.m_Campaign = m_listAdapter.mRippleArray.get(position).campaign;
                IndividualActivity.m_photo = false;
                startActivity(i);
            }
        });
    }
}
