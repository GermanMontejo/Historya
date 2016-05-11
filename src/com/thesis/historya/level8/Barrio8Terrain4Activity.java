package com.thesis.historya.level8;

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
import org.anddev.andengine.entity.modifier.MoveModifier;
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

public class Barrio8Terrain4Activity extends BaseGameActivity {

    BuildableBitmapTextureAtlas heroTexture;
    BuildableBitmapTextureAtlas tiyanakTexture;

    // Textures
    private BitmapTextureAtlas roundTableTexture;
    private BitmapTextureAtlas squareChairTexture;
    private BitmapTextureAtlas bedTexture;
    private BitmapTextureAtlas shelfTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas backgroundTexture;

    // TextureRegions
    private TextureRegion roundTableTextureRegion;
    private TextureRegion squareChairTextureRegion;
    private TextureRegion bedTextureRegion;
    private TextureRegion shelfTextureRegion;
    private TextureRegion talkTextureRegion;
    private TextureRegion backgroundTextureRegion;
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TiledTextureRegion tiyanakTextureRegion;
    private TextureRegion swordButtonTextureRegion;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, swordButton;
    Sprite bed;

    AnimatedSprite sword;

    float backgroundX = 0;
    float backgroundY = 0;
    Sprite background;

    float chairX = 50;
    float chairY = 60;

    float tableX = 85;
    float tableY = chairY;
    Sprite table;
    Sprite chair;
    float heroX = 0;
    float heroY = 0;

    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;
    AnimatedSprite tiyanak;

    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;

    float tiyanakX = CAMERA_WIDTH - 150;
    float tiyanakY = 100;

    private final float shelfX = CAMERA_WIDTH - 100;
    private final float shelfY = 10;
    Sprite shelf;

    private Camera camera;
    private direction playerDirection;
    AnimatedSprite slashSprite;
    long[] durations = new long[4];

    private enum direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static String LEVEL = "LEVEL8";
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
    Music backgroundMusic;

    int heroScore = 0;
    DatabaseHelper dbHelper;
    SQLiteDatabase sqlDb;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        heroY = gameUtils.getY();
        if (getIntent().getBooleanExtra("fromB8Terrain2", false)) {
            heroX = 10;
            getIntent().putExtra("fromB8Terrain2", false);
        }

