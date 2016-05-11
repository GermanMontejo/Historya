package com.thesis.historya.level4;

import java.io.IOException;
import java.util.Arrays;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.audio.sound.Sound;
import org.anddev.andengine.audio.sound.SoundFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.FixedStepEngine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.AnalogOnScreenControl.IAnalogOnScreenControlListener;
import org.anddev.andengine.engine.camera.hud.controls.BaseOnScreenControl;
import org.anddev.andengine.engine.camera.hud.controls.DigitalOnScreenControl;
import org.anddev.andengine.engine.handler.IUpdateHandler;
import org.anddev.andengine.engine.handler.physics.PhysicsHandler;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.WakeLockOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.modifier.ColorModifier;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
import org.anddev.andengine.entity.modifier.ScaleModifier;
import org.anddev.andengine.entity.modifier.SequenceEntityModifier;
import org.anddev.andengine.entity.primitive.Rectangle;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.shape.Shape;
import org.anddev.andengine.entity.sprite.AnimatedSprite;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.text.ChangeableText;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.extension.input.touch.controller.MultiTouch;
import org.anddev.andengine.extension.input.touch.controller.MultiTouchController;
import org.anddev.andengine.extension.input.touch.exception.MultiTouchException;
import org.anddev.andengine.extension.physics.box2d.PhysicsFactory;
import org.anddev.andengine.extension.physics.box2d.PhysicsWorld;
import org.anddev.andengine.input.touch.TouchEvent;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.buildable.builder.BlackPawnTextureBuilder;
import org.anddev.andengine.opengl.texture.buildable.builder.ITextureBuilder.TextureAtlasSourcePackingException;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.opengl.texture.region.TiledTextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.splunk.mint.Mint;
import com.thesis.historya.DatabaseHelper;
import com.thesis.historya.GameOver;
import com.thesis.historya.GameUtils;
import com.thesis.historya.MainMenuActivity;
import com.thesis.historya.level3.Barrio3Terrain3Activity;

