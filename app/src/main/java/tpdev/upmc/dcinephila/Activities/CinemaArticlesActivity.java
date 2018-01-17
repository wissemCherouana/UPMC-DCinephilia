package tpdev.upmc.dcinephila.Activities;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.StyleRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import tpdev.upmc.dcinephila.APIs.ThemoviedbApiAccess;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.DesignClasses.ArticleSlider.CardSliderLayoutManager;
import tpdev.upmc.dcinephila.DesignClasses.ArticleSlider.CardSnapHelper;
import tpdev.upmc.dcinephila.DesignClasses.ArticleSlider.SliderAdapter;
import tpdev.upmc.dcinephila.R;

public class CinemaArticlesActivity extends AppCompatActivity {

    private ArrayList<String> titles = new ArrayList<>();
    private ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private ArrayList<String> links = new ArrayList<>();
    private ArrayList<String> images = new ArrayList<>();

    private CardSliderLayoutManager layoutManger;
    private SliderAdapter sliderAdapter ;
    private RecyclerView recyclerView;

    private TextSwitcher dateSwitcher;
    private TextSwitcher linkSwitcher;
    private TextSwitcher descriptionsSwitcher;

    private TextView title1TextView;
    private TextView title2TextView;
    private long titleAnimDuration;
    private int currentPosition=0;

