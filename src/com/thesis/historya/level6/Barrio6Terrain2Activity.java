package com.thesis.historya.level6;

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

public class Barrio6Terrain2Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    // Textures
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;
    private BuildableBitmapTextureAtlas manTexture;
    private BuildableBitmapTextureAtlas man2Texture;
    private BuildableBitmapTextureAtlas hermit1Texture;
    private BitmapTextureAtlas houseTexture;
    private BitmapTextureAtlas house2Texture;
    private BitmapTextureAtlas house3Texture;

    // TextureRegions
    private TiledTextureRegion manTextureRegion;
    private TiledTextureRegion man2TextureRegion;
    private TiledTextureRegion hermit1TextureRegion;
    private TextureRegion houseTextureRegion;
    private TextureRegion house2TextureRegion;
    private TextureRegion house3TextureRegion;
    private TextureRegion talkTextureRegion;
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;
    private TextureRegion grassTextureRegion;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, swordButton;

    AnimatedSprite sword;
    private AnimatedSprite man2;
    AnimatedSprite hermit1;

    float heroX = 0;
    float heroY = 0;
    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;
    AnimatedSprite man;
    float manX = 30;
    float manY = 70;

    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;

    private final float houseX = 50;
    private final float house2X = CAMERA_WIDTH / 2 + 100;
    private final float house3X = CAMERA_WIDTH / 2 - 60;

    private final float houseY = 15;
    private final float house2Y = 5;
    private final float house3Y = 10;

    float hermit1X = CAMERA_WIDTH / 2;
    float hermit1Y = CAMERA_HEIGHT - 50;
    private Camera camera;
    private direction playerDirection;
    AnimatedSprite slashSprite;
    long[] durations = new long[4];
    private enum direction {
        UP, DOWN, LEFT, RIGHT
    }

    private static String LEVEL = "LEVEL1";
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
    private Sprite house;
    private Sprite house2;
    private Sprite house3;
    Music backgroundMusic;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        if (getIntent().getBooleanExtra("fromB6Terrain1", false)) {
            heroX = 10;
            getIntent().putExtra("fromB6Terrain1", false);
        } else if (getIntent().getBooleanExtra("fromB6Terrain3", false)) {
            heroX = CAMERA_WIDTH - 50;
        } else {
            heroX = gameUtils.getX();
        }
        heroY = gameUtils.getY();
        playerDirection = direction.DOWN;
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "peaceful_moment.mp3");
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
        gameUtils.setAnswer(GameUtils.Riddles.SECRET_CODE.toString(), "Babaylan");
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

        houseTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        houseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(houseTexture, getBaseContext(), "house2.png", 0, 0);

        house2Texture = new BitmapTextureAtlas(64, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        house2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(house2Texture, getBaseContext(), "house4.png", 0, 0);

        house3Texture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        house3TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(house3Texture, getBaseContext(), "treehouse2.png", 0, 0);

        heroTexture = new BuildableBitmapTextureAtlas(256, 256, TextureOptions.DEFAULT);
        heroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(heroTexture, this, "hero7.png", tileColumn, tileRow);

        try {
            heroTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        man2Texture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        man2TextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(man2Texture, getBaseContext(), "man5.png", tileColumn, 2);
        try {
            man2Texture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        hermit1Texture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
        hermit1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(hermit1Texture, getBaseContext(), "hermit3.png", 4, 2);
        try {
            hermit1Texture.build(new BlackPawnTextureBuilder(2));
        } catch (TextureAtlasSourcePackingException e1) {
            e1.printStackTrace();
        }

        manTexture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.DEFAULT);
        manTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(manTexture, getBaseContext(), "man.png", 4, 2);
        try {
            manTexture.build(new BlackPawnTextureBuilder(2));
        } catch (TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "grass_tile13.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(houseTexture);
        fsEngine.getTextureManager().loadTexture(man2Texture);
        fsEngine.getTextureManager().loadTexture(house2Texture);
        fsEngine.getTextureManager().loadTexture(house3Texture);
        fsEngine.getTextureManager().loadTexture(hermit1Texture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(grassTexture);
        fsEngine.getTextureManager().loadTexture(heroTexture);
        fsEngine.getTextureManager().loadTexture(talkTexture);
        fsEngine.getTextureManager().loadTexture(fontTexture);
        fsEngine.getTextureManager().loadTexture(manTexture);
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
        house = new Sprite(houseX, houseY, houseTextureRegion);
        house2 = new Sprite(house2X, house2Y, house2TextureRegion);
        house3 = new Sprite(house3X, house3Y, house3TextureRegion);
        hero = new AnimatedSprite(heroX, heroY, heroTextureRegion);
        hermit1 = new AnimatedSprite(hermit1X, hermit1Y, hermit1TextureRegion);
        hermit1.animate(2000);
        man = new AnimatedSprite(manX, manY, manTextureRegion);
        man.animate(200);

        man2 = new AnimatedSprite(house2.getX(), house2.getY() + house2.getHeight() + 10, man2TextureRegion);
        man2.animate(2000);
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
        scene.getLastChild().attachChild(man2);
        scene.getLastChild().attachChild(house);
        scene.getLastChild().attachChild(house2);
        scene.getLastChild().attachChild(house3);
        scene.getLastChild().attachChild(hermit1);
        scene.getLastChild().attachChild(man);
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
                            Intent b6Terrain1Intent = new Intent(Barrio6Terrain2Activity.this, Barrio6Terrain1Activity.class);
                            b6Terrain1Intent.putExtra("heroFromT2X", hero.getX());
                            b6Terrain1Intent.putExtra("heroFromT2Y", hero.getY());
                            b6Terrain1Intent.putExtra("fromB6Terrain2", true);
                            startActivity(b6Terrain1Intent);
                            Barrio6Terrain2Activity.this.finish();
                        } else if (hero.getX() >= CAMERA_WIDTH) {
                            Intent b6Terrain3Intent = new Intent(Barrio6Terrain2Activity.this, Barrio6Terrain3Activity.class);
                            b6Terrain3Intent.putExtra("heroFromT2X", hero.getX());
                            b6Terrain3Intent.putExtra("heroFromT2Y", hero.getY());
                            b6Terrain3Intent.putExtra("fromB6Terrain2", true);
                            startActivity(b6Terrain3Intent);
                            Barrio6Terrain2Activity.this.finish();
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
                        Toast.makeText(getBaseContext(), "We fled from the previous village and ended up here. So far we love this place!", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(house)) {
                        Toast.makeText(getBaseContext(), "Hmmm, nothing quite fancy there...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(house2)) {
                        Toast.makeText(getBaseContext(), "Cool!", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(house3)) {
                        Toast.makeText(getBaseContext(), "Wow! An actual tree house!", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(man)) {
                        Toast.makeText(getBaseContext(), "This is a peaceful place. You won't see criminals around here...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(hermit1)) {
                        Toast.makeText(getBaseContext(), "The village you saw earlier was cursed for having idolized false prophets and deities...", Toast.LENGTH_LONG).show();
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
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(hermit1)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(hermit1.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(hermit1.getX() + hermit1.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.collidesWith(hermit1) && hero.getX() <= hermit1.getX() + hermit1.getWidth() - 16) {
                        hero.setPosition(hero.getX(), hermit1.getY() - hero.getHeight());
                    }
                } else if (hero.collidesWith(house3)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(house3.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(house3.getX() + house3.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= house3.getY() && hero.getX() + hero.getWidth() >= house3.getX() + 5
                            && hero.getX() <= house3.getX() + house3.getWidth() - 15) {
                        hero.setPosition(hero.getX(), house3.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.collidesWith(house3) && hero.getX() <= house3.getX() + house3.getWidth() - 16) {
                        hero.setPosition(hero.getX(), house3.getY() + house3.getHeight() + 2);
                    }
                }
                else if (hero.collidesWith(house2)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(house2.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(house2.getX() + house2.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= house2.getY() && hero.getX() + hero.getWidth() >= house2.getX() + 5
                            && hero.getX() <= house2.getX() + house2.getWidth() - 15) {
                        hero.setPosition(hero.getX(), house2.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= house2.getY() + house2.getHeight() && hero.getX() + hero.getWidth() >= house2.getX() + 5
                            && hero.getX() <= house2.getX() + house2.getWidth() - 15) {
                        hero.setPosition(hero.getX(), house2.getY() + house2.getHeight() + 2);
                    }
                }  else if (hero.collidesWith(man)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(man.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(man.getX() + man.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= man.getY() && hero.getX() + hero.getWidth() >= man.getX() + 5
                            && hero.getX() <= man.getX() + man.getWidth() - 15) {
                        hero.setPosition(hero.getX(), man.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= man.getY() + man.getHeight() && hero.getX() + hero.getWidth() >= man.getX() + 5
                            && hero.getX() <= man.getX() + man.getWidth() - 15) {
                        hero.setPosition(hero.getX(), man.getY() + man.getHeight() + 2);
                    }
                }
                if (playerDirection.equals(direction.UP)) {
                    if (hero.collidesWith(house) && hero.getX() <= house.getX() + house.getWidth() - 16) {
                        hero.setPosition(hero.getX(), house.getY() + house.getHeight() + 2);
                    } else if (hero.collidesWith(house2) && hero.getX() <= house2.getX() + house2.getWidth() - 16) {
                        hero.setPosition(hero.getX(), house2.getY() + house2.getHeight() + 2);
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
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio6Terrain2Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        Barrio6Terrain2Activity.this.startActivity(new Intent(Barrio6Terrain2Activity.this, MainMenuActivity.class));
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
