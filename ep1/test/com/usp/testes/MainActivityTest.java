package com.usp.testes;

import static com.xtremelabs.robolectric.Robolectric.shadowOf;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Intent;
import android.widget.Button;

import com.usp.PostarComentario;
import com.usp.R;
import com.usp.bandeijao;
import com.xtremelabs.robolectric.RobolectricTestRunner;
import com.xtremelabs.robolectric.shadows.ShadowActivity;
import com.xtremelabs.robolectric.shadows.ShadowIntent;

@RunWith(RobolectricTestRunner.class)
public class MainActivityTest {
	
	private bandeijao activity;
    private Button postarComentarios;
    
	
    @Before
    public void setUp() throws Exception {
    	activity = new bandeijao();
        activity.onCreate(null);
        postarComentarios = (Button)activity.findViewById(R.id.btnPostarComentarios);
    }
    
	
	@Test
	public void clicarBandejao() throws Exception {
        
		postarComentarios.performClick();
		ShadowActivity shadowActivity = shadowOf(activity);
        Intent startedIntent = shadowActivity.getNextStartedActivity();
        ShadowIntent shadowIntent = shadowOf(startedIntent);
        assertThat(shadowIntent.getComponent().getClassName(), equalTo(PostarComentario.class.getName()));
    }
}
