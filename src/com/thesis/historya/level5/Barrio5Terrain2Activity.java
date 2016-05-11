package com.thesis.historya.level5;

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

public class Barrio5Terrain2Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    // Textures
    private BitmapTextureAtlas springTexture;
    private BuildableBitmapTextureAtlas horseTexture;
    private BitmapTextureAtlas talkTexture;
    private BuildableBitmapTextureAtlas mageKidTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;
    private BitmapTextureAtlas farmerTexture;

    // TextureRegions
    private TextureRegion farmerTextureRegion;
    private TextureRegion springTextureRegion;
    private TiledTextureRegion horseTextureRegion;
    private TextureRegion talkTextureRegion;
    private TiledTextureRegion mageKidTextureRegion;
    private TextureRegion baseTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;
    private TextureRegion grassTextureRegion;

    Sprite spring;

    FixedStepEngine fsEngine;
    int tileColumn = 4;
    int tileRow = 6;
    Sprite grassSprite, rock1, swordButton;

    private AnimatedSprite horse;
    AnimatedSprite sword;
    float heroX = 0;
    float heroY = 0;

    float heroFromY = 0;
    float heroFromX = 0;
    AnimatedSprite hero;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;
    float springX = CAMERA_WIDTH / 2;
    float springY = 60;
    private final float mageKidX = 80;
    private final float mageKidY = 40;
    private Camera camera;
    private direction playerDirection;
    AnimatedSprite slashSprite;
    long[] durations = new long[4];
    AnimatedSprite mageKid;

    private enum direction {
        UP, DOWN, LEFT, RIGHT
    }

    private float horseX = CAMERA_WIDTH / 2;
    private final float horseY = 80;

    private Sprite farmer;
    private final float farmerX = horseX - 100;
    private final float farmerY = horseY;

    private static String LEVEL = "LEVEL4";
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

    private MoveModifier arrowModifier;
    protected boolean swordTouched;
    Music backgroundMusic;

    private ColorModifier springColorModifier;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        if (getIntent().getBooleanExtra("fromB5Terrain1", false)) {
            heroX = 10;
            getIntent().putExtra("fromB5Terrain1", false);
        } else if (getIntent().getBooleanExtra("fromB5Terrain3", false)) {
            heroX = CAMERA_WIDTH - 50;
        } else {
            heroX = gameUtils.getX();
        }
        heroY = gameUtils.getY();
        playerDirection = direction.DOWN;

        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "desert_crossing.mp3");
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
        if (gameUtils.isHintOn()) {
            Toast.makeText(getBaseContext(), "Help the little mage learn a spell and he will in turn be able to help you on your quest.", Toast.LENGTH_LONG).show();
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

        horseTexture = new BuildableBitmapTextureAtlas(256, 64, TextureOptions.DEFAULT);
        horseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(horseTexture, this, "horse3.png", 4, 2);
        try {
            horseTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        farmerTexture = new BitmapTextureAtlas(32, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        farmerTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(farmerTexture, getBaseContext(), "farmer.png", 0, 0);

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        mageKidTexture = new BuildableBitmapTextureAtlas(128, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        mageKidTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(mageKidTexture, getBaseContext(), "mageKid.png", 4, 2);
        try {
            mageKidTexture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        grassTexture = new BitmapTextureAtlas(1024, 1024, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "drought_tile4.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        springTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        springTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(springTexture, getBaseContext(), "spring.png", 0, 0);

        fsEngine.getTextureManager().loadTexture(farmerTexture);
        fsEngine.getTextureManager().loadTexture(springTexture);
        fsEngine.getTextureManager().loadTexture(horseTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(mageKidTexture);
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

        farmer = new Sprite(farmerX, farmerY, farmerTextureRegion);

        horseX -= 30;
        horse = new AnimatedSprite(horseX, horseY, horseTextureRegion);
        horse.animate(new long[] { 230, 230, 230, 230 }, 4, 7, true);
        springColorModifier = new ColorModifier(1f, 1f, 1f, 0f, 0.42f, 0f, 0.42f);
        spring = new Sprite(springX, springY, springTextureRegion);
        if (!gameUtils.doesSoilHaveWater()) {
            spring.registerEntityModifier(springColorModifier);
        }

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
        mageKid = new AnimatedSprite(mageKidX, mageKidY, mageKidTextureRegion);
        mageKid.animate(200);

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
        scene.getLastChild().attachChild(farmer);
        scene.getLastChild().attachChild(spring);
        scene.getLastChild().attachChild(horse);
        scene.getLastChild().attachChild(mageKid);
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
                            Intent b5Terrain1Intent = new Intent(Barrio5Terrain2Activity.this, Barrio5Terrain1Activity.class);
                            b5Terrain1Intent.putExtra("heroFromT2X", hero.getX());
                            b5Terrain1Intent.putExtra("heroFromT2Y", hero.getY());
                            b5Terrain1Intent.putExtra("fromB5Terrain2", true);
                            startActivity(b5Terrain1Intent);
                            Barrio5Terrain2Activity.this.finish();
                        } else if (hero.getX() >= CAMERA_WIDTH) {
                            Intent b5Terrain3Intent = new Intent(Barrio5Terrain2Activity.this, Barrio5Terrain3Activity.class);
                            b5Terrain3Intent.putExtra("heroFromT2X", hero.getX());
                            b5Terrain3Intent.putExtra("heroFromT2Y", hero.getY());
                            b5Terrain3Intent.putExtra("fromB5Terrain2", true);
                            startActivity(b5Terrain3Intent);
                            Barrio5Terrain2Activity.this.finish();
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
                    if (hero.collidesWith(mageKid)) {
                        if (gameUtils.doesSoilHaveWater()) {
                            Toast.makeText(getBaseContext(), "Mister! Mister! How did restore the water? Please teach me the spell you used!", Toast.LENGTH_LONG).show();
                            Toast.makeText(getBaseContext(), "*You whispered the magic spell*", Toast.LENGTH_LONG).show();
                            Toast.makeText(getBaseContext(), "Wow, thank you so much! I can't wait to show this to my father!", Toast.LENGTH_LONG).show();
                            gameUtils.teachKidWaterMagic();
                        } else {
                            Toast.makeText(getBaseContext(), "Ha! You mistook me for a mage didn't you? But I'm going to be one just like my dad.", Toast.LENGTH_LONG).show();
                            Toast.makeText(getBaseContext(), "So for now leave me alone and let me practice some spells. Abracadabra!", Toast.LENGTH_LONG).show();
                        }

                    } else if (hero.collidesWith(horse)) {
                        Toast.makeText(getBaseContext(), "*horse neighs*", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(farmer)) {
                        if (!gameUtils.doesSoilHaveWater()) {
                            Toast.makeText(getBaseContext(), "My horse is thirsty! There used to be fresh water here, now it's all mud and dirt. The next clean source of water we could drink is about ten miles or so...", Toast.LENGTH_LONG).show();
                            Toast.makeText(getBaseContext(), "If only...", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getBaseContext(), "Thank you so much for restoring the clean water...", Toast.LENGTH_LONG).show();
                        }

                    } else if (hero.collidesWith(farmer)) {
                        Toast.makeText(getBaseContext(), "My horse is thirsty! There used to be fresh water here, now it's all mud and dirt. The next clean source of water we could drink is about ten miles or so...", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "If only...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(spring)) {
                        String items[] = new String[] { "Water Magic", "Fire Magic", "Lightning Magic", "Nature Magic" };
                        AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio5Terrain2Activity.this).setTitle("Which magic to use?").setItems(items, new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                case 0:
                                    if (gameUtils.doesHeroKnowSpell("water")) {
                                        Toast.makeText(getBaseContext(), "*Chants water spell*", Toast.LENGTH_LONG).show();
                                        spring.unregisterEntityModifier(springColorModifier);
                                        spring.clearEntityModifiers();
                                        gameUtils.setWaterOnSoil();
                                        Toast.makeText(getBaseContext(), "You cast a water spell", Toast.LENGTH_LONG).show();
                                    } else {
                                        Toast.makeText(getBaseContext(), "You haven't learned any water-based spell.", Toast.LENGTH_LONG).show();
                                    }
                                    break;
                                case 1:
                                    if (gameUtils.doesHeroKnowSpell("fire")) {
                                        Toast.makeText(getBaseContext(), "Nothing happens...", Toast.LENGTH_LONG).show();

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
                } else if (hero.collidesWith(mageKid)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(mageKid.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(mageKid.getX() + mageKid.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= mageKid.getY() && hero.getX() + hero.getWidth() >= mageKid.getX() + 5
                            && hero.getX() <= mageKid.getX() + mageKid.getWidth() - 15) {
                        hero.setPosition(hero.getX(), mageKid.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= mageKid.getY() + mageKid.getHeight() && hero.getX() + hero.getWidth() >= mageKid.getX() + 5
                            && hero.getX() <= mageKid.getX() + mageKid.getWidth() - 15) {
                        hero.setPosition(hero.getX(), mageKid.getY() + mageKid.getHeight());
                    }
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
                        hero.setPosition(hero.getX(), horse.getY() + horse.getHeight());
                    }
                } else if (hero.collidesWith(spring)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(spring.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(spring.getX() + spring.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= spring.getY() && hero.getX() + hero.getWidth() >= spring.getX() + 5
                            && hero.getX() <= spring.getX() + spring.getWidth() - 15) {
                        hero.setPosition(hero.getX(), spring.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= spring.getY() + spring.getHeight() && hero.getX() + hero.getWidth() >= spring.getX() + 5
                            && hero.getX() <= spring.getX() + spring.getWidth() - 15) {
                        hero.setPosition(hero.getX(), spring.getY() + spring.getHeight());
                    }
                } else if (hero.collidesWith(farmer)) {
                    if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(farmer.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.LEFT)) {
                        hero.setPosition(farmer.getX() + farmer.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= farmer.getY() && hero.getX() + hero.getWidth() >= farmer.getX() + 5
                            && hero.getX() <= farmer.getX() + farmer.getWidth() - 15) {
                        hero.setPosition(hero.getX(), farmer.getY() - hero.getHeight());
                    } else if (playerDirection.equals(direction.UP) && hero.getY() <= farmer.getY() + farmer.getHeight() && hero.getX() + hero.getWidth() >= farmer.getX() + 5
                            && hero.getX() <= farmer.getX() + farmer.getWidth() - 15) {
                        hero.setPosition(hero.getX(), farmer.getY() + farmer.getHeight());
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
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio5Terrain2Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        Barrio5Terrain2Activity.this.startActivity(new Intent(Barrio5Terrain2Activity.this, MainMenuActivity.class));
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
