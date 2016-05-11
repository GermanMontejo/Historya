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
import com.thesis.historya.MainMenuActivity;

public class Barrio8Terrain2Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    // Textures
    private BitmapTextureAtlas shelfTexture;
    private BitmapTextureAtlas tableTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BuildableBitmapTextureAtlas man2Texture;
    private BitmapTextureAtlas backgroundTexture;

    // TextureRegions
    private TextureRegion shelfTextureRegion;
    private TextureRegion tableTextureRegion;
    private TiledTextureRegion man2TextureRegion;
    private TextureRegion talkTextureRegion;
    private TextureRegion backgroundTextureRegion;
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, swordButton;

    AnimatedSprite sword;
    private AnimatedSprite man2;

    float backgroundX = 0;
    float backgroundY = 0;
    Sprite background;

    float heroX = 0;
    float heroY = 0;
    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;

    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;

    private final float tableX = CAMERA_WIDTH / 2 - 50;
    private final float tableY = CAMERA_HEIGHT / 2 - 50;

    private final float shelfX = CAMERA_WIDTH - 100;
    private final float shelfY = 10;

    Sprite shelf;
    Sprite table;

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

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        heroY = gameUtils.getY();
        if (getIntent().getBooleanExtra("fromB8Terrain1", false)) {
            heroX = gameUtils.getX();
            heroY = CAMERA_HEIGHT - 40;
            getIntent().putExtra("fromB8Terrain1", false);
        } else if (getIntent().getBooleanExtra("fromB8Terrain3", false)) {
            heroX = 10;
            getIntent().putExtra("fromB8Terrain3", false);
        } else {
            heroX = CAMERA_WIDTH - 50;
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
        if (gameUtils.isHintOn()) {
            Toast.makeText(getBaseContext(), "A monster lives in this house. Try to find it and kill it with one slash of your sword.", Toast.LENGTH_LONG).show();
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

        try {
            heroTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        shelfTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        shelfTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(shelfTexture, getBaseContext(), "bookshelves.png", 0, 0);

        tableTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        tableTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tableTexture, getBaseContext(), "table_with_foods.png", 0, 0);

        backgroundTexture = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        backgroundTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(backgroundTexture, getBaseContext(), "house_background.png", 0, 0);

        man2Texture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        man2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(man2Texture, getBaseContext(), "man5.png", tileColumn, 2);
        try {
            man2Texture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(man2Texture);
        fsEngine.getTextureManager().loadTexture(shelfTexture);
        fsEngine.getTextureManager().loadTexture(tableTexture);
        fsEngine.getTextureManager().loadTexture(backgroundTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
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
        hero = new AnimatedSprite(heroX, heroY, heroTextureRegion);

        background = new Sprite(backgroundX, backgroundY, backgroundTextureRegion);

        table = new Sprite(tableX, tableY, tableTextureRegion);

        shelf = new Sprite(shelfX, shelfY, shelfTextureRegion);

        man2 = new AnimatedSprite(40, 50, man2TextureRegion);
        man2.animate(2000);
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
        scene.getLastChild().attachChild(shelf);
        scene.getLastChild().attachChild(table);
        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);

        scene.getLastChild().attachChild(man2);
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
                            Intent b8Terrain3Intent = new Intent(Barrio8Terrain2Activity.this, Barrio8Terrain3Activity.class);
                            b8Terrain3Intent.putExtra("heroFromT2X", hero.getX());
                            b8Terrain3Intent.putExtra("heroFromT2Y", hero.getY());
                            b8Terrain3Intent.putExtra("fromB8Terrain2", true);
                            startActivity(b8Terrain3Intent);
                            Barrio8Terrain2Activity.this.finish();
                        } else if (hero.getX() >= CAMERA_WIDTH && gameUtils.doesTheHeroHaveTheKey()) {
                            Intent b8Terrain4Intent = new Intent(Barrio8Terrain2Activity.this, Barrio8Terrain4Activity.class);
                            b8Terrain4Intent.putExtra("heroFromT2X", hero.getX());
                            b8Terrain4Intent.putExtra("heroFromT2Y", hero.getY());
                            b8Terrain4Intent.putExtra("fromB8Terrain2", true);
                            startActivity(b8Terrain4Intent);
                            Barrio8Terrain2Activity.this.finish();
                        } else if (hero.getY() >= CAMERA_HEIGHT) {
                            if (gameUtils.isEnemyDead(LEVEL)) {
                                Intent b8Terrain1Intent = new Intent(Barrio8Terrain2Activity.this, Barrio8Terrain1Activity.class);
                                b8Terrain1Intent.putExtra("heroFromT2X", hero.getX());
                                b8Terrain1Intent.putExtra("heroFromT2Y", hero.getY());
                                b8Terrain1Intent.putExtra("fromB8Terrain2", true);
                                startActivity(b8Terrain1Intent);
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
                    if (hero.collidesWith(man2)) {
                        if (!gameUtils.isEnemyDead(LEVEL)) {
                            Toast.makeText(getBaseContext(), "Feel free to grab any food you want, after all it's my son's birthday! Ha-ha-ha. Oh and when you have time, please tell me wife to hurry up so we can dine in together, she's in the east side of this house...", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Thank you for killing that monster. I have long thought of killing it but the thought that my wife will hate me forever has stopped me from doing it.", Toast.LENGTH_LONG).show();
                            Toast.makeText(getBaseContext(), "I've unlocked the door. You can go out now.", Toast.LENGTH_LONG).show();
                            ground.detachSelf();
                        }

                    } else if (hero.collidesWith(table)) {
                        Toast.makeText(getBaseContext(), "There are assorted foods on the table. \n *Gobbles some food*.", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(shelf)) {
                        Toast.makeText(getBaseContext(), "The books are arranged by title.", Toast.LENGTH_LONG).show();
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

                if (hero.collidesWith(man2) && hero.getX() <= man2.getX() - 2 && (hero.getY() + hero.getHeight() >= man2.getY() + 10) && playerDirection.equals(direction.LEFT)) {
                    hero.setPosition(man2.getX() - 2, hero.getY());
                } else if (hero.collidesWith(man2) && (hero.getX() + hero.getWidth() >= man2.getX() - 5) && (hero.getY() + hero.getHeight() >= man2.getY() + 10)
                        && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(man2.getX() - (hero.getWidth() - 5), hero.getY());
                } else if (hero.collidesWith(man2) && playerDirection.equals(direction.UP) && hero.getY() <= man2.getY() + man2.getHeight() && hero.getX() <= man2.getX() - 5
                        && hero.getX() >= man2.getX() - 40) {
                    hero.setPosition(hero.getX(), man2.getY() + man2.getHeight() + 1);
                } else if (hero.collidesWith(man2) && playerDirection.equals(direction.DOWN) && (hero.getY() + hero.getHeight() <= man2.getY() + 30) && hero.getX() <= man2.getX() - 5
                        && hero.getX() >= man2.getX() - 40) {
                    hero.setPosition(hero.getX(), man2.getY() - 40);
                }
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 57);
                } else if (hero.collidesWith(ground)) {
                    if ((hero.getX() >= CAMERA_WIDTH / 2 - 50) && (hero.getX() <= CAMERA_WIDTH / 2 + 30) && gameUtils.isEnemyDead(LEVEL)) {

                    } else {
                        hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                    }

                } else if (hero.collidesWith(left1)) {
                    hero.setPosition(left1.getX() + 10, hero.getY());
                } else if (hero.collidesWith(left2)) {
                    hero.setPosition(left2.getX() + 10, hero.getY());
                } else if (hero.collidesWith(right1)) {
                    hero.setPosition(right1.getX() - 55, hero.getY());
                } else if (hero.collidesWith(right2)) {
                    hero.setPosition(right2.getX() - 55, hero.getY());
                } else if (hero.collidesWith(table)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(table.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(table.getX() + table.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= table.getY() && hero.getX() + hero.getWidth() >= table.getX() + 5
                            && hero.getX() <= table.getX() + table.getWidth() - 15) {
                        hero.setPosition(hero.getX(), table.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= table.getY() + table.getHeight() && hero.getX() + hero.getWidth() >= table.getX() + 5
                            && hero.getX() <= table.getX() + table.getWidth() - 15) {
                        hero.setPosition(hero.getX(), table.getY() + table.getHeight() + 2);
                    }
                } else if (hero.collidesWith(shelf)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(shelf.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(shelf.getX() + shelf.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= shelf.getY() + shelf.getHeight() && hero.getX() + hero.getWidth() >= shelf.getX() + 5
                            && hero.getX() <= shelf.getX() + shelf.getWidth() - 15) {
                        hero.setPosition(hero.getX(), shelf.getY() + shelf.getHeight() + 2);
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
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio8Terrain2Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        Barrio8Terrain2Activity.this.startActivity(new Intent(Barrio8Terrain2Activity.this, MainMenuActivity.class));
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
