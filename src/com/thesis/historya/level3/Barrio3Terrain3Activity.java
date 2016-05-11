package com.thesis.historya.level3;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

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
import org.anddev.andengine.entity.modifier.FadeInModifier;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
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

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.EditText;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.splunk.mint.Mint;
import com.thesis.historya.DatabaseHelper;
import com.thesis.historya.GameUtils;
import com.thesis.historya.MainMenuActivity;
import com.thesis.historya.R;
import com.thesis.historya.level4.Barrio4Terrain1Activity;

@SuppressLint("NewApi")
public class Barrio3Terrain3Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;
    BuildableBitmapTextureAtlas hermitTexture;

    // Textures
    private BitmapTextureAtlas portalTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas symbolTexture;
    private BitmapTextureAtlas fireMageTexture;
    private BitmapTextureAtlas lightningMageTexture;
    private BitmapTextureAtlas waterMageTexture;
    private BitmapTextureAtlas natureMageTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;

    // TextureRegions
    private TextureRegion portalTextureRegion;
    private TextureRegion symbolTextureRegion;
    private TextureRegion fireMageTextureRegion;
    private TextureRegion lightningMageTextureRegion;
    private TextureRegion waterMageTextureRegion;
    private TextureRegion natureMageTextureRegion;
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TextureRegion talkTextureRegion;
    private TiledTextureRegion hermitTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;
    private TextureRegion grassTextureRegion;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, swordButton;
    Sprite waterMage, fireMage, lightningMage, natureMage;
    AnimatedSprite sword;

    float heroX = 0;
    float heroY = 0;
    float hermitX = 0;
    float hermitY = 0;
    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;
    AnimatedSprite hermit;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;

    float portalX = CAMERA_WIDTH / 2 - 20;
    float portalY = CAMERA_HEIGHT - 70;
    private float symbolX;
    private float symbolY;

    private final float lightningMageX = CAMERA_WIDTH - 320;
    private final float lightningMageY = 80;
    private final float natureMageX = CAMERA_WIDTH - 320;
    private final float natureMageY = 180;
    private final float waterMageX = CAMERA_WIDTH - 150;
    private final float waterMageY = 80;
    private final float fireMageX = CAMERA_WIDTH - 150;
    private final float fireMageY = 180;
    private Camera camera;
    private direction playerDirection;
    AnimatedSprite slashSprite;
    long[] durations = new long[4];
    AlertDialog.Builder riddlePopUp;
    private enum direction {
        UP, DOWN, LEFT, RIGHT
    }

    private SharedPreferences positionPref;
    private Editor editor;
    GameUtils gameUtils;
    EditText etRiddle;
    private Sound running;
    private Sound slash;
    private Sprite portal;

    private BitmapTextureAtlas fontTexture;
    private Font font;
    private ChangeableText healthRateText;
    int healthRate = 100;
    private Rectangle health;
    private static final String LEVEL = "LEVEL3";
    private boolean isLevelPassed = false;
    private String riddle3 = "";
    private Scene scene;
    private Sprite symbol;
    Music backgroundMusic;

    int heroScore = 0;
    DatabaseHelper dbHelper;
    SQLiteDatabase sqlDb;
    protected Random rand;
    protected int randomNumber;
    protected ArrayList<String> riddleAnswers;
    protected String riddleAnswer;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        if (getIntent().getBooleanExtra("fromB3Terrain2", false)) {
            heroX = 10;
            getIntent().putExtra("fromB3Terrain2", false);
        } else {
            heroX = gameUtils.getX();
        }
        heroY = gameUtils.getY();
        playerDirection = direction.DOWN;
        riddlePopUp = new AlertDialog.Builder(Barrio3Terrain3Activity.this);
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "ritual.mp3");
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
        gameUtils.setAnswer(GameUtils.Riddles.RIDDLE3.toString(), "FreeMasonry");
        // riddle3 = getBaseContext().getString(R.string.riddle3);

        dbHelper = new DatabaseHelper(this);
        sqlDb = dbHelper.getWritableDatabase();
        heroScore = dbHelper.retrieveScore(sqlDb, getBaseContext(), gameUtils.retrieveUsername());
        if (gameUtils.isHintOn()) {
            Toast.makeText(getBaseContext(), "Once you finish answering the riddle from the grand mage, ask each elemental mage and maybe they'll bestow you some magical blessing.", Toast.LENGTH_LONG).show();
        }
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
        hermitTexture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        hermitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hermitTexture, this, "hermit2.png", 4, 2);
        try {
            heroTexture.build(new BlackPawnTextureBuilder(2));
            hermitTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        portalTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        portalTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(portalTexture, getBaseContext(), "portal.png", 0, 0);

        symbolTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        symbolTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(symbolTexture, getBaseContext(), "symbol.png", 0, 0);

        natureMageTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        natureMageTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(natureMageTexture, getBaseContext(), "nature_mage.png", 0, 0);

        lightningMageTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        lightningMageTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(lightningMageTexture, getBaseContext(), "lightning_mage.png", 0, 0);

        fireMageTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        fireMageTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(fireMageTexture, getBaseContext(), "fire_mage.png", 0, 0);

        waterMageTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        waterMageTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(waterMageTexture, getBaseContext(), "water_mage.png", 0, 0);

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "grass_tile11.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(portalTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(waterMageTexture);
        fsEngine.getTextureManager().loadTexture(fireMageTexture);
        fsEngine.getTextureManager().loadTexture(lightningMageTexture);
        fsEngine.getTextureManager().loadTexture(natureMageTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(grassTexture);
        fsEngine.getTextureManager().loadTexture(symbolTexture);
        fsEngine.getTextureManager().loadTexture(heroTexture);
        fsEngine.getTextureManager().loadTexture(hermitTexture);
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

        hermitX = CAMERA_WIDTH / 2;
        hermitY = 50;

        waterMage = new Sprite(waterMageX, waterMageY, waterMageTextureRegion);
        fireMage = new Sprite(fireMageX, fireMageY, fireMageTextureRegion);
        lightningMage = new Sprite(lightningMageX, lightningMageY, lightningMageTextureRegion);
        natureMage = new Sprite(natureMageX, natureMageY, natureMageTextureRegion);

        portal = new Sprite(portalX, portalY, portalTextureRegion);
        hermit = new AnimatedSprite(hermitX, hermitY, hermitTextureRegion);
        symbolX = hermit.getX() - (symbolTexture.getWidth() / 2) + 13;
        symbolY = 115;
        symbol = new Sprite(symbolX, symbolY, symbolTextureRegion);
        symbol.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new ColorModifier(3f, 0f, 1f, 0f, 0f, 0f, 0f), new ColorModifier(3f, 0f, 0f, 0f, 1f, 0f, 0f), new ColorModifier(3f, 0f, 0f, 0f, 0f, 0f, 1f), new ColorModifier(3f, 0f, 0f, 0f, 1f, 0f, 1f))));
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

        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

        PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        // PhysicsFactory.createBoxBody(physicsWorld, right,
        // BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        scene.getLastChild().attachChild(roof);
        scene.getLastChild().attachChild(left);
        // scene.getLastChild().attachChild(right);
        scene.getLastChild().attachChild(ground);
        scene.getLastChild().attachChild(right);

        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);

        scene.getLastChild().attachChild(background);
        scene.getLastChild().attachChild(symbol);
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(hero);
        scene.getLastChild().attachChild(hermit);
        scene.getLastChild().attachChild(waterMage);
        scene.getLastChild().attachChild(lightningMage);
        scene.getLastChild().attachChild(fireMage);
        scene.getLastChild().attachChild(natureMage);


        if (!hermit.isAnimationRunning()) {
            hermit.animate(new long[] { 220, 220, 220, 220, 220, 220, 220, 220 }, 0, 7, true);
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
                            Intent bTerrain2Intent = new Intent(Barrio3Terrain3Activity.this, Barrio3Terrain2Activity.class);
                            bTerrain2Intent.putExtra("heroFromT3X", hero.getX());
                            bTerrain2Intent.putExtra("heroFromT3Y", hero.getY());
                            bTerrain2Intent.putExtra("fromB1Terrain3", true);
                            startActivity(bTerrain2Intent);
                            Barrio3Terrain3Activity.this.finish();
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

        Sprite sword_button = new Sprite((CAMERA_WIDTH / 2 + swordButtonTexture.getWidth()), CAMERA_HEIGHT - 100, swordButtonTextureRegion) {

            @Override
            public boolean onAreaTouched(TouchEvent pSceneTouchEvent, float pTouchAreaLocalX, float pTouchAreaLocalY) {
                switch (pSceneTouchEvent.getAction()) {
                case TouchEvent.ACTION_DOWN:

                    if (playerDirection.equals(direction.LEFT)) {
                        hero.animate(new long[] { 140, 140, 140, 140 }, 16, 19, true);
                        if (gameUtils.isSoundOn()) {
                            slash.play();
                        }

                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.animate(new long[] { 140, 140, 140, 140 }, 20, 23, true);
                        if (gameUtils.isSoundOn()) {
                            slash.play();
                        }
                    }
                    break;
                case TouchEvent.ACTION_UP:
                    // sword.unregisterEntityModifier(entityModifier);
                    hero.stopAnimation();
                    if (playerDirection.equals(direction.LEFT)) {
                        hero.setCurrentTileIndex(1, 1);
                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.setCurrentTileIndex(1, 2);
                    }
                    slash.stop();
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
                    if (hero.collidesWith(hermit)) {

                        rand = new Random();
                        randomNumber = rand.nextInt(18);
                        riddleAnswers = new ArrayList<String>();
                        for (String answers : Barrio3Terrain3Activity.this.getResources().getStringArray(R.array.answers)) {
                            riddleAnswers.add(answers);
                        }
                        List<String> riddles = new ArrayList<String>();
                        for (String riddle : Barrio3Terrain3Activity.this.getResources().getStringArray(R.array.riddles)) {
                            riddles.add(riddle);
                        }
                        riddle3 = riddles.get(randomNumber);
                        riddleAnswer = riddleAnswers.get(randomNumber);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                etRiddle = new EditText(getBaseContext());
                                if (!gameUtils.getLevelPassed(LEVEL)) {
                                    new AlertDialog.Builder(Barrio3Terrain3Activity.this).setTitle("Riddle #3").setMessage(riddle3)
                                            .setView(etRiddle).setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String answer = etRiddle.getText().toString();
                                                    boolean isAnswerCorrect = riddleAnswer.equalsIgnoreCase(answer) ? true : false;
                                                    if (isAnswerCorrect) {
                                                        heroScore += 100;
                                                        Toast.makeText(getBaseContext(), "You got the correct answer young lad! Go to the portal now for your next destination young adventurer.",
                                                                Toast.LENGTH_LONG).show();
                                                        gameUtils.setLevelPassed(LEVEL);
                                                        hermit.registerEntityModifier(new FadeInModifier(4f));
                                                        gameUtils.setQuestionAnswered(GameUtils.Riddles.RIDDLE3.toString(), true);
                                                        portal = new Sprite(portalX, portalY, portalTextureRegion);
                                                        portal.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(3f, 1f, 1.2f, 1f, 1.2f)));
                                                        scene.getLastChild().attachChild(portal);
                                                    } else {
                                                        Toast.makeText(getBaseContext(), "Come back next time when you know the answer...", Toast.LENGTH_LONG).show();
                                                        gameUtils.setQuestionAnswered(GameUtils.Riddles.RIDDLE3.toString(), false);
                                                    }
                                                }
                                            }).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "I wish you luck on your journey!", Toast.LENGTH_LONG).show();
                                    isLevelPassed = gameUtils.getLevelPassed(LEVEL);
                                }
                            }
                        });
                    } else if (hero.collidesWith(waterMage)) {
                        if (gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE3.toString())) {
                            Toast.makeText(getBaseContext(), "You wanna know a little of water magic? I can teach you but please don't tell anyone! *Whispers a water-based spell for conjuring water*", Toast.LENGTH_LONG).show();
                            gameUtils.setLearnSpell("water");
                        } else {
                            Toast.makeText(getBaseContext(), "After the ceremonial magic is completed, my water-mage abilities shall increase...", Toast.LENGTH_LONG).show();
                        }

                    } else if (hero.collidesWith(fireMage)) {
                        if (gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE3.toString())) {
                            Toast.makeText(getBaseContext(), "Hey, thanks for listening man. Next time you run into trouble you can use this fire magic to burn things down. *Whispers a fire-based spell for conjuring fire*", Toast.LENGTH_LONG).show();
                            gameUtils.setLearnSpell("fire");
                        } else {
                            Toast.makeText(getBaseContext(), "I can't wait to burn all the people who underestimated my fire magic!", Toast.LENGTH_LONG).show();
                        }
                    } else if (hero.collidesWith(natureMage)) {
                        if (gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE3.toString())) {
                            Toast.makeText(getBaseContext(), "Be a friend of nature and use this spell to help grow trees. *Whispers a plant-based spell for spawning trees*", Toast.LENGTH_LONG).show();
                            gameUtils.setLearnSpell("nature");
                        } else {
                            Toast.makeText(getBaseContext(), "I shall use my nature magic to spawn new mangroves in this area!", Toast.LENGTH_LONG).show();
                        }
                    } else if (hero.collidesWith(lightningMage)) {
                        if (gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE3.toString())) {
                            Toast.makeText(getBaseContext(), "Take this! Zap! Just kidding kiddo. You can use this spell to conjure lightning to zap people off. *Whispers a lightning-based spell for conjuring electricity*", Toast.LENGTH_LONG).show();
                            gameUtils.setLearnSpell("lightning");
                        } else {
                            Toast.makeText(getBaseContext(), "Be quite will 'ya. I'm trying to concentrate... Do you want to be zapped with my lightning magic?", Toast.LENGTH_LONG).show();
                        }
                    } else if (hero.collidesWith(symbol)) {
                        if (!portal.hasParent()) {
                            Toast.makeText(getBaseContext(), "It's the same symbol the mages used for conjuring a portal. *Chants the spell*", Toast.LENGTH_LONG).show();
                            portal.registerEntityModifier(new LoopEntityModifier(new ScaleModifier(3f, 1f, 1.2f, 1f, 1.2f)));
                            scene.getLastChild().attachChild(portal);
                        } else {
                            portal.detachSelf();
                        }
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

        final EditText etAnswer = new EditText(getBaseContext());

        final Handler alertHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Toast.makeText(getBaseContext(), "Not a priest, not a king but wears different kinds of clothes. What am I?", Toast.LENGTH_LONG).show();
            }

        };
        scene.registerUpdateHandler(new IUpdateHandler() {

            @Override
            public void onUpdate(float pSecondsElapsed) {
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(right) && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(right.getX() - 50, hero.getY());
                }

                if (hero.collidesWith(hermit) && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(hermit.getX() + 3, hero.getY());

                } else if (hero.collidesWith(hermit) && playerDirection.equals(direction.UP) && hero.getX() <= hermit.getX()) {
                    hero.setPosition(hero.getX(), hermit.getY() + hermit.getHeight());

                } else if (hero.collidesWith(hermit) && playerDirection.equals(direction.LEFT)) {
                    hero.setPosition(hermit.getX(), hero.getY());
                    // alertHandler.sendEmptyMessage(0);
                } else if (hero.collidesWith(fireMage)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(fireMage.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(fireMage.getX() + fireMage.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= fireMage.getY() && hero.getX() + hero.getWidth() >= fireMage.getX() + 5
                            && hero.getX() <= fireMage.getX() + fireMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), fireMage.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= fireMage.getY() + fireMage.getHeight() && hero.getX() + hero.getWidth() >= fireMage.getX() + 5
                            && hero.getX() <= fireMage.getX() + fireMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), fireMage.getY() + fireMage.getHeight() + 2);
                    }
                } else if (hero.collidesWith(waterMage)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(waterMage.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(waterMage.getX() + waterMage.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= waterMage.getY() && hero.getX() + hero.getWidth() >= waterMage.getX() + 5
                            && hero.getX() <= waterMage.getX() + waterMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), waterMage.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= waterMage.getY() + waterMage.getHeight() && hero.getX() + hero.getWidth() >= waterMage.getX() + 5
                            && hero.getX() <= waterMage.getX() + waterMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), waterMage.getY() + waterMage.getHeight() + 2);
                    }
                } else if (hero.collidesWith(natureMage)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(natureMage.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(natureMage.getX() + natureMage.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= natureMage.getY() && hero.getX() + hero.getWidth() >= natureMage.getX() + 5
                            && hero.getX() <= natureMage.getX() + natureMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), natureMage.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= natureMage.getY() + natureMage.getHeight() && hero.getX() + hero.getWidth() >= natureMage.getX() + 5
                            && hero.getX() <= natureMage.getX() + natureMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), natureMage.getY() + natureMage.getHeight() + 2);
                    }
                } else if (hero.collidesWith(lightningMage)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(lightningMage.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(lightningMage.getX() + lightningMage.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= lightningMage.getY() && hero.getX() + hero.getWidth() >= lightningMage.getX() + 5
                            && hero.getX() <= lightningMage.getX() + lightningMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), lightningMage.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= lightningMage.getY() + lightningMage.getHeight() && hero.getX() + hero.getWidth() >= lightningMage.getX() + 5
                            && hero.getX() <= lightningMage.getX() + lightningMage.getWidth() - 15) {
                        hero.setPosition(hero.getX(), lightningMage.getY() + lightningMage.getHeight() + 2);
                    }
                } else if (gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE3.toString()) && hero.collidesWith(portal)) {
                    dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                    Intent b4Terrain1Intent = new Intent(Barrio3Terrain3Activity.this, Barrio4Terrain1Activity.class);
                    b4Terrain1Intent.putExtra("heroFromT3X", hero.getX());
                    b4Terrain1Intent.putExtra("heroFromT3Y", hero.getY());
                    b4Terrain1Intent.putExtra("fromB3Terrain3", true);
                    Barrio3Terrain3Activity.this.startActivity(b4Terrain1Intent);
                    Barrio3Terrain3Activity.this.finish();
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
    public void onLoadComplete() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scene.setIgnoreUpdate(true);
            String items[] = new String[] { "Resume", "Back to Main Menu" };
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio3Terrain3Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                        Barrio3Terrain3Activity.this.startActivity(new Intent(Barrio3Terrain3Activity.this, MainMenuActivity.class));
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
