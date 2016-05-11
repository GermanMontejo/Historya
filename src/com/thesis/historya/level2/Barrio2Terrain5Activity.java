package com.thesis.historya.level2;

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
import com.thesis.historya.level3.Barrio3Terrain1Activity;

@SuppressLint("NewApi")
public class Barrio2Terrain5Activity extends BaseGameActivity {
    protected static final String LEVEL = "LEVEL2";
    BuildableBitmapTextureAtlas heroTexture;
    BuildableBitmapTextureAtlas hermitTexture;

    // Textures
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;
    private BitmapTextureAtlas cave1Texture;
    private BuildableBitmapTextureAtlas goatTexture;

    // TextureRegions
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TextureRegion talkTextureRegion;
    private TiledTextureRegion hermitTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion cave1TextureRegion;
    private TextureRegion swordButtonTextureRegion;
    private TextureRegion grassTextureRegion;
    private TiledTextureRegion goatTextureRegion;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, cave1, swordButton;

    AnimatedSprite sword;

    float heroX = 0;
    float heroY = 0;
    float hermitX = 0;
    float hermitY = 0;
    float heroFromY = 0;
    float heroFromX = 0;
    private final float goatX = 10;
    private final float goatY = 30;
    AnimatedSprite hero;
    AnimatedSprite hermit;
    private AnimatedSprite goat;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;
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

