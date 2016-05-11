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
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.hardware.SensorManager;
import android.opengl.GLES20;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.splunk.mint.Mint;
import com.thesis.historya.GameUtils;
import com.thesis.historya.MainMenuActivity;

@SuppressLint("NewApi")
public class Barrio4Terrain1Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    public static String APPLE = "apple";
    // Textures
    private BitmapTextureAtlas girlTexture;
    private BitmapTextureAtlas treeTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BuildableBitmapTextureAtlas manTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;
    private BitmapTextureAtlas houseTexture;
    private BuildableBitmapTextureAtlas horseTexture;

    // TextureRegions
    private TextureRegion girlTextureRegion;
    private TextureRegion treeTextureRegion;
    private TextureRegion talkTextureRegion;
    private TextureRegion houseTextureRegion;
    private TextureRegion baseTextureRegion;
    private TiledTextureRegion manTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;
    private TextureRegion grassTextureRegion;
    private TiledTextureRegion horseTextureRegion;

    private FixedStepEngine fsEngine;
    private final int tileColumn = 4;
    private final int tileRow = 6;

    private Sprite swordButton;

    private AnimatedSprite man;
    private AnimatedSprite horse;
    private Intent b4Terrain2Intent;
    private float heroX = 0;
    private float heroY = 0;

    private float manX = 80;
    private final float manY = 60;
    private AnimatedSprite hero;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;

    private final float houseX = 15;
    private final float houseY = 25;
    private final float treeX = (CAMERA_WIDTH / 2) - 30;
    private final float treeY = houseY;
    private final float horseX = treeX - 10;
    private final float horseY = 160;
    private Sprite tree;
    private float girlX;
    private final float girlY = 50;
    private Camera camera;
    long[] durations = new long[4];

    private GameUtils gameUtils;

    private enum direction {
        UP, DOWN, LEFT, RIGHT
    }

    Sound running, slash;
    private direction playerDirection = direction.DOWN;

    private Editor editor;
    SharedPreferences positionPref;

    private BitmapTextureAtlas fontTexture;
    private Font font;
    private ChangeableText healthRateText;
    int healthRate = 100;
    private Rectangle health;

    private Scene scene;
    private Sprite house;
    private Sprite girl;

    Music backgroundMusic;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        positionPref = getSharedPreferences("positionPref", Context.MODE_PRIVATE);
        setEditor(positionPref.edit());
        if (getIntent().getBooleanExtra("fromB3Terrain3", false)) {
            heroX = 10;
            getIntent().putExtra("fromB3Terrain3", false);
        } else {
            heroX = CAMERA_WIDTH - 30;
        }
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "desert_crossing.mp3");
            backgroundMusic.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SoundFactory.setAssetBasePath("sfx/");
        heroY = gameUtils.getY();
        try {
            running = SoundFactory.createSoundFromAsset(getSoundManager(), getBaseContext(), "running.mp3");
            slash = SoundFactory.createSoundFromAsset(getSoundManager(), getBaseContext(), "slash.mp3");
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (gameUtils.isHintOn()) {
            Toast.makeText(getBaseContext(), "Once you have acquired fire magic, you can use it to burn thick thorny vines.", Toast.LENGTH_LONG).show();
        }
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

        treeTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        treeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(treeTexture, getBaseContext(), "wilted_tree.png", 0, 0);

        horseTexture = new BuildableBitmapTextureAtlas(256, 64, TextureOptions.DEFAULT);
        horseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(horseTexture, this, "horse2.png", 4, 2);
        try {
            horseTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }
        girlTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        girlTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(girlTexture, getBaseContext(), "girl.png", 0, 0);

        houseTexture = new BitmapTextureAtlas(64, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        houseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(houseTexture, getBaseContext(), "house4.png", 0, 0);
        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "drought_tile1.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        manTexture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        manTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(manTexture, getBaseContext(), "man5.png", tileColumn, 2);
        try {
            manTexture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        fsEngine.getTextureManager().loadTexture(horseTexture);
        fsEngine.getTextureManager().loadTexture(girlTexture);
        fsEngine.getTextureManager().loadTexture(manTexture);
        fsEngine.getTextureManager().loadTexture(treeTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(grassTexture);
        fsEngine.getTextureManager().loadTexture(heroTexture);
        fsEngine.getTextureManager().loadTexture(talkTexture);
        fsEngine.getTextureManager().loadTexture(houseTexture);
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
        final PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), true);
        final int centerX = (CAMERA_WIDTH - grassTextureRegion.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - grassTextureRegion.getHeight()) / 2;
        Sprite background = new Sprite(centerX, centerY, grassTextureRegion);
        tree = new Sprite(treeX, treeY, treeTextureRegion);
        // heroY = (displayHeight / 2) - 20;
        // heroX = 10;
        house = new Sprite(houseX, houseY, houseTextureRegion);
        hero = new AnimatedSprite(heroX, heroY, heroTextureRegion);

        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);
        manX = house.getX() + house.getWidth() + 10;
        man = new AnimatedSprite(manX, manY, manTextureRegion);
        man.animate(2000);
        horse = new AnimatedSprite(horseX, horseY, horseTextureRegion);
        horse.animate(new long[] { 230, 230, 230, 230 }, 4, 7, true);
        girlX = tree.getX() + tree.getWidth() + 20;
        girl = new Sprite(girlX, girlY, girlTextureRegion);
        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
        // final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2,
        // CAMERA_HEIGHT);
        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
        // roof.registerEntityModifier(new ScaleYModifier(10, 20, 30));
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

        // health
        healthRate = gameUtils.getHealthRate();
        if (healthRate <= 0) {
            healthRate = 100;
        }
        health = new Rectangle(CAMERA_WIDTH - 130, 25, healthRate, 5);
        health.setColor(0, 100, 0);

        // health rate text
        healthRateText = new ChangeableText(health.getX(), health.getY() - 20, font, "Health rate: " + String.valueOf(healthRate));
        healthRateText.setVisible(true);

        PhysicsFactory.createBoxBody(physicsWorld, roof, BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, left, BodyType.StaticBody, wallFixtureDef);
        // PhysicsFactory.createBoxBody(physicsWorld, right,
        // BodyType.StaticBody, wallFixtureDef);
        PhysicsFactory.createBoxBody(physicsWorld, ground, BodyType.StaticBody, wallFixtureDef);
        scene.getLastChild().attachChild(roof);
        scene.getLastChild().attachChild(left);
        // scene.getLastChild().attachChild(right);
        scene.getLastChild().attachChild(ground);

        scene.getLastChild().attachChild(background);
        scene.getLastChild().attachChild(girl);
        scene.getLastChild().attachChild(hero);
        scene.getLastChild().attachChild(tree);
        scene.getLastChild().attachChild(house);
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(man);
        scene.getLastChild().attachChild(horse);
        // scene.getLastChild().attachChild(healthBar);
        // scene.getLastChild().attachChild(tree1);
        final long[] frameDurations = { 200, 200, 200, 200 };

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

                        if (hero.getX() >= CAMERA_WIDTH) {
                            b4Terrain2Intent = new Intent(Barrio4Terrain1Activity.this, Barrio4Terrain2Activity.class);
                            b4Terrain2Intent.putExtra("heroFromT1X", hero.getX());
                            b4Terrain2Intent.putExtra("heroFromT1Y", hero.getY());
                            b4Terrain2Intent.putExtra("fromB4Terrain1", true);
                            startActivity(b4Terrain2Intent);
                            Barrio4Terrain1Activity.this.finish();
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
                    if (hero.collidesWith(man)) {
                        Toast.makeText(getBaseContext(), "Everything's changed since the drought started. No one knows why it started.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "Trees and plants have all wilted. Animals died due to hunger. People moved out of this place to find a greener place.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "Death consumed this place...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(house)) {
                        Toast.makeText(getBaseContext(), "There's nothing much to look inside, save for a table and two chairs...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(horse)) {
                        Toast.makeText(getBaseContext(), "*horse neighs*", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(tree)) {
                        Toast.makeText(getBaseContext(), "Drought has been cruel to this place. This tree has wilted.", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(girl)) {
                        Toast.makeText(getBaseContext(), "I don't know how long my dad and I will stay here. All of my friends have gone to some other places.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "I wonder how Mina is doing now. Mina is my best friend and we've been inseperable until this whole freak of nature happened.", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "I sure hope she's fine... *Sobs*", Toast.LENGTH_LONG).show();
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
                if (hero.collidesWith(man) && hero.getX() <= man.getX() - 2 && (hero.getY() + hero.getHeight() >= man.getY() + 10) && playerDirection.equals(direction.LEFT)) {
                    hero.setPosition(man.getX() - 2, hero.getY());
                } else if (hero.collidesWith(man) && (hero.getX() + hero.getWidth() >= man.getX() - 5) && (hero.getY() + hero.getHeight() >= man.getY() + 10)
                        && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(man.getX() - (hero.getWidth() - 5), hero.getY());
                } else if (hero.collidesWith(man) && playerDirection.equals(direction.UP) && hero.getY() <= man.getY() + man.getHeight() && hero.getX() <= man.getX() - 5
                        && hero.getX() >= man.getX() - 40) {
                    hero.setPosition(hero.getX(), man.getY() + man.getHeight() + 1);
                } else if (hero.collidesWith(man) && playerDirection.equals(direction.DOWN) && (hero.getY() + hero.getHeight() <= man.getY() + 30) && hero.getX() <= man.getX() - 5
                        && hero.getX() >= man.getX() - 40) {
                    hero.setPosition(hero.getX(), man.getY() - 40);
                }
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(left)) {
                    hero.setPosition(left.getX() + 1, hero.getY());
                } else if (hero.collidesWith(horse)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(horse.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(horse.getX() + horse.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= horse.getY() && hero.getX() + hero.getWidth() >= horse.getX() + 5
                            && hero.getX() <= horse.getX() + horse.getWidth() - 15) {
                        hero.setPosition(hero.getX(), horse.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= horse.getY() + horse.getHeight() && hero.getX() + hero.getWidth() >= horse.getX() + 5
                            && hero.getX() <= horse.getX() + horse.getWidth() - 15) {
                        hero.setPosition(hero.getX(), horse.getY() + horse.getHeight() + 2);
                    }
                } else if (hero.collidesWith(tree)) {
                    if (playerDirection.equals(direction.RIGHT) && hero.getY() + hero.getHeight() >= tree.getY() + tree.getHeight() - 30 && hero.getY() + hero.getHeight() <= tree.getY() + tree.getHeight()) {
                        hero.setPosition(tree.getX() - 45, hero.getY());
                    } else if (playerDirection.equals(direction.LEFT) && hero.getY() + hero.getHeight() >= tree.getY() + tree.getHeight() - 30 && hero.getY() + hero.getHeight() <= tree.getY() + tree.getHeight()) {
                        hero.setPosition(tree.getX() + tree.getWidth() - 20, hero.getY());
                    }
                } else if (hero.collidesWith(girl)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(girl.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(girl.getX() + girl.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= girl.getY() && hero.getX() + hero.getWidth() >= girl.getX() + 5
                            && hero.getX() <= girl.getX() + girl.getWidth() - 15) {
                        hero.setPosition(hero.getX(), girl.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= girl.getY() + girl.getHeight() && hero.getX() + hero.getWidth() >= girl.getX() + 5
                            && hero.getX() <= girl.getX() + girl.getWidth() - 15) {
                        hero.setPosition(hero.getX(), girl.getY() + girl.getHeight() + 2);
                    }
                }
                if (playerDirection.equals(direction.UP)) {
                    if (hero.collidesWith(house) && hero.getX() <= house.getX() + house.getWidth() - 16) {
                        hero.setPosition(hero.getX(), house.getY() + house.getHeight() + 2);
                    }
                } else if (playerDirection.equals(direction.LEFT)) {
                    if (hero.collidesWith(house) && hero.getX() >= house.getX() + house.getWidth() - 18) {
                        hero.setPosition(house.getX() + house.getWidth() - 16, hero.getY());
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

    public Editor getEditor() {
        return editor;
    }

    public void setEditor(Editor editor) {
        this.editor = editor;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            scene.setIgnoreUpdate(true);
            String items[] = new String[] { "Resume", "Back to Main Menu" };
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio4Terrain1Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        Barrio4Terrain1Activity.this.startActivity(new Intent(Barrio4Terrain1Activity.this, MainMenuActivity.class));
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
