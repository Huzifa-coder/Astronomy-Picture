package com.barmej.astronomypicture.Utiles;

import android.net.Uri;

import com.barmej.astronomypicture.MainActivity;
import com.barmej.astronomypicture.entity.Astronomy;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URISyntaxException;

public class OpenAstronomyDataParser extends MainActivity{

    private static final String TITLE_OWM = "title";
    private static final String EXPLANATION_OWM = "explanation";
    private static final String MEDIA_TYPE_OWM = "media_type";
    private static final String URI_OWM = "url";
    private static final String HD_URL_OWM = "hdurl";
    private static final String OWM_MESSAGE_CODE = "code";

    public static Astronomy getAstronomyInfoObjectFromJson(JSONObject astronomyJson) throws JSONException, MalformedURLException, URISyntaxException {

        if(astronomyJson == null){
            return null;
        }

        Astronomy mAstronomy = new Astronomy();
        mAstronomy.setTitle(astronomyJson.getString(TITLE_OWM));
        mAstronomy.setExplanation(astronomyJson.getString(EXPLANATION_OWM));
        mAstronomy.setMediaType(astronomyJson.getString(MEDIA_TYPE_OWM));
        mAstronomy.setUri(Uri.parse(astronomyJson.getString(URI_OWM)));
        if (mAstronomy.getMediaType().equals("image"))
        mAstronomy.setHdurl(Uri.parse(astronomyJson.getString(HD_URL_OWM)));


        return mAstronomy;
    }//end of getAstronomyInfoObjectFromJson

}//end of OpenAstronomyDataParser