    private static String TAG = CinemaArticlesActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cinema_articles);


        sliderAdapter = new SliderAdapter(getApplicationContext(), images, new OnCardClickListener());

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        recyclerView.setAdapter(sliderAdapter);
        recyclerView.setHasFixedSize(true);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    onActiveCardChange();
                }
            }
        });

        layoutManger = (CardSliderLayoutManager) recyclerView.getLayoutManager();

        new CardSnapHelper().attachToRecyclerView(recyclerView);

        final Typeface face = Typeface.createFromAsset(getApplicationContext().getAssets(), "font/Comfortaa-Light.ttf");

        dateSwitcher = (TextSwitcher) findViewById(R.id.ts_temperature);
        dateSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(getResources().getColor(R.color.caldroid_black));
                textView.setGravity(Gravity.CENTER);
                return textView;
            }
        });

        linkSwitcher = (TextSwitcher) findViewById(R.id.ts_clock);
        linkSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getApplicationContext());
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                textView.setTextSize(16);
                return textView;
            }
        });

        linkSwitcher.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(links.get(currentPosition)));
                startActivity(browserIntent);


            }
        });


        descriptionsSwitcher = (TextSwitcher) findViewById(R.id.ts_description);
        descriptionsSwitcher.setInAnimation(this, android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(this, android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new TextViewFactory(R.style.DescriptionTextView, false));

        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("Pitch Perfect 3"));
        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("Star Wars Last Of Jedi"));
        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("Thor ragnarok"));



    }


    @Override
    protected void onPause() {
        super.onPause();
    }

    private void initSwitchers() {


        dateSwitcher.setCurrentText(dates.get(0));
        linkSwitcher.setCurrentText(links.get(0));
        descriptionsSwitcher.setCurrentText(descriptions.get(0));

    }

    private void initCountryText() {
        titleAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        title1TextView = (TextView) findViewById(R.id.tv_country_1);
        title2TextView = (TextView) findViewById(R.id.tv_country_2);
        title1TextView.setText(titles.get(0));

    }

    private void setCountryText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (title1TextView.getAlpha() > title2TextView.getAlpha()) {
            visibleText = title1TextView;
            invisibleText = title2TextView;
        } else {
            visibleText = title2TextView;
            invisibleText = title1TextView;
        }

        final int vOffset=0;
        if (left2right) {
            invisibleText.setX(0);
            //  vOffset = countryOffset2;
        } else {
            //invisibleText.setX(countryOffset2);

        }

        invisibleText.setText(text);

        final ObjectAnimator iAlpha = ObjectAnimator.ofFloat(invisibleText, "alpha", 1f);
        final ObjectAnimator vAlpha = ObjectAnimator.ofFloat(visibleText, "alpha", 0f);
        final ObjectAnimator iX = ObjectAnimator.ofFloat(invisibleText, "x", 0);
        final ObjectAnimator vX = ObjectAnimator.ofFloat(visibleText, "x", vOffset);

        final AnimatorSet animSet = new AnimatorSet();
        animSet.playTogether(iAlpha, vAlpha, iX, vX);
        animSet.setDuration(titleAnimDuration);
        animSet.start();
    }

    private void onActiveCardChange() {
        final int pos = layoutManger.getActiveCardPosition();
        if (pos == RecyclerView.NO_POSITION || pos == currentPosition) {
            return;
        }


        onActiveCardChange(pos);
    }

    private void onActiveCardChange(int pos) {
        int animH[] = new int[] {R.anim.slide_in_right, R.anim.slide_out_left};
        int animV[] = new int[] {R.anim.slide_in_top, R.anim.slide_out_bottom};

        final boolean left2right = pos < currentPosition;
        if (left2right) {
            animH[0] = R.anim.slide_in_left;
            animH[1] = R.anim.slide_out_right;

            animV[0] = R.anim.slide_in_bottom;
            animV[1] = R.anim.slide_out_top;
        }

        setCountryText(titles.get(pos % titles.size()), left2right);

        dateSwitcher.setInAnimation(CinemaArticlesActivity.this, animH[0]);
        dateSwitcher.setOutAnimation(CinemaArticlesActivity.this, animH[1]);
        dateSwitcher.setText(dates.get(pos % dates.size()));

        SpannableString content = new SpannableString("Content");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);


        linkSwitcher.setInAnimation(CinemaArticlesActivity.this, animV[0]);
        linkSwitcher.setOutAnimation(CinemaArticlesActivity.this, animV[1]);
        linkSwitcher.setText(links.get(pos % links.size()));
        linkSwitcher.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(links.get(currentPosition)));
                startActivity(browserIntent);


            }
        });
        descriptionsSwitcher.setText(descriptions.get(pos % descriptions.size()));


        currentPosition = pos;
    }


    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(CinemaArticlesActivity.this);

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(CinemaArticlesActivity.this, styleId);
            } else {
                textView.setTextAppearance(styleId);
            }

            return textView;
        }

    }


    private class OnCardClickListener implements View.OnClickListener {
        @Override
        public void onClick(View view) {
            final CardSliderLayoutManager lm =  (CardSliderLayoutManager) recyclerView.getLayoutManager();

            if (lm.isSmoothScrolling()) {
                return;
            }

            final int activeCardPosition = lm.getActiveCardPosition();
            if (activeCardPosition == RecyclerView.NO_POSITION) {
                return;
            }

            final int clickedPosition = recyclerView.getChildAdapterPosition(view);
            if (clickedPosition == activeCardPosition) {
                // final Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                // intent.putExtra(DetailsActivity.BUNDLE_IMAGE_ID, pics[activeCardPosition % pics.length]);

                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
                    // startActivity(intent);
                } else {
                    final CardView cardView = (CardView) view;
                    final View sharedView = cardView.getChildAt(cardView.getChildCount() - 1);
                    final ActivityOptions options = ActivityOptions
                            .makeSceneTransitionAnimation(CinemaArticlesActivity.this, sharedView, "shared");
                    //startActivity(intent, options.toBundle());
                }
            } else if (clickedPosition > activeCardPosition) {
                recyclerView.smoothScrollToPosition(clickedPosition);
                onActiveCardChange(clickedPosition);
            }
        }
    }


    private void GetArticles(final String urlJsonObj) {

        JsonObjectRequest jsonObjReq = new JsonObjectRequest(Request.Method.GET,
                urlJsonObj, null, new Response.Listener<JSONObject>() {

            @Override
            public void onResponse(JSONObject response) {
                Log.d(TAG, response.toString());

                try {

                    JSONArray articles = (JSONArray) response.getJSONArray("articles");
                    for (int i=0; i<articles.length(); i++)
                    {
                        JSONObject article_json = (JSONObject) articles.get(i);

                        String url_image = article_json.getString("urlToImage");
                        String title = article_json.getString("title");
                        String[] dateParts = article_json.getString("publishedAt").split("T");
                        String date = dateParts[0];
                        String description = article_json.getString("description");
                        String author = "✍ Par : " + article_json.getString("author");
                        String url = article_json.getString("url");
                        titles.add(title);
                        images.add(url_image);
                        dates.add(date);
                        links.add(url);
                        descriptions.add(description);
                    }

                    sliderAdapter.notifyDataSetChanged();


                    initCountryText();
                    initSwitchers();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }



}
