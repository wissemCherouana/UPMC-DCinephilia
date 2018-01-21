package tpdev.upmc.dcinephila.Fragments;

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
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import tpdev.upmc.dcinephila.Activities.MainActivity;
import tpdev.upmc.dcinephila.DesignClasses.AppController;
import tpdev.upmc.dcinephila.DesignClasses.ArticleSlider.CardSliderLayoutManager;
import tpdev.upmc.dcinephila.DesignClasses.ArticleSlider.CardSnapHelper;
import tpdev.upmc.dcinephila.DesignClasses.ArticleSlider.SliderAdapter;
import tpdev.upmc.dcinephila.R;


public class NewsFragment extends Fragment {

    private final ArrayList<String> descriptions = new ArrayList<>();
    private ArrayList<String> countries = new ArrayList<>() ;
    private final ArrayList<String> temperatures = new ArrayList<>();
    private final ArrayList<String> times = new ArrayList<>();
    private static String TAG = MainActivity.class.getSimpleName();

    private ArrayList<String> images = new ArrayList<>();
    private SliderAdapter sliderAdapter ;

    private CardSliderLayoutManager layoutManger;
    private RecyclerView recyclerView;
    private TextSwitcher temperatureSwitcher;
    //private TextSwitcher placeSwitcher;
    private TextSwitcher clockSwitcher;
    private TextSwitcher descriptionsSwitcher;

    private TextView country1TextView;
    private TextView country2TextView;
    private long countryAnimDuration;
    private int currentPosition=0;

    private View view;
    public NewsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_news, container, false);
        sliderAdapter = new SliderAdapter(getContext(), images, new NewsFragment.OnCardClickListener());

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
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

        final Typeface face = Typeface.createFromAsset(getContext().getAssets(), "font/Comfortaa-Light.ttf");

        temperatureSwitcher = (TextSwitcher) view.findViewById(R.id.ts_temperature);
        temperatureSwitcher.setFactory(new NewsFragment.TextViewFactory(R.style.TemperatureTextView, true));

      /*  placeSwitcher = (TextSwitcher) view.findViewById(R.id.ts_place);
        placeSwitcher.setFactory(new NewsFragment.TextViewFactory(R.style.PlaceTextView, false));
*/
        clockSwitcher = (TextSwitcher) view.findViewById(R.id.ts_clock);
        clockSwitcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView textView = new TextView(getContext());
                textView.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                textView.setPaintFlags(Paint.UNDERLINE_TEXT_FLAG);
                textView.setTextSize(16);
                return textView;
            }
        });

        clockSwitcher.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(times.get(currentPosition)));
                startActivity(browserIntent);


            }
        });


        descriptionsSwitcher = (TextSwitcher) view.findViewById(R.id.ts_description);
        descriptionsSwitcher.setInAnimation(getContext(), android.R.anim.fade_in);
        descriptionsSwitcher.setOutAnimation(getContext(), android.R.anim.fade_out);
        descriptionsSwitcher.setFactory(new NewsFragment.TextViewFactory(R.style.DescriptionTextView, false));

        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("The greatest showman"));
        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("Arrow"));
        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("Golden Globes"));
        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("Star Wars Last Of Jedi"));
        GetArticles(ThemoviedbApiAccess.GetArticlesQuery("Pitch Perfect 3"));

        return view;
    }

    private void initSwitchers() {


        temperatureSwitcher.setCurrentText(temperatures.get(0));

        //placeSwitcher.setCurrentText(places.get(0));

        clockSwitcher.setCurrentText(times.get(0));

        descriptionsSwitcher.setCurrentText(descriptions.get(0));

    }

    private void initCountryText() {
        countryAnimDuration = getResources().getInteger(R.integer.labels_animation_duration);
        country1TextView = (TextView) view.findViewById(R.id.tv_country_1);
        country2TextView = (TextView) view.findViewById(R.id.tv_country_2);
        country1TextView.setText(countries.get(0));

    }

    private void setCountryText(String text, boolean left2right) {
        final TextView invisibleText;
        final TextView visibleText;
        if (country1TextView.getAlpha() > country2TextView.getAlpha()) {
            visibleText = country1TextView;
            invisibleText = country2TextView;
        } else {
            visibleText = country2TextView;
            invisibleText = country1TextView;
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
        animSet.setDuration(countryAnimDuration);
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

        setCountryText(countries.get(pos % countries.size()), left2right);

        temperatureSwitcher.setInAnimation(getContext(), animH[0]);
        temperatureSwitcher.setOutAnimation(getContext(), animH[1]);
        temperatureSwitcher.setText(temperatures.get(pos % temperatures.size()));
/*

        placeSwitcher.setInAnimation(getContext(), animV[0]);
        placeSwitcher.setOutAnimation(getContext(), animV[1]);
        placeSwitcher.setText(places.get(pos % places.size()));
*/


        SpannableString content = new SpannableString("Content");
        content.setSpan(new UnderlineSpan(), 0, content.length(), 0);


        clockSwitcher.setInAnimation(getContext(), animV[0]);
        clockSwitcher.setOutAnimation(getContext(), animV[1]);
        clockSwitcher.setText(times.get(pos % times.size()));
        clockSwitcher.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                Intent browserIntent = new Intent(
                        Intent.ACTION_VIEW,
                        Uri.parse(times.get(currentPosition)));
                startActivity(browserIntent);


            }
        });
        descriptionsSwitcher.setText(descriptions.get(pos % descriptions.size()));


        currentPosition = pos;
    }


    private class TextViewFactory implements  ViewSwitcher.ViewFactory {

        @StyleRes
        final int styleId;
        final boolean center;

        TextViewFactory(@StyleRes int styleId, boolean center) {
            this.styleId = styleId;
            this.center = center;
        }

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {
            final TextView textView = new TextView(getContext());

            if (center) {
                textView.setGravity(Gravity.CENTER);
            }

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                textView.setTextAppearance(getContext(), styleId);
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
                            .makeSceneTransitionAnimation(getActivity(), sharedView, "shared");
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
                        String url = article_json.getString("url");
                        countries.add(title);
                        images.add(url_image);
                        temperatures.add(date);
                        times.add(url);
                        descriptions.add(description);
                    }

                    sliderAdapter.notifyDataSetChanged();


                    initCountryText();
                    initSwitchers();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getContext(),
                            "Error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                Toast.makeText(getContext(),
                        error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });

        AppController.getInstance().addToRequestQueue(jsonObjReq);
    }


}
