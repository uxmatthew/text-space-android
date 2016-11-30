package xyz.klinker.messenger.util.media.parsers;

import android.content.Context;
import android.net.Uri;

import org.json.JSONObject;

import java.util.regex.Pattern;

import xyz.klinker.android.article.ArticleLoadedListener;
import xyz.klinker.android.article.ArticleUtils;
import xyz.klinker.android.article.data.Article;
import xyz.klinker.messenger.BuildConfig;
import xyz.klinker.messenger.data.ArticlePreview;
import xyz.klinker.messenger.data.MimeType;
import xyz.klinker.messenger.util.Regex;
import xyz.klinker.messenger.util.media.MediaParser;

public class ArticleParser extends MediaParser {

    public static final String ARTICLE_API_KEY = "00624e91313bfce6e625bfcc40ee7d52";

    public ArticleParser(Context context) {
        super(context);
    }

    @Override
    protected Pattern getPatternMatcher() {
        return Regex.WEB_URL;
    }

    @Override
    protected String getIgnoreMatcher() {
        return null;
    }

    @Override
    protected String getMimeType() {
        return MimeType.MEDIA_ARTICLE;
    }

    @Override
    protected String buildBody(String matchedText) {
        ArticleUtils utils = new ArticleUtils(ARTICLE_API_KEY);
        Article article = utils.fetchArticle(context, matchedText);

        ArticlePreview preview = ArticlePreview.build(article);
        return preview != null && article != null && article.isArticle ? preview.toString() : null;
    }
}