        playerDirection = direction.DOWN;
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "lullaby.mp3");
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

        tiyanakTexture = new BuildableBitmapTextureAtlas(64, 64, TextureOptions.DEFAULT);
        tiyanakTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(tiyanakTexture, this, "tiyanak4.png", 3, 3);

        try {
            tiyanakTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        squareChairTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        squareChairTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(squareChairTexture, getBaseContext(), "square_chair.png", 0, 0);

        roundTableTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        roundTableTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(roundTableTexture, getBaseContext(), "round_table.png", 0, 0);

        bedTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        bedTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(bedTexture, getBaseContext(), "bed.png", 0, 0);

        shelfTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        shelfTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shelfTexture, getBaseContext(), "bookshelves.png", 0, 0);

        backgroundTexture = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTexture, getBaseContext(), "house_background3.png", 0, 0);

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(shelfTexture);
        fsEngine.getTextureManager().loadTexture(roundTableTexture);
        fsEngine.getTextureManager().loadTexture(squareChairTexture);
        fsEngine.getTextureManager().loadTexture(bedTexture);
        fsEngine.getTextureManager().loadTexture(backgroundTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(heroTexture);
        fsEngine.getTextureManager().loadTexture(tiyanakTexture);
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
        hero = new AnimatedSprite(heroX, heroY, heroTextureRegion);

        tiyanak = new AnimatedSprite(tiyanakX, tiyanakY, tiyanakTextureRegion);

        chair = new Sprite(chairX, chairY, squareChairTextureRegion);
        table = new Sprite(tableX, tableY, roundTableTextureRegion);

        background = new Sprite(backgroundX, backgroundY, backgroundTextureRegion);
        shelf = new Sprite(shelfX, shelfY, shelfTextureRegion);
        bed = new Sprite(CAMERA_WIDTH / 2 - bedTexture.getWidth() / 2, 80, bedTextureRegion);
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        final PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), true);
        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 55);
        final Shape left1 = new Rectangle(0, 0, 20, CAMERA_HEIGHT / 2 - 28);
        final Shape left2 = new Rectangle(0, CAMERA_HEIGHT / 2 + 28, 20, CAMERA_HEIGHT / 2 + 28);
        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 15, CAMERA_WIDTH, 15);
        final Shape right1 = new Rectangle(CAMERA_WIDTH - 20, 0, 20, CAMERA_HEIGHT / 2 - 25);
        final Shape right2 = new Rectangle(CAMERA_WIDTH - 20, CAMERA_HEIGHT / 2 + 28, 20, CAMERA_HEIGHT / 2 + 28);
        // health
        healthRate = gameUtils.getHealthRate();
        health = new Rectangle(CAMERA_WIDTH - 130, 25, healthRate, 5);
        health.setColor(0, 100, 0);

        // health rate text
        healthRateText = new ChangeableText(health.getX(), health.getY() - 20, font, "Health rate: " + String.valueOf(healthRate));
        healthRateText.setVisible(true);

        PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, left1, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, left2, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, right1, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, right2, BodyType.StaticBody, wallFixtureDef);

        scene.getLastChild().attachChild(roof);
        scene.getLastChild().attachChild(left1);
        scene.getLastChild().attachChild(left2);
        scene.getLastChild().attachChild(right1);
        scene.getLastChild().attachChild(right2);
        scene.getLastChild().attachChild(ground);
        scene.getLastChild().attachChild(background);
        scene.getLastChild().attachChild(chair);
        scene.getLastChild().attachChild(table);
        scene.getLastChild().attachChild(bed);
        scene.getLastChild().attachChild(shelf);
        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);

        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(hero);

        tiyanak.setCurrentTileIndex(1);
        if (!gameUtils.isEnemyDead(LEVEL)) {
            scene.getLastChild().attachChild(tiyanak);
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                tiyanak.animate(new long[] { 155, 155, 155 }, 3, 5, true);
            }
        }, 1000);

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
                                if (tiyanak.getX() <= hero.getX() && tiyanak.isAnimationRunning() && !gameUtils.isEnemyDead(LEVEL)) {
                                    tiyanak.stopAnimation();
                                    tiyanak.detachSelf();
                                    scene.attachChild(tiyanak);
                                    tiyanak.animate(new long[] { 155, 155, 155 }, 6, 8, true);
                                }
                            } else if (pValueX == -1) {
                                playerDirection = direction.LEFT;
                                hero.animate(durations, 4, 7, false);
                                if (gameUtils.isSoundOn()) {
                                    running.play();
                                }
                                if (tiyanak.isAnimationRunning() && !gameUtils.isEnemyDead(LEVEL)) {
                                    tiyanak.stopAnimation();
                                    tiyanak.detachSelf();
                                    scene.attachChild(tiyanak);
                                    tiyanak.animate(new long[] { 155, 155, 155 }, 3, 5, true);
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
                                if (gameUtils.isSoundOn()) {
                                    running.play();
                                }
                            }
                        }
                        if (hero.getX() <= -10) {
                            dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                            Intent b8Terrain2Intent = new Intent(Barrio8Terrain4Activity.this, Barrio8Terrain2Activity.class);
                            b8Terrain2Intent.putExtra("heroFromT4X", hero.getX());
                            b8Terrain2Intent.putExtra("heroFromT4Y", hero.getY());
                            b8Terrain2Intent.putExtra("fromB8Terrain4", true);
                            startActivity(b8Terrain2Intent);
                            Barrio8Terrain4Activity.this.finish();
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
                        if (hero.collidesWith(tiyanak)) {
                            gameUtils.setEnemyKilled(LEVEL);
                            heroScore += 100;
                            tiyanak.setColor(1f, 0f, 0f);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tiyanak.detachSelf();
                                }
                            }, 2000);
                        }
                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.animate(new long[] { 140, 140, 140, 140 }, 20, 23, true);
                        slash.setLooping(true);
                        if (gameUtils.isSoundOn()) {
                            slash.play();
                        }
                        if (hero.collidesWith(tiyanak)) {
                            gameUtils.setEnemyKilled(LEVEL);
                            heroScore += 100;
                            tiyanak.setColor(1f, 0f, 0f);
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    tiyanak.detachSelf();
                                }
                            }, 2000);
                        }
                    }

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
                    if (hero.collidesWith(bed)) {
                        Toast.makeText(getBaseContext(), "It's just an ordinary bed...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(shelf)) {
                        Toast.makeText(getBaseContext(), "Most of these books are for kids...", Toast.LENGTH_LONG).show();
                    }
                    break;
                case TouchEvent.ACTION_UP:
                    break;
                }
                return true;
            }

        };

        scene.getLastChild().attachChild(talk);
        scene.getLastChild().attachChild(sword_button);
        scene.registerTouchArea(sword_button);
        scene.registerTouchArea(talk);
        scene.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(float pSecondsElapsed) {

                MoveModifier moveModifier = new MoveModifier(3, tiyanak.getX(), hero.getX(), tiyanak.getY(), hero.getY());

                if (tiyanak.getX() >= hero.getX()) {
                    tiyanak.clearEntityModifiers();
                    tiyanak.registerEntityModifier(moveModifier);
                } else {
                    tiyanak.clearEntityModifiers();
                    tiyanak.registerEntityModifier(moveModifier);
                }

                if (healthRate <= 0) {
                    gameUtils.gameOver(scene);
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Barrio8Terrain4Activity.this.startActivity(new Intent(Barrio8Terrain4Activity.this, GameOver.class));
                            Barrio8Terrain4Activity.this.finish();
                        }

                    }, 1500);
                }

                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 57);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(left1)) {
                    hero.setPosition(left1.getX() + 10, hero.getY());
                } else if (hero.collidesWith(left2)) {
                    hero.setPosition(left2.getX() + 10, hero.getY());
                } else if (hero.collidesWith(right1)) {
                    hero.setPosition(right1.getX() - 55, hero.getY());
                } else if (hero.collidesWith(right2)) {
                    hero.setPosition(right2.getX() - 55, hero.getY());
                } else if (hero.collidesWith(shelf)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(shelf.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(shelf.getX() + shelf.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= shelf.getY() + shelf.getHeight() && hero.getX() + hero.getWidth() >= shelf.getX() + 5
                            && hero.getX() <= shelf.getX() + shelf.getWidth() - 15) {
                        hero.setPosition(hero.getX(), shelf.getY() + shelf.getHeight());
                    }
                } else if (hero.collidesWith(tiyanak) && !gameUtils.isEnemyDead(LEVEL)) {
                    healthRate -= 2;
                } else if (hero.collidesWith(bed)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(bed.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(bed.getX() + bed.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= bed.getY() && hero.getX() + hero.getWidth() >= bed.getX() + 5
                            && hero.getX() <= bed.getX() + bed.getWidth() - 15) {
                        hero.setPosition(hero.getX(), bed.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= bed.getY() + bed.getHeight() && hero.getX() + hero.getWidth() >= bed.getX() + 5
                            && hero.getX() <= bed.getX() + bed.getWidth() - 15) {
                        hero.setPosition(hero.getX(), bed.getY() + bed.getHeight());
                    }
                } else if (hero.collidesWith(chair)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(chair.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(chair.getX() + chair.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= chair.getY() + chair.getHeight() && hero.getX() + hero.getWidth() >= chair.getX() + 5
                            && hero.getX() <= chair.getX() + chair.getWidth() - 15) {
                        hero.setPosition(hero.getX(), chair.getY() + chair.getHeight());
                    }
                } else if (hero.collidesWith(table)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(table.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(table.getX() + table.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= table.getY() + table.getHeight() && hero.getX() + hero.getWidth() >= table.getX() + 5
                            && hero.getX() <= table.getX() + table.getWidth() - 15) {
                        hero.setPosition(hero.getX(), table.getY() + table.getHeight());
                    }
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
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio8Terrain4Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                        Barrio8Terrain4Activity.this.startActivity(new Intent(Barrio8Terrain4Activity.this, MainMenuActivity.class));
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