public class Barrio4Terrain2Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    // Textures
    private BuildableBitmapTextureAtlas skeletonTexture;
    private BitmapTextureAtlas arrowTexture;
    private BitmapTextureAtlas thornTexture;
    private BitmapTextureAtlas portalTexture;
    private BitmapTextureAtlas symbolTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas minaTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;

    // TextureRegions
    private TiledTextureRegion skeletonTextureRegion;
    private TextureRegion arrowTextureRegion;
    private TextureRegion thornTextureRegion;
    private TextureRegion portalTextureRegion;
    private TextureRegion symbolTextureRegion;
    private TextureRegion talkTextureRegion;
    private TextureRegion minaTextureRegion;
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;
    private TextureRegion grassTextureRegion;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, rock1, swordButton;

    AnimatedSprite sword;
    AnimatedSprite skeletonArcher;
    float heroX = 0;
    float heroY = 0;
    float arrowX = 0;
    float arrowY = 2;

    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;
    private final float minaX = 80;
    private final float minaY = 40;
    private Camera camera;
    private direction playerDirection;
    AnimatedSprite slashSprite;
    private float symbolX;
    private float symbolY;
    float portalX = CAMERA_WIDTH / 2 - 20;
    float portalY = CAMERA_HEIGHT - 70;
    private final float thornX = CAMERA_WIDTH - 150;
    private final float thornY = 0;
    float skeletonX = CAMERA_WIDTH - 70;
    float skeletonY = 20;
    private Sprite portal;
    long[] durations = new long[4];
    Sprite mina;
    Sprite arrow;
    private int countArrowShot = 50;
    private enum direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static String LEVEL = "LEVEL4";
    private SharedPreferences positionPref;
    private GameUtils gameUtils;
    private Sound running;
    private Sound slash;
    EditText etRiddle;
    private BitmapTextureAtlas fontTexture;
    private Font font;
    private ChangeableText healthRateText;
    int healthRate = 100;
    private Rectangle health;
    Scene scene;
    private Sprite symbol;
    private Sprite thorn;
    boolean hasThornBeenBurnt = false;

    private MoveModifier arrowModifier;
    protected boolean swordTouched;
    Music backgroundMusic;

    int heroScore = 0;
    DatabaseHelper dbHelper;
    SQLiteDatabase sqlDb;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        if (getIntent().getBooleanExtra("fromB4Terrain1", false)) {
            heroX = 10;
            getIntent().putExtra("fromB4Terrain1", false);
        } else if (getIntent().getBooleanExtra("fromB4Terrain3", false)) {
            heroX = CAMERA_WIDTH - 50;
        } else {
            heroX = gameUtils.getX();
        }
        heroY = gameUtils.getY();
        playerDirection = direction.DOWN;
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "visionary_villain.mp3");
            backgroundMusic.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SoundFactory.setAssetBasePath("sfx/");
        try {
            running = SoundFactory.createSoundFromAsset(getSoundManager(), getBaseContext(), "running.mp3");
            slash = SoundFactory.createSoundFromAsset(getSoundManager(), getBaseContext(), "slash.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gameUtils.getHealthRate() <= 0) {
            gameUtils.setHealthRate(100);
        }

        dbHelper = new DatabaseHelper(this);
        sqlDb = dbHelper.getWritableDatabase();
        heroScore = dbHelper.retrieveScore(sqlDb, getBaseContext(), gameUtils.retrieveUsername());
    }

    @Override
    public Engine onLoadEngine() {
        Mint.initAndStartSession(getBaseContext(), "0a0a51e6");

        camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);

        EngineOptions engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
        engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
        engineOptions.getTouchOptions();
        engineOptions.setNeedsSound(true);
        engineOptions.setNeedsMusic(true);
        fsEngine = new FixedStepEngine(engineOptions, 60);
        // fsEngine.setTouchController(new MultiTouchController());
        try {
            if (MultiTouch.isSupported(getBaseContext()) && MultiTouch.isSupportedByAndroidVersion()) {
                fsEngine.setTouchController(new MultiTouchController());
            }
        } catch (MultiTouchException e) {
            e.printStackTrace();
        }

        return fsEngine;
    }

    @Override
    protected void onPause() {
        super.onPause();
        gameUtils.setLastPlace(this.getClass().getName());
        gameUtils.setX(hero.getX());
        gameUtils.setY(hero.getY());
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
    public void onLoadResources() {

        fontTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        FontFactory.setAssetBasePath("font/");
        font = FontFactory.createFromAsset(fontTexture, this, "flubber.TTF", 15, true, Color.BLACK);
        BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

        heroTexture = new BuildableBitmapTextureAtlas(256, 256, TextureOptions.DEFAULT);
        heroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(heroTexture, this, "hero7.png", tileColumn, tileRow);

        try {
            heroTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        arrowTexture = new BitmapTextureAtlas(32, 8, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        arrowTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(arrowTexture, getBaseContext(), "arrow.png", 0, 0);

        skeletonTexture = new BuildableBitmapTextureAtlas(128, 64, TextureOptions.DEFAULT);
        skeletonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(skeletonTexture, getBaseContext(), "skeleton_archer2.png", 3, 2);

        try {
            skeletonTexture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {

        }
        thornTexture = new BitmapTextureAtlas(32, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        thornTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(thornTexture, getBaseContext(), "thorns.png", 0, 0);

        portalTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        portalTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(portalTexture, getBaseContext(), "portal.png", 0, 0);

        symbolTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        symbolTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(symbolTexture, getBaseContext(), "symbol.png", 0, 0);

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        minaTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        minaTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(minaTexture, getBaseContext(), "deadmina.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "drought_tile2.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(arrowTexture);
        fsEngine.getTextureManager().loadTexture(portalTexture);
        fsEngine.getTextureManager().loadTexture(thornTexture);
        fsEngine.getTextureManager().loadTexture(skeletonTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(symbolTexture);
        fsEngine.getTextureManager().loadTexture(minaTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(grassTexture);
        fsEngine.getTextureManager().loadTexture(heroTexture);
        fsEngine.getTextureManager().loadTexture(talkTexture);
        fsEngine.getTextureManager().loadTexture(fontTexture);
        fsEngine.getFontManager().loadFont(font);
    }

    @Override
    public Scene onLoadScene() {
        fsEngine.registerUpdateHandler(new FPSLogger());
        final int displayWidth = getWindowManager().getDefaultDisplay().getWidth();
        final int displayHeight = getWindowManager().getDefaultDisplay().getHeight();
        Arrays.fill(durations, 140);
        scene = new Scene(1);
        final int centerX = (CAMERA_WIDTH - grassTextureRegion.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - grassTextureRegion.getHeight()) / 2;
        Sprite background = new Sprite(centerX, centerY, grassTextureRegion);
        hero = new AnimatedSprite(heroX, heroY, heroTextureRegion);

        skeletonArcher = new AnimatedSprite(skeletonX, skeletonY, skeletonTextureRegion);
        skeletonArcher.animate(300);

        arrowX = skeletonArcher.getX() + 2;
        arrowY = skeletonArcher.getY() + (skeletonArcher.getHeight() / 2);
        arrow = new Sprite(arrowX, arrowY, arrowTextureRegion);
        symbolX = CAMERA_WIDTH / 2 - (symbolTexture.getWidth() / 2) + 13;
        symbolY = 115;
        symbol = new Sprite(symbolX, symbolY, symbolTextureRegion);
        symbol.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new ColorModifier(3f, 0f, 1f, 0f, 0f, 0f, 0f), new ColorModifier(3f, 0f, 0f, 0f, 1f, 0f, 0f), new ColorModifier(3f, 0f, 0f, 0f, 0f, 0f, 1f), new ColorModifier(3f, 0f, 0f, 0f, 1f, 0f, 1f))));

        thorn = new Sprite(thornX, thornY, thornTextureRegion);
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        final PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), true);
        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
        // health
        healthRate = gameUtils.getHealthRate();
        health = new Rectangle(CAMERA_WIDTH - 130, 25, healthRate, 5);
        health.setColor(0, 100, 0);

        // health rate text
        healthRateText = new ChangeableText(health.getX(), health.getY() - 20, font, "Health rate: " + String.valueOf(healthRate));
        healthRateText.setVisible(true);
        mina = new Sprite(minaX, minaY, minaTextureRegion);

        PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        scene.getLastChild().attachChild(roof);
        scene.getLastChild().attachChild(left);
        // scene.getLastChild().attachChild(right);
        scene.getLastChild().attachChild(ground);

        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);

        scene.getLastChild().attachChild(background);
        scene.getLastChild().attachChild(arrow);
        scene.getLastChild().attachChild(skeletonArcher);

        scene.getLastChild().attachChild(thorn);
        scene.getLastChild().attachChild(symbol);
        scene.getLastChild().attachChild(mina);
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(hero);

        final DigitalOnScreenControl digitalOnScreenControl = new DigitalOnScreenControl(0, CAMERA_HEIGHT - baseTextureRegion.getHeight(), camera, baseTextureRegion, knobTextureRegion, .1f,
                new IAnalogOnScreenControlListener() {

                    @Override
                    public void onControlChange(BaseOnScreenControl pBaseOnScreenControl, float pValueX, float pValueY) {
                        if (!hero.isAnimationRunning()) {
                            if (pValueX == 1) {
                                playerDirection = direction.RIGHT;
                                hero.animate(durations, 8, 11, false);
                                if (gameUtils.isSoundOn()) {
                                    running.play();
                                }
                            } else if (pValueX == -1) {
                                playerDirection = direction.LEFT;
                                hero.animate(durations, 4, 7, false);
                                if (gameUtils.isSoundOn()) {
                                    running.play();
                                }
                            } else if (pValueY == 1) {
                                playerDirection = direction.DOWN;
                                hero.animate(durations, 0, 3, false);
                                if (gameUtils.isSoundOn()) {
                                    running.play();
                                }
                            } else if (pValueY == -1) {
                                playerDirection = direction.UP;
                                hero.animate(durations, 12, 15, false);
                                if (gameUtils.isSoundOn()) {
                                    running.play();
                                }
                            } else {
                                hero.stopAnimation();
                                running.stop();
                            }
                        }
                        if (hero.getX() <= -10) {
                            dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                            Intent b3Terrain1Intent = new Intent(Barrio4Terrain2Activity.this, Barrio4Terrain1Activity.class);
                            b3Terrain1Intent.putExtra("heroFromT2X", hero.getX());
                            b3Terrain1Intent.putExtra("heroFromT2Y", hero.getY());
                            b3Terrain1Intent.putExtra("fromB3Terrain2", true);
                            startActivity(b3Terrain1Intent);
                            Barrio4Terrain2Activity.this.finish();
                        } else if (hero.getX() >= CAMERA_WIDTH) {
                            dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                            Intent b3Terrain3Intent = new Intent(Barrio4Terrain2Activity.this, Barrio4Terrain3Activity.class);
                            b3Terrain3Intent.putExtra("heroFromT2X", hero.getX());
                            b3Terrain3Intent.putExtra("heroFromT2Y", hero.getY());
                            b3Terrain3Intent.putExtra("fromB3Terrain2", true);
                            startActivity(b3Terrain3Intent);
                            Barrio4Terrain2Activity.this.finish();
                        }
                        physicsHandler.setVelocity(pValueX * 80, pValueY * 80);
                    }

                    @Override
                    public void onControlClick(AnalogOnScreenControl pAnalogOnScreenControl) {
                    }

                });

        digitalOnScreenControl.getControlBase().setBlendFunction(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        digitalOnScreenControl.getControlBase().setAlpha(0.5f);
        digitalOnScreenControl.getControlBase().setScaleCenter(0, 128);
        digitalOnScreenControl.getControlBase().setScale(1.25f);
        digitalOnScreenControl.getControlKnob().setScale(1.25f);
        digitalOnScreenControl.refreshControlKnobPosition();
        scene.setChildScene(digitalOnScreenControl);
        final Handler handler = new Handler();
        Sprite sword_button = new Sprite((CAMERA_WIDTH / 2 + swordButtonTexture.getWidth()), CAMERA_HEIGHT - 100, swordButtonTextureRegion) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                switch (pSceneTouchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:

                    if (playerDirection.equals(direction.LEFT)) {
                        hero.animate(new long[] { 140, 140, 140, 140 }, 16, 19, true);
                        slash.setLooping(true);
                        if (gameUtils.isSoundOn()) {
                            slash.play();
                        }
                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.animate(new long[] { 140, 140, 140, 140 }, 20, 23, true);
                        slash.setLooping(true);
                        if (gameUtils.isSoundOn()) {
                            slash.play();
                        }
                    }
                    if (hero.collidesWith(skeletonArcher)) {
                        skeletonArcher.setColor(1f, 0f, 0f);
                        gameUtils.setEnemyKilled(LEVEL);
                        heroScore += 50;
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                skeletonArcher.clearEntityModifiers();
                                skeletonArcher.detachSelf();
                                arrow.detachSelf();
                            }
                        }, 1500);

                    }
                    swordTouched = true;

                    break;
                case TouchEvent.ACTION_UP:
                    // sword.unregisterEntityModifier(entityModifier);
                    hero.stopAnimation();
                    slash.stop();
                    if (playerDirection.equals(direction.LEFT)) {
                        hero.setCurrentTileIndex(1, 1);
                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.setCurrentTileIndex(1, 2);
                    }
                    swordTouched = false;
                    break;
                }
                return true;
            }
        };

        Sprite talk = new Sprite(sword_button.getX() + sword_button.getWidth() + 45, sword_button.getY(), talkTextureRegion) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                switch (pSceneTouchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:
                    if (hero.collidesWith(mina)) {
                        Toast.makeText(getBaseContext(), "Poor girl. This must be Mina. Looking by the signs of it, she died out of hunger and thirst...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(symbol)) {
                        Toast.makeText(getBaseContext(), "It's the same symbol the mages used for conjuring a portal. *Chants the spell*", Toast.LENGTH_LONG).show();
                        Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                portal = new Sprite(portalX, portalY, portalTextureRegion);
                                portal.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(3f, 1f, 1.2f, 1f, 1.2f)));
                                scene.getLastChild().attachChild(portal);
                            }

                        }, 4000);

                    } else if (hero.collidesWith(thorn)) {
                        String items[] = new String[] { "Water Magic", "Fire Magic", "Lightning Magic", "Nature Magic" };
                        AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio4Terrain2Activity.this).setTitle("Which magic to use?").setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                case 0:
                                    if (gameUtils.doesHeroKnowSpell("water")) {
                                        Toast.makeText(getBaseContext(), "Nothing happens...", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "You haven't learned any water-based spell.", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case 1:
                                    if (gameUtils.doesHeroKnowSpell("fire")) {
                                        Toast.makeText(getBaseContext(), "*Chants fire spell*", Toast.LENGTH_LONG).show();
                                        thorn.registerEntityModifier(new ColorModifier(4f, 0f, 1f, 0f, 0f, 0f, 0f));
                                        Handler handler = new Handler();
                                        handler.postDelayed(new Runnable() {

                                            @Override
                                            public void run() {
                                                thorn.detachSelf();
                                                hasThornBeenBurnt = true;
                                                Toast.makeText(getBaseContext(), "You burnt the thick thorns...", Toast.LENGTH_LONG).show();
                                            }

                                        }, 4000);
                                    } else {
                                        Toast.makeText(getBaseContext(), "You haven't learned any fire-based spell.", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case 2:
                                    if (gameUtils.doesHeroKnowSpell("lightning")) {
                                        Toast.makeText(getBaseContext(), "Nothing happens...", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "You haven't learned any lightning-based spell.", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case 3:
                                    if (gameUtils.doesHeroKnowSpell("nature")) {
                                        Toast.makeText(getBaseContext(), "Nothing happens...", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "You haven't learned any nature-based spell.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            }
                        });
                        pauseAlert.show();
                    }
                    break;
                case TouchEvent.ACTION_UP:
                    break;
                }
                return true;
            }

        };

        arrow.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (hero.collidesWith(arrow) && !swordTouched) {
                    healthRate -= 2;
                }
            }

            @Override
            public void reset() {
            }

        });
        scene.getLastChild().attachChild(talk);
        scene.getLastChild().attachChild(sword_button);
        scene.registerTouchArea(sword_button);
        scene.registerTouchArea(talk);
        scene.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(float pSecondsElapsed) {
                arrowModifier = new MoveModifier(3, skeletonArcher.getX() + 2, hero.getX(), skeletonArcher.getY() + skeletonArcher.getHeight() / 2, hero.getY());
                if (!arrow.collidesWith(hero) && countArrowShot > 0 && arrow.getX() < 150 && !gameUtils.isEnemyDead(LEVEL)) {
                    arrow.unregisterEntityModifier(arrowModifier);
                    arrow.clearEntityModifiers();
                    arrow.detachSelf();
                    if (!arrow.hasParent()) {
                        scene.getLastChild().attachChild(arrow);
                        arrow.setPosition(arrowX, arrowY);
                    }

                } else if (!arrow.collidesWith(hero) && countArrowShot > 0 && !gameUtils.isEnemyDead(LEVEL)) {
                    arrow.registerEntityModifier(arrowModifier);
                } else {
                    arrow.unregisterEntityModifier(arrowModifier);
                    arrow.clearEntityModifiers();
                    arrow.setPosition(arrowX, arrowY);
                    countArrowShot --;
                }
                if (healthRate <= 0) {
                    gameUtils.gameOver(scene);
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Barrio4Terrain2Activity.this.startActivity(new Intent(Barrio4Terrain2Activity.this, GameOver.class));
                            Barrio4Terrain2Activity.this.finish();
                        }

                    }, 1500);
                }
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(rock1) && playerDirection.equals(direction.RIGHT) && hero.getY() + hero.getHeight() >= rock1.getY() + 70
                        && hero.getY() + hero.getHeight() <= rock1.getY() + 110) {
                    hero.setPosition(rock1.getX() - 45, hero.getY());
                } else if (hero.collidesWith(rock1) && playerDirection.equals(direction.LEFT) && hero.getY() + hero.getHeight() >= rock1.getY() + 70
                        && hero.getY() + hero.getHeight() <= rock1.getY() + 110) {
                    hero.setPosition(rock1.getX() + rock1.getWidth() - 20, hero.getY());
                } else if (hero.collidesWith(rock1)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(rock1.getX() - hero.getWidth(), hero.getY());
                    }
                } else if (hero.collidesWith(mina)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(mina.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(mina.getX() + mina.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= mina.getY() && hero.getX() + hero.getWidth() >= mina.getX() + 5
                            && hero.getX() <= mina.getX() + mina.getWidth() - 15) {
                        hero.setPosition(hero.getX(), mina.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= mina.getY() + mina.getHeight() && hero.getX() + hero.getWidth() >= mina.getX() + 5
                            && hero.getX() <= mina.getX() + mina.getWidth() - 15) {
                        hero.setPosition(hero.getX(), mina.getY() + mina.getHeight() + 2);
                    }
                } else if (hero.collidesWith(portal)) {
                    dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                    Intent b3Terrain3Intent = new Intent(Barrio4Terrain2Activity.this, Barrio3Terrain3Activity.class);
                    b3Terrain3Intent.putExtra("heroFromT2X", hero.getX());
                    b3Terrain3Intent.putExtra("heroFromT2Y", hero.getY());
                    b3Terrain3Intent.putExtra("fromB3Terrain3", true);
                    Barrio4Terrain2Activity.this.startActivity(b3Terrain3Intent);
                    Barrio4Terrain2Activity.this.finish();
                } else if (hero.collidesWith(thorn) && !hasThornBeenBurnt) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(thorn.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(thorn.getX() + thorn.getWidth() - 18, hero.getY());
                    }
                }
                if (!gameUtils.isEnemyDead(LEVEL)) {
                    skeletonArcher.registerEntityModifier(new MoveYModifier(10f, skeletonY, hero.getY()));
                }

                health.setWidth(healthRate);
                gameUtils.setHealthRate(healthRate);
                healthRateText.setText("Health rate: " + String.valueOf(healthRate));
            }

            @Override
            public void reset() {
            }

        });

        return scene;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scene.setIgnoreUpdate(true);
            String items[] = new String[] { "Resume", "Back to Main Menu" };
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio4Terrain2Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                        Barrio4Terrain2Activity.this.startActivity(new Intent(Barrio4Terrain2Activity.this, MainMenuActivity.class));
                        finish();
                        break;
                    }
                }
            });
            pauseAlert.show();
        }
        return super.onKeyDown(keyCode, event);

    }

    @Override
    public void onLoadComplete() {
    }
}
