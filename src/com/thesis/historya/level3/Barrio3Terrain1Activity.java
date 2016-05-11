package com.thesis.historya.level3;

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
import org.anddev.andengine.entity.modifier.IEntityModifier;
import org.anddev.andengine.entity.modifier.LoopEntityModifier;
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
public class Barrio3Terrain1Activity extends BaseGameActivity {
    BuildableBitmapTextureAtlas heroTexture;

    public static String APPLE = "apple";
    // Textures
    private BitmapTextureAtlas appleTexture;
    private BitmapTextureAtlas treeTexture;
    private BitmapTextureAtlas talkTexture;
    private BitmapTextureAtlas swordButtonTexture;
    private BuildableBitmapTextureAtlas firstLadyTexture;
    private BitmapTextureAtlas baseTexture;
    private BitmapTextureAtlas knobTexture;
    private BitmapTextureAtlas grassTexture;
    private BitmapTextureAtlas rockTexture;
    private BitmapTextureAtlas houseTexture;
    private BuildableBitmapTextureAtlas goatTexture;
    private BitmapTextureAtlas psychicTexture;

    // TextureRegions
    private TextureRegion appleTextureRegion;
    private TextureRegion psychicTextureRegion;
    private TextureRegion rockTextureRegion;
    private TextureRegion treeTextureRegion;
    private TextureRegion talkTextureRegion;
    private TextureRegion houseTextureRegion;
    private TextureRegion baseTextureRegion;
    private TiledTextureRegion firstLadyTextureRegion;
    private TextureRegion knobTextureRegion;
    private TiledTextureRegion heroTextureRegion;
    private TextureRegion swordButtonTextureRegion;
	private TextureRegion grassTextureRegion;
    private TiledTextureRegion goatTextureRegion;

	private FixedStepEngine fsEngine;
    private final int tileColumn = 4;
    private final int tileRow = 6;
    private final float goatX = 15;
    private final float goatY = 30;
    private Sprite swordButton;

    private AnimatedSprite firstLady;
    private AnimatedSprite goat;
    private Intent b3Terrain2Intent;
    private float heroX = 0;
    private float heroY = 0;

    private float firstLadyX = 80;
    private final float firstLadyY = 60;
    private final float firstLadyInitialX = 80;
    private AnimatedSprite hero;
    private final int CAMERA_WIDTH = 480;
    private final int CAMERA_HEIGHT = 320;

    private final float houseX = CAMERA_WIDTH / 2;
    private final float houseY = 5;
    private final float treeX = houseX - 60;
    private final float treeY = 5;

    private final float appleX = 15;
    private final float appleY = 15;

    private final float rockX = houseX;
    private final float rockY = 220;
    private final float psychicX = rockX + 8;
    private final float psychicY = rockY - 16;
    private Sprite tree;
    private Sprite rock;
    private Sprite psychic;
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
    private Sprite apple;
    Music backgroundMusic;

