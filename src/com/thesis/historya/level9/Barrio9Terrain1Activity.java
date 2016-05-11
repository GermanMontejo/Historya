package com.thesis.historya.level9;

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
import org.anddev.andengine.entity.modifier.FadeInModifier;
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
import com.thesis.historya.GameUtils;
import com.thesis.historya.GameWon;
import com.thesis.historya.MainMenuActivity;
import com.thesis.historya.R;

@SuppressLint("NewApi")
public class Barrio9Terrain1Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;
    BuildableBitmapTextureAtlas hermitTexture;

    // Textures
    private BitmapTextureAtlas crystalTexture;
    private BitmapTextureAtlas crystal2Texture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;

    // TextureRegions
    private TextureRegion crystalTextureRegion;
    private TextureRegion crystal2TextureRegion;
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

    AnimatedSprite sword;

    float heroX = 0;
    float heroY = 0;

    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;
    AnimatedSprite hermit;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;

    float hermitX = CAMERA_WIDTH / 2;
    float hermitY = 50;

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
    private static final String LEVEL = "LEVEL1";
    private boolean isLevelPassed = false;
    private String riddle9 = "";
    private Scene scene;
    Music backgroundMusic;
    private Sprite crystal, crystal2;
    protected Random rand;
    protected int randomNumber;
    protected ArrayList<String> riddleAnswers;
    protected String riddleAnswer;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        if (getIntent().getBooleanExtra("fromB8Terrain1", false)) {
            heroX = 0;
            getIntent().putExtra("fromB8Terrain1", false);
        }
        heroY = gameUtils.getY();
        playerDirection = direction.DOWN;
        riddlePopUp = new AlertDialog.Builder(Barrio9Terrain1Activity.this);
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "fishermans_prayer.mp3");
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
        gameUtils.setAnswer(GameUtils.Riddles.RIDDLE9.toString(), "Pearl of the Orient");
        // riddle9 = getBaseContext().getString(R.string.riddle9);
        if (gameUtils.isHintOn()) {
            Toast.makeText(getBaseContext(), "Answer the riddle and you will be able to go home to your current timeline.", Toast.LENGTH_LONG).show();
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

        crystalTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        crystalTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(crystalTexture, getBaseContext(), "crystal2.png", 0, 0);

        crystal2Texture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        crystal2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(crystalTexture, getBaseContext(), "crystal2.png", 0, 0);

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "grass_tile16.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(crystalTexture);
        fsEngine.getTextureManager().loadTexture(crystal2Texture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
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
        hermit = new AnimatedSprite(hermitX, hermitY, hermitTextureRegion);

        crystal = new Sprite(hermit.getX() - crystalTexture.getWidth() - 5, hermit.getY(), crystalTextureRegion);
        crystal2 = new Sprite(hermit.getX() + hermit.getWidth(), hermit.getY(), crystal2TextureRegion);
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
        scene.getLastChild().attachChild(crystal);
        scene.getLastChild().attachChild(crystal2);
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(hero);
        scene.getLastChild().attachChild(hermit);

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
                        for (String answers : Barrio9Terrain1Activity.this.getResources().getStringArray(R.array.answers)) {
                            riddleAnswers.add(answers);
                        }
                        List<String> riddles = new ArrayList<String>();
                        for (String riddle : Barrio9Terrain1Activity.this.getResources().getStringArray(R.array.riddles)) {
                            riddles.add(riddle);
                        }
                        riddle9 = riddles.get(randomNumber);
                        riddleAnswer = riddleAnswers.get(randomNumber);

                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                etRiddle = new EditText(getBaseContext());
                                if (!gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE9.toString())) {
                                    Toast.makeText(getBaseContext(), "I am Galan, chief shaman. I am the one who brought you here out of a spell I invented.",
                                            Toast.LENGTH_LONG).show();
                                    Toast.makeText(getBaseContext(), "So far it worked as expected, but before I bring you back, you need to answer this riddle...",
                                            Toast.LENGTH_LONG).show();
                                    Handler handler = new Handler();
                                    handler.postDelayed(new Runnable() {

                                        @Override
                                        public void run() {
                                            new AlertDialog.Builder(Barrio9Terrain1Activity.this).setTitle("Riddle #9").setMessage(riddle9)
                                            .setView(etRiddle).setPositiveButton("OK", new DialogInterface.OnClickListener() {

                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    String answer = etRiddle.getText().toString();
                                                    boolean isAnswerCorrect = riddleAnswer.equalsIgnoreCase(answer) ? true : false;
                                                    if (isAnswerCorrect) {
                                                        Toast.makeText(getBaseContext(), "Okay, I'll be true to my word and will bring you back to your timeline.",
                                                                Toast.LENGTH_LONG).show();
                                                        hermit.registerEntityModifier(new FadeInModifier(4f));
                                                        gameUtils.setQuestionAnswered(GameUtils.Riddles.RIDDLE9.toString(), true);
                                                        gameUtils.gameDone(scene);
                                                        gameUtils.setLevelPassed(LEVEL);
                                                        Handler handler = new Handler();
                                                        handler.postDelayed(new Runnable() {

                                                            @Override
                                                            public void run() {
                                                                Barrio9Terrain1Activity.this.startActivity(new Intent(Barrio9Terrain1Activity.this, GameWon.class));
                                                                Barrio9Terrain1Activity.this.finish();
                                                            }

                                                        }, 5000);
                                                    } else {
                                                        Toast.makeText(getBaseContext(), "Come back next time when you know the answer...", Toast.LENGTH_LONG).show();
                                                        gameUtils.setQuestionAnswered(GameUtils.Riddles.RIDDLE9.toString(), false);
                                                    }
                                                }
                                            }).show();
                                        }

                                    }, 5000);

                                } else {
                                    Toast.makeText(getBaseContext(), "Well done my friend. Now it's time to bring you back to your current timeline.", Toast.LENGTH_LONG).show();
                                    scene.detachChild(right);
                                    isLevelPassed = gameUtils.getLevelPassed(LEVEL);
                                }
                            }
                        });
                    } else if (hero.collidesWith(crystal) || hero.collidesWith(crystal2)) {
                        Toast.makeText(getBaseContext(), "I can feel the crystal vibrating...", Toast.LENGTH_LONG).show();
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
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(right) && playerDirection.equals(direction.RIGHT) && !gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE1.toString())) {
                    hero.setPosition(right.getX() - 50, hero.getY());
                }

                if (hero.collidesWith(hermit) && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(hermit.getX() + 3, hero.getY());
                } else if (hero.collidesWith(hermit) && playerDirection.equals(direction.UP) && hero.getX() <= hermit.getX()) {
                    hero.setPosition(hero.getX(), hermit.getY() + hermit.getHeight());
                } else if (hero.collidesWith(hermit) && playerDirection.equals(direction.LEFT)) {
                    hero.setPosition(hermit.getX(), hero.getY());
                    // alertHandler.sendEmptyMessage(0);
                } else if (hero.collidesWith(crystal)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(crystal.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(crystal.getX() + crystal.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= crystal.getY() && hero.getX() + hero.getWidth() >= crystal.getX() + 5
                            && hero.getX() <= crystal.getX() + crystal.getWidth() - 15) {
                        hero.setPosition(hero.getX(), crystal.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= crystal.getY() + crystal.getHeight() && hero.getX() + hero.getWidth() >= crystal.getX() + 5
                            && hero.getX() <= crystal.getX() + crystal.getWidth() - 15) {
                        hero.setPosition(hero.getX(), crystal.getY() + crystal.getHeight() + 2);
                    }
                } else if (hero.collidesWith(crystal2)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(crystal2.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(crystal2.getX() + crystal2.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= crystal2.getY() && hero.getX() + hero.getWidth() >= crystal2.getX() + 5
                            && hero.getX() <= crystal2.getX() + crystal2.getWidth() - 15) {
                        hero.setPosition(hero.getX(), crystal2.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= crystal2.getY() + crystal2.getHeight() && hero.getX() + hero.getWidth() >= crystal2.getX() + 5
                            && hero.getX() <= crystal2.getX() + crystal2.getWidth() - 15) {
                        hero.setPosition(hero.getX(), crystal2.getY() + crystal2.getHeight() + 2);
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
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio9Terrain1Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        Barrio9Terrain1Activity.this.startActivity(new Intent(Barrio9Terrain1Activity.this, MainMenuActivity.class));
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