    private BitmapTextureAtlas fontTexture;
    private Font font;
    private ChangeableText healthRateText;
    int healthRate = 100;
    private Rectangle health;
    private Scene scene;
    private String riddle2 = "";
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
        if (getIntent().getBooleanExtra("fromB2Terrain4", false)) {
            heroY = 5;
            getIntent().putExtra("fromB2Terrain4", false);
        }
        heroX = gameUtils.getX();
        playerDirection = direction.DOWN;
        riddlePopUp = new AlertDialog.Builder(Barrio2Terrain5Activity.this);
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "port_town.mp3");
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
        gameUtils.setAnswer(GameUtils.Riddles.RIDDLE2.toString(), "1521");
        // riddle2 = getBaseContext().getString(R.string.riddle2);

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
        hermitTexture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        hermitTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hermitTexture, this, "hermit2.png", 4, 2);
        try {
            heroTexture.build(new BlackPawnTextureBuilder(2));
            hermitTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        goatTexture = new BuildableBitmapTextureAtlas(256, 64, TextureOptions.DEFAULT);
        goatTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(goatTexture, this, "goat.png", 4, 2);
        try {
            goatTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);
        cave1Texture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        cave1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(cave1Texture, this, "cave1.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "grass_tile8.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(goatTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(cave1Texture);
        fsEngine.getTextureManager().loadTexture(grassTexture);
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

        cave1 = new Sprite(CAMERA_WIDTH - cave1Texture.getWidth(), -120 + cave1Texture.getHeight(), cave1TextureRegion);

        hermitX = cave1.getX() + 10;
        hermitY = cave1.getY() + cave1.getHeight() - 10;

        goat = new AnimatedSprite(goatX, goatY, goatTextureRegion);
        goat.animate(new long[] { 230, 230, 230, 230 }, 0, 3, true);

        hermit = new AnimatedSprite(hermitX, hermitY, hermitTextureRegion);
        final PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), true);
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

        PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        // PhysicsFactory.createBoxBody(physicsWorld, right,
        // BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        scene.getLastChild().attachChild(left);
        scene.getLastChild().attachChild(ground);
        scene.getLastChild().attachChild(right);

        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);

        scene.getLastChild().attachChild(background);
        scene.getLastChild().attachChild(goat);
        scene.getLastChild().attachChild(cave1);
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(hero);
        scene.getLastChild().attachChild(hermit);

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
                        if (hero.getY() <= -10) {
                            dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                            Intent b2Terrain5Intent = new Intent(Barrio2Terrain5Activity.this, Barrio2Terrain4Activity.class);
                            b2Terrain5Intent.putExtra("heroFromT5X", hero.getX());
                            b2Terrain5Intent.putExtra("heroFromT5Y", hero.getY());
                            b2Terrain5Intent.putExtra("fromB2Terrain5", true);
                            startActivity(b2Terrain5Intent);
                            Barrio2Terrain5Activity.this.finish();
                        } else if (hero.getX() >= CAMERA_WIDTH && gameUtils.getLevelPassed(LEVEL)) {
                            dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                            Intent b3Terrain1Intent = new Intent(Barrio2Terrain5Activity.this, Barrio3Terrain1Activity.class);
                            b3Terrain1Intent.putExtra("heroFromB2T5X", hero.getX());
                            b3Terrain1Intent.putExtra("heroFromB2T5Y", hero.getY());
                            b3Terrain1Intent.putExtra("fromB2Terrain5", true);
                            Barrio2Terrain5Activity.this.startActivity(b3Terrain1Intent);
                            Barrio2Terrain5Activity.this.finish();
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
                        randomNumber = rand.nextInt(17);
                        riddleAnswers = new ArrayList<String>();
                        for (String answers : Barrio2Terrain5Activity.this.getResources().getStringArray(R.array.answers)) {
                            riddleAnswers.add(answers);
                        }
                        List<String> riddles = new ArrayList<String>();
                        for (String riddle : Barrio2Terrain5Activity.this.getResources().getStringArray(R.array.riddles)) {
                            riddles.add(riddle);
                        }
                        riddle2 = riddles.get(randomNumber);
                        riddleAnswer = riddleAnswers.get(randomNumber);

                        runOnUiThread(new Runnable() {

                            private boolean isLevelPassed;

                            @Override
                            public void run() {
                                etRiddle = new EditText(getBaseContext());
                                if (!gameUtils.getLevelPassed(LEVEL)) {
                                    new AlertDialog.Builder(Barrio2Terrain5Activity.this).setTitle("Riddle #2").setMessage(riddle2)
                                            .setView(etRiddle).setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String answer = etRiddle.getText().toString();
                                                    boolean isAnswerCorrect = riddleAnswer.equalsIgnoreCase(answer) ? true : false;
                                                    if (isAnswerCorrect) {
                                                        Toast.makeText(getBaseContext(), "Smart young lad. I shall allow you to pass. Go to the east now, young adventurer!", Toast.LENGTH_LONG).show();
                                                        gameUtils.setQuestionAnswered(GameUtils.Riddles.RIDDLE2.toString(), true);
                                                        heroScore += 100;
                                                        scene.detachChild(right);
                                                        gameUtils.setLevelPassed(LEVEL);
                                                    } else {
                                                        Toast.makeText(getBaseContext(), "Come back next time when you know the answer...", Toast.LENGTH_LONG).show();
                                                        gameUtils.setQuestionAnswered(GameUtils.Riddles.RIDDLE2.toString(), false);
                                                    }
                                                }
                                            }).show();
                                } else {
                                    Toast.makeText(getBaseContext(), "Go to the East now, young adventurer. New riddles are waiting for you on the next levels...", Toast.LENGTH_LONG).show();
                                    scene.detachChild(right);
                                    isLevelPassed = gameUtils.getLevelPassed(LEVEL);
                                }
                            }
                        });
                    }
                    if (hero.collidesWith(goat)) {
                        Toast.makeText(getBaseContext(), "Meh meh...", Toast.LENGTH_LONG).show();
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
                if (hero.collidesWith(left)) {
                    hero.setPosition(left.getX() + 1, hero.getY());
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(right) && playerDirection.equals(direction.RIGHT) && !gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE2.toString())) {
                    hero.setPosition(right.getX() - 50, hero.getY());
                }

                if (hero.collidesWith(cave1) && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(cave1.getX() - 45, hero.getY());
                } else if (hero.collidesWith(cave1) && playerDirection.equals(direction.UP)) {
                    hero.setPosition(hero.getX(), cave1.getY() + cave1.getHeight());
                }
                if (hero.collidesWith(hermit) && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(hermit.getX() + 3, hero.getY());

                } else if (hero.collidesWith(hermit) && playerDirection.equals(direction.UP) && hero.getX() <= hermit.getX()) {
                    hero.setPosition(hero.getX(), hermit.getY() + hermit.getHeight());

                } else if (hero.collidesWith(hermit) && playerDirection.equals(direction.LEFT)) {
                    hero.setPosition(hermit.getX(), hero.getY());
                    // alertHandler.sendEmptyMessage(0);
                }
                if (hero.collidesWith(goat)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(goat.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(goat.getX() + goat.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= goat.getY() && hero.getX() + hero.getWidth() >= goat.getX() + 5
                            && hero.getX() <= goat.getX() + goat.getWidth() - 15) {
                        hero.setPosition(hero.getX(), goat.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= goat.getY() + goat.getHeight() && hero.getX() + hero.getWidth() >= goat.getX() + 5
                            && hero.getX() <= goat.getX() + goat.getWidth() - 15) {
                        hero.setPosition(hero.getX(), goat.getY() + goat.getHeight() + 2);
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
    public void onLoadComplete() {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scene.setIgnoreUpdate(true);
            String items[] = new String[] { "Resume", "Back to Main Menu" };
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio2Terrain5Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        dbHelper.updateScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), heroScore);
                        Barrio2Terrain5Activity.this.startActivity(new Intent(Barrio2Terrain5Activity.this, MainMenuActivity.class));
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
