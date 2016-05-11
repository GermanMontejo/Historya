package com.thesis.historya;

import java.io.IOException;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.FixedStepEngine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.WakeLockOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.Text;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;

import com.thesis.historya.level1.Barrio1Terrain1Activity;

public class GameStory extends BaseGameActivity {
    // CONTSTANTS
    private static final int CAMERA_WIDTH = 480;
    private static final int CAMERA_HEIGHT = 320;

    protected static final int MENU_ABOUT = 0;
    protected static final int MENU_QUIT = MENU_ABOUT + 1;
    protected static final int MENU_PLAY = 100;
    protected static final int MENU_SCORES = MENU_PLAY + 1;
    protected static final int MENU_OPTIONS = MENU_SCORES + 1;
    protected static final int MENU_HELP = MENU_OPTIONS + 1;

    // Fields
    protected Camera camera;
    protected Scene mainScene;

    private Handler handler;

    private BitmapTextureAtlas fontTexture;
    private BitmapTextureAtlas menuTexture;
    private TextureRegion menuTextureRegion;
    private Font font;
    private Text gameText, gameContent;

    EngineOptions engineOptions;
    FixedStepEngine fsEngine;
    // Constructors
    // Getters and Setters
    // Methods for/from SuperClass/Interfaces

    private GameUtils gameUtils;

    private Music backgroundMusic;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "town_of_wishes.mp3");
            backgroundMusic.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic.isPlaying()) {
            backgroundMusic.stop();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (gameUtils.isMusicOn()) {
            if (!backgroundMusic.isPlaying()) {
                backgroundMusic.play();
            }
        }
    }

    @Override
    public Engine onLoadEngine() {
        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
        engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE,
 new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsMusic(true).setNeedsSound(true);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        fsEngine = new FixedStepEngine(engineOptions, 60);
        handler = new Handler();

        return fsEngine;
    }

    @Override
    public void onLoadResources() {

        menuTexture = new BitmapTextureAtlas(1024, 1024,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
        menuTextureRegion = BitmapTextureAtlasTextureRegionFactory
                .createFromAsset(menuTexture, this,
                        "about_background2.png", 0, 0);
        fsEngine.getTextureManager().loadTexture(menuTexture);

        fontTexture = new BitmapTextureAtlas(256, 256,
                TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        FontFactory.setAssetBasePath("font/");
        font = FontFactory.createFromAsset(fontTexture, this, "flubber.TTF",
                12, true, Color.BLUE);
        fsEngine.getTextureManager().loadTexture(fontTexture);
        fsEngine.getFontManager().loadFont(font);
    }

    @Override
    public Scene onLoadScene() {
        fsEngine.registerUpdateHandler(new FPSLogger());
        gameText = new Text(CAMERA_WIDTH / 4 - 5, CAMERA_HEIGHT / 4, font, getResources().getString(R.string.game));
        gameContent = new Text(CAMERA_WIDTH / 4 - 30, CAMERA_HEIGHT / 4 + 20, font, getResources().getString(R.string.game_content));

        final int centerX = (CAMERA_WIDTH - menuTextureRegion.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - menuTextureRegion.getHeight()) / 2;
        mainScene = new Scene(1);
        Sprite menuBack = new Sprite(centerX, centerY, menuTextureRegion);
        mainScene.getLastChild().attachChild(menuBack);
        mainScene.getLastChild().attachChild(gameText);
        mainScene.getLastChild().attachChild(gameContent);
        return mainScene;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onLoadComplete() {
        handler.postDelayed(launchTask, 10000);
    }

    private final Runnable launchTask = new Runnable() {

        @Override
        public void run() {
            startActivity(new Intent(GameStory.this, Barrio1Terrain1Activity.class));
            GameStory.this.finish();
        }

    };

    public Runnable heroRunnable = new Runnable() {

        @Override
        public void run() {
            Class place = null;
            try {
                place = Class.forName(gameUtils.getLastPlace());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    };
}
