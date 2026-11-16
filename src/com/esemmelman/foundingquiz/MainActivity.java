package com.esemmelman.foundingquiz;

import android.app.Activity;
import android.os.Bundle;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.content.res.ColorStateList;
import org.json.*;
import java.io.*;

public class MainActivity extends Activity {
    JSONArray questions;
    int position = -1;
    int[] answers = new int[20];
    LinearLayout page;
    final int navy = Color.rgb(20,40,65), teal = Color.rgb(0,102,100);
    public void onCreate(Bundle state) {
        super.onCreate(state);
        try (InputStream in = getAssets().open("questions.json")) {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            byte[] b = new byte[4096]; int n;
            while ((n = in.read(b)) != -1) out.write(b,0,n);
            questions = new JSONArray(out.toString("UTF-8"));
        } catch(Exception e) { throw new RuntimeException(e); }
        java.util.Arrays.fill(answers, -1);
        if(state != null) { position = state.getInt("position",-1); answers = state.getIntArray("answers"); }
        render();
    }
    protected void onSaveInstanceState(Bundle out) { super.onSaveInstanceState(out); out.putInt("position",position); out.putIntArray("answers",answers); }
    int dp(int value) { return (int)(value * getResources().getDisplayMetrics().density); }
    GradientDrawable background(int color, int border) {
        GradientDrawable shape = new GradientDrawable(); shape.setColor(color);
        shape.setCornerRadius(dp(20)); if(border != 0) shape.setStroke(dp(2),border); return shape;
    }
    void answerStyle(Button b, int color, int foreground) {
        b.setBackgroundTintList(null); b.setBackground(background(color,0)); b.setTextColor(foreground);
    }
    TextView text(String value, int size, boolean bold) {
        TextView t = new TextView(this); t.setText(value); t.setTextSize(Math.max(20, size * 1.35f)); t.setTextColor(navy);
        t.setLineSpacing(dp(4),1.05f);
        t.setPadding(0,dp(14),0,dp(14)); if(bold)t.setTypeface(null,Typeface.BOLD); page.addView(t);
        if (bold && size == 14) {
            t.setTextColor(Color.WHITE); t.setBackground(background(navy,0));
            t.setPadding(dp(18),dp(14),dp(18),dp(14));
        }
        if (size == 26 || size == 32) {
            t.setBackground(background(Color.WHITE,Color.rgb(212,225,240)));
            t.setPadding(dp(20),dp(22),dp(20),dp(22));
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,dp(16),0,dp(18)); t.setLayoutParams(lp);
        }
        return t;
    }
    Button button(String title, Runnable action) {
        Button b = new Button(this); b.setText(title); b.setTextSize(25); b.setAllCaps(false);
        b.setTypeface(null,Typeface.BOLD); b.setGravity(android.view.Gravity.CENTER_VERTICAL | android.view.Gravity.START);
        b.setPadding(dp(22),dp(22),dp(22),dp(22)); b.setMinHeight(dp(94));
        b.setBackgroundTintList(null); b.setBackground(background(Color.rgb(32,78,138),0)); b.setTextColor(Color.WHITE);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1,-2); lp.setMargins(0,dp(8),0,dp(8)); page.addView(b,lp);
        b.setOnClickListener(v -> action.run()); return b;
    }
    JSONObject q(int i) { return questions.optJSONObject(i); }
    int score(int start,int end) { int s=0; for(int i=start;i<end;i++)if(answers[i]==q(i).optInt("answer"))s++; return s; }
    String choice(JSONObject q,int i) { return q.optJSONArray("choices").optString(i); }
    void render() {
        ScrollView scroll = new ScrollView(this); scroll.setFillViewport(true);
        page = new LinearLayout(this); page.setOrientation(1); page.setPadding(dp(24),dp(20),dp(24),dp(24));
        page.setBackground(new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM,new int[]{Color.rgb(223,237,255),Color.rgb(246,240,255),Color.rgb(230,247,243)})); scroll.addView(page); setContentView(scroll);
        page.setOnApplyWindowInsetsListener((v,insets)->{ android.graphics.Insets bars=insets.getInsets(android.view.WindowInsets.Type.systemBars()); v.setPadding(dp(24)+bars.left,dp(20)+bars.top,dp(24)+bars.right,dp(24)+bars.bottom); return insets; });
        if(position < 0) {
            text("★ CIVICS • 20 QUESTIONS",14,true);
            text("Know your\nfounding documents",32,true);
            text("Explore the Bill of Rights and the Declaration of Independence, one question at a time.",19,false);
            text("10 questions on each document\nMultiple choice • No time limit\nAnswer explanations • Works offline",17,false);
            button("Start the quiz",()->{position=0;render();});
            text("Based on National Archives transcripts. Your progress survives screen rotation and your best score is saved on this phone.",14,false);
        } else if(position <20) {
            JSONObject item=q(position);
            text((position<10?"BILL OF RIGHTS":"DECLARATION OF INDEPENDENCE"),14,true);
            text("Question "+(position+1)+" of 20",18,false);
            ProgressBar progress=new ProgressBar(this,null,android.R.attr.progressBarStyleHorizontal); progress.setMax(20); progress.setProgress(position+(answers[position]>=0?1:0)); progress.setProgressTintList(ColorStateList.valueOf(teal)); page.addView(progress,new LinearLayout.LayoutParams(-1,dp(14)));
            text(item.optString("question"),26,true);
            for(int i=0;i<4;i++) {
                final int index=i; Button b=button((char)('A'+i)+". "+choice(item,i),()->{ answers[position]=index; render(); });
                int[] colors={Color.rgb(229,238,255),Color.rgb(237,229,255),Color.rgb(220,245,241),Color.rgb(255,239,211)};
                answerStyle(b,colors[i],navy);
                if(answers[position]>=0){ b.setEnabled(false); if(i==item.optInt("answer")) {b.setText("✓ "+b.getText()); answerStyle(b,Color.rgb(0,105,77),Color.WHITE);} else if(i==answers[position]) {b.setText("✗ "+b.getText()); answerStyle(b,Color.rgb(165,43,56),Color.WHITE);} }
            }
            if(answers[position]>=0) {
                TextView feedback=text(answers[position]==item.optInt("answer")?"✓ Correct!":"Keep learning",20,true);
                feedback.setTextColor(teal);
                text(item.optString("explanation"),18,false);
                button(position==19?"See my results":"Next question",()->{position++;render();});
                scroll.post(()->scroll.smoothScrollTo(0,Math.max(0,feedback.getTop()-dp(28))));
            } else text("Tap one answer to reveal the explanation.",15,false);
        } else {
            int total=score(0,20); android.content.SharedPreferences prefs=getSharedPreferences("quiz",0);
            int best=Math.max(total,prefs.getInt("best",0)); prefs.edit().putInt("best",best).apply();
            text("★ QUIZ COMPLETE",14,true); text(total+" / 20",44,true).setTextColor(teal);
            text(total>=16?"Strong work!":"Every question is a chance to learn.",22,true);
            text("Bill of Rights: "+score(0,10)+" / 10\nDeclaration: "+score(10,20)+" / 10\nPersonal best: "+best+" / 20",18,false);
            button("Try again",()->{java.util.Arrays.fill(answers,-1);position=0;render();});
            text("Your answer review",25,true);
            for(int i=0;i<20;i++) { JSONObject item=q(i); text((i+1)+". "+item.optString("question"),18,true); text("Your answer: "+choice(item,answers[i])+"\nCorrect answer: "+choice(item,item.optInt("answer"))+"\n"+item.optString("explanation"),16,false); }
        }
        text("Sources: National Archives",14,true);
        TextView sources=text("archives.gov/founding-docs/bill-of-rights-transcript\n\narchives.gov/founding-docs/declaration-transcript",13,false);
        sources.setAutoLinkMask(android.text.util.Linkify.WEB_URLS); sources.setLinksClickable(true);
    }
}
