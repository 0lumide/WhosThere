package co.mide.whosthere;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;

import com.balysv.materialmenu.ps.MaterialMenuView;
import com.quinny898.library.persistentsearch.SearchBox;

/**
 * Subclass of SearchBox to override the onBackPressed behaviour
 * Created by Olumide on 1/29/2016.
 */
public class NoBackSearchBox extends SearchBox {
    private MenuListener menuListener;
    private SearchBackPressedListener backListener;

    public NoBackSearchBox(Context context) {
        this(context, null);
    }

    public NoBackSearchBox(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public NoBackSearchBox(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        MaterialMenuView materialMenu = (MaterialMenuView) findViewById(R.id.material_menu_button);

        materialMenu.setOnClickListener(null);
        materialMenu.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NoBackSearchBox.this.getSearchOpen()) {
                    if (!(backListener != null && backListener.onBackPressed()))
                        toggleSearch();
                } else {
                    if (menuListener != null)
                        menuListener.onMenuClick();
                }
            }
        });
    }

    @Override
    public void setMenuListener(MenuListener menuListener) {
        this.menuListener = menuListener;
        super.setMenuListener(menuListener);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent e) {
        if ((e.getKeyCode() == KeyEvent.KEYCODE_BACK) &&!(backListener != null && backListener.onBackPressed())) {
            toggleSearch();
            return true;
        }
        return (e.getKeyCode() == KeyEvent.KEYCODE_BACK && getVisibility() == View.VISIBLE) || super.dispatchKeyEvent(e);
    }

    public void setBackListener(SearchBackPressedListener backListener){
        this.backListener = backListener;
    }

    public static interface SearchBackPressedListener{
        boolean onBackPressed();
    }
}
