package com.thesis.historya;

import java.io.IOException;

import javax.microedition.khronos.opengles.GL10;

import org.anddev.andengine.audio.music.Music;
import org.anddev.andengine.audio.music.MusicFactory;
import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.FixedStepEngine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.WakeLockOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.menu.MenuScene;
import org.anddev.andengine.entity.scene.menu.MenuScene.IOnMenuItemClickListener;
import org.anddev.andengine.entity.scene.menu.item.IMenuItem;
import org.anddev.andengine.entity.scene.menu.item.TextMenuItem;
import org.anddev.andengine.entity.scene.menu.item.decorator.ColorMenuItemDecorator;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.font.Font;
import org.anddev.andengine.opengl.font.FontFactory;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class MainMenuActivity extends BaseGameActivity implements
		IOnMenuItemClickListener {
	// CONTSTANTS
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;

	protected static final int MENU_QUIT = 1;
	protected static final int MENU_PLAY = 100;
	protected static final int MENU_SCORES = MENU_PLAY + 1;
	protected static final int MENU_OPTIONS = MENU_SCORES + 1;
	protected static final int MENU_HELP = MENU_OPTIONS + 1;
	protected static final int MENU_ABOUT = MENU_OPTIONS + 1;

	// Fields
	protected Camera camera;
	protected Scene mainScene;
	protected MenuScene menuScene;

	private Handler handler;

	private BitmapTextureAtlas fontTexture;
	private BitmapTextureAtlas menuTexture;
	private TextureRegion menuTextureRegion;
	private Font font;

	protected TextureRegion menuPlayTextureRegion;
	protected TextureRegion menuScoresTextureRegion;
	protected TextureRegion menuOptionsTextureRegion;
	EngineOptions engineOptions;
	FixedStepEngine fsEngine;
	// Constructors
	// Getters and Setters
	// Methods for/from SuperClass/Interfaces

    private GameUtils gameUtils;

    private Music backgroundMusic;
    private CheckBox musicCB;
    private CheckBox audioCB;
    private CheckBox hintCB;
    int backPressedCount = 0;
    Cursor cursor;
    SQLiteDatabase sqlDb;
    boolean matchedUsername = false;
    DatabaseHelper dbHelper;

	@Override
    protected void onCreate(Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);
        gameUtils = new GameUtils(getBaseContext());
        dbHelper = new DatabaseHelper(this);
        sqlDb = dbHelper.getWritableDatabase();
        MusicFactory.setAssetBasePath("sfx/");
        try {
            backgroundMusic = MusicFactory.createMusicFromAsset(getMusicManager(), getBaseContext(), "town_of_wishes.mp3");
            backgroundMusic.setLooping(true);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String searchQuery = "SELECT * FROM " + DatabaseHelper.TABLE_NAME1;
        cursor = sqlDb.rawQuery(searchQuery, null);
        int usernameIndex = cursor.getColumnIndex(DatabaseHelper.COL_USER_NAME);
        if (cursor.moveToFirst()) {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                if (gameUtils.retrieveUsername().equals(cursor.getString(usernameIndex))) {
                    matchedUsername = true;
                }
            }
        }
        if (!matchedUsername) {
            dbHelper.insertRecordForHistoryaScore(sqlDb, dbHelper.TABLE_NAME1, gameUtils.retrieveUsername(), 0);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
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
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		engineOptions = new EngineOptions(true, ScreenOrientation.LANDSCAPE,
 new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera).setNeedsMusic(true).setNeedsSound(true);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		fsEngine = new FixedStepEngine(engineOptions, 60);
		handler = new Handler();
		return fsEngine;
	}

	@Override
	public void onLoadResources() {

		menuTexture = new BitmapTextureAtlas(1024, 1024,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		menuTextureRegion = BitmapTextureAtlasTextureRegionFactory
				.createFromAsset(menuTexture, this,
						"main_menu_background.jpg", 0, 0);
		fsEngine.getTextureManager().loadTexture(menuTexture);

		fontTexture = new BitmapTextureAtlas(256, 256,
				TextureOptions.BILINEAR_PREMULTIPLYALPHA);
		FontFactory.setAssetBasePath("font/");
		font = FontFactory.createFromAsset(fontTexture, this, "flubber.TTF",
				32, true, Color.BLACK);
		fsEngine.getTextureManager().loadTexture(fontTexture);
		fsEngine.getFontManager().loadFont(font);
	}

	@Override
	public Scene onLoadScene() {
		fsEngine.registerUpdateHandler(new FPSLogger());
		createStaticMenuScene();
		final int centerX = (CAMERA_WIDTH - menuTextureRegion.getWidth()) / 2;
		final int centerY = (CAMERA_HEIGHT - menuTextureRegion.getHeight()) / 2;
		mainScene = new Scene(1);
		Sprite menuBack = new Sprite(centerX, centerY, menuTextureRegion);
		mainScene.getLastChild().attachChild(menuBack);
		mainScene.setChildScene(menuScene);
		return mainScene;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
	    switch (keyCode) {
	    case KeyEvent.KEYCODE_BACK:
	        if (backPressedCount < 1) {
	            Toast.makeText(getBaseContext(), "Press the back button again to signout...", Toast.LENGTH_LONG).show();
	        } else {
	            gameUtils.clearUsername();
	            this.startActivity(new Intent(MainMenuActivity.this, Login.class));
	            this.finish();
	        }

	        break;
	    }
		return super.onKeyDown(keyCode, event);
	}

	public void createStaticMenuScene() {
		menuScene = new MenuScene(camera);
		final IMenuItem playMenuItem = new ColorMenuItemDecorator(
				new TextMenuItem(
				MENU_PLAY, font, "Play Game"), 0.5f, 0.5f, 0.5f, 1.0f, 0.0f,
				0.0f);
		playMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(playMenuItem);

		final IMenuItem optionsMenuItem = new ColorMenuItemDecorator(
				new TextMenuItem(MENU_OPTIONS, font, "Options"), 0.5f, 0.5f,
				0.5f, 1.0f, 0.0f, 0.0f);
		optionsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(optionsMenuItem);

		final IMenuItem aboutMenuItem = new ColorMenuItemDecorator(
                new TextMenuItem(MENU_ABOUT, font, "About"), 0.5f, 0.5f,
                0.5f, 1.0f, 0.0f, 0.0f);
        optionsMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
                GL10.GL_ONE_MINUS_SRC_ALPHA);
        menuScene.addMenuItem(aboutMenuItem);

		final IMenuItem scoresMenuItem = new ColorMenuItemDecorator(
				new TextMenuItem(MENU_SCORES, font, "Scores"), 0.5f, 0.5f,
				0.5f, 1.0f, 0.0f, 0.0f);
		scoresMenuItem.setBlendFunction(GL10.GL_SRC_ALPHA,
				GL10.GL_ONE_MINUS_SRC_ALPHA);
		menuScene.addMenuItem(scoresMenuItem);

		menuScene.buildAnimations();
		menuScene.setBackgroundEnabled(false);
		menuScene.setOnMenuItemClickListener(this);
	}

	@Override
	public void onLoadComplete() {

	}

	@Override
	public boolean onMenuItemClicked(MenuScene pMenuScene, IMenuItem pMenuItem,
			float pMenuItemLocalX, float pMenuItemLocalY) {

		switch (pMenuItem.getID()) {
		case MENU_PLAY:

            handler.postDelayed(heroRunnable, 500);
			break;
		case MENU_OPTIONS:
		    LayoutInflater checkBoxInflater = LayoutInflater.from(MainMenuActivity.this);
            View checkboxLayout = checkBoxInflater.inflate(R.layout.checkbox, null);
            musicCB = (CheckBox)checkboxLayout.findViewById(R.id.chkBoxMusic);
            audioCB = (CheckBox)checkboxLayout.findViewById(R.id.chkBoxSound);
            hintCB = (CheckBox)checkboxLayout.findViewById(R.id.chkBoxHint);

            if (gameUtils.isMusicOn()) {
                musicCB.setChecked(true);
            }
            if (gameUtils.isSoundOn()) {
                audioCB.setChecked(true);
            }
            if (gameUtils.isHintOn()) {
                hintCB.setChecked(true);
            }

		    AlertDialog.Builder pauseAlert = new AlertDialog.Builder(MainMenuActivity.this).setTitle("Options");
		    pauseAlert.setView(checkboxLayout);
		    pauseAlert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		        @Override
                public void onClick(DialogInterface dialog, int which) {
	                  boolean checkBoxResult = false;
	                  if (musicCB.isChecked()) {
	                      gameUtils.setMusicOn();
	                  } else {
	                      gameUtils.setMusicOff();
	                  }
	                  if (audioCB.isChecked()) {
                          gameUtils.setSoundEffectOn();
                      } else {
                          gameUtils.setSoundEffectOff();
                      }
	                  if (hintCB.isChecked()) {
	                      gameUtils.setHintOn();
	                  } else {
	                      gameUtils.setHintOff();
	                  }
	                  return;
	              } });
            pauseAlert.show();
			break;
		case MENU_SCORES:
		    MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, Highscore.class));
			break;
		case MENU_ABOUT:
		    MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, About.class));
		    break;
		default:
			break;
		}

		return false;
	}

    public Runnable heroRunnable = new Runnable() {

		@Override
		public void run() {
            Class place = null;
            try {
                place = Class.forName(gameUtils.getLastPlace());
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            if (null != place) {
                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, place));
            } else {
                dbHelper.insertRecordForRiddles(sqlDb, dbHelper.TABLE_NAME3, gameUtils.retrieveUsername(), getBaseContext());
                MainMenuActivity.this.startActivity(new Intent(MainMenuActivity.this, GameStory.class));
            }
		}
	};

}
