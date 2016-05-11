package com.thesis.historya.level2;

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
import android.os.Handler;
import android.view.KeyEvent;
import android.widget.Toast;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.splunk.mint.Mint;
import com.thesis.historya.GameUtils;
import com.thesis.historya.MainMenuActivity;

@SuppressLint("NewApi")
public class Barrio2Terrain1Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    // Textures
    private BuildableBitmapTextureAtlas boyTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BuildableBitmapTextureAtlas manTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;
    private BitmapTextureAtlas tree1Texture;
    private BitmapTextureAtlas houseTexture;

    // TextureRegions
    private TextureRegion houseTextureRegion;
    private TiledTextureRegion boyTextureRegion;
    private TextureRegion talkTextureRegion;
    private TextureRegion baseTextureRegion;
    private TiledTextureRegion manTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion tree1TextureRegion;
    private TextureRegion swordButtonTextureRegion;
	private TextureRegion grassTextureRegion;

	private FixedStepEngine fsEngine;
    private final int tileColumn = 4;
    private final int tileRow = 6;
    private Sprite grassSprite, tree1, swordButton;

    private AnimatedSprite boy;

    private AnimatedSprite man;
    private Intent b2Terrain2Intent;
    private float heroX = 0;
    private float heroY = 0;
    private final static float boyX = 300;
    private final static float boyY = 150;
    private final float houseX = 50;
    private final float houseY = 5;
    private Sprite house;

    private final static float manX = 250;
    private final float manY = 50;

    private AnimatedSprite hero;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;
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

    Music backgroundMusic;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        positionPref = getSharedPreferences("positionPref", Context.MODE_PRIVATE);
        setEditor(positionPref.edit());
        if (getIntent().getBooleanExtra("fromB1Terrain3", false)) {
            heroX = 10;
            getIntent().putExtra("fromB1Terrain3", false);
        } else {
            heroX = CAMERA_WIDTH - 50;
        }

        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "port_town.mp3");
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
            Toast.makeText(getBaseContext(), "Proceed to the east and you will encounter a telekinetic mage. You can deflect the stones he throws at you by using your sword.", Toast.LENGTH_LONG).show();
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
        heroTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(
                heroTexture, this, "hero7.png", tileColumn, tileRow);

        try {
            heroTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }

        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        houseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        houseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(houseTexture, getBaseContext(), "house3.png", 0, 0);

        tree1Texture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        tree1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(tree1Texture,
                this, "tree.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "grass_tile4.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture,
                getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture,
                getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture,
                getBaseContext(), "onscreen_control_knob.png", 0, 0);

        boyTexture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        boyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(boyTexture, getBaseContext(), "boysprite.png", 4, 2);
        try {
            boyTexture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {
            e.printStackTrace();
        }

        manTexture = new BuildableBitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        manTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(manTexture, getBaseContext(), "spritelady.png", 4, 2);
        try {
            manTexture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        fsEngine.getTextureManager().loadTexture(manTexture);
        fsEngine.getTextureManager().loadTexture(houseTexture);
        fsEngine.getTextureManager().loadTexture(boyTexture);
        fsEngine.getTextureManager().loadTexture(swordButtonTexture);
        fsEngine.getTextureManager().loadTexture(baseTexture);
        fsEngine.getTextureManager().loadTexture(knobTexture);
        fsEngine.getTextureManager().loadTexture(tree1Texture);
		fsEngine.getTextureManager().loadTexture(grassTexture);
        fsEngine.getTextureManager().loadTexture(heroTexture);
        fsEngine.getTextureManager().loadTexture(talkTexture);
        fsEngine.getTextureManager().loadTexture(fontTexture);
        fsEngine.getFontManager().loadFont(font);
	}

	@Override
	public Scene onLoadScene() {
        fsEngine.registerUpdateHandler(new FPSLogger());

        Arrays.fill(durations, 140);
        scene = new Scene(1);
        final PhysicsWorld physicsWorld = new PhysicsWorld(new Vector2(0, SensorManager.GRAVITY_EARTH), true);
        final int centerX = (CAMERA_WIDTH - grassTextureRegion.getWidth()) / 2;
        final int centerY = (CAMERA_HEIGHT - grassTextureRegion.getHeight()) / 2;
        Sprite background = new Sprite(centerX, centerY, grassTextureRegion);

        // heroY = (displayHeight / 2) - 20;
        // heroX = 10;
        hero = new AnimatedSprite(heroX, heroY, heroTextureRegion);

        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);
        house = new Sprite(houseX, houseY, houseTextureRegion);
        man = new AnimatedSprite(manX, manY, manTextureRegion);
        boy = new AnimatedSprite(boyX, boyY, boyTextureRegion);

        tree1 = new Sprite(CAMERA_WIDTH / 2, CAMERA_HEIGHT - tree1Texture.getHeight(), tree1TextureRegion);

        final Shape roof = new Rectangle(0, 0, CAMERA_WIDTH, 2);
        final Shape left = new Rectangle(0, 0, 2, CAMERA_HEIGHT);
        // final Shape right = new Rectangle(CAMERA_WIDTH - 2, 0, 2,
        // CAMERA_HEIGHT);
        final Shape ground = new Rectangle(0, CAMERA_HEIGHT - 2, CAMERA_WIDTH, 2);
        // roof.registerEntityModifier(new ScaleYModifier(10, 20, 30));
        final FixtureDef wallFixtureDef = PhysicsFactory.createFixtureDef(0, 0.5f, 0.5f);

        // health
        healthRate = gameUtils.getHealthRate();
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
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(house);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(man);
        scene.getLastChild().attachChild(boy);
        scene.getLastChild().attachChild(hero);
        // scene.getLastChild().attachChild(healthBar);
        // scene.getLastChild().attachChild(tree1);

        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!man.isAnimationRunning()) {
                    man.animate(200);
                }
            }
        }, 10);

        if (!boy.isAnimationRunning()) {
            boy.animate(2000);
        }

        final DigitalOnScreenControl digitalOnScreenControl = new DigitalOnScreenControl(0,
                CAMERA_HEIGHT - baseTextureRegion.getHeight(), camera, baseTextureRegion,
                knobTextureRegion, .1f, new IAnalogOnScreenControlListener() {

                    @Override
                    public void onControlChange(BaseOnScreenControl pBaseOnScreenControl,
                            float pValueX, float pValueY) {
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
                            b2Terrain2Intent = new Intent(Barrio2Terrain1Activity.this, Barrio2Terrain2Activity.class);
                            b2Terrain2Intent.putExtra("heroFromT1X", hero.getX());
                            b2Terrain2Intent.putExtra("heroFromT1Y", hero.getY());
                            b2Terrain2Intent.putExtra("fromB2Terrain1", true);
                            startActivity(b2Terrain2Intent);
                            Barrio2Terrain1Activity.this.finish();
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

                    if (playerDirection.equals(direction.LEFT) && (!hero.collidesWith(man)) && !hero.collidesWith(boy)) {
                            hero.animate(new long[] { 140, 140, 140, 140 }, 16, 19, true);
                        slash.setLooping(true);
                        if (gameUtils.isSoundOn()) {
                            slash.play();
                        }

                    } else if (playerDirection.equals(direction.RIGHT) && (!hero.collidesWith(man)) && !hero.collidesWith(boy)) {
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
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "Today's a beautiful day! It makes me feel so pumped up!", Toast.LENGTH_LONG).show();
                            }
                        });
                    } else if (hero.collidesWith(boy)) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Toast.makeText(getBaseContext(), "My big brother is training hard to become a great warrior someday!", Toast.LENGTH_LONG).show();
                                Toast.makeText(getBaseContext(), "Oh and be careful with the rogue shaman! He's on a telekinetic rampage...", Toast.LENGTH_LONG).show();
                            }
                        });
                    }
                    if (hero.collidesWith(house)) {
                        Toast.makeText(getBaseContext(), "I wonder if someone's inside...", Toast.LENGTH_LONG).show();
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
                } else if (hero.collidesWith(boy) && playerDirection.equals(direction.DOWN) && (hero.getY() + hero.getHeight() <= boy.getY() + 30) && hero.getX() <= boy.getX() - 5
                        && hero.getX() >= boy.getX() - 40) {
                    hero.setPosition(hero.getX(), boy.getY() - 40);
                } else if (hero.collidesWith(boy) && playerDirection.equals(direction.UP) && hero.getY() <= boy.getY() + boy.getHeight() && hero.getX() <= boy.getX() - 5
                        && hero.getX() >= boy.getX() - 40) {
                    hero.setPosition(hero.getX(), boy.getY() + boy.getHeight() + 1);
                } else if (hero.collidesWith(boy) && (hero.getX() + hero.getWidth() >= boy.getX() - 5) && (hero.getY() + hero.getHeight() >= boy.getY() + 10)
                        && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(boy.getX() - (hero.getWidth() - 5), hero.getY());
                } else if (hero.collidesWith(boy) && hero.getX() <= boy.getX() - 2 && (hero.getY() + hero.getHeight() >= boy.getY() + 10) && playerDirection.equals(direction.LEFT)) {
                    hero.setPosition(boy.getX() - 2, hero.getY());
                }
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(left)) {
                    hero.setPosition(left.getX() + 1, hero.getY());
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
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio2Terrain1Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        Barrio2Terrain1Activity.this.startActivity(new Intent(Barrio2Terrain1Activity.this, MainMenuActivity.class));
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
