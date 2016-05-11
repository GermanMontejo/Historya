package com.thesis.historya;

import org.anddev.andengine.engine.Engine;
import org.anddev.andengine.engine.FixedStepEngine;
import org.anddev.andengine.engine.camera.Camera;
import org.anddev.andengine.engine.options.EngineOptions;
import org.anddev.andengine.engine.options.EngineOptions.ScreenOrientation;
import org.anddev.andengine.engine.options.WakeLockOptions;
import org.anddev.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.anddev.andengine.entity.scene.Scene;
import org.anddev.andengine.entity.scene.background.ColorBackground;
import org.anddev.andengine.entity.sprite.Sprite;
import org.anddev.andengine.entity.util.FPSLogger;
import org.anddev.andengine.opengl.texture.TextureOptions;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.anddev.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.anddev.andengine.opengl.texture.region.TextureRegion;
import org.anddev.andengine.ui.activity.BaseGameActivity;

import android.content.Intent;
import android.os.Handler;

public class StartGameActivity extends BaseGameActivity {
	BitmapTextureAtlas texture;
	Camera camera;
	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	TextureRegion textureRegion;
	private Handler handler;
	FixedStepEngine engine;
	@Override
	public Engine onLoadEngine() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		EngineOptions engineOptions = new EngineOptions(true,
				ScreenOrientation.LANDSCAPE, new RatioResolutionPolicy(
						CAMERA_WIDTH, CAMERA_HEIGHT),
				camera);
		engineOptions.setWakeLockOptions(WakeLockOptions.SCREEN_ON);
		engine = new FixedStepEngine(engineOptions, 60);
		handler = new Handler();
		return engine;
	}

	@Override
	public void onLoadResources() {
	    this.texture = new BitmapTextureAtlas(512, 512, TextureOptions.BILINEAR);
	    BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");
		textureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(texture, this, "historya_logo1.png", 0, 0);
		engine.getTextureManager().loadTexture(texture);
	}

	@Override
	public Scene onLoadScene() {
		engine.registerUpdateHandler(new FPSLogger());
		@SuppressWarnings("deprecation")
		Scene scene = new Scene(1);
		// center X coordinate for splash image
		final int centerX = (CAMERA_WIDTH - textureRegion.getWidth()) / 2;
		// center Y coordinate for splash image
		final int centerY = (CAMERA_HEIGHT - textureRegion.getHeight()) / 2;
		final Sprite splash = new Sprite(centerX, centerY, textureRegion);
		scene.getLastChild().attachChild(splash);
		scene.setBackground(new ColorBackground(1.0f, 1.0f, 1.0f));
		return scene;
	}

	@Override
	public void onLoadComplete() {
		handler.postDelayed(launchTask, 2000);
	}

	private final Runnable launchTask = new Runnable() {

		@Override
		public void run() {
			startActivity(new Intent(StartGameActivity.this,
					Login.class));
			StartGameActivity.this.finish();
		}

	};
}