    @Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        positionPref = getSharedPreferences("positionPref", Context.MODE_PRIVATE);
        setEditor(positionPref.edit());
        if (getIntent().getBooleanExtra("fromB2Terrain5", false)) {
            heroX = 10;
            getIntent().putExtra("fromB2Terrain5", false);
        } else {
            heroX = CAMERA_WIDTH - 30;
        }
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "peaceful_moment.mp3");
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
            Toast.makeText(getBaseContext(), "The psychic can read the minds of anyone, even a mage's mind. Help him and maybe he'll reward you with something.", Toast.LENGTH_LONG).show();
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
        engineOptions.setNeedsMusic(true);
        engineOptions.setNeedsSound(true);
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

        appleTexture = new BitmapTextureAtlas(8, 8, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        appleTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(appleTexture, getBaseContext(), "apple.png", 0, 0);
        treeTexture = new BitmapTextureAtlas(64, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        treeTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(treeTexture, getBaseContext(), "tree2.png", 0, 0);

        goatTexture = new BuildableBitmapTextureAtlas(256, 64, TextureOptions.DEFAULT);
        goatTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(goatTexture, this, "goat.png", 4, 2);
        try {
            goatTexture.build(new BlackPawnTextureBuilder(2));
        } catch (final TextureAtlasSourcePackingException e) {
            e.printStackTrace();
        }
        psychicTexture = new BitmapTextureAtlas(32, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        psychicTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(psychicTexture, getBaseContext(), "psychic.png", 0, 0);
        houseTexture = new BitmapTextureAtlas(64, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        houseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(houseTexture, getBaseContext(), "house4.png", 0, 0);
        talkTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        talkTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(talkTexture, getBaseContext(), "talk.png", 0, 0);

        rockTexture = new BitmapTextureAtlas(32, 32, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        rockTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(rockTexture, getBaseContext(), "rock4.png", 0, 0);

        grassTexture = new BitmapTextureAtlas(1024, 1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        grassTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(grassTexture, this, "grass_tile9.png", 0, 0);

        swordButtonTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        swordButtonTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(swordButtonTexture, getBaseContext(), "sword_button.png", 0, 0);

        baseTexture = new BitmapTextureAtlas(128, 128, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        baseTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(baseTexture, getBaseContext(), "onscreen_control_base.png", 0, 0);

        knobTexture = new BitmapTextureAtlas(64, 64, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        knobTextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(knobTexture, getBaseContext(), "onscreen_control_knob.png", 0, 0);

        firstLadyTexture = new BuildableBitmapTextureAtlas(128, 256, TextureOptions.BILINEAR_PREMULTIPLYALPHA);
        firstLadyTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(firstLadyTexture, getBaseContext(), "spritelady1.png", tileColumn, 4);
        try {
            firstLadyTexture.build(new BlackPawnTextureBuilder(2));
        } catch (Exception e) {
            e.printStackTrace();
        }
        fsEngine.getTextureManager().loadTexture(psychicTexture);
        fsEngine.getTextureManager().loadTexture(appleTexture);
        fsEngine.getTextureManager().loadTexture(goatTexture);
        fsEngine.getTextureManager().loadTexture(firstLadyTexture);
        fsEngine.getTextureManager().loadTexture(rockTexture);
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

        apple = new Sprite(appleX, appleY, appleTextureRegion);
        psychic = new Sprite(psychicX, psychicY, psychicTextureRegion);
        psychic.registerEntityModifier(new LoopEntityModifier(new SequenceEntityModifier(new MoveYModifier(4, psychicY, psychicY - 18), new MoveYModifier(4, psychicY - 18, psychicY))));
        rock = new Sprite(rockX, rockY, rockTextureRegion);
        final PhysicsHandler physicsHandler = new PhysicsHandler(hero);
        hero.registerUpdateHandler(physicsHandler);
        firstLadyX = house.getX() + house.getWidth() + 10;
        firstLady = new AnimatedSprite(firstLadyX, firstLadyY, firstLadyTextureRegion);
        goat = new AnimatedSprite(goatX, goatY, goatTextureRegion);
        goat.animate(new long[] { 230, 230, 230, 230 }, 4, 7, true).registerEntityModifier(new MoveXModifier(10, goatX, goatX + 130));

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
        scene.getLastChild().attachChild(tree);
        scene.getLastChild().attachChild(rock);
        scene.getLastChild().attachChild(hero);
        scene.getLastChild().attachChild(psychic);
        scene.getLastChild().attachChild(house);
        scene.getLastChild().attachChild(health);
        scene.getLastChild().attachChild(healthRateText);
        scene.getLastChild().attachChild(firstLady);
        scene.getLastChild().attachChild(goat);
        // scene.getLastChild().attachChild(healthBar);
        // scene.getLastChild().attachChild(tree1);

        if (!gameUtils.pickedApple()) {
            tree.attachChild(apple);
        } else {
            tree.detachChild(apple);
        }
        final long[] frameDurations = { 200, 200, 200, 200 };
        final IEntityModifier rightMovement = new MoveXModifier(10, firstLadyX, firstLadyInitialX + 180);
        final IEntityModifier leftMovement = new MoveXModifier(10, firstLadyInitialX + 180, firstLadyInitialX);
        Handler handler = new Handler();

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!firstLady.isAnimationRunning() && !gameUtils.hasQuestionBeenAnswered(GameUtils.Riddles.RIDDLE1.toString())) {

                    if (firstLadyX < firstLadyInitialX + 180) {
                        firstLady.animate(frameDurations, 8, 11, true).registerEntityModifier(new SequenceEntityModifier(rightMovement, leftMovement));
                    }
                } else {
                    firstLady.setCurrentTileIndex(0);
                }
            }
        }, 10);

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
                            b3Terrain2Intent = new Intent(Barrio3Terrain1Activity.this, Barrio3Terrain2Activity.class);
                            b3Terrain2Intent.putExtra("heroFromT1X", hero.getX());
                            b3Terrain2Intent.putExtra("heroFromT1Y", hero.getY());
                            b3Terrain2Intent.putExtra("fromB3Terrain1", true);
                            startActivity(b3Terrain2Intent);
                            Barrio3Terrain1Activity.this.finish();
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
                    if (hero.collidesWith(firstLady)) {
                        Toast.makeText(getBaseContext(), "That man has been meditating all day. I bet he's hungry!", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(house)) {
                        Toast.makeText(getBaseContext(), "A house I wouldn't mind living in...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(goat)) {
                        Toast.makeText(getBaseContext(), "Meh meh...", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(rock) && !gameUtils.pickedApple()) {
                        Toast.makeText(getBaseContext(), "Ssshh. Quite down will 'ya! I'm trying to practice my levitation skills here.", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(rock) && gameUtils.pickedApple()) {
                        Toast.makeText(getBaseContext(), "Geez! Thanks for the apple man. Now tell me, what is it you want in return?", Toast.LENGTH_LONG).show();
                        Toast.makeText(getBaseContext(), "Oh wait, I can read in your mind that you need the 'secret code' in order for Arcadius to let you pass through...", Toast.LENGTH_LONG)
                                .show();
                        Toast.makeText(getBaseContext(), "I can help, you. Let me read his mind. Oh the 'secret code' is Babaylan.", Toast.LENGTH_LONG).show();
                    } else if (hero.collidesWith(tree)) {
                        String items[] = new String[] { "Yes", "No" };
                        AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio3Terrain1Activity.this).setTitle("There's an apple in the tree. Will you pick it?").setItems(items,
                                new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialogInterface, int which) {
                                switch (which) {
                                        case 0:
                                            Toast.makeText(getBaseContext(), "You picked the apple!", Toast.LENGTH_LONG).show();
                                            gameUtils.setPickedApple(APPLE, true);
                                            apple.detachSelf();
                                            break;
                                        case 1:
                                            Toast.makeText(getBaseContext(), "You just ignored the apple...", Toast.LENGTH_LONG).show();
                                            gameUtils.setPickedApple(APPLE, false);
                                            break;
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
                if (hero.collidesWith(firstLady) && hero.getX() <= firstLady.getX() - 2 && (hero.getY() + hero.getHeight() >= firstLady.getY() + 10) && playerDirection.equals(direction.LEFT)) {
                    hero.setPosition(firstLady.getX() - 2, hero.getY());
                } else if (hero.collidesWith(firstLady) && (hero.getX() + hero.getWidth() >= firstLady.getX() - 5) && (hero.getY() + hero.getHeight() >= firstLady.getY() + 10)
                        && playerDirection.equals(direction.RIGHT)) {
                    hero.setPosition(firstLady.getX() - (hero.getWidth() - 5), hero.getY());
                } else if (hero.collidesWith(firstLady) && playerDirection.equals(direction.UP) && hero.getY() <= firstLady.getY() + firstLady.getHeight() && hero.getX() <= firstLady.getX() - 5
                        && hero.getX() >= firstLady.getX() - 40) {
                    hero.setPosition(hero.getX(), firstLady.getY() + firstLady.getHeight() + 1);
                } else if (hero.collidesWith(firstLady) && playerDirection.equals(direction.DOWN) && (hero.getY() + hero.getHeight() <= firstLady.getY() + 30) && hero.getX() <= firstLady.getX() - 5
                        && hero.getX() >= firstLady.getX() - 40) {
                    hero.setPosition(hero.getX(), firstLady.getY() - 40);
                }
                if (hero.collidesWith(roof)) {
                    hero.setPosition(hero.getX(), roof.getY() + 2);
                } else if (hero.collidesWith(ground)) {
                    hero.setPosition(hero.getX(), ground.getY() - hero.getHeight());
                } else if (hero.collidesWith(left)) {
                    hero.setPosition(left.getX() + 1, hero.getY());
                } else if (hero.collidesWith(goat)) {
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
                } else if (hero.collidesWith(tree)) {
                    if (playerDirection.equals(direction.RIGHT) && hero.getX() + hero.getWidth() >= tree.getX() && hero.getY() <= tree.getY() + tree.getHeight()) {
                        hero.setPosition(tree.getX() - hero.getWidth() - 2, hero.getY());
                    } else if (playerDirection.equals(direction.UP)) {
                        hero.setPosition(hero.getX(), tree.getY() + tree.getHeight() + 1);
                    }
                } else if (hero.collidesWith(rock)) {
                    if (playerDirection.equals(direction.UP) && hero.getY() <= rock.getY() + rock.getHeight() && hero.getX() + hero.getWidth() >= rock.getX() + 5
                            && hero.getX() <= rock.getX() + rock.getWidth() - 15) {
                        hero.setPosition(hero.getX(), rock.getY() + rock.getHeight());
                    } else if (playerDirection.equals(direction.LEFT) && hero.getX() >= rock.getX() + rock.getWidth() - 25) {
                        hero.setPosition(rock.getX() + rock.getWidth() - 12, hero.getY());
                    } else if (playerDirection.equals(direction.RIGHT)) {
                        hero.setPosition(rock.getX() - hero.getWidth(), hero.getY());
                    } else if (playerDirection.equals(direction.DOWN) && hero.getY() <= rock.getY() && hero.getX() + hero.getWidth() >= rock.getX() + 5
                            && hero.getX() <= rock.getX() + rock.getWidth() - 15) {
                        hero.setPosition(hero.getX(), rock.getY() - hero.getHeight());
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
            AlertDialog.Builder pauseAlert = new AlertDialog.Builder(Barrio3Terrain1Activity.this).setTitle("Game Paused").setItems(items, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                    case 0:
                        scene.setIgnoreUpdate(false);
                        break;
                    case 1:
                        Barrio3Terrain1Activity.this.startActivity(new Intent(Barrio3Terrain1Activity.this, MainMenuActivity.class));
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
