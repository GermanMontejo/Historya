package com.thesis.historya.level2;

import java.io.IOException;
import java.util.ArrayList;
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
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
import org.anddev.andengine.entity.modifier.MoveModifier;
import org.anddev.andengine.entity.modifier.MoveXModifier;
import org.anddev.andengine.entity.modifier.MoveYModifier;
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

public class Barrio2Terrain2Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    // Textures
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;
    private BitmapTextureAtlas rock1Texture;
    private BitmapTextureAtlas rock2Texture;
    private BuildableBitmapTextureAtlas enemy1Texture;

    // TextureRegions
    private TextureRegion talkTextureRegion;
    private TextureRegion rock1TextureRegion;
    private TextureRegion rock2TextureRegion;
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;
    private TextureRegion grassTextureRegion;
    private TiledTextureRegion enemy1TextureRegion;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, swordButton;

    AnimatedSprite sword;

    float heroX = 0;
    float heroY = 0;
    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;
    AnimatedSprite enemy;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;
    private Camera camera;
    private direction playerDirection;
    AnimatedSprite slashSprite;
    long[] durations = new long[4];
    private enum direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static String LEVEL = "LEVEL2";
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
    private float rock2X = 0;
    private float rock2Y = 0;
    Sprite rock2;
    ArrayList<Float> xCoordinates = new ArrayList<Float>();
    ArrayList<Float> yCoordinates = new ArrayList<Float>();
    boolean slashedRock = false;
    static int enemyHealthRate;

    private int countRockThrown;

    private Rectangle enemyHealth;

    private ChangeableText enemyHealthRateText;

    protected boolean enemyHit;

    protected boolean buttonTouched;
    private boolean isEnemyDead;
    private int shoutOnce;

    private LoopEntityModifier loopEntityModifier;

    private Sprite rock1;

    private MoveModifier moveModifier;
    Music backgroundMusic;

    int heroScore = 0;
    DatabaseHelper dbHelper;
    SQLiteDatabase sqlDb;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        if (getIntent().getBooleanExtra("fromB2Terrain1", false)) {
            heroX = 5;
            heroY = gameUtils.getY();
            getIntent().putExtra("fromB1Terrain1", false);
        } else if (getIntent().getBooleanExtra("fromB2Terrain3", false)) {
            heroY = CAMERA_HEIGHT - 30;
            heroX = gameUtils.getX();
        } else {
            heroX = gameUtils.getX();
            heroY = gameUtils.getY();
        }
        playerDirection = direction.DOWN;

        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "evil_approaches.mp3");
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
        enemyHealthRate = gameUtils.getEnemyLife(LEVEL);
        if (enemyHealthRate == -5 && !isEnemyDead) {
            gameUtils.setEnemyLife(LEVEL, 100);
            enemyHealthRate = 100;
        } else {
            enemyHealthRate = 0;
        }
        isEnemyDead = gameUtils.isEnemyDead(LEVEL);

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

        enemy1Texture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
        enemy1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(enemy1Texture, getBaseContext(), "shaman.png", 4, 2);

        try {
            enemy1Texture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "grass_tile5.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        rock1Texture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        rock1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(rock1Texture, getBaseContext(), "rock2.png", 0, 0);
        rock2Texture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        rock2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(rock1Texture, getBaseContext(), "rock3.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(grassTexture);
        fsEngine.getTextureManager().loadTexture(heroTexture);
        fsEngine.getTextureManager().loadTexture(talkTexture);
        fsEngine.getTextureManager().loadTexture(enemy1Texture);
        fsEngine.getTextureManager().loadTexture(fontTexture);
        fsEngine.getTextureManager().loadTexture(rock1Texture);
        fsEngine.getTextureManager().loadTexture(rock2Texture);
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

        float enemyX = CAMERA_WIDTH / 2 + enemy1TextureRegion.getWidth() + 15;
        final float enemyY = 30;
        enemy = new AnimatedSprite(enemyX, enemyY, enemy1TextureRegion);

        float rock1X = enemyX;
        float rock1Y = enemyY + 40;
        rock1 = new Sprite(rock1X, rock1Y, rock1TextureRegion);

        rock2X = rock1X + 5;
        rock2Y = rock1Y + 5;
        rock2 = new Sprite(rock2X, rock2Y, rock2TextureRegion);

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);
        final PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), true);
        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
        final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2, CAMERA_HEIGHT);
        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);

        // health
        healthRate = gameUtils.getHealthRate();
        health = new Rectangle(CAMERA_WIDTH - 130, 25, healthRate, 5);
        health.setColor(0, 100, 0);

        // health rate text
        healthRateText = new ChangeableText(health.getX(), health.getY() - 20, font, "Health rate: " + String.valueOf(healthRate));
        healthRateText.setVisible(true);

        enemyHealth = new Rectangle(100, 25, enemyHealthRate, 5);
        enemyHealth.setColor(100, 0, 0);

        // enemy's health rate
        enemyHealthRateText = new ChangeableText(enemyHealth.getX(), enemyHealth.getY() - 20, font, "Health rate: " + String.valueOf(enemyHealthRate));
        enemyHealthRateText.setVisible(true);

        PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        // PhysicsFactory.createBoxBody(physicsWorld, ground,
        // BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, right, BodyType.StaticBody, wallFixtureDef);
        scene.getLastChild().attachChild(roof);
        scene.getLastChild().attachChild(left);
        scene.getLastChild().attachChild(right);
        // scene.getLastChild().attachChild(ground);

        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);
        scene.getLastChild().attachChild(background);
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(enemyHealthRateText);
        scene.getLastChild().attachChild(enemyHealth);
        scene.getLastChild().attachChild(hero);
        if (!isEnemyDead) {
            scene.getLastChild().attachChild(enemy);
        } else {
            enemy.detachSelf();
        }

        scene.getLastChild().attachChild(rock2);
        scene.getLastChild().attachChild(rock1);

        float initialRockX = rock1.getX();
        float endRockX = initialRockX + 5;
        loopEntityModifier = new LoopEntityModifier(new SequenceEntityModifier(new MoveXModifier(1, initialRockX, endRockX), new MoveXModifier(1, endRockX, initialRockX)));

        if (!isEnemyDead) {
            rock1.registerEntityModifier(loopEntityModifier);
            enemy.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new MoveYModifier(3, enemyY, enemyY + 10), new MoveYModifier(3, enemyY + 10, enemyY))));
            enemy.setCurrentTileIndex(0);
        }

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
                            Intent b2Terrain1Intent = new Intent(Barrio2Terrain2Activity.this, Barrio2Terrain1Activity.class);
                            b2Terrain1Intent.putExtra("heroFromT2X", hero.getX());
                            b2Terrain1Intent.putExtra("heroFromT2Y", hero.getY());
                            b2Terrain1Intent.putExtra("fromB2Terrain2", true);
                            startActivity(b2Terrain1Intent);
                            Barrio2Terrain2Activity.this.finish();
                        } else if (hero.getY() >= CAMERA_HEIGHT) {
                            dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                            Intent b2Terrain3Intent = new Intent(Barrio2Terrain2Activity.this, Barrio2Terrain3Activity.class);
                            b2Terrain3Intent.putExtra("heroFromT2X", hero.getX());
                            b2Terrain3Intent.putExtra("heroFromT2Y", hero.getY());
                            b2Terrain3Intent.putExtra("fromB2Terrain2", true);
                            startActivity(b2Terrain3Intent);
                            Barrio2Terrain2Activity.this.finish();
                        }
                        xCoordinates.add(hero.getX());
                        yCoordinates.add(hero.getY());
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
                        try {
                            slash.setLooping(true);
                            if (gameUtils.isSoundOn()) {
                                slash.play();
                            }
                            buttonTouched = true;
                            slashedRock = true;
                            if (enemyHealthRate <= 0) {
                                heroScore += 50;
                                gameUtils.setEnemyKilled(LEVEL);
                                scene.detachChild(enemy);
                                if (enemyHealthRate <= 0 && shoutOnce < 1) {
                                    enemy.stopAnimation();
                                    enemy.setColor(0.5f, 0, 0, 0.7f);
                                    rock1.unregisterEntityModifier(loopEntityModifier);
                                    handler.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            enemy.detachSelf();
                                        }
                                    }, 1500);
                                    gameUtils.setEnemyLife(LEVEL, 0);
                                    gameUtils.setEnemyKilled(LEVEL);
                                    shoutOnce++;
                                    Toast.makeText(getBaseContext(), "Argh! Curse you!", Toast.LENGTH_LONG).show();
                                    gameUtils.setEnemyKilled(LEVEL);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.animate(new long[] { 140, 140, 140, 140 }, 20, 23, true);
                        try {
                            slash.setLooping(true);
                            slash.play();
                            buttonTouched = true;
                            slashedRock = true;
                            if (enemyHealthRate <= 0 && shoutOnce < 1) {
                                enemy.stopAnimation();
                                enemy.setColor(0.5f, 0, 0, 0.7f);
                                handler.postDelayed(new Runnable() {

                                    @Override
                                    public void run() {
                                        enemy.detachSelf();
                                    }
                                }, 1500);
                                gameUtils.setEnemyKilled(LEVEL);
                                heroScore += 50;
                                gameUtils.setEnemyLife(LEVEL, 0);
                                shoutOnce ++;
                                Toast.makeText(getBaseContext(), "Argh! Curse you!", Toast.LENGTH_LONG).show();
                                gameUtils.setEnemyKilled(LEVEL);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case TouchEvent.ACTION_UP:
                    // sword.unregisterEntityModifier(entityModifier);
                    hero.stopAnimation();
                    slash.stop();
                    buttonTouched = false;
                    if (playerDirection.equals(direction.LEFT)) {
                        hero.setCurrentTileIndex(1, 1);
                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.setCurrentTileIndex(1, 2);
                    }
                    enemyHit = false;
                    slashedRock = false;
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
                    break;
                case TouchEvent.ACTION_UP:
                    break;
                }
                return true;
            }

        };

        countRockThrown = 10;
        scene.getLastChild().attachChild(talk);
        scene.getLastChild().attachChild(sword_button);
        scene.registerTouchArea(sword_button);
        scene.registerTouchArea(talk);

        scene.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(float pSecondsElapsed) {
                moveModifier = new MoveModifier(3, rock2.getX(), hero.getX(), rock2.getY(), hero.getY());
                if (healthRate <= 0) {
                    gameUtils.gameOver(scene);
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            Barrio2Terrain2Activity.this.startActivity(new Intent(Barrio2Terrain2Activity.this, GameOver.class));
                            Barrio2Terrain2Activity.this.finish();
                        }
                    }, 1500);
                }
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(right) && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(right.getX() - 50, hero.getY());
                }
                if (hero.getX() < enemy.getX() && hero.collidesWith(enemy) && hero.getY() > enemy.getY() && !slashedRock) {
                    hero.setPosition(hero.getX(), hero.getY());
                    if (!isEnemyDead) {
                        healthRate -= 2;
                    }
                } else if (hero.getX() > enemy.getX() && hero.collidesWith(enemy) && hero.getY() > enemy.getY() && !slashedRock) {
                    hero.setPosition(hero.getX() + 10, hero.getY());
                    if (!isEnemyDead) {
                        healthRate -= 2;
                    }
                }
                if (!isEnemyDead) {

                    if (!rock2.collidesWith(hero) && countRockThrown > 0) {
                        rock2.clearEntityModifiers();
                        rock2.registerEntityModifier(moveModifier);
                    } else if (rock2.collidesWith(hero) && countRockThrown > 0 && slashedRock) {
                        rock2.unregisterEntityModifier(moveModifier);
                        rock2.clearEntityModifiers();
                        rock2.setPosition(rock2X, rock2Y);
                    } else {
                        rock2.unregisterEntityModifier(moveModifier);
                        rock2.clearEntityModifiers();
                        rock2.setPosition(rock2X, rock2Y);
                        if (enemyHealthRate > 0) {
                            healthRate -= 10;
                            countRockThrown--;
                        }
                    }
                }

                if (buttonTouched && hero.collidesWith(enemy)) {
                    if (enemyHealthRate >= 0) {
                        enemyHealthRate -= 5;
                    }
                } else if (hero.collidesWith(rock1)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(rock1.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(rock1.getX() + rock1.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= rock1.getY() && hero.getX() + hero.getWidth() >= rock1.getX() + 5
                            && hero.getX() <= rock1.getX() + rock1.getWidth() - 15) {
                        hero.setPosition(hero.getX(), rock1.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= rock1.getY() + rock1.getHeight() && hero.getX() + hero.getWidth() >= rock1.getX() + 5
                            && hero.getX() <= rock1.getX() + rock1.getWidth() - 15) {
                        hero.setPosition(hero.getX(), rock1.getY() + rock1.getHeight() + 2);
                    }
                }
                health.setWidth(healthRate);
                gameUtils.setHealthRate(healthRate);
                healthRateText.setText("Health rate: " + String.valueOf(healthRate));
                if (enemyHealthRate >= 0) {
                    enemyHealthRateText.setText("Health rate: " + String.valueOf(enemyHealthRate));
                } else {
                    enemyHealthRateText.setText("Health rate: " + "0");
                    rock2.clearEntityModifiers();
                    rock2.unregisterEntityModifier(moveModifier);

                }
                enemyHealth.setWidth(enemyHealthRate);
                gameUtils.setEnemyLife(LEVEL, enemyHealthRate);

            }

            @Override
            public void reset() {
            }

        });

        return scene;
    }

    @Override
    public void onLoadComplete() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scene.setIgnoreUpdate(true);
            String items[] = new String[] { "Resume", "Back to Main Menu" };
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio2Terrain2Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                        Barrio2Terrain2Activity.this.startActivity(new Intent(Barrio2Terrain2Activity.this, MainMenuActivity.class));
                        finish();
                        break;
                    }
                }
            });
            pauseAlert.show();
        }
        return super.onKeyDown(keyCode, event);

    }
}
